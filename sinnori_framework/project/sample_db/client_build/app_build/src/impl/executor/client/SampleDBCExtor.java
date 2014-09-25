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

package impl.executor.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.MemberList.MemberList;
import kr.pe.sinnori.impl.message.MemberListResult.MemberListResult;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * 샘플 파일 송수신 클라이언트 버전2
 * @author madang01
 *
 */
public class SampleDBCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerTaskException, NotLoginException {
		MemberList inObj = new MemberList();
		
		// log.info(inObj.toString());
		
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
		//log.info("1111111111111");
		// log.info(messageFromServer.toString());
		if (messageFromServer instanceof MemberListResult) {
			MemberListResult outObj = (MemberListResult)messageFromServer;
			log.info("outObj={}", outObj.toString());
		} else {
			log.warn("messageFromServer={}", messageFromServer.toString());
		}
		
	}
}
