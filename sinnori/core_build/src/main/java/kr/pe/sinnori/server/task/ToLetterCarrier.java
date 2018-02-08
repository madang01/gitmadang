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
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class ToLetterCarrier {
	private Logger log = LoggerFactory.getLogger(ToLetterCarrier.class);
	
	private SocketChannel fromSC = null;
	private AbstractMessage inputMessage;
	
	private SocketResource socketResourceOfFromSC  = null;	
	
	private MessageProtocolIF messageProtocol = null;
	private ClassLoader classLoaderOfSererTask = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	private LinkedList<ToLetter> toLetterList = new LinkedList<ToLetter>();
	
	public ToLetterCarrier( SocketChannel fromSC, 
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

	private static SelfExnRes buildSelfExn(SocketChannel toSC, 
			int mailboxIDOfSelfExn, 
			int mailIDOfSelfExn, 
			String errorMessageID,
			SelfExn.ErrorType errorType, 
			String errorReason) {
		
		SelfExnRes selfExnRes = new SelfExnRes();
		selfExnRes.messageHeaderInfo.mailboxID = mailboxIDOfSelfExn;
		selfExnRes.messageHeaderInfo.mailID = mailIDOfSelfExn;
		selfExnRes.setErrorPlace(SelfExn.ErrorPlace.SERVER);
		selfExnRes.setErrorType(errorType);
	
		selfExnRes.setErrorMessageID(errorMessageID);
		selfExnRes.setErrorReason(errorReason);
		
		return selfExnRes;
	}

	private static ToLetter buildToLetterOfSelfExn(SocketChannel toSC, 
			SelfExnRes selfExnRes, 
			MessageProtocolIF messageProtocol) throws 
			NoMoreDataPacketBufferException, 
			BodyFormatException, 
			HeaderFormatException {
		List<WrapBuffer> wrapBufferListOfSelfExn = messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.SELFEXN_ENCODER);
	
		ToLetter toLetter = new ToLetter(toSC, 
				selfExnRes.getMessageID(),
				selfExnRes.messageHeaderInfo.mailboxID,
				selfExnRes.messageHeaderInfo.mailID, wrapBufferListOfSelfExn);
		
		return toLetter;
	}

	private void addOutputMessage(SocketChannel toSC,			
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol) throws InterruptedException {
		String messageIDToClient = outputMessage.getMessageID();
	
		List<WrapBuffer> wrapBufferList = null;		
		
		MessageCodecIF messageServerCodec = null;
		try {
			messageServerCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfSererTask, messageIDToClient);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			log.warn("unknown error::fail to get a server output message codec"+e.getMessage(), e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = "fail to get a server output message codec::"+e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
		AbstractMessageEncoder messageEncoder = null;
		try {
			messageEncoder = messageServerCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			log.warn("unknown error::fail to get a output message encoder::"+e.getMessage(), e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = "fail to get a output message encoder::"+e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
	
		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/
		
		ToLetter toLetter = null;
	
		try {
			wrapBufferList = messageProtocol.M2S(outputMessage, messageEncoder);
			
			toLetter = new ToLetter(toSC, 
					outputMessage.getMessageID(),
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID, wrapBufferList);
		} catch (NoMoreDataPacketBufferException e) {
			log.warn(e.getMessage());
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(NoMoreDataPacketBufferException.class);
			String errorReason = e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (BodyFormatException e) {
			log.warn(e.getMessage());
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;			
		} catch (Exception | Error e) {
			log.warn("unknown error::fail to get a output message stream", e);
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = "fail to get a output message stream::" +e.getMessage();
			addOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
		
		toLetterList.add(toLetter);
	}

	private void addOutputErrorMessageToToLetterList(SocketChannel toSC,
			SelfExn.ErrorType errorType, 
			String errorReason,
			AbstractMessage outputMessage,			
			MessageProtocolIF messageProtocol) {
		SelfExnRes selfExnRes = buildSelfExn(toSC, 
				outputMessage.messageHeaderInfo.mailboxID,
				outputMessage.messageHeaderInfo.mailID,
				outputMessage.getMessageID(),
				errorType,
				errorReason);
		
		ToLetter toLetterOfSelfExn = null;
		try {
			toLetterOfSelfExn = buildToLetterOfSelfExn(toSC, selfExnRes, messageProtocol);
		} catch (Exception e1) {
			String errorMessage = String.format("fail to send the output stream of SelfExn[{}], fromSC hashcode={}, outputMessage={}", 
					selfExnRes.toString(),
					fromSC.hashCode(),
					outputMessage.toString());
			log.warn(errorMessage, e1);
			return;
		}
		toLetterList.add(toLetterOfSelfExn);
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
		
		addOutputMessage(fromSC, syncOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(AbstractMessage asynOutputMessage) throws InterruptedException {
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		addAsynOutputMessage(asynOutputMessage, fromSC);
	}
	
	public void addAsynOutputMessage(AbstractMessage outputMessage, SocketChannel toSC) throws InterruptedException {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = socketResourceOfFromSC.getServerMailID();		
		
		addOutputMessage(toSC, outputMessage, messageProtocol);
	}
	
	public void putAllInputMessageToOutputMessageQueue() throws InterruptedException {
		OutputMessageWriterIF outputMessageWriter = socketResourceOfFromSC.getOutputMessageWriter();
		
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
	
	public static void putInputErrorMessageToOutputMessageQueue(SocketChannel fromSC, 
			SelfExn.ErrorType errorType,
			String errorReason,			
			WrapReadableMiddleObject wrapReadableMiddleObject,
			SocketResource socketResourceOfFromSC,
			MessageProtocolIF messageProtocol) throws InterruptedException {
		if (null == fromSC) {
			throw new IllegalArgumentException("the parameter fromSC is null");
		}
		
		if (null == errorType) {
			throw new IllegalArgumentException("the parameter errorType is null");
		}		
		
		if (null == errorReason) {
			throw new IllegalArgumentException("the parameter errorReason is null");
		}
		
		if (null == wrapReadableMiddleObject) {
			throw new IllegalArgumentException("the parameter wrapReadableMiddleObject is null");
		}
		
		if (null == socketResourceOfFromSC) {
			throw new IllegalArgumentException("the parameter socketResourceOfFromSC is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		OutputMessageWriterIF outputMessageWriterOfFromSC = socketResourceOfFromSC.getOutputMessageWriter();
		
		int mailboxIDOfSelfExn = wrapReadableMiddleObject.getMailboxID();
		int mailIDOfSelfExn = wrapReadableMiddleObject.getMailID();
		String errorMessageID = wrapReadableMiddleObject.getMessageID();
		
		SelfExnRes selfExnRes = buildSelfExn(fromSC, 
				mailboxIDOfSelfExn, 
				mailIDOfSelfExn, 
				errorMessageID, 
				errorType,
				errorReason);

		ToLetter toLetterOfSelfExn = null;
		try {
			toLetterOfSelfExn = buildToLetterOfSelfExn(fromSC, selfExnRes, messageProtocol);
			
			outputMessageWriterOfFromSC.putIntoQueue(toLetterOfSelfExn);
		} catch (InterruptedException e) {
			Logger log = LoggerFactory.getLogger(ToLetterCarrier.class);
			log.warn("InterruptedException 발생으로 SelfExn[{}] 손실 발생, SelfExn 원인 제공 메시지={}", 
					toLetterOfSelfExn.toString(), 
					wrapReadableMiddleObject.toString());
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("fail to send the output stream of SelfExn[{}], fromSC hashcode={}, inputMessage={}", 
					selfExnRes.toString(),
					fromSC.hashCode(),
					wrapReadableMiddleObject.toString());
			Logger log = LoggerFactory.getLogger(ToLetterCarrier.class);
			log.warn(errorMessage, e);
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
