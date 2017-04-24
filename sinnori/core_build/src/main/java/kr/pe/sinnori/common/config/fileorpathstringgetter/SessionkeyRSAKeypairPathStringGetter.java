package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;

/**
 * 세션키를 위한 공개키 암호화를 위한 공개키쌍이 위치한 경로명을 정의한 클래스. 특이사항으로 '부가정보들'가 없다.
 * @author "Won Jonghoon"
 *
 */
public class SessionkeyRSAKeypairPathStringGetter extends AbstractFileOrPathStringGetter {
	public SessionkeyRSAKeypairPathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnSinnoriInstalledPath(
			String mainProjectName, String sinnoriInstalledPathString, String ... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters has one more paramters");
		}
		
		return BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName);
	}
}
