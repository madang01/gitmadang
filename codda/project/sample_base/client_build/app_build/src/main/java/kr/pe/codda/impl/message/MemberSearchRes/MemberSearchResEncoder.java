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

package kr.pe.codda.impl.message.MemberSearchRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * MemberSearchRes message encoder
 * @author Won Jonghoon
 *
 */
public final class MemberSearchResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		MemberSearchRes memberSearchRes = (MemberSearchRes)messageObj;
		encodeBody(memberSearchRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(MemberSearchRes memberSearchRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MemberSearchRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberState"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, memberSearchRes.getMemberState() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "searchID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberSearchRes.getSearchID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "fromDateString"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberSearchRes.getFromDateString() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "toDateString"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, memberSearchRes.getToDateString() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageNo"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, memberSearchRes.getPageNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, memberSearchRes.getPageSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "isNextPage"
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, memberSearchRes.getIsNextPage() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, memberSearchRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<MemberSearchRes.Member> member$2List = memberSearchRes.getMemberList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == member$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != memberSearchRes.getCnt()) {
				String errorMessage = new StringBuilder("the var member$2List is null but the value referenced by the array size[memberSearchRes.getCnt()][").append(memberSearchRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int member$2ListSize = member$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (memberSearchRes.getCnt() != member$2ListSize) {
				String errorMessage = new StringBuilder("the var member$2ListSize[").append(member$2ListSize).append("] is not same to the value referenced by the array size[memberSearchRes.getCnt()][").append(memberSearchRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object member$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "member", member$2ListSize, middleWritableObject);
			for (int i2=0; i2 < member$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
				Object member$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
				MemberSearchRes.Member member$2 = member$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "userID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, member$2.getUserID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, member$2.getNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "passwordFailCount"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, member$2.getPasswordFailCount() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registeredDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, member$2.getRegisteredDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastNicknameModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, member$2.getLastNicknameModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastEmailModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, member$2.getLastEmailModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastPasswordModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, member$2.getLastPasswordModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastStateModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, member$2.getLastStateModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}