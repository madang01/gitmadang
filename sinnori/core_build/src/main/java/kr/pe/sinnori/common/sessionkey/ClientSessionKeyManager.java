package kr.pe.sinnori.common.sessionkey;

import java.util.Hashtable;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.exception.SymmetricException;

public final class ClientSessionKeyManager {
	private static ClientSessionKeyIF mainClientSessionKey = null;	
	private static Hashtable<String, ClientSessionKeyIF> subProjectNameToClientSessionKeyHash = new Hashtable<String, ClientSessionKeyIF>();
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientSessionKeyManagerHolder {
		static final ClientSessionKeyManager singleton = new ClientSessionKeyManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ClientSessionKeyManager getInstance() {
		return ClientSessionKeyManagerHolder.singleton;
	}
	
	private ClientSessionKeyManager() {
		
	}

	public synchronized ClientSessionKeyIF getMainProjectClientSessionKey(AbstractRSAPublickeyGetter clientRSAPublickeyGetter) throws SymmetricException, InterruptedException {
		if (null == mainClientSessionKey) {
			
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getMainProjectPublickeyBytes();
			mainClientSessionKey = getNewClientSessionKey(publicKeyBytes);
		}
		return mainClientSessionKey;
	}
	
	public synchronized ClientSessionKeyIF getSubProjectClientSessionKey(String subProjectName, AbstractRSAPublickeyGetter clientRSAPublickeyGetter) throws IllegalArgumentException, SymmetricException {
		if (null == subProjectName) {
			throw new IllegalArgumentException("the parameter subProjectName is null");
		}
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		if (! sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration().isRegistedSubProjectName(subProjectName)) {				
			throw new IllegalArgumentException("the parameter subProjectName is not registered in the Sinnori config file");
		}
		
		ClientSessionKeyIF subClientSessionKey = subProjectNameToClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getSubProjectPublickeyBytes(subProjectName);
			subClientSessionKey = getNewClientSessionKey(publicKeyBytes);
			
			subProjectNameToClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		}
		 
		return subClientSessionKey;
	}
	
	
	public ClientSessionKeyIF getNewClientSessionKey(byte[] publicKeyBytes) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		return new ClientSessionKey(new ClientRSA(publicKeyBytes));
	}	
}