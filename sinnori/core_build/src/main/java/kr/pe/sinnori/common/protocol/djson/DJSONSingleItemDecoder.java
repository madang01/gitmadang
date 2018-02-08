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
import java.nio.charset.CharsetDecoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * DJSON 단일 항목 디코더
 * 
 * @author "Won Jonghoon"
 *
 */
public class DJSONSingleItemDecoder implements SingleItemDecoderIF {
	private Logger log = LoggerFactory.getLogger(DJSONSingleItemDecoder.class);
	
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder = null;
	private Charset streamCharset = null;
	//private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	//private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public DJSONSingleItemDecoder(CharsetDecoder streamCharsetDecoder) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		this.streamCharsetDecoder = streamCharsetDecoder;
		//this.streamCodingErrorActionOnMalformedInput = systemCharsetDecoder.malformedInputAction();
		// this.streamCodingErrorActionOnUnmappableCharacter = systemCharsetDecoder.unmappableCharacterAction();
		this.streamCharset = streamCharsetDecoder.charset();
		
		checkValidDJSONTypeSingleItemDecoderList();
	}
	
	private void checkValidDJSONTypeSingleItemDecoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (djsonTypeSingleItemDecoderList.length != singleItemTypes.length) {
			log.error("the var djsonTypeSingleItemDecoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					djsonTypeSingleItemDecoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = djsonTypeSingleItemDecoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var djsonTypeSingleItemDecoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	private abstract class abstractDJSONTypeSingleItemDecoder {
		abstract public Object getValue(String itemName, int itemSize,
				Charset itemCharset, JSONObject jsonObjForInputStream) throws Exception;
		
		abstract public SingleItemType getSingleItemType();
	}
	
	private final abstractDJSONTypeSingleItemDecoder[] djsonTypeSingleItemDecoderList = new abstractDJSONTypeSingleItemDecoder[] { 
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
	

	private final class DJSONSelfExnErrorPlaceSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	
	private final class DJSONSelfExnErrorTypeSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONByteSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONUnsignedByteSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {

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
	private final class DJSONShortSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONUnsignedShortSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
		
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
	private final class DJSONIntSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
		
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
	private final class DJSONUnsignedIntSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
		
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
	private final class DJSONLongSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {		
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
	private final class DJSONUBPascalStringSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {		
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
	private final class DJSONUSPascalStringSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONSIPascalStringSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONFixedLengthStringSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONUBVariableLengthBytesSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONUSVariableLengthBytesSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONSIVariableLengthBytesSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONFixedLengthBytesSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {
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
	private final class DJSONJavaSqlDateSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {

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
	private final class DJSONJavaSqlTimestampSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {

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
	private final class DJSONBooleanSingleItemDecoder extends abstractDJSONTypeSingleItemDecoder {

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
	
	@Override
	public Object getValueFromReadableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, int itemSize,
			String nativeItemCharset, Object middleReadObj) throws BodyFormatException {
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (!(middleReadObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"스트림으로 부터 생성된 중간 다리역활 객체[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					middleReadObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		Charset itemCharset = null;
		
		if (null != nativeItemCharset) {			
			try {
				itemCharset = Charset.forName(nativeItemCharset);
			} catch(Exception e) {
				log.warn(String.format("the parameter nativeItemCharset[%s] is not a bad charset name", nativeItemCharset), e);
			}
		}	
		
		JSONObject jsonReadObj = (JSONObject)middleReadObj;
		
		Object retObj = null;
		try {
			retObj = djsonTypeSingleItemDecoderList[itemTypeID].getValue(itemName, itemSize, itemCharset, jsonReadObj);		
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("unknown error::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		return retObj;
	}
	
	
	@Override
	public Object getArrayMiddleObjectFromReadableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object readableMiddleObject)
			throws BodyFormatException {
		if (!(readableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]를 얻는 과정에서  파라미터 middleReadObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, arrayName, readableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		if (arrayCntValue < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s] 생성후 얻기::parameter arrayCntValue is less than zero",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)readableMiddleObject;
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
	public Object getReadableMiddleObjFromArrayMiddleObject(String path, Object arrayObj, int inx) throws BodyFormatException {
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
		Object readableMiddleObjectOfArray = null;
		try {
			readableMiddleObjectOfArray = jsonArray.get(inx);
		} catch(IndexOutOfBoundsException  e) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목 값은 배열 크기를 벗어난 요청입니다.", path);
			throw new BodyFormatException(errorMessage);
		}		
		
		if (!(readableMiddleObjectOfArray instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 배열의 항목의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, readableMiddleObjectOfArray.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return readableMiddleObjectOfArray;
	}
	
	@Override
	public Object getGroupMiddleObjectFromReadableMiddleObject(String path, String groupName, Object readableMiddleObject)
			throws BodyFormatException {
		if (!(readableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]을 얻는 과정에서  파라미터 middleReadObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, groupName, readableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)readableMiddleObject;
		Object valueObj = jsonReadObj.get(groupName);

		if (null == valueObj) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]이 존재하지 않습니다.",
					path, groupName);
			throw new BodyFormatException(errorMessage);
		}

		if (!(valueObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, groupName, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
				
		return valueObj;
	}
	
	
	@Override
	public void closeReadableMiddleObjectWithValidCheck(Object readableMiddleObject) throws BodyFormatException {
		// nothing
	}
}
