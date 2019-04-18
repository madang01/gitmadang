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

package kr.pe.codda.impl.message.AccountSearchProcessRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * AccountSearchProcessRes message encoder
 * @author Won Jonghoon
 *
 */
public final class AccountSearchProcessResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		AccountSearchProcessRes accountSearchProcessRes = (AccountSearchProcessRes)messageObj;
		encodeBody(accountSearchProcessRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(AccountSearchProcessRes accountSearchProcessRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("AccountSearchProcessRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "accountSearchType"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, accountSearchProcessRes.getAccountSearchType() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "userID"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, accountSearchProcessRes.getUserID() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "nickname"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, accountSearchProcessRes.getNickname() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}