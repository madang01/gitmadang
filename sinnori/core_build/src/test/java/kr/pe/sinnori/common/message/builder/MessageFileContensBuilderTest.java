package kr.pe.sinnori.common.message.builder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;
import kr.pe.sinnori.common.message.builder.info.GroupInfo;
import kr.pe.sinnori.common.message.builder.info.OrderedItemSet;
import kr.pe.sinnori.common.message.builder.info.SingleItemInfo;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;

public class MessageFileContensBuilderTest {
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString,
		// mainProjectName, logType);

		log = LoggerFactory.getLogger(MessageFileContensBuilderTest.class);
	}

	@After
	public void finish() {
		System.gc();
	}

	@Test
	public void testBuildStringOfVariableDeclarationPart_SingleItem() {
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
					.buildStringOfVariableDeclarationPart(0,singleItemInfo);

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

			String variableDelarationString = messageFileContensBuilder.buildStringOfVariableDeclarationPart(0,
					singleItemInfo);

			log.info("SingleItemInfo::variableDelarationString=[{}]", variableDelarationString);
		}
	}

	@Test
	public void testBuildStringOfVariableDeclarationPart_ArrayInfo() {
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

			String variableDelarationString = messageFileContensBuilder.buildStringOfVariableDeclarationPart(0,
					arrayInfo);

			log.info("ArrayInfo::variableDelarationString=[{}]", variableDelarationString);
		}
	}

	@Test
	public void testBuildStringOfVariableDeclarationPart_GroupInfo() {
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

			String variableDelarationString = messageFileContensBuilder.buildStringOfVariableDeclarationPart(0,
					groupInfo);

			log.info("GroupInfo::variableDelarationString=[{}]", variableDelarationString);
		}

	}

	@Test
	public void testBuildStringOfGetMethodDefinePart_SingleItem() {
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
					.buildStringOfGetMethodDefinePart(0, singleItemInfo);

			log.info("SingleItem::getMethodDefinePartString=[{}]", getMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfGetMethodDefinePart_ArrayInfo() {
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
					.buildStringOfGetMethodDefinePart(0, arrayInfo);

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
					.buildStringOfGetMethodDefinePart(0, groupInfo);

			log.info("GroupInfo::getMethodDefinePartString=[{}]", getMethodDefinePartString);
		}
	}

	////////////////
	@Test
	public void testBuildStringOfSetMethodDefinePart_SingleItem() {
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
					.buildStringOfSetMethodDefinePart(0, singleItemInfo);

			log.info("SingleItemInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfSetMethodDefinePart_ArrayInfo() {
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
					.buildStringOfSetMethodDefinePart(0, arrayInfo);

			log.info("ArrayInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}

	@Test
	public void testBuildStringOfSetMethodDefinePart_GroupInfo() {
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
					.buildStringOfSetMethodDefinePart(0, groupInfo);

			log.info("GroupInfo::setMethodDefinePartString=[{}]", setMethodDefinePartString);
		}
	}
	
	@Test
	public void testBuildStringOfToStringPart_SingleItemInfo() {
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
					.buildStringOfToStringPart(0, isFirstElement, singleItemInfo);

			log.info("SingleItemInfo::toStringPartString=[{}]", toStringPartString);
		}
	}
	
	@Test
	public void testBuildStringOfToStringPart_ArrayInfo() {
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
					.buildStringOfToStringPart(0, isFirstElement, arrayInfo);

			log.info("SingleItemInfo::toStringPartString=[{}]", toStringPartString);
		}
	}

}
