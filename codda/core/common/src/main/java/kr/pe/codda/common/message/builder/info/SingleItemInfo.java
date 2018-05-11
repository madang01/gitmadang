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
package kr.pe.codda.common.message.builder.info;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.UnknownItemTypeException;
import kr.pe.codda.common.type.ItemInfoType;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.type.SingleItemType;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 단일 항목 정보 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SingleItemInfo extends AbstractItemInfo {
	private final InternalLogger log = InternalLoggerFactory.getInstance(SingleItemInfo.class);

	private String itemName;
	private SingleItemType itemType;
	private String itemTypeName;	
	private String nativeItemDefaultValue;	
	private String nativeItemSize;
	private String nativeItemCharset;
	
	private String firstUpperItemName;
	private int itemTypeID;
	private int itemSize;
	private String defaultValueForVariableDeclarationPart = null;	
	private String javaLangTypeOfItemType;
	private String JavaLangClassCastingTypeOfItemType;

	private static final SingleItemTypeManger singleItemTypeManger = SingleItemTypeManger
			.getInstance();

	/**
	 * 
	 * 단일 항목 정보 클래스 생성자
	 * 
	 * @param itemName
	 *            항목 이름
	 * @param itemTypeName
	 *            항목 값의 타입
	 * @param nativeItemDefaultValue 디폴트 값, 
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
	 * @param nativeItemSize
	 *            항목 타입 부가 정보중 하나인 크기
	 * @param nativeItemCharset
	 *            항목 타입 부가 정보중 하나인 문자셋
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 값이 들어올 경우 던지는 예외
	 */
	public SingleItemInfo(String itemName, String itemTypeName,
			String nativeItemDefaultValue, String nativeItemSize, String nativeItemCharset)
			throws IllegalArgumentException {
		checkParmItemName(itemName);
		
		try {
			itemType = singleItemTypeManger.getSingleItemType(itemTypeName);
		} catch(UnknownItemTypeException e) {
			log.warn(e.toString(), e);
			String errorMessage = new StringBuilder(
					"this single item[")
					.append(itemName).append("]'s attribute 'type' value[")
					.append(itemTypeName)
					.append("]) is not an element of item value type set").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		
		switch(itemType) {
			case SELFEXN_ERROR_PLACE : {
				makeSourceBuilderInformationOfSelfExnErrorPlace(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			case SELFEXN_ERROR_TYPE : {
				makeSourceBuilderInformationOfSelfExnErrorType(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			case BYTE : {
				makeSourceBuilderInformationOfByte(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case UNSIGNED_BYTE : {
				makeSourceBuilderInformationOfUnsignedByte(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case SHORT : {
				makeSourceBuilderInformationOfShort(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case UNSIGNED_SHORT : {
				makeSourceBuilderInformationOfUnsignedShort(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case INTEGER : {
				makeSourceBuilderInformationOfInteger(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case UNSIGNED_INTEGER : {
				makeSourceBuilderInformationOfUnsignedInteger(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case LONG : {
				makeSourceBuilderInformationOfLong(itemName,
						nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case UB_PASCAL_STRING : {
				makeSourceBuilderInformationOfPascalString("ub",
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case US_PASCAL_STRING : {
				makeSourceBuilderInformationOfPascalString("us",
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case SI_PASCAL_STRING : {
				makeSourceBuilderInformationOfPascalString("si",
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case FIXED_LENGTH_STRING : {
				makeSourceBuilderInformationOfFixedLengthString(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case UB_VARIABLE_LENGTH_BYTES : {
				makeSourceBuilderInformationOfVariableLengthByteArray(
						"ub", itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case US_VARIABLE_LENGTH_BYTES : {
				makeSourceBuilderInformationOfVariableLengthByteArray(
						"us", itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case SI_VARIABLE_LENGTH_BYTES : {
				makeSourceBuilderInformationOfVariableLengthByteArray(
						"si", itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case FIXED_LENGTH_BYTES : {
				makeSourceBuilderInformationOfFixedLengthByteArray(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			case JAVA_SQL_DATE : {
				makeSourceBuilderInformationOfJavaSqlDate(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case JAVA_SQL_TIMESTAMP : {
				makeSourceBuilderInformationOfJavaSqlTimestamp(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			case BOOLEAN : {
				makeSourceBuilderInformationOfBoolean(
						itemName, nativeItemDefaultValue, nativeItemSize, nativeItemCharset);
				break;
			}
			
			default : {
				String errorMessage = new StringBuilder("this single item[")
						.append(itemName).append("]'s type[").append(itemTypeName)
						.append("] is a unknown type").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		this.itemName = itemName;
		this.itemTypeID = itemType.getItemTypeID();
		this.firstUpperItemName = new StringBuilder(
				itemName.substring(0, 1).toUpperCase())
		.append(itemName.substring(1)).toString();
		this.itemTypeName = itemTypeName;
		this.nativeItemDefaultValue = nativeItemDefaultValue;
		this.nativeItemSize = nativeItemSize;
		this.nativeItemCharset = nativeItemCharset;
	}
	
	public SingleItemType getItemType() {
		return itemType;
	}

	public String getFirstUpperItemName() {
		return firstUpperItemName;
	}

	public String getJavaLangTypeOfItemType() {
		return javaLangTypeOfItemType;
	}

	public String getJavaLangClassCastingTypeOfItemType() {
		return JavaLangClassCastingTypeOfItemType;
	}

	/**
	 * @see SingleItemTypeManger
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
	public int getItemSize() {
		return itemSize;
	}

	/**
	 * 타입 부과 정보인 문자셋을 반환한다.
	 * 
	 * @return 타입 부가 정보인 문자셋
	 */
	public String getNativeItemCharset() {
		return nativeItemCharset;
	}

	/**
	 * 항목 타입을 반환한다.
	 * 
	 * @return 항목 타입
	 */
	public String getItemTypeName() {
		return itemTypeName;
	}

	/**
	 * 타입 부과 정보인 크기를 반환한다.
	 * 
	 * @return 타입 부과 정보인 크기
	 */
	public String getNatvieItemSize() {
		return nativeItemSize;
	}

	/**
	 * 디폴트 값을 반환한다.
	 * 
	 * @return 디폴트 값
	 */
	/*public String getNativeItemDefaultValue() {
		return nativeItemDefaultValue;
	}*/

	/**
	 * @return IO 소스 생성기에서 변수 선언부에 쓰일 문자열로 표현된 디폴트 값
	 */
	public String getDefaultValueForVariableDeclarationPart() {
		return defaultValueForVariableDeclarationPart;
	}

	/******************* AbstractItemInfo start ***********************/
	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public ItemInfoType getItemInfoType() {
		return ItemInfoType.SINGLE;
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
	
	private void makeSourceBuilderInformationOfSelfExnErrorPlace(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'defaultValue'").toString();
			throw new IllegalArgumentException(errorMessage);
		}	

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = null;
		this.javaLangTypeOfItemType = SelfExn.ErrorPlace.class.getName();
		this.JavaLangClassCastingTypeOfItemType = javaLangTypeOfItemType;
	}

	
	private void makeSourceBuilderInformationOfSelfExnErrorType(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'defaultValue'").toString();
			throw new IllegalArgumentException(errorMessage);
		}	

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'selfexn error place' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = null;
		this.javaLangTypeOfItemType = SelfExn.ErrorType.class.getName();
		this.JavaLangClassCastingTypeOfItemType = javaLangTypeOfItemType;
	}
	
	private void makeSourceBuilderInformationOfByte(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			@SuppressWarnings("unused")
			Byte resultValue = null;
			try {
				resultValue = Byte.parseByte(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal byte").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "byte";
		this.JavaLangClassCastingTypeOfItemType = "Byte";
	}

	private void makeSourceBuilderInformationOfUnsignedByte(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			Short resultValue = null;
			try {
				resultValue = Short.parseShort(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal short").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned byte' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is greater than unsigned byte max[")
						.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned byte' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "short";
		this.JavaLangClassCastingTypeOfItemType = "Short";
	}

	private void makeSourceBuilderInformationOfShort(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			@SuppressWarnings("unused")
			Short resultValue = null;
			try {
				resultValue = Short.parseShort(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal short").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'short' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'short' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "short";
		this.JavaLangClassCastingTypeOfItemType = "Short";
	}

	private void makeSourceBuilderInformationOfUnsignedShort(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			Integer resultValue = null;
			try {
				resultValue = Integer.parseInt(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal integer").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned short' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is greater than unsigned short max[")
						.append(CommonStaticFinalVars.UNSIGNED_SHORT_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned short' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned short' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "int";
		this.JavaLangClassCastingTypeOfItemType = "Integer";
	}

	private void makeSourceBuilderInformationOfInteger(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			@SuppressWarnings("unused")
			Integer resultValue = null;
			try {
				resultValue = Integer.parseInt(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal integer").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'integer' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'integer' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "int";
		this.JavaLangClassCastingTypeOfItemType = "Integer";
	}

	private void makeSourceBuilderInformationOfUnsignedInteger(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			Long resultValue = null;
			try {
				resultValue = Long.parseLong(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal long").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue < 0) {
				String errorMessage = new StringBuilder(
						"this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (resultValue > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				String errorMessage = new StringBuilder(
						"this 'unsigned integer' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] is greater than unsigned integer max[")
						.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
						.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'unsigned integer' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'unsigned integer' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "long";
		this.JavaLangClassCastingTypeOfItemType = "Long";
	}

	private void makeSourceBuilderInformationOfLong(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			@SuppressWarnings("unused")
			Long resultValue = null;
			try {
				resultValue = Long.parseLong(nativeItemDefaultValue);
			} catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder(
						"fail to parses the string argument(=this 'long' type single item[")
						.append(itemName).append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("]) as a signed decimal long").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'long' type single item[").append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder(
					"this 'long' type single item[").append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		this.defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		this.javaLangTypeOfItemType = "long";
		this.JavaLangClassCastingTypeOfItemType = "Long";
	}

	private void makeSourceBuilderInformationOfPascalString(
			String pascalStringGubun, String itemName, String nativeItemDefaultValue,
			String nativeItemSize, String nativeItemCharset)
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
		
		if (null != nativeItemDefaultValue) {
			if (CommonStaticUtil
					.hasLeadingOrTailingWhiteSpace(nativeItemDefaultValue)) {
				String errorMessage = new StringBuilder("this '")
						.append(pascalStringGubun)
						.append(" pascal string' type single item[")
						.append(itemName)
						.append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder("this '")
					.append(pascalStringGubun)
					.append(" pascal string' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			nativeItemCharset = nativeItemCharset.trim();
			try {
				Charset.forName(nativeItemCharset);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("this '")
						.append(pascalStringGubun)
						.append(" pascal string' type single item[")
						.append(itemName).append("]'s attribute 'charset' value[")
						.append(nativeItemCharset).append("] is a bad charset name").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemDefaultValue) {
			defaultValueForVariableDeclarationPart = new StringBuilder("\"")
					.append(nativeItemDefaultValue).append("\"").toString();
		}
		
		this.itemSize = -1;
		javaLangTypeOfItemType = "String";
		JavaLangClassCastingTypeOfItemType = "String";
	}

	private void makeSourceBuilderInformationOfFixedLengthString(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			if (CommonStaticUtil
					.hasLeadingOrTailingWhiteSpace(nativeItemDefaultValue)) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName)
						.append("]'s attribute 'defaultValue' value[")
						.append(nativeItemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		if (null == nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("] needs attribute 'size'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		int tempItemSize;
		try {
			tempItemSize = Integer.parseInt(nativeItemSize);
		} catch (NumberFormatException num_e) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(nativeItemSize).append("] is not integer").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (tempItemSize <= 0) {
			String errorMessage = new StringBuilder(
					"this 'fixed length string' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(tempItemSize)
					.append("] must be greater than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(nativeItemCharset)) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName)
						.append("]'s attribute 'charset' value[")
						.append(nativeItemDefaultValue)
						.append("] has hreading or traling white space")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			try {
				Charset.forName(nativeItemCharset);
			} catch (Exception e) {
				String errorMessage = new StringBuilder(
						"this 'fixed length string' type single item[")
						.append(itemName).append("]'s attribute 'charset' value[")
						.append(nativeItemCharset).append("] is a bad charset name").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (null != nativeItemDefaultValue) {
			defaultValueForVariableDeclarationPart = new StringBuilder("\"")
					.append(nativeItemDefaultValue).append("\"").toString();
		}
		
		this.itemSize = tempItemSize;
		javaLangTypeOfItemType = "String";
		JavaLangClassCastingTypeOfItemType = "String";
	}

	private void makeSourceBuilderInformationOfVariableLengthByteArray(
			String variableLengthByteArrayType, String itemName,
			String nativeItemDefaultValue, String nativeItemSize, String nativeItemCharset)
			throws IllegalArgumentException {
		if (null == variableLengthByteArrayType) {
			throw new IllegalArgumentException("the parameter variableLengthByteArrayGubun is null");
		}
		if (null == itemName) {
			throw new IllegalArgumentException("the parameter itemName is null");
		}
		
		Set<String> variableLengthByteArrayGubunSet = new HashSet<String>();
		variableLengthByteArrayGubunSet.add("ub");
		variableLengthByteArrayGubunSet.add("us");
		variableLengthByteArrayGubunSet.add("si");
		if (! variableLengthByteArrayGubunSet.contains(variableLengthByteArrayType)) {
			throw new IllegalArgumentException("the parameter variableLengthByteArrayGubun is not an element of variable length byte array gubun set[ub, us, si]");
		}
		
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayType)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'defaultValue'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayType)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'size'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder("this '")
					.append(variableLengthByteArrayType)
					.append(" variable length byte[]' type single item[")
					.append(itemName)
					.append("] doesn't support attribute 'charset'").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		defaultValueForVariableDeclarationPart = null;
		javaLangTypeOfItemType = "byte[]";
		JavaLangClassCastingTypeOfItemType = "byte[]";
	}
	
	private void makeSourceBuilderInformationOfFixedLengthByteArray(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == nativeItemSize) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("] needs attribute 'size'")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		int tempItemSize;
		try {
			tempItemSize = Integer.parseInt(nativeItemSize);
		} catch (NumberFormatException num_e) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(nativeItemSize).append("] is not integer").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (tempItemSize <= 0) {
			String errorMessage = new StringBuilder(
					"this 'fixed length byte[]' type single item[")
					.append(itemName).append("]'s attribute 'size' value[")
					.append(tempItemSize)
					.append("] must be greater than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder("this 'fixed length byte[]' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = tempItemSize;
		defaultValueForVariableDeclarationPart = null;
		javaLangTypeOfItemType = "byte[]";
		JavaLangClassCastingTypeOfItemType = "byte[]";
	}
	
	private void makeSourceBuilderInformationOfJavaSqlDate(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder("this 'java sql date' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		defaultValueForVariableDeclarationPart = null;
		javaLangTypeOfItemType = "java.sql.Date";
		JavaLangClassCastingTypeOfItemType = "java.sql.Date";
	}
	
	private void makeSourceBuilderInformationOfJavaSqlTimestamp(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'defaultValue'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder("this 'java sql timestamp' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		defaultValueForVariableDeclarationPart = null;
		javaLangTypeOfItemType = "java.sql.Timestamp";
		JavaLangClassCastingTypeOfItemType = "java.sql.Timestamp";
	}
	
	private void makeSourceBuilderInformationOfBoolean(
			String itemName, String nativeItemDefaultValue, String nativeItemSize,
			String nativeItemCharset) throws IllegalArgumentException {
		if (null != nativeItemDefaultValue) {
			if (!nativeItemDefaultValue.equals("true") && !nativeItemDefaultValue.equals("false")) {
				String errorMessage = new StringBuilder("this 'boolean' type single item[")
				.append(itemName)
				.append("]'s attribute 'defaultValue' value[")
				.append(nativeItemDefaultValue)
				.append("] is not an element of boolean set[true, false]")
				.toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
		}
		if (null != nativeItemSize) {
			String errorMessage = new StringBuilder("this 'boolean' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'size'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != nativeItemCharset) {
			String errorMessage = new StringBuilder("this 'boolean' type single item[")
			.append(itemName)
			.append("] doesn't support attribute 'charset'")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.itemSize = -1;
		defaultValueForVariableDeclarationPart = nativeItemDefaultValue;
		javaLangTypeOfItemType = "boolean";
		JavaLangClassCastingTypeOfItemType = "java.lang.Boolean";
	}


	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ sigleItemName=[");
		strBuff.append(itemName);
		strBuff.append("], itemType=[");
		strBuff.append(itemTypeName);
		strBuff.append("], nativeItemDefaultValue=[");
		strBuff.append(nativeItemDefaultValue);
		strBuff.append("], itemSize=[");
		strBuff.append(nativeItemSize);
		strBuff.append("], itemCharset=[");
		strBuff.append(nativeItemCharset);
		strBuff.append("] }");

		return strBuff.toString();
	}
}
