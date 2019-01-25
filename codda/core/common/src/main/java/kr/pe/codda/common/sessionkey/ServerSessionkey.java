package kr.pe.codda.common.sessionkey;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.SymmetricException;

public class ServerSessionkey implements ServerSessionkeyIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerSessionkey.class);
	
	private ServerRSAIF serverRSA = null;
	private final java.util.Base64.Decoder base64Decoder = java.util.Base64.getMimeDecoder();
	
	
	public ServerSessionkey(ServerRSAIF serverRSA) {
		this.serverRSA = serverRSA;
	}

	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return this.getNewInstanceOfServerSymmetricKey(false, sessionkeyBytes, ivBytes);
	}
	
	/**
	 * 참고) 웹에서는 RAS 관련 javascript API 가 이진 데이터를 다룰 수 없기때문에 부득이 base64 인코딩하여 문자열로 만들어 사용하였다. 하여 대칭키를 얻고자 한다면 base64 디코딩 해야한다. 
	 */
	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(boolean isBase64, byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		// log.info("isBase64={}", isBase64);
		
		
		byte[] realSymmetricKeyBytes = null;
		
		if (isBase64) {
			byte[] base64EncodedStringBytes = serverRSA.decrypt(sessionkeyBytes);
			try {
				realSymmetricKeyBytes = base64Decoder.decode(base64EncodedStringBytes);
			} catch (Exception e) {
				String errorMessage = "fail to decode the parameter sessionkeyBytes using base64";
				
				log.warn(errorMessage, e);
				
				throw new SymmetricException(errorMessage);
			}
			
			// log.info("base64EncodedStringBytes={}", HexUtil.getHexStringFromByteArray(base64EncodedStringBytes));
			
		} else {
			realSymmetricKeyBytes = serverRSA.decrypt(sessionkeyBytes);
		}
		return new ServerSymmetricKey(realSymmetricKeyBytes, ivBytes);
	}	
		
	public String getModulusHexStrForWeb() {
		return serverRSA.getModulusHexStrForWeb();
	}
	
	public final byte[] getDupPublicKeyBytes() {
		return serverRSA.getDupPublicKeyBytes();
	}
	
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException {
		return serverRSA.decrypt(encryptedBytesWithPublicKey);
	}
}
