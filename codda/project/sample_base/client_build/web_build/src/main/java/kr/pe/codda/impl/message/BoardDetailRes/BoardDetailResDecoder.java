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

package kr.pe.codda.impl.message.BoardDetailRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardDetailRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardDetailResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardDetailRes boardDetailRes = new BoardDetailRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardDetailRes");

		boardDetailRes.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setGroupNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "groupNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setGroupSeq((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "groupSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setParentNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "parentNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setDepth((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "depth" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setViewCount((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "viewCount" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setBoardSate((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardSate" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setNickname((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setVotes((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "votes" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "subject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setContent((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "content" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setWriterID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "writerID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setWriterIP((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "writerIP" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "registeredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setLastModifierIP((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "lastModifierIP" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setLastModifierID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "lastModifierID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setLastModifierNickName((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "lastModifierNickName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setLastModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "lastModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setNextAttachedFileSeq((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nextAttachedFileSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int attachedFile$2ListSize = boardDetailRes.getAttachedFileCnt();
		if (attachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var attachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object attachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardDetailRes.AttachedFile> attachedFile$2List = new java.util.ArrayList<BoardDetailRes.AttachedFile>();
		for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
			Object attachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
			BoardDetailRes.AttachedFile attachedFile$2 = new BoardDetailRes.AttachedFile();
			attachedFile$2List.add(attachedFile$2);

			attachedFile$2.setAttachedFileSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setAttachedFileSize((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSize" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardDetailRes.setAttachedFileList(attachedFile$2List);

		pathStack.pop();

		return boardDetailRes;
	}
}