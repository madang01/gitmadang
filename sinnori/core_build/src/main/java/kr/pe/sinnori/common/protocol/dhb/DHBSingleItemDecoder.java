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
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.message.ItemTypeManger;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * DHB 프로톨 단일 항목 디코더
 * @author "Won Jonghoon"
 *
 */
public class DHBSingleItemDecoder implements SingleItemDecoderIF {	
	private Logger log = LoggerFactory.getLogger(DHBSingleItemDecoder.class);
	
	@SuppressWarnings("unused")
	private CharsetDecoder systemCharsetDecoder = null;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	public DHBSingleItemDecoder(CharsetDecoder systemCharsetDecoder) {
		if (null == systemCharsetDecoder) {
			throw new IllegalArgumentException("the parameter systemCharsetDecoder is null");
		}
		this.systemCharsetDecoder = systemCharsetDecoder;
		this.streamCodingErrorActionOnMalformedInput = systemCharsetDecoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = systemCharsetDecoder.unmappableCharacterAction();
	}
	
	private interface DHBTypeSingleItemDecoderIF {
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws Exception;
	}
	
	private final DHBTypeSingleItemDecoderIF[] dhbTypeSingleItemDecoderList = new DHBTypeSingleItemDecoderIF[] { 
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
	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBByteSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getByte();
		}		
	}

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedByteSingleItemDecoder implements DHBTypeSingleItemDecoderIF {

		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getUnsignedByte();
		}		
	}

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBShortSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getShort();
		}		
	}

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedShortSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getUnsignedShort();
		}		
	}

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBIntSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getInt();
		}		
	}

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedIntSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			return binaryInputStream.getUnsignedInt();
		}		
	}

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBLongSingleItemDecoder implements DHBTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			return binaryInputStream.getLong();
		}		
	}

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			return binaryInputStream.getUBPascalString();
		}		
	}

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			return binaryInputStream.getUSPascalString();
		}		
	}

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			return binaryInputStream.getSIPascalString();
		}		
	}

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			if (null == itemCharset) {
				return binaryInputStream.getFixedLengthString(itemSize);
			} else {
				CharsetDecoder userDefinedCharsetDecoder =  itemCharset.newDecoder();
				userDefinedCharsetDecoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				userDefinedCharsetDecoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				return binaryInputStream.getFixedLengthString(itemSize, userDefinedCharsetDecoder);
			}
		}		
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			short len = binaryInputStream.getUnsignedByte();
			return binaryInputStream.getBytes(len);
		}		
	}

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			int len = binaryInputStream.getUnsignedShort();
			return binaryInputStream.getBytes(len);
		}		
	}
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			int len = binaryInputStream.getInt();
			return binaryInputStream.getBytes(len);
		}		
	}
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			return binaryInputStream.getBytes(itemSize);
		}		
	}
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlDateSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws BodyFormatException, SinnoriBufferUnderflowException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Date(javaSqlDateLongValue);
		}
	}
	
	/** DHB 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlTimestampSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws BodyFormatException, SinnoriBufferUnderflowException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			
			long javaSqlDateLongValue = binaryInputStream.getLong();			
			return new java.sql.Timestamp(javaSqlDateLongValue);
		}
	}
	
	/** DHB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBBooleanSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSize,
				Charset itemCharset, BinaryInputStreamIF binaryInputStream) throws BodyFormatException, SinnoriBufferUnderflowException {
			int receivedItemTypeID = binaryInputStream.getUnsignedByte();
			if (itemTypeID != receivedItemTypeID) {
				ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
				String receivedItemTypeName = null;
				try {
					receivedItemTypeName = itemTypeManger.getItemType(receivedItemTypeID);
				} catch (UnknownItemTypeException e) {
					String errorMesssage = String.format("항목 타입[%d][%s] 과 다른 알수 없는 수신받은 항목 타입[%d] 을 수신했습니다.", itemTypeID, itemName, receivedItemTypeID);
					throw new BodyFormatException(errorMesssage);
				}
				
				String errorMesssage = String.format("항목 타입[%d][%s]이 수신 받은 항목 타입[%d][%s] 과 다릅니다.", itemTypeID, itemName, receivedItemTypeID, receivedItemTypeName);
				throw new BodyFormatException(errorMesssage);
			}
			byte booleanByte = binaryInputStream.getByte();
			
			if (booleanByte != 0 && booleanByte != 1) {
				String errorMesssage = String.format("boolean 타입의 항목 값은 참을 뜻하는 1과 거짓을 뜻하는 0 을 갖습니다." +
						"%sboolean 타입의 항목[%s] 값[%d]이  잘못되었습니다. ", 
						CommonStaticFinalVars.NEWLINE, itemName, booleanByte);
				throw new BodyFormatException(errorMesssage);
			}
				
			return Boolean.valueOf(0 != booleanByte);
		}
	}
	

	@Override
	public Object getValueFromReadableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, int itemSize,
			String nativeItemCharset, Charset streamCharset,
			Object readableMiddleObject) throws BodyFormatException {
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
				log.warn(String.format("the parameter nativeItemCharset[%s] is not a bad charset name", nativeItemCharset), e);
			}
		}		
		
		BinaryInputStreamIF binaryInputStream = (BinaryInputStreamIF)readableMiddleObject;
		Object retObj = null;
		try {
			retObj = dhbTypeSingleItemDecoderList[itemTypeID].getValue(itemTypeID, itemName, itemSize, itemCharset, binaryInputStream);
		} catch(IllegalArgumentException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("잘못된 파라미티터 에러::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);			
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
		} catch(SinnoriBufferUnderflowException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("SinnoriBufferUnderflowException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		} catch(SinnoriCharsetCodingException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("SinnoriCharsetCodingException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		} catch(BodyFormatException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("BodyFormatException::");
			errorMessageBuilder.append("{ path=[");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
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
		return retObj;
	}

	@Override
	public Object getArrayObjectFromReadableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object readableMiddleObject)
			throws BodyFormatException {
		return readableMiddleObject;
	}

	@Override
	public Object getReadableMiddleObjFromArrayObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
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
