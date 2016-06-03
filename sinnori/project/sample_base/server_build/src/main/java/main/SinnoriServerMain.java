package main;

import kr.pe.sinnori.common.config.vo.ProjectPartValueObject;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.project.ProjectWorkerIF;
import kr.pe.sinnori.common.serverlib.AbstractDBCommand;
import kr.pe.sinnori.common.serverlib.SampleBaseDB;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinnoriServerMain implements ProjectWorkerIF {	
	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");

		try {
			ServerProject mainServerProject = ServerProjectManager
					.getInstance().getMainServerProject();
			mainServerProject.startServer(new SinnoriServerMain());
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}


	@Override
	public void doStartingWork(
			ProjectPartValueObject projectPartVO) {
		// Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		/*
		
		boolean isDropAllTable = false;
		String keyValueOfIsDropAlltable = System
				.getProperty(ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE);		
		if (null != keyValueOfIsDropAlltable) {
			 if (!keyValueOfIsDropAlltable.equals("true") && !keyValueOfIsDropAlltable.equals("false")) {
				 log.warn("this server's java system properties variable '{}' has 'true' or 'false', this value[{}] is wrong", 
						 ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE, keyValueOfIsDropAlltable);
			 }
			 
			 if (keyValueOfIsDropAlltable.equals("true")) {
				 isDropAllTable = true;
			 } else if (keyValueOfIsDropAlltable.equals("false")) {
				 isDropAllTable = false;
			 } else {
				 log.error("this server's java system properties variable '{}' has 'true' or 'false', this value[{}] is wrong", 
						 ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE, keyValueOfIsDropAlltable);
				 
				 System.exit(1);
			 }
			
		}	*/
		

		
		
		AbstractDBCommand arryDBCommand[] = {new SampleBaseDB()};
		
		for (int i=0;i < arryDBCommand.length; i++) {
			AbstractDBCommand dbCommand = arryDBCommand[i];
			dbCommand.execute();
		}
	}
	
	
}
