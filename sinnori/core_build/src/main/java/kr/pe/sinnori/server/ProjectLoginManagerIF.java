package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;

public interface ProjectLoginManagerIF {
	public void registerloginUser(SocketChannel sc, String loginID);
	public void removeLoginUser(String loginID);
	public void removeLoginUser(SocketChannel sc);
	public boolean isLogin(String loginID);
	public boolean isLogin(SocketChannel sc);
}
