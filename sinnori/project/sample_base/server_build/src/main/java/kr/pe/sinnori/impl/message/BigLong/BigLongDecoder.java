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
package kr.pe.sinnori.impl.message.BigLong;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BigLong 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BigLongDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BigLong bigLong = new BigLong();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BigLong");

		bigLong.setFiller1((byte[])
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "filler1" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		bigLong.setValue1((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "value1" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		bigLong.setValue2((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "value2" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return bigLong;
	}
}