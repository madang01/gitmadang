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

import java.util.LinkedList;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.type.SingleItemType;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * SelfExn 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class SelfExnDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws OutOfMemoryError, BodyFormatException {
		SelfExn selfExn = new SelfExn();
		LinkedList<String> pathStack = new LinkedList<String>();
		pathStack.push("SelfExn");

		selfExn.setErrorPlace((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "errorPlace" // itemName
			, SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleReadableObject));

		selfExn.setErrorGubun((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "errorGubun" // itemName
			, SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleReadableObject));

		selfExn.setErrorMessageID((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "errorMessageID" // itemName
			, SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleReadableObject));

		selfExn.setErrorReason((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "errorMessage" // itemName
			, SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, "utf8" // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return selfExn;
	}
}