package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class ProjectLoginManager implements ProjectLoginManagerIF {
	private final Object monitor = new Object();
	
	private HashMap<SocketChannel, String> socketChannel2LogingIDHash 
	= new HashMap<SocketChannel, String>(); 
	
	private HashMap<String, SocketChannel> logingID2SocketChannelDHash 
	= new HashMap<String, SocketChannel>(); 
	
	
	private ProjectLoginManager() {
		/*this.projectName = projectName;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;*/
	}
	
	public static class Builder {
		public static ProjectLoginManager build() {
            return new ProjectLoginManager();
        }
	}

	@Override
	public void registerloginUser(SocketChannel sc, String loginID) {
		synchronized (monitor) {						
			socketChannel2LogingIDHash.put(sc, loginID);
			logingID2SocketChannelDHash.put(loginID, sc);
		}
	}

	public void removeLoginUser(String loginID) {
		synchronized (monitor) {
			SocketChannel sc = logingID2SocketChannelDHash.get(loginID);
			if (null != sc) {
				logingID2SocketChannelDHash.remove(loginID);
				socketChannel2LogingIDHash.remove(sc);
			}
		}
	}
	
	public void removeLoginUser(SocketChannel sc) {
		synchronized (monitor) {
			String loginID = socketChannel2LogingIDHash.get(sc);
			if (null != loginID) {
				logingID2SocketChannelDHash.remove(loginID);
				socketChannel2LogingIDHash.remove(sc);
			}
		}
	}
	
	@Override
	public boolean isLogin(String loginID) {
		synchronized (monitor) {
			SocketChannel sc = logingID2SocketChannelDHash.get(loginID);			
			return (null != sc);
		}
	}

	@Override
	public boolean isLogin(SocketChannel sc) {
		synchronized (monitor) {
			String loginID = socketChannel2LogingIDHash.get(sc);			
			return (null == loginID);
		}
	}
	
}
