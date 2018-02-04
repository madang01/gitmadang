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
import java.util.List;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
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
			OutputMessageWriterIF outputMessageWriter,
			MessageProtocolIF messageProtocol,
			SocketChannel fromSC,
			WrapReadableMiddleObject receivedLetter, 
			PersonalLoginManagerIF personalLoginManager,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		// CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charsetOfProject);

		if (! personalLoginManager.isLogin()) {
			String messageID = receivedLetter.getMessageID();
			
			// new StringBuilder("로그인 요구 서비스입니다::").append(receivedLetter.toString()).toString()
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			// selfExnOutObj.setError("S", messageID, new NotLoginException("로그인을 요구하는 서비스입니다"));
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(NotLoginException.class));
			
			selfExnOutObj.setErrorMessageID(messageID);
			selfExnOutObj.setErrorMessage("로그인을 요구하는 서비스입니다");
			
			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				
				ToLetter letterToClient = new ToLetter(fromSC,
						selfExnOutObj.getMessageID(),
						selfExnOutObj.messageHeaderInfo.mailboxID,
						selfExnOutObj.messageHeaderInfo.mailID,
						wrapBufferList);
				try {
					outputMessageWriter.putIntoQueue(letterToClient);
				} catch (InterruptedException e) {
					try {
						outputMessageWriter.putIntoQueue(letterToClient);
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
		super.execute(index, projectName, outputMessageWriter, 
				messageProtocol, fromSC, receivedLetter, 
				personalLoginManager, serverObjectCacheManager);
	}
}
