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
import kr.pe.sinnori.common.exception.LoginUserNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
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
	
	private AbstractMessage syncOutputMessage = null;
	
	
	private SocketResourceManagerIF socketResourceManager = null;
	private PersonalLoginManagerIF personalMemberManager = null;
	
	private MessageProtocolIF messageProtocol = null;
	private ClassLoader classLoaderOfServerTask = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	private LinkedList<ToLetter> toLetterList = new LinkedList<ToLetter>();
	
	public ToLetterCarrier( SocketChannel fromSC, 
			AbstractMessage inputMessage,
			SocketResourceManagerIF socketResourceManager,
			PersonalLoginManagerIF personalMemberManager, 
			MessageProtocolIF messageProtocol,
			ClassLoader classLoaderOfServerTask,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.fromSC = fromSC;		
		this.inputMessage = inputMessage;
		this.socketResourceManager = socketResourceManager;
		this.personalMemberManager = personalMemberManager;
		this.messageProtocol = messageProtocol;
		this.classLoaderOfServerTask = classLoaderOfServerTask;
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

	private void doAddOutputMessage(SocketChannel toSC,			
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol) throws InterruptedException {
		String messageIDToClient = outputMessage.getMessageID();
	
		List<WrapBuffer> wrapBufferList = null;		
		
		MessageCodecIF messageServerCodec = null;
		try {
			messageServerCodec = serverObjectCacheManager.getServerMessageCodec(classLoaderOfServerTask, messageIDToClient);
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a server output message codec::").append(e.getMessage()).toString();
			
			log.warn(errorMessage);
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a server output message codec::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
		AbstractMessageEncoder messageEncoder = null;
		try {
			messageEncoder = messageServerCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a output message encoder::").append(e.getMessage()).toString();
			
			log.warn(errorMessage);
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a output message encoder::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
	
		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/
		
		try {
			wrapBufferList = messageProtocol.M2S(outputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(NoMoreDataPacketBufferException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;			
		} catch (Exception | Error e) {	
			String errorMessage = new StringBuilder("unknown error::fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			
			log.warn(errorReason, e);
			
			doAddOutputErrorMessageToToLetterList(toSC, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
		
		ToLetter toLetter = new ToLetter(toSC, 
				outputMessage.getMessageID(),
				outputMessage.messageHeaderInfo.mailboxID,
				outputMessage.messageHeaderInfo.mailID, wrapBufferList);
		
		toLetterList.add(toLetter);
	}

	private void doAddOutputErrorMessageToToLetterList(SocketChannel toSC,
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
		} catch (Exception e) {
			String errorMessage = String.format("unknown error::fail to build the toLetter of SelfExn[%s], toSC[%d]::%s", 
					selfExnRes.toString(),
					toSC.hashCode(),
					e.getMessage());
			log.warn(errorMessage, e);
			return;
		}
		toLetterList.add(toLetterOfSelfExn);
	}

	/*private void doAddAsynOutputMessage(AbstractMessage outputMessage, SocketChannel toSC) throws InterruptedException {		
		SocketResource socketResource = 
				socketResourceManager.getSocketResource(toSC);
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = socketResource.getServerMailID();		
		
		doAddOutputMessage(toSC, outputMessage, messageProtocol);
	}*/
	
	public void addBypassOutputMessage(AbstractMessage bypassOutputMessage) throws InterruptedException {		
		if (inputMessage.messageHeaderInfo.mailboxID == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			addAsynOutputMessage(bypassOutputMessage);
		} else {
			addSyncOutputMessage(bypassOutputMessage);
		}
	}

	public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
		if (null == syncOutputMessage) {
			throw new IllegalArgumentException("the parameter syncOutputMessage is null");
		}
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == inputMessage.messageHeaderInfo.mailboxID) {			
			throw new IllegalArgumentException("the synchronous output message can't be added becase the inputMessage is a asynchronous message");
		}
		
		if (null != this.syncOutputMessage) {
			throw new IllegalArgumentException("the synchronous output message can't be added becase another synchronous message is already registered in the toLetter list");	
		}
		
		this.syncOutputMessage =  syncOutputMessage;
		
		syncOutputMessage.messageHeaderInfo = inputMessage.messageHeaderInfo;		
		doAddOutputMessage(fromSC, syncOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(AbstractMessage asynOutputMessage) throws InterruptedException {
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		SocketResource socketResource = 
				socketResourceManager.getSocketResource(fromSC);
		asynOutputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		asynOutputMessage.messageHeaderInfo.mailID = socketResource.getServerMailID();	
		
		doAddOutputMessage(fromSC, asynOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(AbstractMessage asynOutputMessage, String loginUserID) throws InterruptedException, LoginUserNotFoundException {
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		if (null == loginUserID) {
			throw new IllegalArgumentException("the parameter loginUserID is null");
		}
		
		SocketChannel toSC = personalMemberManager.getSocketChannel(loginUserID);
		
		if (null == toSC) {
			String errorMessage = String.format("the parameter loginUserID[%s] is not a member or not a login user", loginUserID);
			throw new LoginUserNotFoundException(errorMessage);
		}
		
		SocketResource socketResource = 
				socketResourceManager.getSocketResource(toSC);
		
		asynOutputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		asynOutputMessage.messageHeaderInfo.mailID = socketResource.getServerMailID();		
		
		doAddOutputMessage(toSC, asynOutputMessage, messageProtocol);
	}
	
	public void putAllInputMessageToOutputMessageQueue() throws InterruptedException {		
		while (! toLetterList.isEmpty()) {
			ToLetter toLetter = toLetterList.removeFirst();
			
			SocketResource socketResource = 
					socketResourceManager.getSocketResource(toLetter.getToSocketChannel());
			
			OutputMessageWriterIF outputMessageWriter = socketResource.getOutputMessageWriter();			
			
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e) {
				log.warn("the output message[{}] discarded due to an InterruptedException, fromSC hashcode={}, inputMessage={}", 
						toLetter.toString(),
						fromSC.hashCode(),
						inputMessage.toString());
				
				while (! toLetterList.isEmpty()) {
					toLetter = toLetterList.removeFirst();
					log.warn("the output message[{}] discarded due to an InterruptedException, fromSC hashcode={}, inputMessage={}", 
							toLetter.toString(),
							fromSC.hashCode(),
							inputMessage.toString());
					
				}
				
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
			log.warn("the SelfExn output message[{}] discarded due to an InterruptedException, fromSC hashcode={}, inputMessage={}", 
					toLetterOfSelfExn.toString(),
					fromSC.hashCode(),
					wrapReadableMiddleObject.toString());
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("fail to put the SelfExn output message into the output message queue, fromSC hashcode={}, inputMessage={}", 
					toLetterOfSelfExn.toString(),
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
