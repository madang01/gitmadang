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
package kr.pe.sinnori.impl.message.SelfExn;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * SelfExn 메시지 THB 프로토콜 디코더
 * @author Won Jonghoon
 *
 */
public final class SelfExnDecoder extends MessageDecoder {
	/**
	 * <pre>
	 * "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SelfExn 메시지를 반환한다.
	 * 
	 * 파라미터 "단일항목 디코더" 는 "중간 다리 역활 읽기 객체"로 부터 프로토콜에 맞도록  항목 타입별로 디코더이다.
	 *   
	 * 파라미터 "중간 다리 역활 읽기 객체" 는 입력 스트림과 메시지 간에 중간자 역활을 하며 프로토콜 별로 달라지게 된다.
	 * (1) 예를 들면 DHB 프로토콜의 경우 "중간 다리 역활 읽기 객체" 없이 직접적으로 입력 스트림으로 부터 메시지를 추출한다.
	 * 이런 경우   "중간 다리 역활 읽기 객체" 는 그 자체로 입력 스트림이 된다.
	 * 디코딩 흐름도) 입력 스트림 -> 중간 다리 역활 읽기 객체 -> 메시지
	 * (2) 그리고 DJSON 프로토콜의 경우 "중간 다리 역활 읽기 객체" 는 입력 스트림으로부터 추출된 존슨 객체이다.
	 * 디코딩 흐름도) 입력 스트림 -> 존슨 객체 -> 메시지
	 * </pre> 
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj  중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SelfExn 메시지
	 * @throws Exception "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출할때 에러 발생시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		SelfExn SelfExn = new SelfExn();
		SelfExn.setErrorWhere((String)
				singleItemDecoder.getValueFromMiddleReadObj(SelfExn.getMessageID()
				, "errorWhere" // itemName
				, 7 // itemType
				, "ub pascal string" // itemTypeName
				, -1 // itemSizeForLang
				, null // itemCharsetForLang
				, charsetOfProject
				, middleReadObj));
		SelfExn.setErrorGubun((String)
				singleItemDecoder.getValueFromMiddleReadObj(SelfExn.getMessageID()
				, "errorGubun" // itemName
				, 7 // itemType
				, "ub pascal string" // itemTypeName
				, -1 // itemSizeForLang
				, null // itemCharsetForLang
				, charsetOfProject
				, middleReadObj));
		SelfExn.setErrorMessageID((String)
				singleItemDecoder.getValueFromMiddleReadObj(SelfExn.getMessageID()
				, "errorMessageID" // itemName
				, 7 // itemType
				, "ub pascal string" // itemTypeName
				, -1 // itemSizeForLang
				, null // itemCharsetForLang
				, charsetOfProject
				, middleReadObj));
		SelfExn.setErrorMessage((String)
				singleItemDecoder.getValueFromMiddleReadObj(SelfExn.getMessageID()
				, "errorMessage" // itemName
				, 8 // itemType
				, "us pascal string" // itemTypeName
				, -1 // itemSizeForLang
				, null // itemCharsetForLang
				, charsetOfProject
				, middleReadObj));
		
		return SelfExn;
	 }
}
