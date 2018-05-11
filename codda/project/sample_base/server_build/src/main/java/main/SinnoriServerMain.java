package main;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.exception.NotFoundProjectException;
import kr.pe.codda.server.AnyProjectServer;
import kr.pe.codda.server.MainServerManager;
import kr.pe.codda.server.lib.ServerDBEnvironment;

public class SinnoriServerMain {

	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		
		try {
			ServerDBEnvironment.setup();
			
			AnyProjectServer mainProjectServer = MainServerManager.getInstance().getMainProjectServer();
			mainProjectServer.startServer();
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}
}
