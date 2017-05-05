package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public interface ClientRSAIF {
	public byte[] getDupPublicKeyBytes();
	public byte[] encrypt(byte sourceBytes[]) throws SymmetricException;
}
