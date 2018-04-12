package kr.pe.sinnori.common.sessionkey;

import java.security.SecureRandom;
import java.util.Date;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.sinnori.common.exception.SymmetricException;

public class ClientSymmetricKey implements ClientSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private byte[] symmetricKeyBytes = null;
	private String symmetricKeyAlgorithm = null;
	private int symmetricKeySize;
	private byte[] ivBytes = null;
	
	public ClientSymmetricKey(byte ivBytes[]) throws SymmetricException {
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		symmetricKeyBytes = new byte[symmetricKeySize];
		SecureRandom random = new SecureRandom();
		random.setSeed(new Date().getTime() ^ 0xff11ff11fff33fffL);
		random.nextBytes(symmetricKeyBytes);
		this.ivBytes = ivBytes;
	}
	
	
	public byte[] getSessionKeyBytes(ClientRSAIF clientRSA) throws SymmetricException {
		return clientRSA.encrypt(symmetricKeyBytes);
	}
	
		
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
}
