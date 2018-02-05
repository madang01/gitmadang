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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * <pre>
 * 로그인을 요구하지 않는 서버 비지니스 로직 부모 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractServerTask {
	protected Logger log = LoggerFactory.getLogger(AbstractServerTask.class);
	
	private ClassLoader classLoaderOfSererTask = this.getClass().getClassLoader();
	
	public void execute(int index, 
			String projectName,
			SocketChannel fromSC,
			SocketResource socketResourceOfFromSC,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			MessageProtocolIF messageProtocol, 
			ServerObjectCacheManagerIF serverObjectCacheManager) throws InterruptedException {
		
		OutputMessageWriterIF outputMessageWriterOfFromSC = socketResourceOfFromSC.getOutputMessageWriterWithMinimumMumberOfSockets();				
		MessageCodecIF serverMessageCodec = null;

		try {
			serverMessageCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfSererTask, wrapReadableMiddleObject.getMessageID());
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);			
			return;
		} catch (Exception e) {
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = "fail to get a server message codec::" + e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);				
			return;
		}

		AbstractMessageDecoder messageDecoder = null;
		try {
			messageDecoder = serverMessageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());

			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);	
			return;
		} catch(Exception | Error e) {
			log.warn(e.getMessage(), e);
			
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = "fail to get a input message decoder::" + e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);	
			return;
		}


		/*log.info("classLoader[{}], serverTask[{}], create new messageDecoder", 
				classLoaderOfSererTask.hashCode(),
				inputMessageID);*/
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), wrapReadableMiddleObject.getReadableMiddleObject());
			inputMessage.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
		} catch (BodyFormatException e) {
			log.warn(e.getMessage());
			
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);		
			return;
		} catch(Exception | Error e) {
			log.warn(e.getMessage(), e);
			
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = "fail to get a input message from readable middle object::" + e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);	
			return;
		}
		// SocketResource socketResourceOfFromSC
		LetterCarrier letterCarrier = new LetterCarrier(fromSC, 
				inputMessage, 
				socketResourceOfFromSC,
				messageProtocol,
				classLoaderOfSererTask,
				serverObjectCacheManager);
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = socketResourceOfFromSC.getPersonalLoginManager();		

		try {
			doTask(projectName, personalLoginManagerOfFromSC, letterCarrier, inputMessage);		
		} catch (Exception | Error e) {
			log.warn("fail to execuate task", e);
			
			int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
			int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
			String errorMessageID = wrapReadableMiddleObject.getMessageID();
			String errorMessage = "fail to execuate task::" + e.getMessage();
			LetterCarrier.putSelfExnToOutputMessageQueue(fromSC, 
					mailboxIDOfSelfExn, 
					mailIDOfSelfExn, 
					errorMessageID, 
					SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class),
					errorMessage, messageProtocol, outputMessageWriterOfFromSC);
			return;
		}

		letterCarrier.putAllToLettersToOutputMessageWriter();

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}	
	
	
	abstract public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, LetterCarrier letterCarrier,
			AbstractMessage requestMessage) throws Exception;
}
