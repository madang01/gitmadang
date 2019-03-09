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

package kr.pe.codda.impl.message.BoardGroupDetailRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * BoardGroupDetailRes message encoder
 * @author Won Jonghoon
 *
 */
public final class BoardGroupDetailResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardGroupDetailRes boardGroupDetailRes = (BoardGroupDetailRes)messageObj;
		encodeBody(boardGroupDetailRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardGroupDetailRes boardGroupDetailRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardGroupDetailRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardID"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardGroupDetailRes.getBoardID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootBoardNo"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardGroupDetailRes.getRootBoardNo() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootViewCount"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardGroupDetailRes.getRootViewCount() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootBoardSate"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootBoardSate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootNickname"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootVotes"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardGroupDetailRes.getRootVotes() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootSubject"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootSubject() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootContents"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootContents() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootWriterID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootWriterID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootRegisteredDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardGroupDetailRes.getRootRegisteredDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootLastModifierID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootLastModifierID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootLastModifierNickName"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardGroupDetailRes.getRootLastModifierNickName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootLastModifiedDate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, boardGroupDetailRes.getRootLastModifiedDate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootNextAttachedFileSeq"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, boardGroupDetailRes.getRootNextAttachedFileSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootAttachedFileCnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardGroupDetailRes.getRootAttachedFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardGroupDetailRes.RootAttachedFile> rootAttachedFile$2List = boardGroupDetailRes.getRootAttachedFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == rootAttachedFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardGroupDetailRes.getRootAttachedFileCnt()) {
				String errorMessage = new StringBuilder("the var rootAttachedFile$2List is null but the value referenced by the array size[boardGroupDetailRes.getRootAttachedFileCnt()][").append(boardGroupDetailRes.getRootAttachedFileCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int rootAttachedFile$2ListSize = rootAttachedFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardGroupDetailRes.getRootAttachedFileCnt() != rootAttachedFile$2ListSize) {
				String errorMessage = new StringBuilder("the var rootAttachedFile$2ListSize[").append(rootAttachedFile$2ListSize).append("] is not same to the value referenced by the array size[boardGroupDetailRes.getRootAttachedFileCnt()][").append(boardGroupDetailRes.getRootAttachedFileCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object rootAttachedFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "rootAttachedFile", rootAttachedFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < rootAttachedFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("RootAttachedFile").append("[").append(i2).append("]").toString());
				Object rootAttachedFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), rootAttachedFile$2ArrayMiddleObject, i2);
				BoardGroupDetailRes.RootAttachedFile rootAttachedFile$2 = rootAttachedFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, rootAttachedFile$2.getAttachedFileSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, rootAttachedFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileName"
					, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, rootAttachedFile$2.getAttachedFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, rootAttachedFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileSize"
					, kr.pe.codda.common.type.SingleItemType.LONG // itemType
					, rootAttachedFile$2.getAttachedFileSize() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, rootAttachedFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, boardGroupDetailRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardGroupDetailRes.ChildBoard> childBoard$2List = boardGroupDetailRes.getChildBoardList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == childBoard$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardGroupDetailRes.getCnt()) {
				String errorMessage = new StringBuilder("the var childBoard$2List is null but the value referenced by the array size[boardGroupDetailRes.getCnt()][").append(boardGroupDetailRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int childBoard$2ListSize = childBoard$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardGroupDetailRes.getCnt() != childBoard$2ListSize) {
				String errorMessage = new StringBuilder("the var childBoard$2ListSize[").append(childBoard$2ListSize).append("] is not same to the value referenced by the array size[boardGroupDetailRes.getCnt()][").append(boardGroupDetailRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object childBoard$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "childBoard", childBoard$2ListSize, middleWritableObject);
			for (int i2=0; i2 < childBoard$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildBoard").append("[").append(i2).append("]").toString());
				Object childBoard$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), childBoard$2ArrayMiddleObject, i2);
				BoardGroupDetailRes.ChildBoard childBoard$2 = childBoard$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, childBoard$2.getBoardNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, childBoard$2.getGroupNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "groupSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
					, childBoard$2.getGroupSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, childBoard$2.getParentNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, childBoard$2.getDepth() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "writerID"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childBoard$2.getWriterID() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "writerNickname"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childBoard$2.getWriterNickname() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "viewCount"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, childBoard$2.getViewCount() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "boardSate"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childBoard$2.getBoardSate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "registeredDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, childBoard$2.getRegisteredDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "votes"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, childBoard$2.getVotes() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subject"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, childBoard$2.getSubject() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "lastModifiedDate"
					, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
					, childBoard$2.getLastModifiedDate() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachedFileCnt"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, childBoard$2.getAttachedFileCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, childBoard$2MiddleWritableObject);

				java.util.List<BoardGroupDetailRes.ChildBoard.AttachedFile> attachedFile$4List = childBoard$2.getAttachedFileList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == attachedFile$4List) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != childBoard$2.getAttachedFileCnt()) {
						String errorMessage = new StringBuilder("the var attachedFile$4List is null but the value referenced by the array size[childBoard$2.getAttachedFileCnt()][").append(childBoard$2.getAttachedFileCnt()).append("] is not zero").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int attachedFile$4ListSize = attachedFile$4List.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (childBoard$2.getAttachedFileCnt() != attachedFile$4ListSize) {
						String errorMessage = new StringBuilder("the var attachedFile$4ListSize[").append(attachedFile$4ListSize).append("] is not same to the value referenced by the array size[childBoard$2.getAttachedFileCnt()][").append(childBoard$2.getAttachedFileCnt()).append("]").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}

					Object attachedFile$4ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "attachedFile", attachedFile$4ListSize, childBoard$2MiddleWritableObject);
					for (int i4=0; i4 < attachedFile$4ListSize; i4++) {
						pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("AttachedFile").append("[").append(i4).append("]").toString());
						Object attachedFile$4MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), attachedFile$4ArrayMiddleObject, i4);
						BoardGroupDetailRes.ChildBoard.AttachedFile attachedFile$4 = attachedFile$4List.get(i4);

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