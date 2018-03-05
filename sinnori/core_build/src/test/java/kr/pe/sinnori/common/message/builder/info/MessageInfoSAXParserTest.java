package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.AbstractJunitTest;

public class MessageInfoSAXParserTest extends AbstractJunitTest {
	MessageInfoSAXParser messageInfoSAXParser = null;	

	@Before
	public void setup() {
		super.setup();
		
		/**
		 * Warning 로케일 설정 생략하지 말것. xml 파싱시 xsl에서 정의한 규칙에 어긋난 경우 로케일 설정에 따라 메시지를
		 * 보여주기때문에, 상황에 맞는 메시지가 나왔는지 점검을 위해서 기준이 되는 로케일로 영문을 선택하였다.
		 */
		Locale enLocale = new Locale("en-US");
		Locale.setDefault(enLocale);

		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		
	}

	/**
	 * 메시지 정보 파싱 테스트를 위해 존재하는 경로에 있는 지정한 테스트 대상 메시지 정보 파일명 포함 전체 경로를 반환한다.
	 * [신놀이설치경로]/tmp/[테스트 대상짧은 파일명]
	 * 
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @param shortFileName
	 *            테스트 대상 짧은 파일명
	 * @return 테스트 대상 파일명 포함 전체 경로명
	 */
	private String getFilePathStringForJunitTestFile(String shortFileName) {
		String testDataXmlFilePathString = new StringBuilder(sinnoriInstalledPathString)
				.append(File.separator)
				.append("core_build")
				.append(File.separator)
				.append("src")
				.append(File.separator)
				.append("test")
				.append(File.separator)
				.append("resources")
				.append(File.separator)
				.append("message_info_xml_testdata")
				.append(File.separator)
				.append(shortFileName).toString();
	
		return testDataXmlFilePathString;
	}

