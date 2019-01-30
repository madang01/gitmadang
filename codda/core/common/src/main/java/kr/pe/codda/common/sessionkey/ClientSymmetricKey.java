package kr.pe.codda.common.sessionkey;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.exception.SymmetricException;

public class ClientSymmetricKey implements ClientSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	private Encoder base64Encoder = Base64.getEncoder();
	
	private byte[] symmetricKeyBytes = null;
	private String symmetricKeyAlgorithm = null;
	private int symmetricKeySize;
	private byte[] ivBytes = null;
	
	public ClientSymmetricKey(byte ivBytes[]) throws SymmetricException {
		if (null == ivBytes) {
			throw new IllegalArgumentException("the parameter ivBytes is null");
		}
		
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		symmetricKeyBytes = new byte[symmetricKeySize];
		SecureRandom random = new SecureRandom();
		random.setSeed(new Date().getTime() ^ 0xff11ff11fff33fffL);
		random.nextBytes(symmetricKeyBytes);
		this.ivBytes = ivBytes;
	}
	
	
	public byte[] getSessionKeyBytes(ClientRSAIF clientRSA, boolean isBase64) throws SymmetricException {
		if (isBase64) {
			return clientRSA.encrypt(base64Encoder.encode(symmetricKeyBytes));
		} else {
			return clientRSA.encrypt(symmetricKeyBytes);
		}
	}
	
		
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
}
