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

package kr.pe.codda.impl.message.BoardListReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardListReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardListReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardListReq boardListReq = (BoardListReq)messageObj;
		encodeBody(boardListReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardListReq boardListReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardListReq.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageOffset"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, boardListReq.getPageOffset() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageLength"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, boardListReq.getPageLength() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}