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


package kr.pe.codda.server.task;

import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;

import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.WrapReadableMiddleObject;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.SocketResource;
import kr.pe.codda.server.SocketResourceManagerIF;


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
