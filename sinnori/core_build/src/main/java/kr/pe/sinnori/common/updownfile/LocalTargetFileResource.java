/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.common.updownfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import kr.pe.sinnori.common.exception.UpDownFileException;

/**
 * <pre>
 * 로컬 목적지 파일을 원할하게 수신하기 위한 로컬 목적지 파일 자원 클래스.
 * 로컬 목적지 파일 자원은 파일락, 파일 채널, 비트 셋으로 표현된 작업 완료 여부 정보가 있다. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class LocalTargetFileResource extends AbstractFileResource {	
	public enum WorkStep {
		TRANSFER_WORKING, TRANSFER_DONE, CANCEL_DONE
	}

	private WorkStep workStep = WorkStep.TRANSFER_WORKING;
	
	private RandomAccessFile targetRandomAccessFile = null;
	private FileChannel targetFileChannel = null;
	private FileLock targetFileLock = null;	
	
	public LocalTargetFileResource(String ownerID, int targetFileID, boolean append, String sourceFilePathName, String sourceFileName,
			long sourceFileSize, String targetFilePathName, String targetFileName, long targetFileSize,
			int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		super(ownerID, append, sourceFilePathName, sourceFileName,
				sourceFileSize, targetFilePathName, targetFileName, targetFileSize,
				fileBlockSize);
		
		this.targetFileID = targetFileID;
		this.workStep = WorkStep.TRANSFER_WORKING;
		
		File targetFilePath = new File(targetFilePathName);

		if (!targetFilePath.exists()) {
			/** 목적지 파일의 경로가 존재하지 않습니다. */
			String errorMessage = String.format("targetFileID[%d]::target file path[%s] not exist", targetFileID,
					targetFilePathName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		StringBuilder targetfullFileNameBuilder = new StringBuilder(targetFilePathName);
		targetfullFileNameBuilder.append(File.separatorChar);
		targetfullFileNameBuilder.append(targetFileName);

		String targetfullFileName = targetfullFileNameBuilder.toString();
		File targetFileObj = new File(targetfullFileName);

		if (!targetFileObj.exists()) {
			try {
				targetFileObj.createNewFile();
			} catch (IOException e) {
				/** 목적지 파일 신규 생성시 입출력 에러 발생 */
				String errorMessage = String.format("targetFileID[%d]::목적지 파일[%s] 신규 생성시 입출력 에러 발생", targetFileID,
						targetfullFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
		} else {
			if (targetFileObj.isDirectory()) {
				String errorMessage = String.format(
						"targetFileID[%d]::송수신할 목적지 파일의 경로[%s]에는 \n이미 파일명[%s]과 \n일치하는 디렉토리 경로가 존재하여 목적지 파일를 생성할 수 없습니다.",
						targetFileID, targetFilePathName, targetFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
		}

		if (!targetFileObj.canWrite()) {
			/** 목적지 파일 쓰기 권한 없음 */
			String errorMessage = String.format("targetFileID[%d]::target file[%s] can't be written", targetFileID,
					targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		if (!targetFileObj.canRead()) {
			/** 목적지 파일 읽기 권한 없음 */
			String errorMessage = String.format("targetFileID[%d]::target file[%s] can't be read", targetFileID,
					targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		try {
			targetRandomAccessFile = new RandomAccessFile(targetFileObj, "rw");
		} catch (FileNotFoundException e) {
			/** 락을 걸기 위한 랜덤 접근 파일 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다. */
			String errorMessage = String.format(
					"targetFileID[%d]::락을 걸기 위한 목적지 랜덤 접근 파일[%s] 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다.", targetFileID,
					targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}
		// sourceRandomAccessFile.
		// sourceRandomAccessFile.setLength(sourceFileSize);

		targetFileChannel = targetRandomAccessFile.getChannel();
		try {
			targetFileLock = targetFileChannel.tryLock();
		} catch (IOException e) {
			/** 목적지 파일에 락을 걸때 입출력 에러 발생 */
			String errorMessage = String.format("targetFileID[%d]::목적지 파일[%s]에 락을 걸때 입출력 에러 발생", targetFileID,
					targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		} catch (OverlappingFileLockException e) {
			/** 원본 파일에 락을 걸때 중복 락 에러 발생 */
			String errorMessage = String.format("targetFileID[%d]::이미 락이 걸린 목적지 파일[%s]입니다.", targetFileID,
					targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}

		if (null == targetFileLock) {
			/** 다른 프로그램에서 락을 걸어 목적지 파일 락 획득에 실패 */
			String errorMessage = String.format("targetFileID[%d]::다른 프로그램에서 락을 걸어 목적지 파일[%s] 락 획득에 실패", targetFileID,
					targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		
		
		log.info("생성자::{}", toString());
	}

	/**
	 * @param sourceFileID
	 *            the sourceFileID to set
	 */
	public void setSourceFileID(int sourceFileID) {
		this.sourceFileID = sourceFileID;
	}
	
	public void setWorkStep(WorkStep localUploadStep) {
		this.workStep = localUploadStep;
	}

	public WorkStep getWorkStep() {
		return workStep;
	}
	
	/**
	 * <pre>
	 * '전송 처리 정보 윈도우' 가 지정되었다면 창을 자동으로 닫는다. 
	 * 단 예외적으로 '전송 완료' 상태인 경우에는 창을 자동으로 닫지 않는다.
	 * 이는 사용자가 최종 전송 완료된 시점의 정보를 확인할 수 있게 해 주는 배려로
	 * 창은 사용자는 OK 버튼 클릭으로 닫히게 된다.
	 * 
	 * <pre>
	 */
	protected void disposeFileTranferProcessInformationDialogIfExistAndNotTransferDone() {		
		if (null != fileTranferProcessInformationDialog) {
			if (! workStep.equals(LocalTargetFileResource.WorkStep.TRANSFER_DONE)) {
				fileTranferProcessInformationDialog.dispose();
			}
		}
	}
	
	
	/**
	 * <pre>
	 * 덮어 쓰기 모드일 경우 목적지 파일 크기를 0으로 재 조정한다.
	 * 
	 * 중복 받기 이면 크기 0 으로 이어받기 이면 아무런 동작도 하지 않는다.
	 * 목적지 파일 크기는 서버/클라이언트 쌍방 다운로드 할 준비가 끝났을 시점에 크기 재조정이 되어야 한다.
	 * 업로드시에는 서버 업로드 준비 비지니스 로직에서 호출될것이며,
	 * 다운로드시에는 서버 다운로드 준비 출력 메시지를 받은 후 호출될것이다.
	 * </pre>
	 * 
	 * @throws UpDownFileException
	 *             파일 크기 재조정시 입출력 에러 발생시 던지는 예외
	 */
	public void makeZeroSizeIfOverwrite() throws UpDownFileException {
		if (append) {
			return;
		}

		/** 덮어 쓰기일때 크기 0으로 설정 */
		try {
			targetFileChannel.truncate(0L);
		} catch (IOException e) {
			/** 기존에 데이터 삭제를 위한 목적지 파일 크기 0 으로 설정 실패 */
			releaseFileLock();
			String errorMessage = String.format("targetFileID[%d]::기존에 데이터 삭제를 위한 목적지 파일[%s][%s] 크기 0 으로 설정 실패",
					targetFileID, targetFilePathName, targetFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}
	}

	/**
	 * 수신한 파일 조각을 저장한다.
	 * 
	 * @param fileBlockNo
	 *            파일 조각 번호
	 * @param fileData
	 *            파일 조각 데이터
	 * @param checkOver
	 *            중복시 에러 발생 유무, 참이면 중복시 에러 발생, 거짓이면 중복 허용
	 * @return 모든 파일 조각 수신 여부
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException
	 *             수신한 파일 조각을 저장하는 과정에서 에러 발생시 던지는 예외
	 */
	public void writeTargetFileData(int targetFileID, int fileBlockNo, byte[] fileData, boolean checkOver)
			throws IllegalArgumentException, UpDownFileException {
		if (fileBlockNo < 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileBlockNo[%d] less than zero",
					targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", targetFileID,
					fileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] greater than maxFileBlockNo[%d]", targetFileID,
					fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == fileData) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileData is null", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		FileBlockInformation fileBlockInformation = new FileBlockInformation(
				sourceFileID,			
				targetFileID,
				sourceFileSize,
				targetFileSize,
				startFileBlockNo,
				endFileBlockNo,				
				append, fileBlockSize, fileBlockNo);
		
		int expectedLength = fileBlockInformation.getCurrentLength();
		
		if (fileData.length != expectedLength) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileData's length[%d] is not same to the expected length[%d]", 
					targetFileID, fileData.length, expectedLength);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		/**************************** 방어 코드 시작 *****************/
		if (targetFileID != this.targetFileID) {
			String errorMessage = String.format("%d 번째 데이터 패킷 쓰기 작업중 작업중인 목적지 파일 식별자[%d]와 실제 목적지 파일 식별자[%d]가 다릅니다. ",
					fileBlockNo, targetFileID, this.targetFileID);
			log.error(errorMessage);
			System.exit(1);
		}
		/**************************** 방어 코드 종료 *****************/

		try {
			targetRandomAccessFile.seek(fileBlockInformation.getCurrentStartOffset());
			targetRandomAccessFile.write(fileData);

			// log.info(String.format("fileBlockNo=[%d], targetFileOffset=[%d],
			// fileData length=[%d]", fileBlockNo, targetFileOffset,
			// fileData.length));
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format(
					"변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		} catch (IOException e) {
			/** n 번째 목적지 파일 조각 쓰기 실패 */
			String errorMessage = String.format("targetFileID[%d]::%d 번째 목적지 파일[%s][%s] 조각 쓰기 실패", targetFileID,
					fileBlockNo, targetFilePathName, targetFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}		
		
		turnOnWorkedFileBlockBitSetAt(fileBlockNo);
		
		log.info(fileBlockInformation.toString());
	}
	
	public boolean isReleasedFileLock() {
		boolean isReleasedFileLock = true;
	
		if (null != targetRandomAccessFile || null != targetFileChannel || null != targetFileLock) {
			isReleasedFileLock = false;
		}
	
		return isReleasedFileLock;
	}
	
	private long getFirstIndexOfFailureFromBitSet() {
		for (long i=startFileBlockNo; i <= endFileBlockNo; i++) {
			if (! workedFileBlockBitSet.get(i)) {
				return i;
			}
		}
		// targetRandomAccessFile
		return -1;
	}
	
	public void truncateValidMaxSizeOfTargetFile() throws IOException {
		// FIXME!
		long failureFileBlockNo = getFirstIndexOfFailureFromBitSet();
		long validMaxSizeOfTargetFile = failureFileBlockNo * fileBlockSize;
		
		long targetFileSize = targetFileChannel.size();
		
		if (validMaxSizeOfTargetFile < targetFileSize) {
			targetFileChannel.truncate(validMaxSizeOfTargetFile);
		}
	}

	/**
	 * 목적지 파일에 걸은 파일 락을 해제한다. 참고) protected 선언은 동일 패키지 클래스인 송수신 파일 자원 관리자만 접근시키기
	 * 위한 조취이다.
	 */
	protected void releaseFileLock() {
		log.info(String.format("call releaseFileLock, targetFileID[%d], 목적지 파일[%s][%s]", targetFileID,
				targetFilePathName, targetFileName));		
	
		if (null != targetFileLock) {
			try {
				targetFileLock.release();
				targetFileLock = null;
			} catch (IOException e) {
				/** 파일 락 해제시 입출력 에러 발생 */
				String errorMessage = String.format("targetFileID[%d]::목적지 파일[%s][%s] 락 해제시 입출력 에러 발생", targetFileID,
						targetFilePathName, targetFileName);
				log.warn(errorMessage, e);
			}
		}
	
		if (null != targetFileChannel) {
			try {
				targetFileChannel.close();
				targetFileChannel = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("targetFileID[%d]::목적지 파일[%s][%s] 채널 닫기시 입출력 에러 발생", targetFileID,
						targetFilePathName, targetFileName);
				log.warn(errorMessage, e);
			}
		}
	
		if (null != targetRandomAccessFile) {
			try {
				targetRandomAccessFile.close();
				targetRandomAccessFile = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("targetFileID[%d]::목적지 파일[%s][%s] 랜덤 접근 파일 객체 닫기시 입출력 에러 발생",
						targetFileID, targetFilePathName, targetFileName);
				log.warn(errorMessage, e);
			}
		}		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("LocalTargetFileResource [workStep=");
		builder.append(workStep);
		builder.append(", targetRandomAccessFile=");
		builder.append(targetRandomAccessFile);
		builder.append(", targetFileChannel=");
		builder.append(targetFileChannel);
		builder.append(", targetFileLock=");
		builder.append(targetFileLock);
		builder.append("]");
		return builder.toString();
	}	
}
