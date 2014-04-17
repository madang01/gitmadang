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


package kr.pe.sinnori.common.io.dhb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.InputStreamIF;
import kr.pe.sinnori.common.io.OutputStreamIF;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.message.ArrayInfo;
import kr.pe.sinnori.common.message.ItemTypeManger;

/**
 * <pre>
 * DHB 프로토콜의 단일 항목 스트림 변환기 구현 클래스.
 * 참고) 항목 타입 독립적인 접근을 위해서 동일 인터페이스를 갖도록 한다.
 * </pre>
 * 
 * @author Jonghoon Won
 *
 */
public class DHBSingleItem2Stream implements DHBSingleItem2StreamIF, CommonRootIF {
	
	private final DHBSingleItemType2StreamIF[] dhbSingleItemType2StreamList = new DHBSingleItemType2StreamIF[] { 
			new DHBSingleItemByte2Stream(), new DHBSingleItemUnsignedByte2Stream(), 
			new DHBSingleItemShort2Stream(), new DHBSingleItemUnsignedShort2Stream(),
			new DHBSingleItemInt2Stream(), new DHBSingleItemUnsignedInt2Stream(), 
			new DHBSingleItemLong2Stream(), new DHBSingleItemUBPascalString2Stream(),
			new DHBSingleItemUSPascalString2Stream(), new DHBSingleItemSIPascalString2Stream(), 
			new DHBSingleItemFixedLengthString2Stream(), new DHBSingleItemUBVariableLengthBytes2Stream(), 
			new DHBSingleItemUSVariableLengthBytes2Stream(), new DHBSingleItemSIVariableLengthBytes2Stream(), 
			new DHBSingleItemFixedLengthBytes2Stream()
	};
	
	/**
	 * 생성자
	 */
	public DHBSingleItem2Stream() {
		ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
		
		int itemTypeCnt = itemTypeManger.getItemTypeCnt();
		
		if (itemTypeCnt != dhbSingleItemType2StreamList.length) {
			String errorMessage = 
					String.format("송신 단일 항목 변환기 목록 크기[%d]와 항목 타입 관리자의 크기[%d]가 다릅니다.", 
							dhbSingleItemType2StreamList.length, itemTypeCnt);
			log.fatal(errorMessage);
			
			log.fatal("송신 단일 항목 변환기 목록 크기와 항목 타입 관리자의 크기가 다릅니다.");
			System.exit(1);
		}
		
		for (int i=0; i < dhbSingleItemType2StreamList.length; i++) {
			String itemType = dhbSingleItemType2StreamList[i].getItemType();
			try {
				int itemTypeID = itemTypeManger.getItemTypeID(itemType);
				
				if (itemTypeID != i) {
					String errorMessage = 
							String.format("항목 타입 식별자[%d]와 단일 항목 변환기 구현 클래스의 항목 타입[%s]의 항목 타입 식별자[%d] 가 일치하지 않습니다.", 
									i, itemType, itemTypeID);
					log.fatal(errorMessage);
					System.exit(1);
				}
			} catch (UnknownItemTypeException e) {
				log.fatal("UnknownItemTypeException", e);
				System.exit(1);
			}
			
			
			// readConverHash.put(itemTypeOfRead, readConverList[i]);
			// writeConverHash.put(itemTypeOfRead, writeConverList[i]);
		}
	}
	
	
	
	@Override
	public void I2S(String itemName, int itemTypeID, 
			Object itemValue, int itemSizeForLang, Charset itemCharsetForLang, OutputStreamIF sw)
			throws BodyFormatException, IllegalArgumentException, BufferOverflowException, NoMoreDataPacketBufferException {
		
		
		dhbSingleItemType2StreamList[itemTypeID].putValue(itemName, itemValue, itemSizeForLang, itemCharsetForLang, sw);
		
	}
	
	@Override
	public void S2I(String itemName, int itemTypeID, 
			int itemSizeForLang, Charset itemCharsetForLang, 
			HashMap<String, Object> itemValueHash, InputStreamIF sr)
			throws SinnoriCharsetCodingException, SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException {

		itemValueHash.put(itemName,
				dhbSingleItemType2StreamList[itemTypeID].getValue(itemName, itemSizeForLang, itemCharsetForLang, sr));
	}
	
	@Override
	public void writeGroupHead(String groupName, ArrayInfo arrayInfo, OutputStreamIF sw) throws BodyFormatException, NoMoreDataPacketBufferException {
		// DHB는 아무일 안함
		return;
	}

	@Override
	public void readGroupHead(String groupName, ArrayInfo arrayInfo, InputStreamIF sr)
			throws BodyFormatException {
		// DHB는 아무일 안함
		return;
	}

	@Override
	public void writeGroupTail(String groupName, ArrayInfo arrayInfo, OutputStreamIF sw) throws BodyFormatException, NoMoreDataPacketBufferException {
		// DHB는 아무일 안함
		return;
	}

