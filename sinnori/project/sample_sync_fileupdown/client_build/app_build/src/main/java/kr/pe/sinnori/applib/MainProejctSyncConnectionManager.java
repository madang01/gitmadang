package kr.pe.sinnori.applib;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.AnyProjectClient;
import kr.pe.sinnori.client.MainClientManager;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

public class MainProejctSyncConnectionManager {
	private Logger log = LoggerFactory
			.getLogger(MainProejctSyncConnectionManager.class);

	
	private AbstractConnection conn = null;
	

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class MainProejctConnectionManagerHolder {
		static final MainProejctSyncConnectionManager singleton = new MainProejctSyncConnectionManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static MainProejctSyncConnectionManager getInstance() {
		return MainProejctConnectionManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private MainProejctSyncConnectionManager() {
		AnyProjectClient mainProjectClient = MainClientManager.getInstance().getMainProjectClient();
		
		try {
			conn = mainProjectClient.getConnection();
		} catch (InterruptedException | NotSupportedException | ConnectionTimeoutException e1) {
			log.error(e1.getMessage(), e1);
			System.exit(1);
		}
	}
	
	public boolean isConnected() {
		if (null == conn) {
			log.error("conn is null");
			System.exit(1);
		}
		return conn.isConnected();
	}
	
	
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage) throws SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException, ConnectionTimeoutException, InterruptedException {
		if (null == conn) {
			log.error("conn is null");
			System.exit(1);
		}
		return conn.sendSyncInputMessage(inputMessage);
	}
	
	public void closeConnection() {
		if (null == conn) {
			log.error("conn is null");
			// System.exit(1);
			return;
		}
		conn.serverClose();
	}
	
	public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException {
		if (null == conn) {
			log.error("conn is null");
			System.exit(1);
		}
		
		
		conn.changeServerAddress(newServerHost, newServerPort);
	}
	
	
	public void releaseConnection() throws NotSupportedException {
		if (null == conn) {
			log.error("conn is null");
			System.exit(1);
		}
		
		AnyProjectClient mainProjectClient = MainClientManager.getInstance().getMainProjectClient();
		mainProjectClient.releaseConnection(conn);
	}
}
