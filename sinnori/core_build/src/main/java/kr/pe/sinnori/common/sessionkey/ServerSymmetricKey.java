package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.sinnori.common.exception.SymmetricException;

public class ServerSymmetricKey implements ServerSymmetricKeyIF {
private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
	
	private byte[] symmetricKeyBytes = null;
	private String symmetricKeyAlgorithm = null;
	private int symmetricKeySize;
	private byte ivBytes[] = null;
	
	public ServerSymmetricKey(byte[] symmetricKeyBytes, byte[] ivBytes) throws SymmetricException {
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("the paramter symmetricKeyBytes is null");
		}
		if (null == ivBytes) {
			throw new IllegalArgumentException("the paramter ivBytes is null");
		}
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		// symmetricKeyEncodingType = commonPart.getSymmetricKeyEncodingOfSessionKey();
		symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		
		// byte[] symmetricKeyBytes = serverRSA.decryptUsingPrivateKey(sessionkeyBytes);
		
		if (symmetricKeySize != symmetricKeyBytes.length) {
			new SymmetricException(
					String.format("the paramter sessionkeyBytes.length[%d] is differenct from symmetric key size[%d]", 
							symmetricKeyBytes.length, symmetricKeySize));
		}
		
		if (symmetricIVSize != ivBytes.length) {
			new SymmetricException(
					String.format("the paramter ivBytes.length[%d] is differenct from symmetric iv size[%d]", 
							ivBytes.length, symmetricIVSize));
		}
		this.symmetricKeyBytes = symmetricKeyBytes;
		this.ivBytes = ivBytes;
	}
	
	
	public byte[] encrypt(byte[] plainTextBytes) throws IllegalArgumentException, SymmetricException {
		if (null == symmetricKeyBytes) {
			new SymmetricException("sessionkey not setting");
		}
		return symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
	}
	
	public byte[] decrypt(byte[] encryptedBytes) throws IllegalArgumentException, SymmetricException {
		if (null == symmetricKeyBytes) {
			new SymmetricException("sessionkey not setting");
		}
		
		return symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
	}
}
