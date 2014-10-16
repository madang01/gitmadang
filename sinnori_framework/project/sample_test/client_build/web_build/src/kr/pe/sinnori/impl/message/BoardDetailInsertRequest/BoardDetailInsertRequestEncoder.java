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
package kr.pe.sinnori.impl.message.BoardDetailInsertRequest;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardDetailInsertRequest 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardDetailInsertRequestEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BoardDetailInsertRequest)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BoardDetailInsertRequest 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BoardDetailInsertRequest boardDetailInsertRequest = (BoardDetailInsertRequest) messageObj;
		encodeBody(boardDetailInsertRequest, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BoardDetailInsertRequest 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param boardDetailInsertRequest BoardDetailInsertRequest 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BoardDetailInsertRequest boardDetailInsertRequest, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String boardDetailInsertRequestSingleItemPath = "BoardDetailInsertRequest";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(boardDetailInsertRequestSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "groupNO"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailInsertRequest.getGroupNO() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "parentNO"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailInsertRequest.getParentNO() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "groupSeq"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailInsertRequest.getGroupSeq() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "depth"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, boardDetailInsertRequest.getDepth() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "boardTypeID"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, boardDetailInsertRequest.getBoardTypeID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "title"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailInsertRequest.getTitle() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "contents"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, boardDetailInsertRequest.getContents() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(boardDetailInsertRequestSingleItemPath, "userID"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, boardDetailInsertRequest.getUserID() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}