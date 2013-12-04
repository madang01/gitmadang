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

package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴이 바라보는 출력 메시지 소켓 읽기 담당 쓰레드 인터페이스 
 * 
 * @see OutputMessageReader#addNewServer(AbstractAsynConnection)
 * @see OutputMessageReader#getCntOfClients()
 * @author Jonghoon Won
 * 
 */
public interface OutputMessageReaderIF {
	/**
	 * 신규 비동기 방식의 연결 클래스를 등록시킨다. 내부적으로는 연결이 가진 소켓 채널을 selector 와 해쉬 테이블<소켓채널,
	 * 비동기 방식의 연결클래스>에 등록한다.
	 * 
	 * @param serverConnection
	 *            비동기 방식의 연결클래스
	 */
	public void addNewServer(AbstractAsynConnection serverConnection) throws InterruptedException;

	/**
	 * 등록된 소켓 채널 수를 반환한다. 이는 균등 분배를 위해서 필요하다.
	 * 
	 * @return 등록된 소켓 채널 수
	 */
	public int getCntOfClients();
}
