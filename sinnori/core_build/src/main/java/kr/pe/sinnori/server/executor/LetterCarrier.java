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
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class LetterCarrier {
	private Logger log = LoggerFactory.getLogger(LetterCarrier.class);
	
	private SocketChannel fromSC = null;
	private AbstractMessage inputMessage;
	
	private SocketResource socketResourceOfFromSC  = null;	
	
	private MessageProtocolIF messageProtocol = null;
	private ClassLoader classLoaderOfSererTask = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	private OutputMessageWriterIF outputMessageWriter = null;
	
	private LinkedList<ToLetter> toLetterList = new LinkedList<ToLetter>();
	
	public LetterCarrier( SocketChannel fromSC, 
			AbstractMessage inputMessage,
			SocketResource socketResourceOfFromSC,
			MessageProtocolIF messageProtocol,
			ClassLoader classLoaderOfSererTask,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.fromSC = fromSC;		
		this.inputMessage = inputMessage;
		this.socketResourceOfFromSC = socketResourceOfFromSC;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
	}

	public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
		if (null == syncOutputMessage) {
			throw new IllegalArgumentException("the parameter syncOutputMessage is null");
		}
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == inputMessage.messageHeaderInfo.mailboxID) {
			log.warn("입력 메시지가 비동기 메시지일 경우 동기 출력 메시지를 보낼 수 없습니다. 추가 취소된 메시지={}", syncOutputMessage.toString());
			
			throw new IllegalArgumentException("the synchronous output message can't be added becase the inputMessage is a asynchronous message");
		}
		syncOutputMessage.messageHeaderInfo = inputMessage.messageHeaderInfo;
		
		if (toLetterList.size() > 0) {
			log.warn("동기 메시지는 1개만 가질 수 있습니다. 추가 취소된 메시지={}", syncOutputMessage.toString());
			throw new IllegalArgumentException("the synchronous output message can't be added becase another synchronous message is already registered in the toLetter list");
		}
		
		ToLetter toLetter = buildToLetter(fromSC, syncOutputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			toLetterList.add(toLetter);
		}
	}
	
	public void addAsynOutputMessage(AbstractMessage outputMessage) throws InterruptedException {
		if (null == outputMessage) {
			throw new IllegalArgumentException("the parameter outputMessage is null");
		}
		
		addAsynOutputMessage(outputMessage, fromSC);
	}
	
	public void addAsynOutputMessage(AbstractMessage outputMessage, SocketChannel toSC) throws InterruptedException {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = socketResourceOfFromSC.getServerMailID();
		
		ToLetter toLetter = buildToLetter(toSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			toLetterList.add(toLetter);
		}
	}
	
	public ToLetter buildToLetter(SocketChannel toSC,
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) throws InterruptedException {
		String messageIDToClient = outputMessage.getMessageID();
	
		List<WrapBuffer> wrapBufferList = null;		
		
		MessageCodecIF messageServerCodec = null;
		try {
			messageServerCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfSererTask, messageIDToClient);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			
			
			return null;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get a output mesage server codec::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		}
	
		AbstractMessageEncoder messageEncoder = null;
		try {
			messageEncoder = messageServerCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get a output mesage encoder::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		}
	
	
		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/
	
		try {
			wrapBufferList = messageProtocol.M2S(outputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			log.warn(e.getMessage());
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(NoMoreDataPacketBufferException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;		
		} catch (BodyFormatException e) {
			log.warn(e.getMessage());
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;
		} catch (Exception | Error e) {
			log.warn(e.getMessage(), e);
			
			putSelfExnToOutputMessageQueue(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					"fail to a output message stream::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;
		}
		
		ToLetter toLetter = new ToLetter(toSC, 
				outputMessage.getMessageID(),
				outputMessage.messageHeaderInfo.mailboxID,
				outputMessage.messageHeaderInfo.mailID, wrapBufferList);
	
		return toLetter;
	}

	public void putAllToLettersToOutputMessageWriter() throws InterruptedException {
		while (! toLetterList.isEmpty()) {
			ToLetter toLetter = toLetterList.removeFirst();
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e) {
				log.warn("InterruptedException 발생으로 출력 메시지[{}] 손실", toLetter.toString());
				
				while (! toLetterList.isEmpty()) {
					toLetter = toLetterList.removeFirst();
					log.warn("InterruptedException 발생으로 출력 메시지[{}] 손실", toLetter.toString());
				}
				
				// Thread.currentThread().interrupt();
				throw e;
			}
		}
	}
	
	public static void putSelfExnToOutputMessageQueue(SocketChannel toSC, 
			int mailboxIDOfSelfExn, 
			int mailIDOfSelfExn, 
			String errorMessageID,
			String errorType, 
			String errorMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) throws InterruptedException {
		
		Logger log = LoggerFactory.getLogger(LetterCarrier.class);
		
		
		SelfExn selfExnOutObj = new SelfExn();
		selfExnOutObj.messageHeaderInfo.mailboxID = mailboxIDOfSelfExn;
		selfExnOutObj.messageHeaderInfo.mailID = mailIDOfSelfExn;
		selfExnOutObj.setErrorPlace("S");
		selfExnOutObj.setErrorGubun(errorType);

		selfExnOutObj.setErrorMessageID(errorMessageID);
		selfExnOutObj.setErrorMessage(errorMessage);

		List<WrapBuffer> wrapBufferListOfSelfExn = null;
		try {
			wrapBufferListOfSelfExn = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
		} catch (Exception e) {
			String errorMessage2 = String.format("fail to get the output stream of SelfExn, sc={}, SelfExn={}", 
					toSC.hashCode(),
					selfExnOutObj.toString());
			log.warn(errorMessage2, e);
			return;
		}

		ToLetter toLetter = new ToLetter(toSC, 
				selfExnOutObj.getMessageID(),
				selfExnOutObj.messageHeaderInfo.mailboxID,
				selfExnOutObj.messageHeaderInfo.mailID, wrapBufferListOfSelfExn);
		try {
			outputMessageWriter.putIntoQueue(toLetter);
		} catch (InterruptedException e) {
			log.warn("InterruptedException 발생으로 출력 메시지[{}] 손실", toLetter.toString());
			throw e;
		}
	}

	
	
	/*public void clearAllToLetterList() {
		toLetterList.clear();
	}
	
	public SocketResource getSocketResourceOfFromSC() {
		return socketResourceOfFromSC;
	}
	
	public void writeAllToLetterList(String title) {
		int i=0;
		for (ToLetter toLetter : toLetterList) {
			log.info("%::전체삭제-잔존 메시지[{}]=[{}]", i++, toLetter.toString());
		}
	}*/
}
