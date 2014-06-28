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

package impl.executor.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Random;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.common.util.HexUtil;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestFileUpDownCExtor extends AbstractClientExecutor {
	private String testSourceFilePath = null;
	private String testSourceFileName = null;
	private long testSourceFileSize = 0L;

	private String testTargetFilePath = null;
	private String testTargetFileName = null;
	private long testTargetFileSize = 0L;

	
	
	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig,
			MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException,
			DynamicClassCallException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException,
			MessageItemException, NoMatchOutputMessage,
			ServerExcecutorUnknownException, InterruptedException,
			NotLoginException {

		int dataBufferSize = clientProjectConfig.getDataPacketBufferSize();

		File srcFileObj = null;
		File dstFileObj = null;
		
		

		/**
		 * 시나리오 1 : 덮어쓰기 시나리오 1-1 : 데이터 버퍼 크기 대략 3배 복사
		 */
		srcFileObj = createNewSourceFile(dataBufferSize * 3L);
		saveSourceFileVariables(srcFileObj);

		dstFileObj = createNewTargetFile(false, 0L);
		saveTargetFileVariables(dstFileObj);

		doVirtualUpload(dataBufferSize, false);
		
		showReportMD5("덮어쓰기 시나리오 1-1", srcFileObj, dstFileObj);
		

		/**
		 * 시나리오 2 : 덮어쓰기 시나리오 1-2 : 데이터 버퍼 크기 대략 3배 보다 약간 큰 복사
		 */
		srcFileObj = createNewSourceFile(dataBufferSize * 3L + 100);
		saveSourceFileVariables(srcFileObj);

		dstFileObj = createNewTargetFile(false, 0L);
		saveTargetFileVariables(dstFileObj);

		doVirtualUpload(dataBufferSize, false);
		
		showReportMD5("덮어쓰기 시나리오 1-2", srcFileObj, dstFileObj);

		/**
		 * 시나리오 3 : 이어붙이기 시나리오 3-1 : 원본 파일이 50% 전송된후 실패하여 이어붙이기 
		 */
		srcFileObj = createNewSourceFile(dataBufferSize * 3L);
		saveSourceFileVariables(srcFileObj);

		
		try {
			copyFile(srcFileObj, dstFileObj);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		truncateFile(dstFileObj, dataBufferSize / 2L);
				
		// showReportMD5("이어붙이기 시나리오 3-1, 이어붙이기전 상황", srcFileObj, dstFileObj);		
		
		doVirtualUpload(dataBufferSize, true);
		
		showReportMD5("이어붙이기 시나리오 3-1", srcFileObj, dstFileObj);
		
		/**
		 * 시나리오 3 : 이어붙이기 시나리오 3-2 : 이어붙이기 과정에서 목적지 파일이 이어붙이기 전 데이터를 보존함을 증명 
		 */			
		srcFileObj = createNewSourceFile(dataBufferSize * 3L);
		saveSourceFileVariables(srcFileObj);

		long oldDstFileSize = dataBufferSize / 2L;
		
		dstFileObj = createNewTargetFile(false, oldDstFileSize);
		
		
		byte[] srcMD5Bytes = null;
		byte[] dstMD5Bytes = null;
		srcMD5Bytes = getMD5Checksum(dstFileObj);
		
		doVirtualUpload(dataBufferSize, true);
		
		truncateFile(dstFileObj, oldDstFileSize);
		
		dstMD5Bytes = getMD5Checksum(dstFileObj);
		
		showReportMD5("이어붙이기 시나리오 3-2", srcMD5Bytes, dstMD5Bytes);
	}

	/**
	 * 주어진 원본 파일 객체로 부터 파일 경로, 파일명, 크기 정보를 추출하여 멤버 변수로 저장한다.
	 * 
	 * @param srcFileObj
	 *            원본 파일 객체
	 */
	public void saveSourceFileVariables(File srcFileObj) {
		testSourceFilePath = srcFileObj.getParentFile().getAbsolutePath();
		testSourceFileName = srcFileObj.getName();
		testSourceFileSize = srcFileObj.length();

		log.info(String
				.format("testSourceFilePath[%s], testSourceFileName[%s], testSourceFileSize=[%d]",
						testSourceFilePath, testSourceFileName,
						testSourceFileSize));
	}

	/**
	 * 지정된 크기를 갖는 파일 업로드/다운로드시 사용할 원본 파일을 생성한다.
	 * 
	 * @param srcFileSize
	 *            원본 파일 크기, 0보다 커야 한다. 단위 byte.
	 * @return 지정된 크기를 갖는 원본 파일 객체
	 */
	public File createNewSourceFile(long srcFileSize) {
		if (srcFileSize <= 0) {
			String errorMessage = String.format(
					"sourceFileSize=[%d] is less than or equal to zero",
					srcFileSize);
			throw new IllegalArgumentException(errorMessage);
		}

		log.info(String.format("param sourceFileSize[%d]", srcFileSize));

		File sourceFileObj = null;

		try {
			sourceFileObj = File.createTempFile("testFileUpDownSrc", ".tmp");
		} catch (IOException e) {
			String errorMessage = "파일 업다운 테스트용 원본 파일 생성 실패";
			log.fatal(errorMessage, e);
			System.exit(1);
		}

		RandomAccessFile rafOfSourceFile = null;
		Random random = new Random();
		long sumBytes = 0L;

		try {
			rafOfSourceFile = new RandomAccessFile(sourceFileObj, "rw");
		} catch (FileNotFoundException e) {
			String errorMessage = "파일 업다운 테스트용 원본 파일의 RandomAccessFile 객체 생성 실패";
			log.fatal(errorMessage, e);
			System.exit(1);
		}

		try {
			while (sumBytes < srcFileSize) {
				long gap = srcFileSize - sumBytes;

				byte bytes[] = null;
				if (gap < 1024L) {
					bytes = new byte[(int) gap];
					sumBytes += gap;
				} else {
					bytes = new byte[1024];
					sumBytes += 1024L;
				}

				random.nextBytes(bytes);
				try {
					rafOfSourceFile.write(bytes);
				} catch (IOException e) {
					String errorMessage = String.format(
							"sumBytes=[%d], bytes.length=[%d]", sumBytes,
							bytes.length);
					log.fatal(errorMessage, e);
					System.exit(1);
				}

			}
		} finally {
			if (rafOfSourceFile != null) {
				try {
					rafOfSourceFile.close();
				} catch (Exception e) {

				}
			}
		}

		return sourceFileObj;
	}

	/**
	 * 주어진 목적지 파일 객체로 부터 파일 경로, 파일명, 크기 정보를 추출하여 멤버 변수로 저장한다.
	 * 
	 * @param dstFileObj
	 *            원본 파일 객체
	 */
	public void saveTargetFileVariables(File dstFileObj) {
		testTargetFilePath = dstFileObj.getParentFile().getAbsolutePath();
		testTargetFileName = dstFileObj.getName();
		testTargetFileSize = dstFileObj.length();

		log.info(String
				.format("testTargetFilePath[%s], testTargetFileName[%s], testTargetFileSize=[%d]",
						testTargetFilePath, testTargetFileName,
						testTargetFileSize));
	}

	/**
	 * 지정된 크기를 갖는 파일 업로드/다운로드시 사용할 목적지 파일을 생성한다.
	 * 
	 * @param isRandom 파일 데이터 랜덤 생성 여부
	 * @param dstFileSize
	 *            목적지 파일 크기, 0보다 크거나 작다. 단위 byte.
	 *            
	 * @return 지정된 크기를 갖는 목적지 파일 객체
	 */
	public File createNewTargetFile(boolean isRandom, long dstFileSize) {
		if (dstFileSize < 0) {
			String errorMessage = String.format(
					"targetFileSize=[%d] is less than zero", dstFileSize);
			throw new IllegalArgumentException(errorMessage);
		}

		log.info(String.format("param targetFileSize[%d]", dstFileSize));

		File dstFileObj = null;

		try {
			dstFileObj = File.createTempFile("testFileUpDownDst", ".tmp");
		} catch (IOException e) {
			String errorMessage = "파일 업다운 테스트용 목적지 파일 생성 실패";
			log.fatal(errorMessage, e);
			System.exit(1);
		}
		
		if (dstFileSize > 0) {
			if (isRandom) {
				RandomAccessFile rafOfTargetFile = null;
				Random random = new Random();
				long sumBytes = 0L;

				try {
					rafOfTargetFile = new RandomAccessFile(dstFileObj, "rw");
				} catch (FileNotFoundException e) {
					String errorMessage = "파일 업다운 테스트용 목적지 파일의 RandomAccessFile 객체 생성 실패";
					log.fatal(errorMessage, e);
					System.exit(1);
				}

				try {
					while (sumBytes < dstFileSize) {
						long gap = dstFileSize - sumBytes;

						byte bytes[] = null;
						if (gap < 1024L) {
							bytes = new byte[(int) gap];
							sumBytes += gap;
						} else {
							bytes = new byte[1024];
							sumBytes += 1024L;
						}

						random.nextBytes(bytes);
						try {
							rafOfTargetFile.write(bytes);
						} catch (IOException e) {
							String errorMessage = String.format(
									"sumBytes=[%d], bytes.length=[%d]", sumBytes,
									bytes.length);
							log.fatal(errorMessage, e);
							System.exit(1);
						}

					}
				} finally {
					if (rafOfTargetFile != null) {
						try {
							rafOfTargetFile.close();
						} catch (Exception e) {

						}
					}
				}
			} else {
				truncateFile(dstFileObj, dstFileSize);
			}
		}

		return dstFileObj;

		/*
		 * testTargetFilePath = targetFileObj.getParentFile().getAbsolutePath();
		 * testTargetFileName = targetFileObj.getName(); testTargetFileSize =
		 * targetFileSize;
		 * 
		 * log.info(String.format(
		 * "testTargetFilePath[%s], testTargetFileName[%s], testTargetFileSize=[%d]"
		 * , testTargetFilePath, testTargetFileName, testTargetFileSize));
		 */
	}

	/**
	 * 지정된 길이만큼 파일을 자른다.
	 * @param fileObj 파일 객체
	 * @param fileSize 원하는 파일 크기
	 */
	public void truncateFile(File fileObj, long fileSize) {
		if (fileSize < 0) {
			String errorMessage = String.format(
					"fileSize=[%d] is less than zero", fileSize);
			throw new IllegalArgumentException(errorMessage);
		}

		RandomAccessFile rafOfDestinationFile = null;
		try {
			rafOfDestinationFile = new RandomAccessFile(fileObj, "rw");
		} catch (FileNotFoundException e) {
			String errorMessage = "원하는 파일 길이로 설정하기 위한 랜덤 파일 객체 생성 실패";
			log.fatal(errorMessage, e);
			System.exit(1);
		}

		try {
			rafOfDestinationFile.getChannel().truncate(fileSize);
		} catch (IOException e) {
			String errorMessage = "원하는 파일 길이로 설정 실패";
			log.fatal(errorMessage, e);
			System.exit(1);
		}
	}

	/*
	 * private String getFullPath(String filePath, String fileName) {
	 * StringBuilder sb = new StringBuilder(filePath);
	 * sb.append(File.separator); sb.append(fileName); return sb.toString(); }
	 */

	// getFullPath(testSourceFilePath, testSourceFileName)
	// getFullPath(testTargetFilePath, testTargetFileName)
	/**
	 * 파일 복사
	 * 
	 * @param srcFileObj
	 *            원본 파일 객체
	 * @param dstFileObj
	 *            목적지 파일 객체
	 * @throws Exception
	 *             복사 처리중 에러 발생시 던지는 예외
	 */
	public void copyFile(File srcFileObj, File dstFileObj) throws Exception {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(srcFileObj);
			fos = new FileOutputStream(dstFileObj);
			in = fis.getChannel();
			out = fos.getChannel();

			MappedByteBuffer m = in.map(FileChannel.MapMode.READ_ONLY, 0L,
					in.size());
			out.write(m);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {

				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {

				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 주어진 파일의 MD5 체크섬을 반환한다.
	 * 참고 주소 : http://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	 * @param fileObj MD5 체크섬을 구할 파일 객체
	 * @return 파일 데이터의 MD5 체크섬 결과
	 */
	public byte[] getMD5Checksum(File fileObj) {
		byte[] md5Bytes = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(fileObj);
			bis = new BufferedInputStream(fis);
			
			byte[] buffer = new byte[1024*4];
			MessageDigest md5Hash = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while (numRead != -1) {
				numRead = bis.read(buffer);
				if (numRead > 0) {
					md5Hash.update(buffer, 0, numRead);
				}
			}
			

			md5Bytes = md5Hash.digest();

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return md5Bytes;
	}
	
	/**
	 * 원본과 목적지 두 파일의 MD5 비교하여 리포팅 한다.
	 * @param title 리포트 제목 
	 * @param srcFileObj 원본 파일 객체
	 * @param dstFileObj 목적지 파일 객체
	 */
	public void showReportMD5(String title, File srcFileObj, File dstFileObj) {
		byte[] srcMD5Bytes = null;
		byte[] dstMD5Bytes = null;
		srcMD5Bytes = getMD5Checksum(srcFileObj);
		dstMD5Bytes = getMD5Checksum(dstFileObj);
		
		log.info(String.format("%s::파일 MD5 비교 결과 = %s", title, java.util.Arrays.equals(srcMD5Bytes, dstMD5Bytes)));
		log.info(String.format("%s::source file md5[%s], destination file md5[%s]", title, HexUtil.byteArrayAllToHex(srcMD5Bytes), HexUtil.byteArrayAllToHex(dstMD5Bytes)));
	}
	
	/**
	 * 원본과 목적지 MD5 바이트 배열를 비교하여 리포팅 한다.
	 * @param title 리포트 제목
	 * @param srcDstMD5Bytes 원본 MD5 바이트 배열
	 * @param dstDstMD5Bytes 목적지 MD5 바이트 배열
	 */
	public void showReportMD5(String title, byte[] srcMD5Bytes, byte[] dstMD5Bytes) {
		log.info(String.format("%s::MD5 비교 결과 = %s", title, java.util.Arrays.equals(srcMD5Bytes, dstMD5Bytes)));
		log.info(String.format("%s::source MD5[%s], target MD5[%s]", title, HexUtil.byteArrayAllToHex(srcMD5Bytes), HexUtil.byteArrayAllToHex(dstMD5Bytes)));
	}

	/**
	 * <pre>
	 * 지정된 송수신 버퍼 크기와 이어받기여부에 맞게 가상 파일 업로드를 수행한다. 
	 * 가상 파일 업로드는 파일 송수신을 위한 클라이언트와 서버간에 파일 업로드 행위를 가상적으로 구현한다.
	 * 파일 송수신이 원격 파일 복사 혹은 이어 붙이기 라면 가상 파일 업로드는 로컬 파일 복사 혹은 이어 붙이기이다.
	 * </pre>  
	 * @param dataBufferSize 지정된 송수신 버퍼 크기
	 * @param append 이어받기 여부, 참이면 목적지 파일에 이어 받고 거짓이면 목적지 파일을 덮어 쓴다.
	 */
	public void doVirtualUpload(int dataBufferSize, boolean append) {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = null;

		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager
				.getInstance();

		LocalTargetFileResource localTargetFileResource = null;

		long startTime = 0L;
		long endTime = 0L;

		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(append, testSourceFilePath,
							testSourceFileName, testSourceFileSize,
							testTargetFilePath, testTargetFileName,
							testTargetFileSize, dataBufferSize);

			localTargetFileResource = localTargetFileResourceManager
					.pollLocalTargetFileResource(append, testSourceFilePath,
							testSourceFileName, testSourceFileSize,
							testTargetFilePath, testTargetFileName,
							testTargetFileSize, dataBufferSize);

			startTime = System.currentTimeMillis();

			int startFileBlockNo = 0;
			int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();

			for (; startFileBlockNo <= endFileBlockNo; startFileBlockNo++) {
				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(startFileBlockNo);
				localSourceFileResource.readSourceFileData(startFileBlockNo,
						fileData, true);

				localTargetFileResource.writeTargetFileData(startFileBlockNo,
						fileData, true);
			}

		} catch (IllegalArgumentException e) {
			log.fatal(String.format("target file[%s%s%s], %s",
					testTargetFilePath, File.separator, testTargetFileName,
					e.getMessage()), e);
			System.exit(1);
		} catch (UpDownFileException e) {
			log.fatal(String.format("target file[%s%s%s], %s",
					testTargetFilePath, File.separator, testTargetFileName,
					e.getMessage()), e);
			System.exit(1);
		} finally {
			endTime = System.currentTimeMillis();
			log.info(String.format("elapsed=[%d ms]", (endTime - startTime)));

			localSourceFileResourceManager
					.putLocalSourceFileResource(localSourceFileResource);
			localTargetFileResourceManager
					.putLocalTargetFileResource(localTargetFileResource);
		}
	}

	public void testReadFileSpeed(int dataBufferSize) {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = null;

		long startTime = 0L;
		long endTime = 0L;
		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(false, testSourceFilePath,
							testSourceFileName, testSourceFileSize,
							testTargetFilePath, testTargetFileName,
							testTargetFileSize, dataBufferSize);

			startTime = System.currentTimeMillis();

			int startFileBlockNo = 0;
			int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();

			for (; startFileBlockNo <= endFileBlockNo; startFileBlockNo++) {
				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(startFileBlockNo);
				localSourceFileResource.readSourceFileData(startFileBlockNo,
						fileData, true);
			}

		} catch (IllegalArgumentException e) {
			log.fatal(String.format("source file[%s%s%s], %s",
					testSourceFilePath, File.separator, testSourceFileName,
					e.getMessage()), e);
			System.exit(1);
		} catch (UpDownFileException e) {
			log.fatal(String.format("source file[%s%s%s], %s",
					testSourceFilePath, File.separator, testSourceFileName,
					e.getMessage()), e);
			System.exit(1);
		} finally {
			endTime = System.currentTimeMillis();
			log.info(String.format("elapsed=[%d ms]", (endTime - startTime)));

			localSourceFileResourceManager
					.putLocalSourceFileResource(localSourceFileResource);
		}
	}

	public void testWriteFileSpeed(int dataBufferSize) {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = null;

		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager
				.getInstance();

		LocalTargetFileResource localTargetFileResource = null;
		long startTime = 0L;
		long endTime = 0L;

		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(false, testSourceFilePath,
							testSourceFileName, testSourceFileSize,
							testTargetFilePath, testTargetFileName,
							testTargetFileSize, dataBufferSize);

			localTargetFileResource = localTargetFileResourceManager
					.pollLocalTargetFileResource(false, testSourceFilePath,
							testSourceFileName, testSourceFileSize,
							testTargetFilePath, testTargetFileName,
							testTargetFileSize, dataBufferSize);

			startTime = System.currentTimeMillis();

			int startFileBlockNo = 0;
			int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();

			for (; startFileBlockNo <= endFileBlockNo; startFileBlockNo++) {
				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(startFileBlockNo);
				localSourceFileResource.readSourceFileData(startFileBlockNo,
						fileData, true);

				localTargetFileResource.writeTargetFileData(startFileBlockNo,
						fileData, true);
			}

		} catch (IllegalArgumentException e) {
			log.fatal(String.format("target file[%s%s%s], %s",
					testTargetFilePath, File.separator, testTargetFileName,
					e.getMessage()), e);
			System.exit(1);
		} catch (UpDownFileException e) {
			log.fatal(String.format("target file[%s%s%s], %s",
					testTargetFilePath, File.separator, testTargetFileName,
					e.getMessage()), e);
			System.exit(1);
		} finally {
			endTime = System.currentTimeMillis();
			log.info(String.format("elapsed=[%d ms]", (endTime - startTime)));

			localSourceFileResourceManager
					.putLocalSourceFileResource(localSourceFileResource);
			localTargetFileResourceManager
					.putLocalTargetFileResource(localTargetFileResource);
		}
	}

}
