package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;

/**
 * 서버 동적 클래스 관리자의 리소스 기본 경로명을 정의한 클래스. 특이사항으로 '부가정보들'가 없다.
 * @author "Won Jonghoon"
 *
 */
public class ServerClassloaderAPPINFPathStringGetter extends AbstractFileOrPathStringGetter{

	public ServerClassloaderAPPINFPathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnSinnoriInstalledPath(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getServerAPPINFPathString(mainProjectName,
						sinnoriInstalledPathString);
	}
}
