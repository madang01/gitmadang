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
package kr.pe.sinnori.common.protocol.thb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * THB 단일 항목 인코더
 * @author "Won Jonghoon"
 *
 */
public class THBSingleItemEncoder implements SingleItemEncoderIF {
	private Logger log = LoggerFactory.getLogger(THBSingleItemEncoder.class);
	
	@SuppressWarnings("unused")
	private CharsetEncoder systemCharsetEncoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public THBSingleItemEncoder(CharsetEncoder systemCharsetEncoder) {
		this.systemCharsetEncoder = systemCharsetEncoder;
		this.streamCodingErrorActionOnMalformedInput = systemCharsetEncoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = systemCharsetEncoder.malformedInputAction();
		
		checkValidTHBTypeSingleItemEncoderList();
	}
	
	private void checkValidTHBTypeSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (thbTypeSingleItemEncoderList.length != singleItemTypes.length) {
			log.error("the var thbTypeSingleItemEncoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					thbTypeSingleItemEncoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = thbTypeSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var thbTypeSingleItemEncoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	private abstract class abstractTHBTypeSingleItemEncoder {
		abstract public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception;
		
		protected void writeItemID(int itemTypeID, BinaryOutputStreamIF binaryOutputStream) throws BufferOverflowException, IllegalArgumentException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			binaryOutputStream.putUnsignedByte(itemTypeID);
		}
		
		abstract public SingleItemType getSingleItemType();
	}
	
	private final abstractTHBTypeSingleItemEncoder[] thbTypeSingleItemEncoderList = new abstractTHBTypeSingleItemEncoder[] { 
			new THBByteSingleItemEncoder(), new THBUnsignedByteSingleItemEncoder(), 
			new THBShortSingleItemEncoder(), new THBUnsignedShortSingleItemEncoder(),
			new THBIntSingleItemEncoder(), new THBUnsignedIntSingleItemEncoder(), 
			new THBLongSingleItemEncoder(), new THBUBPascalStringSingleItemEncoder(),
			new THBUSPascalStringSingleItemEncoder(), new THBSIPascalStringSingleItemEncoder(), 
			new THBFixedLengthStringSingleItemEncoder(), new THBUBVariableLengthBytesSingleItemEncoder(), 
			new THBUSVariableLengthBytesSingleItemEncoder(), new THBSIVariableLengthBytesSingleItemEncoder(), 
			new THBFixedLengthBytesSingleItemEncoder(), 
			new THBJavaSqlDateSingleItemEncoder(), new THBJavaSqlTimestampSingleItemEncoder(),
			new THBBooleanSingleItemEncoder()
	};

		
	/** THB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBByteSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			
			byte tempItemValue = 0;
				
			if (null != itemValue) {
				tempItemValue = (Byte) itemValue;
			}
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {

		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			short tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Short) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedByte(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			short tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Short) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putShort(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			int tempItemValue = 0;	
			
			if (null != itemValue) {
				tempItemValue = (Integer) itemValue;
			}

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedShort(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			int tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Integer) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putInt(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			long tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Long) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedInt(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			long tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Long) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(tempItemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemCharset) {
				binaryOutputStream.putUBPascalString(tempItemValue);
			} else {
				binaryOutputStream.putUBPascalString(tempItemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == itemCharset) {
				binaryOutputStream.putUSPascalString(tempItemValue);
			} else {
				binaryOutputStream.putUSPascalString(tempItemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == itemCharset) {
				binaryOutputStream.putSIPascalString(tempItemValue);
			} else {
				binaryOutputStream.putSIPascalString(tempItemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemCharset) {
				binaryOutputStream.putFixedLengthString(itemSize, tempItemValue);
			} else {
				CharsetEncoder userDefinedCharsetEncoder =  itemCharset.newEncoder();
				userDefinedCharsetEncoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				userDefinedCharsetEncoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				
				binaryOutputStream.putFixedLengthString(itemSize, tempItemValue, userDefinedCharsetEncoder);
			}

		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemValue) {
				binaryOutputStream.putUnsignedByte((short)0);
			} else {
				byte tempItemValue[] = (byte[]) itemValue;
				/*
				if (realValue.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
					throw new IllegalArgumentException(String.format(
							"파라미터 바이트 배열 길이[%d]는 unsigned byte 최대값[%d]을 넘을 수 없습니다.", realValue.length, CommonStaticFinal.MAX_UNSIGNED_BYTE));
				}
				*/
				binaryOutputStream.putUnsignedByte(tempItemValue.length);
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemValue) {
				binaryOutputStream.putUnsignedShort(0);
			} else {
				byte tempItemValue[] = (byte[]) itemValue;
				binaryOutputStream.putUnsignedShort(tempItemValue.length);
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemValue) {
				binaryOutputStream.putInt(0);
			} else {

				byte tempItemValue[] = (byte[]) itemValue;
				binaryOutputStream.putInt(tempItemValue.length);
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == itemValue) {

				byte tempItemValue[] = new byte[itemSize];
				Arrays.fill((byte[]) tempItemValue, (byte) 0);
				binaryOutputStream.putBytes((byte[]) tempItemValue, 0, itemSize);
			} else {
				byte tempItemValue[] = (byte[]) itemValue;	

				if (tempItemValue.length != itemSize) {
					throw new IllegalArgumentException(
							String.format(
									"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									tempItemValue.length, itemSize));
				}
				
				
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlDateSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue,
				int itemSize, Charset itemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {
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
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(javaSqlDateLongValue);
			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlTimestampSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue,
				int itemSize, Charset itemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {
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
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(javaSqlTimestampLongValue);			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** THB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBBooleanSingleItemEncoder extends abstractTHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue,
				int itemSize, Charset itemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {
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
			
			boolean booleanValue = (Boolean)itemValue;		
			
			byte booleanByte;
			
			if (booleanValue) {
				booleanByte = 1;
			} else {
				booleanByte = 0;
			}
						
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(booleanByte);				
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
		
		if (!(writableMiddleObject instanceof BinaryOutputStreamIF)) {
			String errorMessage = String.format(
					"중간 다리역활 출력 객체[%s] 타입이 OutputStreamIF 이 아닙니다.",
					writableMiddleObject.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
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
		
		BinaryOutputStreamIF binaryOutputStream = (BinaryOutputStreamIF)writableMiddleObject;		
		try {
			thbTypeSingleItemEncoderList[itemTypeID].putValue(itemTypeID, itemName, itemValue, itemSize, itemCharset, binaryOutputStream);
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("unknown error::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
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
	public Object getWritableMiddleObjectjFromArrayObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}
	
	@Override
	public Object getArrayObjectFromWritableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object writableMiddleObject) throws BodyFormatException {
		return writableMiddleObject;
	}
}
