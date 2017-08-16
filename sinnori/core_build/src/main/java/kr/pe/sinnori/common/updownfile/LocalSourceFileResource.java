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

import org.apache.lucene.util.LongBitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
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
public class LocalSourceFileResource {
	private Logger log = LoggerFactory.getLogger(LocalSourceFileResource.class);

	private String ownerID = null;

	public enum WorkStep {
		TRANSFER_WORKING, TRANSFER_DONE, CANCEL_WORKING, CANCEL_DONE
	}

	private WorkStep workStep = WorkStep.TRANSFER_WORKING;
	

	private int sourceFileID = Integer.MAX_VALUE;
	private int targetFileID = Integer.MIN_VALUE;

	private boolean append = false;

	private String targetFilePathName = null;
	private String targetFileName = null;
	private long targetFileSize = -1;

	private String sourceFilePathName = null;
	private String sourceFileName = null;
	private long sourceFileSize = -1;

	private int fileBlockSize = -1;
	private long startFileBlockNo = -1;
	private long endFileBlockNo = -1;

	// private long wantedCardinalityOfWorkedFileBlockBitSet = -1;
	/** 주의점 : BitSet 의 크기는 동적으로 자란다. 따라서 해당 비트 인덱스에 대한 엄격한 제한이 필요하다. */
	private LongBitSet workedFileBlockBitSet = null;
	
	private RandomAccessFile sourceRandomAccessFile = null;
	private FileChannel sourceFileChannel = null;
	private FileLock sourceFileLock = null;

	/**
	 * Warning! the variable fileBlockMaxSize must not create getXXX method
	 * because it is Sinnori configuration variable
	 */
	private int fileBlockMaxSize = -1;
	
	

	/******* view 와 관련된 모듈 시작 ***************/
	private FileTranferProcessInformationDialogIF fileTranferProcessInformationDialog = null;

	private void noticeAddedFileData(int fileBlockNo) {
		if (null != fileTranferProcessInformationDialog) {
			long currentStartOffset = fileBlockNo*fileBlockSize;
			long currentEndOffset = currentStartOffset + fileBlockSize;
			
			if (append && fileBlockSize == startFileBlockNo) {
				currentStartOffset = targetFileSize;
			}
			
			if (currentEndOffset > sourceFileSize) {
				currentEndOffset = sourceFileSize;
			}
			
			int receivedDataSize =  (int)(currentEndOffset - currentStartOffset);
			
			fileTranferProcessInformationDialog.noticeAddedFileData(receivedDataSize);
		}
	}
	
