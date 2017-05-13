package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public abstract class ServerSessionkeyManager {
	// private static ServerSessionkeyManager severSessionkeyManger = null;
	
	private static ServerSessionkeyIF severSessionkey = null;
	
	public static synchronized ServerSessionkeyIF getInstance() throws SymmetricException {
		if (null == severSessionkey) {
			severSessionkey = new ServerSessionkey(new ServerRSA());
		}
		return severSessionkey;
	}
	
	
	/*public ServerSessionkey getServerSessionkey() {
		return severSessionkey;
	}*/
	
	/*public ServerSymmetricKey getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException {
		return severSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes, ivBytes);
	}*/
}
