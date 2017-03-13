package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.serverlib.AbstractDBCommand;
import kr.pe.sinnori.common.serverlib.SampleBaseDB;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

public class SinnoriServerMain {	
	
	public void InitSimplebaseServer() {			
		AbstractDBCommand arryDBCommand[] = {new SampleBaseDB()};
		
		for (int i=0;i < arryDBCommand.length; i++) {
			AbstractDBCommand dbCommand = arryDBCommand[i];
			dbCommand.execute();
		}
	}
	
	
	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		
		SinnoriServerMain sinnoriServerMain = new SinnoriServerMain();
		sinnoriServerMain.InitSimplebaseServer();
		
		try {
			ServerProject mainServerProject = ServerProjectManager
					.getInstance().getRunningMainServerProject();
			mainServerProject.startServer();
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}	
}
