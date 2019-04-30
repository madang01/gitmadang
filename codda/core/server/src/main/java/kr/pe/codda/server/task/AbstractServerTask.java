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

import java.util.HashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.MessageEncoderManagerIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;

/**
 * <pre>
 * 로그인을 요구하지 않는 서버 비지니스 로직 부모 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다.
 * 서버 비지니스 로직 클래스 이름 형식은 
 * 접두어 '메시지 식별자' 와 접미어 'ServerTask' 로 구성된다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractServerTask implements MessageEncoderManagerIF {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractServerTask.class);
	
	private ClassLoader taskClassLoader = this.getClass().getClassLoader();
	// private MessageCodecIF serverInputMessageCodec = null;
	private AbstractMessageDecoder inputMessageDecoder = null;
	private HashMap<String, MessageCodecIF> messageID2ServerMessageCodecHash = 
			new HashMap<String, MessageCodecIF>();
	
	public AbstractServerTask() throws DynamicClassCallException {
		String classFullName = this.getClass().getName();
		int startIndex = classFullName.lastIndexOf(".") + 1;		
		int endIndex = classFullName.lastIndexOf("ServerTask");
		
		String messageID = classFullName.substring(startIndex, endIndex);
		
		/** WARNING! junit 에서 inner class 로 mock 객체를 만들어 테스트시 필요하므로 지우지 말것 */
		int middleIndex = messageID.lastIndexOf("$");		
		if (middleIndex >= 0) {
			char[] classNames =  messageID.toCharArray();
			
			for (middleIndex++; middleIndex < classNames.length; middleIndex++) {
				if (classNames[middleIndex] < '0' || classNames[middleIndex] > '9') {
					break;
				}
			}
			
			startIndex = startIndex + middleIndex;
			
			messageID = classFullName.substring(startIndex, endIndex);
		}
		
		
		
		// FIXME!
		// log.info("className=[{}], messageID=[{}]", classFullName, messageID);
		
		String messageCodecClassFullName = IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, messageCodecClassFullName);		
		
		if (! (retObject instanceof MessageCodecIF)) {
			String errorMessage = new StringBuilder()
			.append("this instance(classLoader=")
			.append(taskClassLoader.hashCode())
			.append(") of ").append(classFullName)
			.append("] class is not a instance of MessageCodecIF class").toString();

			throw new DynamicClassCallException(errorMessage);
		}
		
		MessageCodecIF serverMessageCodec = (MessageCodecIF)retObject;
		
		inputMessageDecoder = serverMessageCodec.getMessageDecoder();		
		
		messageID2ServerMessageCodecHash.put(messageID, serverMessageCodec);
	}

	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		MessageCodecIF serverMessageCodec = messageID2ServerMessageCodecHash.get(messageID);
		if (null == serverMessageCodec) {
			String serverMessageCodecClassFullName = IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
			Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, serverMessageCodecClassFullName);
			
			if (! (retObject instanceof MessageCodecIF)) {
				String errorMessage = new StringBuilder()
				.append("this instance of ")
				.append(serverMessageCodecClassFullName)
				.append(" class that was created by the classloader[")
				.append(taskClassLoader.hashCode())
				.append("] is not a instance of MessageCodecIF class").toString();

				throw new DynamicClassCallException(errorMessage);
			}
			
			serverMessageCodec = (MessageCodecIF)retObject;		
			
			messageID2ServerMessageCodecHash.put(messageID, serverMessageCodec);
		}
		
		return serverMessageCodec.getMessageEncoder();
	}	
	
	public void execute(String projectName,
			AcceptedConnection fromAcceptedConnection,			
			ProjectLoginManagerIF projectLoginManager,						
			int mailboxID, int mailID, String messageID, Object readableMiddleObject,
			MessageProtocolIF messageProtocol,
			PersonalLoginManagerIF fromPersonalLoginManager) throws InterruptedException {

		/*log.info("classLoader[{}], serverTask[{}], create new messageDecoder", 
				classLoaderOfSererTask.hashCode(),
				inputMessageID);*/
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = inputMessageDecoder.decode(messageProtocol.getSingleItemDecoder(), readableMiddleObject);
			inputMessage.messageHeaderInfo.mailboxID = mailboxID;
			inputMessage.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a input message from readable middle object[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("]").toString();
			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = new StringBuilder(errorMessage)
					.append(", errmsg=").append(e.getMessage()).toString();
			
			log.warn(errorReason);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,					
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;		
		} catch(Exception | Error e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a input message from readable middle object[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("], errmsg=")
					.append(e.getMessage()).toString();			
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
			return;
		}
		
// 		PersonalLoginManagerIF fromPersonalLoginManager = fromAcceptedConnection.getPersonalLoginManager();
		
		ToLetterCarrier toLetterCarrier = new ToLetterCarrier(fromAcceptedConnection,
				inputMessage, 
				projectLoginManager,
				messageProtocol,
				this);				

		try {
			doTask(projectName, fromPersonalLoginManager, toLetterCarrier, inputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
			
			
			String errorReason = new StringBuilder()
					.append("unknown error::fail to execuate the input message's task[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			
			log.warn(errorReason, e);
			
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue( 
					errorType,
					errorReason,
					mailboxID, mailID, messageID, fromAcceptedConnection, messageProtocol);
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
