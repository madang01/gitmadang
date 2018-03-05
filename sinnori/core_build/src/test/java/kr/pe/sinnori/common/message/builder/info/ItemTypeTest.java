package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.type.SingleItemType;

public class ItemTypeTest extends AbstractJunitTest {
	
	

	@Test
	public void test_ordinal와itemTypeID같은지검사() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", singleItemType.ordinal(), singleItemType.getItemTypeID());
	}
	
	
	@Test
	public void testGetItemTypeID_ItemTypeID를순차적으로잘정의했는지검사() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType singleItemType = singleItemTypes[i];
			assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", i, singleItemType.getItemTypeID());
		}
	}
	
	
	@Test
	public void test() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		log.info("singleItemType.name={}", singleItemType.name());
	}
}
