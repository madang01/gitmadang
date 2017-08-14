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
 * 로컬 목적지 파일을 원할하게 수신하기 위한 로컬 목적지 파일 자원 클래스.
 * 로컬 목적지 파일 자원은 파일락, 파일 채널, 비트 셋으로 표현된 작업 완료 여부 정보가 있다. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class LocalTargetFileResource {
	private Logger log = LoggerFactory.getLogger(LocalTargetFileResource.class);

	private String ownerID = null;
	
	
	public enum WorkStep {
		TRANSFER_WORKING, TRANSFER_DONE, CANCEL_DONE
	}

	private WorkStep workStep = WorkStep.TRANSFER_WORKING;

	private int sourceFileID = Integer.MAX_VALUE;
	private int targetFileID = Integer.MIN_VALUE;

	private boolean append = false;

	private String sourceFilePathName = null;
	private String sourceFileName = null;
	private long sourceFileSize = -1;

	private String targetFilePathName = null;
	private String targetFileName = null;
	private long targetFileSize = -1;

	private long fileBlockSize = -1;
	private long startFileBlockNo = -1;
	private long endFileBlockNo = -1;
	private long firstFileDataLength = -1;
	private long lastFileDataLength = -1;
	
	
	
	// private long wantedCardinalityOfWorkedFileBlockBitSet = -1;

	/** 주의점 : BitSet 의 크기는 동적으로 자란다. 따라서 해당 비트 인덱스에 대한 엄격한 제한이 필요하다. */
	private LongBitSet workedFileBlockBitSet = null;
	private RandomAccessFile targetRandomAccessFile = null;
	private FileChannel targetFileChannel = null;
	private FileLock targetFileLock = null;

	/**
	 * Warning! the variable fileBlockMaxSize must not create getXXX method
	 * because it is Sinnori configuration variable
	 */
	private int fileBlockMaxSize = -1;

	/******* view 와 관련된 모듈 시작 ***************/
	private FileTranferProcessInformationDialogIF viewObject = null;

	public void setViewObject(FileTranferProcessInformationDialogIF viewObject) {
		this.viewObject = viewObject;
	}

	public FileTranferProcessInformationDialogIF getViewObejct() {
		return viewObject;
	}

	/******* view 와 관련된 모듈 종료 ***************/
	
	public LocalTargetFileResource(String ownerID, int targetFileID, boolean append, String sourceFilePathName, String sourceFileName,
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
			String errorMessage = String.format("targetFileID[%d]::parameter sourceFilePathName is null", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		sourceFilePathName = sourceFilePathName.trim();

		if (sourceFilePathName.equals("")) {
			String errorMessage = String.format("targetFileID[%d]::parameter sourceFilePathName is a empty",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == sourceFileName) {
			String errorMessage = String.format("targetFileID[%d]::parameter sourceFileName is null", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		sourceFileName = sourceFileName.trim();

		if (sourceFileName.equals("")) {
			String errorMessage = String.format("targetFileID[%d]::parameter sourceFileName is a empty", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (sourceFileSize <= 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter sourceFileSize[%d] less than zero",
					targetFileID, sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		/*
		 * if (sourceFileSize > CommonStaticFinal.UP_DOWN_SOURCE_FILE_MAX_SIZE)
		 * { String errorMessage = String
		 * .format("targetFileID[%d]::parameter sourceFileSize[%d] is over than max[%d]"
		 * , targetFileID, sourceFileSize,
		 * CommonStaticFinal.UP_DOWN_SOURCE_FILE_MAX_SIZE);
		 * log.warn(errorMessage); throw new
		 * IllegalArgumentException(errorMessage); }
		 */

		if (null == targetFilePathName) {
			String errorMessage = String.format("targetFileID[%d]::parameter targetFilePathName is null", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		targetFilePathName = targetFilePathName.trim();

		if (targetFilePathName.equals("")) {
			String errorMessage = String.format("targetFileID[%d]::parameter targetFilePathName is a empty",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == targetFileName) {
			String errorMessage = String.format("targetFileID[%d]::parameter targetFileName is null", targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		targetFileName = targetFileName.trim();

		if (targetFileName.equals("")) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter targetFileName is a empty, so change to parameter sourceFileName[%s]",
					targetFileID, sourceFileName);
			log.info(errorMessage);
			targetFileName = sourceFileName;
		}

		if (targetFileSize < 0) {
			String errorMessage = String.format(
					"targetFileID[%d]::targetFile[%s][%s]::parameter targetFileSize[%d] less than zero", targetFileID,
					targetFilePathName, targetFileName, targetFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize <= 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileBlockSize[%d] less than or equal zero",
					targetFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (0 != (fileBlockSize % 1024)) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockSize[%d] is not a multiple of 1024", targetFileID,
					fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize > fileBlockMaxSize) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileBlockSize[%d] is over than max[%d]",
					targetFileID, fileBlockSize, fileBlockMaxSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (append) {
			/** 이어받기 */
			if (sourceFileSize <= targetFileSize) {
				String errorMessage = String.format(
						"targetFileID[%d]::targetFile[%s][%s]::parameter sourceFileSize[%d] less than or equal to parameter targetFileSize[%d]",
						targetFileID, targetFilePathName, targetFileName, sourceFileSize, targetFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}

		this.ownerID = ownerID;
		this.workStep = WorkStep.TRANSFER_WORKING;
		this.targetFileID = targetFileID;
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
						"targetFileID[%d]::endFileBlockNo[%d] greater than (Long.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big",
						targetFileID, endFileBlockNo, fileBlockSize, sourceFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			this.startFileBlockNo = (this.targetFileSize + fileBlockSize - 1) / fileBlockSize - 1;

			if (startFileBlockNo > endFileBlockNo) {
				String errorMessage = String.format(
						"targetFileID[%d]::the variable 'startFileBlockNo'[%d] is greater than endFileBlockNo[%d], sourceFileSize=[%d], ",
						targetFileID, startFileBlockNo, endFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			if (sourceFileSize < fileBlockSize) {
				/** 원본 파일 크기가 전송할 데이터 블락 크기 보다 작은 경우 */
				this.firstFileDataLength = sourceFileSize - this.targetFileSize;
			} else {
				/** 원본 파일 크기가 전송할 데이터 블락 크기 보다 크거나 같은 경우 */
				/** 첫번째로 전송할 블락에서 이미 전송된 데이터 크기 */
				long transferedDataSizeForStartFileBlockNo = this.targetFileSize - startFileBlockNo * fileBlockSize;
				/** 첫번째로 전송할 블락에서 이미 전송된 데이터를 제외 */
				this.firstFileDataLength = fileBlockSize - transferedDataSizeForStartFileBlockNo;
			}

			if (endFileBlockNo == startFileBlockNo) {
				/** 전송할 데이터 블락이 1개뿐인 경우 */
				this.lastFileDataLength = sourceFileSize - this.targetFileSize;
			} else {
				/** 전송할 데이터 블락이 2개 이상인 경우 */
				this.lastFileDataLength = sourceFileSize - endFileBlockNo * fileBlockSize;
			}

			workedFileBlockBitSet = new LongBitSet( endFileBlockNo + 1);

			/** 이어 받기를 시작하는 위치 전까지 데이터 쓰기 여부를 참으로 설정한다. */
			for (long i = 0; i < startFileBlockNo; i++) {
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
						"targetFileID[%d]::endFileBlockNo[%d] greater than (Long.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big",
						targetFileID, endFileBlockNo, fileBlockSize, sourceFileSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			this.startFileBlockNo = 0;

			if (startFileBlockNo > endFileBlockNo) {
				String errorMessage = String.format(
						"targetFileID[%d]::the variable 'startFileBlockNo'[%d] is greater than endFileBlockNo[%d], sourceFileSize=[%d], ",
						targetFileID, startFileBlockNo, endFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}

			if (sourceFileSize < fileBlockSize) {
				/** 원본 파일 크기가 전송할 데이터 블락 크기 보다 작은 경우 */
				this.firstFileDataLength = sourceFileSize;
			} else {
				/** 원본 파일 크기가 전송할 데이터 블락 크기 보다 크거나 같은 경우 */
				this.firstFileDataLength = fileBlockSize;
			}

			if (endFileBlockNo == startFileBlockNo) {
				/** 전송할 데이터 블락이 1개뿐인 경우 */
				this.lastFileDataLength = sourceFileSize;
			} else {
				/** 전송할 데이터 블락이 2개 이상인 경우 */
				this.lastFileDataLength = sourceFileSize - endFileBlockNo * fileBlockSize;
			}

			workedFileBlockBitSet = new LongBitSet(endFileBlockNo + 1);
		}
		
		// wantedCardinalityOfWorkedFileBlockBitSet = workedFileBlockBitSet.length();

		log.info(String.format(
				"startFileBlockNo=[%d], endFileBlockNo=[%d], firstFileDataLength=[%d], lastFileDataLength=[%d]",
				startFileBlockNo, endFileBlockNo, firstFileDataLength, lastFileDataLength));

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

		// boolean isFile = sourceFile.createNewFile();
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
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setWorkStep(WorkStep localUploadStep) {
		this.workStep = localUploadStep;
	}

	public WorkStep getWorkStep() {
		return workStep;
	}

	/**
	 * @return the sourceFileID
	 */
	public int getSourceFileID() {
		return sourceFileID;
	}

	/**
	 * @param sourceFileID
	 *            the sourceFileID to set
	 */
	public void setSourceFileID(int sourceFileID) {
		this.sourceFileID = sourceFileID;
	}

	/**
	 * @return the targetFileID
	 */
	public int getTargetFileID() {
		return targetFileID;
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
	 * @return the targetFileSize
	 */
	public long getTargetFileSize() {
		return targetFileSize;
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
	 * @return the sourceFileSize
	 */
	public long getSourceFileSize() {
		return sourceFileSize;
	}

	/**
	 * @return the fileBlockSize
	 */
	public long getFileBlockSize() {
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

	private void turnOnWorkedFileBlockBitSetAt(int fileBlockNo) {
		if (fileBlockNo < 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter fileBlockNo[%d] is less than zero",
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
					"targetFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", targetFileID,
					fileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		try {
			if (workedFileBlockBitSet.get(fileBlockNo)) {
				/** 파일 조각 중복 도착 */
				String errorMessage = String.format("targetFileID[%d]::파일 조각[%d] 중복 도착", targetFileID, fileBlockNo);
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
	}
	
	/*public void setWantedCardinalityOfWorkedFileBlockBitSet(int canceledFileBlockNo) {
		if (canceledFileBlockNo < 0) {
			String errorMessage = String.format("targetFileID[%d]::parameter canceledFileBlockNo[%d] is less than zero",
					targetFileID, canceledFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (canceledFileBlockNo < startFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]", targetFileID,
					canceledFileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (canceledFileBlockNo > endFileBlockNo) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileBlockNo[%d] is greater than endFileBlockNo[%d]", targetFileID,
					canceledFileBlockNo, endFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}		
		
		wantedCardinalityOfWorkedFileBlockBitSet = Math.min(wantedCardinalityOfWorkedFileBlockBitSet, canceledFileBlockNo);
	}
	*/
	/**
	 * 
	 * @return 첫번째로 실패한 파일 블락 번호, if nothing then -1
	 */
	public long getFirstFailedFileBlockNo() {
		for (long i=startFileBlockNo; i <= endFileBlockNo; i++) {
			boolean isSuccess = workedFileBlockBitSet.get(i);
			if (! isSuccess) {
				return i;
			}
		}
		return -1L;
	}

	/**
	 * @return 파일 복사 작업이 완료 되었는지 여부
	 */
	public boolean whetherLocalFileCopyWorkIsCompleted() {
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

		if (fileBlockNo == endFileBlockNo) {
			if (fileData.length != lastFileDataLength) {
				String errorMessage = String.format(
						"targetFileID[%d]::parameter fileData's length[%d] is not equal to lastFileDataLength[%d]",
						targetFileID, fileData.length, lastFileDataLength);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		} else if (fileBlockNo == startFileBlockNo) {
			if (fileData.length != firstFileDataLength) {
				String errorMessage = String.format(
						"targetFileID[%d]::parameter fileData's length[%d] is not equal to firstFileDataLength[%d]",
						targetFileID, fileData.length, firstFileDataLength);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		} else {
			if (fileData.length != fileBlockSize) {
				String errorMessage = String.format(
						"targetFileID[%d]::parameter fileData's length[%d] is not equal to fileBlockSize[%d]",
						targetFileID, fileData.length, fileBlockSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}

		long targetFileOffset;

		if (append && startFileBlockNo == fileBlockNo) {
			if (sourceFileSize < fileBlockSize) {
				targetFileOffset = sourceFileSize - fileData.length;
			} else {
				targetFileOffset = (long) fileBlockSize * fileBlockNo + fileBlockSize - fileData.length;
			}

			// FIXME!
			try {
				long targetFileLength = targetFileChannel.size();
				if (targetFileOffset != targetFileLength) {
					log.warn("파일 송수신 덧붙이기 모드에서 계산해서 얻은 오프셋[{}]과 파일의 크기[{}]가 서로 다름", targetFileOffset, targetFileLength);
				}
			} catch (IOException e) {
				/** n 번째 목적지 파일 조각 쓰기 실패 */
				String errorMessage = String.format("targetFileID[%d]::%d 번째 목적지 파일[%s][%s] 조각 쓰기 실패", targetFileID,
						fileBlockNo, targetFilePathName, targetFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
			
		} else {
			targetFileOffset = (long) fileBlockSize * fileBlockNo;
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
			targetRandomAccessFile.seek(targetFileOffset);
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
	}
	
	public boolean isReleasedFileLock() {
		boolean isReleasedFileLock = true;
	
		if (null != targetRandomAccessFile || null != targetFileChannel || null != targetFileLock) {
			isReleasedFileLock = false;
		}
	
		return isReleasedFileLock;
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
		builder.append("LocalTargetFileResource [ownerID=");
		builder.append(ownerID);
		builder.append(", localWorkStep=");
		builder.append(workStep);
		builder.append(", sourceFileID=");
		builder.append(sourceFileID);
		builder.append(", targetFileID=");
		builder.append(targetFileID);
		builder.append(", append=");
		builder.append(append);
		builder.append(", sourceFilePathName=");
		builder.append(sourceFilePathName);
		builder.append(", sourceFileName=");
		builder.append(sourceFileName);
		builder.append(", sourceFileSize=");
		builder.append(sourceFileSize);
		builder.append(", targetFilePathName=");
		builder.append(targetFilePathName);
		builder.append(", targetFileName=");
		builder.append(targetFileName);
		builder.append(", targetFileSize=");
		builder.append(targetFileSize);
		builder.append(", fileBlockSize=");
		builder.append(fileBlockSize);
		builder.append(", startFileBlockNo=");
		builder.append(startFileBlockNo);
		builder.append(", endFileBlockNo=");
		builder.append(endFileBlockNo);
		builder.append(", firstFileDataLength=");
		builder.append(firstFileDataLength);
		builder.append(", lastFileDataLength=");
		builder.append(lastFileDataLength);
		
		/*builder.append(", wantedSizeOfWorkedFileBlockBitSet=");
		builder.append(wantedCardinalityOfWorkedFileBlockBitSet);*/
		builder.append(", workedFileBlockBitSet.cardinality()=");
		builder.append(workedFileBlockBitSet.cardinality());
		builder.append(", workedFileBlockBitSet.length()=");
		builder.append(workedFileBlockBitSet.length());
		
		builder.append(", targetRandomAccessFile=");
		builder.append(targetRandomAccessFile);
		builder.append(", targetFileChannel=");
		builder.append(targetFileChannel);
		builder.append(", targetFileLock=");
		builder.append(targetFileLock);
		builder.append(", fileBlockMaxSize=");
		builder.append(fileBlockMaxSize);
		builder.append(", viewObject=");
		builder.append(viewObject);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void finalize() {
		log.warn("큐에 반환되지 못한 랩 버퍼 소멸");
		releaseFileLock();
	}

}
