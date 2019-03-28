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

package kr.pe.codda.impl.message.BoardInfoListRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardInfoListRes message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardInfoListResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardInfoListRes boardInfoListRes = (BoardInfoListRes)messageObj;
		encodeBody(boardInfoListRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardInfoListRes boardInfoListRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardInfoListRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardInfoListRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardInfoListRes.BoardInfo> boardInfo$2List = boardInfoListRes.getBoardInfoList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == boardInfo$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardInfoListRes.getCnt()) {
				String errorMessage = new StringBuilder("the var boardInfo$2List is null but the value referenced by the array size[boardInfoListRes.getCnt()][").append(boardInfoListRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int boardInfo$2ListSize = boardInfo$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardInfoListRes.getCnt() != boardInfo$2ListSize) {
				String errorMessage = new StringBuilder("the var boardInfo$2ListSize[").append(boardInfo$2ListSize).append("] is not same to the value referenced by the array size[boardInfoListRes.getCnt()][").append(boardInfoListRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object boardInfo$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "boardInfo", boardInfo$2ListSize, middleWritableObject);
			for (int i2=0; i2 < boardInfo$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardInfo").append("[").append(i2).append("]").toString());
				Object boardInfo$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), boardInfo$2ArrayMiddleObject, i2);
				BoardInfoListRes.BoardInfo boardInfo$2 = boardInfo$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, boardInfo$2.getBoardID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, boardInfo$2.getBoardName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardListType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, boardInfo$2.getBoardListType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPolicyType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, boardInfo$2.getBoardReplyPolicyType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardWritePermissionType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, boardInfo$2.getBoardWritePermissionType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPermissionType"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, boardInfo$2.getBoardReplyPermissionType() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, boardInfo$2.getCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "total"
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, boardInfo$2.getTotal() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nextBoardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, boardInfo$2.getNextBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardInfo$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}