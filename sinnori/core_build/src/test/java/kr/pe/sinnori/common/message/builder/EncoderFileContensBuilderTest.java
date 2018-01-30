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

public class EncoderFileContensBuilderTest {
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
	public void testBuildStringOfSingleItemInfoPart() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String path = "AllItemTypeReq";
		String varNameOfSetOwner = "allItemTypeReq";
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
		String varNameOfSetOwner = "allItemTypeReq";		
		
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
	
	@Test
	public void testBuildStringOfPartCheckingListSizeIsValid() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String varNameOfSetOwner = "allItemTypeReq";		
		
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
	
	@Test
	public void testBuildStringOfPartWhoseListIsNotNullAtArray() {
		EncoderFileContensBuilder encoderFileContensBuilder = new EncoderFileContensBuilder();
		
		int depth = 1;
		String path = "AllItemTypeReq";
		String varNameOfSetOwner = "allItemTypeReq";
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
		String path = "AllItemTypeReq";
		String varNameOfSetOwner = "allItemTypeReq";
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
		String path = "AllItemTypeReq";
		String varNameOfSetOwner = "allItemTypeReq";
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
