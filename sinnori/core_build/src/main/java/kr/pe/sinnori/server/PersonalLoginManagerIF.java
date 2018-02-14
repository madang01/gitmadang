package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;

public interface PersonalLoginManagerIF {
	public boolean isLogin();
	public void registerLoginUser(String loginID);
	public void releaseLoginUserResource();
	public String getUserID();
	
	public SocketChannel getSocketChannel(String loginUserID);
}
