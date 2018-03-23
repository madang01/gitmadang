package kr.pe.sinnori.common.message.builder;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.type.SingleItemType;

public class EncoderFileContensBuilderTest extends AbstractJunitTest {	

	@Test
	public void testBuildStringOfSingleItemInfoPart() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String path = "Empty";
		String varNameOfSetOwner = "empty";
		String middleObjVarName = "middleWritableObject";		
		
		String itemName = "ubBytes";
		String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
		String nativeItemDefaultValue = null;
		String nativeItemSize = null;
		String nativeItemCharset = null;

		SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
				nativeItemSize, nativeItemCharset);
		
		String result = encoderFileContensBuilder.buildStringOfSingleItemInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo);
		
		log.info(result);
	}
	
	@Test
	public void testBuildStringOfPartWhoseListIsNullAtArray() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String varNameOfSetOwner = "empty";		
		
		{
			log.info("test case::direct");
			
			String arrayName = "member";
			String arrayCntType = "direct";
			String arrayCntValue = "3";

			ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
			OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				arrayItemSet.addItemInfo(singleItemInfo);
			}
			
			String result = encoderFileContensBuilder.buildStringOfPartWhoseListIsNullAtArray(depth, varNameOfSetOwner, arrayInfo);
			
			log.info(result);
		}
		
		{
			log.info("test case::reference");
			
			String arrayName = "member";
			String arrayCntType = "reference";
			String arrayCntValue = "cnt";

			ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
			OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				arrayItemSet.addItemInfo(singleItemInfo);
			}
			
			String result = encoderFileContensBuilder.buildStringOfPartWhoseListIsNullAtArray(depth, varNameOfSetOwner, arrayInfo);
			
			log.info(result);
		}
	}
	
	@Test
	public void testBuildStringOfPartCheckingListSizeIsValid() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String varNameOfSetOwner = "empty";		
		
		{
			log.info("test case:direct");
			
			String arrayName = "member";
			String arrayCntType = "direct";
			String arrayCntValue = "3";

			ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
			OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				arrayItemSet.addItemInfo(singleItemInfo);
			}
			
			String result = encoderFileContensBuilder.buildStringOfPartCheckingListSizeIsValid(depth, varNameOfSetOwner, arrayInfo);
			
			log.info(result);
			
		}
		
		{
			log.info("test case:case:direct");
			
			String arrayName = "member";
			String arrayCntType = "reference";
			String arrayCntValue = "cnt";

			ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
			OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				arrayItemSet.addItemInfo(singleItemInfo);
			}
			
			String result = encoderFileContensBuilder.buildStringOfPartCheckingListSizeIsValid(depth, varNameOfSetOwner, arrayInfo);
			
			log.info(result);
			
		}
		
	}
	
	@Test
	public void testBuildStringOfPartWhoseListIsNotNullAtArray() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String path = "Empty";
		String varNameOfSetOwner = "empty";
		String middleObjVarName = "middleWritableObject";
		
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "3";

		ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
		OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
		{
			String itemName = "ubBytes";
			String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);
			arrayItemSet.addItemInfo(singleItemInfo);
		}
		
		String result = encoderFileContensBuilder.buildStringOfPartWhoseListIsNotNullAtArray(depth
				, path
				, varNameOfSetOwner
				, middleObjVarName
				, arrayInfo);
		
		log.info(result);
	}
	
	@Test
	public void testBuildStringOfArrayInfoPart() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 2;
		String path = "Empty";
		String varNameOfSetOwner = "empty";
		String middleObjVarName = "middleWritableObject";
		
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "3";

		ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType, arrayCntValue);
		OrderedItemSet arrayItemSet = arrayInfo.getOrderedItemSet();
		{
			String itemName = "ubBytes";
			String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);
			arrayItemSet.addItemInfo(singleItemInfo);
		}
		
		String result = encoderFileContensBuilder.buildStringOfArrayInfoPart(depth
				, path
				, varNameOfSetOwner
				, middleObjVarName
				, arrayInfo);
		
		log.info(result);
	}
	
	@Test
	public void testBuildStringOfGroupInfoPart() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 2;
		String path = "Empty";
		String varNameOfSetOwner = "empty";
		String middleObjVarName = "middleWritableObject";
		

		String groupName = "group1";

		GroupInfo groupInfo = new GroupInfo(groupName);
		OrderedItemSet groupOrderedItemSet = groupInfo.getOrderedItemSet();
		{
			String itemName = "ubBytes3";
			String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);
			groupOrderedItemSet.addItemInfo(singleItemInfo);
		}
		
		String result = encoderFileContensBuilder.buildStringOfGroupInfoPart(depth
				, path
				, varNameOfSetOwner
				, middleObjVarName
				, groupInfo);
		
		log.info(result);
	}
	
	
}
