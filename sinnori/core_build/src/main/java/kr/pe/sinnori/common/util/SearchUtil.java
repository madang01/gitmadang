package kr.pe.sinnori.common.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchUtil {		
	public static final int MIN_BUFFER_SIZE = 1024;
	public static final int MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO = 5*1000;
	
	// Sleep time when the number of bytes read is 0.
	
	public static long findKeywordInFile(File soruceFile, byte[] searchKeyword, int bufferSize, long sleepTimeWhenTheNumberOfBytesReadIsZero) throws Exception {
		if (null == soruceFile) {
			throw new IllegalArgumentException("the parameter soruceFile is null");
		}
		if (!soruceFile.exists()) {
			throw new IllegalArgumentException(
					String.format("the parameter soruceFile[%s] doesn't exist", soruceFile.getAbsolutePath()));
		}

		if (!soruceFile.isFile()) {
			throw new IllegalArgumentException(
					String.format("the parameter soruceFile[%s] isn't a normal file", soruceFile.getAbsolutePath()));
		}

		if (null == searchKeyword) {
			throw new IllegalArgumentException("the parameter searchKeyword is null");
		}
		
		if (0 == searchKeyword.length) {
			throw new IllegalArgumentException("the parameter searchKeyword's length is zero");
		}
		
		if (bufferSize < MIN_BUFFER_SIZE) {
			throw new IllegalArgumentException(String.format("the parameter bufferSize[%d] must be greater than or equals to %d", bufferSize, MIN_BUFFER_SIZE));
		}
		
		if (bufferSize < searchKeyword.length) {
			throw new IllegalArgumentException(String.format("the parameter bufferSize[%d] must be greater than or equals to the parameter searchKeyword's length[%d]", bufferSize, searchKeyword.length));
		}
		
		if (0 >= sleepTimeWhenTheNumberOfBytesReadIsZero) {
			throw new IllegalArgumentException(String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be greater than zero", sleepTimeWhenTheNumberOfBytesReadIsZero));
		}
		
		if (sleepTimeWhenTheNumberOfBytesReadIsZero > MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO) {
			throw new IllegalArgumentException(String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", sleepTimeWhenTheNumberOfBytesReadIsZero, MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO));
		}
		
		
		final long sourceFileSize = soruceFile.length();
		if (sourceFileSize < searchKeyword.length) {
			return -1;
		}

		FileChannel sourceFileChannel = null;
		RandomAccessFile sourceRandomAccessFile = null;
		FileLock soruceFileLock = null;
		ByteBuffer sourceByteBuffer = ByteBuffer.allocate(bufferSize);
		try {
			sourceRandomAccessFile = new RandomAccessFile(soruceFile, "r");

			sourceFileChannel = sourceRandomAccessFile.getChannel();
			
			soruceFileLock = sourceFileChannel.lock(0, Long.MAX_VALUE, true);

			// long sourceFileSize = sourceFileChannel.size();
			/*if (0 == sourceFileSize) {
				return -1L;
			}
			
			if (sourceFileSize < searchKeyword.length) {
				return -1L;
			}*/
			/** 변수 currentPosition 는 파일 내 키워드 일치 여부를 판단할때의 위치이다. */
			long currentPosition = 0;
			boolean isSearchKeyworkd = true;
			int remaining = -1;
			int readBytes = -1;
			
			while ((currentPosition+searchKeyword.length) <= sourceFileSize) {
				
				/**
				 * 남아 있는 데이터가 버퍼 크기 보다 크다면 버퍼가 꽉 찰때까지 읽어 온다.
				 * 남아 있는 데이터가 버퍼 크기 보다 작거나 같다면 남은 크기만큼 전부 읽어 온다.    
				 */
				do {
					readBytes = sourceFileChannel.read(sourceByteBuffer);
					if (0 == readBytes) {
						Logger.getGlobal().log(Level.INFO, "sleep becase the read bytes is zero");
						Thread.sleep(sleepTimeWhenTheNumberOfBytesReadIsZero);
					}
				} while(-1 != readBytes && sourceByteBuffer.hasRemaining());
				
				/** 읽어온 데이터에서 키워드 일치 여부 검사 작업을 위한 사전 준비 작업 */
				sourceByteBuffer.flip();				
				
				/** 버퍼내 키워드 일치 여부 검사 */
				for (remaining = sourceByteBuffer.remaining(); remaining  >= searchKeyword.length; remaining--, currentPosition++) {
					
					if (searchKeyword[0] == sourceByteBuffer.get()) {
						isSearchKeyworkd = true;
						sourceByteBuffer.mark();
						for (int j = 1; j < searchKeyword.length; j++) {
							if (searchKeyword[j] != sourceByteBuffer.get()) {
								isSearchKeyworkd = false;
								break;
							}
						}
						sourceByteBuffer.reset();
						
						if (isSearchKeyworkd) {
							return currentPosition;
						}
					}
				}
				
				/** 남아 있는 데이터에 이어 받기를 위한 준비 작업 */
				sourceByteBuffer.compact();
			}			
			
		} catch (Exception e) {
			Logger.getGlobal().log(Level.WARNING, "fail to work", e);
			throw e;
		} finally {
			if (null != soruceFileLock) {
				try {
					soruceFileLock.release();
				} catch (IOException e) {
					Logger.getGlobal().log(Level.WARNING, "fail to release source file lock", e);
				}
			}
			
			if (null != sourceRandomAccessFile) {
				try {
					sourceRandomAccessFile.close();
				} catch (IOException e) {
					Logger.getGlobal().log(Level.WARNING, "fail to close source random access file", e);
				}
			}

			if (null != sourceFileChannel) {
				try {
					sourceFileChannel.close();
				} catch (IOException e) {
					Logger.getGlobal().log(Level.WARNING, "fail to close source file channel", e);
				}
			}
		}		
		
		return -1;		
	}
}
