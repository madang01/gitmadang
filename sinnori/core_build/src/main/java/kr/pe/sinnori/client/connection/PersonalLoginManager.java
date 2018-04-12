package kr.pe.sinnori.client.connection;

import java.io.IOException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PersonalLoginManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(PersonalLoginManager.class);
	
	private AbstractConnection conn = null;
	private SocketResoruceIF socketResoruce = null;
	
	private boolean isLogin = false;
	private String loginID = null;
	
	public PersonalLoginManager(AbstractConnection conn, SocketResoruceIF socketResoruce) {
		if (null == conn) {
			throw new IllegalArgumentException("the parameter conn is null");
		}
		
		this.conn = conn;
		this.socketResoruce = socketResoruce;
	}
	
	public boolean isLogin() {
		return (conn.isConnected() && isLogin);
	}
	
	public void registerLoginUser(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}
		if (isLogin()) { 
			throw new IllegalArgumentException("alreay login");
		}
		isLogin = true;
		this.loginID = loginID;
	}	
	
	public String getUserID() {
		if (! isLogin()) { 
			return null;
		}
		return loginID;
	}
	
	public void releaseLoginUserResources() {
		try {
			conn.close();
		} catch (IOException e) {
			log.warn("fail to close connection[{}]", conn.hashCode());
		}
		
		socketResoruce.releaseSocketResources();
	}
}
