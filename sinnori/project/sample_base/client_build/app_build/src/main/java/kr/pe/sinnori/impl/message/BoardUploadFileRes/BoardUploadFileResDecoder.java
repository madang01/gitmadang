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
package kr.pe.sinnori.impl.message.BoardUploadFileRes;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardUploadFileRes 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardUploadFileRes boardUploadFileRes = new BoardUploadFileRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardUploadFileRes");

		boardUploadFileRes.setAttachId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileRes.setOwnerId((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ownerId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileRes.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileRes.setRegisterDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "registerDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileRes.setModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "modifiedDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileRes.setAttachedFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachedFileCnt" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int attachedFile$2ListSize = boardUploadFileRes.getAttachedFileCnt();
		Object attachedFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, middleReadableObject);
		java.util.List<BoardUploadFileRes.AttachedFile> attachedFile$2List = new java.util.ArrayList<BoardUploadFileRes.AttachedFile>();
		for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
			Object attachedFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
			BoardUploadFileRes.AttachedFile attachedFile$2 = new BoardUploadFileRes.AttachedFile();
			attachedFile$2List.add(attachedFile$2);

			attachedFile$2.setAttachSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachSeq" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setAttachedFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachedFileName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			attachedFile$2.setSystemFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "systemFileName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachedFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardUploadFileRes.setAttachedFileList(attachedFile$2List);

		pathStack.pop();

		return boardUploadFileRes;
	}
}