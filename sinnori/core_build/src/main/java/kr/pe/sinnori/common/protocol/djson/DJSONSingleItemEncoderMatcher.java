package kr.pe.sinnori.common.protocol.djson;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import org.json.simple.JSONObject;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;
import kr.pe.sinnori.common.util.HexUtil;

public class DJSONSingleItemEncoderMatcher implements DJSONSingleItemEncoderMatcherIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(DJSONSingleItemEncoderMatcher.class);
	
	private CharsetEncoder streamCharsetEncoder = null;
	private Charset streamCharset = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractDJSONSingleItemEncoder[] djsonSingleItemEncoderList = new AbstractDJSONSingleItemEncoder[] {
			new DJSONSelfExnErrorPlaceSingleItemEncoder(), new DJSONSelfExnErrorTypeSingleItemEncoder(),
			new DJSONByteSingleItemEncoder(), new DJSONUnsignedByteSingleItemEncoder(), 
			new DJSONShortSingleItemEncoder(), new DJSONUnsignedShortSingleItemEncoder(),
			new DJSONIntSingleItemEncoder(), new DJSONUnsignedIntSingleItemEncoder(), 
			new DJSONLongSingleItemEncoder(), new DJSONUBPascalStringSingleItemEncoder(),
			new DJSONUSPascalStringSingleItemEncoder(), new DJSONSIPascalStringSingleItemEncoder(), 
			new DJSONFixedLengthStringSingleItemEncoder(), new DJSONUBVariableLengthBytesSingleItemEncoder(), 
			new DJSONUSVariableLengthBytesSingleItemEncoder(), new DJSONSIVariableLengthBytesSingleItemEncoder(), 
			new DJSONFixedLengthBytesSingleItemEncoder(), 
			new DJSONJavaSqlDateSingleItemEncoder(), new DJSONJavaSqlTimestampSingleItemEncoder(),
			new DJSONBooleanSingleItemEncoder()
	};
	
	public DJSONSingleItemEncoderMatcher(CharsetEncoder streamCharsetEncoder) {
		this.streamCharsetEncoder = streamCharsetEncoder;
		
		streamCharset = streamCharsetEncoder.charset();
		streamCodingErrorActionOnMalformedInput = streamCharsetEncoder.malformedInputAction();
		streamCodingErrorActionOnUnmappableCharacter = streamCharsetEncoder.malformedInputAction();
		
		checkValidDJSONSingleItemEncoderList();
	}

	private void checkValidDJSONSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (djsonSingleItemEncoderList.length != singleItemTypes.length) {
			log.error("the var djsonSingleItemEncoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					djsonSingleItemEncoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = djsonSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var djsonSingleItemEncoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	
	private final class DJSONSelfExnErrorPlaceSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof SelfExn.ErrorPlace)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 SelfExn.ErrorPlcae 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			SelfExn.ErrorPlace errorPalce = (SelfExn.ErrorPlace)itemValue;
			
			jsonObjForOutputStream.put(itemName, errorPalce.getErrorPlaceByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_PLACE;
		}
	}
	
	private final class DJSONSelfExnErrorTypeSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof SelfExn.ErrorType)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 SelfExn.ErrorType 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			SelfExn.ErrorType errorType = (SelfExn.ErrorType)itemValue;
			
			jsonObjForOutputStream.put(itemName, errorType.getErrorTypeByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_TYPE;
		}
	}
	
	
	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONByteSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Byte)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Byte 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** DJSON 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedByteSingleItemEncoder extends AbstractDJSONSingleItemEncoder {

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Short)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Short 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Short tempItemValue = (Short) itemValue;
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Byte 범위가 아닙니다.", 
								tempItemValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** DJSON 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONShortSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Short)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Short 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** DJSON 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedShortSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Integer)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Integer 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Integer tempItemValue = (Integer) itemValue;
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Short 범위가 아닙니다.", 
								tempItemValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** DJSON 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONIntSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Integer)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Integer 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** DJSON 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedIntSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Long)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Long 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Long tempItemValue = (Long) itemValue;
			if (tempItemValue < 0 || tempItemValue > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Integer 범위가 아닙니다.", 
								tempItemValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** DJSON 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONLongSingleItemEncoder extends AbstractDJSONSingleItemEncoder {		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Long)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Long 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** DJSON 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBPascalStringSingleItemEncoder extends AbstractDJSONSingleItemEncoder {		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			String tempItemValue = (String) itemValue;
			
			byte[] valueBytes = null;
			
			if (null == itemCharset) {
				valueBytes = tempItemValue.getBytes(streamCharset);
			} else {
				valueBytes = tempItemValue.getBytes(itemCharset);
			}
			
			
			if (valueBytes.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = 
						String.format("UBPascalString 타입 항목의 값을 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned byte 최대값[%d]을 넘었습니다.", 
								DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSPascalStringSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			String tempItemValue = (String) itemValue;
			
			byte[] valueBytes = null;
			if (null == itemCharset) {
				valueBytes = tempItemValue.getBytes(streamCharset);
			} else {
				valueBytes = tempItemValue.getBytes(itemCharset);
			}
			if (valueBytes.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = 
						String.format("UBPascalString 타입 항목의 값을 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned short 최대값[%d]을 넘었습니다.", 
								DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIPascalStringSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObjForOutputStream.put(itemName, itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** DJSON 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthStringSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws Exception {
			String tempItemValue = null;
			
			if (null == itemValue) tempItemValue = "";
			else {
				if (!(itemValue instanceof String)) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				tempItemValue = (String) itemValue;
			}
			
			
			ByteBuffer outputBuffer = null;
			
			try {
				outputBuffer = ByteBuffer.allocate(itemSize);
			} catch(OutOfMemoryError e) {
				log.warn("OutOfMemoryError", e);
				throw e;
			}
			
			/** 고정 크기 출력 스트림 */
			FixedSizeOutputStream fsos = null;
			
			fsos = new FixedSizeOutputStream(outputBuffer, streamCharsetEncoder);
			
			if (null == itemCharset) {
				itemCharset = streamCharset;
				fsos.putFixedLengthString(itemSize, tempItemValue);
			} else {
				CharsetEncoder itemCharsetEncoder = itemCharset.newEncoder();
				itemCharsetEncoder
						.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				itemCharsetEncoder
						.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				
				// fsos = new FixedSizeOutputStream(outputBuffer, itemCharsetEncoder);
				fsos.putFixedLengthString(itemSize, tempItemValue, itemCharsetEncoder);
			}
			
			outputBuffer.flip();
			
			jsonObjForOutputStream.put(itemName, new String(outputBuffer.array(), itemCharset));

		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** DJSON 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBVariableLengthBytesSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			byte tempItemValue[] = null;
			
			if (null == itemValue) {
				tempItemValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tempItemValue = (byte[]) itemValue;
				
				if (tempItemValue.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
					String errorMessage = 
							String.format("ub variable length byte[] 타입 항목의 길이[%d]가 unsigned byte 최대값을 넘었습니다.",  tempItemValue.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			jsonObjForOutputStream.put(itemName, HexUtil.getHexStringFromByteArray(tempItemValue));
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** DJSON 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSVariableLengthBytesSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			byte tempItemValue[] = null;
			
			if (null == itemValue) {
				tempItemValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tempItemValue = (byte[]) itemValue;
				
				if (tempItemValue.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
					String errorMessage = 
							String.format("ub variable length byte[] 타입 항목의 길이[%d]가 unsigned short 최대값을 넘었습니다.",  tempItemValue.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			jsonObjForOutputStream.put(itemName, HexUtil.getHexStringFromByteArray(tempItemValue));
		}
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DJSON 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIVariableLengthBytesSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			byte tempItemValue[] = null;
			
			if (null == itemValue) {
				tempItemValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tempItemValue = (byte[]) itemValue;
			}
			
			jsonObjForOutputStream.put(itemName, HexUtil.getHexStringFromByteArray(tempItemValue));
		}
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DJSON 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthBytesSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws IllegalArgumentException {
			byte tempItemValue[] = null;
			
			if (null == itemValue) {
				tempItemValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tempItemValue = (byte[]) itemValue;
				
				if (tempItemValue.length != itemSize) {
					throw new IllegalArgumentException(
							String.format(
									"바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									tempItemValue.length, itemSize));
				}
			}
			jsonObjForOutputStream.put(itemName, HexUtil.getHexStringFromByteArray(tempItemValue));
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** DJSON 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DJSONJavaSqlDateSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue,
				int itemSize, Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws Exception {
			if (null == itemValue) {
				String errorMessage = "항목의 값이 null 입니다.";
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (!(itemValue instanceof java.sql.Date)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 java.sql.Date 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			java.sql.Date javaSqlDateValue = (java.sql.Date)itemValue;
			long javaSqlDateLongValue = javaSqlDateValue.getTime();			
			jsonObjForOutputStream.put(itemName, javaSqlDateLongValue);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** DJSON 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DJSONJavaSqlTimestampSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue,
				int itemSize, Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws Exception {
			if (null == itemValue) {
				String errorMessage = "항목의 값이 null 입니다.";
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (!(itemValue instanceof java.sql.Timestamp)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 java.sql.Timestamp 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			java.sql.Timestamp javaSqlTimestampValue = (java.sql.Timestamp)itemValue;
			long javaSqlTimestampLongValue = javaSqlTimestampValue.getTime();			
			jsonObjForOutputStream.put(itemName, javaSqlTimestampLongValue);
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** DJSON 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DJSONBooleanSingleItemEncoder extends AbstractDJSONSingleItemEncoder {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue,
				int itemSize, Charset itemCharset, JSONObject jsonObjForOutputStream)
				throws Exception {
			if (null == itemValue) {
				String errorMessage = "항목의 값이 null 입니다.";
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (!(itemValue instanceof java.lang.Boolean)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 java.lang.Boolean 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			java.lang.Boolean booleanValue = (java.lang.Boolean)itemValue;
			if (booleanValue) {
				jsonObjForOutputStream.put(itemName, "true");
			} else {
				jsonObjForOutputStream.put(itemName, "false");
			}
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	
	public AbstractDJSONSingleItemEncoder get(int itemTypeID) {
		return djsonSingleItemEncoderList[itemTypeID];
	}
}
