package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

public interface ClientRSAIF {
	public byte[] getDupPublicKeyBytes();
	public byte[] encrypt(byte sourceBytes[]) throws SymmetricException;
}
