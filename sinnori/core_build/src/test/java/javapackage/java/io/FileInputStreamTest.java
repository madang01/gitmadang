package javapackage.java.io;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class FileInputStreamTest extends AbstractJunitSupporter {
	
	
	@Test
	public void testClose_닫은후동작을보기위한테스트() {
		File tempFile = null;
		String prefix = "Test";
		String suffix = ".temp";
		try {
			tempFile = File.createTempFile(prefix, suffix);
			
			
			
		} catch (IOException e) {
			fail("can't create tempfile becase of io error::"+e.getMessage());
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(tempFile);
			fis.close();
			
			
		} catch (FileNotFoundException e) {
			fail("temp file not found error::"+e.getMessage());
		} catch (IOException e) {
			fail("fail to close temp file::"+e.getMessage());
		}
		
		
		try {
			fis.read();
		} catch (IOException e) {
			/** result :: java.io.IOException: Stream Closed */
			log.warn("IOException", e);
		}
		
		
	}
}
