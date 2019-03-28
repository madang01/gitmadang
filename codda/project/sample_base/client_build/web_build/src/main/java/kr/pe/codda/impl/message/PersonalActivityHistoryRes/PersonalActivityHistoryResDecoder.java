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

package kr.pe.codda.impl.message.PersonalActivityHistoryRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * PersonalActivityHistoryRes message decoder
 * @author Won Jonghoon
 *
 */
public final class PersonalActivityHistoryResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		PersonalActivityHistoryRes personalActivityHistoryRes = new PersonalActivityHistoryRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("PersonalActivityHistoryRes");

		personalActivityHistoryRes.setTargetUserID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "targetUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		personalActivityHistoryRes.setTargetUserNickname((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "targetUserNickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		personalActivityHistoryRes.setTotal((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "total" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		personalActivityHistoryRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int personalActivity$2ListSize = personalActivityHistoryRes.getCnt();
		if (personalActivity$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var personalActivity$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object personalActivity$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "personalActivity", personalActivity$2ListSize, middleReadableObject);
		java.util.List<PersonalActivityHistoryRes.PersonalActivity> personalActivity$2List = new java.util.ArrayList<PersonalActivityHistoryRes.PersonalActivity>();
		for (int i2=0; i2 < personalActivity$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("PersonalActivity").append("[").append(i2).append("]").toString());
			Object personalActivity$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), personalActivity$2ArrayMiddleObject, i2);
			PersonalActivityHistoryRes.PersonalActivity personalActivity$2 = new PersonalActivityHistoryRes.PersonalActivity();
			personalActivity$2List.add(personalActivity$2);

			personalActivity$2.setMemberActivityType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "memberActivityType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setBoardName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardName" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setBoardListType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardListType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setBoardID((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setGroupNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setBoardSate((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setSourceSubject((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "sourceSubject" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setSourceWriterID((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "sourceWriterID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			personalActivity$2.setSourceWriterNickname((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "sourceWriterNickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, personalActivity$2MiddleWritableObject));

			pathStack.pop();
		}

		personalActivityHistoryRes.setPersonalActivityList(personalActivity$2List);

		pathStack.pop();

		return personalActivityHistoryRes;
	}
}