package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

public interface ServerSymmetricKeyIF {
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException;
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException;
}
