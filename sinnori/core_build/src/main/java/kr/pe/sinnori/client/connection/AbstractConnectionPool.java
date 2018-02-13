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

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

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
	
	
	
	abstract public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws SocketTimeoutException, IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException;
	
	
	/**
	 * @return 연결 객체
	 * @throws InterruptedException 연결 폴에서 연결 객체를 가져올때 인터럽트가 걸렸을 경우 던지는 예외
	 * @throws NotSupportedException 공유+비동기 연결 폴에서 실행시 던지는 예외.  공유+비동기 연결 폴은 직접적으로 연결 객체를 받을 수 없다.
	 */
	abstract public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException;
	
	/**
	 * 연결 객체를 반환한다.
	 * @param conn
	 * @throws NotSupportedException
	 */
	abstract public void release(AbstractConnection conn) throws NotSupportedException;
	
	
}
