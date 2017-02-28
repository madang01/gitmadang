package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

public class SinnoriServerMain {
	
	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");

		try {
			ServerProject mainServerProject = ServerProjectManager	.getInstance().getRunningMainServerProject();
			mainServerProject.startServer();
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}	
	
}
