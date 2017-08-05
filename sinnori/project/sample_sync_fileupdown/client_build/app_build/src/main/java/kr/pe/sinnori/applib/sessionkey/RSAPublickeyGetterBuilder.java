package kr.pe.sinnori.applib.sessionkey;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.applib.MainProejctSyncConnectionManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.AbstractRSAPublickeyGetter;
import kr.pe.sinnori.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.sinnori.impl.message.PublicKeyRes.PublicKeyRes;

public class RSAPublickeyGetterBuilder extends AbstractRSAPublickeyGetter {

	private final Object mainProjectPublicKeyMonitor = new Object();
	
	private byte[] mainProjectPublicKeyBytes = null;
	

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

	protected byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException, InterruptedException {
		Logger log = LoggerFactory.getLogger(RSAPublickeyGetterBuilder.class);

		synchronized (mainProjectPublicKeyMonitor) {
			if (null == mainProjectPublicKeyBytes) {
				MainProejctSyncConnectionManager mainProejctConnectionManager = MainProejctSyncConnectionManager.getInstance();

				PublicKeyReq publicKeyReq = new PublicKeyReq();
				PublicKeyRes publicKeyRes = null;
				AbstractMessage ouputMessage = null;

				try {
					ouputMessage = mainProejctConnectionManager.sendSyncInputMessage(publicKeyReq);
				} catch (SocketTimeoutException | ServerNotReadyException | NoMoreDataPacketBufferException
						| BodyFormatException | DynamicClassCallException | ServerTaskException | NotLoginException 
						| ConnectionTimeoutException  e) {
					String errorMessage = e.getMessage();
					log.warn(errorMessage, e);
					throw new SymmetricException(errorMessage);
				}

				if (!ouputMessage.getMessageID().equals("PublicKeyRes")) {
					String errorMessage = String.format(
							"expected message id PublicKeyRes but returned message id is not PublicKeyRes, ",
							ouputMessage.toString());
					throw new SymmetricException(errorMessage);
				}

				publicKeyRes = (PublicKeyRes) ouputMessage;

				mainProjectPublicKeyBytes = publicKeyRes.getPublicKeyBytes();
			}

			return mainProjectPublicKeyBytes;
		}
	}

	public byte[] getSubProjectPublickeyBytes(String subProjectName) throws SymmetricException {
		throw new SymmetricException("the sample_sync_fileupdown client applicaiton doesn't support this method becase of  no sub-project");
	}	
}
