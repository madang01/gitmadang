package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ProjectLoginManagerIF {
	/**
	 * 소켓 연결 여부랑 상관 없이 로그인 등록이 안되어 있다면 신규 등록 하고 로그인 등록이 되어 있다면 예외 던짐
	 * @param sc
	 * @param loginID
	 * @throws IllegalArgumentException 로그인 등록이 되어 있을시 발생
	 */
	public void registerloginUser(SelectionKey selectedKey, String loginID);
	/**
	 * 소켓 연결 여부랑 로그인 등록 여부 둘다 상관 없이 등록 삭제 처리하므로 중복 호출도 가능함
	 * @param loginID
	 */
	public void removeLoginUser(String loginID);
	
	public void removeLoginUser(SelectionKey selectedKey);
			
	/**
	 * @param loginID
	 * @return 소켓이 연결되어있고 로그인 등록되어 있다면 true, 아니면 false
	 */
	public boolean isLogin(String loginID);
	/**
	 * @param sc
	 * @return 소켓이 연결되어있고 로그인 등록되어 있다면 true, 아니면 false
	 */
	public boolean isLogin(SelectionKey selectedKey);
	/**
	 * @param sc
	 * @return 소켓 연결 여부에 상관 없이 로그인 등록되어 있다면 로그인 아이디를 반환하고 그렇지 않다면 null 을 반환
	 */
	public String getUserID(SelectionKey selectedKey);
	
	public SelectionKey getSelectionKey(String loginUserID);
	
	public AcceptedConnection getAcceptedConnection(SelectionKey selectedKey);
}
