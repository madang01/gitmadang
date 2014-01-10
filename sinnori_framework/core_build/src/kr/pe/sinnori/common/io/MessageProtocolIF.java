package kr.pe.sinnori.common.io;

import java.nio.charset.Charset;
import java.util.ArrayList;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * 메시지 교환 프로토콜을 구현하는 스트림 변환기를 정의하는 인터페이스.
 * 
 * @author Jonghoon Won
 *
 */
public interface MessageProtocolIF {
	
	/**
	 * 메시지를 데이터 패킷 버퍼로 구현한 스트림으로 변환한다.
	 * @param messageObj 스트림으로 변환을 원하는 메시지
	 * @param clientCharset 문자셋
	 * @return 메시지 내용이 담긴 데이터 패킷 버퍼 목록
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼가 없을때 던지는 예외
	 * @throws BodyFormatException 바디 구성할때 에러 발생시 던지는 예외
	 */
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, Charset clientCharset) throws NoMoreDataPacketBufferException, BodyFormatException;
	
	
	/**
	 * <pre>
	 *  읽기 전용 버퍼 목록으로 부터 지정된 메시지 클래스를 추출하여 목록을 구성 후 반환한다.
	 * 역자주) 속도가 떨어지지만 코드 중복을 없애기 위해서 하나로 통합함.
	 * </pre>
	 * 
	 * @param targetClass 스트림으로 부터 추출을 원하는 메시지 클래스
	 * @param clientCharset 문자셋
	 * @param messageInputStreamResource 메시지 입력 스트림 자원
	 * @param messageManger 메시지 관리자
	 * @return 읽기 전용 버퍼 목록으로 부터 추출한 메시지 목록, IOMode 가 true이면 입력 메시지 목록, false 이면 출력 메시지 목록이 된다.
	 * @throws HeaderFormatException 헤더 포맷 구성시 에러 발생시 던지는 예외
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼가 없을때 던지는 예외
	 */
	public ArrayList<AbstractMessage> S2MList(Class<? extends AbstractMessage> targetClass, 
			Charset clientCharset,
			MessageInputStreamResourcePerSocket messageInputStreamResource, 
			MessageMangerIF messageManger) 
					throws HeaderFormatException, NoMoreDataPacketBufferException;
}

