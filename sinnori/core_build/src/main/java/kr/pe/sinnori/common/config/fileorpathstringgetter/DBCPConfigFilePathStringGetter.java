package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;

public class DBCPConfigFilePathStringGetter extends AbstractFileOrPathStringGetter{
	public DBCPConfigFilePathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnBuildSystem(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamters) {
		if (0 == etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters must have dbcp paramter");
		}
		
		if (1 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters must have two more paramters");
		}
		return BuildSystemPathSupporter
				.getDBCPConfigFilePathString(mainProjectName,
						sinnoriInstalledPathString, etcParamters[0]);
	}
}
