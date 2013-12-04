package kr.pe.sinnori.common.io;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;

import org.json.simple.JSONObject;


/**
 * 프로토콜의 항목 타입 단일 항목 JSON Object 변환기 인터페이스.<br/>
 * 이 인터페이스는 프로토콜 그리고 데이터 타입별로 구현된다.<br/>
 * 이 인터페이스를 상속 받는 클래스는 입출력 스트림을 운영하여 프로토콜과 데이터 타입에 맞도록 구현한다.<br/>
 * 참고) 타입별 필요한 파라미터는 다르지만 동일 인터페이스를 갖기 위해서 모든 파라미터를 받는다.
 * 
 * @author Jonghoon Won
 *
 */
public interface SingleItemJConverterByTypeIF {

	/**
	 * 입력 JSON object 로부터 항목의 값을 읽어 온다.<br/>
	 * @param itemName 항목 이름
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param jsonObj 입력 JSON object
	 * @return 입력 스트림으로 부터 읽은 항목의 값
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 * @throws BodyFormatException 바디 구것시 에러 발생시 던지는 예외
	 */
	public Object getValue(String itemName, int itemSizeForLang,
			Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException;
	
	/**
	 * 출력 JSON object 에 항목의 값을 저장한다.
	 * @param itemName 항목 이름
	 * @param itemValue 항목 값 
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param jsonObj 출력 JSON object
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 */
	public void putValue(String itemName, Object itemValue,
			int itemSizeForLang, Charset itemCharsetForLang, JSONObject jsonObj)
			throws IllegalArgumentException;
	
	/**
	 * @return 항목 타입
	 */
	public String getItemType();

}
