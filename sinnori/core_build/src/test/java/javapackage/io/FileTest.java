package javapackage.io;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class FileTest {
	private InternalLogger log = InternalLoggerFactory.getInstance(FileTest.class);
	
	
	/**
	 * Warning! first create a file. the file's user is not admin 
	 * 			and the file has a read permission and a read deny permission.
	 *          second do test using a non admin user account.
	 * Info) java cann't change  a read deny permission. a read permission can be changed by java at a few OS.
	 * @throws Exception
	 */
	@Test(expected=FileNotFoundException.class)
	public void testCanRead_Win7_UserWhoIsNotAdmin_ReadingYesAndReadingDenyYesFile() throws Exception {
		String osName = System.getProperty("os.name");
		if (!osName.startsWith("Windows ")) {
			log.warn("This Test Window7 32bit Home Premium K, if a other OS[{}] I can't ensure this test succession", osName);
		}		
		
		File ReadingYesAndReadingDenyYesFile = new File("d:\\t1.txt");
		
		if (!ReadingYesAndReadingDenyYesFile.exists()) {
			fail("file not exist");
		}
		
		if (!ReadingYesAndReadingDenyYesFile.canRead()) {
			String errorMessage = String.format("this file[%s] doesn't hava permission to read", 
					ReadingYesAndReadingDenyYesFile.getAbsolutePath());
			fail(errorMessage);
		}
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(ReadingYesAndReadingDenyYesFile);
		} catch (FileNotFoundException e) {
			/**
			 * 예외 발생 사유 : d:\t1.txt (액세스가 거부되었습니다)
			 */
			throw e;
		}  finally {
			if (null != fis) {
				try {
					fis.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void testSetReadable() {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("test", "");
		} catch (IOException e1) {
			fail("fail to create a temp file, errormessage=" + e1.getMessage());
		}

		log.info("temp file=[{}]", tempFile.getAbsolutePath());
		
		boolean isSuccess = tempFile.setReadable(false);
		if (!isSuccess) {
			/**
			 * In Window7 32bit Home Premium K a readable permission can't be set false.
			 * Maybe I guess this function depend on OS.
			 */
			fail("fail to set readable[false]");
		}
	}

}
