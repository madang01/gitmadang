package kr.pe.codda.common.config.fileorpathstringgetter;

import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;

public class SessionkeyRSAPublickeyFilePathStringGetter extends AbstractFileOrPathStringGetter {
	public SessionkeyRSAPublickeyFilePathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOninstalledPath(
			String installedPathString,
			String mainProjectName, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getSessionKeyRSAPublickeyFilePathString(installedPathString, mainProjectName);
	}
}