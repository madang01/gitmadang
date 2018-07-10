package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ProjectLoginManagerIF {
	public void registerloginUser(SelectionKey selectedKey, String loginID);
	public void removeLoginUser(SelectionKey selectedKey);
			
	public boolean isLogin(String loginID);
	public SelectionKey getSelectionKey(String loginID);
	public AcceptedConnection getAcceptedConnection(SelectionKey selectedKey);
}
