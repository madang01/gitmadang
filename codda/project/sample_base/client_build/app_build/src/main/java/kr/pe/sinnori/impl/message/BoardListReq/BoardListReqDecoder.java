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
package kr.pe.sinnori.impl.message.BoardListReq;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardListReq 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardListReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardListReq boardListReq = new BoardListReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListReq");

		boardListReq.setBoardId((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListReq.setStartNo((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "startNo" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardListReq.setPageSize((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "pageSize" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardListReq;
	}
}