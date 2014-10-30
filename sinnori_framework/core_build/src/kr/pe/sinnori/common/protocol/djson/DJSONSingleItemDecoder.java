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

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.util.HexUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * DJSON 단일 항목 디코더
 * 
 * @author "Jonghoon Won"
 *
 */
public class DJSONSingleItemDecoder implements SingleItemDecoderIF, CommonRootIF {
	private interface DJSONTypeSingleItemDecoderIF {
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream) throws Exception;
	}
	
	private final DJSONTypeSingleItemDecoderIF[] dhbTypeSingleItemDecoderList = new DJSONTypeSingleItemDecoderIF[] { 
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
	

	
	
	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONByteSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Byte.MIN_VALUE || tValue > Byte.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값[%d]이 byte 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			byte value = (byte) tValue;
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedByteSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {

		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값[%d]이 unsigned byte 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tValue;
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONShortSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Short.MIN_VALUE || tValue > Short.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값[%d]이 short 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tValue;
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedShortSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값[%d]이 unsigned short 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tValue;
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONIntSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Integer.MIN_VALUE || tValue > Integer.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값[%d]이 integer 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tValue;
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUnsignedIntSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinalVars.MAX_UNSIGNED_INT) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값[%d]이 unsigned integer 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			return tValue;
		}		
	}

	/** DJSON 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONLongSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 long 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 long 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue;
			
			return tValue;
		}		
	}

	/** DJSON 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBPascalStringSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			
			String tValue = (String)jsonValue;
			int tSize = tValue.getBytes(charsetOfProject).length;
			
			if (tSize > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 문자열 크기[%d]가 unsinged byte 범위를 넘어섰습니다. 참고) 프로젝트 문자셋[%s]", 
								itemName, tSize, charsetOfProject.name());
				throw new BodyFormatException(errorMessage);
			}
			
			return tValue;
			
		}		
	}

	/** DJSON 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSPascalStringSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			String tValue = (String)jsonValue;
			int tSize = tValue.getBytes(charsetOfProject).length;
			
			if (tSize > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USPascalString 타입 항목[%s]의 문자열 크기[%d]가 unsinged short 범위를 넘어섰습니다. 참고) 프로젝트 문자셋[%s]", 
								itemName, tSize, charsetOfProject.name());
				throw new BodyFormatException(errorMessage);
			}
			
			return tValue;
		}		
	}

	/** DJSON 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIPascalStringSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			/*String tValue = (String)jsonValue;
			return tValue;*/
			return jsonValue;
		}		
	}

	/** DJSON 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthStringSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			return jsonValue;
		}		
	}

	

	/** DJSON 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUBVariableLengthBytesSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.getByteArrayFromHexString(tValue);
					
					if (value.length > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
						String errorMessage = 
								String.format("UBVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned byte 최대값을 넘었습니다.", 
										itemName, value.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONUSVariableLengthBytesSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 USVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.getByteArrayFromHexString(tValue);
					
					if (value.length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
						String errorMessage = 
								String.format("USVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned short 최대값을 넘었습니다.", 
										itemName, value.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}		
	}
	
	/** DJSON 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSIVariableLengthBytesSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 SIVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.getByteArrayFromHexString(tValue);
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}		
	}
	
	/** DJSON 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONFixedLengthBytesSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject, JSONObject jsonObjFromStream)
				throws Exception {
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.getByteArrayFromHexString(tValue);
					
					if (value.length != itemSizeForLang) {
						throw new IllegalArgumentException(
								String.format(
										"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
										value.length, itemSizeForLang));
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}		
	}

	/** DJSON 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONJavaSqlDateSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {

		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject,
				JSONObject jsonObjFromStream) throws Exception {			
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 java sql date 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
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
	}
	
	/** DJSON 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONJavaSqlTimestampSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {

		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject,
				JSONObject jsonObjFromStream) throws Exception {			
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 java sql timestamp 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
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
	}
	
	/** DJSON 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONBooleanSingleItemDecoder implements DJSONTypeSingleItemDecoderIF {

		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, Charset charsetOfProject,
				JSONObject jsonObjFromStream) throws Exception {			
			Object jsonValue = jsonObjFromStream.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 boolean 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObjFromStream.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 boolean 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			
			
			String tValue = (String)jsonValue;
			
			if (tValue.equals("true")) {
				return true;
			} else if (tValue.equals("false")) {
				return false;
			} else {
				String errorMessage = 
						String.format("JSON Object 에서 boolean 타입의 값은  문자열 true, false 를 갖습니다." +
								"%sJSON Object 로 부터 얻은 boolean 타입 항목[%s]의 값[%s]이 잘못되었습니다.", 
								CommonStaticFinalVars.NEWLINE, itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}			
		}
	}
	
	@Override
	public Object getValueFromMiddleReadObj(String path, String itemName,
			int itemTypeID, String itemTypeName, int itemSizeForLang,
			String itemCharset, Charset charsetOfProject,
			Object middleReadObj) throws BodyFormatException {
		
		if (!(middleReadObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"스트림으로 부터 생성된 중간 다리역활 객체[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					middleReadObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		Charset itemCharsetForLang = null;
		if (null != itemCharset) {
			try {
				itemCharsetForLang = Charset.forName(itemCharset);
			} catch(Exception e) {
				log.warn("문자셋[{}] 이름이 잘못되었습니다.", itemCharset);
			}
		}
		
		JSONObject jsonReadObj = (JSONObject)middleReadObj;
		
		Object retObj = null;
		try {
			retObj = dhbTypeSingleItemDecoderList[itemTypeID].getValue(itemName, itemSizeForLang, itemCharsetForLang, charsetOfProject, jsonReadObj);
		} catch(IllegalArgumentException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("잘못된 파라미티터 에러::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);			
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
		} catch(SinnoriBufferUnderflowException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("SinnoriBufferUnderflowException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		} catch(SinnoriCharsetCodingException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("SinnoriCharsetCodingException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		} catch(BodyFormatException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("BodyFormatException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.info(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("알수없는에러::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		return retObj;
	}
	
	
	@Override
	public Object getArrayObjFromMiddleReadObj(String path, String arrayName,
			int arrayCntValue, Object middleReadObj)
			throws BodyFormatException {
		if (!(middleReadObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]를 얻는 과정에서  파라미터 middleReadObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, arrayName, middleReadObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		if (arrayCntValue < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s] 생성후 얻기::parameter arrayCntValue is less than zero",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)middleReadObj;
		Object valueObj = jsonReadObj.get(arrayName);

		if (null == valueObj) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]이 존재하지 않습니다.",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
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

	@Override
	public Object getMiddleReadObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException {
		if (null == path) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 path 의 값[%d] 이 null 입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (inx < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 inx 의 값[%d] 이 음수입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (!(arrayObj instanceof JSONArray)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목 값을 얻는 과정에서 파라미터 arrayObj[%s]의 데이터 타입이 JSONArray 이 아닙니다.",
					path, arrayObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONArray jsonArray = (JSONArray)arrayObj;
		Object valueObj = null;
		try {
			valueObj = jsonArray.get(inx);
		} catch(IndexOutOfBoundsException  e) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목 값은 배열 크기를 벗어난 요청입니다.", path);
			throw new BodyFormatException(errorMessage);
		}		
		
		if (!(valueObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return valueObj;
	}
	
	
	@Override
	public void finish(Object middleReadObj) throws BodyFormatException {
		// nothing
	}
}
