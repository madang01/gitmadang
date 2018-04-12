package kr.pe.sinnori.weblib.sessionkey;

import java.io.IOException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.AbstractRSAPublickeyGetter;
import kr.pe.sinnori.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.sinnori.impl.message.PublicKeyRes.PublicKeyRes;

public class RSAPublickeyGetterBuilder extends AbstractRSAPublickeyGetter {
	InternalLogger log = InternalLoggerFactory.getInstance(RSAPublickeyGetterBuilder.class);
	

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class RSAPublickeyGetterHolder {
		static final AbstractRSAPublickeyGetter singleton = new RSAPublickeyGetterBuilder();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static AbstractRSAPublickeyGetter build() {
		return RSAPublickeyGetterHolder.singleton;
	}

	private RSAPublickeyGetterBuilder() {
	}

	protected byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException {
		

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(mainProjectConnectionPool);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}

	public byte[] getSubProjectPublickeyBytes(String subProjectName) throws SymmetricException {
		AnyProjectConnectionPoolIF subProjectConnectionPoo = null;
		try {
			subProjectConnectionPoo = ConnectionPoolManager.getInstance().getSubProjectConnectionPool(subProjectName);
		} catch (IllegalStateException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(subProjectConnectionPoo);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}

	private byte[] getPublickeyFromServer(AnyProjectConnectionPoolIF anyProjectConnectionPool) throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {
		PublicKeyReq publicKeyReq = new PublicKeyReq();		

		AbstractMessage outObj = anyProjectConnectionPool.sendSyncInputMessage(publicKeyReq);
		PublicKeyRes publicKeyRes = (PublicKeyRes) outObj;

		return publicKeyRes.getPublicKeyBytes();
	}
}
