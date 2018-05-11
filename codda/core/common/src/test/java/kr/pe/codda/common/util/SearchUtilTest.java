package kr.pe.codda.common.util;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;



public class SearchUtilTest extends AbstractJunitTest {
	
	/*@Test
	public void testCharByteSize() {
		ByteBuffer test01 = ByteBuffer.allocate(20);
		log.info("test01 pos={}", test01.position());
		test01.putChar('a');
		log.info("test01 pos={}", test01.position());
		test01.putChar('똠');
		log.info("test01 pos={}", test01.position());
		
		// char ch = new StringBuilder("\uD800\uDF30").charAt(0);
		// char ch2 = '\u10330';
	
		final int codePoint = 0x10330; // for instance
		final char[] toChars = Character.toChars(codePoint);
		log.info("codePoint[{}] chars length={}", codePoint, toChars.length);
	}*/
	
	@Test
	public void testFindKeywordInFile_theParameterSoruceFileIsNull() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedErrorMessage = "the parameter soruceFile is null";
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterSoruceFileDoesNotExist() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		soruceFile = new File("a12fiopz.temp");				
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter soruceFile[%s] doesn't exist", soruceFile.getAbsolutePath());
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterSoruceFileIsNotNormalFile() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		soruceFile = new File("a12fiopz.dir");
		
		boolean isSuccess = soruceFile.mkdir();
		
		if (!isSuccess) {
			fail("fail to create new directory for test");
		}
		
