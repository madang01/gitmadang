package kr.pe.sinnori.common.config.iteminfo;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.util.SequencedProperties;

public class SinnoriItemIDInfoMangerTest extends AbstractJunitSupporter {
	
	
	@Test
	public void testGetInstance() {
		SinnoriItemIDInfoManger.getInstance();
	}
	
	@Test
	public void testGetNewSinnoriConfigSequencedProperties() {
		String mainProjectName = "sample_test";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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
	
	
}
