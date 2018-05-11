package main;

import kr.pe.codda.common.exception.NotFoundProjectException;
import kr.pe.codda.server.AnyProjectServer;
import kr.pe.codda.server.MainServerManager;

public class SinnoriServerMain {
	public static void main(String argv[]) throws NotFoundProjectException {
		AnyProjectServer mainProjectServer = MainServerManager.getInstance().getMainProjectServer();
		mainProjectServer.startServer();
	}
}