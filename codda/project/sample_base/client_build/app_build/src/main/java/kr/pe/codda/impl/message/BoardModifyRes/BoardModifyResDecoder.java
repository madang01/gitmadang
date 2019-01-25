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

package kr.pe.codda.impl.message.BoardModifyRes;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardModifyRes message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardModifyResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardModifyRes boardModifyRes = new BoardModifyRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardModifyRes");

		boardModifyRes.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyRes.setBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardModifyRes.setDeletedAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "deletedAttachedFileCnt" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int deletedAttachedFile$2ListSize = boardModifyRes.getDeletedAttachedFileCnt();
		if (deletedAttachedFile$2ListSize < 0) {
			String errorMessage = new StringBuilder("the var deletedAttachedFile$2ListSize is less than zero").toString();
			throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
		}

		Object deletedAttachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "deletedAttachedFile", deletedAttachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardModifyRes.DeletedAttachedFile> deletedAttachedFile$2List = new java.util.ArrayList<BoardModifyRes.DeletedAttachedFile>();
		for (int i2=0; i2 < deletedAttachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("DeletedAttachedFile").append("[").append(i2).append("]").toString());
			Object deletedAttachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), deletedAttachedFile$2ArrayMiddleObject, i2);
			BoardModifyRes.DeletedAttachedFile deletedAttachedFile$2 = new BoardModifyRes.DeletedAttachedFile();
			deletedAttachedFile$2List.add(deletedAttachedFile$2);

			deletedAttachedFile$2.setAttachedFileSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileSeq" // itemName
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, deletedAttachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardModifyRes.setDeletedAttachedFileList(deletedAttachedFile$2List);

		pathStack.pop();

		return boardModifyRes;
	}
}