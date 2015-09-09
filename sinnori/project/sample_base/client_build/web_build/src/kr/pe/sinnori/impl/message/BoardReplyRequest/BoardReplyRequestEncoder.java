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
package kr.pe.sinnori.impl.message.BoardReplyRequest;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardReplyRequest 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardReplyRequestEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardReplyRequest)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardReplyRequest 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardReplyRequest boardReplyRequest = (BoardReplyRequest) messageObj;
		encodeBody(boardReplyRequest, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardReplyRequest 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardReplyRequest BoardReplyRequest 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardReplyRequest boardReplyRequest, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardReplyRequestSingleItemPath = "BoardReplyRequest";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardReplyRequestSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "boardId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardReplyRequest.getBoardId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "parentBoardNo"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardReplyRequest.getParentBoardNo() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "subject"
					, 8 // itemTypeID
					, "us pascal string" // itemTypeName
					, boardReplyRequest.getSubject() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "content"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, boardReplyRequest.getContent() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "attachId"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, boardReplyRequest.getAttachId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "userId"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardReplyRequest.getUserId() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardReplyRequestSingleItemPath, "ip"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardReplyRequest.getIp() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}