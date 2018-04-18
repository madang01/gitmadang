package kr.pe.sinnori.common.config.iteminfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.util.SequencedProperties;

public class SinnoriItemIDInfoMangerTest extends AbstractJunitTest {
	
	
	@Test
	public void testGetInstance() {
		SinnoriItemIDInfoManger.getInstance();
	}
	
	@Test
	public void testGetNewSinnoriConfigSequencedProperties() {
		String mainProjectName = "sample_test";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		SequencedProperties newSinnoriProperties = SinnoriItemIDInfoManger.getInstance()
				.getNewSinnoriConfigSequencedProperties(sinnoriInstalledPathString, mainProjectName);
		log.info(newSinnoriProperties.toString());
		
		/*try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(newSinnoriProperties, "this file is the project "+mainProjectName+"'s configuration properties file", 
					"D:\\temp.properties", CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
			org.junit.Assert.fail(e.getMessage());
		}*/
	}
	
	@Test
	public void testIsDisabled_true() {
		// String mainProjectName = "sample_test";
		// String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		
		String prefixOfItemID = "";
		Properties sourceProperties = new Properties();
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID, "SERVER");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				"33333333333");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				"33333333333");
		
		boolean expectedValue = true;
		
		boolean acutalValue = sinnoriItemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		// log.info("acutalValue={}", acutalValue);
		
		assertEquals(expectedValue, acutalValue);
		
		
		acutalValue = sinnoriItemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		// SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID
		
	}
	
	@Test
	public void testIsDisabled_false() {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		
		String prefixOfItemID = "";
		Properties sourceProperties = new Properties();
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID, "FILE");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				"33333333333");
		
		sourceProperties.setProperty(ItemIDDefiner.CommonPartItemIDDefiner
				.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				"4444");
		
		boolean expectedValue = false;
		
		boolean acutalValue = sinnoriItemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		acutalValue = sinnoriItemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
	}
	
	
	@Test
	public void testIsFileOrPathStringGetter() {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		boolean expectedValue = true;
		boolean acutalValue = false;
		
		expectedValue = true;
		acutalValue = sinnoriItemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = true;
		acutalValue = sinnoriItemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = false;
		acutalValue = sinnoriItemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
	}
	
	
	@Test
	public void testGetFileOrPathStringGetter() {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		AbstractFileOrPathStringGetter actuvalFileOrPathStringGetter = null;
		
		String itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		actuvalFileOrPathStringGetter = sinnoriItemIDInfoManger.getFileOrPathStringGetter(itemID);
		
		if (null == actuvalFileOrPathStringGetter) {
			fail("the dbcp config file item is not a FileOrPathStringGetter type");
		}
		
		String mainProjectName = "sample_base";
		String sampleBaseDBCPFilePathString = actuvalFileOrPathStringGetter
		.getFileOrPathStringDependingOnSinnoriInstalledPath(sinnoriInstalledPath.getAbsolutePath(), 
				mainProjectName, "sample_base_db");
		
		File sampleBaseDBCPFile = new File(sampleBaseDBCPFilePathString);
		if (! sampleBaseDBCPFile.exists()) {
			fail("the sample_base project's sample_base_db file dosn't exist");
		}
		
		if (! sampleBaseDBCPFile.isFile()) {
			fail("the sample_base project's sample_base_db file is not a regual file");
		}
	}
}
