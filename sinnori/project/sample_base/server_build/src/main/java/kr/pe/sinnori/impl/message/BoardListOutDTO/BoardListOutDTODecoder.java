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

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardListOutDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardListOutDTODecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardListOutDTO boardListOutDTO = new BoardListOutDTO();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListOutDTO");

		boardListOutDTO.setBoardId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListOutDTO.setStartNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "startNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListOutDTO.setPageSize((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListOutDTO.setTotal((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "total" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListOutDTO.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int board$2ListSize = boardListOutDTO.getCnt();
		Object board$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "board", board$2ListSize, middleReadableObject);
		java.util.List<BoardListOutDTO.Board> board$2List = new java.util.ArrayList<BoardListOutDTO.Board>();
		for (int i2=0; i2 < board$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Board").append("[").append(i2).append("]").toString());
			Object board$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), board$2ArrayMiddleObject, i2);
			BoardListOutDTO.Board board$2 = new BoardListOutDTO.Board();
			board$2List.add(board$2);

			board$2.setBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupNo" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupSeq((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupSeq" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setParentNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setDepth((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "depth" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setSubject((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "subject" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setWriterId((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "writerId" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setNickname((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "nickname" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setViewCount((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "viewCount" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setVotes((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "votes" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setDeleteFlag((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "deleteFlag" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setRegisterDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "registerDate" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "modifiedDate" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setMemberGubunName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "memberGubunName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			pathStack.pop();
		}

		boardListOutDTO.setBoardList(board$2List);

		pathStack.pop();

		return boardListOutDTO;
	}
}