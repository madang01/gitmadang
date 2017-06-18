package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public final class ServerSessionkeyManager {
	private ServerSessionkeyIF mainProjectSeverSessionkey = null;
	private SymmetricException savedSymmetricException = null;
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ServerSessionkeyManagerHolder {
		static final ServerSessionkeyManager singleton = new ServerSessionkeyManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ServerSessionkeyManager getInstance() {
		return ServerSessionkeyManagerHolder.singleton;
	}
	
	private ServerSessionkeyManager() {
		try {
			mainProjectSeverSessionkey = new ServerSessionkey(new ServerRSA());
		} catch (SymmetricException e) {
			savedSymmetricException = e;
		}
	}
	
	public ServerSessionkeyIF getMainProjectServerSessionkey() throws SymmetricException {
		if (null != savedSymmetricException) throw savedSymmetricException;
		
		return mainProjectSeverSessionkey;
	}
}
