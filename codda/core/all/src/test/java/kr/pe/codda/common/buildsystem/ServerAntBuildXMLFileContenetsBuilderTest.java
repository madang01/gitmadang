package kr.pe.codda.common.buildsystem;

import junitlib.AbstractJunitTest;

public class ServerAntBuildXMLFileContenetsBuilderTest extends AbstractJunitTest {

	/*@Test
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
	}*/
}
