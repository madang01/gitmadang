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

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardUploadFileInDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileInDTODecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardUploadFileReq boardUploadFileInDTO = new BoardUploadFileReq();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardUploadFileInDTO");

		boardUploadFileInDTO.setUserId((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "userId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileInDTO.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileInDTO.setAttachId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardUploadFileInDTO.setSelectedOldAttachFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "selectedOldAttachFileCnt" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int selectedOldAttachFile$2ListSize = boardUploadFileInDTO.getSelectedOldAttachFileCnt();
		Object selectedOldAttachFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "selectedOldAttachFile", selectedOldAttachFile$2ListSize, middleReadableObject);
		java.util.List<BoardUploadFileReq.SelectedOldAttachFile> selectedOldAttachFile$2List = new java.util.ArrayList<BoardUploadFileReq.SelectedOldAttachFile>();
		for (int i2=0; i2 < selectedOldAttachFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SelectedOldAttachFile").append("[").append(i2).append("]").toString());
			Object selectedOldAttachFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), selectedOldAttachFile$2ArrayMiddleObject, i2);
			BoardUploadFileReq.SelectedOldAttachFile selectedOldAttachFile$2 = new BoardUploadFileReq.SelectedOldAttachFile();
			selectedOldAttachFile$2List.add(selectedOldAttachFile$2);

			selectedOldAttachFile$2.setAttachSeq((Short)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachSeq" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, selectedOldAttachFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardUploadFileInDTO.setSelectedOldAttachFileList(selectedOldAttachFile$2List);

		boardUploadFileInDTO.setNewAttachFileCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "newAttachFileCnt" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int newAttachFile$2ListSize = boardUploadFileInDTO.getNewAttachFileCnt();
		Object newAttachFile$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "newAttachFile", newAttachFile$2ListSize, middleReadableObject);
		java.util.List<BoardUploadFileReq.NewAttachFile> newAttachFile$2List = new java.util.ArrayList<BoardUploadFileReq.NewAttachFile>();
		for (int i2=0; i2 < newAttachFile$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("NewAttachFile").append("[").append(i2).append("]").toString());
			Object newAttachFile$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), newAttachFile$2ArrayMiddleObject, i2);
			BoardUploadFileReq.NewAttachFile newAttachFile$2 = new BoardUploadFileReq.NewAttachFile();
			newAttachFile$2List.add(newAttachFile$2);

			newAttachFile$2.setAttachFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "attachFileName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachFile$2MiddleWritableObject));

			newAttachFile$2.setSystemFileName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "systemFileName" // itemName
				, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, newAttachFile$2MiddleWritableObject));

			pathStack.pop();
		}

		boardUploadFileInDTO.setNewAttachFileList(newAttachFile$2List);

		pathStack.pop();

		return boardUploadFileInDTO;
	}
}