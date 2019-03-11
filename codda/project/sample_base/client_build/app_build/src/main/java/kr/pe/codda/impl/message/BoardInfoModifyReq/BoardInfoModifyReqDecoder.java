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

package kr.pe.codda.impl.message.BoardInfoModifyReq;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * BoardInfoModifyReq message decoder
 * @author Won Jonghoon
 *
 */
public final class BoardInfoModifyReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardInfoModifyReq boardInfoModifyReq = new BoardInfoModifyReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardInfoModifyReq");

		boardInfoModifyReq.setRequestedUserID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "requestedUserID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardID((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardID" // itemName
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardName((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardName" // itemName
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardInformation((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardInformation" // itemName
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardReplyPolicyType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardReplyPolicyType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardWritePermissionType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardWritePermissionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardInfoModifyReq.setBoardReplyPermissionType((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "boardReplyPermissionType" // itemName
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardInfoModifyReq;
	}
}