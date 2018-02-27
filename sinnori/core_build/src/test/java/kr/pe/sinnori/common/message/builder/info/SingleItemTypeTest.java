package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.type.SingleItemType;

public class SingleItemTypeTest extends AbstractJunitSupporter {
	
	@Test
	public void test_ItemTypeID가정말로키가맞는지그리고0부터순차적으로할당되었는지에대한테스트() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		int[] arrayOfSingleItemTypeID = new int[singleItemTypes.length];
		Arrays.fill(arrayOfSingleItemTypeID, -1);
		for (SingleItemType singleItemType : singleItemTypes) {
			int singleItemTypeID = singleItemType.getItemTypeID();
			try {
				arrayOfSingleItemTypeID[singleItemTypeID]=singleItemTypeID;
			} catch(IndexOutOfBoundsException e) {
				fail(String.format("singleItemType[%s] is bad, singleItemTypeID[%d] is out of the range[0 ~ %d]", singleItemType.toString(), singleItemTypeID, singleItemTypes.length-1));
			}
		}
		for (int i=0; i < arrayOfSingleItemTypeID.length; i++) {
			int singleItemTypeID = arrayOfSingleItemTypeID[i];
			if (-1 == singleItemTypeID) {
				fail(String.format("the singleItemTypeID[%d] is not found", i));
			}
		}
	}
}
