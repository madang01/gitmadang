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
package kr.pe.sinnori.impl.message.SelfExn;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * SelfExn 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class SelfExnDecoder extends AbstractMessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SelfExn 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param middleReadableObject 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SelfExn 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws OutOfMemoryError, BodyFormatException {
		SelfExn selfExn = new SelfExn();
		String sigleItemPath0 = "SelfExn";

		selfExn.setErrorPlace((String)
		singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		, "errorPlace" // itemName
		, SingleItemType.UB_PASCAL_STRING // itemType
		, -1 // itemSize
		, "ISO-8859-1" // nativeItemCharset
		, middleReadableObject));

		selfExn.setErrorGubun((String)
		singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		, "errorGubun" // itemName
		, SingleItemType.UB_PASCAL_STRING // itemType
		, -1 // itemSize
		, "ISO-8859-1" // nativeItemCharset
		, middleReadableObject));

		selfExn.setErrorMessageID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		, "errorMessageID" // itemName
		, SingleItemType.UB_PASCAL_STRING // itemType
		, -1 // itemSize
		, "ISO-8859-1" // nativeItemCharset
		, middleReadableObject));

		selfExn.setErrorMessage((String)
		singleItemDecoder.getValueFromReadableMiddleObject(sigleItemPath0
		, "errorMessage" // itemName
		, SingleItemType.US_PASCAL_STRING // itemType
		, -1 // itemSize
		, "utf8" // nativeItemCharset
		, middleReadableObject));
		return selfExn;
	}
}