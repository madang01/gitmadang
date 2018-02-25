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
import java.util.List;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
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

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스.<br/>
 * 참고) 비공유 방식은 큐로 관리 되기에 비공유 방식의 동기 연결 클래스는 큐 인터페이스를 구현하고 있다<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareAsynConnection extends AbstractAsynConnection {

	private boolean isQueueIn = true;
	
	
	private final AsynPrivateMailbox asynPrivateMailbox = new AsynPrivateMailbox(1, socketTimeOut);

	public NoShareAsynConnection(String projectName, String host, int port, long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility,
			AsynSocketResourceIF asynSocketResource)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, 
				clientMessageUtility,
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

		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

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
		asynSocketResource.getInputMessageWriter().putIntoQueue(toLetter);
		
		
		wrapReadableMiddleObject = asynPrivateMailbox.getSyncOutputMessage();

		/*
		 * String messageID = letterFromServer.getMessageID(); int mailboxID =
		 * letterFromServer.getMailboxID(); int mailID = letterFromServer.getMailID();
		 * Object middleReadObj = letterFromServer.getMiddleReadObj();
		 */

		AbstractMessage outObj = clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject);
		if (outObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}

		endTime = new java.util.Date().getTime();
		log.info(String.format("2.시간차=[%d]", (endTime - startTime)));

		return outObj;
	}
	
	public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException {
		WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
		
		if (wrapReadableMiddleObject.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			try {
				asynSocketResource.getClientExecutor().putIntoQueue(fromLetter);
			} catch (InterruptedException e) {
				log.warn("인터럽트 발생에 의한 비동기 출력 메시지[{}] 버림", fromLetter.toString());
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
				asynPrivateMailbox.putToSyncOutputMessageQueue(fromLetter);
			} catch (InterruptedException e) {
				log.warn("인터럽트 발생에 의한 동기 출력 메시지[{}] 버림", fromLetter.toString());
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
