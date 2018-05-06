package kr.pe.codda.server;

import java.nio.channels.SocketChannel;

import kr.pe.codda.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.codda.common.updownfile.LocalTargetFileResourceManager;

public class PersonalLoginManager implements PersonalLoginManagerIF {	
	// private Logger log = LoggerFactory.getLogger(PersonalLoginManager.class);
	
	private String personalLoginID = null;
	private SocketChannel personalSC = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	
	public PersonalLoginManager(SocketChannel personalSC, 
			ProjectLoginManagerIF projectLoginManager) {
		if (null == personalSC) {
			throw new IllegalArgumentException("the parameter personalSC is null");
		}
		
		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
		}
		
		this.personalSC = personalSC;
		this.projectLoginManager = projectLoginManager;
	}
	
	public boolean isLogin() {
		return projectLoginManager.isLogin(personalSC);
	}
	
	public void registerLoginUser(String loginID) {
		this.personalLoginID = loginID;
		projectLoginManager.registerloginUser(personalSC, loginID);
	}	
	
	public String getUserID() {
		if (! personalSC.isConnected()) {
			return null;
		}
		return projectLoginManager.getUserID(personalSC);
	}
	
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	public void releaseLoginUserResource() {
		if (null != personalLoginID) {
			projectLoginManager.removeLoginUser(personalSC);
			LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
			LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
		}		
	}

	@Override
	public SocketChannel getSocketChannel(String loginUserID) {
		return projectLoginManager.getSocketChannel(loginUserID);
	}
}
