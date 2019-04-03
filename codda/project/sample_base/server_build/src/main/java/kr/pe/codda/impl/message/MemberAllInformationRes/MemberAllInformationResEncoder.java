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

package kr.pe.codda.impl.message.MemberAllInformationRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * MemberAllInformationRes message encoder
 * @author Won Jonghoon
 *
 */
public final class MemberAllInformationResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		MemberAllInformationRes memberAllInformationRes = (MemberAllInformationRes)messageObj;
		encodeBody(memberAllInformationRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(MemberAllInformationRes memberAllInformationRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberAllInformationRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nickname"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberAllInformationRes.getNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "state"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, memberAllInformationRes.getState() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "role"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, memberAllInformationRes.getRole() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "passwordHint"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberAllInformationRes.getPasswordHint() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "passwordAnswer"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, memberAllInformationRes.getPasswordAnswer() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "passwordFailedCount"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, memberAllInformationRes.getPasswordFailedCount() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberAllInformationRes.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registeredDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, memberAllInformationRes.getRegisteredDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifiedDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, memberAllInformationRes.getLastModifiedDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}