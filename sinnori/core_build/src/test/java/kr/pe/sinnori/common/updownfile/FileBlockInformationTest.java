package kr.pe.sinnori.common.updownfile;

import static org.junit.Assert.fail;

import org.junit.Test;

public class FileBlockInformationTest {

	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeIsZero() {
		try {
			FileBlockInformation.getFileBlockCountUsingBigDecimal(0, 10);
			
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
			FileBlockInformation.getFileBlockCountUsingBigDecimal(-10, 10);
			
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
			FileBlockInformation.getFileBlockCountUsingBigDecimal(10, 0);
			
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
			FileBlockInformation.getFileBlockCountUsingBigDecimal(10, -20);
			
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
			fileBlockCount = FileBlockInformation.getFileBlockCountUsingBigDecimal(10, 21);			
			
			org.junit.Assert.assertThat("the expected value comparison",
					fileBlockCount, org.hamcrest.CoreMatchers.equalTo(expectedFileBlockCount));
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeGreaterThanFileBlock() {
		long fileBlockCount =  -1;
		long expectedFileBlockCount =  2;
		try {
			fileBlockCount = FileBlockInformation.getFileBlockCountUsingBigDecimal(42, 21);			
			
			org.junit.Assert.assertThat("the expected value comparison",
					fileBlockCount, org.hamcrest.CoreMatchers.equalTo(expectedFileBlockCount));
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetFileBlockCountUsingBigDecimal_parameter_fileSizeIsMax() {
		long fileBlockCount =  -1;
		long expectedFileBlockCount =  Long.MAX_VALUE;
		try {
			fileBlockCount = FileBlockInformation.getFileBlockCountUsingBigDecimal(Long.MAX_VALUE, 1);			
			
			org.junit.Assert.assertThat("the expected value comparison",
					fileBlockCount, org.hamcrest.CoreMatchers.equalTo(expectedFileBlockCount));
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
}
