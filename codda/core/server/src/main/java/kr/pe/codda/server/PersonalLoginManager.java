package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

import kr.pe.codda.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.codda.common.updownfile.LocalTargetFileResourceManager;

public class PersonalLoginManager implements PersonalLoginManagerIF {	
	// private Logger log = LoggerFactory.getLogger(PersonalLoginManager.class);
	
	private SelectionKey personalSelectionKey = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	
	private String personalLoginID = null;
	
	public PersonalLoginManager(SelectionKey personalSelectionKey, 
			ProjectLoginManagerIF projectLoginManager) {
		if (null == personalSelectionKey) {
			throw new IllegalArgumentException("the parameter personalSelectionKey is null");
		}
		
		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
		}
		
		this.personalSelectionKey = personalSelectionKey;
		this.projectLoginManager = projectLoginManager; 
	}
	
	public boolean isLogin() {
		return projectLoginManager.isLogin(personalSelectionKey);
	}
	
	public void registerLoginUser(String loginID) {
		this.personalLoginID = loginID;
		projectLoginManager.registerloginUser(personalSelectionKey, loginID);
	}	
	
	public String getUserID() {		
		return projectLoginManager.getUserID(personalSelectionKey);
	}
	
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	protected void releaseLoginUserResource() {
		if (null != personalLoginID) {
			projectLoginManager.removeLoginUser(personalSelectionKey);
			LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
			LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
		}
	}

	/*@Override
	public SocketChannel getSocketChannel(String loginUserID) {
		return projectLoginManager.getSocketChannel(loginUserID);
	}*/
}
