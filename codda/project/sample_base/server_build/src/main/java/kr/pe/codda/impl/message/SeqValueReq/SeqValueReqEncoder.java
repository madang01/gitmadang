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
package kr.pe.codda.impl.message.SeqValueReq;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * SeqValueReq 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class SeqValueReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		SeqValueReq seqValueReq = (SeqValueReq)messageObj;
		encodeBody(seqValueReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(SeqValueReq seqValueReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("SeqValueReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "seqTypeId"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, seqValueReq.getSeqTypeId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "wantedSize"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, seqValueReq.getWantedSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}