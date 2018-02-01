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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.ItemInfoType;
import kr.pe.sinnori.common.type.MessageTransferDirectionType;

/**
 * XML로 작성된 메시지 정보 파일을 SAX 파싱하여 메시지 정보를 작성하는 클래스.<br/>
 * XML로 작성된 메시지 정보 파일의 구조를 정의하는 XSD 파일과 연계하여 신놀이 메시지 구조 적합성을 검증한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class MessageInfoSAXParser extends DefaultHandler {
	private final Logger log = LoggerFactory.getLogger(MessageInfoSAXParser.class);
	
	
	public static final String XML_EXTENSION_SUFFIX = ".xml";
	private final int XML_EXTENSION_SUFFIX_LENGTH = XML_EXTENSION_SUFFIX.length();
	
	/** this member variables is initialized in constructor start */	
	private SAXParser saxParser;
	// 배열 크기로 참조하는 단일 항목이 가질 수 있는 값의 타입 집합
	private Set<String> possibleItemTypeNameSetForArraySizeReferenceVariable = new HashSet<String>();
	/** this member variables is initialized in constructor end */
	
	/** this member variables is initialized in parse(File) start */
	private MessageInfo messageInfo = null;
	private boolean isFileNameCheck = true;
	private File messageInformationXMLFile = null;
	private String messageIDOfXMLFile = null;	
	private String rootTag = null;
	private Stack<String> startTagStack = new Stack<String>();
	private Stack<String> tagValueStack = new Stack<String>();
	private Stack<OrderedItemSet> itemSetStack = new Stack<OrderedItemSet>();
	/** this member variables is initialized in parse(File) end */


	/**
	 * 생성자
	 * 
	 * @param isFileNameCheck 파일명의 메시지 식별자와 파일 내용의 메시지 식별자 구별 여부
	 */
	public MessageInfoSAXParser() throws SAXException {
		saxParser = getNewInstanceOfSAXParser();
			
		
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("byte");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("unsigned byte");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("short");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("unsigned short");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("integer");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("unsigned integer");
		possibleItemTypeNameSetForArraySizeReferenceVariable.add("long");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		

		String startTag = qName.toLowerCase();

		if (null == rootTag) {
			rootTag = startTag;
			if (!rootTag.equals(CommonStaticFinalVars.MESSAGE_INFO_XML_FILE_ROOT_TAG)) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine meets unknown root element then throws exception.
				 */
				String errorMessage = new StringBuilder("this xml file's root tag[")
				.append(rootTag)
				.append("] is differnet from sinnori message information xml file's root tag[")
				.append(CommonStaticFinalVars.MESSAGE_INFO_XML_FILE_ROOT_TAG)
				.append("]").toString();
				throw new SAXException(errorMessage);
			}
		}

		startTagStack.push(startTag);

		/**
		 * 메시지 식별자에 대한 설명을 하는 태그(=desc) 무시한다.
		 * Warnging! startTagStack 에 desc 태그를 일단 넣어야 한다. 
		 * 그래야 나중에  '태그 값을 받는 이벤트'(=characters method)에서 tagValueStack 에 desc 태그 값을 넣지 않을 수 있기때문이다.
		 * startTagStack 에 넣은 desc '태그는 태그 끝을 알리는 이벤트'(=endElement method)에서 제거한다.
		 */
		if (startTag.equals("desc"))
			return;

		if (startTag.equals("singleitem")) {
			OrderedItemSet workingItemSet = itemSetStack.peek();
			
			

			String itemName = attributes.getValue("name");
			if (null == itemName) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine throws exception having message is
				 * "Attribute 'name' must appear on element 'singleitem'"
				 */
				String errorMessage = new StringBuilder("single item needs attribute 'name'").toString();
				throw new SAXException(errorMessage);				
			}

			if (workingItemSet.isRegisted(itemName)) {
				String errorMessage = new StringBuilder("this single item name[")
				.append(itemName).append("] was duplicated, ")
				.append(CommonStaticFinalVars.NEWLINE).append(getParsingResultFromItemGroupInfoStack()).toString();
				throw new SAXException(errorMessage);
			}

			String itemTypeName = attributes.getValue("type");
			if (null == itemTypeName) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine throws exception having message is
				 * "Attribute 'type' must appear on element 'singleitem'"
				 */
				String errorMessage = new StringBuilder("this single item[")
				.append(itemName).append("] needs atttribute 'type'").toString();
				throw new SAXException(errorMessage);
			}

			String itemDefaultValue = attributes.getValue("defaultValue");
			String itemSize = attributes.getValue("size");
			String itemCharset = attributes.getValue("charset");			

			SingleItemInfo singleItemInfo = null;
			
			try {
				singleItemInfo = new SingleItemInfo(itemName, itemTypeName,
						itemDefaultValue, itemSize, itemCharset);
			} catch (IllegalArgumentException e) {
				String errorMessage = "fail to create instance of SingleItemInfo class";
				log.warn(errorMessage, e);
				throw new SAXException(new StringBuilder(e.getMessage())
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getParsingResultFromItemGroupInfoStack()).toString());
			}
			
			workingItemSet.addItemInfo(singleItemInfo);
		} else if (startTag.equals("array")) {
			OrderedItemSet workingItemSet = itemSetStack.peek();
			
			

			String arrayName = attributes.getValue("name");

			if (null == arrayName) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine throws exception having message is
				 * "Attribute 'name' must appear on element 'array'"
				 */
				String errorMessage = new StringBuilder("this array item needs attribute 'name'").toString();
				throw new SAXException(errorMessage);
			}

			if (workingItemSet.isRegisted(arrayName)) {
				String errorMessage = new StringBuilder("this array item name[")
				.append(arrayName).append("] was duplicated, ")
				.append(CommonStaticFinalVars.NEWLINE).append(getParsingResultFromItemGroupInfoStack()).toString();
				throw new SAXException(errorMessage);
			}

			String arrayCntType = attributes.getValue("cnttype");
			
			if (null == arrayCntType) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine throws exception having message is
				 * "Attribute 'cnttype' must appear on element 'array'"
				 */
				
				String errorMessage = new StringBuilder("this array item[")
				.append(arrayName).append("] needs attribute 'cnttype'").toString();
				throw new SAXException(errorMessage);
			}
			
			
			String arrayCntValue = attributes.getValue("cntvalue");
			
			if (null == arrayCntValue) {
				/**
				 * this code is dead code and defensive code 
				 * because if xsl engine throws exception having message is
				 * "Attribute 'cntvalue' must appear on element 'array'"
				 */
				
				String errorMessage = new StringBuilder("this array item[")
				.append(arrayName).append("] needs attribute 'arrayCntValue'").toString();
				throw new SAXException(errorMessage);
			}
			
			if (arrayCntType.equals("reference")) {
				AbstractItemInfo itemInfoForArraySizeReferenceVariable = workingItemSet
						.getItemInfo(arrayCntValue);

				if (null == itemInfoForArraySizeReferenceVariable) {					
					String errorMessage = new StringBuilder("any single item that specifies this array item[")
					.append(arrayName).append("]'s size doesn't exist").toString();
					throw new SAXException(errorMessage);
				}

				ItemInfoType messageItemTypeOfArraySizeReferenceVariable = itemInfoForArraySizeReferenceVariable
						.getItemInfoType();
				if (messageItemTypeOfArraySizeReferenceVariable.equals(ItemInfoType.ARRAY)) {					
					String errorMessage = new StringBuilder("the logical gubun of item that specifies this array item[")
					.append(arrayName).append("]'s size must be only single").toString();
					throw new SAXException(errorMessage);
				}

				SingleItemInfo singleItemInfoForArraySizeReferenceVariable = (SingleItemInfo) itemInfoForArraySizeReferenceVariable;
				String itemTypeNameOfArraySizeReferenceVariable = singleItemInfoForArraySizeReferenceVariable.getItemTypeName();				
				
				if (!possibleItemTypeNameSetForArraySizeReferenceVariable.contains(itemTypeNameOfArraySizeReferenceVariable)) {
					String errorMessage = new StringBuilder("the value of single item that specifies this array item[")
					.append(arrayName).append("]'s size must be number, possible item type set={")
					.append(possibleItemTypeNameSetForArraySizeReferenceVariable.toString())
					.append("}").toString();
					throw new SAXException(errorMessage);
				}
			}

			ArrayInfo arrayInfo = null;
			try {
				arrayInfo = new ArrayInfo(arrayName, arrayCntType,
					arrayCntValue);
			} catch (IllegalArgumentException e) {
				String errorMessage = "fail to create instance of ArrayInfo class";
				log.warn(errorMessage, e);
				throw new SAXException(new StringBuilder(e.getMessage())
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getParsingResultFromItemGroupInfoStack()).toString());
			}
			
			workingItemSet.addItemInfo(arrayInfo);
			itemSetStack.push(arrayInfo.getOrderedItemSet());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String tagValue = new String(ch, start, length);


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
		String endTag = qName.toLowerCase();

		startTagStack.pop();

		/**
		 * 메시지 식별자에 대한 설명을 하는 태그(=desc) 무시. desc 태그의 값이 있을 수 있으므로 startTagStack
		 * 에 desc 태그를 시작 이벤트일때 저장후 태그 끝나는 이벤트일때 제거한다.
		 */
		if (endTag.equals("desc"))
			return;

		if (endTag.equals("messageid")) {
			if (tagValueStack.empty()) {
				/*String errorMessage = "메시지 식별자는 필수 항목";
				log.warn(errorMessage);
				
				isBadXML = true;
				return;*/
				String errorMessage = new StringBuilder("tag \"messageid\"'s value is a empty string").toString();
				throw new SAXException(errorMessage);
			}

			String messageID = tagValueStack.pop();			
			
			if (isFileNameCheck) {			
				if (!messageIDOfXMLFile.equals(messageID)) {
					String errorMessage = new StringBuilder("The tag \"messageid\"'s value[")
					.append(messageID).append("] is different from message id[")
					.append(messageIDOfXMLFile).append("] of '<message id>.xml' format file[")
					.append(messageInformationXMLFile.getAbsolutePath()).append("]").toString();
					throw new SAXException(errorMessage);
				}
			}
			
			
			messageInfo = new MessageInfo(messageID, messageInformationXMLFile);

			itemSetStack.push(messageInfo.getOrderedItemSet());
			
		} else if (endTag.equals("direction")) {
			if (tagValueStack.empty()) {
				String errorMessage = new StringBuilder("tag \"direction\"'s value is a empty string").toString();
				throw new SAXException(errorMessage);
			}
			
			String tagValue = tagValueStack.pop();
			
			tagValue = tagValue.trim().toUpperCase();
			
			if (null == messageInfo) {
				log.error("the var messageInfo is null");
				System.exit(1);
			}
			
			if (tagValue.equals("FROM_NONE_TO_NONE")) {
				messageInfo.setDirection(MessageTransferDirectionType.FROM_NONE_TO_NONE);
			} else if (tagValue.equals("FROM_SERVER_TO_CLINET")) {
				messageInfo.setDirection(MessageTransferDirectionType.FROM_SERVER_TO_CLINET);
			} else if (tagValue.equals("FROM_CLIENT_TO_SERVER")) {
				messageInfo.setDirection(MessageTransferDirectionType.FROM_CLIENT_TO_SERVER);
			} else if (tagValue.equals("FROM_ALL_TO_ALL")) {
				messageInfo.setDirection(MessageTransferDirectionType.FROM_ALL_TO_ALL);
			} else {
				String errorMessage = new StringBuilder("tag \"direction\"'s value[").append(tagValue)
						.append("] is not a member of direction set[FROM_NONE_TO_NONE, FROM_SERVER_TO_CLINET, FROM_CLIENT_TO_SERVER, FROM_ALL_TO_ALL]").toString();
				throw new SAXException(errorMessage);
			}
		} else if (endTag.equals("array")) {
			itemSetStack.pop();
			// ArrayInfo arrayInfo = (ArrayInfo) itemInfoStack.pop();
			/*ItemGroupIF workItemGroupInfo = itemInfoStack.peek();
			workItemGroupInfo.addItemInfo(arrayInfo);*/
		}
	}
	
	private String getParsingResultFromItemGroupInfoStack() {
		if (null == messageInfo) {
			return "the var messageInfo is null";
		}
		
		StringBuilder messageStringBuilder = new StringBuilder("parsing result=");			
		messageStringBuilder.append(messageInfo.toString());
		return messageStringBuilder.toString();
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("warning::");
		messageStringBuilder.append(getParsingResultFromItemGroupInfoStack());
		messageStringBuilder.append(", ");
		messageStringBuilder.append(e.toString());	
		throw new SAXException(messageStringBuilder.toString());
	}	
	

	@Override
	public void error(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("error::");
		messageStringBuilder.append(getParsingResultFromItemGroupInfoStack());
		messageStringBuilder.append(", ");
		messageStringBuilder.append(e.toString());		
		throw new SAXException(messageStringBuilder.toString());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		StringBuilder messageStringBuilder = new StringBuilder("fatalError::");
		messageStringBuilder.append(getParsingResultFromItemGroupInfoStack());
		messageStringBuilder.append(", ");
		messageStringBuilder.append(e.toString());		
		throw new SAXException(messageStringBuilder.toString());
	}

	
	/**
	 * XML로 작성된 메시지 정보 파일을 SAX 파싱하여 얻은 결과물인 메시지 정보를 반환한다. 쓰레드 세이프를 위해 동기화한다.
	 * @param xmlFile
	 * @param isFileNameCheck
	 * @return XML로 작성된 메시지 정보 파일을 SAX 파싱하여 얻은 결과물인 메시지 정보
	 * @throws IllegalArgumentException 파라미터 xmlFile 가 잘못되었을 경우 예를 들면 null, 
	 * 확장자가 .xml이 아닌경우 그리고 파일명이 [메시지 식별자].xml 포맷을 가지지 않거나 
	 * 파임명에서 추출한  메시지 식별자가가 메시지 식별자 형식에 부합하지 않는 경우 이 예외를 던진다.
	 * @throws SAXException
	 * @throws IOException
	 */
	public MessageInfo parse(File xmlFile, boolean isFileNameCheck) 
			throws IllegalArgumentException, SAXException, IOException {
		if (null == xmlFile) {
			throw new IllegalArgumentException("the parameter xmlFile is null");
		}
		
		if (! xmlFile.exists()) {
			String errorMessage = new StringBuilder("the parameter xmlFile[")
			.append(xmlFile.getAbsolutePath())
			.append("] does not exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! xmlFile.canRead()) {
			String errorMessage = new StringBuilder("the file(=the parameter xmlFile[")
			.append(xmlFile.getAbsolutePath())
			.append("]) doesn't hava permission to read").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String xmlFilePathString = xmlFile.getAbsolutePath();
				
		synchronized (possibleItemTypeNameSetForArraySizeReferenceVariable) {
			this.messageInformationXMLFile = xmlFile;
			this.isFileNameCheck = isFileNameCheck;
			if (isFileNameCheck) {
				this.messageIDOfXMLFile = getMessageIDFromXMLFilePathString(xmlFilePathString);
			}
			
			try {
				saxParser.parse(xmlFile, this);
				return messageInfo;
			} finally {
				reset();	
			}
		}		
	}
	
	private SAXParser getNewInstanceOfSAXParser() throws SAXException {
		SAXParser saxParser = null;
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			saxParserFactory.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");	
			
			saxParserFactory.setSchema(schemaFactory
					.newSchema(new Source[] { new StreamSource(SingleItemTypeManger.getInstance().getMesgXSLInputSream()) }));
			
			saxParser = saxParserFactory.newSAXParser();
		} catch (Exception | Error e) {
			log.warn(e.getMessage(), e);
			throw new SAXException(e.getMessage());
		}
		
		return saxParser;
	}
	
	private void reset() throws SAXException {
		this.rootTag = null;
		this.startTagStack.clear();
		this.tagValueStack.clear();
		this.itemSetStack.clear();
		try {
			saxParser.reset();
		} catch (UnsupportedOperationException e) {
			/**
			 * 두번 세번 이상 사용할려면 과거 파싱한 잔재를 지워 초기 상태로 만들어야한다.
			 * 이 전제 조건이 무너지면 구조를 바꾸어야 하기때문에 시스템 종료를 사용하였다. 
			 */
			log.warn("sax 파서가 reset() 메소드를 미 지원하므로 과거 파싱 잔재를 지워 초기 상태로 만들기 위해서 기존 sax 파서를 버리고 새로운 sax 파서 인스턴스로 대체한다.", e);
			
			saxParser  = getNewInstanceOfSAXParser();
		}	
	}
	
	public String getMessageIDFromXMLFilePathString(String messageInformationXMLFilePathString) 
			throws IllegalArgumentException {
		if (null == messageInformationXMLFilePathString) {
			throw new IllegalArgumentException("the parameter messageInformationXMLFilePathString is null");
		}
		
		if (messageInformationXMLFilePathString.equals("")) {
			throw new IllegalArgumentException("the parameter messageInformationXMLFilePathString is a empty string");
		}
		
		if (messageInformationXMLFilePathString.length() <= XML_EXTENSION_SUFFIX.length()) {
			String errorMessage = new StringBuilder("the parameter messageInformationXMLFilePathString[")
			.append(messageInformationXMLFilePathString)
			.append("]'s length[")
			.append(messageInformationXMLFilePathString.length()).append("] is too small, its length must be greater than ")
			.append(XML_EXTENSION_SUFFIX.length()).toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! messageInformationXMLFilePathString.endsWith(XML_EXTENSION_SUFFIX)) {
			String errorMessage = new StringBuilder("the parameter messageInformationXMLFilePathString[")
			.append(messageInformationXMLFilePathString)
			.append("]'s suffix is not '.xml'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		String messageIDOfXMLFile = null;
		int lengthOfXMLFilePathString = messageInformationXMLFilePathString.length();		
		int startIndexOfExpectedXMLExtension = lengthOfXMLFilePathString - XML_EXTENSION_SUFFIX_LENGTH;
		// String exptectedXMLExtension = xmlFilePathString.substring(startIndexOfExpectedXMLExtension);
		int lastIndexOfFileSeparator = messageInformationXMLFilePathString.lastIndexOf(File.separator);
		
		
		/*if (!exptectedXMLExtension.equals(XML_EXTENSION_SUFFIX)) {
			String errorMessage = new StringBuilder("the parameter xmlFilePathString[")
			.append(xmlFilePathString)
			.append("])'s suffix is not '.xml'").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
				
		if (lastIndexOfFileSeparator < 0) {
			messageIDOfXMLFile = messageInformationXMLFilePathString.substring(0, startIndexOfExpectedXMLExtension);
		} else {
			if (lastIndexOfFileSeparator >= (startIndexOfExpectedXMLExtension-1)) {
				String errorMessage = new StringBuilder("fail to get message id from the parameter messageInformationXMLFilePathString[")
				.append(messageInformationXMLFilePathString)
				.append("] that is '<messageID>.xml' format file name").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			messageIDOfXMLFile = messageInformationXMLFilePathString.substring(lastIndexOfFileSeparator+1, startIndexOfExpectedXMLExtension);
		}		
		
		if (! AbstractMessage.IsValidMessageID(messageIDOfXMLFile)) {
			String errorMessage = new StringBuilder("the parameter messageInformationXMLFilePathString[")
			.append(messageInformationXMLFilePathString)
			.append("] has a invalid message id[")
			.append(messageIDOfXMLFile)
			.append("], (note) the message information XML file name format is '<messageID>.xml'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return messageIDOfXMLFile;
	}
	
	
}
