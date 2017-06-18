package kr.pe.sinnori.common.seesionkey;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.AbstractRSAPublickeyGetter;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;

public class RSAPublickeyGetterBuilder extends AbstractRSAPublickeyGetter {

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class RSAPublickeyGetterHolder {
		static final AbstractRSAPublickeyGetter singleton = new RSAPublickeyGetterBuilder();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static AbstractRSAPublickeyGetter build() {
		return RSAPublickeyGetterHolder.singleton;
	}

	private RSAPublickeyGetterBuilder() {
	}

	protected byte[] getPublickeyBytesFromMainProjectServer() throws SymmetricException {
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ServerSessionkeyIF serverSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		return serverSessionkey.getDupPublicKeyBytes();
	}

	public byte[] getSubProjectPublickeyBytes(String subProjectName) throws SymmetricException {
		throw new SymmetricException("this function(=getSubProjectPublickeyBytes) dosn't be supported");
	}
}
