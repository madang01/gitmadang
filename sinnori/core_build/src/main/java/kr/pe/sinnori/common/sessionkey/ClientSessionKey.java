package kr.pe.sinnori.common.sessionkey;

import java.util.Arrays;
import java.util.Random;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.exception.SymmetricException;

public class ClientSessionKey implements ClientSessionKeyIF {
	private ClientRSAIF clientRSAPublickey = null;
	private ClientSymmetricKeyIF clientSymmetricKey = null;
	
	private byte[] sessionKeyBytes = null;
	private byte[] ivBytes = null;
	
	public ClientSessionKey(ClientRSAIF clientRSA) throws SymmetricException {
		this.clientRSAPublickey = clientRSA;
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		
		int symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		
		// this.clientSymmetricKey = clientSymmetricKey;
		ivBytes = new byte[symmetricIVSize];
		Random random = new Random();
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
		return clientRSAPublickey.getDupPublicKeyBytes();
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