		soruceFile.deleteOnExit();
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter soruceFile[%s] isn't a normal file", soruceFile.getAbsolutePath());
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
				
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterSearchKeywordIsNull() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(20);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put("tt".getBytes());
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}
		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = "the parameter searchKeyword is null";
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterSearchKeywordLengthIsZero() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		searchKeyword = new byte[0];
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(20);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put("tt".getBytes());
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}
		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = "the parameter searchKeyword's length is zero";
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testFindKeywordInFile_theParameterBufferSize_lessThanMin() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		bufferSize = SearchUtil.MIN_BUFFER_SIZE - 1;
		searchKeyword = "hello".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		/*ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(20);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put(searchKeyword);*/
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(searchKeyword);
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}
		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter bufferSize[%d] must be greater than or equals to %d", bufferSize, SearchUtil.MIN_BUFFER_SIZE);
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterBufferSize_greaterThanOrEqualsTotTheParameterSearchKeywordLength() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = new byte[SearchUtil.MIN_BUFFER_SIZE+1];
		/*ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(searchKeyword.length);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put(searchKeyword);*/
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(searchKeyword);
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter bufferSize[%d] must be greater than or equals to the parameter searchKeyword's length[%d]", 
					bufferSize, searchKeyword.length);
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	
	@Test
	public void testFindKeywordInFile_theParameterSleepTimeWhenTheNumberOfBytesReadIsZero_lessThanOrEqualsToZero() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(20);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put(searchKeyword);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be greater than zero", 
					sleepTimeWhenTheNumberOfBytesReadIsZero);
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testFindKeywordInFile_theParameterSleepTimeWhenTheNumberOfBytesReadIsZero_greaterThanMax() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO+1;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(20);
		tempFileContentsByteBuffer.position(0);
		tempFileContentsByteBuffer.put(searchKeyword);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		try {
			SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testFindKeywordInFile_headExpectedIndex() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		long expectedIndex = 0;
		
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(bufferSize*2);
		tempFileContentsByteBuffer.position((int)expectedIndex);
		tempFileContentsByteBuffer.put(searchKeyword);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		
		long returnedIndex = -1;
		try {
			returnedIndex = SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				log.warn(errorMessage, e);
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
		
		if (returnedIndex != expectedIndex) {
			fail(String.format("expected index[%d] but returned index[%d]", expectedIndex, returnedIndex));
		}
	}
	
	@Test
	public void testFindKeywordInFile_middleExpectedIndex() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		
		long expectedIndex = bufferSize - searchKeyword.length+1;
		
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(bufferSize*2);		
		tempFileContentsByteBuffer.position((int)expectedIndex);
		tempFileContentsByteBuffer.put(searchKeyword);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		
		long returnedIndex = -1;
		try {
			returnedIndex = SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
		
		if (returnedIndex != expectedIndex) {
			fail(String.format("expected index[%d] but returned index[%d]", expectedIndex, returnedIndex));
		}
	}
	
	@Test
	public void testFindKeywordInFile_lastExpectedIndex() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		
		int tempFileSize = bufferSize*2;
		long expectedIndex = tempFileSize - searchKeyword.length;
		
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(tempFileSize);		
		tempFileContentsByteBuffer.position((int)expectedIndex);
		tempFileContentsByteBuffer.put(searchKeyword);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		
		long returnedIndex = -1;
		try {
			returnedIndex = SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
		
		if (returnedIndex != expectedIndex) {
			fail(String.format("expected index[%d] but returned index[%d]", expectedIndex, returnedIndex));
		}
	}
	
	@Test
	public void testFindKeywordInFile_notFound() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		
		int tempFileSize = bufferSize*2;
		long expectedIndex = -1;
		
		ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(tempFileSize);		
		// tempFileContentsByteBuffer.position((int)tempFileSize - searchKeyword.length+1);
		// tempFileContentsByteBuffer.position(searchKeyword.length/2);
		tempFileContentsByteBuffer.position(bufferSize - searchKeyword.length+searchKeyword.length/2);
		tempFileContentsByteBuffer.put(searchKeyword, 0, searchKeyword.length - 1);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(tempFileContentsByteBuffer.array());
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		
		long returnedIndex = -1;
		try {
			returnedIndex = SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
		
		if (returnedIndex != expectedIndex) {
			fail(String.format("expected index[%d] but returned index[%d]", expectedIndex, returnedIndex));
		}
	}
	
	@Test
	public void testFindKeywordInFile_fileSizeLessThanSearchKeywordLength() {
		File soruceFile = null;
		byte[] searchKeyword = null;
		
		int bufferSize=0;
		long sleepTimeWhenTheNumberOfBytesReadIsZero=0;
		
		
		sleepTimeWhenTheNumberOfBytesReadIsZero = SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO;
		bufferSize = SearchUtil.MIN_BUFFER_SIZE;
		searchKeyword = "helloWorld".getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);
		
		// int tempFileSize = 1;
		long expectedIndex = -1;
		
		// ByteBuffer tempFileContentsByteBuffer = ByteBuffer.allocate(tempFileSize);		
		// tempFileContentsByteBuffer.position((int)tempFileSize - searchKeyword.length+1);
		// tempFileContentsByteBuffer.position(searchKeyword.length/2);
		// tempFileContentsByteBuffer.position(bufferSize - searchKeyword.length+searchKeyword.length/2);
		// tempFileContentsByteBuffer.put(searchKeyword, 0, searchKeyword.length - 1);
		
		FileOutputStream tempFileOutputStream = null;
		try {
			soruceFile = File.createTempFile("temp", ".tmp");
			soruceFile.deleteOnExit();
			tempFileOutputStream = new FileOutputStream(soruceFile);
			tempFileOutputStream.write(0xaa);
		} catch (IOException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("fail to create new temp file");
		} finally {
			if (null != tempFileOutputStream) {
				try {
					tempFileOutputStream.close();
				} catch (IOException e) {
					String errorMessage = e.getMessage();			
					log.warn(errorMessage, e);
					fail("fail to close the new temp file output stream");
				}
			}
		}		
		
		
		long returnedIndex = -1;
		try {
			returnedIndex = SearchUtil.findKeywordInFile(soruceFile, searchKeyword, bufferSize, sleepTimeWhenTheNumberOfBytesReadIsZero);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			
			String expectedErrorMessage = String.format("the parameter sleepTimeWhenTheNumberOfBytesReadIsZero[%d] must be less than or equals to %d", 
					sleepTimeWhenTheNumberOfBytesReadIsZero, 
					SearchUtil.MAX_SLEEP_TIME_WHEN_THE_NUMBER_OF_BYTES_READ_IS_ZERO);
			if (! expectedErrorMessage.equals(errorMessage)) {
				fail(String.format("expected error message[%s] but unexpected error message[%s]", expectedErrorMessage, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();			
			log.warn(errorMessage, e);
			fail("unknown error");
		}
		
		if (returnedIndex != expectedIndex) {
			fail(String.format("expected index[%d] but returned index[%d]", expectedIndex, returnedIndex));
		}
	}
	
}
