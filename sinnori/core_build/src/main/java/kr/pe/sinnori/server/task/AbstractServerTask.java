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

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.classloader.ServerSimpleClassLoaderIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;

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
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractServerTask.class);
	
	private ServerSimpleClassLoaderIF serverSimpleClassLoader = null;
	
	public void setServerSimpleClassloader(ServerSimpleClassLoaderIF serverSimpleClassLoader) {
		this.serverSimpleClassLoader = serverSimpleClassLoader;
	}
	
	public void execute(int index, 
			String projectName,
			SocketChannel fromSC,
			SocketResourceManagerIF socketResourceManager,
			SocketResource socketResourceOfFromSC,
			PersonalLoginManagerIF personalLoginManagerOfFromSC,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			MessageProtocolIF messageProtocol) throws InterruptedException {
		
		MessageCodecIF serverInputMessageCodec = null;

		try {
			// serverInputMessageCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfSererTask, wrapReadableMiddleObject.getMessageID());
			serverInputMessageCodec = serverSimpleClassLoader.getMessageCodec(wrapReadableMiddleObject.getMessageID());
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get the server message codec of the input message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("]").toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknwon error::fail to get the server message codec of the input message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		}

		AbstractMessageDecoder inputMessageDecoder = null;
		try {
			inputMessageDecoder = serverInputMessageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = String.format("fail to get a input message[%s] decoder, errmsg=%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		} catch(Exception | Error e) {			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = String.format("unknown error::fail to get a input message[%s] decoder::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		}

		/*log.info("classLoader[{}], serverTask[{}], create new messageDecoder", 
				classLoaderOfSererTask.hashCode(),
				inputMessageID);*/
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = inputMessageDecoder.decode(messageProtocol.getSingleItemDecoder(), wrapReadableMiddleObject.getReadableMiddleObject());
			inputMessage.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a input message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("] from readable middle object").toString();
			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = new StringBuilder(errorMessage)
					.append(", errmsg=").append(e.getMessage()).toString();
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;		
		} catch(Exception | Error e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a input message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("] from readable middle object::")
					.append(e.getMessage()).toString();			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		}
		
		// PersonalLoginManagerIF personalLoginManagerOfFromSC = socketResourceOfFromSC.getPersonalLoginManager();
		
		ToLetterCarrier toLetterCarrier = new ToLetterCarrier(fromSC, 
				inputMessage, 
				socketResourceManager,
				personalLoginManagerOfFromSC,
				messageProtocol,
				serverSimpleClassLoader);				

		try {
			doTask(projectName, personalLoginManagerOfFromSC, toLetterCarrier, inputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
			String errorReason = String.format("unknown error::fail to execuate the message[%s]'s task::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
					errorType,
					errorReason,
					wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
			return;
		}

		toLetterCarrier.putAllInputMessageToOutputMessageQueue();

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}
	
	abstract public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception;
	
	
	@Override
	public void finalize() {
		log.info("call finalize");
	}
}
