/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.sinnori.common.protocol.djson;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;
import kr.pe.sinnori.common.protocol.djson.header.DJSONHeader;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * DJSON 단일 항목 인코더
 * 
 * @author "Won Jonghoon"
 *
 */
public class DJSONSingleItemEncoder implements SingleItemEncoderIF {
	private Logger log = LoggerFactory.getLogger(DJSONSingleItemEncoder.class);
	
	private CharsetEncoder streamCharsetEncoder = null;
	private Charset streamCharset = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public DJSONSingleItemEncoder(CharsetEncoder streamCharsetEncoder) {
		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCodingErrorActionOnMalformedInput = streamCharsetEncoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetEncoder.unmappableCharacterAction();
		streamCharset = streamCharsetEncoder.charset();
		
		checkValidDJSONTypeSingleItemEncoderList();
	}
	
	private void checkValidDJSONTypeSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (djsonTypeSingleItemEncoderList.length != singleItemTypes.length) {
			log.error("the var djsonTypeSingleItemEncoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					djsonTypeSingleItemEncoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = djsonTypeSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var djsonTypeSingleItemEncoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	private abstract class abstractDJSONTypeSingleItemEncoder {
		abstract public void putValue(String itemName, Object itemValue, int itemSize,
				Charset itemCharset,  JSONObject jsonObjForOutputStream)
				throws Exception;
		
		abstract public SingleItemType getSingleItemType();
	}
	
	private final abstractDJSONTypeSingleItemEncoder[] djsonTypeSingleItemEncoderList = new abstractDJSONTypeSingleItemEncoder[] { 
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

	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONByteSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONUnsignedByteSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {

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
	private final class DJSONShortSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONUnsignedShortSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
		
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
	private final class DJSONIntSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
		
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
	private final class DJSONUnsignedIntSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
		
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
	private final class DJSONLongSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {		
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
	private final class DJSONUBPascalStringSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {		
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
	private final class DJSONUSPascalStringSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONSIPascalStringSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONFixedLengthStringSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONUBVariableLengthBytesSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONUSVariableLengthBytesSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONSIVariableLengthBytesSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class DJSONFixedLengthBytesSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class  DJSONJavaSqlDateSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class  DJSONJavaSqlTimestampSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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
	private final class  DJSONBooleanSingleItemEncoder extends abstractDJSONTypeSingleItemEncoder {
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

	@Override
	public void putValueToWritableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, Object itemValue,
			int itemSize, String nativeItemCharset, Object writableMiddleObject) throws Exception {
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (null == itemValue) {
			throw new IllegalArgumentException("the parameter itemValue is null");
		}
		
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					writableMiddleObject.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		Charset itemCharset = null;
		if (null == nativeItemCharset) {
			itemCharset = streamCharset;
		} else {
			try {
				itemCharset = Charset.forName(nativeItemCharset);
			} catch(Exception e) {
				log.warn(String.format("the parameter nativeItemCharset[%s] is not a bad charset name", nativeItemCharset), e);
				
				itemCharset = streamCharset;
			}
		}
		
		
		JSONObject jsonObjForOutputStream = (JSONObject)writableMiddleObject;
		try {
			djsonTypeSingleItemEncoderList[itemTypeID].putValue(itemName, itemValue, itemSize, itemCharset, jsonObjForOutputStream);
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("unknown error::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSize);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(nativeItemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}
	}
	
	@Override
	public Object getWritableMiddleObjectjFromArrayMiddleObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		if (null == path) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 path 의 값[%d] 이 null 입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (inx < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 inx 의 값[%d] 이 음수입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (!(arrayObj instanceof JSONArray)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻는 과정에서 파라미터 arrayObj[%s]의 데이터 타입이 JSONArray 이 아닙니다.",
					path, arrayObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONArray jsonArray = (JSONArray)arrayObj;
		Object writableMiddleObjectOfArray = null;
		try {
			writableMiddleObjectOfArray = jsonArray.get(inx);
		} catch(IndexOutOfBoundsException  e) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값은 배열 크기를 벗어난 요청입니다.", path);
			throw new BodyFormatException(errorMessage);
		}		
		
		if (!(writableMiddleObjectOfArray instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, writableMiddleObjectOfArray.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return writableMiddleObjectOfArray;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getArrayMiddleObjectFromWritableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object writableMiddleObject)
			throws BodyFormatException {
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]를 얻는 과정에서  파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, arrayName, writableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		if (arrayCntValue < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s] 생성후 얻기::parameter arrayCntValue is less than zero",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)writableMiddleObject;
		Object valueObj = jsonReadObj.get(arrayName);

		if (null == valueObj) {
			/*String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]이 존재하지 않습니다.",
					path, arrayName);
			throw new BodyFormatException(errorMessage);*/
			JSONArray jsonArray = new JSONArray();
			for (int i=0; i < arrayCntValue; i++) {
				jsonArray.add(new JSONObject());
			}
			jsonReadObj.put(arrayName, jsonArray);
			return jsonArray;
		}

		if (!(valueObj instanceof JSONArray)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]의 값의 타입[%s]이 JSONArray 이 아닙니다.",
					path, arrayName, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		
		JSONArray jsonArray = (JSONArray)valueObj;
		
		if (jsonArray.size() !=  arrayCntValue) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]의 크기[%s]가 파라미터 arrayCntValue[%d]와 다릅니다.",
					path, arrayName, jsonArray.size(), arrayCntValue);
			throw new BodyFormatException(errorMessage);
		}
		
		return jsonArray;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getGroupMiddleObjectFromWritableMiddleObject(String path, String groupName, Object writableMiddleObject)
			throws BodyFormatException {
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]을 얻는 과정에서  파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, groupName, writableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)writableMiddleObject;
		Object valueObj = jsonReadObj.get(groupName);

		if (null == valueObj) {
			JSONObject jsonGroup = new JSONObject();
			
			jsonReadObj.put(groupName, jsonGroup);
			return jsonGroup;
		}

		if (!(valueObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, groupName, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return valueObj;
	}
}
