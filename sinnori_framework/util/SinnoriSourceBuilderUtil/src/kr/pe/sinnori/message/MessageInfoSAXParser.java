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

package kr.pe.sinnori.message;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import kr.pe.sinnori.common.lib.CommonType;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML로 작성된 메시지 정보 파일을 SAX 파싱하여 메시지 정보를 작성하는 클래스.<br/>
 * XML로 작성된 메시지 정보 파일의 구조를 정의하는 XSD 파일과 연계하여 신놀이 메시지 구조 적합성을 검증한다.
 * 
 * @author Jonghoon Won
 * 
 */
public class MessageInfoSAXParser extends DefaultHandler {
	private boolean isFileNameCheck = true;
	private SAXParserFactory parserFact;
	private SAXParser parser;
	private File xmlFile = null;
	// private Charset charsetOfProject = null;
	
	private final String ROOT_TAG = "sinnori_message";
	private String rootTag = null;
	private boolean isBadXML = false;

	private Stack<String> startTagStack = new Stack<String>();
	private Stack<String> tagValueStack = new Stack<String>();
	private Stack<ItemGroupInfoIF> multiItemInfoStack = new Stack<ItemGroupInfoIF>();

	// private String messageID = null;

	/**
	 * 생성자
	 * 
	 * @param xmlFile
	 *            XML로 작성된 메시지 정보 파일
	 */
	public MessageInfoSAXParser(File xmlFile,  boolean isFileNameCheck) {
		// File messageInfoXSDFile = (File)conf.getResource("common.message_info.xsdfile.value");
		this.isFileNameCheck = isFileNameCheck;
		try {
			parserFact = SAXParserFactory.newInstance();
			parserFact.setValidating(false);
			parserFact.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");

			parserFact.setSchema(schemaFactory
					.newSchema(new Source[] { new StreamSource(ItemTypeManger.getInstance().getMesgXSLInputSream()) }));

			parser = parserFact.newSAXParser();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.xmlFile = xmlFile;
		// this.charsetOfProject = charsetOfProject;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		/**
		 * XML 문서 파싱시 에러가 발생하였다면 중지.
		 */
		if (isBadXML)
			return;

		String startTag = qName.toLowerCase();

		/**
		 * 환경 변수에 등재된 루트 태그와 현재 XML 파일의 루트 태그가 일치하는지 검사하여 일치하지 않으면 에러 처리한다.
		 */
		if (null == rootTag) {
			
			if (!startTag.equals(ROOT_TAG)) {
				// log.warn(String.format("환경변수 등재된 루트 태그[%s] 와 읽어 온 XML 파일의 루트 태그[%s]가 일치하지 않습니다.",ROOT_TAG, startTag));
				String errorMessage = String.format(
						"환경변수 등재된 루트 태그[%s] 와 읽어 온 XML 파일의 루트 태그[%s]가 일치하지 않습니다.",
						ROOT_TAG, startTag);
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}
			rootTag = startTag;
		}

		startTagStack.push(startTag);

		/**
		 * 메시지 식별자에 대한 설명을 하는 태그(=desc) 무시. desc 태그의 값이 있을 수 있으므로 startTagStack
		 * 에 desc 태그를 시작 이벤트일때 저장후 태그 끝나는 이벤트일때 제거한다.
		 */
		if (startTag.equals("desc"))
			return;

		if (startTag.equals("singleitem")) {
			ItemGroupInfoIF workItemGroupInfo = multiItemInfoStack.peek();

			String itemName = attributes.getValue("name");
			if (null == itemName) {
				// log.warn(String.format("단일 항목[%s] 필수 속성인 '항목 이름' 없음", startTag));
				String errorMessage = String.format("단일 항목[%s] 필수 속성인 '항목 이름' 없음", startTag);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}

			if (null != workItemGroupInfo.getItemInfo(itemName)) {
				// log.warn(String.format("단일 항목[%s] 필수 속성인 '항목 이름' 중복", startTag));
				String errorMessage = String.format("단일 항목[%s] 필수 속성인 '항목 이름' 중복", startTag);
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
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
				// log.warn(String.format("bad item name[%s], 항목 이름은 XML 이름 규칙을 따른다.", itemName));
				String errorMessage = String.format("bad item name[%s], 항목 이름은 XML 이름 규칙을 따른다.", itemName);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}
			
			if (itemName.toLowerCase().indexOf("xml") == 0) {
				/**
				 * -------- http://www.w3.org/TR/xml/#NT-Name 사이트 부분 인용 ----------  
				 * Names beginning with the string "xml", 
				 * or with any string which would match (('X'|'x') ('M'|'m') ('L'|'l')), 
				 * are reserved for standardization in this or future versions of this specification.
				 */
				// log.warn(String.format("bad item name[%s], 항목 이름은 대소 문자 구분없이 XML 로 시작되는 문자열을 가질 수 없습니다.", itemName));
				String errorMessage = String.format("bad item name[%s], 항목 이름은 대소 문자 구분없이 XML 로 시작되는 문자열을 가질 수 없습니다.", itemName);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}
			
			

			String itemType = attributes.getValue("type");
			if (null == itemType) {
				// log.warn(String.format("단일 항목[%s] 필수 속성인 '항목 타입' 없음", startTag));
				String errorMessage = String.format("단일 항목[%s] 필수 속성인 '항목 타입' 없음", startTag);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}
			
			/**
			try {
				itemTypeManger.getItemTypeID(itemType);
			} catch (UnknownItemTypeException e) {
				log.warn(String.format("[%s] bad item type[%s]", itemName, itemType), e);
				isBadXML = true;
				return;
			}
			*/

			String itemDefaultValue = attributes.getValue("defaultValue");

			String itemSize = attributes.getValue("size");

			int itemSizeForLang = -1;
			if (null != itemSize) {
				try {
					itemSizeForLang = Integer.parseInt(itemSize);

				} catch (NumberFormatException num_e) {
					// log.warn(String.format("단일 항목[%s] 타입 부가 정보인 크기[%s]가 숫자가 아닙니다.", itemName, itemSize));
					String errorMessage = String.format("단일 항목[%s] 타입 부가 정보인 크기[%s]가 숫자가 아닙니다.", itemName, itemSize);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
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
			String itemCharset = attributes.getValue("charset");
			if (null != itemCharset) {
				itemCharset = itemCharset.trim();
				try {
					Charset.forName(itemCharset);
				} catch (Exception e) {
					// log.warn(String.format("단일 항목[%s] 타입 부가 정보인 문자셋[%s]이 옳바르지 않습니다.", itemName, itemCharset));
					String errorMessage = String.format("단일 항목[%s] 타입 부가 정보인 문자셋[%s]이 옳바르지 않습니다.", itemName, itemCharset);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}
			}
			

			/**
			 * '항목 타입'이 바이트 배열일 경우 속성 '항목 크기'는 필수 속성이 되며 이때 고정 크기이면 0 보다 큰값을 갖고
			 * 비 고정 크기이면 -1을 갖는다.
			 */
			if (itemType.equals("fixed length byte[]")) {
				if (null == itemSize) {
					// log.warn(String.format("고정 크기 바이트 배열형 타입 단일 항목[%s]은 크기 지정이 필 수 입니다.", itemName));
					String errorMessage = String.format("고정 크기 바이트 배열형 타입 단일 항목[%s]은 크기 지정이 필 수 입니다.", itemName);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}

				if (itemSizeForLang <= 0) {
					//log.warn(String.format("고정 크기 바이트 배열형 타입 단일 항목[%s]의 크기[%d]는 0보다 커야 합니다.",  itemName, itemSizeForLang));
					String errorMessage = String.format("고정 크기 바이트 배열형 타입 단일 항목[%s]의 크기[%d]는 0보다 커야 합니다.",  itemName, itemSizeForLang);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}

			} else if (itemType.equals("fixed length string")) {
				if (null == itemSize) {
					// log.warn(String.format("[%s]은 크기 지정이 필 수 입니다.", itemName));
					String errorMessage = String.format("[%s]은 크기 지정이 필 수 입니다.", itemName);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}

				if (itemSizeForLang <= 0) {
					// log.warn(String.format("고정 크기 문자열 타입 단일 항목[%s]의 크기[%s]는 0보다 커야 합니다.", itemName, itemSize));
					String errorMessage = String.format("고정 크기 문자열 타입 단일 항목[%s]의 크기[%s]는 0보다 커야 합니다.", itemName, itemSize);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}
			}

			if (!itemType.equals("fixed length string")) {
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

			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemType,
						itemDefaultValue, itemSize, itemCharset);
			} catch (IllegalArgumentException e) {
				// log.warn(String.format("파일[%s] 단일 항목[%s] 클래스 인스턴스 생성 실패::%s", xmlFile.getName(), startTag, e.getMessage()));
				String errorMessage = String.format("파일[%s] 단일 항목[%s] 클래스 인스턴스 생성 실패::%s", xmlFile.getName(), startTag, e.getMessage());
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}
			
			workItemGroupInfo.addItemInfo(singleItemInfo);
		} else if (startTag.equals("array")) {
			ItemGroupInfoIF workItemGroupInfo = multiItemInfoStack.peek();

			String arrayName = attributes.getValue("name");

			if (null == arrayName) {
				// log.warn(String.format("배열 항목[%s] 필수 속성인 '배열 이름' 없음", startTag));
				String errorMessage = String.format("배열 항목[%s] 필수 속성인 '배열 이름' 없음", startTag);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}

			if (null != workItemGroupInfo.getItemInfo(arrayName)) {
				// log.warn(String.format("11.error :: 배열 항목[%s] 필수 속성인 '배열 이름' 중복", startTag));
				String errorMessage = String.format("배열 항목[%s] 필수 속성인 '배열 이름' 중복", startTag);
				System.out.println(errorMessage);
				isBadXML = true;
				return;
			}

			String arrayCntType = attributes.getValue("cnttype");
			String arrayCntValue = attributes.getValue("cntvalue");

			if (arrayCntType.equals("reference")) {
				AbstractItemInfo refItemInfo = workItemGroupInfo
						.getItemInfo(arrayCntValue);

				if (null == refItemInfo) {
					// log.warn(String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]이 존재하지 않습니다", arrayName, arrayCntValue));
					String errorMessage = String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]이 존재하지 않습니다", arrayName, arrayCntValue);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}

				CommonType.LOGICAL_ITEM_GUBUN refLogicalItemType = refItemInfo
						.getLogicalItemGubun();
				if (CommonType.LOGICAL_ITEM_GUBUN.ARRAY_ITEM == refLogicalItemType) {
					// log.warn(String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]은 숫자형 단일 항목만 올 수 있습니다. 참조 항목은 배열입니다.", arrayName, arrayCntValue));
					String errorMessage = String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]은 숫자형 단일 항목만 올 수 있습니다. 참조 항목은 배열입니다.", arrayName, arrayCntValue);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}

				SingleItemInfo refSingleItemInfo = (SingleItemInfo) refItemInfo;
				String refItemType = refSingleItemInfo.getItemType();

				if (-1 != refItemType.lastIndexOf("byte[]")
						|| -1 != refItemType.lastIndexOf("string")) {
					// log.warn(String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]은 숫자형 단일 항목만 올 수 있습니다. 참조 항목 타입[%s]", arrayName, arrayCntValue, refItemType));
					String errorMessage = String.format("배열[%s]의 크기를 지정하는 참조 항목[%s]은 숫자형 단일 항목만 올 수 있습니다. 참조 항목 타입[%s]", arrayName, arrayCntValue, refItemType);
					System.out.println(errorMessage);
					
					// System.exit(1);
					isBadXML = true;
					return;
				}
			} else {
				if (null == arrayCntValue) {
					// log.warn(String.format("배열[%s]은 크기 지정은 필 수 입니다.", arrayName));
					String errorMessage = String.format("배열[%s]은 크기 지정은 필 수 입니다.", arrayName);
					System.out.println(errorMessage);
					
					isBadXML = true;
					return;
				}

				int itemSizeForLang = -1;
				if (null != arrayCntValue) {
					try {
						itemSizeForLang = Integer.parseInt(arrayCntValue);

					} catch (NumberFormatException num_e) {
						// log.warn(String.format("배열[%s]의 크기[%s]가 숫자가 아닙니다.", arrayName, arrayCntValue));
						String errorMessage = String.format("배열[%s]의 크기[%s]가 숫자가 아닙니다.", arrayName, arrayCntValue);
						System.out.println(errorMessage);
						isBadXML = true;
						return;
					}
				}

				if (itemSizeForLang > 0) {
					// log.warn(String.format("배열[%s]의 크기[%s]는 0보다 커야 합니다.", arrayName, arrayCntValue));
					String errorMessage = String.format("배열[%s]의 크기[%s]는 0보다 커야 합니다.", arrayName, arrayCntValue);
					System.out.println(errorMessage);
					isBadXML = true;
					return;
				}
			}

			ArrayInfo arrayInfo = new ArrayInfo(arrayName, arrayCntType,
					arrayCntValue);
			multiItemInfoStack.push(arrayInfo);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// 정상적인 신놀리 스키마 XML 파일만 처리한다.
		if (isBadXML)
			return;

		// StringBuffer tagValue = new StringBuffer();
		// tagValue.append(ch, start, length);
		String tagValue = new String(ch, start, length);

		// System.out.println(String.format("tagValue=[%s]", tagValue));

		/**
		 * 메시지 식별자에 대한 설명을 하는 태그(=desc)는 무시한다. 따라서 desc 태그의 값은 저장하지 않는다.
		 */
		String startTag = startTagStack.lastElement();
		if (startTag.equals("desc"))
			return;

		tagValueStack.push(tagValue);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		/** 정상적인 신놀리 스키마 XML 파일만 처리한다. */
		if (isBadXML)
			return;

		String endTag = qName.toLowerCase();
		// System.out.println(String.format("endTag=[%s]", endTag));

		startTagStack.pop();

		/**
		 * 메시지 식별자에 대한 설명을 하는 태그(=desc) 무시. desc 태그의 값이 있을 수 있으므로 startTagStack
		 * 에 desc 태그를 시작 이벤트일때 저장후 태그 끝나는 이벤트일때 제거한다.
		 */
		if (endTag.equals("desc"))
			return;

		if (endTag.equals("messageid")) {
			if (tagValueStack.empty()) {
				// log.warn("16.error :: 메시지 식별자는 필수 항목");
				String errorMessage = "메시지 식별자는 필수 항목";
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}

			String tagValue = tagValueStack.pop();
			
			String fileName = xmlFile.getName();
			int lastIndex = fileName.lastIndexOf('.');
			String t1 = fileName.substring(0, lastIndex);
			int firstIndex = t1.lastIndexOf('.');
			
			String messageID = null;
			if (firstIndex != -1) {
				messageID = t1.substring(firstIndex+1);
			} else {
				messageID = t1;
			}
			
			if (isFileNameCheck && !messageID.equals(tagValue)) {
				String errorMessage = String.format("메시지 정보 파일 이름으로 추출된 메시지 식별자[%s]와 메시지 정보 파일에서 지정한 메시지 식별자[%s]가 다릅니다.", messageID, tagValue);
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}
			
			// messageID = tagValue;
			String regexMessageID = "[A-Z][a-zA-Z0-9]+";
			boolean isValid = messageID.matches(regexMessageID);
			if (!isValid) {
				String errorMessage = String.format("메시지 식별자[%s]는 첫번째 글자는 대문자이어야 하고 두번째 문자부터는 영문과 숫자로 구성됩니다.", messageID);
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}
			
			MessageInfo messageInfo = new MessageInfo(tagValue, xmlFile.lastModified());

			multiItemInfoStack.push(messageInfo);
			
		} else if (endTag.equals("direction")) {
			if (tagValueStack.empty()) {
				String errorMessage = "메시지 통신 방향은 필수 항목";
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}
			
			String tagValue = tagValueStack.pop();
			
			tagValue = tagValue.trim().toUpperCase();
			
			MessageInfo messageInfo = (MessageInfo)multiItemInfoStack.peek();
			
			if (tagValue.equals("FROM_NONE_TO_NONE")) {
				messageInfo.setDirection(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_NONE_TO_NONE);
			} else if (tagValue.equals("FROM_SERVER_TO_CLINET")) {
				messageInfo.setDirection(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET);
			} else if (tagValue.equals("FROM_CLIENT_TO_SERVER")) {
				messageInfo.setDirection(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER);
			} else if (tagValue.equals("FROM_ALL_TO_ALL")) {
				messageInfo.setDirection(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL);
			} else {
				String errorMessage = "알수 없는 메시지 통신 방향 값::"+tagValue;
				System.out.println(errorMessage);
				
				isBadXML = true;
				return;
			}
		} else if (endTag.equals("array")) {
			ArrayInfo arrayInfo = (ArrayInfo) multiItemInfoStack.pop();
			ItemGroupInfoIF workItemGroupInfo = multiItemInfoStack.peek();
			workItemGroupInfo.addItemInfo(arrayInfo);
		}
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		// log.warn("SAXParseException:warning", e);
		e.printStackTrace();

		isBadXML = true;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		// log.warn("SAXParseException:error", e);
		e.printStackTrace();

		isBadXML = true;
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		//log.warn("SAXParseException:fatalError", e);
		e.printStackTrace();

		isBadXML = true;
	}

	/**
	 * XML로 작성된 메시지 정보 파일을 SAX 파싱하여 메시지 정보를 작성후 반환한다. 파싱 실패시 null 를 반환한다.
	 * 
	 * @return XML로 작성된 메시지 정보 파일의 내용을 담은 메시지 정보
	 */
	public MessageInfo parse() {
		MessageInfo retMessageInfo = null;

		try {
			parser.parse(xmlFile, this);
			if (!isBadXML)
				retMessageInfo = (MessageInfo) multiItemInfoStack.pop();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retMessageInfo;
	}
}
