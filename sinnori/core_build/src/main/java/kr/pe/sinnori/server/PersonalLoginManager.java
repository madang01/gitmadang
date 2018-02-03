package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;

public class PersonalLoginManager {
	private Logger log = LoggerFactory.getLogger(PersonalLoginManager.class);
	
	private String loginID = null;
	private SocketChannel personalSC = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	
	public PersonalLoginManager(SocketChannel personalSC, 
			ProjectLoginManagerIF projectLoginManager) {
		this.personalSC = personalSC;
		this.projectLoginManager = projectLoginManager;
	}
	
	public boolean isLogin() {
		return projectLoginManager.isLogin(personalSC);
	}
	
	public void registerLoginUser(String loginID) {
		this.loginID = loginID;
		projectLoginManager.registerloginUser(personalSC, loginID);
	}
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	public void releaseLoginUserResource() {
		// FIXME!
		log.info("personalSC[{}] logout", personalSC.hashCode());
		
		if (null != loginID) {
			LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(loginID);
			LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(loginID);
		}		
	}	
}
