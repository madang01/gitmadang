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
package kr.pe.sinnori.client.connection.sync;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 클라이언트 소켓 채널 블락킹 모드가 블락인 동기 연결 클래스의 부모 추상화 클래스<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractSyncConnection extends AbstractConnection {

	protected InputStream inputStream = null;
	
	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param asynOutputMessageQueue 서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 */
	public AbstractSyncConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			ClientProjectConfig clientProjectConfig,
			LinkedBlockingQueue<ReceivedLetter> asynOutputMessageQueue,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException, NoMoreDataPacketBufferException {
		super(index, socketTimeOut, whetherToAutoConnect, clientProjectConfig, 
				asynOutputMessageQueue, messageProtocol, dataPacketBufferQueueManager, clientObjectCacheManager);
		// log.info("whether_to_auto_connect=[%s]", whether_to_auto_connect);
	}	
}
