package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public class ServerSessionkey implements ServerSessionkeyIF {
	ServerSymmetricKeyIF serverSymmetricKey = null;
	ServerRSAIF serverRSA = null;
	
	
	public ServerSessionkey(ServerRSAIF serverRSA) {
		this.serverRSA = serverRSA;
	}
	
	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return new ServerSymmetricKey(serverRSA.getClientSymmetricKeyBytes(sessionkeyBytes), ivBytes);
	}	
		
	public String getModulusHexStrForWeb() {
		return serverRSA.getModulusHexStrForWeb();
	}
	
	public final byte[] getDupPublicKeyBytes() {
		return serverRSA.getDupPublicKeyBytes();
	}
}
