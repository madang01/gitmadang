package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.type.SingleItemType;

public class SingleItemTypeMangerTest extends AbstractJunitSupporter {
	
	
	@Test
	public void testGetSingleItemType_sigleItemID를통해얻은SigleItemType맞는지검사() {
		for (SingleItemType expectedSingleItemType : SingleItemType.values()) {
			SingleItemType actualSingleItemType = SingleItemTypeManger.getInstance().getSingleItemType(expectedSingleItemType.getItemTypeID());
			
			assertEquals("sigleItemID 를 통해 얻은 SigleItemType 맞는지 검사", expectedSingleItemType, actualSingleItemType);
		}
	}
	
	
	@Test
	public void test() {
		SingleItemTypeManger singleItemTypeManger = SingleItemTypeManger.getInstance();
	
		log.info("Sinnori message information xsl file={}", singleItemTypeManger.getMessageXSLStr());
	}
}
