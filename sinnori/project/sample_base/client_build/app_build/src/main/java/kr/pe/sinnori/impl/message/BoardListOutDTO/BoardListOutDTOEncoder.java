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
package kr.pe.sinnori.impl.message.BoardListOutDTO;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardListOutDTO 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardListOutDTOEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardListOutDTO)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardListOutDTO 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardListOutDTO boardListOutDTO = (BoardListOutDTO) messageObj;
		encodeBody(boardListOutDTO, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardListOutDTO 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardListOutDTO BoardListOutDTO 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardListOutDTO boardListOutDTO, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardListOutDTOSingleItemPath = "BoardListOutDTO";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardListOutDTOSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardListOutDTOSingleItemPath, "boardId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardListOutDTO.getBoardId() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardListOutDTOSingleItemPath, "startNo"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardListOutDTO.getStartNo() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardListOutDTOSingleItemPath, "pageSize"
					, 3 // itemTypeID
					, "unsigned short" // itemTypeName
					, boardListOutDTO.getPageSize() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardListOutDTOSingleItemPath, "total"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardListOutDTO.getTotal() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardListOutDTOSingleItemPath, "cnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardListOutDTO.getCnt() // itemValue
					, -1 // itemSize
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<BoardListOutDTO.Board> boardList = boardListOutDTO.getBoardList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == boardList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardListOutDTO.getCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(boardListOutDTO.getCnt())
				.append("] is not zero but ")
				.append(boardListOutDTOSingleItemPath)
				.append(".")
				.append("boardList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int boardListSize = boardList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardListSize != boardListOutDTO.getCnt()) {
				String errorMessage = new StringBuilder(boardListOutDTOSingleItemPath)
				.append(".")
				.append("boardList.length[")
				.append(boardListSize)
				.append("] is not same to ")
				.append(boardListOutDTOSingleItemPath)
				.append(".")
				.append("cnt[")
				.append(boardListOutDTO.getCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object boardMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(boardListOutDTOSingleItemPath, "board", boardListSize, middleWriteObj);
			for (int i=0; i < boardListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Board").append("[").append(i).append("]").toString());
				String boardSingleItemPath = singleItemPathStatck.getLast();
				Object boardMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(boardSingleItemPath, boardMiddleWriteArray, i);
				BoardListOutDTO.Board board = boardList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "boardNo"
							, 5 // itemTypeID
							, "unsigned integer" // itemTypeName
							, board.getBoardNo() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "groupNo"
							, 5 // itemTypeID
							, "unsigned integer" // itemTypeName
							, board.getGroupNo() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "groupSeq"
							, 3 // itemTypeID
							, "unsigned short" // itemTypeName
							, board.getGroupSeq() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "parentNo"
							, 5 // itemTypeID
							, "unsigned integer" // itemTypeName
							, board.getParentNo() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "depth"
							, 1 // itemTypeID
							, "unsigned byte" // itemTypeName
							, board.getDepth() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "subject"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getSubject() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "writerId"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getWriterId() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "nickname"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getNickname() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "viewCount"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getViewCount() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "votes"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getVotes() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "deleteFlag"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getDeleteFlag() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "registerDate"
							, 16 // itemTypeID
							, "java sql timestamp" // itemTypeName
							, board.getRegisterDate() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "modifiedDate"
							, 16 // itemTypeID
							, "java sql timestamp" // itemTypeName
							, board.getModifiedDate() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "memberGubunName"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getMemberGubunName() // itemValue
							, -1 // itemSize
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}