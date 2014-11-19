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
package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;

/**
 * 클라이언트 연결 클래스가 바라보는 출력 메시지 소켓 읽기 담당 쓰레드 폴 인터페이스.
 * 
 * @see OutputMessageReaderPool#addNewServer(AbstractAsynConnection)
 * @author Won Jonghoon
 * 
 */
public interface OutputMessageReaderPoolIF {
	/**
	 * 소켓 채널의 균등한 분배를 위해서 등록된 쓰레드중 최소 소켓을 갖는 쓰레드에 신규 소켓 채널을 배당한다.
	 * 
	 * @param serverConnection
	 *            등록을 원하는 신규 연결 클래스
	 */
	public void addNewServer(AbstractAsynConnection serverConnection);
}
