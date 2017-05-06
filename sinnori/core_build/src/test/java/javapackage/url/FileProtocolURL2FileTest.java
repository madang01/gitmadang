package javapackage.url;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProtocolURL2FileTest {
	@Test
	public void fileProcolURL2FileTest_파일프로토콜_URL_host주소부가들어가있는경우() {
		// Logger log = LoggerFactory.getLogger(FileProtocolURL2FileTest.class);
		
		URL urlOfMapper = null;
		try {
			urlOfMapper = new URL("file://localhost/D:/temp.properties");
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}
		
		//log.info("success URL");
		
		URI uriOfMapper = null;
		try {
			uriOfMapper = urlOfMapper.toURI();
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		// log.info("success URI");
		
		@SuppressWarnings("unused")
		File resourceFile = null;
		
		try {
			resourceFile = new File(uriOfMapper);
		} catch(Exception e) {
			if (!e.getMessage().equals("URI has an authority component")) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void fileProcolURL2FileTest_success() {
		Logger log = LoggerFactory.getLogger(FileProtocolURL2FileTest.class);
		
		URL urlOfMapper = null;
		try {
			urlOfMapper = new URL("file:///D:/temp.properties");
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}
		
		//log.info("success URL");
		
		URI uriOfMapper = null;
		try {
			uriOfMapper = urlOfMapper.toURI();
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//log.info("success URI");
		
		File resourceFile = null;
		
		try {
			resourceFile = new File(uriOfMapper);
		} catch(Exception e) {
			log.warn("11111", e);
			fail(e.getMessage());
		}
		
		if (! resourceFile.exists()) {
			fail("the file converted url whose protocol is file doesn't exist");
		}
		
		if (! resourceFile.isFile()) {
			fail("the file converted url whose protocol is file is not a regular file");
		}
	}
}