package kr.pe.sinnori.common.message.codec;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageDecoder {	
	protected Logger log = LoggerFactory.getLogger(AbstractMessageDecoder.class);
	
	/**
	 * <pre>
	 * "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 메시지를 반환한다.
	 * 그리고 이때 정상적인 경우  "중간 다리 역활 읽기 객체" 의 자원도 함께 반환한다. 
	 * 
	 * 파라미터 "단일항목 디코더" 는 "중간 다리 역활 읽기 객체"로 부터 프로토콜에 맞도록  항목 타입별로 디코더이다.
	 *   
	 * 파라미터 "중간 다리 역활 읽기 객체" 는 입력 스트림과 메시지 간에 중간자 역활을 하며 프로토콜 별로 달라지게 된다.
	 * (1) 예를 들면 DHB 프로토콜의 경우 "중간 다리 역활 읽기 객체" 없이 직접적으로 입력 스트림으로 부터 메시지를 추출한다.
	 * 이런 경우   "중간 다리 역활 읽기 객체" 는 그 자체로 입력 스트림이 된다.
	 * 디코딩 흐름도) 입력 스트림 -> 중간 다리 역활 읽기 객체 -> 메시지
	 * (2) 그리고 DJSON 프로토콜의 경우 "중간 다리 역활 읽기 객체" 는 입력 스트림으로부터 추출된 존슨 객체이다.
	 * 디코딩 흐름도) 입력 스트림 -> 존슨 객체 -> 메시지
	 * </pre> 
	 * @param singleItemDecoder 단일항목 디코더
	 * @param streamCharset 프로젝트 문자셋
	 * @param middleReadableObject  중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 메시지
	 * @throws Exception "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출할때 에러 발생시 던지는 예외
	 */
	public AbstractMessage decode(SingleItemDecoderIF singleItemDecoder, Charset streamCharset, Object middleReadableObject) throws OutOfMemoryError, BodyFormatException {
		/**
		 * <pre>
		 * 중간 다리 역활 읽기 객체는 입력 스트림과 입력 메시지 간에 중간자 역활을 하며 프로토콜 별로 달라지게 된다.
		 * 프로토콜에 따라서는 예를 들면 DHB 프로토콜의 경우 "중간 다리 역활 읽기 객체" 없이 직접적으로 입력 스트림으로 부터 입력 메시지를 추출한다.
		 * 이런 경우   "중간 다리 역활 읽기 객체" 는 그 자체로 입력 스트림이 된다.
		 * 흐름도) 입력 스트림 -> 중간 다리 역활 읽기 객체 -> 입력 메시지
		 * </pre>
		 */
		// Object middleReadObj =argv[0];
		AbstractMessage retObj = null;
		try {
			retObj = decodeBody(singleItemDecoder, streamCharset, middleReadableObject);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(BodyFormatException e) {
			throw e;
		} catch(Exception e) {
			String errorMessage = "unknown error::"+e.getMessage();
			log.warn(errorMessage, e);
			new BodyFormatException(errorMessage);
		}
		
		/**
		 * <pre>
		 * MiddleReadableObject 가 가진 자원 반환을 하는 장소는  2군데이다.
		 * 첫번째 장소는 메시지 추출 후 쓰임이 다해서 호출하는 AbstractMessageDecoder#decode 이며
		 * 두번째 장소는 2번 연속 호출해도 무방하기때문에 안전하게 자원 반환을 보장하기위한 Executor#run 이다.
		 * </pre>
		 */
		try {
			singleItemDecoder.closeReadableMiddleObjectWithValidCheck(middleReadableObject);
		} catch(BodyFormatException e) {
			log.warn("{}, 추출된 메시지=[{}]", e.getMessage(), retObj.toStringUsingReflection());
			throw e;
		}
		
		
		return retObj;
	}
	
	protected abstract AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset streamCharset, Object middleReadObj) throws OutOfMemoryError, BodyFormatException;
}