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

import java.io.IOException;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 프로젝트 개발자 시선의 클라이언트용 서버 접속 API 인터페이스
 * @author Won Jonghoon
 *
 */
public interface ClientProjectIF {
	
	public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws IOException,  
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException,  InterruptedException;
		
	
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException;
	
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
