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
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * BoardDetailOutDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailOutDTODecoder extends MessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardDetailOutDTO 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardDetailOutDTO 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		BoardDetailOutDTO boardDetailOutDTO = new BoardDetailOutDTO();
		String sigleItemPath0 = "BoardDetailOutDTO";

		boardDetailOutDTO.setBoardNo((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "boardNo" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setBoardId((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "boardId" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setGroupNo((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "groupNo" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setGroupSeq((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "groupSeq" // itemName
		, 3 // itemTypeID
		, "unsigned short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setParentNo((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "parentNo" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setDepth((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "depth" // itemName
		, 1 // itemTypeID
		, "unsigned byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setSubject((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "subject" // itemName
		, 8 // itemTypeID
		, "us pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setContent((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "content" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setWriterId((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "writerId" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setNickname((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "nickname" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setViewCount((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "viewCount" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setVotes((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "votes" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setDeleteFlag((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "deleteFlag" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setIp((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "ip" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setRegisterDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "registerDate" // itemName
		, 16 // itemTypeID
		, "java sql timestamp" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "modifiedDate" // itemName
		, 16 // itemTypeID
		, "java sql timestamp" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setMemberGubunName((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "memberGubunName" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardDetailOutDTO.setMemberState((Byte)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "memberState" // itemName
		, 0 // itemTypeID
		, "byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));
		return boardDetailOutDTO;
	}
}