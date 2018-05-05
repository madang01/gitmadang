package kr.pe.codda.common.sessionkey;

import kr.pe.codda.common.exception.SymmetricException;

public interface ServerSessionkeyIF {
	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	public ServerSymmetricKeyIF getNewInstanceOfServerSymmetricKey(boolean isBase64, byte[] sessionkeyBytes, byte[] ivBytes) throws SymmetricException;
	public String getModulusHexStrForWeb();
	public byte[] getDupPublicKeyBytes();
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException;
	
}
