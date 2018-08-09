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

package kr.pe.codda.impl.message.BoardReplyReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardReplyReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardReplyReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardReplyReq boardReplyReq = (BoardReplyReq)messageObj;
		encodeBody(boardReplyReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardReplyReq boardReplyReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardReplyReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardReplyReq.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentBoardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardReplyReq.getParentBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardReplyReq.getSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "content"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardReplyReq.getContent() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "writerID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardReplyReq.getWriterID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardReplyReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardReplyReq.getAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardReplyReq.AttachedFile> attachedFile$2List = boardReplyReq.getAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == attachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardReplyReq.getAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var attachedFile$2List is null but the value referenced by the array size[boardReplyReq.getAttachedFileCnt()][").append(boardReplyReq.getAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int attachedFile$2ListSize = attachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardReplyReq.getAttachedFileCnt() != attachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var attachedFile$2ListSize[").append(attachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardReplyReq.getAttachedFileCnt()][").append(boardReplyReq.getAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object attachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
				Object attachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
				BoardReplyReq.AttachedFile attachedFile$2 = attachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, attachedFile$2.getAttachedFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}