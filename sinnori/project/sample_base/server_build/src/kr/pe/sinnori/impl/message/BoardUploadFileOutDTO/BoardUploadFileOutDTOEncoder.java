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
package kr.pe.sinnori.impl.message.BoardUploadFileOutDTO;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardUploadFileOutDTO 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileOutDTOEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardUploadFileOutDTO)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardUploadFileOutDTO 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardUploadFileOutDTO boardUploadFileOutDTO = (BoardUploadFileOutDTO) messageObj;
		encodeBody(boardUploadFileOutDTO, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardUploadFileOutDTO 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardUploadFileOutDTO BoardUploadFileOutDTO 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardUploadFileOutDTO boardUploadFileOutDTO, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardUploadFileOutDTOSingleItemPath = "BoardUploadFileOutDTO";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardUploadFileOutDTOSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "attachId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardUploadFileOutDTO.getAttachId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "ownerId"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardUploadFileOutDTO.getOwnerId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "ip"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardUploadFileOutDTO.getIp() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "registerDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardUploadFileOutDTO.getRegisterDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "modifiedDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardUploadFileOutDTO.getModifiedDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "attachFileCnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardUploadFileOutDTO.getAttachFileCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<BoardUploadFileOutDTO.AttachFile> attachFileList = boardUploadFileOutDTO.getAttachFileList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == attachFileList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardUploadFileOutDTO.getAttachFileCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(boardUploadFileOutDTO.getAttachFileCnt())
				.append("] is not zero but ")
				.append(boardUploadFileOutDTOSingleItemPath)
				.append(".")
				.append("attachFileList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int attachFileListSize = attachFileList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (attachFileListSize != boardUploadFileOutDTO.getAttachFileCnt()) {
				String errorMessage = new StringBuilder(boardUploadFileOutDTOSingleItemPath)
				.append(".")
				.append("attachFileList.length[")
				.append(attachFileListSize)
				.append("] is not same to ")
				.append(boardUploadFileOutDTOSingleItemPath)
				.append(".")
				.append("attachFileCnt[")
				.append(boardUploadFileOutDTO.getAttachFileCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object attachFileMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(boardUploadFileOutDTOSingleItemPath, "attachFile", attachFileListSize, middleWriteObj);
			for (int i=0; i < attachFileListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("AttachFile").append("[").append(i).append("]").toString());
				String attachFileSingleItemPath = singleItemPathStatck.getLast();
				Object attachFileMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(attachFileSingleItemPath, attachFileMiddleWriteArray, i);
				BoardUploadFileOutDTO.AttachFile attachFile = attachFileList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(attachFileSingleItemPath, "attachSeq"
							, 1 // itemTypeID
							, "unsigned byte" // itemTypeName
							, attachFile.getAttachSeq() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, attachFileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(attachFileSingleItemPath, "attachFileName"
							, 8 // itemTypeID
							, "us pascal string" // itemTypeName
							, attachFile.getAttachFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, attachFileMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(attachFileSingleItemPath, "systemFileName"
							, 8 // itemTypeID
							, "us pascal string" // itemTypeName
							, attachFile.getSystemFileName() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, attachFileMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}