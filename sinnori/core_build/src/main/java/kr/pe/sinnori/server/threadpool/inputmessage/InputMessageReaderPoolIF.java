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

package kr.pe.sinnori.server.threadpool.inputmessage;

import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드가 바라보는 입력 메시지 소켓 읽기 담당 쓰레드 폴 인터페이스.
 * 
 * @author Won Jonghoon
 */

public interface InputMessageReaderPoolIF {	
	/*public void addNewClientFromAcceptProcessor(SocketChannel newSC) throws InterruptedException,
	NoMoreDataPacketBufferException;*/
	
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumNumberOfSockets();
}
