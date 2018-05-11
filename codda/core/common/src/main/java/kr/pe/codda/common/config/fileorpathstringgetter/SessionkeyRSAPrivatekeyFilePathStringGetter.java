package kr.pe.codda.common.config.fileorpathstringgetter;

import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;

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
		
		return BuildSystemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(installedPathString, mainProjectName);
	}
}