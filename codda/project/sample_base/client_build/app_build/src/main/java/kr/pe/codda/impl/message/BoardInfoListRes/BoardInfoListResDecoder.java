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

package kr.pe.codda.impl.message.BoardInfoListRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardInfoListRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardInfoListResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardInfoListRes boardInfoListRes = new BoardInfoListRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardInfoListRes");

		boardInfoListRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int boardInfo$2ListSize = boardInfoListRes.getCnt();
		if (boardInfo$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var boardInfo$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object boardInfo$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "boardInfo", boardInfo$2ListSize, middleReadableObject);
		java.util.List<BoardInfoListRes.BoardInfo> boardInfo$2List = new java.util.ArrayList<BoardInfoListRes.BoardInfo>();
		for (int i2=0; i2 < boardInfo$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardInfo").append("[").append(i2).append("]").toString());
			Object boardInfo$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), boardInfo$2ArrayMiddleObject, i2);
			BoardInfoListRes.BoardInfo boardInfo$2 = new BoardInfoListRes.BoardInfo();
			boardInfo$2List.add(boardInfo$2);

			boardInfo$2.setBoardID((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setBoardName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardName" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setBoardListType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardListType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setBoardReplyPolicyType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardReplyPolicyType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setBoardWritePermissionType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardWritePermissionType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setBoardReplyPermissionType((Byte)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardReplyPermissionType" // itemName
				, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setCnt((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "cnt" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setTotal((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "total" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			boardInfo$2.setNextBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "nextBoardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardInfo$2MiddleWritableObject));

			pathStack.pop();
		}

		boardInfoListRes.setBoardInfoList(boardInfo$2List);

		pathStack.pop();

		return boardInfoListRes;
	}
}