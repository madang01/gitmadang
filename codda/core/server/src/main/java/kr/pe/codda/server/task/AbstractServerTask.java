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

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.ServerSimpleClassLoaderIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;

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
			AcceptedConnection fromAcceptedConnection,			
			ProjectLoginManagerIF projectLoginManager,						
			ReadableMiddleObjectWrapper readableMiddleObjectWrapper,
			MessageProtocolIF messageProtocol) throws InterruptedException {
		
		MessageCodecIF serverInputMessageCodec = null;

		try {
			// serverInputMessageCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfSererTask, readableMiddleObjectWrapper.getMessageID());
			serverInputMessageCodec = serverSimpleClassLoader.getMessageCodec(readableMiddleObjectWrapper.getMessageID());
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get the server message codec of the input message[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("]").toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get the server message codec of the input message[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		}

		AbstractMessageDecoder inputMessageDecoder = null;
		try {
			inputMessageDecoder = serverInputMessageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = new StringBuilder()
					.append("fail to get a input message decoder[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		} catch(Exception | Error e) {			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = new StringBuilder()
					.append("unknown error::fail to get a input message decoder[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		}

		/*log.info("classLoader[{}], serverTask[{}], create new messageDecoder", 
				classLoaderOfSererTask.hashCode(),
				inputMessageID);*/
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = inputMessageDecoder.decode(messageProtocol.getSingleItemDecoder(), readableMiddleObjectWrapper.getReadableMiddleObject());
			inputMessage.messageHeaderInfo.mailboxID = readableMiddleObjectWrapper.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = readableMiddleObjectWrapper.getMailID();
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a input message from readable middle object[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("]").toString();
			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = new StringBuilder(errorMessage)
					.append(", errmsg=").append(e.getMessage()).toString();
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;		
		} catch(Exception | Error e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a input message from readable middle object[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("], errmsg=")
					.append(e.getMessage()).toString();			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		}
		
		PersonalLoginManagerIF fromPersonalLoginManager = fromAcceptedConnection.getPersonalLoginManager();
		
		ToLetterCarrier toLetterCarrier = new ToLetterCarrier(fromAcceptedConnection,
				inputMessage, 
				projectLoginManager,
				messageProtocol,
				serverSimpleClassLoader);				

		try {
			doTask(projectName, fromPersonalLoginManager, toLetterCarrier, inputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
			String errorReason = new StringBuilder()
					.append("unknown error::fail to execuate the input message's task[")
					.append(readableMiddleObjectWrapper.toSimpleInformation())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					readableMiddleObjectWrapper, fromAcceptedConnection, messageProtocol);
			return;
		}


		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}
	
	abstract public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception;
	
	
	@Override
	public void finalize() {
		log.info("{} call finalize", this.getClass().getSimpleName());
	}
}