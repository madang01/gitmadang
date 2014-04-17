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
import java.util.BitSet;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * <pre>
 * 로컬 목적지 파일을 원할하게 수신하기 위한 로컬 목적지 파일 자원 클래스.
 * 로컬 목적지 파일 자원은 파일락, 파일 채널, 비트 셋으로 표현된 작업 완료 여부 정보가 있다. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * 
 * @author Jonghoon Won
 * 
 */
public class LocalTargetFileResource implements CommonRootIF {
	private final Object monitor = new Object();

	private boolean isCanceled = false;
	private boolean isInQueue = true;
		
	private int sourceFileID = -1;
	private int targetFileID = 0;
	
	private boolean append;
	
	private String sourceFilePathName = null;
	private String sourceFileName = null;
	private long sourceFileSize = 0;
	
	private String targetFilePathName = null;
	private String targetFileName = null;
	private long targetFileSize = 0;
	
	
	private long fileBlockSize = 0;
	
	private long startFileBlockNo = 0L;
	private long endFileBlockNo = 0;
	private long firstFileDataLength = 0L;
	private long lastFileDataLength = 0;
	
	/** 주의점 : BitSet 의 크기는 동적으로 자란다. 따라서 해당 비트 인덱스에 대한 엄격한 제한이 필요하다. */
	private BitSet workedFileBlockBitSet = null;
	private RandomAccessFile targetRandomAccessFile = null;
	private FileChannel targetFileChannel = null;
	private FileLock targetFileLock = null;

	// private String oldSourceFileInfo = null;
	// private ArrayList<MappedByteBuffer> mappedByteBufferList = new ArrayList<MappedByteBuffer>();

	/**
	 * @return the isCanceled
	 */
	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * 
	 */
	public void cancel() {
		this.isCanceled = true;
	}

	public LocalTargetFileResource(int targetFileID) {
		this.targetFileID = targetFileID;
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
	 * <pre>
	 * 원격지에 있는 원본 파일을 로컬 목적지 파일로 복사할 준비로 로컬 목적지 파일의 락을 건다.
	 * 만약 목적지 파일이 없으면 신규 생성, 목적지 파일이 있으면 크기 0 으로 기존 데이터가 삭제된다.
	 * 	 * 참고) protected 선언은 동일 패키지 클래스인 {@link LocalTargetFileResourceManager}  접근시키기 위한 조취이다.
	 * </pre>
	 * 
	 * @param append 이어받기 여부
	 * @param sourceFilePathName 원본 파일의 경로 이름
	 * @param sourceFileName 경로명을 제외한 원본 파일 이름
	 * @param sourceFileSize 원본 파일 크기
	 * @param targetFilePathName 목적지 파일의 경로 이름
	 * @param targetFileName 경로명을 제외한 목적지 파일 이름
	 * @param targetFileSize 목적지 파일 크기
	 * @param fileBlockSize 파일 송수신 파일 조각 크기
	 * @throws IllegalArgumentException 잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException 파일 송수신과 관련된 파일 관련 작업시 발생한 에
	 */
	protected void readyWritingFile(boolean append, 
			String sourceFilePathName, String sourceFileName, long sourceFileSize,
			String targetFilePathName, String targetFileName, long targetFileSize, 
			int fileBlockSize)
			throws IllegalArgumentException, UpDownFileException {
		if (null == sourceFilePathName) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter sourceFilePathName is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		sourceFilePathName = sourceFilePathName.trim();

		if (sourceFilePathName.equals("")) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter sourceFilePathName is a empty",
							targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == sourceFileName) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter sourceFileName is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		sourceFileName = sourceFileName.trim();

