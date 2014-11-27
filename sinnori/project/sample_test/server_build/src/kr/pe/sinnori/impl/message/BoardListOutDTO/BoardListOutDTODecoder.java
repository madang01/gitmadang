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
package kr.pe.sinnori.impl.message.BoardListOutDTO;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * BoardListOutDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardListOutDTODecoder extends MessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardListOutDTO 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardListOutDTO 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		BoardListOutDTO boardListOutDTO = new BoardListOutDTO();
		String sigleItemPath0 = "BoardListOutDTO";

		boardListOutDTO.setBoardId((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "boardId" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardListOutDTO.setStartNo((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "startNo" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardListOutDTO.setPageSize((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "pageSize" // itemName
		, 3 // itemTypeID
		, "unsigned short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardListOutDTO.setTotal((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "total" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardListOutDTO.setCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int boardListSize = boardListOutDTO.getCnt();
		Object boardMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "board", boardListSize, middleReadObj);
		java.util.List<BoardListOutDTO.Board> boardList = new java.util.ArrayList<BoardListOutDTO.Board>();
		for (int i=0; i < boardListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Board[").append(i).append("]").toString();
			Object boardMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, boardMiddleReadArray, i);
			BoardListOutDTO.Board board = new BoardListOutDTO.Board();
			boardList.add(board);

			board.setBoardNo((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "boardNo" // itemName
			, 5 // itemTypeID
			, "unsigned integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setGroupNo((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "groupNo" // itemName
			, 5 // itemTypeID
			, "unsigned integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setGroupSeq((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "groupSeq" // itemName
			, 3 // itemTypeID
			, "unsigned short" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setParentNo((Long)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "parentNo" // itemName
			, 5 // itemTypeID
			, "unsigned integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setDepth((Short)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "depth" // itemName
			, 1 // itemTypeID
			, "unsigned byte" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setSubject((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "subject" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setWriterId((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "writerId" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setNickname((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "nickname" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setViewCount((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "viewCount" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setVotes((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "votes" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setDeleteFlag((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "deleteFlag" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setRegisterDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "registerDate" // itemName
			, 16 // itemTypeID
			, "java sql timestamp" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "modifiedDate" // itemName
			, 16 // itemTypeID
			, "java sql timestamp" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));

			board.setMemberGubunName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "memberGubunName" // itemName
			, 7 // itemTypeID
			, "ub pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, boardMiddleReadObj));
		}
		boardListOutDTO.setBoardList(boardList);
		return boardListOutDTO;
	}
}