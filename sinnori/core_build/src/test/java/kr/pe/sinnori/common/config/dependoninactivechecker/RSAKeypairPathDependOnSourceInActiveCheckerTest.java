package kr.pe.sinnori.common.config.dependoninactivechecker;

import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

import kr.pe.sinnori.common.config.AbstractDependOnInactiveChecker;
import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.ItemID;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningEmptyOrNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningPath;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.ConfigErrorException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAKeypairPathDependOnSourceInActiveCheckerTest {
	Logger log = LoggerFactory
			.getLogger(RSAKeypairPathDependOnSourceInActiveCheckerTest.class);
	
	
	private AbstractDependOnInactiveChecker inactiveChecker = null;
	
	@Before
	public void setup() {
		SinnoriLogbackManger.getInstance().setup();
		
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";	
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;
		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentSourceItemIDInfo() throws Exception {
		//String projectName = "sample_test";
		//String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		//String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";		
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;
		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		/*try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}*/
		
		String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter dependentSourceItemIDInfo is null", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentTargetItemIDInfo() throws Exception {	
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		// String dependentTargetItemID = ItemID.CommonPart.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;		
		String dependentSourceItemID = ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID;		
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;		
		/*try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}*/
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter dependentTargetItemIDInfo is null", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_inactiveStrings() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";	
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		// String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		String[] inactiveStrings = null;
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter inactiveStrings is null", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_EmptyParameter_inactiveStrings() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";		
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		// String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		String[] inactiveStrings = new String[0];
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("this paramter inactiveChecker is a zero size string array", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_dependentSourceItemIDInfo_NotFileType() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";		
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"테스트를 위한 RSAKeypairPath가아닌 문자열 타입 항목 식별자 정보",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningEmptyOrNoTrimString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter dependentSourceItemIDInfo's nativeValueConverter generic type is not java.io.File", e);
			throw e;
		}
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_dependentTargetItemIDInfo_NotAbstractSetTypeNativeValueConverter() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";	
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<Charset> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = false;
			dependentTargetItemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					BuildSystemPathSupporter.getAPPINFPathString(
							projectName,
							sinnoriInstalledPathString),
							isDefaultValueCheck,
					new GeneralConverterReturningCharset());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = {CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter dependentTargetItemIDInfo's nativeValueConverter class is not inherited by AbstractSetTypeNativeValueConverter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_dependentTargetItemIDInfo_NotRSAKeypairPathDependOnSourceInActiveChecker() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";	
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<Integer> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = false;
			dependentTargetItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					"10",
							isDefaultValueCheck,
					new SetTypeConverterReturningInteger("10", "20", "30"));
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = {"10"};
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter dependentTargetItemIDInfo's nativeValueConverter class is not a SetTypeConverterOfSessionKeyRSAKeypairSource", e);
			throw e;
		}
	}	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_inactiveStrings() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		
		String dependentTargetItemID = "sessionkey.rsa_keypair_source.value";		
		String dependentSourceItemID = "sessionkey.rsa_keypair_path.value";		
		boolean isDefaultValueCheck = false;
		
		ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> dependentTargetItemIDInfo = null;		
		try {
			isDefaultValueCheck = true;
			dependentTargetItemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					dependentTargetItemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isDefaultValueCheck = false;
		ItemIDInfo<File> dependentSourceItemIDInfo = null;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					dependentSourceItemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					BuildSystemPathSupporter.getSessionKeyRSAKeypairPathString(
							projectName,
							sinnoriInstalledPathString),
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		String[] inactiveStrings = { "API", "not elements" };
		
		try {
			inactiveChecker = 
					new RSAKeypairPathDependOnSourceInActiveChecker(dependentSourceItemIDInfo,
							dependentTargetItemIDInfo, inactiveStrings);
		} catch(IllegalArgumentException e) {
			log.info("the parameter inactiveStrings's one more elements are bad", e);
			throw e;
		}
	}
	
	@Test
	public void testIsInactive_ExpectedValueComparison() {
		boolean expectedValue = true;
		boolean returnedValue = false;
		
		String prefixOfItemID = "";
		Properties sinnoriConfigFileProperties = new Properties();		
		
		sinnoriConfigFileProperties.put(prefixOfItemID+inactiveChecker.getDependentTargetItemID(), CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString());
		sinnoriConfigFileProperties.put(prefixOfItemID+inactiveChecker.getDependentSourceItemID(), ".");
		
		String depenedentTargetItemValue = sinnoriConfigFileProperties.getProperty(prefixOfItemID+inactiveChecker.getDependentTargetItemID());		
		expectedValue = (depenedentTargetItemValue.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API.toString()));
		returnedValue = inactiveChecker.isInactive(sinnoriConfigFileProperties, prefixOfItemID);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
}
