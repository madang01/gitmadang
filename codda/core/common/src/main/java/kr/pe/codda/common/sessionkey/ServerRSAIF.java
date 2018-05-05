package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

public interface ServerRSAIF {
	public byte[] getDupPublicKeyBytes();
	public byte[] decrypt(byte[] sessionKeyBytes) throws SymmetricException;
	public String getModulusHexStrForWeb();
}
