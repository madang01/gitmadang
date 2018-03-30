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
package kr.pe.sinnori.impl.message.BoardUploadFileReq;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardUploadFileInDTO 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileInDTOEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardUploadFileReq boardUploadFileInDTO = (BoardUploadFileReq)messageObj;
		encodeBody(boardUploadFileInDTO, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardUploadFileReq boardUploadFileInDTO, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardUploadFileInDTO");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "userId"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardUploadFileInDTO.getUserId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ip"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardUploadFileInDTO.getIp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachId"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardUploadFileInDTO.getAttachId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "selectedOldAttachFileCnt"
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, boardUploadFileInDTO.getSelectedOldAttachFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardUploadFileReq.SelectedOldAttachFile> selectedOldAttachFile$2List = boardUploadFileInDTO.getSelectedOldAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == selectedOldAttachFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileInDTO.getSelectedOldAttachFileCnt()) {
				String errorMessage = new StringBuilder("the var selectedOldAttachFile$2List is null but the value referenced by the array size[boardUploadFileInDTO.getSelectedOldAttachFileCnt()][").append(boardUploadFileInDTO.getSelectedOldAttachFileCnt()).append("] is not zero").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int selectedOldAttachFile$2ListSize = selectedOldAttachFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardUploadFileInDTO.getSelectedOldAttachFileCnt() != selectedOldAttachFile$2ListSize) {
				String errorMessage = new StringBuilder("the var selectedOldAttachFile$2ListSize[").append(selectedOldAttachFile$2ListSize).append("] is not same to the value referenced by the array size[boardUploadFileInDTO.getSelectedOldAttachFileCnt()][").append(boardUploadFileInDTO.getSelectedOldAttachFileCnt()).append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object selectedOldAttachFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "selectedOldAttachFile", selectedOldAttachFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < selectedOldAttachFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SelectedOldAttachFile").append("[").append(i2).append("]").toString());
				Object selectedOldAttachFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), selectedOldAttachFile$2ArrayMiddleObject, i2);
				BoardUploadFileReq.SelectedOldAttachFile selectedOldAttachFile$2 = selectedOldAttachFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachSeq"
					, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, selectedOldAttachFile$2.getAttachSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, selectedOldAttachFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "newAttachFileCnt"
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, boardUploadFileInDTO.getNewAttachFileCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<BoardUploadFileReq.NewAttachFile> newAttachFile$2List = boardUploadFileInDTO.getNewAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == newAttachFile$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileInDTO.getNewAttachFileCnt()) {
				String errorMessage = new StringBuilder("the var newAttachFile$2List is null but the value referenced by the array size[boardUploadFileInDTO.getNewAttachFileCnt()][").append(boardUploadFileInDTO.getNewAttachFileCnt()).append("] is not zero").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int newAttachFile$2ListSize = newAttachFile$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardUploadFileInDTO.getNewAttachFileCnt() != newAttachFile$2ListSize) {
				String errorMessage = new StringBuilder("the var newAttachFile$2ListSize[").append(newAttachFile$2ListSize).append("] is not same to the value referenced by the array size[boardUploadFileInDTO.getNewAttachFileCnt()][").append(boardUploadFileInDTO.getNewAttachFileCnt()).append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object newAttachFile$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "newAttachFile", newAttachFile$2ListSize, middleWritableObject);
			for (int i2=0; i2 < newAttachFile$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachFile").append("[").append(i2).append("]").toString());
				Object newAttachFile$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), newAttachFile$2ArrayMiddleObject, i2);
				BoardUploadFileReq.NewAttachFile newAttachFile$2 = newAttachFile$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachFileName"
					, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, newAttachFile$2.getAttachFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, newAttachFile$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "systemFileName"
					, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
					, newAttachFile$2.getSystemFileName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, newAttachFile$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}