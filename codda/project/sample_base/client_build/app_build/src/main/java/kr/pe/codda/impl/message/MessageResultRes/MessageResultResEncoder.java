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
package kr.pe.codda.impl.message.MessageResultRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * MessageResultRes 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class MessageResultResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		MessageResultRes messageResultRes = (MessageResultRes)messageObj;
		encodeBody(messageResultRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(MessageResultRes messageResultRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("MessageResultRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "taskMessageID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, messageResultRes.getTaskMessageID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "isSuccess"
			, kr.pe.codda.common.type.SingleItemType.BOOLEAN // itemType
			, messageResultRes.getIsSuccess() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "resultMessage"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, messageResultRes.getResultMessage() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}