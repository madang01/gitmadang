package kr.pe.sinnori.weblib.sessionkey;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.AnyProjectClient;
import kr.pe.sinnori.client.MainClientManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.AbstractRSAPublickeyGetter;
import kr.pe.sinnori.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.sinnori.impl.message.PublicKeyRes.PublicKeyRes;

public class RSAPublickeyGetterBuilder extends AbstractRSAPublickeyGetter {

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
		Logger log = LoggerFactory.getLogger(RSAPublickeyGetterBuilder.class);

		AnyProjectClient mainClientProject = MainClientManager.getInstance().getMainProjectClient();

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(mainClientProject);
		} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
				| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}

	protected byte[] getSubProjectPublickeyBytes(String subProjectName) throws SymmetricException {
		Logger log = LoggerFactory.getLogger(RSAPublickeyGetterBuilder.class);

		AnyProjectClient subClientProject = null;
		try {
			subClientProject = MainClientManager.getInstance().getSubProjectClient(subProjectName);
		} catch (NotFoundProjectException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		byte[] publicKeyBytes = null;
		try {
			publicKeyBytes = getPublickeyFromServer(subClientProject);
		} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
				| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return publicKeyBytes;
	}

	private byte[] getPublickeyFromServer(AnyProjectClient clientProject)
			throws SymmetricException, SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		PublicKeyReq publicKeyReq = new PublicKeyReq();		

		AbstractMessage outObj = clientProject.sendSyncInputMessage(publicKeyReq);
		PublicKeyRes publicKeyRes = (PublicKeyRes) outObj;

		return publicKeyRes.getPublicKeyBytes();
	}
}
