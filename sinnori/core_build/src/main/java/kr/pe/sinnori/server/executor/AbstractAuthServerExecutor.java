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


package kr.pe.sinnori.server.executor;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * <pre>
 * 로그인을 요구하는 서버 비지니스 로직 부모 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다.
 * </pre>
 * @author Won Jonghoon
 *
 */
public abstract class AbstractAuthServerExecutor extends AbstractServerTask {
	
	
	@Override
	public void execute(int index, 
			String projectName,
			SocketChannel fromSC,
			SocketResource socketResourceOfFromSC,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			MessageProtocolIF messageProtocol, 
			ServerObjectCacheManagerIF serverObjectCacheManager) throws InterruptedException {
		// CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charsetOfProject);

		PersonalLoginManagerIF personalLoginManagerOfFromSC = socketResourceOfFromSC.getPersonalLoginManager();
		OutputMessageWriterIF outputMessageWriterOfFromSC = socketResourceOfFromSC.getOutputMessageWriterWithMinimumMumberOfSockets();
		
		if (! personalLoginManagerOfFromSC.isLogin()) {
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = "로그인을 요구하는 서비스입니다";
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(NotLoginException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);
			
			return;
		}
		super.execute(index, projectName, fromSC, 
				socketResourceOfFromSC, 
				wrapReadableMiddleObject, 
				messageProtocol, 
				serverObjectCacheManager);
	}
}
