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
package kr.pe.sinnori.impl.message.BigUShort;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BigUShort 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BigUShortDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BigUShort bigUShort = new BigUShort();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BigUShort");

		bigUShort.setFiller1((byte[])
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "filler1" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		bigUShort.setValue1((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "value1" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		bigUShort.setValue2((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "value2" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return bigUShort;
	}
}