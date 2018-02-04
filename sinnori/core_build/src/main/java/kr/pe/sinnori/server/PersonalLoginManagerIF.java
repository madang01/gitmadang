package kr.pe.sinnori.server;

public interface PersonalLoginManagerIF {
	public boolean isLogin();
	public void registerLoginUser(String loginID);
	public void releaseLoginUserResource();
	public String getUserID();
}
