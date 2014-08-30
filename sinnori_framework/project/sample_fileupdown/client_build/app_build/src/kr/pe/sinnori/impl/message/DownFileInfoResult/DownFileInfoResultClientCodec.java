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
package kr.pe.sinnori.impl.message.DownFileInfoResult;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;

/**
 * DownFileInfoResult 클라이언트 코덱
 * @author Jonghoon Won
 *
 */
public final class DownFileInfoResultClientCodec implements MessageCodecIF {

	@Override
	public MessageDecoder getMessageDecoder() throws DynamicClassCallException {
		return new DownFileInfoResultDecoder();
	}

	@Override
	public MessageEncoder getMessageEncoder() throws DynamicClassCallException {
		throw new DynamicClassCallException("DownFileInfoResult메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
	}
}