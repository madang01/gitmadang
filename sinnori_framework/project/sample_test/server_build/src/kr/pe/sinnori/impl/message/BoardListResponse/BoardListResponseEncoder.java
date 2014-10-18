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
package kr.pe.sinnori.impl.message.BoardListResponse;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardListResponse 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardListResponseEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardListResponse)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardListResponse 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardListResponse boardListResponse = (BoardListResponse) messageObj;
		encodeBody(boardListResponse, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardListResponse 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardListResponse BoardListResponse 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardListResponse boardListResponse, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardListResponseSingleItemPath = "BoardListResponse";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardListResponseSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardListResponseSingleItemPath, "cnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardListResponse.getCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<BoardListResponse.Board> boardList = boardListResponse.getBoardList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == boardList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != boardListResponse.getCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(boardListResponse.getCnt())
				.append("] is not zero but ")
				.append(boardListResponseSingleItemPath)
				.append(".")
				.append("boardList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int boardListSize = boardList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (boardListSize != boardListResponse.getCnt()) {
				String errorMessage = new StringBuilder(boardListResponseSingleItemPath)
				.append(".")
				.append("boardList.length[")
				.append(boardListSize)
				.append("] is not same to ")
				.append(boardListResponseSingleItemPath)
				.append(".")
				.append("cnt[")
				.append(boardListResponse.getCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object boardMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(boardListResponseSingleItemPath, "board", boardListSize, middleWriteObj);
			for (int i=0; i < boardListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Board").append("[").append(i).append("]").toString());
				String boardSingleItemPath = singleItemPathStatck.getLast();
				Object boardMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(boardSingleItemPath, boardMiddleWriteArray, i);
				BoardListResponse.Board board = boardList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "boardNO"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, board.getBoardNO() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "groupNO"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, board.getGroupNO() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "parentNO"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, board.getParentNO() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "groupSeq"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, board.getGroupSeq() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "depth"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getDepth() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "boardTypeID"
							, 6 // itemTypeID
							, "long" // itemTypeName
							, board.getBoardTypeID() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "title"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getTitle() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "contents"
							, 9 // itemTypeID
							, "si pascal string" // itemTypeName
							, board.getContents() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "userID"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getUserID() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "viewCnt"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getViewCnt() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "votes"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getVotes() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "isDeleted"
							, 7 // itemTypeID
							, "ub pascal string" // itemTypeName
							, board.getIsDeleted() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "createDate"
							, 16 // itemTypeID
							, "java sql timestamp" // itemTypeName
							, board.getCreateDate() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "lastModifiedDate"
							, 16 // itemTypeID
							, "java sql timestamp" // itemTypeName
							, board.getLastModifiedDate() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(boardSingleItemPath, "memberGubun"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, board.getMemberGubun() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, boardMiddleWriteObj);
				singleItemPathStatck.pop();
			}
		}
	}
}