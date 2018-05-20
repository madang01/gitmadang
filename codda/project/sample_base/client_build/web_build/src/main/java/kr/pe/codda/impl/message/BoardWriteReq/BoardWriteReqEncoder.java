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
package kr.pe.codda.impl.message.BoardWriteReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardWriteReq 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardWriteReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardWriteReq boardWriteReq = (BoardWriteReq)messageObj;
		encodeBody(boardWriteReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardWriteReq boardWriteReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardWriteReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardId"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardWriteReq.getBoardId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardWriteReq.getSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "content"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardWriteReq.getContent() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachId"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardWriteReq.getAttachId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "userId"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardWriteReq.getUserId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardWriteReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}