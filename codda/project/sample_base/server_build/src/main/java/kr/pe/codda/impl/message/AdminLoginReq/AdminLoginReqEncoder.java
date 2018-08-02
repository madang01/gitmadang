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

package kr.pe.codda.impl.message.AdminLoginReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * AdminLoginReq message encoder
 * @author Won Jonghooon
 *
 */
public final class AdminLoginReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		AdminLoginReq adminLoginReq = (AdminLoginReq)messageObj;
		encodeBody(adminLoginReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(AdminLoginReq adminLoginReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("AdminLoginReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "idCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, adminLoginReq.getIdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pwdCipherBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, adminLoginReq.getPwdCipherBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sessionKeyBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, adminLoginReq.getSessionKeyBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ivBase64"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, adminLoginReq.getIvBase64() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}