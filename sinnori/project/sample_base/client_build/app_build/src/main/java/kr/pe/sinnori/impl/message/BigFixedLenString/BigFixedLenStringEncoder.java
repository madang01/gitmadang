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
package kr.pe.sinnori.impl.message.BigFixedLenString;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BigFixedLenString 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BigFixedLenStringEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BigFixedLenString bigFixedLenString = (BigFixedLenString)messageObj;
		encodeBody(bigFixedLenString, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BigFixedLenString bigFixedLenString, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BigFixedLenString");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "filler1"
			, kr.pe.sinnori.common.type.SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, bigFixedLenString.getFiller1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "value1"
			, kr.pe.sinnori.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
			, bigFixedLenString.getValue1() // itemValue
			, 10 // itemSize
			, "EUC-KR" // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}