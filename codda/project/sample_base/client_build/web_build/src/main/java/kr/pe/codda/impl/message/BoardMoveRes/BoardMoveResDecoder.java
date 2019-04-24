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

package kr.pe.codda.impl.message.BoardMoveRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardMoveRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardMoveResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardMoveRes boardMoveRes = new BoardMoveRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardMoveRes");

		boardMoveRes.setSourceBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "sourceBoardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardMoveRes.setSourceBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "sourceBoardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardMoveRes.setTargetBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "targetBoardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardMoveRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int boardMoveInfo$2ListSize = boardMoveRes.getCnt();
		if (boardMoveInfo$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var boardMoveInfo$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object boardMoveInfo$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "boardMoveInfo", boardMoveInfo$2ListSize, middleReadableObject);
		java.util.List<BoardMoveRes.BoardMoveInfo> boardMoveInfo$2List = new java.util.ArrayList<BoardMoveRes.BoardMoveInfo>();
		for (int i2=0; i2 < boardMoveInfo$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardMoveInfo").append("[").append(i2).append("]").toString());
			Object boardMoveInfo$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), boardMoveInfo$2ArrayMiddleObject, i2);
			BoardMoveRes.BoardMoveInfo boardMoveInfo$2 = new BoardMoveRes.BoardMoveInfo();
			boardMoveInfo$2List.add(boardMoveInfo$2);

			boardMoveInfo$2.setFromBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "fromBoardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardMoveInfo$2MiddleWritableObject));

			boardMoveInfo$2.setToBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "toBoardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardMoveInfo$2MiddleWritableObject));

			boardMoveInfo$2.setAttachedFileCnt((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileCnt" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, boardMoveInfo$2MiddleWritableObject));

			int attachedFile$3ListSize = boardMoveInfo$2.getAttachedFileCnt();
			if (attachedFile$3ListSize < 0) {
				String errorMessage = new StringBuilder("the var attachedFile$3ListSize is less than zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object attachedFile$3ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$3ListSize, boardMoveInfo$2MiddleWritableObject);
			java.util.List<BoardMoveRes.BoardMoveInfo.AttachedFile> attachedFile$3List = new java.util.ArrayList<BoardMoveRes.BoardMoveInfo.AttachedFile>();
			for (int i3=0; i3 < attachedFile$3ListSize; i3++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i3).append("]").toString());
				Object attachedFile$3MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachedFile$3ArrayMiddleObject, i3);
				BoardMoveRes.BoardMoveInfo.AttachedFile attachedFile$3 = new BoardMoveRes.BoardMoveInfo.AttachedFile();
				attachedFile$3List.add(attachedFile$3);

				attachedFile$3.setAttachedFileSeq((Short)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "attachedFileSeq" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				pathStack.pop();
			}

			boardMoveInfo$2.setAttachedFileList(attachedFile$3List);

			pathStack.pop();
		}

		boardMoveRes.setBoardMoveInfoList(boardMoveInfo$2List);

		pathStack.pop();

		return boardMoveRes;
	}
}