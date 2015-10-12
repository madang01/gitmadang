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
package kr.pe.sinnori.common.message.info;


import java.nio.charset.Charset;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 단일 항목 정보 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public class SingleItemInfo extends AbstractItemInfo {
	private final Logger log = LoggerFactory.getLogger(SingleItemInfo.class);
	
	private String itemName;
	private String firstUpperItemName;
	private String itemValueType;
	private String itemTypeForJavaLang;
	private String itemTypeForJavaLangClassCasting;
	private int itemTypeID;
	private String itemDefaultValue;
	private Object itemDefaultValueForLang=null;
	private String itemSize;
	private int itemSizeForLang = -1;
	private String itemCharset;
	
	private static final ItemValueTypeManger itemTypeIDManger = ItemValueTypeManger.getInstance(); 
	
	
	private void checkParmItemName(String itemName) throws IllegalArgumentException {
		if (null == itemName) {
			throw new IllegalArgumentException("the parmamter itemName is null");
		}
		
		if (itemName.length() < 2) {
			throw new IllegalArgumentException("the parmamter itemName's length is greater than or eqaul to 2");
		}
		
		/**
		 * <pre>
		 * 항목 이름 규칙을 XML tag name rules 로 적용함. 이는 향후 메시지를 XML 으로 표현하고자 하는 포석임.
		 * 주의점) 자바는 내부적으로 UTF-16 을 사용하고 BMP를 벗어나는 U+10000-U+EFFFF 문자들은 2문자를 연결한 4byte로 표시함. 
		 *       U+10000의 자바 문자열 표현 \uD800\uDC00, U+EFFFF의 자바 문자열 표현은 \uDB7F\uDFFF 이다.
		 * 참고 주소 : http://stackoverflow.com/questions/5396164/java-how-to-check-if-string-is-a-valid-xml-element-name
		 * 
		 * -------- http://www.w3.org/TR/xml/#NT-Name 사이트 부분 인용 ----------
		 * Name ::== NameStartChar NameChar*
		 * NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
		 * NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
		 * </pre>
		 */
		StringBuilder regularStr = new StringBuilder("^[:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d");
		regularStr.append("\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff");
		regularStr.append("\\uf900-\\ufdcf\\ufdf0-\\ufffd\\uD800\\uDC00-\\uDB7F\\uDFFF]");
		regularStr.append("[:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6");
		regularStr.append("\\u00F8-\\u02ff\\u0370-\\u037d\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f");
		regularStr.append("\\u2c00-\\u2fef\\u3001-\\udfff\\uf900-\\ufdcf\\ufdf0-\\ufffd\\uD800\\uDC00-\\uDB7F\\uDFFF\\-\\.0-9");
		regularStr.append("\\u00b7\\u0300-\\u036f\\u203f-\\u2040]*\\Z");
		
		if (!itemName.matches(regularStr.toString())) {				
			/*log.warn("this single item name[{}] should be decided in accordance with java xml tag name rule in this message infomation xml file[{}]", 
					itemName, messageInformationXMLFile.getAbsolutePath());
			
			isBadXML = true;
			return;*/
			String errorMessage = new StringBuilder("this single item name[")
			.append(itemName).append("] should be decided in accordance with java xml tag name rule").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemName.toLowerCase().indexOf("xml") == 0) {
			/**
			 * -------- http://www.w3.org/TR/xml/#NT-Name 사이트 부분 인용 ----------  
			 * Names beginning with the string "xml", 
			 * or with any string which would match (('X'|'x') ('M'|'m') ('L'|'l')), 
			 * are reserved for standardization in this or future versions of this specification.
			 */
			String errorMessage = new StringBuilder("this single item name[")
			.append(itemName).append("] must not begin with the string 'xml' that would match (('X'|'x') ('M'|'m') ('L'|'l'))").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	private int getItemValueTypeIDAfterCheckingParmItemValueType(String itemValueType) throws IllegalArgumentException {
		if (null == itemValueType) {
			throw new IllegalArgumentException("the parmamter itemType is null");
		}
		int itemTypeID;
		try {
			itemTypeID = itemTypeIDManger.getItemValueTypeID(itemValueType);
		} catch (UnknownItemTypeException e) {
			log.warn("unknown item type error", e);
			throw new IllegalArgumentException(e.getMessage());
		}
		
		return itemTypeID;
	}

	/**
	 * 단일 항목 정보 클래스 생성자
	 * 
	 * @param itemName
	 *            항목 이름
	 * @param itemValueType
	 *            항목 값의 타입
	 * @param itemDefaultValue
	 *            디폴트 값
	 * @param itemSize
	 *            항목 타입 부가 정보중 하나인 크기
	 * @param itemCharset
	 *            항목 타입 부가 정보중 하나인 문자셋
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 값이 들어올 경우 던지는 예외
	 */
	public SingleItemInfo(String itemName, String itemValueType,
			String itemDefaultValue, String itemSize, String itemCharset)
			throws IllegalArgumentException {		
		checkParmItemName(itemName);
		itemTypeID = getItemValueTypeIDAfterCheckingParmItemValueType(itemValueType);
		
		
		
		if (null != itemSize) {
			try {
				itemSizeForLang = Integer.parseInt(itemSize);

			} catch (NumberFormatException num_e) {					
				/*log.warn("this single item[{}]'s attribute size[{}] must be number in this message infomation xml file[{}]", 
						itemName, itemSize, messageInformationXMLFile.getAbsolutePath());
				isBadXML = true;
				return;*/
				
				String errorMessage = new StringBuilder("this single item[")
				.append(itemName).append("]'s attribute size[")
				.append(itemSize).append("] is not integer").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}		
		
		/**
		 * <pre>
		 * 타입이 고정 문자열 크기일 경우만 문자셋을 지정할 수 있다.
		 * 문자셋 이름이 null 값으로 넘어가면 
		 * 프로토콜 단일 항목 인코더에서는 파라미터로 넘어가는 프로젝트 문자셋으로 지정되며,
		 * 문자셋 이름이 null 이 아니면 
		 * 프로토콜 단일 항목 인코더에서는 문자셋 객체로 변환된다.
		 * 따라서 메시지 정보를 꾸릴때 문자셋 이름이 있을 경우 반듯이 문자셋 객체로 바꿀수 있는 바른 이름인지 검사를 수행해야 한다.  
		 * </pre>
		 */
		
		if (null != itemCharset) {
			itemCharset = itemCharset.trim();
			try {
				Charset.forName(itemCharset);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("this single item[")
				.append(itemName).append("]'s attribute charset[")
				.append(itemCharset).append("] is bad").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}		
		
		if (!itemValueType.equals("fixed length string")) {
			/**
			 * <pre>
			 * 타입이 고정 문자열 크기일 경우만 문자셋을 지정할 수 있다.
			 * 문자셋 이름이 null 값으로 넘어가면 
			 * 프로토콜 단일 항목 인코더에서는 파라미터로 넘어가는 프로젝트 문자셋으로 지정되며,
			 * 문자셋 이름이 null 이 아니면 
			 * 프로토콜 단일 항목 인코더에서는 문자셋 객체로 변환된다.
			 * 따라서 메시지 정보를 꾸릴때 문자셋 이름이 있을 경우 반듯이 문자셋 객체로 바꿀수 있는 바른 이름인지 검사를 수행해야 한다.  
			 * </pre>
			 */
			itemCharset = null;
		}
				
		if (itemValueType.equals("byte")) {
			itemTypeForJavaLang = "byte";
			itemTypeForJavaLangClassCasting = "Byte";
			
			if (null != itemDefaultValue) {
				Byte resultValue = null;
				try {
					resultValue = Byte.parseByte(itemDefaultValue);
				} catch(NumberFormatException nfe) {				
					String errorMessage = new StringBuilder("fail to parses the string argument(=this single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal byte").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("unsigned byte")) {
			itemTypeForJavaLang = "short";
			itemTypeForJavaLangClassCasting = "Short";
			
			if (null != itemDefaultValue) {
				Short resultValue = null;
				try {
					resultValue = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'unsigned byte' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal short").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue < 0) {
					String errorMessage = new StringBuilder("this 'unsigned byte' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is less than zero").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
					String errorMessage = new StringBuilder("this 'unsigned byte' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is greater than unsinged byte max[")
					.append(CommonStaticFinalVars.MAX_UNSIGNED_BYTE).append("]").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("short")) {
			itemTypeForJavaLang = "short";
			itemTypeForJavaLangClassCasting = "Short";
			
			if (null != itemDefaultValue) {
				Short resultValue = null;
				try {
					resultValue = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'short' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal short").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("unsigned short")) {
			itemTypeForJavaLang = "int";
			itemTypeForJavaLangClassCasting = "Integer";
			
			if (null != itemDefaultValue) {
				Integer resultValue = null;
				try {
					resultValue = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'unsigned short' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal integer").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue < 0) {
					String errorMessage = new StringBuilder("this 'unsigned short' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is less than zero").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
					String errorMessage = new StringBuilder("this 'unsigned short' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is greater than unsinged short max[")
					.append(CommonStaticFinalVars.MAX_UNSIGNED_SHORT).append("]").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("integer")) {
			itemTypeForJavaLang = "int";
			itemTypeForJavaLangClassCasting = "Integer";
			
			if (null != itemDefaultValue) {
				Integer resultValue = null;
				try {
					resultValue = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'integer' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal integer").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("unsigned integer")) {
			itemTypeForJavaLang = "long";
			itemTypeForJavaLangClassCasting = "Long";
			
			if (null != itemDefaultValue) {
				Long resultValue = null;
				try {
					resultValue = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'unsigned integer' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal long").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue < 0) {
					String errorMessage = new StringBuilder("this 'unsigned integer' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is less than zero").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (resultValue > CommonStaticFinalVars.MAX_UNSIGNED_INTEGER) {
					String errorMessage = new StringBuilder("this 'unsigned integer' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) is greater than unsinged integer max[")
					.append(CommonStaticFinalVars.MAX_UNSIGNED_SHORT).append("]").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("long")) {
			itemTypeForJavaLang = "long";
			itemTypeForJavaLangClassCasting = "Long";
			
			if (null != itemDefaultValue) {
				Long resultValue = null;
				try {
					resultValue = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = new StringBuilder("fail to parses the string argument(=this 'long' type single item[")
					.append(itemName).append("]'s default value[")
					.append(itemDefaultValue).append("]) as a signed decimal long").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				itemDefaultValueForLang = resultValue;
			}
		} else if (itemValueType.equals("ub pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemValueType.equals("us pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemValueType.equals("si pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemValueType.equals("fixed length string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null == itemSize) {				
				String errorMessage = new StringBuilder("this 'fixed length string' type single item[")
				.append(itemName).append("] needs attribute 'size'").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (itemSizeForLang <= 0) {				
				String errorMessage = new StringBuilder("this 'fixed length string' type single item[")
				.append(itemName).append("]'s size[").append(itemSizeForLang)
				.append("] must be greater than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		
		} else if (itemValueType.equals("ub variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemValueType.equals("us variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemValueType.equals("si variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemValueType.equals("fixed length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null == itemSize) {				
				String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
				.append(itemName).append("] needs attribute 'size'").toString();
				throw new IllegalArgumentException(errorMessage);
			}				

			if (itemSizeForLang <= 0) {
				String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
				.append(itemName).append("]'s size[").append(itemSizeForLang)
				.append("] must be bigger than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}		
			
			if (null != itemDefaultValue) {
				String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
				.append(itemName).append("] should not have a default value").toString();
				
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemValueType.equals("java sql date")) {
			itemTypeForJavaLang = "java.sql.Date";
			itemTypeForJavaLangClassCasting = "java.sql.Date";
		} else if (itemValueType.equals("java sql timestamp")) {
			itemTypeForJavaLang = "java.sql.Timestamp";
			itemTypeForJavaLangClassCasting = "java.sql.Timestamp";
		} else if (itemValueType.equals("boolean")) {
			itemTypeForJavaLang = "boolean";
			itemTypeForJavaLangClassCasting = "java.lang.Boolean";
			
		} else {			
			String errorMessage = new StringBuilder("this single item[")
			.append(itemName).append("]'s type[")
			.append(itemValueType)
			.append("] is a unknown type").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		this.itemName = itemName;
		this.firstUpperItemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1); 
		this.itemValueType = itemValueType;
		this.itemDefaultValue = itemDefaultValue;
		this.itemSize = itemSize;
		this.itemCharset = itemCharset;
	}
	
	public String getFirstUpperItemName() {
		return firstUpperItemName;
	}

	
	public String getItemTypeForJavaLang() {
		return itemTypeForJavaLang;
	}
	
	public String getItemTypeForJavaLangClassCasting() {
		return itemTypeForJavaLangClassCasting;
	}
	
	
	/**
	 * @see ItemValueTypeManger
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
	 * 항목 타입을 반환한다.
	 * 
	 * @return 항목 타입
	 */
	public String getItemValueType() {
		return itemValueType;
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
		strBuff.append(itemValueType);
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
