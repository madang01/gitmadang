package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

/**
 * 서버에서 클라이언트가 보낸 세션키로 부터 생성되는 대칭키 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ServerSymmetricKeyIF {
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException;
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException;
}
