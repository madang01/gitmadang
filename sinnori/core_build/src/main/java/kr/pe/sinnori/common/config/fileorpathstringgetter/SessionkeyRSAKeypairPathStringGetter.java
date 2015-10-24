package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;

public class SessionkeyRSAKeypairPathStringGetter extends AbstractFileOrPathStringGetter {
	public SessionkeyRSAKeypairPathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnBuildSystem(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(mainProjectName,
						sinnoriInstalledPathString);
	}
}
