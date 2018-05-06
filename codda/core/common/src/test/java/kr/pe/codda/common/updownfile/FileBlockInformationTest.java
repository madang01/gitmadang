package kr.pe.codda.common.updownfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class FileBlockInformationTest {

	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeIsZero() {
		try {
			FileTransferInformation.getFileBlockCountUsingBigDecimal(0, 10);
			
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			if (! e.getMessage().equals("the parameter fileSize is less than or equal to zero")) {
				fail("not expected message");
			}
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeLessThanZero() {
		try {
			FileTransferInformation.getFileBlockCountUsingBigDecimal(-10, 10);
			
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			if (! e.getMessage().equals("the parameter fileSize is less than or equal to zero")) {
				fail("not expected message");
			}
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileBlockSizeIsZero() {
		try {
			FileTransferInformation.getFileBlockCountUsingBigDecimal(10, 0);
			
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			if (! e.getMessage().equals("the parameter fileBlockSize is less than or equal to zero")) {
				fail("not expected message");
			}
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileBlockSizeLessThanZero() {
		try {
			FileTransferInformation.getFileBlockCountUsingBigDecimal(10, -20);
			
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			if (! e.getMessage().equals("the parameter fileBlockSize is less than or equal to zero")) {
				fail("not expected message");
			}
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeLessThanFileBlock() {
		long fileBlockCount =  -1;
		long expectedFileBlockCount =  1;
		try {
			fileBlockCount = FileTransferInformation.getFileBlockCountUsingBigDecimal(10, 21);			
			
			assertEquals("the expected value comparison", fileBlockCount, expectedFileBlockCount);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeGreaterThanFileBlock() {
		long fileBlockCount =  -1;
		long expectedFileBlockCount =  2;
		try {
			fileBlockCount = FileTransferInformation.getFileBlockCountUsingBigDecimal(42, 21);			
			
			assertEquals("the expected value comparison", fileBlockCount, expectedFileBlockCount);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeIsMax() {
		long fileBlockCount =  -1;
		long expectedFileBlockCount =  Long.MAX_VALUE;
		try {
			fileBlockCount = FileTransferInformation.getFileBlockCountUsingBigDecimal(Long.MAX_VALUE, 1);			
			
			assertEquals("the expected value comparison", fileBlockCount, expectedFileBlockCount);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
}
