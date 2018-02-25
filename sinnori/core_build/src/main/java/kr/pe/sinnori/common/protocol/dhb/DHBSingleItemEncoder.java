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
package kr.pe.sinnori.common.protocol.dhb;

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
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;

/**
 * DHB 프로톨 단일 항목 인코더
 * @author "Won Jonghoon"
 *
 */
public class DHBSingleItemEncoder implements SingleItemEncoderIF {
	private Logger log = LoggerFactory.getLogger(DHBSingleItemEncoder.class);
	
	@SuppressWarnings("unused")
	private CharsetEncoder systemCharsetEncoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public DHBSingleItemEncoder(CharsetEncoder systemCharsetEncoder) {
		this.systemCharsetEncoder = systemCharsetEncoder;
		this.streamCodingErrorActionOnMalformedInput = systemCharsetEncoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = systemCharsetEncoder.malformedInputAction();
		
		checkValidDHBTypeSingleItemEncoderList();
	}
	
	private void checkValidDHBTypeSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (dhbTypeSingleItemEncoderList.length != singleItemTypes.length) {
			log.error("the var dhbTypeSingleItemEncoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					dhbTypeSingleItemEncoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = dhbTypeSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var dhbTypeSingleItemEncoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	private abstract class abstractDHBTypeSingleItemEncoder {
		abstract public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception;
		
		protected void writeItemID(int itemTypeID, BinaryOutputStreamIF binaryOutputStream) throws BufferOverflowException, IllegalArgumentException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			binaryOutputStream.putUnsignedByte(itemTypeID);
		}
		
		abstract public SingleItemType getSingleItemType();
	}
	
	private final abstractDHBTypeSingleItemEncoder[] dhbTypeSingleItemEncoderList = new abstractDHBTypeSingleItemEncoder[] { 
			new DHBSelfExnErrorPlaceSingleItemEncoder(), new DHBSelfExnErrorTypeSingleItemEncoder(),
			new DHBByteSingleItemEncoder(), new DHBUnsignedByteSingleItemEncoder(), 
			new DHBShortSingleItemEncoder(), new DHBUnsignedShortSingleItemEncoder(),
			new DHBIntSingleItemEncoder(), new DHBUnsignedIntSingleItemEncoder(), 
			new DHBLongSingleItemEncoder(), new DHBUBPascalStringSingleItemEncoder(),
			new DHBUSPascalStringSingleItemEncoder(), new DHBSIPascalStringSingleItemEncoder(), 
			new DHBFixedLengthStringSingleItemEncoder(), new DHBUBVariableLengthBytesSingleItemEncoder(), 
			new DHBUSVariableLengthBytesSingleItemEncoder(), new DHBSIVariableLengthBytesSingleItemEncoder(), 
			new DHBFixedLengthBytesSingleItemEncoder(), 
			new DHBJavaSqlDateSingleItemEncoder(), new DHBJavaSqlTimestampSingleItemEncoder(),
			new DHBBooleanSingleItemEncoder()
	};
	
	private final class DHBSelfExnErrorPlaceSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				throw new IllegalArgumentException("the parameter itemValue is null, the value of the item type 'selfexn error place' must be not null");
			}
			SelfExn.ErrorPlace tempItemValue = (SelfExn.ErrorPlace) itemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(tempItemValue.getErrorPlaceByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_PLACE;
		}
	}
	
	private final class DHBSelfExnErrorTypeSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				throw new IllegalArgumentException("the parameter itemValue is null, the value of the item type 'selfexn error type' must be not null");
			}
			SelfExn.ErrorType tempItemValue = (SelfExn.ErrorType) itemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(tempItemValue.getErrorTypeByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_TYPE;
		}
	}
	
	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBByteSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			
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

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedByteSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {

		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, 
				IllegalArgumentException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBShortSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedShortSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBIntSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedIntSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBLongSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBPascalStringSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {		
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

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSPascalStringSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIPascalStringSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthStringSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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
				
				binaryOutputStream.putFixedLengthString(itemSize, tempItemValue,
						userDefinedCharsetEncoder);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBVariableLengthBytesSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
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

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSVariableLengthBytesSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
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
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIVariableLengthBytesSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			
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
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthBytesSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
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
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBJavaSqlDateSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBJavaSqlTimestampSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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
	
	/** DHB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBBooleanSingleItemEncoder extends abstractDHBTypeSingleItemEncoder {
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
	public void putValueToWritableMiddleObject(String path, String itemName, SingleItemType singleItemType, Object itemValue,
			int itemSize, String nativeItemCharset, Object writableMiddleObject)
			throws BodyFormatException, NoMoreDataPacketBufferException {
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (null == itemValue) {
			throw new IllegalArgumentException("the parameter itemValue is null");
		}
		
		if (!(writableMiddleObject instanceof BinaryOutputStreamIF)) {
			String errorMessage = String.format(
					"the parameter middleObjToStream[%s] is not Inherited the BinaryOutputStreamIF interface",
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
			dhbTypeSingleItemEncoderList[itemTypeID].putValue(itemTypeID, itemName, itemValue, itemSize, itemCharset, binaryOutputStream);
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(Exception | OutOfMemoryError e) {
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
			errorMessageBuilder.append(itemCharset);
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
		return arrayObj;
	}

	@Override
	public Object getArrayMiddleObjectFromWritableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object writableMiddleObject) throws BodyFormatException {
		return writableMiddleObject;
	}	
	
	@Override
	public Object getGroupMiddleObjectFromWritableMiddleObject(String path, String groupName, Object writableMiddleObject)
			throws BodyFormatException {
		return writableMiddleObject;
	}
}
