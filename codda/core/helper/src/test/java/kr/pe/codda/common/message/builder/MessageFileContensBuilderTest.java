package kr.pe.codda.common.message.builder;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.message.builder.info.ArrayInfo;
import kr.pe.codda.common.message.builder.info.GroupInfo;
import kr.pe.codda.common.message.builder.info.OrderedItemSet;
import kr.pe.codda.common.message.builder.info.SingleItemInfo;
import kr.pe.codda.common.type.SingleItemType;

public class MessageFileContensBuilderTest extends AbstractJunitTest {
	

	@Test
	public void testAddVariableDeclarationPartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = "12";
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
			.addVariableDeclarationPartForSingleItemInfo(contentsStringBuilder, 1,singleItemInfo);

			log.info("SingleItemInfo::variableDelarationString=[{}]", contentsStringBuilder.toString());
		}

		{
			String itemName = "ubBytes";
			String itemTypeName = SingleItemType.UB_VARIABLE_LENGTH_BYTES.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
			.addVariableDeclarationPartForSingleItemInfo(contentsStringBuilder, 1,singleItemInfo);

			log.info("SingleItemInfo::variableDelarationString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddVariableDeclarationPartForArrayInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addVariableDeclarationPartForArrayInfo(contentsStringBuilder, 0, arrayInfo);

			log.info("ArrayInfo::variableDelarationString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddVariableDeclarationPartForGroupInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder.addVariableDeclarationPartForGroupInfo(contentsStringBuilder, 0,
					groupInfo);

			log.info("GroupInfo::variableDelarationString=[{}]", contentsStringBuilder.toString());
		}

	}

	@Test
	public void testAddGetMethodDefinePartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addGetMethodDefinePartForSingleItemInfo(contentsStringBuilder, 0, singleItemInfo);

			log.info("SingleItem::getMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddGetMethodDefinePartForArrayInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addGetMethodDefinePartForArrayInfo(contentsStringBuilder, 0, arrayInfo);

			log.info("ArrayInfo::getMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddGetMethodDefinePartForGroupInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addGetMethodDefinePartForGroupInfo(contentsStringBuilder, 0, groupInfo);

			log.info("GroupInfo::getMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}

	////////////////
	@Test
	public void testAddSetMethodDefinePartForSingleItemInfo() {
		MessageFileContensBuilder messageFileContensBuilder = new MessageFileContensBuilder();

		{
			String itemName = "byte1";
			String itemTypeName = SingleItemType.BYTE.getItemTypeName();
			String nativeItemDefaultValue = null;
			String nativeItemSize = null;
			String nativeItemCharset = null;

			SingleItemInfo singleItemInfo = new SingleItemInfo(itemName, itemTypeName, nativeItemDefaultValue,
					nativeItemSize, nativeItemCharset);

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addSetMethodDefinePartForSingleItemInfo(contentsStringBuilder, 0, singleItemInfo);

			log.info("SingleItemInfo::setMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddSetMethodDefinePartForArrayInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addSetMethodDefinePartForArrayInfo(contentsStringBuilder, 0, arrayInfo);

			log.info("ArrayInfo::setMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}

	@Test
	public void testAddSetMethodDefinePartForGroupInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addSetMethodDefinePartForGroupInfo(contentsStringBuilder, 0, groupInfo);

			log.info("GroupInfo::setMethodDefinePartString=[{}]", contentsStringBuilder.toString());
		}
	}
	
	@Test
	public void testAddToStringPartForSingleItemInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addToStringPartForSingleItemInfo(contentsStringBuilder, 0, isFirstElement, singleItemInfo);

			log.info("SingleItemInfo::toStringPartString=[{}]", contentsStringBuilder.toString());
		}
	}
	
	@Test
	public void testAddToStringPartForArrayInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addToStringPartForArrayInfo(contentsStringBuilder, 0, isFirstElement, arrayInfo);

			log.info("ArrayInfo::toStringPartString=[{}]", contentsStringBuilder.toString());
		}
	}
	
	@Test
	public void testAddToStringPartForGroupInfo() {
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

			StringBuilder contentsStringBuilder = new StringBuilder();
			messageFileContensBuilder
					.addToStringPartForGroupInfo(contentsStringBuilder, 0, isFirstElement, groupInfo);

			log.info("GroupInfo::toStringPartString=[{}]", contentsStringBuilder.toString());
		}
	}

}
