package kr.pe.sinnori.common.type;

/** 
 * <pre>
 * 메시지 전송 방향.
 * (1) FROM_NONE_TO_NONE : 메시지는 서버에서 클라이언트로 혹은 클라이언트에서 서버로 양쪽 모두에서 전송되지 않는다.
 * (2) FROM_SERVER_TO_CLINET : 메시지는 서버에서 클라이언트로만 전송된다.
 * (3) FROM_CLIENT_TO_SERVER : 메시지는 클라이언트에서 서버로만 전송된다.
 * (4) FROM_ALL_TO_ALL : 메시지는 서버에서 클라이언트로도 혹은 클라이언트에서 서버로 양쪽 모두에서 전송된다.
 * </pre> 
 */
public enum MessageTransferDirectionType {	
	FROM_NONE_TO_NONE,
	FROM_SERVER_TO_CLINET, 
	FROM_CLIENT_TO_SERVER,
	FROM_ALL_TO_ALL;
}
