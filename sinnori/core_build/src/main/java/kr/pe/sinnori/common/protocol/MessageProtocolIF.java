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
package kr.pe.sinnori.common.protocol;

import java.util.ArrayDeque;
import java.util.List;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;

/**
 * 메시지 프로토콜 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface MessageProtocolIF {
	
	
	public List<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	
	
	public void S2MList(SocketOutputStream socketOutputStream, ArrayDeque<WrapReadableMiddleObject> wrapReadableMiddleObjectList) 
					throws HeaderFormatException, NoMoreDataPacketBufferException;
	
	public SingleItemDecoderIF getSingleItemDecoder();
	
	public int getMessageHeaderSize();
}

