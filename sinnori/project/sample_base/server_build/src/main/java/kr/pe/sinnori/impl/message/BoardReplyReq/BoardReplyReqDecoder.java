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
package kr.pe.sinnori.impl.message.BoardReplyReq;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardReplyReq 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardReplyReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardReplyReq");

		boardReplyReq.setBoardId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setParentBoardNo((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "parentBoardNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setSubject((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "subject" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setContent((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "content" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setAttachId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setUserId((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "userId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardReplyReq.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardReplyReq;
	}
}