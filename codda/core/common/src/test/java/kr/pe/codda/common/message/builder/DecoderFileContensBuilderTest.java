package kr.pe.codda.common.message.builder;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.message.builder.info.ArrayInfo;
import kr.pe.codda.common.message.builder.info.GroupInfo;
import kr.pe.codda.common.message.builder.info.OrderedItemSet;
import kr.pe.codda.common.message.builder.info.SingleItemInfo;
import kr.pe.codda.common.type.SingleItemType;

public class DecoderFileContensBuilderTest extends AbstractJunitTest {
		
		@Test
		public void testAddSingleItemInfoPart() {
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
			
			StringBuilder contetnsStringBuilder = new StringBuilder();
			decoderFileContensBuilder.addSingleItemInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, singleItemInfo);
			
			log.info(contetnsStringBuilder.toString());
		}
		
		@Test
		public void testAddArraySizeVarDeclarationPart() {
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
			
			StringBuilder contetnsStringBuilder = new StringBuilder();
			decoderFileContensBuilder.addArraySizeVarDeclarationPart(contetnsStringBuilder, depth, varNameOfSetOwner, arrayInfo);
			
			log.info(contetnsStringBuilder.toString());
		}
		
		
		@Test
		public void testAddArrayInfoPart() {
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
			
			StringBuilder contetnsStringBuilder = new StringBuilder();
			
			decoderFileContensBuilder.addArrayInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, arrayInfo);
			
			log.info(contetnsStringBuilder.toString());
		}
		
		@Test
		public void testAddGroupInfoPart() {
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
			
			StringBuilder contetnsStringBuilder = new StringBuilder();
			decoderFileContensBuilder.addGroupInfoPart(contetnsStringBuilder, depth, path, varNameOfSetOwner, middleObjVarName, groupInfo);
			
			log.info(contetnsStringBuilder.toString());
		}
}
