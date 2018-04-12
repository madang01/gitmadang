package kr.pe.sinnori.common.protocol.djson;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.json.simple.JSONObject;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;
import kr.pe.sinnori.common.util.HexUtil;

public class DJSONSingleItemDecoderMatcher implements DJSONSingleItemDecoderMatcherIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(DJSONSingleItemDecoderMatcher.class);
	
	private Charset streamCharset = null;
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder = null;
	@SuppressWarnings("unused")
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	@SuppressWarnings("unused")
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractDJSONSingleItemDecoder[] djsonSingleItemDecoderList = new AbstractDJSONSingleItemDecoder[] { 
			new DJSONSelfExnErrorPlaceSingleItemDecoder(), new DJSONSelfExnErrorTypeSingleItemDecoder(),
			new DJSONByteSingleItemDecoder(), new DJSONUnsignedByteSingleItemDecoder(), 
			new DJSONShortSingleItemDecoder(), new DJSONUnsignedShortSingleItemDecoder(),
			new DJSONIntSingleItemDecoder(), new DJSONUnsignedIntSingleItemDecoder(), 
			new DJSONLongSingleItemDecoder(), new DJSONUBPascalStringSingleItemDecoder(),
			new DJSONUSPascalStringSingleItemDecoder(), new DJSONSIPascalStringSingleItemDecoder(), 
			new DJSONFixedLengthStringSingleItemDecoder(), new DJSONUBVariableLengthBytesSingleItemDecoder(), 
			new DJSONUSVariableLengthBytesSingleItemDecoder(), new DJSONSIVariableLengthBytesSingleItemDecoder(), 
			new DJSONFixedLengthBytesSingleItemDecoder(), 
			new DJSONJavaSqlDateSingleItemDecoder(), new DJSONJavaSqlTimestampSingleItemDecoder(),
			new DJSONBooleanSingleItemDecoder()
	};
	
	
	public DJSONSingleItemDecoderMatcher(CharsetDecoder streamCharsetDecoder) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.streamCodingErrorActionOnMalformedInput = streamCharsetDecoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetDecoder.unmappableCharacterAction();
		
		checkValidDJSONTypeSingleItemDecoderList();
	}
	
	
	private void checkValidDJSONTypeSingleItemDecoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (djsonSingleItemDecoderList.length != singleItemTypes.length) {
			log.error("the var djsonTypeSingleItemDecoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					djsonSingleItemDecoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = djsonSingleItemDecoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var djsonTypeSingleItemDecoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}

	private final class DJSONSelfExnErrorPlaceSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < Byte.MIN_VALUE || tempItemValue > Byte.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값[%d]이 byte 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			byte value = (byte) tempItemValue;
			
			SelfExn.ErrorPlace errorPlace = SelfExn.ErrorPlace.valueOf(value); 
			
			return errorPlace;
		}
		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_PLACE;
		}
	}
	
	private final class DJSONSelfExnErrorTypeSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < Byte.MIN_VALUE || tempItemValue > Byte.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값[%d]이 byte 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			byte value = (byte) tempItemValue;
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(value); 
			
			return errorType;
		}
		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_TYPE;
		}
	}
	
	
	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONByteSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < Byte.MIN_VALUE || tempItemValue > Byte.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값[%d]이 byte 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			byte value = (byte) tempItemValue;
			
			return value;
		}
		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** DJSON 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedByteSingleItemDecoder extends AbstractDJSONSingleItemDecoder {

		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값[%d]이 unsigned byte 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tempItemValue;
			
			return value;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** DJSON 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONShortSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < Short.MIN_VALUE || tempItemValue > Short.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값[%d]이 short 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tempItemValue;
			
			return value;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** DJSON 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedShortSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값[%d]이 unsigned short 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tempItemValue;
			
			return value;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** DJSON 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONIntSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < Integer.MIN_VALUE || tempItemValue > Integer.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값[%d]이 integer 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tempItemValue;
			
			return value;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** DJSON 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedIntSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue; 
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값[%d]이 unsigned integer 값 범위를 벗어났습니다.", 
								itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			return tempItemValue;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** DJSON 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONLongSingleItemDecoder extends AbstractDJSONSingleItemDecoder {		
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 long 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 long 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tempItemValue = (long)jsonValue;
			
			return tempItemValue;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** DJSON 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBPascalStringSingleItemDecoder extends AbstractDJSONSingleItemDecoder {		
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			
			String tempItemValue = (String)jsonValue;
			byte[] valueBytes = null;
			if (null == itemCharset) {
				valueBytes = tempItemValue.getBytes(streamCharset);
			} else {
				valueBytes = tempItemValue.getBytes(itemCharset);
			}
			
			
			if (valueBytes.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 문자열 크기[%d]가 unsinged byte 범위를 넘어섰습니다. 참고) 프로젝트 문자셋[%s]", 
								itemName, valueBytes.length, streamCharset.name());
				throw new BodyFormatException(errorMessage);
			}
			
			return tempItemValue;
			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSPascalStringSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			String tempItemValue = (String)jsonValue;
			byte[] valueBytes = null;
			if (null == itemCharset) {
				valueBytes = tempItemValue.getBytes(streamCharset);
			} else {
				valueBytes = tempItemValue.getBytes(itemCharset);
			}
			
			
			if (valueBytes.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USPascalString 타입 항목[%s]의 문자열 크기[%d]가 unsinged short 범위를 넘어섰습니다. 참고) 프로젝트 문자셋[%s]", 
								itemName, valueBytes.length, streamCharset.name());
				throw new BodyFormatException(errorMessage);
			}
			
			return tempItemValue;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIPascalStringSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			/*String tempItemValue = (String)jsonValue;
			return tempItemValue;*/
			return jsonValue;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthStringSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			String tempItemValue = (String)jsonValue;
			byte[] valueBytes = null;
			if (null == itemCharset) {
				valueBytes = tempItemValue.getBytes(streamCharset);
			} else {
				valueBytes = tempItemValue.getBytes(itemCharset);
			}
			
			if (valueBytes.length > itemSize) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthString 타입 항목[%s]의 문자열 크기[%d]가 지정한 크기[%d]를 넘어섰습니다. 참고) 프로젝트 문자셋[%s]", 
								itemName, valueBytes.length, itemSize, (null == itemCharset) ? streamCharset.name() : itemCharset.name());
				throw new BodyFormatException(errorMessage);
			}
			
			return tempItemValue;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** DJSON 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBVariableLengthBytesSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] returnValue = null;
			String tempItemValue = (String)jsonValue;
			
			if (tempItemValue.isEmpty()) {
				returnValue = new byte[0];
			} else {
				try {
					returnValue = HexUtil.getByteArrayFromHexString(tempItemValue);
					
					if (returnValue.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
						String errorMessage = 
								String.format("UBVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned byte 최대값을 넘었습니다.", 
										itemName, returnValue.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tempItemValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return returnValue;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** DJSON 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSVariableLengthBytesSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 USVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] returnValue = null;
			String tempItemValue = (String)jsonValue;
			
			if (tempItemValue.isEmpty()) {
				returnValue = new byte[0];
			} else {
				try {
					returnValue = HexUtil.getByteArrayFromHexString(tempItemValue);
					
					if (returnValue.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
						String errorMessage = 
								String.format("USVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned short 최대값을 넘었습니다.", 
										itemName, returnValue.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tempItemValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return returnValue;
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DJSON 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIVariableLengthBytesSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 SIVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] returnValue = null;
			String tempItemValue = (String)jsonValue;
			
			if (tempItemValue.isEmpty()) {
				returnValue = new byte[0];
			} else {
				try {
					returnValue = HexUtil.getByteArrayFromHexString(tempItemValue);
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tempItemValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return returnValue;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DJSON 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthBytesSingleItemDecoder extends AbstractDJSONSingleItemDecoder {
		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream)
				throws Exception {
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tempItemValue = (String)jsonValue;
			
			if (tempItemValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.getByteArrayFromHexString(tempItemValue);
					
					if (value.length != itemSize) {
						throw new IllegalArgumentException(
								String.format(
										"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
										value.length, itemSize));
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tempItemValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}

	/** DJSON 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONJavaSqlDateSingleItemDecoder extends AbstractDJSONSingleItemDecoder {

		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset,
				JSONObject jsonObjForInputStream) throws Exception {			
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 java sql date 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 java sql date 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long javaSqlDateLongValue = (long)jsonValue;
			return new java.sql.Date(javaSqlDateLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** DJSON 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONJavaSqlTimestampSingleItemDecoder extends AbstractDJSONSingleItemDecoder {

		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset,
				JSONObject jsonObjForInputStream) throws Exception {			
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 java sql timestamp 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 java sql timestamp 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long javaSqlTimestampLongValue = (long)jsonValue;
			return new java.sql.Timestamp(javaSqlTimestampLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** DJSON 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONBooleanSingleItemDecoder extends AbstractDJSONSingleItemDecoder {

		@Override
		public Object getValue(String itemName, int itemSize,
				Charset itemCharset,
				JSONObject jsonObjForInputStream) throws Exception {			
			Object jsonValue = jsonObjForInputStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 boolean 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjForInputStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 boolean 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			
			
			String tempItemValue = (String)jsonValue;
			
			if (tempItemValue.equals("true")) {
				return true;
			} else if (tempItemValue.equals("false")) {
				return false;
			} else {
				String errorMessage = 
						String.format("JSON Object 에서 boolean 타입의 값은  문자열 true, false 를 갖습니다." +
								"%sJSON Object 로 부터 얻은 boolean 타입 항목[%s]의 값[%s]이 잘못되었습니다.", 
								CommonStaticFinalVars.NEWLINE, itemName, tempItemValue);
				throw new BodyFormatException(errorMessage);
			}			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	
	public AbstractDJSONSingleItemDecoder get(int itemTypeID) {
		return djsonSingleItemDecoderList[itemTypeID];
	}
}
