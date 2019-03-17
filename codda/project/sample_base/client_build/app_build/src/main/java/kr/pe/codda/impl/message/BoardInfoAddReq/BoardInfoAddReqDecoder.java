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

package kr.pe.codda.impl.message.BoardInfoAddReq;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardInfoAddReq message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardInfoAddReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardInfoAddReq");

		boardInfoAddReq.setRequestedUserID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "requestedUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoAddReq.setBoardName((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoAddReq.setBoardListType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardListType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoAddReq.setBoardReplyPolicyType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardReplyPolicyType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoAddReq.setBoardWritePermissionType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardWritePermissionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoAddReq.setBoardReplyPermissionType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardReplyPermissionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardInfoAddReq;
	}
}