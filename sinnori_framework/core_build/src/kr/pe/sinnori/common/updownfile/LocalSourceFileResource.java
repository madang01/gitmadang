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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.BitSet;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * <pre>
 * 로컬 원본 파일을 원할하게 송신하기 위한 로컬 원본 파일 자원 클래스.
 * 로컬 원본 파일 자원은 파일락, 파일 채널, 비트 셋으로 표현된 작업 완료 여부 정보가 있다. 
 * 주) 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다.
 * </pre>
 * @author Jonghoon Won
 *
 */
public class LocalSourceFileResource implements CommonRootIF {
	private final Object monitor = new Object();
	
	private boolean isInQueue = true;
	
	private int sourceFileID = 0;
	// private int clientTargetFileID = -1;
	private String targetFilePathName = null;
	private String targetFileName = null;
	private long sourceFileSize = 0;
	private String sourceFilePathName = null;
	private String sourceFileName = null;
	private int fileBlockSize = 0;
	private long fileBlockMaxNo = 0;
	private long lastFileDataLength = 0;
	/** 주의점 : BitSet 의 크기는 동적으로 자란다. 따라서 해당 비트 인덱스에 대한 엄격한 제한이 필요하다. */
	private BitSet workedFileBlockBitSet = null;
	private RandomAccessFile sourceRandomAccessFile = null;
	private FileChannel sourceFileChannel = null;
	private FileLock sourceFileLock = null;
	
	public LocalSourceFileResource(int sourceFileID) {
		this.sourceFileID = sourceFileID;
	}
	
