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

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * MemberRegisterReq message decoder
 * @author Won Jonghooon
 *
 */
public final class MemberRegisterReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberRegisterReq");

		memberRegisterReq.setIdCipherBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "idCipherBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setPwdCipherBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pwdCipherBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setNicknameCipherBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nicknameCipherBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setHintCipherBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "hintCipherBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setAnswerCipherBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "answerCipherBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setSessionKeyBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "sessionKeyBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberRegisterReq.setIvBase64((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ivBase64" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return memberRegisterReq;
	}
}