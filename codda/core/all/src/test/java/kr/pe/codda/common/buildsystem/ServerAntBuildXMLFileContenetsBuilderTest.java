package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class ServerAntBuildXMLFileContenetsBuilderTest extends AbstractJunitTest {

	@Test
	public void testBuild_isEqualToSampleBaseServerAntBuildXMLFile() {
		final String mainProjectName = "sample_base";
		String expectedServerAntBuildXMLFileContents = ServerAntBuildXMLFileContenetsBuilder.build(mainProjectName);
		
		String serverAntBuildXMLFilePathString = ServerBuildSytemPathSupporter.getServerAntBuildXMLFilePathString(installedPath.getAbsolutePath(), mainProjectName);
		File serverAntBuildXMLFilePath = new File (serverAntBuildXMLFilePathString);
		
		String acutalServerAntBuildXMLFileContents = null;
		try {
			acutalServerAntBuildXMLFileContents = FileUtils.readFileToString(serverAntBuildXMLFilePath, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("fail to read a serer ant build xml file[")
					.append(serverAntBuildXMLFilePathString)
					.append("]").toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		assertEquals(expectedServerAntBuildXMLFileContents, acutalServerAntBuildXMLFileContents);
	}
}
