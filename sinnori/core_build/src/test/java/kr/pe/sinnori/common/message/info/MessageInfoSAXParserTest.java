package kr.pe.sinnori.common.message.info;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class MessageInfoSAXParserTest {
	private Logger log = LoggerFactory
			.getLogger(MessageInfoSAXParserTest.class);
	
	MessageInfoSAXParser messageInfoSAXParser = null;

	@Before
	public void setup() {
		SinnoriLogbackManger.getInstance().setup();
		
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Ignore
	@Test(expected=MessageInfoSAXParserException.class)
	public void testConstructor() throws Exception {
		/** TODO how to meet bug? I don't know. help me! */
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_NullParameter_xmlFilePathString() {
		String xmlFilePathString = null;
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("the parameter xmlFilePathString is null", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_EmptyString_xmlFilePathString() {
		String xmlFilePathString = "";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("the parameter xmlFilePathString is a empty string", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_xml확장자보다작은경우() {
		String xmlFilePathString = "a";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("the parameter xmlFilePathString[a]'s length is less than length of string '.xml'", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_xml확장자와같은경우() {
		String xmlFilePathString = ".xml";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("the parameter xmlFilePathString[.xml]'s length is equal to length of string '.xml'", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_잘못된메시지식별자를파일명으로가지는xml확장자파일명() {
		String xmlFilePathString = "a.xml";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("잘못된 메시지 식별자를 파일명으로 가지는 xml 확장자 파일명["+xmlFilePathString+"]", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_부모경로를가지지만메시지식별자가없는파일명으로가지는xml파일명() {
		String xmlFilePathString = File.separator+".xml";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("부모 경로를 가지지만 메시지 식별자가 없는 xml 확장자를 갖는 파일명["+xmlFilePathString+"]", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_xml확장자를갖지않는파일명() {
		String xmlFilePathString = "a.xml2";
		try {
			messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("xml 확장자를 갖지 않는 파일명["+xmlFilePathString+"]", e);
			throw e;
		}
	}
	
	@Test
	public void testGetMessageIDFromXMLFilePathString__ExpectedValueComparison() {
		String xmlFilePathString = "Ab.xml";
		String expectedValue = "Ab";
		String returnedValue = null;
		try {
			returnedValue = messageInfoSAXParser.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			fail(e.getMessage());
		}
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testParse() {
		File xmlFile = new File("D:\\gitsinnori\\sinnori\\project\\sample_base\\impl\\message\\info\\AllDataType.xml");
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);
			
			log.info(messageInfo.toString());
		} catch (IllegalArgumentException | SAXException | IOException e) {
			e.printStackTrace();
			fail(""+e.getMessage());
		}
	}
}
