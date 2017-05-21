package main;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.server.AnyProjectServer;
import kr.pe.sinnori.server.MainServerManager;

public class SinnoriServerMain {
	public static void main(String argv[]) throws NotFoundProjectException {
		AnyProjectServer mainProjectServer = MainServerManager.getInstance().getMainProjectServer();
		mainProjectServer.startServer();
	}
}