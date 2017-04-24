package kr.pe.sinnori.common.config.fileorpathstringgetter;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;

/**
 * 공통 메시지 정보 파일들이 위치한 경로를 정의한 클래스. 특이사항으로 '부가정보들' 없음.
 * 
 * @author "Won Jonghoon"
 * 
 */
public class CommonMessageInfoXMLPathStringGetter extends
		AbstractFileOrPathStringGetter {
	public CommonMessageInfoXMLPathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnSinnoriInstalledPath(
			String mainProjectName, String sinnoriInstalledPathString,
			String... etcParamters) {
		if (0 < etcParamters.length) {
			throw new IllegalArgumentException(
					"the paramter etcParamters has one more paramters");
		}

		return BuildSystemPathSupporter.getMessageInfoPathString(sinnoriInstalledPathString, mainProjectName);
	}
}
