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
import java.util.Arrays;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DHB 프로톨 단일 항목 인코더
 * @author "Won Jonghoon"
 *
 */
public class DHBSingleItemEncoder implements SingleItemEncoderIF {
	private Logger log = LoggerFactory.getLogger(DHBSingleItemEncoder.class);
	
	private interface DHBTypeSingleItemEncoderIF {
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception;
	}
	
	private final DHBTypeSingleItemEncoderIF[] dhbTypeSingleItemEncoderList = new DHBTypeSingleItemEncoderIF[] { 
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

	

	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBByteSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			
			byte tempItemValue = 0;
				
			if (null != itemValue) {
				tempItemValue = (Byte) itemValue;
			}
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putByte(tempItemValue);
		}
	}

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedByteSingleItemEncoder implements DHBTypeSingleItemEncoderIF {

		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, 
				IllegalArgumentException, NoMoreDataPacketBufferException {
			short tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Short) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putUnsignedByte(tempItemValue);
		}
	}

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBShortSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			short tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Short) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putShort(tempItemValue);
		}
	}

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedShortSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			int tempItemValue = 0;	
			
			if (null != itemValue) {
				tempItemValue = (Integer) itemValue;
			}

			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putUnsignedShort(tempItemValue);
		}
	}

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBIntSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			int tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Integer) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putInt(tempItemValue);
		}
	}

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedIntSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			long tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Long) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putUnsignedInt(tempItemValue);
		}
	}

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBLongSingleItemEncoder implements DHBTypeSingleItemEncoderIF {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
			long tempItemValue = 0;
			
			if (null != itemValue) {
				tempItemValue = (Long) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putLong(tempItemValue);
		}
	}

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBPascalStringSingleItemEncoder implements DHBTypeSingleItemEncoderIF {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putUBPascalString(tempItemValue);
		}
	}

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSPascalStringSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putUSPascalString(tempItemValue);
		}
	}

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIPascalStringSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putSIPascalString(tempItemValue);
		}
	}

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthStringSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String tempItemValue = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				tempItemValue = (String) itemValue;
			}
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			
			if (null == itemCharset) {
				binaryOutputStream.putFixedLengthString(itemSize, tempItemValue);
			} else {
				binaryOutputStream.putFixedLengthString(itemSize, tempItemValue,
						CharsetUtil.createCharsetEncoder(itemCharset));
			}

		}
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBVariableLengthBytesSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			binaryOutputStream.putUnsignedByte(itemTypeID);
			
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
	}

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSVariableLengthBytesSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			binaryOutputStream.putUnsignedByte(itemTypeID);
			
			if (null == itemValue) {
				binaryOutputStream.putUnsignedShort(0);
			} else {
				byte tempItemValue[] = (byte[]) itemValue;
				binaryOutputStream.putUnsignedShort(tempItemValue.length);
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
	}
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIVariableLengthBytesSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			
			if (null == itemValue) {
				binaryOutputStream.putInt(0);
			} else {

				byte tempItemValue[] = (byte[]) itemValue;
				binaryOutputStream.putInt(tempItemValue.length);
				binaryOutputStream.putBytes(tempItemValue);
			}
		}
	}
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthBytesSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
		@Override
		public void putValue(int itemTypeID, String itemName, Object itemValue, int itemSize,
				Charset itemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			binaryOutputStream.putUnsignedByte(itemTypeID);
			
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
	}
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBJavaSqlDateSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
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
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putLong(javaSqlDateLongValue);
			
		}
	}
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBJavaSqlTimestampSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
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
			
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putLong(javaSqlTimestampLongValue);			
		}
	}
	
	/** DHB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  DHBBooleanSingleItemEncoder implements DHBTypeSingleItemEncoderIF {
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
						
			binaryOutputStream.putUnsignedByte(itemTypeID);
			binaryOutputStream.putByte(booleanByte);			
		}
	}
	
	@Override
	public void putValueToMiddleWriteObj(String path, String itemName, int itemTypeID, String itemTypeName, Object itemValue,
			int itemSize, String nativeItemCharset, Charset streamCharset, Object middleObjToStream)
			throws BodyFormatException, NoMoreDataPacketBufferException {		
		
		if (!(middleObjToStream instanceof BinaryOutputStreamIF)) {
			String errorMessage = String.format(
					"the parameter middleObjToStream[%s] is not Inherited the BinaryOutputStreamIF interface",
					middleObjToStream.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
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
		
		BinaryOutputStreamIF binaryOutputStream = (BinaryOutputStreamIF)middleObjToStream;
		
				
		try {
			dhbTypeSingleItemEncoderList[itemTypeID].putValue(itemTypeID, itemName, itemValue, itemSize, itemCharset, binaryOutputStream);
		} catch(IllegalArgumentException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("wrong paramter error::");
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
			log.info(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		} catch(BufferOverflowException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("BufferOverflowException::");
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
			log.info(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		} catch(NoMoreDataPacketBufferException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("NoMoreDataPacketBufferException::");
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
			throw new NoMoreDataPacketBufferException(errorMessage);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("알수없는에러::");
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
	public Object getMiddleWriteObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}

	@Override
	public Object getArrayObjFromMiddleWriteObj(String path, String arrayName,
			int arrayCntValue, Object middleWriteObj) throws BodyFormatException {
		return middleWriteObj;
	}	
}
