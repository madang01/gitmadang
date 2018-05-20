package kr.pe.codda.common.config.fileorpathstringgetter;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;

public class SessionkeyRSAPrivatekeyFilePathStringGetter extends AbstractFileOrPathStringGetter {
	public SessionkeyRSAPrivatekeyFilePathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnInstalledPath(
			String installedPathString,
			String mainProjectName, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return ProjectBuildSytemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(installedPathString, mainProjectName);
	}
}