package kr.pe.sinnori.server;

public interface LoginManagerIF {
	public void login(String loginID, ClientResource clientResource);
	public boolean isLogin(String loginID);

}
