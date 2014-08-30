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
package kr.pe.sinnori.impl.message.BigShort;

import java.nio.charset.Charset;
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BigShort 메시지 인코더
 * @author Jonghoon won
 *
 */
public final class BigShortEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof BigShort)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 BigShort 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		BigShort bigShort = (BigShort) messageObj;
		encodeBody(bigShort, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * BigShort 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param bigShort BigShort 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(BigShort bigShort, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String bigShortSingleItemPath = "BigShort";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(bigShortSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(bigShortSingleItemPath, "filler1"
					, 13 // itemTypeID
					, "si variable length byte[]" // itemTypeName
					, bigShort.getFiller1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(bigShortSingleItemPath, "value1"
					, 2 // itemTypeID
					, "short" // itemTypeName
					, bigShort.getValue1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(bigShortSingleItemPath, "value2"
					, 2 // itemTypeID
					, "short" // itemTypeName
					, bigShort.getValue2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
	}
}