		if (sourceFileName.equals("")) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter sourceFileName is a empty",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (sourceFileSize <= 0) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter sourceFileSize[%d] less than zero",
							targetFileID, sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*if (sourceFileSize > CommonStaticFinal.UP_DOWN_SOURCE_FILE_MAX_SIZE) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter sourceFileSize[%d] is over than max[%d]",
							targetFileID, sourceFileSize, CommonStaticFinal.UP_DOWN_SOURCE_FILE_MAX_SIZE);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}*/

		if (null == targetFilePathName) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter targetFilePathName is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		targetFilePathName = targetFilePathName.trim();

		if (targetFilePathName.equals("")) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter targetFilePathName is a empty",
							targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == targetFileName) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter targetFileName is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		targetFileName = targetFileName.trim();

		if (targetFileName.equals("")) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter targetFileName is a empty, so change to parameter sourceFileName[%s]",
							targetFileID, sourceFileName);
			log.info(errorMessage);
			targetFileName = sourceFileName;
		}
		
		if (targetFileSize < 0) {
			String errorMessage = String
					.format("targetFileID[%d]::targetFile[%s][%s]::parameter targetFileSize[%d] less than zero",
							targetFileID, targetFilePathName, targetFileName, targetFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize <= 0) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockSize[%d] less than or equal zero",
							targetFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (0 != (fileBlockSize % 1024)) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockSize[%d] is not a multiple of 1024",
							targetFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockSize > (Integer)conf.getResource("common.updownfile.file_block_max_size.value")) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockSize[%d] is over than max[%d]",
							targetFileID, fileBlockSize, (Integer)conf.getResource("common.updownfile.file_block_max_size.value"));
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		
		
		this.sourceFilePathName = sourceFilePathName;
		this.sourceFileName = sourceFileName;
		this.sourceFileSize = sourceFileSize;
		this.targetFilePathName = targetFilePathName;
		this.targetFileName = targetFileName;
		this.targetFileSize = targetFileSize;
		this.fileBlockSize = fileBlockSize;
		
		this.endFileBlockNo = (sourceFileSize + fileBlockSize - 1) / fileBlockSize - 1;

		if (endFileBlockNo > Integer.MAX_VALUE - 1) {
			/**
			 * 자바는 배열 크기가 정수로 제한되는데, 파일 조각 받은 여부를 기억하는 BitSet 도 그대로 그 문제를 상속한다.
			 * 따라서 fileBlock 최대 갯수는 정수(=Integer) 이어야 한다.
			 */
			String errorMessage = String
					.format("targetFileID[%d]::endFileBlockNo[%d] greater than (Integer.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big",
							targetFileID, endFileBlockNo, fileBlockSize,
							sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		this.lastFileDataLength = sourceFileSize - endFileBlockNo * fileBlockSize;

		workedFileBlockBitSet = new BitSet((int) endFileBlockNo + 1);
		
		if (append) {
			/** 이어받기 */
			this.startFileBlockNo = (this.targetFileSize + fileBlockSize - 1) / fileBlockSize - 1;
			
			if (startFileBlockNo > Integer.MAX_VALUE - 1) {
				/**
				 * 자바는 배열 크기가 정수로 제한되는데, 파일 조각 받은 여부를 기억하는 BitSet 도 그대로 그 문제를 상속한다.
				 * 따라서 fileBlock 최대 갯수는 정수(=Integer) 이어야 한다.
				 */
				String errorMessage = String
						.format("targetFileID[%d]::startFileBlockNo[%d] is greater than (Integer.MAX - 1)",
								targetFileID, startFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (startFileBlockNo > endFileBlockNo) {
				String errorMessage = String
						.format("targetFileID[%d]::the variable 'startFileBlockNo'[%d] is greater than endFileBlockNo[%d], sourceFileSize=[%d], ",
								targetFileID, startFileBlockNo, endFileBlockNo);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
			
			this.firstFileDataLength = fileBlockSize - (this.targetFileSize - startFileBlockNo * fileBlockSize);
			
			/** 이어 받기를 시작하는 위치 전까지 데이터 쓰기 여부를 참으로 설정한다. */
			for (int i=0; i < startFileBlockNo; i++ ) {
				workedFileBlockBitSet.set(i);
			}
		} else {
			/** 덮어 쓰기 */
			this.startFileBlockNo = 0;
			this.firstFileDataLength = fileBlockSize;
		}

		File targetFilePath = new File(targetFilePathName);

		if (!targetFilePath.exists()) {
			/** 목적지 파일의 경로가 존재하지 않습니다. */
			String errorMessage = String.format(
					"targetFileID[%d]::target file path[%s] not exist",
					targetFileID, targetFilePathName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		StringBuilder targetfullFileNameBuilder = new StringBuilder(
				targetFilePathName);
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
				String errorMessage = String.format(
						"targetFileID[%d]::목적지 파일[%s] 신규 생성시 입출력 에러 발생",
						targetFileID, targetfullFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
		}

		if (!targetFileObj.canWrite()) {
			/** 목적지 파일 쓰기 권한 없음 */
			String errorMessage = String.format(
					"targetFileID[%d]::target file[%s] can't be written",
					targetFileID, targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		if (!targetFileObj.canRead()) {
			/** 목적지 파일 읽기 권한 없음 */
			String errorMessage = String.format(
					"targetFileID[%d]::target file[%s] can't be read",
					targetFileID, targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		try {
			targetRandomAccessFile = new RandomAccessFile(targetFileObj, "rw");
		} catch (FileNotFoundException e) {
			/** 락을 걸기 위한 랜덤 접근 파일 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다. */
			String errorMessage = String
					.format("targetFileID[%d]::락을 걸기 위한 목적지 랜덤 접근 파일[%s] 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다.",
							targetFileID, targetfullFileName);
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
			String errorMessage = String.format(
					"targetFileID[%d]::목적지 파일[%s]에 락을 걸때 입출력 에러 발생",
					targetFileID, targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		} catch (OverlappingFileLockException e) {
			/** 원본 파일에 락을 걸때 중복 락 에러 발생 */
			String errorMessage = String.format(
					"targetFileID[%d]::이미 락이 걸린 목적지 파일[%s]입니다.", targetFileID,
					targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}

		if (null == targetFileLock) {
			/** 다른 프로그램에서 락을 걸어 목적지 파일 락 획득에 실패 */
			String errorMessage = String.format(
					"targetFileID[%d]::다른 프로그램에서 락을 걸어 목적지 파일[%s] 락 획득에 실패",
					targetFileID, targetfullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		long realTargetFileSize = -1;
		try {
			realTargetFileSize = targetFileChannel.size();
		} catch (IOException e) {
			/** 입출력 에러 발생으로 파일 크기 얻기 실패 */
			releaseFileLock();
			String errorMessage = String.format(
					"targetFileID[%d]::입출력 에러 발생으로 목적지 파일[%s] 크기 얻기 실패",
					targetFileID, targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}

		if (realTargetFileSize != targetFileSize) {			
			releaseFileLock();
			String errorMessage = String
					.format("targetFileID[%d]::목적지 파일[%s]의 크기[%d]외 실제 크기[%d]가 같지 않습니다.",
							targetFileID, targetfullFileName,
							realTargetFileSize, targetFileSize);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		// FIXME!
		/*
		int mappedByteBufferListSize = (int)endFileBlockNo + 1;
		long tmpSourceFileSize = sourceFileSize;
		int i=0;
		try {
			for (; i < mappedByteBufferListSize; i++) {
				long minSize = Math.min(tmpSourceFileSize, (long)fileBlockSize);
				
				MappedByteBuffer mappedByteBuffer = targetFileChannel.map(MapMode.READ_WRITE, 0L+(long)i*fileBlockSize, minSize); 
				
				mappedByteBufferList.add(mappedByteBuffer);
				
				tmpSourceFileSize -= fileBlockSize;
			}
		} catch (Exception e) {
			releaseFileLock();
			String errorMessage = String.format(
					"[%d] targetFileID[%d]::입출력 에러 발생으로 목적지 파일[%s] 에서 MappedByteBuffer 얻기 실패",
					i, targetFileID, targetfullFileName);
			log.warn(errorMessage, e);
			throw new UpDownFileException(errorMessage);
		}
		
		log.info(String.format("mappedByteBufferList init success, mappedByteBufferListSize[%d]", mappedByteBufferListSize));
		*/
	}

	/**
	 * @return 큐에 들어간 상태 여부
	 */
	public boolean isInQueue() {
		return isInQueue;
	}

	/**
	 * 큐에 들어갈대 상태 변화를
	 */
	public void queueIn() {
		isInQueue = true;
		isCanceled = false;
		// mappedByteBufferList.clear();
	}

	/**
	 * 큐에서 나올때 상태 변화를 주는 클래스
	 */
	public void queueOut() {
		isInQueue = false;
		// lastCallerThrowable = new Throwable();
	}

	/**
	 * 목적지 파일에 걸은 파일 락을 해제한다. 참고) protected 선언은 동일 패키지 클래스인 송수신 파일 자원 관리자만 접근시키기
	 * 위한 조취이다.
	 */
	protected void releaseFileLock() {
		log.info(String.format(
				"call releaseFileLock, targetFileID[%d], 목적지 파일[%s][%s]",
				targetFileID, targetFilePathName, targetFileName));

		sourceFileID = -1;
		
		synchronized (monitor) {
			workedFileBlockBitSet = null;
		}

		if (null != targetFileLock) {
			try {
				targetFileLock.release();
				targetFileLock = null;
			} catch (IOException e) {
				/** 파일 락 해제시 입출력 에러 발생 */
				String errorMessage = String.format(
						"targetFileID[%d]::목적지 파일[%s][%s] 락 해제시 입출력 에러 발생",
						targetFileID, targetFilePathName, targetFileName);
				log.warn(errorMessage, e);
			}
		}

		if (null != targetFileChannel) {
			try {
				targetFileChannel.close();
				targetFileChannel = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format(
						"targetFileID[%d]::목적지 파일[%s][%s] 채널 닫기시 입출력 에러 발생",
						targetFileID, targetFilePathName, targetFileName);
				log.warn(errorMessage, e);
			}
		}

		if (null != targetRandomAccessFile) {
			try {
				targetRandomAccessFile.close();
				targetRandomAccessFile = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String
						.format("targetFileID[%d]::목적지 파일[%s][%s] 랜덤 접근 파일 객체 닫기시 입출력 에러 발생",
								targetFileID, targetFilePathName,
								targetFileName);
				log.warn(errorMessage, e);
			}
		}
	}

	/**
	 * <pre>
	 * 목적지 파일 크기를 재 조정한다.
	 * 
	 * 중복 받기 이면 크기 0 으로 이어받기 이면 아무런 동작도 하지 않는다.
	 * 목적지 파일 크기는 서버/클라이언트 쌍방 다운로드 할 준비가 끝났을 시점에 크기 재조정이 되어야 한다.
	 * 업로드시에는 서버 업로드 준비 비지니스 로직에서 호출될것이며,
	 * 다운로드시에는 서버 다운로드 준비 출력 메시지를 받은 후 호출될것이다.
	 * </pre> 
	 * @throws UpDownFileException 파일 크기 재조정시 입출력 에러 발생시 던지는 예외
	 */
	public void truncate() throws UpDownFileException {
		if (append) return;
		
		/** 덮어 쓰기일때 크기 0으로 설정 */
		try {
			targetFileChannel.truncate(0L);
		} catch (IOException e) {
			/** 기존에 데이터 삭제를 위한 목적지 파일 크기 0 으로 설정 실패 */
			releaseFileLock();
			String errorMessage = String
					.format("targetFileID[%d]::기존에 데이터 삭제를 위한 목적지 파일[%s][%s] 크기 0 으로 설정 실패",
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
	public boolean writeTargetFileData(int fileBlockNo, byte[] fileData,
			boolean checkOver) throws IllegalArgumentException,
			UpDownFileException {
		if (fileBlockNo < 0) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockNo[%d] less than zero",
							targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (fileBlockNo < startFileBlockNo) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockNo[%d] is less than startFileBlockNo[%d]",
							targetFileID, fileBlockNo, startFileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockNo[%d] greater than maxFileBlockNo[%d]",
							targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == fileData) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileData is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo == endFileBlockNo) {
			if (fileData.length != lastFileDataLength) {
				String errorMessage = String
						.format("targetFileID[%d]::parameter fileData's length[%d] is not equal to lastFileDataLength[%d]",
								targetFileID, fileData.length,
								lastFileDataLength);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		} else if (fileBlockNo == startFileBlockNo) {
			if (fileData.length != firstFileDataLength) {
				String errorMessage = String
						.format("targetFileID[%d]::parameter fileData's length[%d] is not equal to firstFileDataLength[%d]",
								targetFileID, fileData.length,
								firstFileDataLength);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		} else {
			if (fileData.length != fileBlockSize) {
				String errorMessage = String
						.format("targetFileID[%d]::parameter fileData's length[%d] is not equal to fileBlockSize[%d]",
								targetFileID, fileData.length, fileBlockSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		long targetFileOffset;
		
		if (append && startFileBlockNo == fileBlockNo) {
			targetFileOffset = (long)fileBlockSize * fileBlockNo + fileBlockSize - fileData.length;
		} else {
			targetFileOffset = (long)fileBlockSize * fileBlockNo;
		}
		
		
		synchronized (monitor) {
			if (null == workedFileBlockBitSet) {
				log.info(String.format("workedFileBlockBitSet is null, targetFileID[%d] sourceFile[%s][%s] fileBlockNo[%d]", targetFileID, targetFilePathName, targetFileName, fileBlockNo));
				return false;
			}
			
			try {
				// FIXME!
				// targetFileChannel.write(ByteBuffer.wrap(fileData), (long) fileBlockSize * fileBlockNo);
				targetRandomAccessFile.seek(targetFileOffset);
				targetRandomAccessFile.write(fileData);
			} catch (IOException e) {
				/** n 번째 목적지 파일 조각 쓰기 실패 */
				String errorMessage = String.format(
						"targetFileID[%d]::%d 번째 목적지 파일[%s][%s] 조각 쓰기 실패",
						targetFileID, fileBlockNo, targetFilePathName,
						targetFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}

			if (workedFileBlockBitSet.get(fileBlockNo)) {
				/** 파일 조각 중복 도착 */
				String errorMessage = String.format(
						"targetFileID[%d]::파일 조각[%d] 중복 도착", targetFileID,
						fileBlockNo);
				log.warn(errorMessage);
				if (checkOver)
					throw new UpDownFileException(errorMessage);
			} else {
				workedFileBlockBitSet.set(fileBlockNo);
			}

			boolean isFinished = (workedFileBlockBitSet
					.cardinality() == (endFileBlockNo + 1));
			return isFinished;
		}
	}
	
	/**
	 * 
	 * @param fileBlockNo
	 * @param fileData
	 * @param checkOver
	 * @return
	 * @throws IllegalArgumentException
	 * @throws UpDownFileException
	 */
	/*
	public boolean writeTargetFileData2(int fileBlockNo, byte[] fileData,
			boolean checkOver) throws IllegalArgumentException,
			UpDownFileException {

		if (fileBlockNo < 0) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockNo[%d] less than zero",
							targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (fileBlockNo > endFileBlockNo) {
			String errorMessage = String
					.format("targetFileID[%d]::parameter fileBlockNo[%d] greater than maxFileBlockNo[%d]",
							targetFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == fileData) {
			String errorMessage = String.format(
					"targetFileID[%d]::parameter fileData is null",
					targetFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		
		if (fileBlockNo == endFileBlockNo) {
			if (fileData.length != lastFileDataLength) {
				String errorMessage = String
						.format("targetFileID[%d]::parameter fileData's length[%d] is not equal to lastFileDataLength[%d]",
								targetFileID, fileData.length,
								lastFileDataLength);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		} else {
			if (fileData.length != fileBlockSize) {
				String errorMessage = String
						.format("targetFileID[%d]::parameter fileData's length[%d] is not equal to fileBlockSize[%d]",
								targetFileID, fileData.length, fileBlockSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		synchronized (monitor) {
			if (null == workedFileBlockBitSet) {
				log.info(String.format("workedFileBlockBitSet is null, targetFileID[%d] sourceFile[%s][%s] fileBlockNo[%d]", targetFileID, targetFilePathName, targetFileName, fileBlockNo));
				return false;
			}
			
			// FIXME!
			MappedByteBuffer mappedByteBuffer = null;
			try {
				mappedByteBuffer = mappedByteBufferList.get(fileBlockNo);
				mappedByteBuffer.put(fileData);
			} catch(Exception  e) {
				log.fatal("Exception", e);
				System.exit(1);
			}			

			if (workedFileBlockBitSet.get(fileBlockNo)) {
				String errorMessage = String.format(
						"targetFileID[%d]::파일 조각[%d] 중복 도착", targetFileID,
						fileBlockNo);
				log.warn(errorMessage);
				if (checkOver)
					throw new UpDownFileException(errorMessage);
			} else {
				workedFileBlockBitSet.set(fileBlockNo);
			}

			boolean isFinished = (workedFileBlockBitSet
					.cardinality() == (endFileBlockNo + 1));
			return isFinished;
		}
	}
	*/

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

	@Override
	public void finalize() {
		log.warn("큐에 반환되지 못한 랩 버퍼 소멸");
		releaseFileLock();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpFileResource [isInQueue=");
		builder.append(isInQueue);
		builder.append(", isCanceled=");
		builder.append(isCanceled);
		builder.append(", append=");
		builder.append(append);
		builder.append(", sourceFileID=");
		builder.append(sourceFileID);
		builder.append(", targetFileID=");
		builder.append(targetFileID);
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
		builder.append("]");
		return builder.toString();
	}
}
