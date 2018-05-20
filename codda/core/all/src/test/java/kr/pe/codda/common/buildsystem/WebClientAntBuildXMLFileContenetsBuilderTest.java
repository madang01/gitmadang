package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class WebClientAntBuildXMLFileContenetsBuilderTest extends AbstractJunitTest {
	@Test
	public void testBuild_isEqualToSampleBaseWebClientAntBuildXMLFile() {
		final String mainProjectName = "sample_base";
		String expectedAppClientAntBuildXMLFileContents = WebClientAntBuildXMLFileContenetsBuilder.build(mainProjectName);
		
		// log.info(expectedAppClientAntBuildXMLFileContents);
		
		String appClientAntBuildXMLFilePathString = WebClientBuildSystemPathSupporter.getWebClientAntBuildXMLFilePathString(installedPath.getAbsolutePath(), mainProjectName);
		File appClientAntBuildXMLFilePath = new File (appClientAntBuildXMLFilePathString);
		
		String acutalAppClientAntBuildXMLFileContents = null;
		try {
			acutalAppClientAntBuildXMLFileContents = FileUtils.readFileToString(appClientAntBuildXMLFilePath, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("fail to read a serer ant build xml file[")
					.append(appClientAntBuildXMLFilePathString)
					.append("]").toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		assertEquals(expectedAppClientAntBuildXMLFileContents, acutalAppClientAntBuildXMLFileContents);		
	}
}
