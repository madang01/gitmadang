package kr.pe.sinnori.common.config.dependonvalidchecker;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import kr.pe.sinnori.common.config.AbstractDependOnValidChecker;
import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningEmptyOrNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningPath;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.sinnori.common.exception.ConfigErrorException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidCheckerTest {
	Logger log = LoggerFactory
			.getLogger(MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidCheckerTest.class);
	
	
	private AbstractDependOnValidChecker validChecker = null;
	
	@Before
	public void setup() {		
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		boolean isWritePermissionChecking = false;
		
		ItemIDInfo<File> dependentTargetItemIDInfo = null;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		
		String dependentTargetItemID = "server.classloader.appinf.path.value";
		String dependentSourceItemID = "server.classloader.mybatis_config_file_relative_path.value";
		
		isWritePermissionChecking = false;
		try {
			dependentTargetItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					BuildSystemPathSupporter.getAPPINFPathString(
							projectName,
							sinnoriInstalledPathString),
					isWritePermissionChecking,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isWritePermissionChecking = true;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"ClassLoader#getResourceAsStream 의 구현 이며 \n<APP-INF>/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다.\nex) kr/pe/sinnori/impl/mybatis/mybatisConfig.xml",
					"", isWritePermissionChecking,
					new GeneralConverterReturningEmptyOrNoTrimString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		try {
			validChecker = new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentSourceConfigItem() throws Exception {
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		boolean isWritePermissionChecking = false;
		
		ItemIDInfo<File> dependentTargetItemIDInfo = null;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		
		String dependentTargetItemID = "server.classloader.appinf.path.value";
		@SuppressWarnings("unused")
		String dependentSourceItemID = "server.classloader.mybatis_config_file_relative_path.value";
		
		isWritePermissionChecking = false;
		try {
			dependentTargetItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					BuildSystemPathSupporter.getAPPINFPathString(
							projectName,
							sinnoriInstalledPathString),
					isWritePermissionChecking,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
				
		try {
			validChecker = new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		} catch (IllegalArgumentException e) {
			log.info("the parameter dependentSourceConfigItem is null, errormessage={}", e.getMessage());
			throw e;
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentTargetItemIDInfo() throws Exception {
		
		boolean isWritePermissionChecking = false;
		
		ItemIDInfo<File> dependentTargetItemIDInfo = null;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		
		@SuppressWarnings("unused")
		String dependentTargetItemID = "server.classloader.appinf.path.value";

		String dependentSourceItemID = "server.classloader.mybatis_config_file_relative_path.value";
		
		isWritePermissionChecking = true;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"ClassLoader#getResourceAsStream 의 구현 이며 \n<APP-INF>/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다.\nex) kr/pe/sinnori/impl/mybatis/mybatisConfig.xml",
					"", isWritePermissionChecking,
					new GeneralConverterReturningEmptyOrNoTrimString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		try {
			validChecker = new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		} catch (IllegalArgumentException e) {
			log.info("the parameter dependentTargetItemIDInfo is null, errormessage={}", e.getMessage());
			throw e;
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_dependentSourceItemIDInfo() throws Exception {		
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		boolean isWritePermissionChecking = false;
		
		ItemIDInfo<File> dependentTargetItemIDInfo = null;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		
		String dependentTargetItemID = "server.classloader.appinf.path.value";
		String dependentSourceItemID = "server.classloader.mybatis_config_file_relative_path.value";
		
		isWritePermissionChecking = false;
		try {
			dependentTargetItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					BuildSystemPathSupporter.getAPPINFPathString(
							projectName,
							sinnoriInstalledPathString),
					isWritePermissionChecking,
					new GeneralConverterReturningPath());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isWritePermissionChecking = true;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"ClassLoader#getResourceAsStream 의 구현 이며 \n<APP-INF>/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다.\nex) kr/pe/sinnori/impl/mybatis/mybatisConfig.xml",
					"notEmptyNeeds", isWritePermissionChecking,
					new GeneralConverterReturningNoTrimString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		try {
			validChecker = new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		} catch (IllegalArgumentException e) {
			log.info("the parameter dependentSourceItemIDInfo's itemValueConverter type is not GeneralConverterReturningEmptyOrNoTrimString, errormessage={}", e.getMessage());
			throw e;
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_ValidButBadParameter_dependentTargetItemIDInfo() throws Exception {		
		String projectName = "sample_test";
		String sinnoriInstalledPathString = ".";
		boolean isWritePermissionChecking = false;
		
		ItemIDInfo<File> dependentTargetItemIDInfo = null;
		ItemIDInfo<String> dependentSourceItemIDInfo = null;
		
		String dependentTargetItemID = "server.classloader.appinf.path.value";
		String dependentSourceItemID = "server.classloader.mybatis_config_file_relative_path.value";
		
		isWritePermissionChecking = false;
		try {
			dependentTargetItemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH, dependentTargetItemID, "서버 동적 클래스 APP-INF 경로",
					BuildSystemPathSupporter.getAPPINFPathString(
							projectName,
							sinnoriInstalledPathString),
					isWritePermissionChecking,
					new GeneralConverterReturningRegularFile(false));
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		isWritePermissionChecking = true;
		try {
			dependentSourceItemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"ClassLoader#getResourceAsStream 의 구현 이며 \n<APP-INF>/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다.\nex) kr/pe/sinnori/impl/mybatis/mybatisConfig.xml",
					"", isWritePermissionChecking,
					new GeneralConverterReturningEmptyOrNoTrimString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}
		
		try {
			validChecker = new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		} catch (IllegalArgumentException e) {
			log.info("the parameter dependentTargetItemIDInfo's itemValueConverter type is not GeneralConverterReturningPath, errormessage={}", e.getMessage());
			throw e;
		} catch (ConfigErrorException e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testIsValid_ExpectedValueComparison() {
		boolean expectedValue = true;
		boolean returnedValue = false;
		
		String prefixOfItem = "project.sample_base.";
		Properties sinnoriConfigFileProperties = new Properties();
		
		
		String tempFileFullPathString = new StringBuilder(".")
		.append(File.separator)
		.append("tempdir").toString();
		
		File tempFile = new File(tempFileFullPathString);
		
		boolean result = tempFile.mkdir();
		if (!result) {
			fail(tempFileFullPathString + " dir creatation fail");
		}
		
		tempFile.deleteOnExit();

		tempFileFullPathString = new StringBuilder(tempFileFullPathString)
		.append(File.separator)
		.append("resources").toString();
		
		tempFile = new File(tempFileFullPathString);
		
		result = tempFile.mkdir();
		if (!result) {
			fail(tempFileFullPathString+" dir creatation fail");
		}
		
		tempFile.deleteOnExit();
		
		tempFileFullPathString = new StringBuilder(tempFileFullPathString)
		.append(File.separator)
		.append("temp0000.tmp").toString();
		
		tempFile = new File(tempFileFullPathString);

		try {
			result = tempFile.createNewFile();
		} catch (IOException e) {
			fail("./tempdir/resources/temp0000.tmp regular file creatation fail, errormessage="+e.getMessage());
		}
		if (!result) {
			fail("./tempdir/resources/temp0000.tmp regular file creatation fail");
		}
		
		tempFile.deleteOnExit();
		
		// log.info("tempFileFullPathString={}", tempFileFullPathString);
		
		String tempFileOnlyPathString = null;
		String tempFileOnlyName = null;
		
		int lastInxOfFileSeperator = tempFileFullPathString.lastIndexOf(File.separatorChar);
		int secondLastInxOfFileSeperator = tempFileFullPathString.substring(0, lastInxOfFileSeperator).lastIndexOf(File.separatorChar);
				
		/*log.info("tempFileOnlyPathString={}, lastInxOfFileSeperator={}, secondLastInxOfFileSeperator={}", 
				tempFileFullPathString, lastInxOfFileSeperator, secondLastInxOfFileSeperator);*/
		
		try {
			tempFileOnlyPathString = tempFileFullPathString.substring(0, secondLastInxOfFileSeperator);
			tempFileOnlyName = tempFileFullPathString.substring(lastInxOfFileSeperator+1);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}		
		
		/*log.info("tempFileOnlyPathString={}", tempFileOnlyPathString);
		log.info("tempFileOnlyName={}", tempFileOnlyName);*/
		
		
		tempFile.deleteOnExit();
		
		/*tempFileOnlyPathString = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\APP-INF";
		tempFileOnlyName = "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml";*/
				
		sinnoriConfigFileProperties.put(prefixOfItem+validChecker.getDependentTargetItemID(), tempFileOnlyPathString);
		sinnoriConfigFileProperties.put(prefixOfItem+validChecker.getDependentSourceItemID(), tempFileOnlyName);
		
		try {
			returnedValue = validChecker.isValid(sinnoriConfigFileProperties, prefixOfItem);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			fail(e.getMessage());
		}
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
}
