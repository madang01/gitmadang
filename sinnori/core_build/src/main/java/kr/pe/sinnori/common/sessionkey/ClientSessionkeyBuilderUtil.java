package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public abstract class ClientSessionkeyBuilderUtil {
	public static ClientSessionKey getNewInstanceOfClientSessionkey(byte[] publicKeyBytes) throws SymmetricException {
		return new ClientSessionKey(new ClientRSA(publicKeyBytes));
	}
	
}