	@Override
	public void readGroupTail(String groupName, ArrayInfo arrayInfo, InputStreamIF sr)
			throws BodyFormatException {
		// DHB는 아무일 안함
		return;
	}
	
	
	/** DHB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemByte2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getByte();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			byte value = 0;
			
			if (null != itemValue) {
				value = (Byte) itemValue;
			}
			
			sw.putByte(value);
		}
		
		@Override
		public String getItemType() {
			return "byte";
		}
	}

	/** DHB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUnsignedByte2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getUnsignedByte();
		}

		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			sw.putUnsignedByte(value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned byte";
		}
	}

	/** DHB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemShort2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getShort();
		}

		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			
			sw.putShort(value);
		}
		
		@Override
		public String getItemType() {
			return "short";
		}
	}

	/** DHB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUnsignedShort2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getUnsignedShort();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			int value = 0;	
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}

			sw.putUnsignedShort(value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned short";
		}
	}

	/** DHB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemInt2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getInt();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			int value = 0;
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}
			
			sw.putInt(value);
		}
		
		@Override
		public String getItemType() {
			return "integer";
		}
	}

	/** DHB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUnsignedInt2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getUnsignedInt();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			sw.putUnsignedInt(value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned integer";
		}
	}

	/** DHB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemLong2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getLong();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			sw.putLong(value);
		}
		
		@Override
		public String getItemType() {
			return "long";
		}
	}

	/** DHB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUBPascalString2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getUBPascalString();
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinal.EMPTY_STRING;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putUBPascalString(value);
		}
		
		@Override
		public String getItemType() {
			return "ub pascal string";
		}
	}

	/** DHB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUSPascalString2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getUSPascalString();
		}

		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putUSPascalString(value);
		}

		@Override
		public String getItemType() {
			return "us pascal string";
		}
	}

	/** DHB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemSIPascalString2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			return sr.getSIPascalString();
		}

		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putSIPascalString(value);
		}
		
		@Override
		public String getItemType() {
			return "si pascal string";
		}
	}

	/** DHB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemFixedLengthString2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			if (null == itemCharsetForLang) {
				return sr.getString(itemSizeForLang).trim();
			} else {
				return sr.getString(itemSizeForLang, CharsetUtil.createCharsetDecoder(itemCharsetForLang)).trim();
			}
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			if (null == itemCharsetForLang) {
				sw.putString(itemSizeForLang, value);
			} else {
				sw.putString(itemSizeForLang, value,
						CharsetUtil.createCharsetEncoder(itemCharsetForLang));
			}

		}
		
		@Override
		public String getItemType() {
			return "fixed length string";
		}
	}

	

	/** DHB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUBVariableLengthBytes2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			short len = sr.getUnsignedByte();
			return sr.getBytes(len);
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putUnsignedByte((short)0);
			} else {
				byte value[] = (byte[]) itemValue;
				/*
				if (realValue.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
					throw new IllegalArgumentException(String.format(
							"파라미터 바이트 배열 길이[%d]는 unsigned byte 최대값[%d]을 넘을 수 없습니다.", realValue.length, CommonStaticFinal.MAX_UNSIGNED_BYTE));
				}
				*/
				sw.putUnsignedByte(value.length);
				sw.putBytes(value);
			}
		}
		
		@Override
		public String getItemType() {
			return "ub variable length byte[]";
		}
	}

	/** DHB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemUSVariableLengthBytes2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			int len = sr.getUnsignedShort();
			return sr.getBytes(len);
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putUnsignedShort(0);
			} else {
				byte value[] = (byte[]) itemValue;
				sw.putUnsignedShort(value.length);
				sw.putBytes(value);
			}
		}
		
		@Override
		public String getItemType() {
			return "us variable length byte[]";
		}
	}
	
	/** DHB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemSIVariableLengthBytes2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {
			int len = sr.getInt();
			return sr.getBytes(len);
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putInt(0);
			} else {

				byte value[] = (byte[]) itemValue;
				sw.putInt(value.length);
				sw.putBytes(value);
			}
		}
		
		@Override
		public String getItemType() {
			return "si variable length byte[]";
		}
	}
	
	/** DHB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DHBSingleItemFixedLengthBytes2Stream implements DHBSingleItemType2StreamIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, InputStreamIF sr) throws SinnoriBufferUnderflowException,
				IllegalArgumentException, SinnoriCharsetCodingException {

			return sr.getBytes(itemSizeForLang);
		}
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {

				byte value[] = new byte[itemSizeForLang];
				Arrays.fill((byte[]) value, (byte) 0);
				sw.putBytes((byte[]) value, 0, itemSizeForLang);
			} else {
				byte value[] = (byte[]) itemValue;	

				if (value.length != itemSizeForLang) {
					throw new IllegalArgumentException(
							String.format(
									"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									value.length, itemSizeForLang));
				}
				
				
				sw.putBytes(value);
			}
		}
		
		@Override
		public String getItemType() {
			return "fixed length byte[]";
		}
	}
		
}
