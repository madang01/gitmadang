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

import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;

import kr.pe.codda.common.classloader.MessageCodecManagerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.LoginUserNotFoundException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.ProjectLoginManagerIF;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class ToLetterCarrier {
	private InternalLogger log = InternalLoggerFactory.getInstance(ToLetterCarrier.class);
	
	private AcceptedConnection fromAcceptedConnection = null;
	private AbstractMessage inputMessage;
	private ProjectLoginManagerIF projectLoginManager = null;
	
	private AbstractMessage syncOutputMessage = null;	
	
	
	private MessageProtocolIF messageProtocol = null;
	private MessageCodecManagerIF messageCodecManager = null;
	
	// private LinkedList<ToLetter> toLetterList = new LinkedList<ToLetter>();
	
	public ToLetterCarrier(AcceptedConnection fromAcceptedConnection,
			AbstractMessage inputMessage,
			ProjectLoginManagerIF projectLoginManager,
			MessageProtocolIF messageProtocol,
			MessageCodecManagerIF messageCodecManager) {
		this.fromAcceptedConnection = fromAcceptedConnection;
		this.inputMessage = inputMessage;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		this.messageCodecManager = messageCodecManager;
	}

	private static SelfExnRes buildSelfExn(int mailboxIDOfSelfExn, 
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

	private void doAddOutputMessage(AcceptedConnection toAcceptedConnection,
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol) throws InterruptedException {
		String messageIDToClient = outputMessage.getMessageID();
	
		ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue = null;		
		
		MessageCodecIF messageServerCodec = null;
		try {
			messageServerCodec = messageCodecManager.getMessageCodec(messageIDToClient);
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a server output message codec::").append(e.getMessage()).toString();
			
			log.warn(errorMessage);
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a server output message codec::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
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
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a output message encoder::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = errorMessage;
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
	
	
		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/
		
		try {
			outputMessageWrapBufferQueue = messageProtocol.M2S(outputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(NoMoreDataPacketBufferException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			log.warn(errorReason);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;			
		} catch (Exception | Error e) {	
			String errorMessage = new StringBuilder("unknown error::fail to build a output message stream[")
					.append(outputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			
			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(BodyFormatException.class);
			String errorReason = errorMessage;
			
			
			log.warn(errorReason, e);
			
			doAddOutputErrorMessage(toAcceptedConnection, errorType, errorReason, outputMessage, messageProtocol);
			return;
		}
		
		
		toAcceptedConnection.addOutputMessage(outputMessage, outputMessageWrapBufferQueue);
	}

	private void doAddOutputErrorMessage(AcceptedConnection toAcceptedConnection,
			SelfExn.ErrorType errorType, 
			String errorReason,
			AbstractMessage outputMessage,			
			MessageProtocolIF messageProtocol) throws InterruptedException {
		SelfExnRes selfExnRes = buildSelfExn( 
				outputMessage.messageHeaderInfo.mailboxID,
				outputMessage.messageHeaderInfo.mailID,
				outputMessage.getMessageID(),
				errorType,
				errorReason);
		
		ArrayDeque<WrapBuffer> wrapBufferListOfSelfExn = null;
		try {
			wrapBufferListOfSelfExn = messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.SELFEXN_ENCODER);
			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to build a output stream of the output message SelfExnRes[")
					.append(selfExnRes.toString())
					.append("] to send to to-client[")
					.append(toAcceptedConnection.toSimpleInfomation())
					.append("] because of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			return;
		}
		
		
		toAcceptedConnection.addOutputMessage(outputMessage, wrapBufferListOfSelfExn);
	}

	
	
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
		doAddOutputMessage(fromAcceptedConnection, syncOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(AbstractMessage asynOutputMessage) throws InterruptedException {
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		asynOutputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		asynOutputMessage.messageHeaderInfo.mailID = fromAcceptedConnection.getServerMailID();	
		
		doAddOutputMessage(fromAcceptedConnection, asynOutputMessage, messageProtocol);
	}
	
	public void addAsynOutputMessage(String toLoginID, AbstractMessage asynOutputMessage) throws InterruptedException, LoginUserNotFoundException {
		if (null == toLoginID) {
			throw new IllegalArgumentException("the parameter toLoginID is null");
		}
		
		if (null == asynOutputMessage) {
			throw new IllegalArgumentException("the parameter asynOutputMessage is null");
		}
		
		SelectionKey loginIDSelectionKey = projectLoginManager.getSelectionKey(toLoginID);
		
		if (null == loginIDSelectionKey) {
			String errorMessage = String.format("the user who has the parameter loginUserID[%s] is not a member or doens't login", toLoginID);
			throw new LoginUserNotFoundException(errorMessage);
		}
		
		AcceptedConnection loignUserAcceptedConnection = projectLoginManager.getAcceptedConnection(loginIDSelectionKey);
		
		if (null == loignUserAcceptedConnection) {
			String errorMessage = String.format("the user who has the parameter loginUserID[%s] was disconnected", toLoginID);
			throw new LoginUserNotFoundException(errorMessage);
		}
		
		asynOutputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		asynOutputMessage.messageHeaderInfo.mailID = loignUserAcceptedConnection.getServerMailID();		
		
		doAddOutputMessage(loignUserAcceptedConnection, asynOutputMessage, messageProtocol);
	}
	
	public static void putInputErrorMessageToOutputMessageQueue(SelfExn.ErrorType errorType,
			String errorReason,			
			ReadableMiddleObjectWrapper readableMiddleObjectWrapper,
			AcceptedConnection fromAcceptedConnection,
			MessageProtocolIF messageProtocol) throws InterruptedException {
		
		if (null == errorType) {
			throw new IllegalArgumentException("the parameter errorType is null");
		}		
		
		if (null == errorReason) {
			throw new IllegalArgumentException("the parameter errorReason is null");
		}
		
		if (null == readableMiddleObjectWrapper) {
			throw new IllegalArgumentException("the parameter readableMiddleObjectWrapper is null");
		}
		
		if (null == fromAcceptedConnection) {
			throw new IllegalArgumentException("the parameter fromSocketResource is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		int mailboxIDOfSelfExn = readableMiddleObjectWrapper.getMailboxID();
		int mailIDOfSelfExn = readableMiddleObjectWrapper.getMailID();
		String errorMessageID = readableMiddleObjectWrapper.getMessageID();
		
		SelfExnRes selfExnRes = buildSelfExn(mailboxIDOfSelfExn, 
				mailIDOfSelfExn, 
				errorMessageID, 
				errorType,
				errorReason);

		ArrayDeque<WrapBuffer> wrapBufferListOfSelfExn = null;
		try {
			wrapBufferListOfSelfExn = messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.SELFEXN_ENCODER);
			
		} catch (Exception e) {
			InternalLogger log = InternalLoggerFactory.getInstance(ToLetterCarrier.class);
			String errorMessage = new StringBuilder()
					.append("fail to build a output stream of the output message SelfExnRes[")
					.append(selfExnRes.toString())
					.append("] to send to from-client[")
					.append(fromAcceptedConnection.toSimpleInfomation())
					.append("] because of unknown error, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			return;
		}
		
		fromAcceptedConnection.addOutputMessage(selfExnRes, wrapBufferListOfSelfExn);
	}	
}