	@Ignore
	@Test(expected = SAXException.class)
	public void testConstructor() throws Exception {
		/** TODO how to meet bug? I don't know. help me! */
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_NullParameter_messageInformationXMLFilePathString() {
		String expectedMessage = "the parameter messageInformationXMLFilePathString is null";
		String xmlFilePathString = null;
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (expectedMessage.equals(errorMessage))
				return;
		}
		fail(new StringBuilder("'").append(expectedMessage)
				.append("' test failed").toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_EmptyString_messageInformationXMLFilePathString() {
		String expectedMessage = "the parameter messageInformationXMLFilePathString is a empty string";
		String xmlFilePathString = "";
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (expectedMessage.equals(errorMessage))
				return;
		}
		fail(new StringBuilder("'").append(expectedMessage)
				.append("' test failed").toString());
	}

	
	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_길이가작은경우() {
		String testTitle = "메시지 정보 파일명(메시지식별자+.xml) 길이가 작은 경우";
		String xmlFilePathString = ".xml";
		String expectedMessage = String
				.format("the parameter messageInformationXMLFilePathString[%s]'s length[%d] is too small, its length must be greater than %d",
						xmlFilePathString, xmlFilePathString.length(), MessageInfoSAXParser.XML_EXTENSION_SUFFIX.length());
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}
	
	
	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_확장자가xml이아닌경우() {
		String testTitle = "메시지 정보 파일명 길이가 4보다 커야 한다는 조건은 만족하지만 확장자가 xml 이 아닌 경우";
		String xmlFilePathString = "abcde";
		String expectedMessage = String
				.format("the parameter messageInformationXMLFilePathString[%s]'s suffix is not '.xml'",
						xmlFilePathString);
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}


	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_xmlFilePathString_잘못된메시지식별자를파일명으로가지는xml확장자파일명() {
		String testTitle = "잘못된 메시지 식별자를 파일명으로 가지는 xml 확장자 파일명";
		String messageID = "a";
		String xmlFilePathString = messageID + ".xml";
		String expectedMessage = String
				.format("the parameter messageInformationXMLFilePathString[%s] has a invalid message id[%s], (note) the message information XML file name format is '<messageID>.xml'",
						xmlFilePathString, messageID);
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_부모경로를갖지만메시지식별자가없고xml확장자를갖는파일명() {
		String testTitle = "부모 경로를 갖지만 메시지 식별자가 없고 xml 확장자를 갖는 파일명";
		String xmlFilePathString = File.separator + ".xml";
		String expectedMessage = String
				.format("fail to get message id from the parameter messageInformationXMLFilePathString[%s] that is '<messageID>.xml' format file name",
						xmlFilePathString);
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString_ValidButBadParameter_messageInformationXMLFilePathString_xml확장자를갖지않는파일명() {
		String testTitle = "xml 확장자를 갖지 않는 파일명";
		String xmlFilePathString = "a.xml2";
		String expectedMessage = String.format(
				"the parameter messageInformationXMLFilePathString[%s]'s suffix is not '.xml'",
				xmlFilePathString);
		try {
			messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testGetMessageIDFromXMLFilePathString__ExpectedValueComparison() {
		String xmlFilePathString = "Ab.xml";
		String expectedValue = "Ab";
		String returnedValue = null;
		try {
			returnedValue = messageInfoSAXParser
					.getMessageIDFromXMLFilePathString(xmlFilePathString);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			fail(e.getMessage());
		}

		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}

	@Test
	public void testParse_ok() {
		String testTitle = "정상적인 경우";

		File xmlFile = new File(getFilePathStringForJunitTestFile("AllDataType.xml"));
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException | SAXException | IOException e) {
			log.warn(e.toString(), e);
			fail(new StringBuilder("'").append(testTitle)
					.append("' test failed").toString());
		}
	}

	@Test
	public void testParse_XSLRuleError_파일크기0인파일() {
		String testTitle = "파일 크기 0인 파일";
		File xmlFile = new File(getFilePathStringForJunitTestFile("Zero.xml"));
		String expectedMessage = "Premature end of file.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);
			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_신놀이메시지ROOT태그가아닌것을ROOT태그로사용() {
		String testTitle = "신놀이 메시지 ROOT 태그가 아닌것을 ROOT 태그로 사용";
		File xmlFile = new File(getFilePathStringForJunitTestFile("BadRootElement.xml"));
		String expectedMessage = "Cannot find the declaration of element 'array'";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_messageID태그가없는경우() {
		String testTitle = "messageID 태그가 없는 경우";
		File xmlFile = new File(getFilePathStringForJunitTestFile("NoMessageIDTag.xml"));
		String expectedMessage = "One of '{messageID}' is expected.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_단일항목_이름속성생략() {
		String testTitle = "단일 항목 이름 속성 생략";
		File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNoNameAttribute.xml"));
		String expectedMessage = "Attribute 'name' must appear on element 'singleitem'.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.indexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_단일항목_타입속성생략() {
		String testTitle = "단일 항목 타입 속성 생략";
		File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNoTypeAttribute.xml"));
		String expectedMessage = "Attribute 'type' must appear on element 'singleitem'.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.indexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_단일항목_2번중복되는이름속성() {
		String testTitle = "단일 항목 2번 중복되는 이름 속성";
		File xmlFile = new File(
				getFilePathStringForJunitTestFile("SingleItemDoubleNameAttribute.xml"));
		String expectedMessage = "Attribute \"name\" was already specified for element \"singleitem\".";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_단일항목_2번중복되는디폴트값속성() {
		String testTitle = "단일 항목 2번 중복되는 디폴트값 속성";
		File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemDoubleDefaultValueAttribute.xml"));
		String expectedMessage = "Attribute \"defaultValue\" was already specified for element \"singleitem\".";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_배열_이름속성생략() {
		String testTitle = "배열 이름 속성 생략";
		File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoNameAttribute.xml"));
		String expectedMessage = "Attribute 'name' must appear on element 'array'.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_배열_반복횟수타입속성생략() {
		String testTitle = "배열 '반복 횟수 타입' 속성 생략";
		File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoCntTypeAttribute.xml"));
		String expectedMessage = "Attribute 'cnttype' must appear on element 'array'.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_XSLRuleError_배열_반복횟수값속성생략() {
		String testTitle = "배열 '반복 횟수 값' 속성 생략";
		File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNoCntValueAttribute.xml"));
		String expectedMessage = "Attribute 'cntvalue' must appear on element 'array'.";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_메시지아이디파일명과messageID태그의값불일치() {
		String testTitle = "메시지 아이디 파일명과 messageID 태그의 값 불일치";
		String messageIDTagValue = "IAmNotBadMessageID";
		String messageIDOfFileName = "BadMessageID";
		File xmlFile = new File(getFilePathStringForJunitTestFile(messageIDOfFileName + ".xml"));
		String expectedMessage = String
				.format("The tag \"messageid\"'s value[%s] is different from message id[%s] of '<message id>.xml' format file[%s]",
						messageIDTagValue, messageIDOfFileName,
						xmlFile.getAbsolutePath());
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage))
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_잘못된통신방향성() {
		String testTitle = "잘못된 통신 방향성";
		File xmlFile = new File(getFilePathStringForJunitTestFile("BadDirection.xml"));
		String expectedMessage = "is not a member of direction set[FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL]";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf(expectedMessage) > 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_단일항목_중복항목() {
		String testTitle = "단일 항목-중복";
		File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNameDuplication.xml"));
		String duplicationTagName = "itemID";
		String expectedMessage = "this single item name[" + duplicationTagName
				+ "] was duplicated";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.indexOf(expectedMessage) == 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_단일항목_숫자형_디폴트값문자() {
		String testTitle = "단일 항목-숫자형-디폴트값 문자";
		File xmlFile = new File(getFilePathStringForJunitTestFile("SingleItemNumberTypeBadDefaultValue.xml"));
		String expectedMessage = "fail to parses the string argument(=this 'integer' type single item[itemCnt]'s attribute 'defaultValue' value[ab]) as a signed decimal integer";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.startsWith(expectedMessage))
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_배열항목_중복() {
		String testTitle = "배열 항목-중복";
		File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemNameDuplication.xml"));
		String duplicationTagName = "byteVar1";
		String expectedMessage = "this array item name[" + duplicationTagName
				+ "] was duplicated";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.indexOf(expectedMessage) == 0)
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_xsl만족하지만잘못된문서_배열항목_크기직접입력방식에서문자인크기() {
		String testTitle = "배열 항목-크기 직접 입력 방식에서 문자인 크기";
		File xmlFile = new File(getFilePathStringForJunitTestFile("ArrayItemDirectBadSize.xml"));
		String expectedMessage = "fail to parses the string argument(=this array item[item]'s attribute 'cntvalue' value[hello]) as a signed decimal integer";
		try {
			MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile, true);

			log.info(messageInfo.toString());
		} catch (IllegalArgumentException e) {
			log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
					+ "], errormessage=" + e.getMessage(), e);
		} catch (SAXException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.startsWith(expectedMessage))
				return;
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		fail(new StringBuilder("'").append(testTitle).append("' test failed")
				.toString());
	}

	@Test
	public void testParse_재사용하여반복사용가능한지여부판단을위한여러번호출() {
		String testTitle = "중복 항목";
		String subTestTitle = null;

		File xmlFile = null;
		String expectedMessage = null;

		while (true) {
			subTestTitle = "메시지 아이디 파일명과 messageID 태그의 값 불일치";
			String messageIDTagValue = "IAmNotBadMessageID";
			String messageIDOfFileName = "BadMessageID";
			xmlFile = new File(getFilePathStringForJunitTestFile(messageIDOfFileName + ".xml"));
			expectedMessage = String
					.format("The tag \"messageid\"'s value[%s] is different from message id[%s] of '<message id>.xml' format file[%s]",
							messageIDTagValue, messageIDOfFileName,
							xmlFile.getAbsolutePath());

			try {
				MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile,
						true);

				log.info(messageInfo.toString());
			} catch (IllegalArgumentException e) {
				log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
						+ "], errormessage=" + e.getMessage(), e);
			} catch (SAXException e) {
				log.warn(e.toString(), e);
				String errorMessage = e.getMessage();
				if (errorMessage.indexOf(expectedMessage) == 0)
					break;
			} catch (IOException e) {
				log.warn(e.toString(), e);
			}
			fail(new StringBuilder("'").append(testTitle).append("-")
					.append(subTestTitle).append("' test failed").toString());
		}

		while (true) {
			subTestTitle = "첫번째 정상";
			xmlFile = new File(getFilePathStringForJunitTestFile("AllDataType.xml"));
			try {
				MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile,
						true);

				log.info(messageInfo.toString());
				break;
			} catch (IllegalArgumentException | SAXException | IOException e) {
				log.warn(e.toString(), e);
				fail(new StringBuilder("'").append(testTitle).append("-")
						.append(subTestTitle).append("' test failed")
						.toString());
			}
		}

		while (true) {
			subTestTitle = "신놀이 메시지 ROOT 태그가 아닌것을 ROOT 태그로 사용";
			xmlFile = new File(getFilePathStringForJunitTestFile("BadRootElement.xml"));
			expectedMessage = "Cannot find the declaration of element 'array'";
			try {
				MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile,
						true);

				log.info(messageInfo.toString());
			} catch (IllegalArgumentException e) {
				log.warn("fail to parse xml file[" + xmlFile.getAbsolutePath()
						+ "], errormessage=" + e.getMessage(), e);
			} catch (SAXException e) {
				log.warn(e.toString(), e);
				String errorMessage = e.getMessage();
				if (errorMessage.lastIndexOf(expectedMessage) > 0)
					break;
			} catch (IOException e) {
				log.warn(e.toString(), e);
			}
			fail(new StringBuilder("'").append(testTitle).append("-")
					.append(subTestTitle).append("' test failed").toString());
		}
		while (true) {
			subTestTitle = "두번째 정상";
			xmlFile = new File(getFilePathStringForJunitTestFile("Echo.xml"));
			try {
				MessageInfo messageInfo = messageInfoSAXParser.parse(xmlFile,
						true);

				log.info(messageInfo.toString());
				break;
			} catch (IllegalArgumentException | SAXException | IOException e) {
				log.warn(e.toString(), e);
				fail(new StringBuilder("'").append(testTitle).append("-")
						.append(subTestTitle).append("' test failed")
						.toString());
			}
		}
	}
}