	/**
	 * <pre> 
	 * 로컬에 있는 원본 파일을 원격지에 있는 목적지 파일로 복사할 준비로 로컬 원본 파일의 락을 건다.
	 * 	 * 참고) protected 선언은 동일 패키지 클래스인 {@link LocalSourceFileResourceManager}  접근시키기 위한 조취이다.
	 * </pre> 
	 * @param sourceFilePathName 원본 파일의 경로 이름
	 * @param sourceFileName 원본 파일 이름
	 * @param sourceFileSize 원본 파일 크기
	 * @param targetFilePathName 원본 파일의 경로 이름
	 * @param targetFileName 원본 파일 이름
	 * @param fileBlockSize 파일 송수신 파일 조각 크기
	 * @thrwos IllegalArgumentException 잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException 파일 송수신과 관련된 파일 관련 작업시 발생한 에러
	 */
	protected void readyReadingFile(String sourceFilePathName, String sourceFileName, long sourceFileSize,
			String targetFilePathName,String targetFileName, int fileBlockSize) throws IllegalArgumentException, UpDownFileException {
		if (null == sourceFilePathName) {
			String errorMessage = String.format("sourceFileID[%d]::parameter sourceFilePathName is null", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		sourceFilePathName = sourceFilePathName.trim();
		
		if (sourceFilePathName.equals("")) {
			String errorMessage = String.format("sourceFileID[%d]::parameter sourceFilePathName is a empty", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == sourceFileName) {
			String errorMessage = String.format("sourceFileID[%d]::parameter sourceFileName is null", sourceFileID);
			log.warn(errorMessage);			
			throw new IllegalArgumentException(errorMessage);
		}
		
		sourceFileName = sourceFileName.trim();
		
		if (sourceFileName.equals("")) {
			String errorMessage = String.format("sourceFileID[%d]::parameter sourceFileName is a empty", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (sourceFileSize <= 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter sourceFileSize[%d] less than zero", sourceFileID, sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (null == targetFilePathName) {
			String errorMessage = String.format("sourceFileID[%d]::parameter targetFilePathName is null", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		targetFilePathName = targetFilePathName.trim();
		
		if (targetFilePathName.equals("")) {
			String errorMessage = String.format("sourceFileID[%d]::parameter targetFilePathName is a empty", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (null == targetFileName) {
			String errorMessage = String.format("sourceFileID[%d]::parameter targetFileName is null", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		targetFileName = targetFileName.trim();
		
		if (targetFileName.equals("")) {
			String errorMessage = String.format("sourceFileID[%d]::parameter targetFileName is a empty, so change to parameter sourceFileName[%s]", sourceFileID, sourceFileName); 
			log.info(errorMessage);
			targetFileName = sourceFileName;
		}

		if (fileBlockSize <= 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockSize[%d] less than or equal zero", sourceFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (0 != (fileBlockSize % 1024)) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockSize[%d] is not a multiple of 1024", sourceFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (fileBlockSize > 1024*1024L) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockSize[%d] is not a multiple of 1024", sourceFileID, fileBlockSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		// this.clientTargetFileID = clientTargetFileID;
		this.targetFilePathName = targetFilePathName;
		this.targetFileName = targetFileName;
		this.sourceFileSize= sourceFileSize;
		this.sourceFilePathName = sourceFilePathName;
		this.sourceFileName = sourceFileName;
		this.fileBlockSize = fileBlockSize ;
		this.fileBlockMaxNo = (sourceFileSize + fileBlockSize -1) / fileBlockSize - 1;
		
		
		if (fileBlockMaxNo > Integer.MAX_VALUE - 1) {
			/**  
			 * 자바는 배열 크기가 정수로 제한되는데, 파일 조각 받은 여부를 기억하는 BitSet 도 그대로 그 문제를 상속한다.
			 * 따라서 fileBlock 최대 갯수는 정수(=Integer) 이어야 한다.  
			 */
			String errorMessage = String.format("sourceFileID[%d]::fileBlockMaxNo[%d] greater than (Integer.MAX - 1), maybe parameter fileBlockSize[%d] is not enough size or parameter sourceFileSize[%d] too big", sourceFileID, fileBlockMaxNo, fileBlockSize, sourceFileSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.lastFileDataLength = sourceFileSize - fileBlockMaxNo * fileBlockSize;
		
		
		workedFileBlockBitSet = new BitSet((int)fileBlockMaxNo+1);
		
		
		File sourceFilePath = new File(sourceFilePathName);
		
		if (!sourceFilePath.exists()) {
			/**  원본 파일의 경로가 존재하지 않습니다. */
			String errorMessage = String.format("sourceFileID[%d]::source file path[%s] not exist", sourceFileID, sourceFilePathName);
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
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s] 신규 생성시 입출력 에러 발생", sourceFileID, sourcefullFileName);
				log.warn(errorMessage);
				throw new UpDownFileException(errorMessage);
			}
		}
		
		if (!sourceFileObj.canWrite()) {
			/** 원본 파일 쓰기 권한 없음 */
			String errorMessage = String.format("sourceFileID[%d]::source file[%s] can't be written", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		if (!sourceFileObj.canRead()) {
			/** 원본 파일 읽기 권한 없음 */
			String errorMessage = String.format("sourceFileID[%d]::source file[%s] can't be read", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		
		
		
		try {
			sourceRandomAccessFile = new RandomAccessFile(sourceFileObj, "rw");
		} catch (FileNotFoundException e) {
			/** 락을 걸기 위한 랜덤 접근 파일 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다. */
			String errorMessage = String.format("sourceFileID[%d]::락을 걸기 위한 원본 랜덤 접근 파일[%s] 객체 생성 실패, 경로및 파일 접근 권한을 점검하시기 바랍니다.", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		// sourceRandomAccessFile.
		// sourceRandomAccessFile.setLength(sourceFileSize);
		
		
		sourceFileChannel = sourceRandomAccessFile.getChannel();
		try {
			sourceFileLock = sourceFileChannel.tryLock();
		} catch (IOException e) {
			/** 원본 파일에 락을 걸때 입출력 에러 발생 */
			String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s]에 락을 걸때 입출력 에러 발생", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		} catch(OverlappingFileLockException e) {
			/** 원본 파일에 락을 걸때 중복 락 에러 발생 */
			String errorMessage = String.format("sourceFileID[%d]::이미 락이 걸린 원본 파일[%s]입니다.", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		if (null == sourceFileLock) {
			/** 다른 프로그램에서 락을 걸어 원본 파일 락 획득에 실패 */
			String errorMessage = String.format("sourceFileID[%d]::다른 프로그램에서 락을 걸어 원본 파일[%s] 락 획득에 실패", sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			new UpDownFileException(errorMessage);
		}
		
		
		// long realSourceFileSize = sourceFileObj.length();
		long realSourceFileSize = -1;
		try {
			realSourceFileSize = sourceFileChannel.size();
		} catch (IOException e) {
			/** 입출력 에러 발생으로 파일 크기 얻기 실패 */
			releaseFileLock();
			String errorMessage = String.format("sourceFileID[%d]::입출력 에러 발생으로 파일[%s] 크기 얻기 실패", 
					sourceFileID, sourcefullFileName);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}

		if (realSourceFileSize != sourceFileSize) {
			/** 클라이언트에서 인식하는 서버 소스 파일의 크기외 실제 크기가 같지 않습니다. */
			releaseFileLock();
			String errorMessage = String.format("sourceFileID[%d]::클라이언트에서 인식하는 서버 소스 파일[%s]의 크기[%d]외 실제 크기[%d]가 같지 않습니다.", 
					sourceFileID, sourcefullFileName, realSourceFileSize, sourceFileSize);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
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
	}

	/**
	 * 큐에서 나올때 상태 변화를 주는 클래스
	 */
	public void queueOut() {
		isInQueue = false;
		// lastCallerThrowable = new Throwable();
	}

	/**
	 * 원본 파일에 걸은 파일 락을 해제한다.
	 * 참고) protected 선언은 동일 패키지 클래스인 송수신 파일 자원 관리자만 접근시키기 위한 조취이다.
	 */
	protected void releaseFileLock() {
		// FIXME!
		log.info(String.format("call releaseFileLock, sourceFileID[%d], 원본 파일[%s][%s]", sourceFileID, sourceFilePathName, sourceFileName));
		
		workedFileBlockBitSet = null;
		
		if (null != sourceFileLock) {
			try {
				sourceFileLock.release();
				sourceFileLock = null;
			} catch (IOException e) {
				/** 파일 락 해제시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 락 해제시 입출력 에러 발생", sourceFileID, sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}

		if (null != sourceFileChannel) {
			try {
				sourceFileChannel.close();
				sourceFileChannel = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 채널 닫기시 입출력 에러 발생", sourceFileID, sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}
		
		if (null != sourceRandomAccessFile) {
			try {
				sourceRandomAccessFile.close();
				sourceRandomAccessFile = null;
			} catch (IOException e) {
				/** 파일 채널 닫기시 입출력 에러 발생 */
				String errorMessage = String.format("sourceFileID[%d]::원본 파일[%s][%s] 랜덤 접근 파일 객체 닫기시 입출력 에러 발생", sourceFileID, sourceFilePathName, sourceFileName);
				log.warn(errorMessage, e);
			}
		}
	}
	
	/**
	 * <pre>
	 * 원본 파일에서 파일 조각 번호에 대응하는 데이터를 담을 바이트 배열을 반환한다. 
	 * 마지막 전까지는 지정한 파일 조각 크기, 마지막은 남은 파일 데이터 크기가 된다.
	 * 
	 * 주의점) 이 메소드는 아래와 같은 자바언어의 제한 때문에 만들었다.
	 * 자바는 파라미터가 객체인 경우 call by referfence 이지만 C 언어와 다르게 직접적으로 참조의 참조를 넘길 수가 없다.
	 * 예를 드면 void chgBytes(byte[] parmBytes) { parmBytes = new byte[10]; } 
	 * void test() { byte srcBytes[] = null; chgBytes(srcBytes); if (null == srcBytes) System.out.println("srcBytes is null"); else  System.out.println("srcBytes is not null"); }   
	 * 라고 할때 test 메소드 호출 결과로 "srcBytes is null" 이다. 
	 * 이 문제를 극복할 방법은 여러가지가 있을 수 있지만,
	 * 아래 메소드를 통해서 이를 해결하는 방법을 선택하였다. 
	 * </pre>
	 * @param fileBlockNo 원본 파일 조각 번호
	 * @return 원본 파일에서 파일 조각 번호에 대응하는 데이터를 담을 바이트 배열
	 */
	public byte[] getByteArrayOfFileBlockNo(int fileBlockNo) {
		byte[] fileData = null;
		if (fileBlockNo == fileBlockMaxNo) {
			fileData = new byte[(int)lastFileDataLength];
			
		} else {
			fileData = new byte[fileBlockSize];
			
		}
		return fileData;
	}
	
	/**
	 * 수신한 파일 조각을 저장한다. 
	 * @param fileBlockNo 파일 조각 번호
	 * @param fileData 파일 조각 번호에 대응하는 파일 조각 데이터를 저장할 바이트 배열
	 * @param checkOver 중복시 에러 발생 유무, 참이면 중복시 에러 발생, 거짓이면 중복 허용
	 * @return 모든 파일 조각 수신 여부
	 * @throws IllegalArgumentException 잘못된 파라미터 입력시 던지는 예외
	 * @throws UpDownFileException 수신한 파일 조각을 저장하는 과정에서 에러 발생시 던지는 예외
	 */
	public boolean readSourceFileData(int fileBlockNo, byte[] fileData, boolean checkOver) throws IllegalArgumentException, UpDownFileException {
		// sourceFileBuffer
		if (fileBlockNo < 0) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockNo[%d] less than zero", sourceFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (fileBlockNo > fileBlockMaxNo) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileBlockNo[%d] greater than maxFileBlockNo[%d]", sourceFileID, fileBlockNo);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == fileData) {
			String errorMessage = String.format("sourceFileID[%d]::parameter fileData is null", sourceFileID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		// FIXME!
		// log.info(String.format("fileBlockNo=[%d], fileData.length=[%d], checkOver=[%s]", fileBlockNo, fileData.length, checkOver));

		/*
		// 로컬 원본 파일 자원 관리자는 느슷한 구조의 큐 자원 관리자로 통제된 사용 방법외 방법으로 사용시 비 정상 동작한다. 
		if (isInQueue) {
			String errorMessage = String.format("경고::로컬 원본 파일 자원 관리자 큐에 있는 상태에서 호출, sourceFileID[%d]", sourceFileID);
			log.warn(errorMessage);
			throw new UpDownFileException(errorMessage);
		}
		*/
		
		// sourceRandomAccessFile.seek(fileBlockSize*fileBlockNo);
		// sourceRandomAccessFile.write(fileData);
		try {
			sourceFileChannel.read(ByteBuffer.wrap(fileData), (long)fileBlockSize*fileBlockNo);
		} catch (IOException e) {
			/** n 번째 원본 파일 조각 읽기 실패 */
			String errorMessage = String.format("sourceFileID[%d]::%d 번째 원본 파일[%s][%s] 조각 읽기 실패", sourceFileID, fileBlockNo, sourceFilePathName, sourceFileName);
			log.warn(errorMessage);
			new UpDownFileException(errorMessage);
		}
		
		
		
		synchronized (monitor) {
			if (workedFileBlockBitSet.get(fileBlockNo)) {
				/** 파일 조각 중복 도착 */
				String errorMessage = String.format("sourceFileID[%d]::파일 조각[%d] 중복 도착", sourceFileID, fileBlockNo); 
				log.warn(errorMessage);
				if (checkOver) throw new UpDownFileException(errorMessage);
			} else {
				workedFileBlockBitSet.set(fileBlockNo);
			}
			
			boolean isCompletedReadingFile = (workedFileBlockBitSet.cardinality() == (fileBlockMaxNo+1));
			return isCompletedReadingFile;
		}
	}
	
	/**
	 * @return the sourceFileID
	 */
	public int getSourceFileID() {
		return sourceFileID;
	}
	
	/**
	 * @return the clientTargetFileID
	 */
	/*
	public int getClientTargetFileID() {
		return clientTargetFileID;
	}
	*/

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
	
	public int getFileBlockMaxNo() {
		return (int)fileBlockMaxNo;
	}
	
	@Override
	public void finalize() {		
		log.warn("큐에 반환되지 못한 랩 버퍼 소멸");
		releaseFileLock();
	}
	
	 

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpFileResource [isInQueue=");
		builder.append(isInQueue);
		builder.append(", sourceFileID=");
		builder.append(sourceFileID);
		// builder.append(", clientTargetFileID=");
		// builder.append(clientTargetFileID);
		builder.append(", targetFilePathName=");
		builder.append(targetFilePathName);
		builder.append(", targetFileName=");
		builder.append(targetFileName);
		builder.append(", sourceFileSize=");
		builder.append(sourceFileSize);
		builder.append(", sourceFilePathName=");
		builder.append(sourceFilePathName);
		builder.append(", sourceFileName=");
		builder.append(sourceFileName);
		builder.append(", fileBlockSize=");
		builder.append(fileBlockSize);
		builder.append("]");
		return builder.toString();
	}
}
