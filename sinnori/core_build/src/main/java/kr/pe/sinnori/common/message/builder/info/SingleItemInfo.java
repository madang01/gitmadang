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
package kr.pe.sinnori.common.message.builder.info;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.util.CommonStaticUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 단일 항목 정보 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SingleItemInfo extends AbstractItemInfo {
	private final Logger log = LoggerFactory.getLogger(SingleItemInfo.class);

	private String itemName;
	private String itemValueType;	
	private String itemDefaultValue;	
	private String itemSize;
	private String itemCharset;
	
	private String firstUpperItemName;
	private int itemTypeID;
	private int itemSizeForLang;
	private String defaultValueRightValueString = null;	
	private String javaLangTypeOfItemValueType;
	private String JavaLangClassCastingTypeOfItemValueType;

	private static final ItemValueTypeManger itemTypeIDManger = ItemValueTypeManger
			.getInstance();

	/**
	 * 
	 * 단일 항목 정보 클래스 생성자
	 * 
	 * @param itemName
	 *            항목 이름
	 * @param itemValueType
	 *            항목 값의 타입
	 * @param itemDefaultValue 디폴트 값, 
	 * <pre>Warning! 파스칼 문자열 타입에 디폴트 값을 지정할때
	 * 송수신 에러가 발생할 수 있기때문에 주의가 필요하다.
	 * 이는 송수신시 파스칼 문자열 길이에 제약이 있는데 
	 * 이 제약을 어긴 디폴트 값을 지정했기때문이다. 
	 * 이 제약을 검사하지 않고 디폴트 값을 허용하는 이유는 
	 * 검사 수행 시점을 송수신 할때로 보류 했기때문이다.
	 * 
	 * 검사 수행 시점을 송수신 할때로 보류한 이유는 다음과 같다. 
	 * 비지니스 로직 수행 과정에서 파스칼 문자열 타입 항목은 
	 * 송수신시 제약사항과 상관없는 값을 가질 수 있기때문이다. 
	 * 또한 부가적인 이유중 하나는 파스칼 문자열 길이는 
	 * 지정한 문자셋을 갖는 바이트 배열의 크기인데, 
	 * 파스칼 문자열 항목의 문자셋은 옵션이고, 
	 * 미 지정시 갖게 되는 문자셋은
	 * 작업중인 프로젝트의 환경변수 '문자셋' 인데 
	 * 작업중인 프로젝트는 송수신때 결정되기때문이다.</pre> 
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
		int itemTypeID = -1;
		
		try {
			itemTypeID = getItemValueTypeIDAfterCheckingParmItemValueType(itemValueType);
		} catch(UnknownItemTypeException e) {
			log.warn(e.toString(), e);
			String errorMessage = new StringBuilder(
					"this single item[")
					.append(itemName).append("]'s attribute 'type' value[")
					.append(itemValueType)
					.append("]) is not an element of item value type set").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValueType.equals("byte")) {
			makeByteTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("unsigned byte")) {
			makeUnsignedByteTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("short")) {
			makeShortTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("unsigned short")) {
			makeUnsignedShortTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("integer")) {
			makeIntegerTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("unsigned integer")) {
			makeUnsignedIntegerTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("long")) {
			makeLongTypeInformationAfterCheckingAdditionalInformation(itemName,
					itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("ub pascal string")) {
			makePascalStringTypeInformationAfterCheckingAdditionalInformation("ub",
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("us pascal string")) {
			makePascalStringTypeInformationAfterCheckingAdditionalInformation("us",
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("si pascal string")) {
			makePascalStringTypeInformationAfterCheckingAdditionalInformation("si",
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("fixed length string")) {
			makeFixedLengthStringTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("ub variable length byte[]")) {
			makeVariableLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
					"ub", itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("us variable length byte[]")) {
			makeVariableLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
					"us", itemName, itemDefaultValue, itemSize, itemCharset);

		} else if (itemValueType.equals("si variable length byte[]")) {
			makeVariableLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
					"si", itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("fixed length byte[]")) {
			makeFixedLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("java sql date")) {
			makeJavaSqlDateTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("java sql timestamp")) {
			makeJavaSqlTimestampTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else if (itemValueType.equals("boolean")) {
			makeBooleanTypeInformationAfterCheckingAdditionalInformation(
					itemName, itemDefaultValue, itemSize, itemCharset);
		} else {
			String errorMessage = new StringBuilder("this single item[")
					.append(itemName).append("]'s type[").append(itemValueType)
					.append("] is a unknown type").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.itemName = itemName;
		this.itemTypeID = itemTypeID;
		this.firstUpperItemName = new StringBuilder(
				itemName.substring(0, 1).toUpperCase())
		.append(itemName.substring(1)).toString();
		this.itemValueType = itemValueType;
		this.itemDefaultValue = itemDefaultValue;
		this.itemSize = itemSize;
		this.itemCharset = itemCharset;
	}

	public String getFirstUpperItemName() {
		return firstUpperItemName;
	}

	public String getJavaLangTypeOfItemValueType() {
		return javaLangTypeOfItemValueType;
	}

	public String getJavaLangClassCastingTypeOfItemValueType() {
		return JavaLangClassCastingTypeOfItemValueType;
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

	public String getDefaultValueRightValueString() {
		return defaultValueRightValueString;
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

	private void checkParmItemName(String itemName)
			throws IllegalArgumentException {
		if (null == itemName) {
			throw new IllegalArgumentException("the parmamter itemName is null");
		}

		if (itemName.length() < 2) {
			throw new IllegalArgumentException(
					"this single item's attribute 'name' value length is greater than or eqaul to 2");
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
		StringBuilder regularStr = new StringBuilder(
				"^[:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d");
		regularStr
				.append("\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff");
		regularStr
				.append("\\uf900-\\ufdcf\\ufdf0-\\ufffd\\uD800\\uDC00-\\uDB7F\\uDFFF]");
		regularStr.append("[:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6");
		regularStr
				.append("\\u00F8-\\u02ff\\u0370-\\u037d\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f");
		regularStr
				.append("\\u2c00-\\u2fef\\u3001-\\udfff\\uf900-\\ufdcf\\ufdf0-\\ufffd\\uD800\\uDC00-\\uDB7F\\uDFFF\\-\\.0-9");
		regularStr.append("\\u00b7\\u0300-\\u036f\\u203f-\\u2040]*\\Z");

		if (!itemName.matches(regularStr.toString())) {
			String errorMessage = new StringBuilder("this single item name[")
					.append(itemName)
					.append("] should be decided in accordance with java xml tag name rule")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemName.toLowerCase().indexOf("xml") == 0) {
			/**
			 * -------- http://www.w3.org/TR/xml/#NT-Name 사이트 부분 인용 ----------
			 * Names beginning with the string "xml", or with any string which
			 * would match (('X'|'x') ('M'|'m') ('L'|'l')), are reserved for
			 * standardization in this or future versions of this specification.
			 */
			String errorMessage = new StringBuilder("this single item name[")
					.append(itemName)
					.append("] must not begin with the string 'xml' that would match (('X'|'x') ('M'|'m') ('L'|'l'))")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

	private int getItemValueTypeIDAfterCheckingParmItemValueType(
			String itemValueType) throws IllegalArgumentException, UnknownItemTypeException {
		if (null == itemValueType) {
			throw new IllegalArgumentException("the parmamter itemType is null");
		}
		int itemTypeID = itemTypeIDManger.getItemValueTypeID(itemValueType);
		
		return itemTypeID;
	}

	private void makeByteTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			@SuppressWarnings("unused")
			Byte resultValue = null;
			try {
				resultValue = Byte.parseByte(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal byte").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "byte";
		JavaLangClassCastingTypeOfItemValueType = "Byte";
	}

	private void makeUnsignedByteTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			Short resultValue = null;
			try {
				resultValue = Short.parseShort(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal short").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is greater than unsigned byte max[")
						.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "short";
		JavaLangClassCastingTypeOfItemValueType = "Short";
	}

	private void makeShortTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			@SuppressWarnings("unused")
			Short resultValue = null;
			try {
				resultValue = Short.parseShort(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal short").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'short' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'short' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "short";
		JavaLangClassCastingTypeOfItemValueType = "Short";
	}

	private void makeUnsignedShortTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			Integer resultValue = null;
			try {
				resultValue = Integer.parseInt(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal integer").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is greater than unsigned short max[")
						.append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned short' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned short' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "int";
		JavaLangClassCastingTypeOfItemValueType = "Integer";
	}

	private void makeIntegerTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			@SuppressWarnings("unused")
			Integer resultValue = null;
			try {
				resultValue = Integer.parseInt(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal integer").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'integer' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'integer' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "int";
		JavaLangClassCastingTypeOfItemValueType = "Integer";
	}

	private void makeUnsignedIntegerTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			Long resultValue = null;
			try {
				resultValue = Long.parseLong(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal long").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] is greater than unsigned integer max[")
						.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned integer' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned integer' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "long";
		JavaLangClassCastingTypeOfItemValueType = "Long";
	}

	private void makeLongTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			@SuppressWarnings("unused")
			Long resultValue = null;
			try {
				resultValue = Long.parseLong(itemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'long' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("]) as a signed decimal long").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemSize) {
			String errorMessage = new StringBuilder(
					"this 'long' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder(
					"this 'long' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "long";
		JavaLangClassCastingTypeOfItemValueType = "Long";
	}

	private void makePascalStringTypeInformationAfterCheckingAdditionalInformation(
			String pascalStringGubun, String itemName, String itemDefaultValue,
			String itemSize, String itemCharset)
			throws IllegalArgumentException {
		if (null == pascalStringGubun) {
			throw new IllegalArgumentException("the parameter pascalStringGubun is null");
		}
		if (null == itemName) {
			throw new IllegalArgumentException("the parameter itemName is null");
		}
		
		Set<String> pascalStringGubunSet = new HashSet<String>();
		pascalStringGubunSet.add("ub");
		pascalStringGubunSet.add("us");
		pascalStringGubunSet.add("si");
		if (! pascalStringGubunSet.contains(pascalStringGubun)) {
			throw new IllegalArgumentException("the parameter pascalStringGubun is not an element of pascal string gubun set[ub, us, si]");
		}
		
		if (null != itemDefaultValue) {
			if (CommonStaticUtil
					.hasLeadingOrTailingWhiteSpace(itemDefaultValue)) {
				String errorMessage = new StringBuilder("this '")
						.append(pascalStringGubun)
						.append(" pascal string' type single item[")
						.append(itemName)
						.append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		if (null != itemSize) {
			String errorMessage = new StringBuilder("this '")
					.append(pascalStringGubun)
					.append(" pascal string' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			itemCharset = itemCharset.trim();
			try {
				Charset.forName(itemCharset);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("this '")
						.append(pascalStringGubun)
						.append(" pascal string' type single item[")
						.append(itemName).append("]'s attribute 'charset' value[")
						.append(itemCharset).append("] is a bad charset name").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemDefaultValue) {
			defaultValueRightValueString = new StringBuilder("\"")
					.append(itemDefaultValue).append("\"").toString();
		}
		
		itemSizeForLang = -1;
		javaLangTypeOfItemValueType = "String";
		JavaLangClassCastingTypeOfItemValueType = "String";
	}

	private void makeFixedLengthStringTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			if (CommonStaticUtil
					.hasLeadingOrTailingWhiteSpace(itemDefaultValue)) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName)
						.append("]'s attribute 'defaultValue' value[")
						.append(itemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		if (null == itemSize) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("] needs attribute 'size'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		int itemSizeForLang;
		try {
			itemSizeForLang = Integer.parseInt(itemSize);
		} catch (NumberFormatException num_e) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(itemSize).append("] is not integer").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemSizeForLang <= 0) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(itemSizeForLang)
					.append("] must be greater than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(itemCharset)) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName)
						.append("]'s attribute 'charset' value[")
						.append(itemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			try {
				Charset.forName(itemCharset);
			} catch (Exception e) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName).append("]'s attribute 'charset' value[")
						.append(itemCharset).append("] is a bad charset name").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != itemDefaultValue) {
			defaultValueRightValueString = new StringBuilder("\"")
					.append(itemDefaultValue).append("\"").toString();
		}
		
		this.itemSizeForLang = itemSizeForLang;
		javaLangTypeOfItemValueType = "String";
		JavaLangClassCastingTypeOfItemValueType = "String";
	}

	private void makeVariableLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
			String variableLengthByteArrayGubun, String itemName,
			String itemDefaultValue, String itemSize, String itemCharset)
			throws IllegalArgumentException {
		if (null == variableLengthByteArrayGubun) {
			throw new IllegalArgumentException("the parameter variableLengthByteArrayGubun is null");
		}
		if (null == itemName) {
			throw new IllegalArgumentException("the parameter itemName is null");
		}
		
		Set<String> variableLengthByteArrayGubunSet = new HashSet<String>();
		variableLengthByteArrayGubunSet.add("ub");
		variableLengthByteArrayGubunSet.add("us");
		variableLengthByteArrayGubunSet.add("si");
		if (! variableLengthByteArrayGubunSet.contains(variableLengthByteArrayGubun)) {
			throw new IllegalArgumentException("the parameter variableLengthByteArrayGubun is not an element of variable length byte array gubun set[ub, us, si]");
		}
		
		if (null != itemDefaultValue) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayGubun)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'defaultValue'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != itemSize) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayGubun)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayGubun)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSizeForLang = -1;
		defaultValueRightValueString = null;
		javaLangTypeOfItemValueType = "byte[]";
		JavaLangClassCastingTypeOfItemValueType = "byte[]";
	}
	
	private void makeFixedLengthByteArrayTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == itemSize) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("] needs attribute 'size'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		int itemSizeForLang;
		try {
			itemSizeForLang = Integer.parseInt(itemSize);
		} catch (NumberFormatException num_e) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(itemSize).append("] is not integer").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemSizeForLang <= 0) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(itemSizeForLang)
					.append("] must be greater than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSizeForLang = itemSizeForLang;
		defaultValueRightValueString = null;
		javaLangTypeOfItemValueType = "byte[]";
		JavaLangClassCastingTypeOfItemValueType = "byte[]";
	}
	
	private void makeJavaSqlDateTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != itemSize) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSizeForLang = -1;
		defaultValueRightValueString = null;
		javaLangTypeOfItemValueType = "java.sql.Date";
		JavaLangClassCastingTypeOfItemValueType = "java.sql.Date";
	}
	
	private void makeJavaSqlTimestampTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != itemSize) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSizeForLang = -1;
		defaultValueRightValueString = null;
		javaLangTypeOfItemValueType = "java.sql.Timestamp";
		JavaLangClassCastingTypeOfItemValueType = "java.sql.Timestamp";
	}
	
	private void makeBooleanTypeInformationAfterCheckingAdditionalInformation(
			String itemName, String itemDefaultValue, String itemSize,
			String itemCharset) throws IllegalArgumentException {
		if (null != itemDefaultValue) {
			if (!itemDefaultValue.equals("true") && !itemDefaultValue.equals("false")) {
				String errorMessage = new StringBuilder("this 'boolean' type single item[")
				.append(itemName)
				.append("]'s attribute 'defaultValue' value[")
				.append(itemDefaultValue)
				.append("] is not an element of boolean set[true, false]")
				.toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
		}
		if (null != itemSize) {
			String errorMessage = new StringBuilder("this 'boolean' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemCharset) {
			String errorMessage = new StringBuilder("this 'boolean' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSizeForLang = -1;
		defaultValueRightValueString = itemDefaultValue;
		javaLangTypeOfItemValueType = "boolean";
		JavaLangClassCastingTypeOfItemValueType = "java.lang.Boolean";
	}


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
