package kr.pe.sinnori.client.sessionkey;

import java.util.Hashtable;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientRSA;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKey;

public abstract class ClientSessionKeyManager {
	private static ClientSessionKeyIF mainClientSessionKey = null;	
	private static Hashtable<String, ClientSessionKeyIF> subClientSessionKeyHash = new Hashtable<String, ClientSessionKeyIF>();

	public static synchronized ClientSessionKeyIF getMainProjectClientSessionKey() throws SymmetricException {
		if (null == mainClientSessionKey) {			
			byte[] publicKeyBytes = ClientRSAPublickeyGetter.getMainProjectPublickeyBytes();
			mainClientSessionKey = getNewClientSessionKey(publicKeyBytes);
		}
		return mainClientSessionKey;
	}
	
	
	public static synchronized ClientSessionKeyIF getSubProjectClientSessionKey(String subProjectName) throws IllegalArgumentException, SymmetricException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		if (! sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration().isRegistedSubProjectName(subProjectName)) {				
			throw new IllegalArgumentException("the parameter subProjectName is not registered in the Sinnori config file");
		}
		
		ClientSessionKeyIF subClientSessionKey = subClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = ClientRSAPublickeyGetter.getSubProjectPublickeyBytes(subProjectName);
			subClientSessionKey = getNewClientSessionKey(publicKeyBytes);
			
			subClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		}
		 
		return subClientSessionKey;
	}
	
	
	public static ClientSessionKeyIF getNewClientSessionKey(byte[] publicKeyBytes) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		return new ClientSessionKey(new ClientRSA(publicKeyBytes));
	}	
}
