package kr.pe.sinnori.client.sessionkey;

import java.util.Hashtable;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientSessionKey;
import kr.pe.sinnori.common.sessionkey.ClientSessionkeyBuilderUtil;

public abstract class ClientSessionKeyBuilder {
	private static ClientSessionKey mainClientSessionKey = null;	
	private static Hashtable<String, ClientSessionKey> subClientSessionKeyHash = new Hashtable<String, ClientSessionKey>();

	public static synchronized ClientSessionKey build() throws SymmetricException {
		if (null == mainClientSessionKey) {
			byte[] publicKeyBytes = null;

			SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
					.getSinnoriRunningProjectConfiguration();
			CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

			CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY rsaKeyPairSoureOfSessionkey = commonPart
					.getRsaKeypairSourceOfSessionKey();

			if (rsaKeyPairSoureOfSessionkey.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER)) {
				publicKeyBytes = ClientRSAPublickeyUtil.getPublickeyFromMainProjectServer();
			} else if (rsaKeyPairSoureOfSessionkey.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.FILE)) {
				publicKeyBytes = ClientRSAPublickeyUtil.getPublickeyFromFile();
			} else {
				new SymmetricException(new StringBuilder("unknown rsa keypair source[")
						.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
			}

			mainClientSessionKey = ClientSessionkeyBuilderUtil.getNewInstanceOfClientSessionkey(publicKeyBytes);
		}
		return mainClientSessionKey;
	}
	
	/**
	 * @param subProjectName 공개키를 제공할 서버 정보를 갖는 서브 프로젝트의 이름
	 * @return 서브 프로젝트 서버에 접속하여 얻은 공개키를 갖는 클라이언트 세션키를 반환한다.
	 * @throws SymmetricException
	 */
	public static synchronized ClientSessionKey buildForSubProject(String subProjectName) throws SymmetricException {		
		ClientSessionKey subClientSessionKey = subClientSessionKeyHash.get(subProjectName);
		
		if (null == subClientSessionKey) {
			byte[] publicKeyBytes = ClientRSAPublickeyUtil.getPublickeyFromSubProjectServer(subProjectName);
			subClientSessionKey = ClientSessionkeyBuilderUtil.getNewInstanceOfClientSessionkey(publicKeyBytes);
			
			subClientSessionKeyHash.put(subProjectName, subClientSessionKey);
		}
		 
		return subClientSessionKey;
	}
}
