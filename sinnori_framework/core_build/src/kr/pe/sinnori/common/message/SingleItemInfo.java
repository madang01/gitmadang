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

package kr.pe.sinnori.common.message;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.lib.CommonType;

/**
 * 단일 항목 정보 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public class SingleItemInfo extends AbstractItemInfo {
	private String itemName;
	private String itemType;
	private Class<?> itemTypeForLang;
	private int itemTypeID;
	private String itemDefaultValue;
	private Object itemDefaultValueForLang=null;
	private String itemSize;
	private int itemSizeForLang = -1;
	private String itemCharset;
	private Charset itemChasetForLang = null;
	
	// private boolean itemUseYN=false;

	
	
	private static final ItemTypeManger itemTypeIDManger = ItemTypeManger.getInstance(); 
	

	/**
	 * 단일 항목 정보 클래스 생성자
	 * 
	 * @param itemName
	 *            항목 이름
	 * @param itemType
	 *            항목 타입
	 * @param itemDefaultValue
	 *            디폴트 값
	 * @param itemSize
	 *            항목 타입 부가 정보중 하나인 크기
	 * @param itemCharset
	 *            항목 타입 부가 정보중 하나인 문자셋
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 값이 들어올 경우 던지는 예외
	 */
	public SingleItemInfo(String itemName, String itemType,
			String itemDefaultValue, String itemSize, String itemCharset)
			throws IllegalArgumentException {
		this.itemName = itemName;
		this.itemType = itemType;
		this.itemDefaultValue = itemDefaultValue;
		this.itemSize = itemSize;
		this.itemCharset = itemCharset;

		
		try {
			itemTypeID = itemTypeIDManger.getItemTypeID(itemType);
		} catch (UnknownItemTypeException e) {
			log.fatal("UnknownItemTypeException", e);
			System.exit(1);
		}
				
		if (itemType.equals("byte")) {
			itemTypeForLang = java.lang.Byte.class;
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Byte.parseByte(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 byte 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned byte")) {
			itemTypeForLang = java.lang.Short.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned byte 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("short")) {
			itemTypeForLang = java.lang.Short.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 short 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned short")) {
			itemTypeForLang = java.lang.Integer.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned short 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("integer")) {
			itemTypeForLang = java.lang.Integer.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 integer 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned integer")) {
			itemTypeForLang = java.lang.Long.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned integer 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("long")) {
			itemTypeForLang = java.lang.Long.class;
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 long 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					log.warn(errorMessage, nfe);
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("ub pascal string")) {
			itemTypeForLang = java.lang.String.class;
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("us pascal string")) {
			itemTypeForLang = java.lang.String.class;
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("si pascal string")) {
			itemTypeForLang = java.lang.String.class;
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("fixed length string")) {
			itemTypeForLang = java.lang.String.class;
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		
		} else if (itemType.equals("ub variable length byte[]")) {
			itemTypeForLang = byte[].class;
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("us variable length byte[]")) {
			itemTypeForLang = byte[].class;
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("si variable length byte[]")) {
			itemTypeForLang = byte[].class;
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("fixed length byte[]")) {
			itemTypeForLang = byte[].class;
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 fixed length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else {
			/** XSD 에서 항목 타입을 제약한다. 따라서 이곳 로직은 들어 올 수 없다. */
			String errorMessage = String.format("unkown type[%s]", itemType);
			log.warn(errorMessage);
			// System.exit(1);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemSize) {
			try {
				itemSizeForLang = Integer.parseInt(itemSize);
			} catch (NumberFormatException num_e) {
				/** SAX 파싱 과정에서 검사를 해서 이곳 로직을 들어 올 수 없다. */
				String errorMessage = String.format(
						"타입 부가 정보인 크기[%s]가 숫자가 아닙니다.", itemSize);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemCharset) {
			try {
				itemChasetForLang = Charset.forName(itemCharset);
			} catch (Exception e) {
				/** SAX 파싱 과정에서 검사를 해서 이곳 로직을 들어 올 수 없다 */
				String errorMessage = String.format("unkown charset[%s]",
						itemCharset);
				log.warn(errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
	}
	

	/**
	 * 자바 언어에서 쓰이는 항목 타입을 반환한다.
	 * 
	 * @return 자바 언어에서 쓰이는 항목 타입
	 */
	public Class<?> getItemTypeForLang() {
		return itemTypeForLang;
	}
	
	/**
	 * @see ItemTypeManger
	 * @return 항목 타입 식별자
	 */
	public int getItemTypeID() {
		return itemTypeID;
	}

	/**
	 * 정수형 항목 크기를 반환한다.
	 * 
	 * @return 정수형 항목 크기
	 */
	public int getItemSizeForLang() {
		return itemSizeForLang;
	}

	/**
	 * 타입 부과 정보인 문자셋을 반환한다.
	 * 
	 * @return 타입 부가 정보인 문자셋
	 */
	public String getItemCharset() {
		return itemCharset;
	}

	/**
	 * 자바언어에서 쓰이는 타입 부과 정보인 문자셋을 반환한다.
	 * 
	 * @return 자바언어에서 쓰이는 타입 부과 정보인 문자셋
	 */
	public Charset getItemCharsetForLang() {
		return itemChasetForLang;
	}

	/*
	 * public boolean getItemUseYN() { return itemUseYN; } public void
	 * enableItemUseYN() { this.itemUseYN = true; }
	 */

	/**
	 * 항목 타입을 반환한다.
	 * 
	 * @return 항목 타입
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * 타입 부과 정보인 크기를 반환한다.
	 * 
	 * @return 타입 부과 정보인 크기
	 */
	public String getItemSize() {
		return itemSize;
	}

	/**
	 * 디폴트 값을 반환한다.
	 * 
	 * @return 디폴트 값
	 */
	public String getItemDefaultValue() {
		return itemDefaultValue;
	}
	
	public Object getItemDefaultValueForLang() {
		return itemDefaultValueForLang;
	}

	

	/******************* AbstractItemInfo start ***********************/
	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public CommonType.LOGICAL_ITEM_GUBUN getLogicalItemGubun() {
		return CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM;
	}

	/******************* AbstractItemInfo end ***********************/

	

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ itemName=[");
		strBuff.append(itemName);
		strBuff.append("], itemType=[");
		strBuff.append(itemType);
		strBuff.append("], itemDefaultValue=[");
		strBuff.append(itemDefaultValue);
		strBuff.append("], itemSize=[");
		strBuff.append(itemSize);
		strBuff.append("], itemCharset=[");
		strBuff.append(itemCharset);
		strBuff.append("] }");

		return strBuff.toString();
	}
}
