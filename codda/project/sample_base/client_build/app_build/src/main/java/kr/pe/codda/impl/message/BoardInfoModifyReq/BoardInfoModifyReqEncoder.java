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

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardInfoModifyReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardInfoModifyReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardInfoModifyReq boardInfoModifyReq = (BoardInfoModifyReq)messageObj;
		encodeBody(boardInfoModifyReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardInfoModifyReq boardInfoModifyReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardInfoModifyReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "requestedUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardInfoModifyReq.getRequestedUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardInfoModifyReq.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardName"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardInfoModifyReq.getBoardName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardInformation"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardInfoModifyReq.getBoardInformation() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPolicyType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardInfoModifyReq.getBoardReplyPolicyType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardWritePermissionType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardInfoModifyReq.getBoardWritePermissionType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPermissionType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardInfoModifyReq.getBoardReplyPermissionType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}