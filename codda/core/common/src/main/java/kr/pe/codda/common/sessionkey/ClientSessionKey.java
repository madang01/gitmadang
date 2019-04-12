package kr.pe.codda.common.sessionkey;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.exception.SymmetricException;

public class ClientSessionKey implements ClientSessionKeyIF {
	private ClientRSAIF clientRSA = null;
	private ClientSymmetricKeyIF clientSymmetricKey = null;
	
	private byte[] sessionKeyBytes = null;
	private byte[] ivBytes = null;
	
	public ClientSessionKey(ClientRSAIF clientRSA, boolean isBase64) throws SymmetricException {
		this.clientRSA = clientRSA;
		
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		
		int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		
		// this.clientSymmetricKey = clientSymmetricKey;
		ivBytes = new byte[symmetricIVSize];
		
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		    /** dead code */
			String errorMesssage = "fail to create a instance of SecureRandom class";
			throw new SymmetricException(errorMesssage);
		}

		random.nextBytes(ivBytes);
		
		clientSymmetricKey = new ClientSymmetricKey(ivBytes);
		
		this.sessionKeyBytes = clientSymmetricKey.getSessionKeyBytes(clientRSA, isBase64);
	}
	
	public ClientSymmetricKeyIF getClientSymmetricKey() {
		return clientSymmetricKey;
	}

	public final byte[] getDupSessionKeyBytes() {
		return Arrays.copyOf(sessionKeyBytes, sessionKeyBytes.length);
	}
	
	public byte[] getDupPublicKeyBytes() {
		return clientRSA.getDupPublicKeyBytes();
	}
	
	
	public byte[] getDupIVBytes() {
		return Arrays.copyOf(ivBytes, ivBytes.length);
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSessionKey [sessionKeyBytes=");
		builder.append(Arrays.toString(sessionKeyBytes));
		builder.append(", ivBytes=");
		builder.append(Arrays.toString(ivBytes));
		builder.append("]");
		return builder.toString();
	}
}
