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

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 서버 입력 메시지 소켓 읽기 담당 쓰레드 폴이 바라보는 입력 메시지 소켓 읽기 담당 쓰레드 인터페이스
 * 
 * @author Won Jonghoon
 * 
 */
public interface InputMessageReaderIF {

	/**
	 * 신규 소캣 채널을 등록한다.<br/>
	 * 내부적으로는 selector 와 해쉬 테이블<소켓채널, 클라이언트 자원>에 등록한다.<br/>
	 * <br/>
	 * 
	 * 신규 소켓 채널을 selector 적용을 받기 위해서는 신규 소켓 채널을 등록시킨 다음 <br/>
	 * Selector.select() 수행되어야 한다.<br/>
	 * 이를 순서적으로 나열하면 아래와 같다.<br/>
	 * (1) 신규 소켓 채널 등록 : newClients.add(sc); <br/>
	 * (2) 신규 소켓 채널의 selector 등록 : sc.register(selector, SelectionKey.OP_READ);
	 * (3) selector.select();<br/>
	 * 그러나 (1)과 (2), (3)은 별도의 thread 이다. <br/>
	 * (1)은 접속 요청 허용 쓰레드이고<br/>
	 * (2), (3) 는 읽기 수행 쓰레드이다.<br/>
	 * 따라서 (1)수행시 본연의 업무외에 (2)와 (3)을 순차적으로 수행시킬수있는 방안이 있어야 한다.<br/>
	 * 이 문제를 해결하기 위해서 일정시간 대기후 소켓 채널이 selector 등록 여부를 조사하여<br/>
	 * 만약 미 등록시에는 selector 을 깨운다. 이동작은 소켓 채널이 selector 에 등록 될때까지 반복된다.
	 * 
	 * @param sc
	 * @throws InterruptedException
	 *             쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException
	 *             소켓 채널당 1:1로 읽기 전용 버퍼를 할당받는다. 이 읽기 전용 버퍼 확보 실패시 발생
	 */
	public void addClient(SocketChannel sc) throws NoMoreDataPacketBufferException;

	/**
	 * 등록된 소켓 채널 수를 반환한다. 이는 균등 분배를 위해서 필요하다.
	 * 
	 * @return 등록된 소켓 채널 수
	 */
	public int getCntOfClients();
}
