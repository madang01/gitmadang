package kr.pe.codda.common.config.iteminfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfoManger;
import kr.pe.codda.common.util.SequencedProperties;

public class ItemIDInfoMangerTest extends AbstractJunitTest {
	
	
	@Test
	public void testGetInstance() {
		ItemIDInfoManger.getInstance();
	}
	
	@Test
	public void testGetNewConfigSequencedProperties() {
		String mainProjectName = "sample_test";
		String installedPathString = installedPath.getAbsolutePath();
		SequencedProperties newConfigProperties = ItemIDInfoManger.getInstance()
				.getNewConfigSequencedProperties(installedPathString, mainProjectName);
		log.info(newConfigProperties.toString());
	}
	
	@Test
	public void testIsDisabled_true() {
		// String mainProjectName = "sample_test";
		// String installedPathString = installedPath.getAbsolutePath();
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		
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
		
		boolean acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		// log.info("acutalValue={}", acutalValue);
		
		assertEquals(expectedValue, acutalValue);
		
		
		acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		// SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID
		
	}
	
	@Test
	public void testIsDisabled_false() {
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		
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
		
		boolean acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
		acutalValue = itemIDInfoManger.isDisabled(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, 
				prefixOfItemID, sourceProperties);
		
		assertEquals(expectedValue, acutalValue);
		
	}
	
	
	@Test
	public void testIsFileOrPathStringGetter() {
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		boolean expectedValue = true;
		boolean acutalValue = false;
		
		expectedValue = true;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = true;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
		
		expectedValue = false;
		acutalValue = itemIDInfoManger.isFileOrPathStringGetter(ItemIDDefiner.CommonPartItemIDDefiner.JDF_USER_LOGIN_PAGE_ITEMID);
		
		assertEquals(expectedValue, acutalValue);
	}
	
	
	@Test
	public void testGetFileOrPathStringGetter() {
		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		AbstractFileOrPathStringGetter actuvalFileOrPathStringGetter = null;
		
		String itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		actuvalFileOrPathStringGetter = itemIDInfoManger.getFileOrPathStringGetter(itemID);
		
		if (null == actuvalFileOrPathStringGetter) {
			fail("the dbcp config file item is not a FileOrPathStringGetter type");
		}
		
		String mainProjectName = "sample_base";
		String sampleBaseDBCPFilePathString = actuvalFileOrPathStringGetter
		.getFileOrPathStringDependingOnInstalledPath(installedPath.getAbsolutePath(), 
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
