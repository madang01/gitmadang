package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;

public class SessionkeyRSAPrivatekeyFilePathStringGetter extends AbstractFileOrPathStringGetter {
	public SessionkeyRSAPrivatekeyFilePathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnSinnoriInstalledPath(
			String sinnoriInstalledPathString,
			String mainProjectName, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(sinnoriInstalledPathString, mainProjectName);
	}
}