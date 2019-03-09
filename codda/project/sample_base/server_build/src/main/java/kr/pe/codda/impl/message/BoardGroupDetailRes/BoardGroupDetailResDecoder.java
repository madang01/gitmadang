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

package kr.pe.codda.impl.message.BoardGroupDetailRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardGroupDetailRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardGroupDetailResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardGroupDetailRes boardGroupDetailRes = new BoardGroupDetailRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardGroupDetailRes");

		boardGroupDetailRes.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootBoardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootViewCount((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootViewCount" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootBoardSate((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootBoardSate" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootNickname((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootNickname" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootVotes((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootVotes" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootSubject" // itemName
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootContents((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootContents" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootWriterID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootWriterID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootRegisteredDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootRegisteredDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootLastModifierID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootLastModifierID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootLastModifierNickName((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootLastModifierNickName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootLastModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootLastModifiedDate" // itemName
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootNextAttachedFileSeq((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootNextAttachedFileSeq" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardGroupDetailRes.setRootAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootAttachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int rootAttachedFile$2ListSize = boardGroupDetailRes.getRootAttachedFileCnt();
		if (rootAttachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var rootAttachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object rootAttachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "rootAttachedFile", rootAttachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardGroupDetailRes.RootAttachedFile> rootAttachedFile$2List = new java.util.ArrayList<BoardGroupDetailRes.RootAttachedFile>();
		for (int i2=0; i2 < rootAttachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("RootAttachedFile").append("[").append(i2).append("]").toString());
			Object rootAttachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), rootAttachedFile$2ArrayMiddleObject, i2);
			BoardGroupDetailRes.RootAttachedFile rootAttachedFile$2 = new BoardGroupDetailRes.RootAttachedFile();
			rootAttachedFile$2List.add(rootAttachedFile$2);

			rootAttachedFile$2.setAttachedFileSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, rootAttachedFile$2MiddleWritableObject));

			rootAttachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, rootAttachedFile$2MiddleWritableObject));

			rootAttachedFile$2.setAttachedFileSize((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSize" // itemName
				, kr.pe.codda.common.type.SingleItemType.LONG // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, rootAttachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardGroupDetailRes.setRootAttachedFileList(rootAttachedFile$2List);

		boardGroupDetailRes.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int childBoard$2ListSize = boardGroupDetailRes.getCnt();
		if (childBoard$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var childBoard$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object childBoard$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "childBoard", childBoard$2ListSize, middleReadableObject);
		java.util.List<BoardGroupDetailRes.ChildBoard> childBoard$2List = new java.util.ArrayList<BoardGroupDetailRes.ChildBoard>();
		for (int i2=0; i2 < childBoard$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildBoard").append("[").append(i2).append("]").toString());
			Object childBoard$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), childBoard$2ArrayMiddleObject, i2);
			BoardGroupDetailRes.ChildBoard childBoard$2 = new BoardGroupDetailRes.ChildBoard();
			childBoard$2List.add(childBoard$2);

			childBoard$2.setBoardNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setGroupNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setGroupSeq((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "groupSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setParentNo((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "parentNo" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setDepth((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "depth" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setWriterID((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "writerID" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setWriterNickname((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "writerNickname" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setViewCount((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "viewCount" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setBoardSate((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "boardSate" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setRegisteredDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "registeredDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setVotes((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "votes" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setSubject((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "subject" // itemName
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setLastModifiedDate((java.sql.Timestamp)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "lastModifiedDate" // itemName
				, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			childBoard$2.setAttachedFileCnt((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileCnt" // itemName
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, childBoard$2MiddleWritableObject));

			int attachedFile$3ListSize = childBoard$2.getAttachedFileCnt();
			if (attachedFile$3ListSize < 0) {
				String errorMessage = new StringBuilder("the var attachedFile$3ListSize is less than zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object attachedFile$3ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$3ListSize, childBoard$2MiddleWritableObject);
			java.util.List<BoardGroupDetailRes.ChildBoard.AttachedFile> attachedFile$3List = new java.util.ArrayList<BoardGroupDetailRes.ChildBoard.AttachedFile>();
			for (int i3=0; i3 < attachedFile$3ListSize; i3++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i3).append("]").toString());
				Object attachedFile$3MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachedFile$3ArrayMiddleObject, i3);
				BoardGroupDetailRes.ChildBoard.AttachedFile attachedFile$3 = new BoardGroupDetailRes.ChildBoard.AttachedFile();
				attachedFile$3List.add(attachedFile$3);

				attachedFile$3.setAttachedFileSeq((Short)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "attachedFileSeq" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				attachedFile$3.setAttachedFileName((String)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "attachedFileName" // itemName
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				attachedFile$3.setAttachedFileSize((Long)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "attachedFileSize" // itemName
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$3MiddleWritableObject));

				pathStack.pop();
			}

			childBoard$2.setAttachedFileList(attachedFile$3List);

			pathStack.pop();
		}

		boardGroupDetailRes.setChildBoardList(childBoard$2List);

		pathStack.pop();

		return boardGroupDetailRes;
	}
}