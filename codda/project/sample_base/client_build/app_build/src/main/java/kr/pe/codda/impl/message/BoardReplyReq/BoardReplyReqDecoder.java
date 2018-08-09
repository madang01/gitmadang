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

package kr.pe.codda.impl.message.BoardReplyReq;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardReplyReq message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardReplyReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardReplyReq");

		boardReplyReq.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setParentBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "parentBoardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "subject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setContent((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "content" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setWriterID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "writerID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setAttachedFileCnt((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int attachedFile$2ListSize = boardReplyReq.getAttachedFileCnt();
		Object attachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardReplyReq.AttachedFile> attachedFile$2List = new java.util.ArrayList<BoardReplyReq.AttachedFile>();
		for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
			Object attachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
			BoardReplyReq.AttachedFile attachedFile$2 = new BoardReplyReq.AttachedFile();
			attachedFile$2List.add(attachedFile$2);

			attachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardReplyReq.setAttachedFileList(attachedFile$2List);

		pathStack.pop();

		return boardReplyReq;
	}
}