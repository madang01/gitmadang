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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.message.builder.info.SingleItemTypeManger;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;

/**
 * DHB 프로톨 단일 항목 디코더
 * @author "Won Jonghoon"
 *
 */
public class DHBSingleItemDecoder implements SingleItemDecoderIF {	
	private Logger log = LoggerFactory.getLogger(DHBSingleItemDecoder.class);
	
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public DHBSingleItemDecoder(CharsetDecoder streamCharsetDecoder) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.streamCodingErrorActionOnMalformedInput = streamCharsetDecoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetDecoder.unmappableCharacterAction();		

		checkValidDHBTypeSingleItemDecoderList();
	}

	private void checkValidDHBTypeSingleItemDecoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (dhbTypeSingleItemDecoderList.length != singleItemTypes.length) {
			log.error("the var dhbTypeSingleItemDecoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					dhbTypeSingleItemDecoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = dhbTypeSingleItemDecoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var dhbTypeSingleItemDecoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}
	
	private abstract class abstractDHBTypeSingleItemDecoder {
		abstract public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws Exception;
		
		protected void throwExceptionIfItemTypeIsDifferent(int itemTypeID, String itemName,
				BinaryInputStreamIF binaryInputStream) throws SinnoriBufferUnderflowException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				String itemTypeName = "unknown";
				try {
					itemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(itemTypeID);
				} catch (UnknownItemTypeException e) {
				}
				
				String receivedItemTypeName = "unknown";
				try {
					receivedItemTypeName = SingleItemTypeManger.getInstance().getItemTypeName(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
				}
				
				String errorMesssage = String.format("this single item type[id:%d, name:%s][%s] is different from the received item type[id:%d, name:%s]", 
						itemTypeID,
						itemTypeName,
						itemName, 
						receivedItemTypeID,
						receivedItemTypeName);
				
				throw new BodyFormatException(errorMesssage);
			}
		}
		
		abstract public SingleItemType getSingleItemType();
	}
	
	private final abstractDHBTypeSingleItemDecoder[] dhbTypeSingleItemDecoderList = new abstractDHBTypeSingleItemDecoder[] { 
			new DHBSelfExnErrorPlaceSingleItemDecoder(), new DHBSelfExnErrorTypeSingleItemDecoder(),
			new DHBByteSingleItemDecoder(), new DHBUnsignedByteSingleItemDecoder(), 
			new DHBShortSingleItemDecoder(), new DHBUnsignedShortSingleItemDecoder(),
			new DHBIntSingleItemDecoder(), new DHBUnsignedIntSingleItemDecoder(), 
			new DHBLongSingleItemDecoder(), new DHBUBPascalStringSingleItemDecoder(),
			new DHBUSPascalStringSingleItemDecoder(), new DHBSIPascalStringSingleItemDecoder(), 
			new DHBFixedLengthStringSingleItemDecoder(), new DHBUBVariableLengthBytesSingleItemDecoder(), 
			new DHBUSVariableLengthBytesSingleItemDecoder(), new DHBSIVariableLengthBytesSingleItemDecoder(), 
			new DHBFixedLengthBytesSingleItemDecoder(), 
			new DHBJavaSqlDateSingleItemDecoder(),  new DHBJavaSqlTimestampSingleItemDecoder(),
			new DHBBooleanSingleItemDecoder()
	};	
	

	private final class DHBSelfExnErrorPlaceSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return SelfExn.ErrorPlace.valueOf(binaryInputStream.getByte());
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_PLACE;
		}
	}	
	
	
	private final class DHBSelfExnErrorTypeSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return SelfExn.ErrorType.valueOf(binaryInputStream.getByte());
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_TYPE;
		}
	}
	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBByteSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getByte();
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedByteSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {

		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedByte();
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBShortSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getShort();
		}	
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedShortSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getUnsignedShort();
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBIntSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			return binaryInputStream.getInt();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedIntSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception  {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getUnsignedInt();
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBLongSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getLong();
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBPascalStringSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			if (null == itemCharset) {
				return binaryInputStream.getUBPascalString();
			} else {
				return binaryInputStream.getUBPascalString(itemCharset);
			}
			
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSPascalStringSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			if (null == itemCharset) {
				return binaryInputStream.getUSPascalString();
			} else {
				return binaryInputStream.getUSPascalString(itemCharset);
			}
		}		
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIPascalStringSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			if (null == itemCharset) {
				return binaryInputStream.getSIPascalString();
			} else {
				return binaryInputStream.getSIPascalString(itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthStringSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			if (null == itemCharset) {
				return binaryInputStream.getFixedLengthString(itemSize);
			} else {
				CharsetDecoder userDefinedCharsetDecoder =  itemCharset.newDecoder();
				userDefinedCharsetDecoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				userDefinedCharsetDecoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				return binaryInputStream.getFixedLengthString(itemSize, userDefinedCharsetDecoder);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBVariableLengthBytesSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			short len = binaryInputStream.getUnsignedByte();
			return binaryInputStream.getBytes(len);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSVariableLengthBytesSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			int len = binaryInputStream.getUnsignedShort();
			return binaryInputStream.getBytes(len);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIVariableLengthBytesSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			int len = binaryInputStream.getInt();
			return binaryInputStream.getBytes(len);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthBytesSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			return binaryInputStream.getBytes(itemSize);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlDateSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Date(javaSqlDateLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** DHB 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlTimestampSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Timestamp(javaSqlDateLongValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** DHB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBBooleanSingleItemDecoder extends abstractDHBTypeSingleItemDecoder {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws Exception {
			throwExceptionIfItemTypeIsDifferent(itemTypeID, itemName, binaryInputStream);
			byte booleanByte = binaryInputStream.getByte();
			
			if (booleanByte != 0 && booleanByte != 1) {
				String errorMesssage = String.format("boolean 타입의 항목 값은 참을 뜻하는 1과 거짓을 뜻하는 0 을 갖습니다." +
						"%sboolean 타입의 항목[%s] 값[%d]이  잘못되었습니다. ", 
						CommonStaticFinalVars.NEWLINE, itemName, booleanByte);
				throw new BodyFormatException(errorMesssage);
			}
				
			return (0 != booleanByte);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	

	@Override
	public Object getValueFromReadableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, int itemSize,
			String nativeItemCharset, Object readableMiddleObject) throws BodyFormatException {
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (!(readableMiddleObject instanceof BinaryInputStreamIF)) {
			String errorMessage = String.format(
					"the parameter middleReadObj[%s] is not Inherited the BinaryInputStreamIF interface",
					readableMiddleObject.getClass().getCanonicalName());
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		Charset itemCharset = null;
		
		if (null != nativeItemCharset) {			
			try {
				itemCharset = Charset.forName(nativeItemCharset);
			} catch(Exception e) {
				String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
				.append(nativeItemCharset).append("] is a bad charset name::")
				.append("{ path=[")
				.append(path)
				.append("], itemName=[")
				.append(itemName)
				.append("], itemType=[")
				.append(itemTypeName)
				.append("]}").toString();
				
				log.warn(errorMessage);
			}
		}		
		
		BinaryInputStreamIF binaryInputStream = (BinaryInputStreamIF)readableMiddleObject;
		Object retObj = null;
		try {
			retObj = dhbTypeSingleItemDecoderList[itemTypeID].getValue(itemTypeID, itemName, itemSize, itemCharset, binaryInputStream);		
		} catch(Exception | OutOfMemoryError e) {
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
		return readableMiddleObject;
	}

	@Override
	public Object getReadableMiddleObjFromArrayMiddleObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}
	
	@Override
	public Object getGroupMiddleObjectFromReadableMiddleObject(String path, String groupName, Object readableMiddleObject)
			throws BodyFormatException {
		return readableMiddleObject;
	}
	
	@Override
	public void closeReadableMiddleObjectWithValidCheck(Object readableMiddleObject) throws BodyFormatException {
		if (!(readableMiddleObject instanceof BinaryInputStreamIF)) {
			String errorMessage = String.format(
					"스트림으로 부터 생성된 중간 다리역활 객체[%s]의 데이터 타입이 InputStreamIF 이 아닙니다.",
					readableMiddleObject.getClass().getCanonicalName());
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		BinaryInputStreamIF binaryInputStream = (BinaryInputStreamIF)readableMiddleObject;
		long remainingBytes = binaryInputStream.available();
		
		binaryInputStream.close();
		
		if (0 > remainingBytes) {
			String errorMessage = String.format(
					"메시지 추출후 남은 데이터[%d]가 존재합니다.",
					remainingBytes);
			throw new BodyFormatException(errorMessage);
		}
	}
}
