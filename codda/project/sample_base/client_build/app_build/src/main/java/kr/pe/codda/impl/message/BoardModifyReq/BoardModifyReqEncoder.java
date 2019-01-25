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

package kr.pe.codda.impl.message.BoardModifyReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardModifyReq message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardModifyReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardModifyReq boardModifyReq = (BoardModifyReq)messageObj;
		encodeBody(boardModifyReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardModifyReq boardModifyReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardModifyReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "requestedUserID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardModifyReq.getRequestedUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardModifyReq.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardModifyReq.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardModifyReq.getSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "content"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardModifyReq.getContent() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardModifyReq.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nextAttachedFileSeq"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardModifyReq.getNextAttachedFileSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "oldAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardModifyReq.getOldAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardModifyReq.OldAttachedFile> oldAttachedFile$2List = boardModifyReq.getOldAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == oldAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardModifyReq.getOldAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var oldAttachedFile$2List is null but the value referenced by the array size[boardModifyReq.getOldAttachedFileCnt()][").append(boardModifyReq.getOldAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int oldAttachedFile$2ListSize = oldAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardModifyReq.getOldAttachedFileCnt() != oldAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var oldAttachedFile$2ListSize[").append(oldAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardModifyReq.getOldAttachedFileCnt()][").append(boardModifyReq.getOldAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object oldAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "oldAttachedFile", oldAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < oldAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("OldAttachedFile").append("[").append(i2).append("]").toString());
				Object oldAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), oldAttachedFile$2ArrayMiddleObject, i2);
				BoardModifyReq.OldAttachedFile oldAttachedFile$2 = oldAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, oldAttachedFile$2.getAttachedFileSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, oldAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "newAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardModifyReq.getNewAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardModifyReq.NewAttachedFile> newAttachedFile$2List = boardModifyReq.getNewAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == newAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardModifyReq.getNewAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var newAttachedFile$2List is null but the value referenced by the array size[boardModifyReq.getNewAttachedFileCnt()][").append(boardModifyReq.getNewAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int newAttachedFile$2ListSize = newAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardModifyReq.getNewAttachedFileCnt() != newAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var newAttachedFile$2ListSize[").append(newAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardModifyReq.getNewAttachedFileCnt()][").append(boardModifyReq.getNewAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object newAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "newAttachedFile", newAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < newAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachedFile").append("[").append(i2).append("]").toString());
				Object newAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), newAttachedFile$2ArrayMiddleObject, i2);
				BoardModifyReq.NewAttachedFile newAttachedFile$2 = newAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, newAttachedFile$2.getAttachedFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, newAttachedFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSize"
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, newAttachedFile$2.getAttachedFileSize() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, newAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}