package kr.pe.codda.common.buildsystem;

import junitlib.AbstractJunitTest;

public class AppClientAntBuildXMLFileContenetsBuilderTest extends AbstractJunitTest {

	/*@Test
	public void testBuild_isEqualToSampleBaseAppClientAntBuildXMLFile() {
		final String mainProjectName = "sample_base";
		String expectedAppClientAntBuildXMLFileContents = AppClientAntBuildXMLFileContenetsBuilder.build(mainProjectName);
		
		String appClientAntBuildXMLFilePathString = AppClientBuildSystemPathSupporter.getAppClientAntBuildXMLFilePathString(installedPath.getAbsolutePath(), mainProjectName);
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
		
	}*/
}
