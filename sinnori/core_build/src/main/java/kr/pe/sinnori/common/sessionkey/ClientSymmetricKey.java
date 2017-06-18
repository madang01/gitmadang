package kr.pe.sinnori.common.sessionkey;

import java.util.Date;
import java.util.Random;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.exception.SymmetricException;

public class ClientSymmetricKey implements ClientSymmetricKeyIF {
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private byte[] symmetricKeyBytes = null;
	private String symmetricKeyAlgorithm = null;
	// private int symmetricIVSize;
	// private CommonType.SYMMETRIC_KEY_ENCODING_TYPE symmetricKeyEncodingType = null;
	private int symmetricKeySize;
	private byte[] ivBytes = null;
	
	public ClientSymmetricKey(byte ivBytes[]) throws SymmetricException {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		// symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		// symmetricKeyEncodingType = commonPart.getSymmetricKeyEncodingOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		symmetricKeyBytes = new byte[symmetricKeySize];
		Random random = new Random();
		random.setSeed(new Date().getTime());
		random.nextBytes(symmetricKeyBytes);
		this.ivBytes = ivBytes;
	}
	
	/*public byte[] getSymmetricKeyBytes() {
		return symmetricKeyBytes;
	}*/
	
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
