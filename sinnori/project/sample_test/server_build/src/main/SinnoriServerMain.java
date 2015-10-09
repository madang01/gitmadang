package main;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

public class SinnoriServerMain implements CommonRootIF {
	public static void main(String argv[]) throws NotFoundProjectException {
		
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
		if (null == projectName) {
			log.error("자바 시스템 환경 변수[{}] 가 정의되지 않았습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
			System.exit(1);
		}
		
		String trimProjectName = projectName.trim();
		
		if (trimProjectName.length() == 0) {
			log.error("자바 시스템 환경 변수[{}] 값[{}]이 빈 문자열 있습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME, projectName);
			System.exit(1);
		}
		
		if (! projectName.equals(trimProjectName)) {
			log.error("자바 시스템 환경 변수[{}] 값[{}] 앞뒤로 공백 문자열이 존재합니다.", 
					CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME, projectName);
			System.exit(1);
		}
		
		
		ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);
			
		serverProject.startServer();

	}
}
