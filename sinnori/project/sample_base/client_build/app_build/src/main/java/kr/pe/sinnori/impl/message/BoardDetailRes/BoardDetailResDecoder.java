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
package kr.pe.sinnori.impl.message.BoardDetailRes;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardDetailRes 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailResDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardDetailRes boardDetailRes = new BoardDetailRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardDetailRes");

		boardDetailRes.setBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setBoardId((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setGroupNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "groupNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setGroupSeq((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "groupSeq" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setParentNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "parentNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setDepth((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "depth" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "subject" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setContent((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "content" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setWriterId((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "writerId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setNickname((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "nickname" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setViewCount((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "viewCount" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setVotes((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "votes" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setBoardSate((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardSate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setRegisterDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "registerDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "modifiedDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setAttachId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setAttachFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachFileCnt" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int attachFile$2ListSize = boardDetailRes.getAttachFileCnt();
		Object attachFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "attachFile", attachFile$2ListSize, middleReadableObject);
		java.util.List<BoardDetailRes.AttachFile> attachFile$2List = new java.util.ArrayList<BoardDetailRes.AttachFile>();
		for (int i2=0; i2 < attachFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachFile").append("[").append(i2).append("]").toString());
			Object attachFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), attachFile$2ArrayMiddleObject, i2);
			BoardDetailRes.AttachFile attachFile$2 = new BoardDetailRes.AttachFile();
			attachFile$2List.add(attachFile$2);

			attachFile$2.setAttachSeq((Long)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachSeq" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachFile$2MiddleWritableObject));

			attachFile$2.setAttachFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachFileName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, attachFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardDetailRes.setAttachFileList(attachFile$2List);

		boardDetailRes.setMembershipLevel((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "membershipLevel" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardDetailRes.setMemberState((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "memberState" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardDetailRes;
	}
}