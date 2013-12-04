package main;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

public class SinnoriServerMain implements CommonRootIF {

	public static void main(String argv[]) {
		String projectName = System.getenv("SINNORI_PROJECT_NAME");
		if (null == projectName) {
			log.fatal("환경변수 SINNORI_PROJECT_NAME 가 정의되지 않았습니다.");
			System.exit(1);
		}
		
		if (projectName.trim().length() == 0) {
			log.fatal("환경변수 SINNORI_PROJECT_NAME 값이 지정되지 않았습니다. 환경변수 SINNORI_PROJECT_NAME 에 프로젝트 이름을 정해주세요.");
			System.exit(1);
		}
		
		ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);
			
		serverProject.startServer();

	}
}