	public void setFileTranferProcessInformationDialog(FileTranferProcessInformationDialogIF fileTranferProcessInformationDialog) {
		if (null == fileTranferProcessInformationDialog) {
			throw new IllegalArgumentException("the parameter fileTranferProcessInformationDialog is null");
		}
		this.fileTranferProcessInformationDialog = fileTranferProcessInformationDialog;
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
			if (! workStep.equals(WorkStep.TRANSFER_DONE)) {
				fileTranferProcessInformationDialog.dispose();
			}
		}
	}
	
	/******* view 와 관련된 모듈 종료 ***************/
	
	
	

	/**
	 * 생성자
	 * @param ownerID
	 * @param sourceFileID
	 * @param append
	 * @param sourceFilePathName
	 * @param sourceFileName
	 * @param sourceFileSize
	 * @param targetFilePathName
	 * @param targetFileName
	 * @param targetFileSize
	 * @param fileBlockSize
	 * @throws IllegalArgumentException
	 * @throws UpDownFileException
	 */
	public LocalSourceFileResource(String ownerID, int sourceFileID, boolean append, String sourceFilePathName, String sourceFileName,
			long sourceFileSize, String targetFilePathName, String targetFileName, long targetFileSize,
			int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		fileBlockMaxSize = commonPart.getFileBlockMaxSize();
		
		
		if (null == ownerID) {
			throw new IllegalArgumentException("the parameter ownerID is null");
		}

		if (null == sourceFilePathName) {
			throw new IllegalArgumentException("the parameter sourceFilePathName is null");
		}

		sourceFilePathName = sourceFilePathName.trim();

		if (sourceFilePathName.equals("")) {
			throw new IllegalArgumentException("the parameter sourceFilePathName is empty");
		}
		
		if (sourceFileSize <= 0) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter sourceFileSize[%d] is less than or equal to zero", sourceFileID,
					sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == targetFilePathName) {
			throw new IllegalArgumentException("the parameter targetFilePathName is null");
		}

		targetFilePathName = targetFilePathName.trim();

		if (targetFilePathName.equals("")) {
			throw new IllegalArgumentException("the parameter targetFilePathName is empty");
		}

		if (targetFileSize < 0) {
			String errorMessage = String.format(
					"sourceFileID[%d]::targetFile[%s][%s]::parameter targetFileSize[%d] less than zero", sourceFileID,
					targetFilePathName, targetFileName, targetFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize <= 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockSize[%d] less than or equal zero",
					sourceFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (0 != (fileBlockSize % 1024)) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockSize[%d] is not a multiple of 1024", sourceFileID,
					fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize > fileBlockMaxSize) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockSize[%d] is over  than max[%d]",
					sourceFileID, fileBlockSize, fileBlockMaxSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (append) {
			/** 이어받기 */
			if (sourceFileSize <= targetFileSize) {
				String errorMessage = String.format(
						"sourceFileID[%d]::sourceFile[%s][%s]::parameter sourceFileSize[%d] less than or equal to parameter targetFileSize[%d]",
						sourceFileID, sourceFilePathName, sourceFilePathName, sourceFileSize, targetFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}

		this.ownerID = ownerID;
		this.workStep = WorkStep.TRANSFER_WORKING;
		this.sourceFileID = sourceFileID;
		this.append = append;
		this.sourceFilePathName = sourceFilePathName;
		this.sourceFileName = sourceFileName;
		this.sourceFileSize = sourceFileSize;
		this.targetFilePathName = targetFilePathName;
		this.targetFileName = targetFileName;
		this.targetFileSize = targetFileSize;
		this.fileBlockSize = fileBlockSize;

		if (append) {
			/** 이어받기 */
			this.endFileBlockNo = (sourceFileSize + fileBlockSize - 1) / fileBlockSize - 1;

			if (endFileBlockNo > (Long.MAX_VALUE - 1)) {
				/**
				 * 자바는 배열 크기가 정수로 제한되는데, 파일 조각 받은 여부를 기억하는 BitSet 도 그대로 그 문제를
				 * 상속한다. 따라서 fileBlock 최대 갯수는 정수(=Integer) 이어야 한다.
				 */
				String errorMessage = String.format(
						"sourceFileID[%d]::endFileBlockNo[%d] greater than (Long.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big",
						sourceFileID, endFileBlockNo, fileBlockSize, sourceFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			this.startFileBlockNo = (this.targetFileSize + fileBlockSize - 1) / fileBlockSize - 1;
			if (startFileBlockNo > endFileBlockNo) {
				String errorMessage = String.format(
						"sourceFileID[%d]::the variable 'startFileBlockNo'[%d] is greater than endFileBlockNo[%d], sourceFileSize=[%d], ",
						sourceFileID, startFileBlockNo, endFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
			
			workedFileBlockBitSet = new LongBitSet(endFileBlockNo + 1);

			/** 이어 받기를 시작하는 위치 전까지 데이터 읽기 여부를 참으로 설정한다. */
			for (int i = 0; i < startFileBlockNo; i++) {
				workedFileBlockBitSet.set(i);
			}

		} else {
			/** 덮어 쓰기 */

			this.endFileBlockNo = (sourceFileSize + fileBlockSize - 1) / fileBlockSize - 1;

			if (endFileBlockNo > (Long.MAX_VALUE - 1)) {
				/**
				 * 자바는 배열 크기가 정수로 제한되는데, 파일 조각 받은 여부를 기억하는 BitSet 도 그대로 그 문제를
				 * 상속한다. 따라서 fileBlock 최대 갯수는 정수(=Integer) 이어야 한다.
				 */
				String errorMessage = String.format(
						"sourceFileID[%d]::endFileBlockNo[%d] greater than (Long.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big",
						sourceFileID, endFileBlockNo, fileBlockSize, sourceFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			this.startFileBlockNo = 0;

			if (startFileBlockNo > endFileBlockNo) {
				String errorMessage = String.format(
						"sourceFileID[%d]::the variable 'startFileBlockNo'[%d] is greater than endFileBlockNo[%d], sourceFileSize=[%d], ",
						sourceFileID, startFileBlockNo, endFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}			

			workedFileBlockBitSet = new LongBitSet(endFileBlockNo + 1);
		}
		
		// wantedCardinalityOfWorkedFileBlockBitSet = workedFileBlockBitSet.length();

		

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

	

	public String getOwnerID() {
		return ownerID;
	}

	public void setWorkStep(WorkStep workStep) {
		this.workStep = workStep;
	}

	public WorkStep getWorkStep() {
		return workStep;
	}

	/**
	 * @return the sourceFileID
	 */
	public final int getSourceFileID() {
		return sourceFileID;
	}


	/**
	 * @return the targetFileID
	 */
	public final int getTargetFileID() {
		return targetFileID;
	}

	/**
	 * @param targetFileID
	 *            the targetFileID to set
	 */
	public void setTargetFileID(int targetFileID) {
		this.targetFileID = targetFileID;
	}
	

	/**
	 * @return the targetFilePathName
	 */
	public String getTargetFilePathName() {
		return targetFilePathName;
	}

	/**
	 * @return the targetFileName
	 */
	public String getTargetFileName() {
		return targetFileName;
	}

	/**
	 * @return the sourceFileSize
	 */
	public long getSourceFileSize() {
		return sourceFileSize;
	}

	/**
	 * @return the sourceFilePathName
	 */
	public String getSourceFilePathName() {
		return sourceFilePathName;
	}

	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

	/**
	 * @return the fileBlockSize
	 */
	public int getFileBlockSize() {
		return fileBlockSize;
	}

	/**
	 * @return the startFileBlockNo
	 */
	public int getStartFileBlockNo() {
		return (int) startFileBlockNo;
	}

	/**
	 * @return the endFileBlockNo
	 */
	public int getEndFileBlockNo() {
		return (int) endFileBlockNo;
	}

	public long getStartOffset() {
		if (append) {
			return targetFileSize;
		} else {
			return 0;
		}
	}

	public void turnOnWorkedFileBlockBitSetAt(int fileBlockNo) {
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

		try {
			if (workedFileBlockBitSet.get(fileBlockNo)) {
				/** 파일 조각 중복 도착 */
				String errorMessage = String.format("sourceFileID[%d]::파일 조각[%d] 중복 도착", sourceFileID, fileBlockNo);
				log.error(errorMessage);
				System.exit(1);
			} else {
				workedFileBlockBitSet.set(fileBlockNo);
			}
		} catch (NullPointerException e) {
			/**
			 * 심각한 로직 버그
			 */
			String errorMessage = String.format("변수 workedFileBlockBitSet 가 null 입니다, toString=[%s]", toString());
			log.error(errorMessage);
			System.exit(1);
		}
		
		noticeAddedFileData(fileBlockNo);
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
	
	
	/*public void setWantedCardinalityOfWorkedFileBlockBitSet(int canceledFileBlockNo) {
		if (canceledFileBlockNo < 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter canceledFileBlockNo[%d] is less than zero",
					sourceFileID, canceledFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (canceledFileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", sourceFileID,
					canceledFileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (canceledFileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"sourceFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", sourceFileID,
					canceledFileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}		
		
		wantedCardinalityOfWorkedFileBlockBitSet = Math.min(wantedCardinalityOfWorkedFileBlockBitSet, canceledFileBlockNo);
	}*/

	

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
			
		long currentStartOffset = fileBlockNo*fileBlockSize;
		long currentEndOffset = currentStartOffset + fileBlockSize;
		
		if (append && fileBlockSize == startFileBlockNo) {
			currentStartOffset = targetFileSize;
		}
		
		if (currentEndOffset > sourceFileSize) {
			currentEndOffset = sourceFileSize;
		}
		
		byte[] fileData = new byte[(int)(currentEndOffset - currentEndOffset)];
	
		try {
			sourceRandomAccessFile.seek(currentStartOffset);
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
		
		log.info("fileBlockNo=[{}], startFileBlockNo=[{}], endFileBlockNo=[{}], fileBlockSize[{}], currentStartOffset=[{}], currentEndOffset=[{}], fileData.length=[{}]", 
				fileBlockNo, startFileBlockNo, endFileBlockNo, fileBlockSize, 
				currentStartOffset, currentEndOffset, fileData.length);
		
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
		builder.append("LocalSourceFileResource [ownerID=");
		builder.append(ownerID);
		builder.append(", localUploadStep=");
		builder.append(workStep);
		builder.append(", sourceFileID=");
		builder.append(sourceFileID);
		builder.append(", targetFileID=");
		builder.append(targetFileID);
		builder.append(", append=");
		builder.append(append);
		builder.append(", targetFilePathName=");
		builder.append(targetFilePathName);
		builder.append(", targetFileName=");
		builder.append(targetFileName);
		builder.append(", targetFileSize=");
		builder.append(targetFileSize);
		builder.append(", sourceFilePathName=");
		builder.append(sourceFilePathName);
		builder.append(", sourceFileName=");
		builder.append(sourceFileName);
		builder.append(", sourceFileSize=");
		builder.append(sourceFileSize);
		builder.append(", fileBlockSize=");
		builder.append(fileBlockSize);
		builder.append(", startFileBlockNo=");
		builder.append(startFileBlockNo);
		builder.append(", endFileBlockNo=");
		builder.append(endFileBlockNo);
		/*builder.append(", wantedSizeOfWorkedFileBlockBitSet=");
		builder.append(wantedCardinalityOfWorkedFileBlockBitSet);*/
		builder.append(", workedFileBlockBitSet.cardinality()=");
		builder.append(workedFileBlockBitSet.cardinality());
		builder.append(", workedFileBlockBitSet.length()=");
		builder.append(workedFileBlockBitSet.length());
		
		// builder.append(HexUtil.getHexStringFromByteArray(workedFileBlockBitSet.toByteArray()));
		builder.append(", fileBlockMaxSize=");
		builder.append(fileBlockMaxSize);
		builder.append(", whether viewObject is null =");
		builder.append((null == fileTranferProcessInformationDialog));
		builder.append("]");
		return builder.toString();
	}

}
