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

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * MemberAllInformationRes message decoder
 * @author Won Jonghoon
 *
 */
public final class MemberAllInformationResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		MemberAllInformationRes memberAllInformationRes = new MemberAllInformationRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberAllInformationRes");

		memberAllInformationRes.setNickname((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setState((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "state" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setRole((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "role" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setPasswordHint((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "passwordHint" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setPasswordAnswer((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "passwordAnswer" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setPasswordFailedCount((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "passwordFailedCount" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "registeredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		memberAllInformationRes.setLastModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "lastModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return memberAllInformationRes;
	}
}