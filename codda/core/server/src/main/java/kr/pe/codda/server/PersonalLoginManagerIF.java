package kr.pe.codda.server;

public interface PersonalLoginManagerIF {
	public boolean isLogin();
	public void registerLoginUser(String loginID);
	public String getLoginID();
}
