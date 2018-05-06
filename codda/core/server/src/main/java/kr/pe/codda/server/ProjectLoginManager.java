package kr.pe.codda.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ProjectLoginManager implements ProjectLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ProjectLoginManager.class);
	
	private final Object monitor = new Object();
	
	private HashMap<SocketChannel, String> socketChannel2LogingIDHash 
	= new HashMap<SocketChannel, String>(); 
	
	private HashMap<String, SocketChannel> logingID2SocketChannelDHash 
	= new HashMap<String, SocketChannel>();
	

	@Override
	public void registerloginUser(SocketChannel sc, String loginID) {
		if (null == sc) {
			throw new IllegalArgumentException("the parameter sc is null");
		}

		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}
		
		synchronized (monitor) {			
			if (socketChannel2LogingIDHash.containsKey(sc)) {				
				log.warn("the parameter sc[{}] is the socket channel that is already registered", sc.hashCode());
				return;
			}
			if (logingID2SocketChannelDHash.containsKey(loginID)) {
				log.warn("the parameter loginID[{}] is the login id that is already registered", loginID);
			}
			
			log.info("register a login user[sc hashcode={}, loginID={}]", sc.hashCode(), loginID);			
			
			socketChannel2LogingIDHash.put(sc, loginID);
			logingID2SocketChannelDHash.put(loginID, sc);
		}
	}

	private void doRemoveLoginUser(SocketChannel sc, String loginID) {
		logingID2SocketChannelDHash.remove(loginID);
		socketChannel2LogingIDHash.remove(sc);
	}
	
	public void removeLoginUser(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}
		
		synchronized (monitor) {
			SocketChannel sc = logingID2SocketChannelDHash.get(loginID);
			if (null != sc) {
				doRemoveLoginUser(sc, loginID);
			}
		}
	}
	
	public void removeLoginUser(SocketChannel sc) {
		if (null == sc) {
			throw new IllegalArgumentException("the parameter sc is null");
		}
		
		synchronized (monitor) {
			String loginID = socketChannel2LogingIDHash.get(sc);
			if (null != loginID) {
				doRemoveLoginUser(sc, loginID);
			}
		}
	}
	
	@Override
	public boolean isLogin(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}
		
		boolean isLogin = false;
			
		synchronized (monitor) {			
			SocketChannel sc = logingID2SocketChannelDHash.get(loginID);
			/**
			 * <pre>
			 * Warning! 한줄 표현(isLogin = (null != sc) && sc.isConnected()) 가능하지만 
			 * 연산자 우선 순위를 고려 안하면 null point exception 발생하므로 
			 * 아래 처럼 연산자 우선 순위를 순차적으로 풀어 여러 줄로 기술한것이므로 다시 한줄로 다시 만들지 말것
			 * </pre> 
			 */
			if (null != sc) {
				isLogin = sc.isConnected();
			}
		}
		
		return isLogin;
	}

	@Override
	public boolean isLogin(SocketChannel sc) {
		if (null == sc) {
			throw new IllegalArgumentException("the parameter sc is null");
		}
		
		boolean isLogin = false;
		
		synchronized (monitor) {
			String loginID = socketChannel2LogingIDHash.get(sc);
			/**
			 * <pre>
			 * Warning! 한줄 표현(isLogin = (null != loginID) && sc.isConnected()) 가능하지만 
			 * 연산자 우선 순위를 고려 안하면 null point exception 발생하므로
			 * 아래 처럼 연산자 우선 순위를 순차적으로 풀어 여러 줄로 기술한것이므로 다시 한줄로 다시 만들지 말것
			 * </pre> 
			 */
			if (null != loginID) {
				isLogin = sc.isConnected();
			}
		}
		
		return isLogin;
	}
	
	public String getUserID(SocketChannel sc) {
		if (null == sc) {
			throw new IllegalArgumentException("the parameter sc is null");
		}
		String loginID = null;
		synchronized (monitor) {
			loginID = socketChannel2LogingIDHash.get(sc);
		}
		
		return loginID;
	}

	@Override
	public SocketChannel getSocketChannel(String loginUserID) {
		if (null == loginUserID) {
			throw new IllegalArgumentException("the parameter loginUserID is null");
		}
		
		return logingID2SocketChannelDHash.get(loginUserID);
	}
}
