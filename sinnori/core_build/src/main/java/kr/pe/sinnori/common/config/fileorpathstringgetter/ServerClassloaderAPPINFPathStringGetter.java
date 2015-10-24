package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;

public class ServerClassloaderAPPINFPathStringGetter extends AbstractFileOrPathStringGetter{

	public ServerClassloaderAPPINFPathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnBuildSystem(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getAPPINFPathString(mainProjectName,
						sinnoriInstalledPathString);
	}
}
