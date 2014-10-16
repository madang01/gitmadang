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
package kr.pe.sinnori.impl.message.BoardListResponse;

import java.nio.charset.Charset;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * BoardListResponse 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardListResponseDecoder extends MessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardListResponse 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardListResponse 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		BoardListResponse boardListResponse = new BoardListResponse();
		String sigleItemPath0 = "BoardListResponse";

		boardListResponse.setCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		Object boardMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "board", boardListResponse.getCnt(), middleReadObj);
		BoardListResponse.Board[] boardList = new BoardListResponse.Board[boardListResponse.getCnt()];
		for (int i=0; i < boardList.length; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Board[").append(i).append("]").toString();
			Object boardMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, boardMiddleReadArray, i);
			boardList[i] = new BoardListResponse.Board();

			boardList[i].setBoardNO((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "boardNO" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setGroupNO((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "groupNO" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setParentNO((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "parentNO" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setGroupSeq((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "groupSeq" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setDepth((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "depth" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setBoardTypeID((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "boardTypeID" // itemName
			, 6 // itemTypeID
			, "long" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setTitle((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "title" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setContents((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "contents" // itemName
			, 9 // itemTypeID
			, "si pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setUserID((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "userID" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setViewCnt((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "viewCnt" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setVotes((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "votes" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setIsDeleted((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "isDeleted" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setCreateDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "createDate" // itemName
			, 16 // itemTypeID
			, "java sql timestamp" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setLastModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "lastModifiedDate" // itemName
			, 16 // itemTypeID
			, "java sql timestamp" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			boardList[i].setMemberGubun((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "memberGubun" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));
boardListResponse.setBoardList(boardList);
		}
		return boardListResponse;
	}
}