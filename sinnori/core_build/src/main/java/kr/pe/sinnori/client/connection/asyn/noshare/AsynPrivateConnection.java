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
package kr.pe.sinnori.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;


public class AsynPrivateConnection extends AbstractAsynConnection {

	private boolean isQueueIn = true;
	
	
	private final AsynPrivateMailbox asynPrivateMailbox = new AsynPrivateMailbox(1, socketTimeOut);

	public AsynPrivateConnection(ConnectionFixedParameter connectionFixedParameter,
			AsynSocketResourceIF asynSocketResource)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(connectionFixedParameter,
				asynSocketResource);

		// log.info(String.format("project[%s] NoShareAsynConnection 생성자 end", projectName));
	}

	/**
	 * 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부 반환 메소드
	 * 
	 * @return 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부
	 */
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
		// log.info("NoShareAsynConnection[{}] queue in", this.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
		// log.info("NoShareAsynConnection[{}] queue out", this.hashCode());
	}

		
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws InterruptedException, NoMoreDataPacketBufferException,
			DynamicClassCallException, ServerTaskException, AccessDeniedException, BodyFormatException, IOException {
		// log.info("inputMessage=[%s]", inputMessage.toString());	

		ClassLoader classLoader = inObj.getClass().getClassLoader();

		WrapReadableMiddleObject wrapReadableMiddleObject = null;

		inObj.messageHeaderInfo.mailboxID = asynPrivateMailbox.getMailboxID();
		inObj.messageHeaderInfo.mailID = asynPrivateMailbox.getMailID();

		List<WrapBuffer> wrapBufferListOfInputMessage = clientMessageUtility.buildReadableWrapBufferList(classLoader, inObj);
		
		ToLetter toLetter = new ToLetter(serverSC, inObj.getMessageID(), 
				inObj.messageHeaderInfo.mailboxID, 
				inObj.messageHeaderInfo.mailID, 
				wrapBufferListOfInputMessage);
		InputMessageWriterIF  inputMessageWriter  = asynSocketResource.getInputMessageWriter();
		inputMessageWriter.putIntoQueue(toLetter);
		
		try {
			wrapReadableMiddleObject = asynPrivateMailbox.getSyncOutputMessage();
		} catch(SocketTimeoutException e) {			
			String errorMessage = new StringBuilder("timeout for the input message[")
					.append(e.getMessage()).append("][")
					.append(inObj.toString()).append("]").toString();
			throw new SocketTimeoutException(errorMessage);
		}		

		AbstractMessage outObj = clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject);
		if (outObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}

		return outObj;
	}
	
	public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException {
		// FIXME!
		// log.info("fromLetter={}", fromLetter.toString());
		
		
		WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
		
		if (wrapReadableMiddleObject.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			try {
				asynSocketResource.getClientExecutor().putAsynOutputMessage(fromLetter);
			} catch (InterruptedException e) {				
				log.warn("인터럽트 발생에 의한 비동기 출력 메시지[{}] 버림", fromLetter.toString());
			
				wrapReadableMiddleObject.closeReadableMiddleObject();
			
				
				throw e;
			}
		} else {
			if (isInQueue()) {
				String errorMessage = String.format(
						"연결 클래스가 큐 대기중 상태입니다. fromLetter=[%s]",
						fromLetter.toString());

				log.warn(errorMessage);
				return;
			}

			try {
				asynPrivateMailbox.putSyncOutputMessage(fromLetter);
			} catch (InterruptedException e) {
				log.warn("인터럽트 발생에 의한 동기 출력 메시지[{}] 버림", fromLetter.toString());
				
				wrapReadableMiddleObject.closeReadableMiddleObject();
				
				throw e;
			}
		}
	}

	@Override
	public void finalize() {
		try {
			serverSC.close();
		} catch (IOException e) {
		}
		
		noticeThisConnectionWasRemovedFromReadyOnleySelector();
		
		if (! isQueueIn) {
			log.warn("큐로 복귀 못한 비동기 비공유 연결[{}]", hashCode());
		}
	}			
}
