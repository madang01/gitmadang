package kr.pe.sinnori.gui.syncfileupdown.lib;

public class UserMessageManger {
	
	private UserMessageManger() {
		/** nothing */
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class UserMessageMangerHolder {
		static final UserMessageManger singleton = new UserMessageManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static UserMessageManger getInstance() {
		return UserMessageMangerHolder.singleton;
	}
	
	public String getMessageWhenSocketTimeoutException() {
		return "소켓 타임아웃";
	}
	
	public String getMessageWhenServerNotReadyException() {
		return "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요";
	}
	
	public String getMessageWhenInterruptedException() {
		return "쓰레드 인터럽트 호출되어 종료합니다";
	}
	
	
	public String getMessageWhenLoginServiceAccessWithoutLogin() {
		return "이 서비스는 로그인이 필요합니다.";
	}
}
