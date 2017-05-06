package main;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.server.AnyProjectServer;
import kr.pe.sinnori.server.ProjectServerManager;

public class SinnoriServerMain {
	public static void main(String argv[]) throws NotFoundProjectException {
		AnyProjectServer mainProjectServer = ProjectServerManager.getInstance().getRunningMainProjectServer();
		mainProjectServer.startServer();
	}
}