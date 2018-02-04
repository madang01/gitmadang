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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
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
	
	
	public void sendSelfExnToClient(SocketChannel sc, WrapReadableMiddleObject wrapReadableMiddleObject, 
			String errorGuubun, 
			String errorMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) {
		
		int mailboxID = wrapReadableMiddleObject.getMailboxID();
		int mailID = wrapReadableMiddleObject.getMailID();
		String messageID = wrapReadableMiddleObject.getMessageID();
		
		sendSelfExnToClient(sc, mailboxID, mailID, messageID, errorGuubun, errorMessage, messageProtocol, outputMessageWriter);
		
	}
	public void sendSelfExnToClient(SocketChannel sc, int mailboxID, int mailID, String messageID,
			String errorGuubun, 
			String errorMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) {
		
		
		SelfExn selfExnOutObj = new SelfExn();
		selfExnOutObj.messageHeaderInfo.mailboxID = mailboxID;
		selfExnOutObj.messageHeaderInfo.mailID = mailID;

		selfExnOutObj.setErrorPlace("S");
		selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));

		selfExnOutObj.setErrorMessageID(messageID);
		selfExnOutObj.setErrorMessage(errorMessage);

		List<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
		} catch (Exception e1) {
			log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, sc={}, SelfExn={}", sc.hashCode(),
					selfExnOutObj.toString());
			System.exit(1);
		}

		putToOutputMessageQueue(sc, selfExnOutObj, wrapBufferList,
				outputMessageWriter);
	}
	
	
	
	public void execute(int index, String projectName, OutputMessageWriterIF outputMessageWriter, MessageProtocolIF messageProtocol,
			SocketChannel fromSC,  WrapReadableMiddleObject wrapReadableMiddleObject,
			PersonalLoginManagerIF personalLoginManager, ServerObjectCacheManagerIF serverObjectCacheManager) {
		
		// this.messageProtocol = messageProtocol;
		// this.serverObjectCacheManager = serverObjectCacheManager;
		
		// FIXME!
		// log.info("inputMessage=[%s]", inputMessage.toString());
		// long firstErraseTime = new java.util.Date().getTime();

		// CharsetEncoder charsetEncoderOfProject =
		// CharsetUtil.createCharsetEncoder(charsetOfProject);

		String inputMessageID = wrapReadableMiddleObject.getMessageID();

		// Charset projectCharset = serverProjectConfig.getCharset();
		
		ClassLoader classLoaderOfSererTask = this.getClass().getClassLoader();
		
		MessageCodecIF messageCodec = null;

		try {
			messageCodec = serverObjectCacheManager.getServerCodec(classLoaderOfSererTask, inputMessageID);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);				
			return;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get the message codec::" + e.getMessage(),
					messageProtocol,
					outputMessageWriter);				
			return;
		}

		AbstractMessageDecoder messageDecoder = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());

			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get the message decoder::" + e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return;
		}


		log.info("classLoader[{}], serverTask[{}], create new messageDecoder", classLoaderOfSererTask.hashCode(),
				inputMessageID);
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), wrapReadableMiddleObject.getReadableMiddleObject());
			inputMessage.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
		} catch (BodyFormatException e) {
			log.warn(e.getMessage());
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);			
			return;
		} catch (OutOfMemoryError e) {
			log.warn(e.getMessage(), e);

			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					"fail to get the input message::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					"fail to get the input message::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return;
		}
		
		LetterCarrier letterCarrier = new LetterCarrier(fromSC, 
				inputMessage, 
				messageProtocol,
				classLoaderOfSererTask,
				serverObjectCacheManager, 
				outputMessageWriter);
		
		// PersonalLoginManager personalLoginManager = socketResourceManager.getClientResource(fromSC).getPersonalLoginManager();
		

		try {
			doTask(projectName, personalLoginManager, letterCarrier, inputMessage);
		} catch (java.lang.Error e) {
			String errorMessgae = 
					String.format("1.%s Executor[%d], fromSC[%d], inputMessage[%s], errorMessage=%s", 
							projectName, 
							index,
							fromSC.hashCode(), inputMessage.toString(), e.getMessage());
			
			log.warn(errorMessgae, e);
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class),
					"fail to execuate task::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			/**
			 * FIXME! 서버 타스크 수행중 받은 편지들 로그 남기기, 삭제할 필요는 없어 삭제는 하지 않음.
			 */
			letterCarrier.writeLogAll("1.서버 타스크 수행중 에러");
			return;
		} catch (Exception e) {
			String errorMessgae = 
					String.format("2.%s Executor[%d], fromSC[%d], inputMessage[%s], errorMessage=%s", 
							projectName, 
							index,
							fromSC.hashCode(), inputMessage.toString(), e.getMessage());
			
			log.warn(errorMessgae, e);
			
			sendSelfExnToClient(fromSC, 
					wrapReadableMiddleObject, 
					SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class),
					"fail to execuate task::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			/**
			 * FIXME! 서버 타스크 수행중 받은 편지들 로그 남기기, 삭제할 필요는 없어 삭제는 하지 않음.
			 */
			letterCarrier.writeLogAll("2.서버 타스크 수행중 에러");
			return;
		}

		letterCarrier.putAsynToLetterListToOutputMessageWriter();

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}

	
	private void putToOutputMessageQueue(SocketChannel toSC, 
			AbstractMessage wrapBufferMessage, 
			List<WrapBuffer> wrapBufferList,
			OutputMessageWriterIF outputMessageWriter) {

		// wrapBufferMessage.messageHeaderInfo = messageFromClient.messageHeaderInfo;

		ToLetter letterToClient = new ToLetter(toSC, 
				wrapBufferMessage.getMessageID(),
				wrapBufferMessage.messageHeaderInfo.mailboxID,
				wrapBufferMessage.messageHeaderInfo.mailID, wrapBufferList);
		try {
			outputMessageWriter.putIntoQueue(letterToClient);
		} catch (InterruptedException e) {
			try {
				outputMessageWriter.putIntoQueue(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, toSC hashCode=[{}], 전달 못한 송신 메시지=[{}]",
						toSC.hashCode(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}

	/*private void putToOutputMessageQueue(SocketChannel clientSC, WrapReadableMiddleObject wrapReadableMiddleObject,
			AbstractMessage wrapBufferMessage, List<WrapBuffer> wrapBufferList,
			OutputMessageWriterIF outputMessageWriter) {

		
		 * wrapBufferMessage.messageHeaderInfo.mailboxID =
		 * receivedLetter.getMailboxID(); wrapBufferMessage.messageHeaderInfo.mailID =
		 * receivedLetter.getMailID();
		 

		ToLetter letterToClient = new ToLetter(clientSC, wrapBufferMessage.getMessageID(), 
				wrapBufferMessage.messageHeaderInfo.mailboxID , 
				wrapBufferMessage.messageHeaderInfo.mailID, 
				wrapBufferList);
		try {
			outputMessageWriter.putIntoQueue(letterToClient);
		} catch (InterruptedException e) {
			try {
				outputMessageWriter.putIntoQueue(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], 입력 메시지[{}] 추출 실패, 전달 못한 송신 메시지=[{}]",
						clientSC.hashCode(), wrapReadableMiddleObject.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}*/

	
	abstract public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, LetterCarrier letterCarrier,
			AbstractMessage requestMessage) throws Exception;
}
