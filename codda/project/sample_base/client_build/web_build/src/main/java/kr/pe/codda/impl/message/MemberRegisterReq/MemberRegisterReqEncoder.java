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

package kr.pe.codda.impl.message.MemberRegisterReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * MemberRegisterReq message encoder
 * @author Won Jonghoon
 *
 */
public final class MemberRegisterReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		MemberRegisterReq memberRegisterReq = (MemberRegisterReq)messageObj;
		encodeBody(memberRegisterReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(MemberRegisterReq memberRegisterReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberRegisterReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "idCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getIdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pwdCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getPwdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nicknameCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getNicknameCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "hintCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getHintCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "answerCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getAnswerCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sessionKeyBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getSessionKeyBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ivBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberRegisterReq.getIvBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberRegisterReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}