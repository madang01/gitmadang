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


package kr.pe.sinnori.server.task;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;


public abstract class AbstractAuthServerTask extends AbstractServerTask {

	@Override
	public void execute(int index, 
			String projectName,
			SocketChannel fromSC,
			SocketResourceManagerIF socketResourceManager,
			SocketResource socketResourceOfFromSC,
			PersonalLoginManagerIF personalLoginManagerOfFromSC,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			MessageProtocolIF messageProtocol) throws InterruptedException {		
		
		if (! personalLoginManagerOfFromSC.isLogin()) {
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					SelfExn.ErrorType.valueOf(AccessDeniedException.class),
					"you are not logged in. this service requires a login",
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			
			return;
		}
		super.execute(index, projectName, fromSC, 
				socketResourceManager, 
				socketResourceOfFromSC,
				personalLoginManagerOfFromSC,
				wrapReadableMiddleObject, 
				messageProtocol);
	}
}
