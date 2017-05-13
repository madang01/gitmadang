package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public interface ServerSessionkeyIF {
	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	public String getModulusHexStrForWeb();
	public byte[] getDupPublicKeyBytes();
}
