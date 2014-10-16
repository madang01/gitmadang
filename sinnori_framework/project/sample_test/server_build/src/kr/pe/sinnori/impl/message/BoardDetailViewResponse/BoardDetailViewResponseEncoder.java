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
package kr.pe.sinnori.impl.message.BoardDetailViewResponse;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardDetailViewResponse 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailViewResponseEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardDetailViewResponse)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardDetailViewResponse 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardDetailViewResponse boardDetailViewResponse = (BoardDetailViewResponse) messageObj;
		encodeBody(boardDetailViewResponse, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardDetailViewResponse 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardDetailViewResponse BoardDetailViewResponse 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardDetailViewResponse boardDetailViewResponse, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardDetailViewResponseSingleItemPath = "BoardDetailViewResponse";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardDetailViewResponseSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "boardNO"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailViewResponse.getBoardNO() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "groupNO"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailViewResponse.getGroupNO() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "parentNO"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailViewResponse.getParentNO() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "groupSeq"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailViewResponse.getGroupSeq() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "depth"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailViewResponse.getDepth() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "boardTypeID"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailViewResponse.getBoardTypeID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "title"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailViewResponse.getTitle() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "contents"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailViewResponse.getContents() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "userID"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailViewResponse.getUserID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "viewCnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailViewResponse.getViewCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "votes"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailViewResponse.getVotes() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "isDeleted"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailViewResponse.getIsDeleted() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "createDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardDetailViewResponse.getCreateDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "lastModifiedDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardDetailViewResponse.getLastModifiedDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "memberGubun"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailViewResponse.getMemberGubun() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailViewResponseSingleItemPath, "memberStatus"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailViewResponse.getMemberStatus() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}