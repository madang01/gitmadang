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

package kr.pe.codda.impl.message.BoardModifyRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardModifyRes message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardModifyResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardModifyRes boardModifyRes = (BoardModifyRes)messageObj;
		encodeBody(boardModifyRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardModifyRes boardModifyRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardModifyRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardModifyRes.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardModifyRes.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "deletedAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardModifyRes.getDeletedAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardModifyRes.DeletedAttachedFile> deletedAttachedFile$2List = boardModifyRes.getDeletedAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == deletedAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardModifyRes.getDeletedAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var deletedAttachedFile$2List is null but the value referenced by the array size[boardModifyRes.getDeletedAttachedFileCnt()][").append(boardModifyRes.getDeletedAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int deletedAttachedFile$2ListSize = deletedAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardModifyRes.getDeletedAttachedFileCnt() != deletedAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var deletedAttachedFile$2ListSize[").append(deletedAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardModifyRes.getDeletedAttachedFileCnt()][").append(boardModifyRes.getDeletedAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object deletedAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "deletedAttachedFile", deletedAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < deletedAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("DeletedAttachedFile").append("[").append(i2).append("]").toString());
				Object deletedAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), deletedAttachedFile$2ArrayMiddleObject, i2);
				BoardModifyRes.DeletedAttachedFile deletedAttachedFile$2 = deletedAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, deletedAttachedFile$2.getAttachedFileSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, deletedAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}