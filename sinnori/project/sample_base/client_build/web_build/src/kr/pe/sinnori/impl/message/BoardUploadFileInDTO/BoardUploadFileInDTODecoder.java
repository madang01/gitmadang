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
package kr.pe.sinnori.impl.message.BoardUploadFileInDTO;

import java.nio.charset.Charset;
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

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardUploadFileInDTO 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardUploadFileInDTO 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		BoardUploadFileInDTO boardUploadFileInDTO = new BoardUploadFileInDTO();
		String sigleItemPath0 = "BoardUploadFileInDTO";

		boardUploadFileInDTO.setUserId((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "userId" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileInDTO.setIp((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "ip" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileInDTO.setAttachId((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "attachId" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileInDTO.setSelectedOldAttachFileCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "selectedOldAttachFileCnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int selectedOldAttachFileListSize = boardUploadFileInDTO.getSelectedOldAttachFileCnt();
		Object selectedOldAttachFileMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "selectedOldAttachFile", selectedOldAttachFileListSize, middleReadObj);
		java.util.List<BoardUploadFileInDTO.SelectedOldAttachFile> selectedOldAttachFileList = new java.util.ArrayList<BoardUploadFileInDTO.SelectedOldAttachFile>();
		for (int i=0; i < selectedOldAttachFileListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("SelectedOldAttachFile[").append(i).append("]").toString();
			Object selectedOldAttachFileMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, selectedOldAttachFileMiddleReadArray, i);
			BoardUploadFileInDTO.SelectedOldAttachFile selectedOldAttachFile = new BoardUploadFileInDTO.SelectedOldAttachFile();
			selectedOldAttachFileList.add(selectedOldAttachFile);

			selectedOldAttachFile.setAttachSeq((Short)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "attachSeq" // itemName
			, 1 // itemTypeID
			, "unsigned byte" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, selectedOldAttachFileMiddleReadObj));
		}
		boardUploadFileInDTO.setSelectedOldAttachFileList(selectedOldAttachFileList);

		boardUploadFileInDTO.setNewAttachFileCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "newAttachFileCnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int newAttachFileListSize = boardUploadFileInDTO.getNewAttachFileCnt();
		Object newAttachFileMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "newAttachFile", newAttachFileListSize, middleReadObj);
		java.util.List<BoardUploadFileInDTO.NewAttachFile> newAttachFileList = new java.util.ArrayList<BoardUploadFileInDTO.NewAttachFile>();
		for (int i=0; i < newAttachFileListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("NewAttachFile[").append(i).append("]").toString();
			Object newAttachFileMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, newAttachFileMiddleReadArray, i);
			BoardUploadFileInDTO.NewAttachFile newAttachFile = new BoardUploadFileInDTO.NewAttachFile();
			newAttachFileList.add(newAttachFile);

			newAttachFile.setAttachFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "attachFileName" // itemName
			, 8 // itemTypeID
			, "us pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, newAttachFileMiddleReadObj));

			newAttachFile.setSystemFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "systemFileName" // itemName
			, 8 // itemTypeID
			, "us pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, newAttachFileMiddleReadObj));
		}
		boardUploadFileInDTO.setNewAttachFileList(newAttachFileList);
		return boardUploadFileInDTO;
	}
}