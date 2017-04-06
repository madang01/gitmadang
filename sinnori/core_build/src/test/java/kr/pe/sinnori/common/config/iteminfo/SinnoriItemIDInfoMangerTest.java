package kr.pe.sinnori.common.config.iteminfo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.util.SequencedProperties;

public class SinnoriItemIDInfoMangerTest {
	Logger log = LoggerFactory
			.getLogger(SinnoriItemIDInfoMangerTest.class);
	
	@Before
	public void setup() {		
		SinnoriLogbackManger.getInstance().setup();
	}
	
	@Test
	public void testGetInstance() {
		SinnoriItemIDInfoManger.getInstance();
	}
	
	@Test
	public void testGetNewSinnoriConfigSequencedProperties() {
		String mainProjectName = "sample_test";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		SequencedProperties newSinnoriProperties = SinnoriItemIDInfoManger.getInstance()
				.getNewSinnoriConfigSequencedProperties(mainProjectName, sinnoriInstalledPathString);
		log.info(newSinnoriProperties.toString());
		
		/*try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(newSinnoriProperties, "this file is the project "+mainProjectName+"'s configuration properties file", 
					"D:\\temp.properties", CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
			org.junit.Assert.fail(e.getMessage());
		}*/
	}
	
	
}
