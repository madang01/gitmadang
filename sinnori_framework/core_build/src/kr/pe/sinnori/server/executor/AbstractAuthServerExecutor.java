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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.io.LetterToClient;

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
			ServerProjectConfig serverProjectConfig, 			
			Charset projectCharset,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageProtocolIF messageProtocol,
			SocketChannel clientSC,
			ClientResource clientResource,
			ReceivedLetter receivedLetter, LoginManagerIF loginManager,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		if (!clientResource.isLogin()) {
			String messageID = receivedLetter.getMessageID();
			
			try {
				log.info(String.format("비로그인 상태에서 로그인 서비스 접근::%s::socket addr=[%s]", receivedLetter.toString(), clientSC.getRemoteAddress().toString()));
			} catch (Exception e) {
				log.info(String.format("비로그인 상태에서 로그인 서비스 접근::%s::원격지 주소얻기실패=[%s]", receivedLetter.toString(), e.getMessage()));
			}
			
			
			// new StringBuilder("로그인 요구 서비스입니다::").append(receivedLetter.toString()).toString()
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			// selfExnOutObj.setError("S", messageID, new NotLoginException("로그인을 요구하는 서비스입니다"));
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(NotLoginException.class);
			selfExnOutObj.setErrorMessageID(messageID);
			selfExnOutObj.setErrorMessage("로그인을 요구하는 서비스입니다");
			
			ArrayList<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				
				LetterToClient letterToClient = new LetterToClient(clientSC,
						selfExnOutObj,
						wrapBufferList);
				try {
					ouputMessageQueue.put(letterToClient);
				} catch (InterruptedException e) {
					try {
						ouputMessageQueue.put(letterToClient);
					} catch (InterruptedException e1) {
						log.error(new StringBuilder("재시도 과정에서 인터럽트 발생하여 종료::selfExnOutObj=[")
						.append(selfExnOutObj.toString()).append("]").toString(), e1);					
						Thread.interrupted();
					}
				}
			} catch(Exception e) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패", e);
			}
			return;
		}
		super.execute(index, serverProjectConfig, projectCharset, ouputMessageQueue, 
				messageProtocol, clientSC, clientResource, receivedLetter, 
				loginManager, serverObjectCacheManager);
	}
}
