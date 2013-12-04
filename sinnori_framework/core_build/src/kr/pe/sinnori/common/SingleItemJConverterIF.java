package kr.pe.sinnori.common;

import java.nio.charset.Charset;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.ItemTypeManger;

import org.json.simple.JSONObject;

public interface SingleItemJConverterIF {
	/**
	 * <pre>
	 * 항목 정보와 항목의 값을 스트림에 저장한다. 
	 * 주) 메시지  값을 저장할때 값의 타입 정보와 항목의 타입 정보의 일치성 여부를 따져서 저장한다.
	 *     따라서 이곳에서는 값과 항목 타입의 일치성 여부를 검사하지 않는다.
	 * </pre>
	 * 
	 * @param itemName 항목 이름
	 * @param itemTypeID 항목 타입 식별자, 항목 타입 관리자{@link ItemTypeManger} 를 통해 관리 된다.
	 * @param itemValue 항목 값
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param sw 출력 스트림
	 * @throws BodyFormatException 항목 쓰기 실패시 던지는 예외
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 */
	public void I2S(String itemName, int itemTypeID, 
			Object itemValue, int itemSizeForLang, Charset itemCharsetForLang, JSONObject jsonObj)
			throws BodyFormatException, IllegalArgumentException;
	
	/**
	 * 항목정보를 바탕으로 스트림에서 항목의 값을 읽어 항목값 해쉬에 저장한다. 항목값 해쉬의 키는 항목명, 값은 항목의 값이다. 
	 * @param itemName 항목 이름
	 * @param itemTypeID 항목 타입 식별자, 항목 타입 관리자{@link ItemTypeManger} 를 통해 관리 된다.
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param itemValueHash 항목명 해쉬, 키는 항목명, 값은 항목의 값이다.
	 * @param sr 입력 스트림
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 * @throws BodyFormatException 항목 쓰기 실패시 던지는 예외
	 */
	public void S2I(String itemName, int itemTypeID, 
			int itemSizeForLang, Charset itemCharsetForLang, 
			HashMap<String, Object> itemValueHash, JSONObject jsonObj)
			throws IllegalArgumentException, BodyFormatException;
}
