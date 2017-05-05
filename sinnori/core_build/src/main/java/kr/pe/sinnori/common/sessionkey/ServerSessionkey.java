package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public class ServerSessionkey {
	ServerSymmetricKey serverSymmetricKey = null;
	ServerRSA serverRSA = null;
	
	
	public ServerSessionkey(ServerRSA serverRSA) {
		this.serverRSA = serverRSA;
	}
	
	public ServerSymmetricKey getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return new ServerSymmetricKey(serverRSA.decryptUsingPrivateKey(sessionkeyBytes), ivBytes);
	}	
		
	public String getModulusHexStrForWeb() {
		return serverRSA.getModulusHexStrForWeb();
	}
	
	public final byte[] getPublicKeyBytes() {
		return serverRSA.getPublicKeyBytes();
	}
}
