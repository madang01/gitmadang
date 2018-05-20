package kr.pe.codda.common.exception;


/**
 * 로그인 서비스등에서 서버 타스크에 대한 권한 검사시 권한 없을대 던지는 예외
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class ServerTaskPermissionException extends ServerTaskException {	
	public ServerTaskPermissionException(String errorMessage) {
		super(errorMessage);
	}
}
