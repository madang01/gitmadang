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

package kr.pe.codda.impl.message.BoardListRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardListRes message encoder
 * @author Won Jonghooon
 *
 */
public final class BoardListResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardListRes boardListRes = (BoardListRes)messageObj;
		encodeBody(boardListRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardListRes boardListRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardListRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardId"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardListRes.getBoardId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "startNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardListRes.getStartNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "pageSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, boardListRes.getPageSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "total"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardListRes.getTotal() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardListRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardListRes.Board> board$2List = boardListRes.getBoardList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == board$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardListRes.getCnt()) {
				String errorMessage = new StringBuilder("the var board$2List is null but the value referenced by the array size[boardListRes.getCnt()][").append(boardListRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int board$2ListSize = board$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardListRes.getCnt() != board$2ListSize) {
				String errorMessage = new StringBuilder("the var board$2ListSize[").append(board$2ListSize).append("] is not same to the value referenced by the array size[boardListRes.getCnt()][").append(boardListRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object board$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "board", board$2ListSize, middleWritableObject);
			for (int i2=0; i2 < board$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Board").append("[").append(i2).append("]").toString());
				Object board$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), board$2ArrayMiddleObject, i2);
				BoardListRes.Board board$2 = board$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, board$2.getBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, board$2.getGroupNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
					, board$2.getGroupSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, board$2.getParentNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, board$2.getDepth() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, board$2.getSubject() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "writerId"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, board$2.getWriterId() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, board$2.getNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "viewCount"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, board$2.getViewCount() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "votes"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, board$2.getVotes() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardSate"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, board$2.getBoardSate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registerDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, board$2.getRegisterDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "modifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, board$2.getModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberGubunName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, board$2.getMemberGubunName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, board$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}