package kr.pe.sinnori.server;

public interface LoginManagerIF {
	public void doLoginSuccess(String loginID, ClientResource clientResource);
	public boolean isLogin(String loginID);

}
