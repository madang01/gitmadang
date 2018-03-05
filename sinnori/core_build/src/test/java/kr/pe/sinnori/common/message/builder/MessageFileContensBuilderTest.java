package kr.pe.sinnori.common.message.builder;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.type.SingleItemType;

public class MessageFileContensBuilderTest extends AbstractJunitTest {
	

	@Test
	public void testBuildStringOfVariableDeclarationPartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = "12";
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			String variableDelarationString = messageFileContensBuilder
					.buildStringOfVariableDeclarationPartForSingleItemInfo(1,singleItemInfo);

			log.info("SingleItemInfo::variableDelarationString=[{}]", variableDelarationString);
		}

		{
			String itemName = "ubBytes";
			String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			String variableDelarationString = messageFileContensBuilder
					.buildStringOfVariableDeclarationPartForSingleItemInfo(1, singleItemInfo);

			log.info("SingleItemInfo::variableDelarationString=[{}]", variableDelarationString);
		}
	}

	@Test
	public void testBuildStringOfVariableDeclarationPartForArrayInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();
		{
			String arrayName = "array1";
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

			String variableDelarationString = messageFileContensBuilder
					.buildStringOfVariableDeclarationPartForArrayInfo(0, arrayInfo);

			log.info("ArrayInfo::variableDelarationString=[{}]", variableDelarationString);
		}
	}

	@Test
	public void testBuildStringOfVariableDeclarationPartForGroupInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String groupName = "group1";

			GroupInfo groupInfo = new GroupInfo(groupName);
			OrderedItemSet groupItemSet = groupInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes3";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				groupItemSet.addItemInfo(singleItemInfo);
			}

			String variableDelarationString = messageFileContensBuilder.buildStringOfVariableDeclarationPartForGroupInfo(0,
					groupInfo);

			log.info("GroupInfo::variableDelarationString=[{}]", variableDelarationString);
		}

	}

	@Test
	public void testBuildStringOfGetMethodDefinePartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			String getMethodDefinePartString = messageFileContensBuilder
					.buildStringOfGetMethodDefinePartForSingleItemInfo(0, singleItemInfo);

			log.info("SingleItem::getMethodDefinePartString=[{}]", getMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfGetMethodDefinePartForArrayInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String arrayName = "array1";
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

			String getMethodDefinePartString = messageFileContensBuilder
					.buildStringOfGetMethodDefinePartForArrayInfo(0, arrayInfo);

			log.info("ArrayInfo::getMethodDefinePartString=[{}]", getMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfGetMethodDefinePart_GroupInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String groupName = "group1";

			GroupInfo groupInfo = new GroupInfo(groupName);
			OrderedItemSet groupItemSet = groupInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes3";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				groupItemSet.addItemInfo(singleItemInfo);
			}

			String getMethodDefinePartString = messageFileContensBuilder
					.buildStringOfGetMethodDefinePartForGroupInfo(0, groupInfo);

			log.info("GroupInfo::getMethodDefinePartString=[{}]", getMethodDefinePartString);
		}
	}

	////////////////
	@Test
	public void testBuildStringOfSetMethodDefinePartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			String setMethodDefinePartString = messageFileContensBuilder
					.buildStringOfSetMethodDefinePartForSingleItemInfo(0, singleItemInfo);

			log.info("SingleItemInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfSetMethodDefinePartForArrayInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String arrayName = "array1";
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

			String setMethodDefinePartString = messageFileContensBuilder
					.buildStringOfSetMethodDefinePartForArrayInfo(0, arrayInfo);

			log.info("ArrayInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfSetMethodDefinePartForGroupInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String groupName = "group1";

			GroupInfo groupInfo = new GroupInfo(groupName);
			OrderedItemSet groupItemSet = groupInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes3";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				groupItemSet.addItemInfo(singleItemInfo);
			}

			String setMethodDefinePartString = messageFileContensBuilder
					.buildStringOfSetMethodDefinePartForGroupInfo(0, groupInfo);

			log.info("GroupInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}
	
	@Test
	public void testBuildStringOfToStringPartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		
		boolean[] isFirstElements = {true, false};
		for (boolean isFirstElement : isFirstElements) {
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			String toStringPartString = messageFileContensBuilder
					.buildStringOfToStringPartForSingleItemInfo(0, isFirstElement, singleItemInfo);

			log.info("SingleItemInfo::toStringPartString=[{}]", toStringPartString);
		}
	}
	
	@Test
	public void testBuildStringOfToStringPartForArrayInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		
		boolean[] isFirstElements = {true, false};
		for (boolean isFirstElement : isFirstElements) {
			String arrayName = "array1";
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

			String toStringPartString = messageFileContensBuilder
					.buildStringOfToStringPartForArrayInfo(0, isFirstElement, arrayInfo);

			log.info("ArrayInfo::toStringPartString=[{}]", toStringPartString);
		}
	}
	
	@Test
	public void testBuildStringOfToStringPartForGroupInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		
		boolean[] isFirstElements = {true, false};
		for (boolean isFirstElement : isFirstElements) {
			String groupName = "group1";

			GroupInfo groupInfo = new GroupInfo(groupName);
			OrderedItemSet groupItemSet = groupInfo.getOrderedItemSet();
			{
				String itemName = "ubBytes3";
				String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
				String nativeItemDefaultValue = null;
				String nativeItemSize = null;
				String nativeItemCharset = null;

				SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
						nativeItemSize, nativeItemCharset);
				groupItemSet.addItemInfo(singleItemInfo);
			}

			String toStringPartString = messageFileContensBuilder
					.buildStringOfToStringPartForGroupInfo(0, isFirstElement, groupInfo);

			log.info("GroupInfo::toStringPartString=[{}]", toStringPartString);
		}
	}

}
