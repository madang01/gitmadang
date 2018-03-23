package kr.pe.sinnori.common.message.builder;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.type.SingleItemType;

public class DecoderFileContensBuilderTest extends AbstractJunitTest {
		
		@Test
		public void testBuildStringOfSingleItemInfoPart() {
			DecoderFileContensBuilder decoderFileContensBuilder = new DecoderFileContensBuilder();
			
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
			
			String result = decoderFileContensBuilder.buildStringOfSingleItemInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo);
			
			log.info(result);
		}
		
		@Test
		public void testBuildStringOfArraySizeVarDeclarationPart() {
			DecoderFileContensBuilder decoderFileContensBuilder = new DecoderFileContensBuilder();
			
			int depth = 1;
			String varNameOfSetOwner = "empty";	
			
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
			
			String result = decoderFileContensBuilder.buildStringOfArraySizeVarDeclarationPart(depth, varNameOfSetOwner, arrayInfo);
			
			log.info(result);
		}
		
		
		@Test
		public void testBuildStringOfArrayInfoPart() {
			DecoderFileContensBuilder decoderFileContensBuilder = new DecoderFileContensBuilder();
			
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
			
			String result = decoderFileContensBuilder.buildStringOfArrayInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, arrayInfo);
			
			log.info(result);
		}
		
		@Test
		public void testBuildStringOfGroupInfoPart() {
			DecoderFileContensBuilder decoderFileContensBuilder = new DecoderFileContensBuilder();
			
			int depth = 1;
			String path = "Empty";
			String varNameOfSetOwner = "empty";
			String middleObjVarName = "middleWritableObject";		
			
			String groupName = "vip";

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
			
			String result = decoderFileContensBuilder.buildStringOfGroupInfoPart(depth, path, varNameOfSetOwner, middleObjVarName, groupInfo);
			
			log.info(result);
		}
}
