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

package kr.pe.codda.impl.message.BoardListRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardListRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardListResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardListRes boardListRes = new BoardListRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListRes");

		boardListRes.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListRes.setPageOffset((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pageOffset" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListRes.setPageLength((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pageLength" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListRes.setTotal((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "total" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int board$2ListSize = boardListRes.getCnt();
		Object board$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "board", board$2ListSize, middleReadableObject);
		java.util.List<BoardListRes.Board> board$2List = new java.util.ArrayList<BoardListRes.Board>();
		for (int i2=0; i2 < board$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Board").append("[").append(i2).append("]").toString());
			Object board$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), board$2ArrayMiddleObject, i2);
			BoardListRes.Board board$2 = new BoardListRes.Board();
			board$2List.add(board$2);

			board$2.setBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setGroupSeq((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setParentNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setDepth((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "depth" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setWriterID((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "writerID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setViewCount((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "viewCount" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setBoardSate((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setNickname((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "nickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setVotes((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "votes" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setSubject((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "subject" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			board$2.setFinalModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "finalModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, board$2MiddleWritableObject));

			pathStack.pop();
		}

		boardListRes.setBoardList(board$2List);

		pathStack.pop();

		return boardListRes;
	}
}