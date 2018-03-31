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
package kr.pe.sinnori.impl.message.BoardDetailRes;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardDetailRes 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardDetailRes boardDetailRes = (BoardDetailRes)messageObj;
		encodeBody(boardDetailRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardDetailRes boardDetailRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardDetailRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardId"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getBoardId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupNo"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getGroupNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupSeq"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, boardDetailRes.getGroupSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getParentNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardDetailRes.getDepth() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardDetailRes.getSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "content"
			, kr.pe.sinnori.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardDetailRes.getContent() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "writerId"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getWriterId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nickname"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "viewCount"
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getViewCount() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "votes"
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getVotes() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "deleteFlag"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getDeleteFlag() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registerDate"
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardDetailRes.getRegisterDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "modifiedDate"
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardDetailRes.getModifiedDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachId"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getAttachId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachFileCnt"
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getAttachFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardDetailRes.AttachFile> attachFile$2List = boardDetailRes.getAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == attachFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardDetailRes.getAttachFileCnt()) {
				String errorMessage = new StringBuilder("the var attachFile$2List is null but the value referenced by the array size[boardDetailRes.getAttachFileCnt()][").append(boardDetailRes.getAttachFileCnt()).append("] is not zero").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int attachFile$2ListSize = attachFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardDetailRes.getAttachFileCnt() != attachFile$2ListSize) {
				String errorMessage = new StringBuilder("the var attachFile$2ListSize[").append(attachFile$2ListSize).append("] is not same to the value referenced by the array size[boardDetailRes.getAttachFileCnt()][").append(boardDetailRes.getAttachFileCnt()).append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object attachFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachFile", attachFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < attachFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachFile").append("[").append(i2).append("]").toString());
				Object attachFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachFile$2ArrayMiddleObject, i2);
				BoardDetailRes.AttachFile attachFile$2 = attachFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachSeq"
					, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, attachFile$2.getAttachSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachFileName"
					, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, attachFile$2.getAttachFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberGubunName"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getMemberGubunName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberState"
			, kr.pe.sinnori.common.type.SingleItemType.BYTE // itemType
			, boardDetailRes.getMemberState() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}