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

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * PersonalActivityHistoryRes message encoder
 * @author Won Jonghoon
 *
 */
public final class PersonalActivityHistoryResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		PersonalActivityHistoryRes personalActivityHistoryRes = (PersonalActivityHistoryRes)messageObj;
		encodeBody(personalActivityHistoryRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(PersonalActivityHistoryRes personalActivityHistoryRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("PersonalActivityHistoryRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "targetUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, personalActivityHistoryRes.getTargetUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "targetUserNickname"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, personalActivityHistoryRes.getTargetUserNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "total"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, personalActivityHistoryRes.getTotal() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageNo"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, personalActivityHistoryRes.getPageNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, personalActivityHistoryRes.getPageSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, personalActivityHistoryRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<PersonalActivityHistoryRes.PersonalActivity> personalActivity$2List = personalActivityHistoryRes.getPersonalActivityList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == personalActivity$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != personalActivityHistoryRes.getCnt()) {
				String errorMessage = new StringBuilder("the var personalActivity$2List is null but the value referenced by the array size[personalActivityHistoryRes.getCnt()][").append(personalActivityHistoryRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int personalActivity$2ListSize = personalActivity$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (personalActivityHistoryRes.getCnt() != personalActivity$2ListSize) {
				String errorMessage = new StringBuilder("the var personalActivity$2ListSize[").append(personalActivity$2ListSize).append("] is not same to the value referenced by the array size[personalActivityHistoryRes.getCnt()][").append(personalActivityHistoryRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object personalActivity$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "personalActivity", personalActivity$2ListSize, middleWritableObject);
			for (int i2=0; i2 < personalActivity$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("PersonalActivity").append("[").append(i2).append("]").toString());
				Object personalActivity$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), personalActivity$2ArrayMiddleObject, i2);
				PersonalActivityHistoryRes.PersonalActivity personalActivity$2 = personalActivity$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberActivityType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, personalActivity$2.getMemberActivityType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, personalActivity$2.getBoardName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardListType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, personalActivity$2.getBoardListType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, personalActivity$2.getBoardID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, personalActivity$2.getBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, personalActivity$2.getGroupNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardSate"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, personalActivity$2.getBoardSate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registeredDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, personalActivity$2.getRegisteredDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sourceSubject"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, personalActivity$2.getSourceSubject() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sourceWriterID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, personalActivity$2.getSourceWriterID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sourceWriterNickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, personalActivity$2.getSourceWriterNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, personalActivity$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}