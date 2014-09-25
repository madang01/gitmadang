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
package kr.pe.sinnori.impl.message.MemberListResult;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * MemberListResult 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class MemberListResultEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof MemberListResult)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 MemberListResult 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		MemberListResult memberListResult = (MemberListResult) messageObj;
		encodeBody(memberListResult, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * MemberListResult 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param memberListResult MemberListResult 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(MemberListResult memberListResult, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String memberListResultSingleItemPath = "MemberListResult";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(memberListResultSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(memberListResultSingleItemPath, "cnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, memberListResult.getCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		MemberListResult.Member[] memberList = memberListResult.getMemberList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == memberList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != memberListResult.getCnt()) {
				String errorMessage = new StringBuilder(memberListResultSingleItemPath)
				.append(".")
				.append("memberList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (memberList.length != memberListResult.getCnt()) {
				String errorMessage = new StringBuilder(memberListResultSingleItemPath)
				.append(".")
				.append("memberList.length[")
				.append(memberList.length)
				.append("] is not same to ")
				.append(memberListResultSingleItemPath)
				.append(".")
				.append("cnt[")
				.append(memberListResult.getCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object memberMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(memberListResultSingleItemPath, "member", memberList.length, middleWriteObj);
			for (int i=0; i < memberList.length; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Member").append("[").append(i).append("]").toString());
				String memberSingleItemPath = singleItemPathStatck.getLast();
				Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
				MemberListResult.Member member = memberList[i];
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "id"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, member.getId() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "pwd"
							, 8 // itemTypeID
							, "us pascal string" // itemTypeName
							, member.getPwd() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "email"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, member.getEmail() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "phone"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, member.getPhone() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "regdate"
							, 16 // itemTypeID
							, "java sql timestamp" // itemTypeName
							, member.getRegdate() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}