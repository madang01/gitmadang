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
package kr.pe.codda.common.protocol;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;

/**
 * 메시지 프로토콜 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface MessageProtocolIF {
	
	
	public ArrayDeque<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	public void S2MList(SocketChannel fromSC, SocketOutputStream socketOutputStream, ReceivedMessageBlockingQueueIF receivedMessageBlockingQueue) 
					throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException;
	
	
	public SingleItemDecoderIF getSingleItemDecoder();
	
	public int getMessageHeaderSize();
}

