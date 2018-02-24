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

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * 클라이언트 프로젝트 개발자 시선의 클라이언트용 서버 접속 API 인터페이스
 * @author Won Jonghoon
 *
 */
public interface AnyProjectConnectionPoolIF {
	
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException;
	
	public void sendAsynInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, ConnectionPoolException, NotSupportedException, IOException,
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException;
		
	public AbstractConnection createConnection(String host, int port)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException;
}
