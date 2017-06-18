package kr.pe.sinnori.common.sessionkey;

public interface ClientSessionKeyIF {
	public ClientSymmetricKeyIF getClientSymmetricKey();
	public byte[] getDupSessionKeyBytes();
	public byte[] getDupPublicKeyBytes();
	
	public byte[] getDupIVBytes();
	
}
