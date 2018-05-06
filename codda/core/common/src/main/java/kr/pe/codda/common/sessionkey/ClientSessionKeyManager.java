package kr.pe.codda.common.sessionkey;

import java.util.concurrent.ConcurrentHashMap;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.exception.SymmetricException;

public final class ClientSessionKeyManager {
	private static ClientSessionKeyIF mainClientSessionKey = null;	
	private static ConcurrentHashMap<String, ClientSessionKeyIF> subProjectNameToClientSessionKeyHash = new ConcurrentHashMap<String, ClientSessionKeyIF>();
	
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
		
		CoddaConfiguration sinnoriRunningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		
		if (! sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration().isRegistedSubProjectName(subProjectName)) {				
			throw new IllegalArgumentException("the parameter subProjectName is not registered in the Sinnori config file");
		}
		
		ClientSessionKeyIF subClientSessionKey = subProjectNameToClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = clientRSAPublickeyGetter.getSubProjectPublickeyBytes(subProjectName);
			subClientSessionKey = buildNewSubClientSessionKey(subProjectName, publicKeyBytes);
		}
		 
		return subClientSessionKey;
	}
	
	private ClientSessionKeyIF buildNewSubClientSessionKey(String subProjectName, byte[] publicKeyBytes) throws SymmetricException {
		ClientSessionKeyIF subClientSessionKey = getNewClientSessionKey(publicKeyBytes);	
		subProjectNameToClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		return subClientSessionKey;
	}
	
	
	public ClientSessionKeyIF getNewClientSessionKey(byte[] publicKeyBytes) throws SymmetricException {
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		return new ClientSessionKey(new ClientRSA(publicKeyBytes));
	}	
}
