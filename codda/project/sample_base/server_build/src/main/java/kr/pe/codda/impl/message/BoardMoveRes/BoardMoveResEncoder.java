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

package kr.pe.codda.impl.message.BoardMoveRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardMoveRes message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardMoveResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardMoveRes boardMoveRes = (BoardMoveRes)messageObj;
		encodeBody(boardMoveRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardMoveRes boardMoveRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardMoveRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sourceBoardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardMoveRes.getSourceBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sourceBoardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardMoveRes.getSourceBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "targetBoardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardMoveRes.getTargetBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardMoveRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardMoveRes.BoardMoveInfo> boardMoveInfo$2List = boardMoveRes.getBoardMoveInfoList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == boardMoveInfo$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardMoveRes.getCnt()) {
				String errorMessage = new StringBuilder("the var boardMoveInfo$2List is null but the value referenced by the array size[boardMoveRes.getCnt()][").append(boardMoveRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int boardMoveInfo$2ListSize = boardMoveInfo$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardMoveRes.getCnt() != boardMoveInfo$2ListSize) {
				String errorMessage = new StringBuilder("the var boardMoveInfo$2ListSize[").append(boardMoveInfo$2ListSize).append("] is not same to the value referenced by the array size[boardMoveRes.getCnt()][").append(boardMoveRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object boardMoveInfo$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "boardMoveInfo", boardMoveInfo$2ListSize, middleWritableObject);
			for (int i2=0; i2 < boardMoveInfo$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("BoardMoveInfo").append("[").append(i2).append("]").toString());
				Object boardMoveInfo$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), boardMoveInfo$2ArrayMiddleObject, i2);
				BoardMoveRes.BoardMoveInfo boardMoveInfo$2 = boardMoveInfo$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "fromBoardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, boardMoveInfo$2.getFromBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardMoveInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "toBoardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, boardMoveInfo$2.getToBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardMoveInfo$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileCnt"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, boardMoveInfo$2.getAttachedFileCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, boardMoveInfo$2MiddleWritableObject);

				java.util.List<BoardMoveRes.BoardMoveInfo.AttachedFile> attachedFile$4List = boardMoveInfo$2.getAttachedFileList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == attachedFile$4List) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != boardMoveInfo$2.getAttachedFileCnt()) {
						String errorMessage = new StringBuilder("the var attachedFile$4List is null but the value referenced by the array size[boardMoveInfo$2.getAttachedFileCnt()][").append(boardMoveInfo$2.getAttachedFileCnt()).append("] is not zero").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int attachedFile$4ListSize = attachedFile$4List.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (boardMoveInfo$2.getAttachedFileCnt() != attachedFile$4ListSize) {
						String errorMessage = new StringBuilder("the var attachedFile$4ListSize[").append(attachedFile$4ListSize).append("] is not same to the value referenced by the array size[boardMoveInfo$2.getAttachedFileCnt()][").append(boardMoveInfo$2.getAttachedFileCnt()).append("]").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}

					Object attachedFile$4ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$4ListSize, boardMoveInfo$2MiddleWritableObject);
					for (int i4=0; i4 < attachedFile$4ListSize; i4++) {
						pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i4).append("]").toString());
						Object attachedFile$4MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachedFile$4ArrayMiddleObject, i4);
						BoardMoveRes.BoardMoveInfo.AttachedFile attachedFile$4 = attachedFile$4List.get(i4);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
							, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
							, attachedFile$4.getAttachedFileSeq() // itemValue
							, -1 // itemSize
							, null // nativeItemCharset
							, attachedFile$4MiddleWritableObject);

						pathStack.pop();
					}
				}

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}