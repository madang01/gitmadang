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

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.InputStreamIF;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.ItemTypeManger;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * DHB 프로톨 단일 항목 디코더
 * @author "Jonghoon Won"
 *
 */
public class DHBSingleItemDecoder implements SingleItemDecoderIF, CommonRootIF {
	private interface DHBTypeSingleItemDecoderIF {
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws Exception;
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
			new DHBJavaSqlDateSingleItemDecoder(),  new DHBJavaSqlTimestampSingleItemDecoder()
	};	
	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBByteSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getByte();
		}		
	}

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedByteSingleItemDecoder implements DHBTypeSingleItemDecoderIF {

		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getUnsignedByte();
		}		
	}

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBShortSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getShort();
		}		
	}

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedShortSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getUnsignedShort();
		}		
	}

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBIntSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getInt();
		}		
	}

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUnsignedIntSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException  {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			return sr.getUnsignedInt();
		}		
	}

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBLongSingleItemDecoder implements DHBTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			return sr.getLong();
		}		
	}

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {		
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			return sr.getUBPascalString();
		}		
	}

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			return sr.getUSPascalString();
		}		
	}

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIPascalStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			return sr.getSIPascalString();
		}		
	}

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthStringSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException, BodyFormatException {
			
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			if (null == itemCharsetForLang) {
				return sr.getString(itemSizeForLang).trim();
			} else {
				return sr.getString(itemSizeForLang, CharsetUtil.createCharsetDecoder(itemCharsetForLang)).trim();
			}
		}		
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUBVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			short len = sr.getUnsignedByte();
			return sr.getBytes(len);
		}		
	}

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBUSVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			int len = sr.getUnsignedShort();
			return sr.getBytes(len);
		}		
	}
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSIVariableLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			int len = sr.getInt();
			return sr.getBytes(len);
		}		
	}
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBFixedLengthBytesSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr)
				throws SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			return sr.getBytes(itemSizeForLang);
		}		
	}
	
	/** DHB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlDateSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws BodyFormatException, SinnoriBufferUnderflowException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			long javaSqlDateLongValue = sr.getLong();			
			return new java.sql.Date(javaSqlDateLongValue);
		}
	}
	
	/** DHB 프로토콜의 java sql timestamp 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBJavaSqlTimestampSingleItemDecoder implements DHBTypeSingleItemDecoderIF {
		@Override
		public Object getValue(int itemTypeID, String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws BodyFormatException, SinnoriBufferUnderflowException {
			int receivedItemTypeID = sr.getUnsignedByte();
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
			
			long javaSqlDateLongValue = sr.getLong();			
			return new java.sql.Timestamp(javaSqlDateLongValue);
		}
	}

	@Override
	public Object getValueFromMiddleReadObj(String path, String itemName,
			int itemTypeID, String itemTypeName, int itemSizeForLang,
			String itemCharset, Charset charsetOfProject,
			Object middleReadObj) throws BodyFormatException {
		if (!(middleReadObj instanceof InputStreamIF)) {
			String errorMessage = String.format(
					"스트림으로 부터 생성된 중간 다리역활 객체[%s]의 데이터 타입이 InputStreamIF 이 아닙니다.",
					middleReadObj.getClass().getCanonicalName());
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		Charset itemCharsetForLang = null;
		if (null != itemCharset) {
			try {
				itemCharsetForLang = Charset.forName(itemCharset);
			} catch(Exception e) {
				log.warn("문자셋[{}] 이름이 잘못되었습니다.", itemCharset);
			}
		}
		
		InputStreamIF sr = (InputStreamIF)middleReadObj;
		Object retObj = null;
		try {
			retObj = dhbTypeSingleItemDecoderList[itemTypeID].getValue(itemTypeID, itemName, itemSizeForLang, itemCharsetForLang, sr);
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
			log.info(errorMessage);
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
		return middleReadObj;
	}

	@Override
	public Object getMiddleReadObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}
	
	@Override
	public void finish(Object middleReadObj) throws BodyFormatException {
		if (!(middleReadObj instanceof InputStreamIF)) {
			String errorMessage = String.format(
					"스트림으로 부터 생성된 중간 다리역활 객체[%s]의 데이터 타입이 InputStreamIF 이 아닙니다.",
					middleReadObj.getClass().getCanonicalName());
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		InputStreamIF sr = (InputStreamIF)middleReadObj;
		long remainingBytes = sr.remaining();
		
		sr.close();
		
		if (0 > remainingBytes) {
			String errorMessage = String.format(
					"메시지 추출후 남은 데이터[%d]가 존재합니다.",
					remainingBytes);
			throw new BodyFormatException(errorMessage);
		}
	}
}
