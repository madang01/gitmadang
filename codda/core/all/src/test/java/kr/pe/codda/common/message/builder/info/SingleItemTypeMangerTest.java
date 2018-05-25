package kr.pe.codda.common.message.builder.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.type.SingleItemType;

public class SingleItemTypeMangerTest extends AbstractJunitTest {
	
	
	@Test
	public void testGetSingleItemType_sigleItemID를통해얻은SigleItemType맞는지검사() {
		for (SingleItemType expectedSingleItemType : SingleItemType.values()) {
			SingleItemType actualSingleItemType = SingleItemTypeManger.getInstance().getSingleItemType(expectedSingleItemType.getItemTypeID());
			
			assertEquals("sigleItemID 를 통해 얻은 SigleItemType 맞는지 검사", expectedSingleItemType, actualSingleItemType);
		}
	}
	
	
	@Test
	public void test() {
		File messageInfoFile = new File("D:\\gitmadang\\codda\\project\\sample_base\\resources\\message_info\\BoardDetailReq.xml");
		
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = e.toString();
			log.warn(errorMessage, e);
			fail("fail to create a instance of MessageInfoSAXParser class");
		}
		MessageInfo messageInfo = null;
		try {
			messageInfo = messageInfoSAXParser.parse(messageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = e.toString();
			log.warn(errorMessage, e);
			fail("fail to parse the message information file");
		}
		
		
	}
	
	@Test
	public void testGetMessageXSLStr() {
		SingleItemTypeManger singleItemTypeManger = SingleItemTypeManger.getInstance();
		
		log.info("the message information xsl file={}", singleItemTypeManger.getMessageXSLStr());
	}
	
	
}
