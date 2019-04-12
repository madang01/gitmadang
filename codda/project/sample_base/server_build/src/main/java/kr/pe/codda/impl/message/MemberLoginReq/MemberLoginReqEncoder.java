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

package kr.pe.codda.impl.message.MemberLoginReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * MemberLoginReq message encoder
 * @author Won Jonghoon
 *
 */
public final class MemberLoginReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		MemberLoginReq memberLoginReq = (MemberLoginReq)messageObj;
		encodeBody(memberLoginReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(MemberLoginReq memberLoginReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberLoginReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "idCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberLoginReq.getIdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pwdCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberLoginReq.getPwdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sessionKeyBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberLoginReq.getSessionKeyBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ivBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberLoginReq.getIvBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberLoginReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}