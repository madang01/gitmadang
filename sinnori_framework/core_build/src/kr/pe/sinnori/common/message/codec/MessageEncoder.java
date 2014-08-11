package kr.pe.sinnori.common.message.codec;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

public abstract class MessageEncoder {
	/**
	 * <pre>
	 * "메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param messageObj 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	abstract public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception;
}
