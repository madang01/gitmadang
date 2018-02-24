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

package kr.pe.sinnori.server.threadpool.inputmessage.handler;

import java.nio.channels.SocketChannel;

/**
 * 서버 입력 메시지 소켓 읽기 담당 쓰레드 폴이 바라보는 입력 메시지 소켓 읽기 담당 쓰레드 인터페이스
 * 
 * @author Won Jonghoon
 * 
 */
public interface InputMessageReaderIF {	
	public void addNewSocket(SocketChannel newSC) throws InterruptedException;
	public int getNumberOfSocket();
}
