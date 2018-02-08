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

package kr.pe.sinnori.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 프로젝트 개발자 시선의 클라이언트용 서버 접속 API 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ClientProjectIF {
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
	 * @throws ConnectionPoolTimeoutException 
	 * @throws InterruptedException 
	 */
	public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws SocketTimeoutException, ServerNotReadyException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, ConnectionPoolTimeoutException, InterruptedException;
		
	/**
	 * @return 연결 객체
	 * @throws InterruptedException 연결 폴에서 연결 객체를 가져올때 인터럽트가 걸렸을 경우 던지는 예외
	 * @throws NotSupportedException 공유+비동기 연결 폴에서 실행시 던지는 예외.  공유+비동기 연결 폴은 직접적으로 연결 객체를 받을 수 없다.
	 * @throws ConnectionPoolTimeoutException 
	 */
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, ConnectionPoolTimeoutException;
	
	/**
	 * 연결 객체를 반환한다.
	 * @param conn
	 * @throws NotSupportedException
	 */
	public void releaseConnection(AbstractConnection conn) throws NotSupportedException;
	
	/**
	 * 새로운 서버 익명 메시지 비지니스 로직으로 교체를 한다.
	 * @param newAnonymousServerMessageTask 새로운 서버 익명 메시지 비지니스 로직
	 */
	public void changeAsynOutputMessageTask(AsynOutputMessageTaskIF newAnonymousServerMessageTask);
	
	/**
	 * 클라이언트 프로젝트 중지
	 */
	public void stop();
	
	/**
	 * @return 메시지 스트림 변환 프로토콜
	 */
	public MessageProtocolIF getMessageProtocol();
	
	public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException;
	
	// public void saveSinnoriConfiguration() throws IllegalArgumentException, SinnoriConfigurationException, IOException;
}
