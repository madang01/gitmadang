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
package kr.pe.sinnori.client.connection;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 클라이언트 연결 클래스 폴 관리자 부모 추상화 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractConnectionPool {
	protected Logger log = LoggerFactory.getLogger(AbstractConnectionPool.class);
	
	/** 모니터 */
	protected final Object monitor = new Object();
	

	/**
	 * 서버에서 공지등 불특정 다수한테 메시지를 보낼때 출력 메시지를 담은 큐
	 */
	protected LinkedBlockingQueue< ReceivedLetter> serverOutputMessageQueue = null;

	
	/**
	 * 생성자
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param serverOutputMessageQueue 서버에서 보내는 불특정 다수 메시지를 받는 큐
	 */
	protected AbstractConnectionPool(LinkedBlockingQueue<ReceivedLetter> serverOutputMessageQueue) {
		this.serverOutputMessageQueue = serverOutputMessageQueue;
	}
	
	/**
	 * 입력 메시지를 받아 서버로 보낸후 출력 메시지를 얻더 반환한다.
	 * 
	 * @param inputMessage
	 *            입력 메시지
	 * @return 출력 메시지 목록
	 * @throws ServerNotReadyException
	 *             서버 연결 실패시 발생
	 * @throws SocketTimeoutException
	 *             서버 응답 시간 초과시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             래퍼 메시지를 만들때 데이터 패킷 버퍼 큐에서 버퍼를 확보하는데 실패할때 발생
	 * @throws BodyFormatException
	 *             스트림에서 메시지로, 메시지에서 스트림으로 바꿀때 바디 부분 구성 실패시 발생
	 * @throws InterruptedException 
	 */
	abstract public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws SocketTimeoutException, ServerNotReadyException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, NotLoginException, ConnectionPoolTimeoutException, InterruptedException;
	
	
	/**
	 * @return 연결 객체
	 * @throws InterruptedException 연결 폴에서 연결 객체를 가져올때 인터럽트가 걸렸을 경우 던지는 예외
	 * @throws NotSupportedException 공유+비동기 연결 폴에서 실행시 던지는 예외.  공유+비동기 연결 폴은 직접적으로 연결 객체를 받을 수 없다.
	 */
	abstract public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, ConnectionPoolTimeoutException;
	
	/**
	 * 연결 객체를 반환한다.
	 * @param conn
	 * @throws NotSupportedException
	 */
	abstract public void release(AbstractConnection conn) throws NotSupportedException;
	
	/**
	 * @return 메일함 갯수
	 */
	abstract public int getUsedMailboxCnt();

	/**
	 * @return 전체 메일함 갯수
	 */
	abstract public int getTotalMailbox();
	
	abstract public List<AbstractConnection> getConnectionList();
}
