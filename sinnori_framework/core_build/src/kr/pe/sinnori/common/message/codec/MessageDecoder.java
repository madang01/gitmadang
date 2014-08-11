package kr.pe.sinnori.common.message.codec;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

public abstract class MessageDecoder implements CommonRootIF {	
	/**
	 * <pre>
	 * "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 메시지를 반환한다.
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
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj  중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 메시지
	 * @throws Exception "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출할때 에러 발생시 던지는 예외
	 */
	public AbstractMessage decode(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object middleReadObj) throws OutOfMemoryError, BodyFormatException {
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
			retObj = decodeBody(singleItemDecoder, charsetOfProject, middleReadObj);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(BodyFormatException e) {
			throw e;
		} catch(Exception e) {
			String errorMessage = "";
			log.warn(errorMessage);
			new BodyFormatException(errorMessage);
		}
		
		try {
			singleItemDecoder.finish(middleReadObj);
		} catch(BodyFormatException e) {
			log.warn("{}, 추출된 메시지=[{}]", e.getMessage(), retObj.toStringUsingReflection());
			throw e;
		}
		
		return retObj;
	}
	
	protected abstract AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object middleReadObj) throws OutOfMemoryError, BodyFormatException;
}