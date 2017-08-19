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
 * WARNING! this class is no thread safe
 * 
 * 로컬 원본 파일을 원할하게 송신하기 위한 로컬 원본 파일 자원 클래스. * 
 * 로컬 원본 파일 자원은 파일락, 파일 채널, 비트 셋으로 표현된 작업 완료 여부 정보가 있다. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class LocalSourceFileResource extends AbstractFileResource {
	public enum WorkStep {
		TRANSFER_WORKING, TRANSFER_DONE, CANCEL_WORKING, CANCEL_DONE
	}

	private WorkStep workStep = WorkStep.TRANSFER_WORKING;
	
	private RandomAccessFile sourceRandomAccessFile = null;
	private FileChannel sourceFileChannel = null;
	private FileLock sourceFileLock = null;
	
	public LocalSourceFileResource(String ownerID, int sourceFileID, boolean append, String sourceFilePathName, String sourceFileName,
			long sourceFileSize, String targetFilePathName, String targetFileName, long targetFileSize,
			int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		super(ownerID, append, sourceFilePathName, sourceFileName,
				sourceFileSize, targetFilePathName, targetFileName, targetFileSize,
				fileBlockSize);
		
		this.sourceFileID = sourceFileID;
		this.workStep = WorkStep.TRANSFER_WORKING;
				

		File sourceFilePath = new File(sourceFilePathName);

		if (!sourceFilePath.exists()) {
			/** 원본 파일의 경로가 존재하지 않습니다. */
			String errorMessage = String.format("sourceFileID[%d]::source file path[%s] not exist", sourceFileID,
					sourceFilePathName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		StringBuilder sourcefullFileNameBuilder = new StringBuilder(sourceFilePathName);
		sourcefullFileNameBuilder.append(File.separatorChar);
		sourcefullFileNameBuilder.append(sourceFileName);

		String sourcefullFileName = sourcefullFileNameBuilder.toString();
		File sourceFileObj = new File(sourcefullFileName);

		// boolean isFile = sourceFile.createNewFile();
		if (!sourceFileObj.exists()) {
			try {
				sourceFileObj.createNewFile();
			} catch (IOException e) {
				/** 원본 파일 신규 생성시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s] 신규 생성시 입출력 에러 발생", sourceFileID,
						sourcefullFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
		}

		if (!sourceFileObj.canWrite()) {
			/** 원본 파일 쓰기 권한 없음 */
			String errorMessage = String.format("sourceFileID[%d]::source file[%s] can't be written", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		if (!sourceFileObj.canRead()) {
			/** 원본 파일 읽기 권한 없음 */
			String errorMessage = String.format("sourceFileID[%d]::source file[%s] can't be read", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		try {
			sourceRandomAccessFile = new RandomAccessFile(sourceFileObj, "rw");
		} catch (FileNotFoundException e) {
			/** 락을 걸기 위한 랜덤 접근 파일 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다. */
			String errorMessage = String.format(
					"sourceFileID[%d]::락을 걸기 위한 원본 랜덤 접근 파일[%s] 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다.", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}
		// sourceRandomAccessFile.
		// sourceRandomAccessFile.setLength(sourceFileSize);

		sourceFileChannel = sourceRandomAccessFile.getChannel();
		try {
			sourceFileLock = sourceFileChannel.tryLock();
		} catch (IOException e) {
			/** 원본 파일에 락을 걸때 입출력 에러 발생 */
			String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s]에 락을 걸때 입출력 에러 발생", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		} catch (OverlappingFileLockException e) {
			/** 원본 파일에 락을 걸때 중복 락 에러 발생 */
			String errorMessage = String.format("sourceFileID[%d]::이미 락이 걸린 원본 파일[%s]입니다.", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}

		if (null == sourceFileLock) {
			/** 다른 프로그램에서 락을 걸어 원본 파일 락 획득에 실패 */
			String errorMessage = String.format("sourceFileID[%d]::다른 프로그램에서 락을 걸어 원본 파일[%s] 락 획득에 실패", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		// long realSourceFileSize = sourceFileObj.length();
		long realSourceFileSize = -1;
		try {
			realSourceFileSize = sourceFileChannel.size();
		} catch (IOException e) {
			/** 입출력 에러 발생으로 파일 크기 얻기 실패 */
			releaseFileLock();
			String errorMessage = String.format("sourceFileID[%d]::입출력 에러 발생으로 소스 파일[%s] 크기 얻기 실패", sourceFileID,
					sourcefullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}

		if (realSourceFileSize != sourceFileSize) {
			releaseFileLock();
			String errorMessage = String.format("sourceFileID[%d]::소스 파일[%s]의 크기[%d]외 실제 크기[%d]가 같지 않습니다.",
					sourceFileID, sourcefullFileName, realSourceFileSize, sourceFileSize);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		log.info(toString());
	}

	
	/**
	 * @param targetFileID
	 *            the targetFileID to set
	 */
	public void setTargetFileID(int targetFileID) {
		this.targetFileID = targetFileID;
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
			if (! workStep.equals(LocalSourceFileResource.WorkStep.TRANSFER_DONE)) {
				fileTranferProcessInformationDialog.dispose();
			}
		}
	}
	
	/**
	 * @return 원격지 파일 복사 작업 완료 여부
	 */
	public boolean whetherRemoteFileCopyWorkIsCompleted() {
		boolean isFinished = false;
		try {
			// isFinished = (workedFileBlockBitSet.cardinality() == wantedCardinalityOfWorkedFileBlockBitSet);
			isFinished = (workedFileBlockBitSet.cardinality() == workedFileBlockBitSet.length());
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format("변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		}

		return isFinished;
	}
	
	

	/**
	 * 수신한 파일 조각을 저장한다.
	 * 
	 * @param fileBlockNo
	 *            파일 조각 번호
	 * @param fileData
	 *            파일 조각 번호에 대응하는 파일 조각 데이터를 저장할 바이트 배열
	 * @return 모든 파일 조각 수신 여부
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException
	 *             수신한 파일 조각을 저장하는 과정에서 에러 발생시 던지는 예외
	 */
	public byte[] readSourceFileData(int sourceFileID, int fileBlockNo) 
			throws IllegalArgumentException, UpDownFileException {
	
		if (fileBlockNo < 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockNo[%d] is less than zero",
					sourceFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (fileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", sourceFileID,
					fileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", sourceFileID,
					fileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		/**************************** 방어 코드 시작 *****************/
		if (sourceFileID != this.sourceFileID) {
			String errorMessage = String.format("%d 번째 데이터 패킷 읽기 작업중 작업중인 소스 파일 식별자[%d]와 실제 소스 파일 식별자[%d]가 다릅니다. ",
					fileBlockNo, sourceFileID, this.sourceFileID);
			log.error(errorMessage);
			System.exit(1);
		}
		/**************************** 방어 코드 종료 *****************/
			
		FileBlockInformation fileBlockInformation = new FileBlockInformation(
				sourceFileID,			
				targetFileID,
				sourceFileSize,
				targetFileSize,
				startFileBlockNo,
				endFileBlockNo,				
				append, fileBlockSize, fileBlockNo);
		
		byte[] fileData = new byte[fileBlockInformation.getCurrentLength()];
	
		try {
			sourceRandomAccessFile.seek(fileBlockInformation.getCurrentStartOffset());
			sourceRandomAccessFile.read(fileData);
	
			// log.info(String.format("fileBlockNo=[%d],
			// sourceFileOffset=[%d], fileData length=[%d]", fileBlockNo,
			// sourceFileOffset, fileData.length));
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format("변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		} catch (IOException e) {
			/** n 번째 원본 파일 조각 읽기 실패 */
			String errorMessage = String.format("sourceFileID[%d]::%d 번째 원본 파일[%s][%s] 조각 읽기 실패", sourceFileID,
					fileBlockNo, sourceFilePathName, sourceFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		log.info(fileBlockInformation.toString());
		
		return fileData;
	}

	public boolean isReleasedFileLock() {
		boolean isReleasedFileLock = true;
	
		if (null != sourceRandomAccessFile || null != sourceFileChannel || null != sourceFileLock) {
			isReleasedFileLock = false;
		}
	
		return isReleasedFileLock;
	}

	/**
	 * 원본 파일에 걸은 파일 락을 해제한다. 참고) protected 선언은 동일 패키지 클래스인 송수신 파일 자원 관리자만 접근시키기
	 * 위한 조취이다.
	 */
	protected void releaseFileLock() {
		log.info("release source file lock, info=[{}]", toString());
	
		if (null != sourceFileLock) {
			try {
				sourceFileLock.release();
				sourceFileLock = null;
			} catch (IOException e) {
				/** 파일 락 해제시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 락 해제시 입출력 에러 발생", sourceFileID,
						sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}
	
		if (null != sourceFileChannel) {
			try {
				sourceFileChannel.close();
				sourceFileChannel = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 채널 닫기시 입출력 에러 발생", sourceFileID,
						sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}
	
		if (null != sourceRandomAccessFile) {
			try {
				sourceRandomAccessFile.close();
				sourceRandomAccessFile = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 랜덤 접근 파일 객체 닫기시 입출력 에러 발생",
						sourceFileID, sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}
	}

	@Override
	public void finalize() {
		log.warn("큐에 반환되지 못한 랩 버퍼 소멸");
		releaseFileLock();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("LocalSourceFileResource [workStep=");
		builder.append(workStep);
		builder.append(", sourceRandomAccessFile=");
		builder.append(sourceRandomAccessFile);
		builder.append(", sourceFileChannel=");
		builder.append(sourceFileChannel);
		builder.append(", sourceFileLock=");
		builder.append(sourceFileLock);
		builder.append("]");
		return builder.toString();
	}
}
