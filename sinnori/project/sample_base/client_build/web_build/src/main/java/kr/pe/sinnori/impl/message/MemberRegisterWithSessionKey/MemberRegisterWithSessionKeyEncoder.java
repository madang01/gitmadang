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
package kr.pe.sinnori.impl.message.MemberRegisterWithSessionKey;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * MemberRegisterWithSessionKey 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class MemberRegisterWithSessionKeyEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof MemberRegisterWithSessionKey)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 MemberRegisterWithSessionKey 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		MemberRegisterWithSessionKey memberRegisterWithSessionKey = (MemberRegisterWithSessionKey) messageObj;
		encodeBody(memberRegisterWithSessionKey, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * MemberRegisterWithSessionKey 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param memberRegisterWithSessionKey MemberRegisterWithSessionKey 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(MemberRegisterWithSessionKey memberRegisterWithSessionKey, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String memberRegisterWithSessionKeySingleItemPath = "MemberRegisterWithSessionKey";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(memberRegisterWithSessionKeySingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "idCipherBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getIdCipherBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "pwdCipherBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getPwdCipherBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "nicknameCipherBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getNicknameCipherBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "hintCipherBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getHintCipherBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "answerCipherBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getAnswerCipherBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "sessionKeyBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getSessionKeyBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(memberRegisterWithSessionKeySingleItemPath, "ivBase64"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, memberRegisterWithSessionKey.getIvBase64() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}