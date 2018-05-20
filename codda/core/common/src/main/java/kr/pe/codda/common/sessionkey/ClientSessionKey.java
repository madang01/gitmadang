package kr.pe.codda.common.sessionkey;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.exception.SymmetricException;

public class ClientSessionKey implements ClientSessionKeyIF {
	private ClientRSAIF clientRSA = null;
	private ClientSymmetricKeyIF clientSymmetricKey = null;
	
	private byte[] sessionKeyBytes = null;
	private byte[] ivBytes = null;
	
	public ClientSessionKey(ClientRSAIF clientRSA) throws SymmetricException {
		this.clientRSA = clientRSA;
		
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		
		int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		
		// this.clientSymmetricKey = clientSymmetricKey;
		ivBytes = new byte[symmetricIVSize];
		SecureRandom random = new SecureRandom();
		random.setSeed(new Date().getTime());
		random.nextBytes(ivBytes);
		
		clientSymmetricKey = new ClientSymmetricKey(ivBytes);
		
		this.sessionKeyBytes = clientSymmetricKey.getSessionKeyBytes(clientRSA);
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
