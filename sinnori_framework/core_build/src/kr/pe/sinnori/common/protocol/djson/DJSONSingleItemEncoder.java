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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;
import kr.pe.sinnori.common.protocol.djson.header.DJSONHeader;
import kr.pe.sinnori.common.util.HexUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * DJSON 단일 항목 인코더
 * 
 * @author "Jonghoon Won"
 *
 */
public class DJSONSingleItemEncoder implements SingleItemEncoderIF, CommonRootIF {
	
	private interface DJSONTypeSingleItemEncoderIF {
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws Exception;
	}
	
	private final DJSONTypeSingleItemEncoderIF[] dhbTypeSingleItemEncoderList = new DJSONTypeSingleItemEncoderIF[] { 
			new DJSONByteSingleItemEncoder(), new DJSONUnsignedByteSingleItemEncoder(), 
			new DJSONShortSingleItemEncoder(), new DJSONUnsignedShortSingleItemEncoder(),
			new DJSONIntSingleItemEncoder(), new DJSONUnsignedIntSingleItemEncoder(), 
			new DJSONLongSingleItemEncoder(), new DJSONUBPascalStringSingleItemEncoder(),
			new DJSONUSPascalStringSingleItemEncoder(), new DJSONSIPascalStringSingleItemEncoder(), 
			new DJSONFixedLengthStringSingleItemEncoder(), new DJSONUBVariableLengthBytesSingleItemEncoder(), 
			new DJSONUSVariableLengthBytesSingleItemEncoder(), new DJSONSIVariableLengthBytesSingleItemEncoder(), 
			new DJSONFixedLengthBytesSingleItemEncoder(), 
			new DJSONJavaSqlDateSingleItemEncoder(), new DJSONJavaSqlTimestampSingleItemEncoder()
	};

	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONByteSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Byte)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Byte 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedByteSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Short)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Short 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Short tValue = (Short) itemValue;
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Byte 범위가 아닙니다.", 
								tValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONShortSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Short)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Short 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedShortSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Integer)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Integer 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Integer tValue = (Integer) itemValue;
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Short 범위가 아닙니다.", 
								tValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONIntSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Integer)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Integer 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedIntSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Long)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Long 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			Long tValue = (Long) itemValue;
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_INT) {
				String errorMessage = 
						String.format("항목의 값[%d]이 Unsigned Integer 범위가 아닙니다.", 
								tValue);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONLongSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof Long)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 Long 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBPascalStringSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			String tValue = (String) itemValue;
			
			byte[] valueBytes = tValue.getBytes(charsetOfProject);
			if (valueBytes.length > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("UBPascalString 타입 항목의 값을 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned byte 최대값[%d]을 넘었습니다.", 
								DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinalVars.MAX_UNSIGNED_BYTE);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, tValue);
		}
	}

	/** DJSON 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSPascalStringSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang,  Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			String tValue = (String) itemValue;
			
			byte[] valueBytes = tValue.getBytes(charsetOfProject);
			if (valueBytes.length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("UBPascalString 타입 항목의 값을 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned short 최대값[%d]을 넘었습니다.", 
								DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinalVars.MAX_UNSIGNED_SHORT);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, tValue);
		}
	}

	/** DJSON 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIPascalStringSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			if (!(itemValue instanceof String)) {
				String errorMessage = 
						String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
								itemValue.getClass().getCanonicalName());
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonWriteObj.put(itemName, itemValue);
		}
	}

	/** DJSON 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthStringSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			String tValue = null;
			
			if (null == itemValue) tValue = "";
			else {
				if (!(itemValue instanceof String)) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 String 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				tValue = (String) itemValue;
			}
			
			
			ByteBuffer outputBuffer = null;
			
			try {
				outputBuffer = ByteBuffer.allocate(itemSizeForLang);
			} catch(OutOfMemoryError e) {
				log.warn("OutOfMemoryError", e);
				throw e;
			}
			
			/** 고정 크기 출력 스트림 */
			FixedSizeOutputStream fsos = null;
			
			if (null == itemCharsetForLang) {
				fsos = new FixedSizeOutputStream(outputBuffer, itemCharsetForLang, CharsetUtil.createCharsetEncoder(charsetOfProject));
			} else {
				fsos = new FixedSizeOutputStream(outputBuffer, itemCharsetForLang, CharsetUtil.createCharsetEncoder(itemCharsetForLang));
			}		
			
			try {
				fsos.putString(itemSizeForLang, tValue);
			} catch (BufferOverflowException e) {
				/** dead code area */
				e.printStackTrace();
				System.exit(1);
			} catch (NoMoreDataPacketBufferException e) {
				/** dead code area */
				e.printStackTrace();
				System.exit(1);
			}
			
			outputBuffer.flip();
			
			jsonWriteObj.put(itemName, new String(outputBuffer.array(), itemCharsetForLang));

		}
	}

	

	/** DJSON 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBVariableLengthBytesSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			byte tValue[] = null;
			
			if (null == itemValue) {
				tValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tValue = (byte[]) itemValue;
				
				if (tValue.length > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
					String errorMessage = 
							String.format("ub variable length byte[] 타입 항목의 길이[%d]가 unsigned byte 최대값을 넘었습니다.",  tValue.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			jsonWriteObj.put(itemName, HexUtil.getHexStringFromByteArray(tValue));
		}
	}

	/** DJSON 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSVariableLengthBytesSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			byte tValue[] = null;
			
			if (null == itemValue) {
				tValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tValue = (byte[]) itemValue;
				
				if (tValue.length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
					String errorMessage = 
							String.format("ub variable length byte[] 타입 항목의 길이[%d]가 unsigned short 최대값을 넘었습니다.",  tValue.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			jsonWriteObj.put(itemName, HexUtil.getHexStringFromByteArray(tValue));
		}
	}
	
	/** DJSON 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIVariableLengthBytesSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			byte tValue[] = null;
			
			if (null == itemValue) {
				tValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tValue = (byte[]) itemValue;
			}
			
			jsonWriteObj.put(itemName, HexUtil.getHexStringFromByteArray(tValue));
		}
	}
	
	/** DJSON 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthBytesSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonWriteObj)
				throws IllegalArgumentException {
			byte tValue[] = null;
			
			if (null == itemValue) {
				tValue = new byte[0];
			} else {
				if (!(itemValue instanceof byte[])) {
					String errorMessage = 
							String.format("항목의 값의 타입[%s]이 byte[] 가 아닙니다.", 
									itemValue.getClass().getCanonicalName());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tValue = (byte[]) itemValue;
				
				if (tValue.length != itemSizeForLang) {
					throw new IllegalArgumentException(
							String.format(
									"바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									tValue.length, itemSizeForLang));
				}
			}
			jsonWriteObj.put(itemName, HexUtil.getHexStringFromByteArray(tValue));
		}
	}
	
	/** DJSON 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DJSONJavaSqlDateSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue,
				int itemSizeForLang, Charset itemCharsetForLang,
				Charset charsetOfProject, JSONObject jsonWriteObj)
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
			jsonWriteObj.put(itemName, javaSqlDateLongValue);
		}		
	}
	
	/** DJSON 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DJSONJavaSqlTimestampSingleItemEncoder implements DJSONTypeSingleItemEncoderIF {
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue,
				int itemSizeForLang, Charset itemCharsetForLang,
				Charset charsetOfProject, JSONObject jsonWriteObj)
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
			
			java.sql.Timestamp javaSqlTimestampValue = (java.sql.Timestamp)itemValue;
			long javaSqlTimestampLongValue = javaSqlTimestampValue.getTime();			
			jsonWriteObj.put(itemName, javaSqlTimestampLongValue);
		}		
	}

	@Override
	public void putValueToMiddleWriteObj(String path, String itemName,
			int itemTypeID, String itemTypeName, Object itemValue,
			int itemSizeForLang, String itemCharset,
			Charset charsetOfProject, Object middleWriteObj) throws Exception {
		
		if (!(middleWriteObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					middleWriteObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		Charset itemCharsetForLang = null;
		if (null == itemCharset) {
			itemCharsetForLang = charsetOfProject;
		} else {
			itemCharsetForLang = Charset.forName(itemCharset);
		}
		
		
		JSONObject jsonWriteObj = (JSONObject)middleWriteObj;
		try {
			dhbTypeSingleItemEncoderList[itemTypeID].putValue(itemName, itemValue, itemSizeForLang, itemCharsetForLang, charsetOfProject, jsonWriteObj);
		} catch(IllegalArgumentException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("잘못된 파라미티터 에러::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.info(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("알수없는에러::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		}
	}
	
	@Override
	public Object getMiddleWriteObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException {
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
		Object valueObj = null;
		try {
			valueObj = jsonArray.get(inx);
		} catch(IndexOutOfBoundsException  e) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값은 배열 크기를 벗어난 요청입니다.", path);
			throw new BodyFormatException(errorMessage);
		}		
		
		if (!(valueObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return valueObj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getArrayObjFromMiddleWriteObj(String path, String arrayName,
			int arrayCntValue, Object middleWriteObj)
			throws BodyFormatException {
		if (!(middleWriteObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]를 얻는 과정에서  파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, arrayName, middleWriteObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		if (arrayCntValue < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s] 생성후 얻기::parameter arrayCntValue is less than zero",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)middleWriteObj;
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
}
