package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public interface ClientSymmetricKeyIF {
	public byte[] getSessionKeyBytes(ClientRSAIF clientRSA) throws SymmetricException;
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException;
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException;
}
