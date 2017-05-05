package kr.pe.sinnori.client.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientRSA;
import kr.pe.sinnori.common.sessionkey.ClientSessionKey;

public abstract class ClientSessionkeyUtil {
	public static ClientSessionKey getNewInstanceOfClientSessionkey(byte[] publicKeyBytes) throws SymmetricException {
		return new ClientSessionKey(new ClientRSA(publicKeyBytes));
	}
}
