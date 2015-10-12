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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 * 신놀이 메시지를 구성하는 항목 타입을 관리 클래스. 
 * 항목 타입명을 이곳에서 관리를 하므로 메시지의 구조를 정의하는 XSL 는 자체적으로 포함하고 있다.
 * 유틸 디렉토리의 MesgXSLUtil.sh or  MesgXSLUtil.bat 를 이용하면
 * xsl 파일을 자바 소스로 내장하기 위한 StringBuilder 를 이용한 문자열로 변환을 해 주는 기능과
 * 결과물로 얻는 StringBuilder 를 이용한 문자열를 다시 역으로 임시 파일로 내리는 기능도 있다.
 * 이를 잘 활용하여 새로운 데이터 타입이나 수정시 유용할것이다.
 * 단, 자바 문자열로 변환은 org.apache.commons.lang.StringEscapeUtils 를 이용하는데,
 * 한글의 경우 유니코드 문자열로 변환을 하기때문에 읽기 곤란한 점이 있다. 
 * 하지만 한글은 주석에서만 있으므로 나중 영문으로 변경하면 회피할 수 있는 문제이므로 그대로 넘어가기로 함.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class ItemValueTypeManger {
	private final Logger log = LoggerFactory.getLogger(ItemValueTypeManger.class);
	
	private String messageXSLStr = null;
	
	private LinkedHashMap<String, Integer> itemValueTypeToIDHash  = new LinkedHashMap<String, Integer>();
	private HashMap<Integer, String> itemIDToItemValueTypeHash  = new HashMap<Integer, String>();
		
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class ItemValueTypeMangerHolder {
		static final ItemValueTypeManger singleton = new ItemValueTypeManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ItemValueTypeManger getInstance() {
		return ItemValueTypeMangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private ItemValueTypeManger() {
		
		int id=0;
		String itemValueType = "byte";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned byte";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "short";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned short";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "integer";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned integer";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "long";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "ub pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "us pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "si pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "fixed length string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "ub variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "us variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "si variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "fixed length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "java sql date";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "java sql timestamp";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "boolean";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		/** 신규 타입 추가시 구현 언어인 자바 타입등을 정의한 SingleItemInfo 에도 추가를 해 주어야 한다. */
		
		StringBuilder mesgXSLStringBuilder = new StringBuilder();
		mesgXSLStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		mesgXSLStringBuilder.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<!-- \uD56D\uBAA9 \uADF8\uB8F9 -->\n");
		mesgXSLStringBuilder.append("\t<xs:group name=\"itemgroup\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:choice>\n");
		mesgXSLStringBuilder.append("\t\t\t<!-- \uB2E8\uC77C \uD56D\uBAA9 -->\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"singleitem\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uC774\uB984 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uD56D\uBAA9 \uD0C0\uC785, \uD56D\uBAA9 \uD0C0\uC785\uC740 \uD06C\uAC8C 2\uAC00\uC9C0\uB85C \uB098\uB258\uB294\uB370 \uC22B\uC790\uD615\uACFC \uBB38\uC790\uD615\uC774 \uC788\uB2E4. \uC22B\uC790\uD615 \uAC19\uC740 \uACBD\uC6B0 \uC815\uC218\uB9CC \uC9C0\uC6D0\uD558\uBA70 \uBD80\uD638 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC788\uC74C\uACFC \uBD80\uD638 \uC5C6\uC74C\uC73C\uB85C \uB098\uB258\uBA70 \uBE44 \uBD80\uD638(= \uBD80\uD638 \uC5C6\uC74C)\uB9CC \uC55E\uC5D0 \uD45C\uC2DC\uD55C\uB2E4. \uB2E8 \uD2B9\uC815 \uC5B8\uC5B4\uC758 \uACBD\uC6B0 \uC608\uB97C \uB4E4\uBA74 \uC790\uBC14\uC758 \uACBD\uC6B0 \uBD80\uD638 \uC5C6\uC74C\uC744 \uC9C0\uC6D0\uD558\uC9C0 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC54A\uC73C\uBBC0\uB85C \uC774\uB97C \uC18C\uD504\uD2B8\uC6E8\uC5B4\uB85C \uAD6C\uD604\uD55C\uB2E4. \uC18C\uD504\uD2B8\uC6E8\uC5B4 \uAD6C\uD604\uC5D0\uB294 \uD55C\uACC4\uAC00 \uC788\uB2E4 \uC608\uB97C \uB4E4\uBA74 unsigned long \uAC19\uC740 \uACBD\uC6B0 \uC790\uBC14\uB85C \uAD6C\uD604\uD560\uB824\uACE0 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uD558\uBA74 \uBD88\uAC00\uB2A5\uC5D0 \uAC00\uAE4C\uC6B4 \uB9E4\uC6B0 \uD798\uB4E0 \uC77C\uC774\uB2E4. \uB530\uB77C\uC11C \uBC18\uB4EF\uC774 \uC2E0\uB180\uC774\uB97C \uAD6C\uD604\uD558\uB294 \uC5B8\uC5B4 \uD2B9\uC131\uC73C\uB85C \uAE30\uC778\uD558\uB294 \uD0C0\uC785 \uC81C\uD55C\uC744 \uC219\uC9C0\uD574\uC57C \uD55C\uB2E4. \uD0C0\uC785 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC81C\uD55C\uC744 \uADF9\uBCF5 \uD558\uB294 \uBC29\uBC95\uC73C\uB85C \uBB38\uC790\uC5F4 \uADF8 \uC790\uCCB4\uB85C \uBCF4\uB0B4\uACE0 \uD074\uB77C\uC774\uC5B8\uD2B8 \uD639\uC740 \uBE44\uC9C0\uB2C8\uC2A4 \uCE21\uC5D0\uC11C \uC774\uB97C \uC801\uC808\uD558\uAC8C \uBCC0\uD658\uD558\uC5EC \uC0AC\uC6A9\uD558\uB294\uAC83\uC744 \uCD94\uCC9C\uD55C\uB2E4. \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC2E4\uC218\uD615 \uB370\uC774\uD130\uC758 \uACBD\uC6B0 \uC774\uB807\uAC8C \uD574\uACB0\uD558\uAE30\uB97C \uBC14\uB780\uB2E4. \uC608\uC81C) unsigned byte, \uBC30\uC5F4\uC740 byte \uB9CC \uC9C0\uC6D0\uD55C\uB2E4. \uC608\uC81C) byte[] \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC22B\uC790\uD615 \uD0C0\uC785 \uBAA9\uB85D : byte, short, integer, long \uBB38\uC790\uD615 \uD0C0\uC785 \uBAA9\uB85D : string -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"type\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		
		Iterator<String> itemValueTypeIter = itemValueTypeToIDHash.keySet().iterator();

		while (itemValueTypeIter.hasNext()) {
			mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"");
			mesgXSLStringBuilder.append(itemValueTypeIter.next());
			mesgXSLStringBuilder.append("\" />\n");
		}
		
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uD0C0\uC785 \uBD80\uAC00 \uC815\uBCF4\uC778 \uD06C\uAE30\uB294 2\uAC00\uC9C0 \uD0C0\uC785\uC5D0\uC11C\uB9CC \uC720\uC6A9\uD558\uB2E4. (1) \uACE0\uC815 \uD06C\uAE30 \uBC14\uC774\uD2B8 \uBC30\uC5F4(fixed length byte[]) \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t(2) \uACE0\uC815 \uD06C\uAE30 \uBB38\uC790\uC5F4(fixed length string) -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"size\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uD0C0\uC785 \uBD80\uAC00 \uC815\uBCF4\uC778\uC778 \uBB38\uC790\uC14B\uC740 \uC624\uC9C1 \uACE0\uC815 \uD06C\uAE30 \uBB38\uC790\uC5F4(fixed length string)\uC5D0\uC11C\uB9CC \uC720\uD6A8\uD558\uB2E4. -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"charset\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uAC12 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"defaultValue\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t<!-- \uBC30\uC5F4 -->\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"array\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uD56D\uBAA9 \uADF8\uB8F9 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uC774\uB984 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uC9C0\uC815 \uBC29\uC2DD(cnttype)\uC740 2\uAC00\uC9C0\uAC00 \uC788\uB2E4. (1) \uC9C1\uC811(direct) : \uACE0\uC815 \uD06C\uAE30 \uC9C0\uC815\uBC29\uC2DD\uC73C\uB85C \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uC5D0\uB294 \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uAC12\uC774 \uC800\uC7A5\uB418\uBA70, (2) \uCC38\uC870(reference) : \uAC00\uBCC0 \uD06C\uAE30 \uC9C0\uC815\uBC29\uC2DD\uC73C\uB85C \uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uB294 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uCC38\uC870\uD558\uB294 \uD56D\uBAA9\uC758 \uAC12\uC774\uB2E4. -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cnttype\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"reference\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"direct\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<!-- \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218(cntvalue) \"\uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uC9C0\uC815 \uBC29\uC2DD\"\uC774 \uC9C1\uC811(direct) \uC774\uBA74 \uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uB97C \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uBC18\uD658\uD558\uBA70, \uCC38\uC870(reference)\uC77C \uACBD\uC6B0\uC5D0\uB294 \uCC38\uC870\uD558\uB294 \uD56D\uBAA9 \uC774\uB984\uC744 \uBC18\uD658\uD55C\uB2E4. \uCC38\uC870\uD558\uB294 \uD56D\uBAA9\uC740 \uC22B\uC790\uD615\uC73C\uB85C \uBC30\uC5F4\uACFC \uAC19\uC740 \uB2E8\uACC4\uB85C \uBC18\uB4EF\uC774 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC55E\uC5D0 \uB098\uC640\uC57C \uD55C\uB2E4. \uC774\uB807\uAC8C \uC55E\uC5D0 \uB098\uC640\uC57C \uD558\uB294 \uC774\uC720\uB294 \uBC30\uC5F4 \uC815\uBCF4\uB97C \uC77D\uC5B4\uC640\uC11C \uBC30\uC5F4 \uC815\uBCF4\uB97C \uC800\uC7A5\uD558\uAE30 \uC804\uC5D0 \uCC38\uC870 \uBCC0\uC218\uAC00 \uAC19\uC740 \uB808\uBCA8\uC5D0\uC11C \uC874\uC7AC\uD558\uBA70 \n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\uC22B\uC790\uD615\uC778\uC9C0 \uD310\uB2E8\uC744 \uD558\uAE30 \uC704\uD574\uC11C\uC774\uB2E4. \uBA54\uC2DC\uC9C0 \uC815\uBCF4 \uD30C\uC77C\uC744 \uC21C\uCC28\uC801\uC73C\uB85C \uC77D\uAE30 \uB54C\uBB38\uC5D0 \uBC30\uC5F4 \uB4A4\uC5D0 \uC704\uCE58\uD558\uBA74 \uC54C \uC218\uAC00 \uC5C6\uB2E4. -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cntvalue\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\t\t</xs:choice>\n");
		mesgXSLStringBuilder.append("\t</xs:group>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<!-- 메시지 -->\n");
		mesgXSLStringBuilder.append("\t<xs:element name=\"sinnori_message\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:sequence>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<!-- 메시지 식별자 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"messageID\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<!-- 메시지 통신 방향 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"direction\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9_]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<!-- 항목 그룹 -->\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\"\n");
		mesgXSLStringBuilder.append("\t\t\t\t\tmaxOccurs=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t</xs:element>\n");
		mesgXSLStringBuilder.append("</xs:schema>\n");
		
		messageXSLStr = mesgXSLStringBuilder.toString();
		
		FileWriter fw = null;
		BufferedWriter bw  = null;
		try {
			File f = File.createTempFile("SinnoriMsgXSL", ".tmp");
			
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write(messageXSLStr);
			
			
			log.info("the sinnori message information .xsl temporary file[{}] was created successfully", f.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getMessageXSLStr() {
		return messageXSLStr;
	}
	
	public ByteArrayInputStream getMesgXSLInputSream() {
		ByteArrayInputStream xslByteArrayInputStream = new ByteArrayInputStream(messageXSLStr.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
		return xslByteArrayInputStream;
	}
	
	public int getItemValueTypeID(String itemValueType) throws UnknownItemTypeException {
		Integer itemValueTypeID = itemValueTypeToIDHash.get(itemValueType);
		if (null == itemValueTypeID) {
			String errorMessage = String.format("unknown message item type[%s]", itemValueType);
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);
			// log.warn(errorMessage, e);
			e.printStackTrace();
			throw e;
		}		
		return itemValueTypeID.intValue();
	}
	
	public String getItemValueType(int itemValueTypeID) throws UnknownItemTypeException {
		String itemValueType = itemIDToItemValueTypeHash.get(itemValueTypeID);
		if (null == itemValueType) {
			String errorMessage = String.format("unknown message item type id[%d]", itemValueTypeID);
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);
			// log.warn(errorMessage, e);
			e.printStackTrace();
			throw e;
		}		
		return itemValueType;
	}
	
	public int getItemValueTypeCnt() {
		return itemValueTypeToIDHash.size();
	}
}
