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
import java.util.LinkedList;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardUploadFileInDTO 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileInDTOEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardUploadFileInDTO)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardUploadFileInDTO 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardUploadFileInDTO boardUploadFileInDTO = (BoardUploadFileInDTO) messageObj;
		encodeBody(boardUploadFileInDTO, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardUploadFileInDTO 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardUploadFileInDTO BoardUploadFileInDTO 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardUploadFileInDTO boardUploadFileInDTO, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardUploadFileInDTOSingleItemPath = "BoardUploadFileInDTO";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardUploadFileInDTOSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "userId"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardUploadFileInDTO.getUserId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "ip"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardUploadFileInDTO.getIp() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "attachId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardUploadFileInDTO.getAttachId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "selectedOldAttachFileCnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardUploadFileInDTO.getSelectedOldAttachFileCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<BoardUploadFileInDTO.SelectedOldAttachFile> selectedOldAttachFileList = boardUploadFileInDTO.getSelectedOldAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == selectedOldAttachFileList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileInDTO.getSelectedOldAttachFileCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(boardUploadFileInDTO.getSelectedOldAttachFileCnt())
				.append("] is not zero but ")
				.append(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("selectedOldAttachFileList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int selectedOldAttachFileListSize = selectedOldAttachFileList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (selectedOldAttachFileListSize != boardUploadFileInDTO.getSelectedOldAttachFileCnt()) {
				String errorMessage = new StringBuilder(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("selectedOldAttachFileList.length[")
				.append(selectedOldAttachFileListSize)
				.append("] is not same to ")
				.append(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("selectedOldAttachFileCnt[")
				.append(boardUploadFileInDTO.getSelectedOldAttachFileCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object selectedOldAttachFileMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "selectedOldAttachFile", selectedOldAttachFileListSize, middleWriteObj);
			for (int i=0; i < selectedOldAttachFileListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("SelectedOldAttachFile").append("[").append(i).append("]").toString());
				String selectedOldAttachFileSingleItemPath = singleItemPathStatck.getLast();
				Object selectedOldAttachFileMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(selectedOldAttachFileSingleItemPath, selectedOldAttachFileMiddleWriteArray, i);
				BoardUploadFileInDTO.SelectedOldAttachFile selectedOldAttachFile = selectedOldAttachFileList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(selectedOldAttachFileSingleItemPath, "attachSeq"
							, 1 // itemTypeID
							, "unsigned byte" // itemTypeName
							, selectedOldAttachFile.getAttachSeq() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, selectedOldAttachFileMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "newAttachFileCnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardUploadFileInDTO.getNewAttachFileCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<BoardUploadFileInDTO.NewAttachFile> newAttachFileList = boardUploadFileInDTO.getNewAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == newAttachFileList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileInDTO.getNewAttachFileCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(boardUploadFileInDTO.getNewAttachFileCnt())
				.append("] is not zero but ")
				.append(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("newAttachFileList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int newAttachFileListSize = newAttachFileList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (newAttachFileListSize != boardUploadFileInDTO.getNewAttachFileCnt()) {
				String errorMessage = new StringBuilder(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("newAttachFileList.length[")
				.append(newAttachFileListSize)
				.append("] is not same to ")
				.append(boardUploadFileInDTOSingleItemPath)
				.append(".")
				.append("newAttachFileCnt[")
				.append(boardUploadFileInDTO.getNewAttachFileCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object newAttachFileMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(boardUploadFileInDTOSingleItemPath, "newAttachFile", newAttachFileListSize, middleWriteObj);
			for (int i=0; i < newAttachFileListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("NewAttachFile").append("[").append(i).append("]").toString());
				String newAttachFileSingleItemPath = singleItemPathStatck.getLast();
				Object newAttachFileMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(newAttachFileSingleItemPath, newAttachFileMiddleWriteArray, i);
				BoardUploadFileInDTO.NewAttachFile newAttachFile = newAttachFileList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(newAttachFileSingleItemPath, "attachFileName"
							, 8 // itemTypeID
							, "us pascal string" // itemTypeName
							, newAttachFile.getAttachFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, newAttachFileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(newAttachFileSingleItemPath, "systemFileName"
							, 8 // itemTypeID
							, "us pascal string" // itemTypeName
							, newAttachFile.getSystemFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, newAttachFileMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}