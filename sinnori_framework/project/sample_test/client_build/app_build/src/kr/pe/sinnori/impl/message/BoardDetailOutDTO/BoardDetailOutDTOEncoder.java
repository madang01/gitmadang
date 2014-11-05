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
package kr.pe.sinnori.impl.message.BoardDetailOutDTO;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardDetailOutDTO 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailOutDTOEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardDetailOutDTO)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardDetailOutDTO 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardDetailOutDTO boardDetailOutDTO = (BoardDetailOutDTO) messageObj;
		encodeBody(boardDetailOutDTO, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardDetailOutDTO 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardDetailOutDTO BoardDetailOutDTO 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardDetailOutDTO boardDetailOutDTO, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardDetailOutDTOSingleItemPath = "BoardDetailOutDTO";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardDetailOutDTOSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "boardNo"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardDetailOutDTO.getBoardNo() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "boardId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardDetailOutDTO.getBoardId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "groupNo"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardDetailOutDTO.getGroupNo() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "groupSeq"
					, 3 // itemTypeID
					, "unsigned short" // itemTypeName
					, boardDetailOutDTO.getGroupSeq() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "parentNo"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardDetailOutDTO.getParentNo() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "depth"
					, 1 // itemTypeID
					, "unsigned byte" // itemTypeName
					, boardDetailOutDTO.getDepth() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "subject"
					, 8 // itemTypeID
					, "us pascal string" // itemTypeName
					, boardDetailOutDTO.getSubject() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "content"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, boardDetailOutDTO.getContent() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "writerId"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailOutDTO.getWriterId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "nickname"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailOutDTO.getNickname() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "viewCount"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailOutDTO.getViewCount() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "votes"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailOutDTO.getVotes() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "deleteFlag"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailOutDTO.getDeleteFlag() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "ip"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailOutDTO.getIp() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "registerDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardDetailOutDTO.getRegisterDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "modifiedDate"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, boardDetailOutDTO.getModifiedDate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "memberGubunName"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailOutDTO.getMemberGubunName() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailOutDTOSingleItemPath, "memberState"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, boardDetailOutDTO.getMemberState() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}