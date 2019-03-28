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

package kr.pe.codda.impl.message.BoardDetailRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardDetailRes message encoder
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


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardDetailRes.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardName"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getBoardName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardListType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardDetailRes.getBoardListType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPolicyType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardDetailRes.getBoardReplyPolicyType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardReplyPermssionType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardDetailRes.getBoardReplyPermssionType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getGroupNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupSeq"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, boardDetailRes.getGroupSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDetailRes.getParentNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardDetailRes.getDepth() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "viewCount"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getViewCount() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardSate"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, boardDetailRes.getBoardSate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "votes"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getVotes() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardDetailRes.getSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "contents"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardDetailRes.getContents() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstWriterID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getFirstWriterID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstWriterNickname"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getFirstWriterNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstRegisteredDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardDetailRes.getFirstRegisteredDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifierID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getLastModifierID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifierNickName"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDetailRes.getLastModifierNickName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifiedDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardDetailRes.getLastModifiedDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nextAttachedFileSeq"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardDetailRes.getNextAttachedFileSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "isBoardPassword"
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, boardDetailRes.getIsBoardPassword() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardDetailRes.AttachedFile> attachedFile$2List = boardDetailRes.getAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == attachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardDetailRes.getAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var attachedFile$2List is null but the value referenced by the array size[boardDetailRes.getAttachedFileCnt()][").append(boardDetailRes.getAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int attachedFile$2ListSize = attachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardDetailRes.getAttachedFileCnt() != attachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var attachedFile$2ListSize[").append(attachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardDetailRes.getAttachedFileCnt()][").append(boardDetailRes.getAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object attachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < attachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i2).append("]").toString());
				Object attachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachedFile$2ArrayMiddleObject, i2);
				BoardDetailRes.AttachedFile attachedFile$2 = attachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, attachedFile$2.getAttachedFileSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, attachedFile$2.getAttachedFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSize"
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, attachedFile$2.getAttachedFileSize() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, attachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "childNodeCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardDetailRes.getChildNodeCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardDetailRes.ChildNode> childNode$2List = boardDetailRes.getChildNodeList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == childNode$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardDetailRes.getChildNodeCnt()) {
				String errorMessage = new StringBuilder("the var childNode$2List is null but the value referenced by the array size[boardDetailRes.getChildNodeCnt()][").append(boardDetailRes.getChildNodeCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int childNode$2ListSize = childNode$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardDetailRes.getChildNodeCnt() != childNode$2ListSize) {
				String errorMessage = new StringBuilder("the var childNode$2ListSize[").append(childNode$2ListSize).append("] is not same to the value referenced by the array size[boardDetailRes.getChildNodeCnt()][").append(boardDetailRes.getChildNodeCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object childNode$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "childNode", childNode$2ListSize, middleWritableObject);
			for (int i2=0; i2 < childNode$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildNode").append("[").append(i2).append("]").toString());
				Object childNode$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), childNode$2ArrayMiddleObject, i2);
				BoardDetailRes.ChildNode childNode$2 = childNode$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, childNode$2.getBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
					, childNode$2.getGroupSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, childNode$2.getParentNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, childNode$2.getDepth() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "contents"
					, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
					, childNode$2.getContents() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "votes"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, childNode$2.getVotes() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardSate"
					, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
					, childNode$2.getBoardSate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstWriterID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childNode$2.getFirstWriterID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstWriterNickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childNode$2.getFirstWriterNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "firstRegisteredDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, childNode$2.getFirstRegisteredDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifierID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childNode$2.getLastModifierID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifierNickName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childNode$2.getLastModifierNickName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, childNode$2.getLastModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nextAttachedFileSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, childNode$2.getNextAttachedFileSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "isBoardPassword"
					, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
					, childNode$2.getIsBoardPassword() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileCnt"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, childNode$2.getAttachedFileCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childNode$2MiddleWritableObject);

				java.util.List<BoardDetailRes.ChildNode.AttachedFile> attachedFile$4List = childNode$2.getAttachedFileList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == attachedFile$4List) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != childNode$2.getAttachedFileCnt()) {
						String errorMessage = new StringBuilder("the var attachedFile$4List is null but the value referenced by the array size[childNode$2.getAttachedFileCnt()][").append(childNode$2.getAttachedFileCnt()).append("] is not zero").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int attachedFile$4ListSize = attachedFile$4List.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (childNode$2.getAttachedFileCnt() != attachedFile$4ListSize) {
						String errorMessage = new StringBuilder("the var attachedFile$4ListSize[").append(attachedFile$4ListSize).append("] is not same to the value referenced by the array size[childNode$2.getAttachedFileCnt()][").append(childNode$2.getAttachedFileCnt()).append("]").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}

					Object attachedFile$4ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$4ListSize, childNode$2MiddleWritableObject);
					for (int i4=0; i4 < attachedFile$4ListSize; i4++) {
						pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i4).append("]").toString());
						Object attachedFile$4MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachedFile$4ArrayMiddleObject, i4);
						BoardDetailRes.ChildNode.AttachedFile attachedFile$4 = attachedFile$4List.get(i4);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
							, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
							, attachedFile$4.getAttachedFileSeq() // itemValue
							, -1 // itemSize
							, null // nativeItemCharset
							, attachedFile$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
							, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
							, attachedFile$4.getAttachedFileName() // itemValue
							, -1 // itemSize
							, null // nativeItemCharset
							, attachedFile$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSize"
							, kr.pe.codda.common.type.SingleItemType.LONG // itemType
							, attachedFile$4.getAttachedFileSize() // itemValue
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