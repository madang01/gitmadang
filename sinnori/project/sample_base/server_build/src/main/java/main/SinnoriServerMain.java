package main;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

public class SinnoriServerMain {	
	public static void main(String argv[]) throws NotFoundProjectException {		
		ServerProject mainServerProject = ServerProjectManager.getInstance().getMainServerProject();			
		mainServerProject.startServer();
	}
}
