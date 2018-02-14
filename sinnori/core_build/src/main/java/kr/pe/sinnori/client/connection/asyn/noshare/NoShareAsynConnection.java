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

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

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

	public NoShareAsynConnection(String projectName, int index, String host, int port, long socketTimeOut,
			boolean whetherToAutoConnect, AsynPublicMailbox asynPublicMailbox,
			InputMessageWriterIF inputMessageWriter,
			OutputMessageReaderIF outputMessageReader, 
			SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager, ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException {
		super(projectName, index, host, port, socketTimeOut, whetherToAutoConnect, 
				asynPublicMailbox,
				outputMessageReader, 
				socketOutputStream,
				messageProtocol, dataPacketBufferQueueManager, clientObjectCacheManager);

		// this.messageManger = messageManger;
		// this.outputMessageQueue = outputMessageQueue;
		// mailbox = new AsynPrivateMailbox(this, 1, inputMessageQueue,
		// outputMessageQueueQueueManger);

		/*
		 * try { reopenSocketChannel(); } catch (IOException e) { String errorMessage =
		 * String.format(
		 * "project[%s] NoShareAsynConnection[%d], fail to config a socket channel",
		 * projectName, index); log.error(errorMessage, e); System.exit(1); }
		 */

		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				connectServer();
			} catch (IOException e) {
				log.warn(String.format("project[%s] NoShareAsynConnection[%d] fail to connect server", projectName,
						index), e);
				// System.exit(1);
			}
		}

		log.info(String.format("project[%s] NoShareAsynConnection[%d] 생성자 end", projectName, index));
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
	public void queueIn() {
		isQueueIn = true;
		log.info("get NoShareAsynConnection[{}] from the connection queue", monitor.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	public void queueOut() {
		isQueueIn = false;
		log.info("get NoShareAsynConnection[{}] from the connection queue", monitor.hashCode());
	}

	/**
	 * throws ServerNotReadyException, SocketTimeoutException,
	 * NoMoreDataPacketBufferException, BodyFormatException,
	 * DynamicClassCallException, ServerTaskException, AccessDeniedException
	 */

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException {

		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// log.info("inputMessage=[%s]", inputMessage.toString());

		connectServer();

		ClassLoader classLoader = inObj.getClass().getClassLoader();

		WrapReadableMiddleObject receivedLetter = null;

		inObj.messageHeaderInfo.mailboxID = asynPrivateMailbox.getMailboxID();
		inObj.messageHeaderInfo.mailID = asynPrivateMailbox.getMailID();

		List<WrapBuffer> wrapBufferListOfInputMessage = getWrapBufferListOfInputMessage(classLoader, inObj);
		
		writeInputMessageToSocketChannel(wrapBufferListOfInputMessage);
		
		receivedLetter = asynPrivateMailbox.getSyncOutputMessage();

		/*
		 * String messageID = letterFromServer.getMessageID(); int mailboxID =
		 * letterFromServer.getMailboxID(); int mailID = letterFromServer.getMailID();
		 * Object middleReadObj = letterFromServer.getMiddleReadObj();
		 */

		AbstractMessage outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);

		endTime = new java.util.Date().getTime();
		log.info(String.format("2.시간차=[%d]", (endTime - startTime)));

		return outObj;
	}	

	@Override
	public void putToOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject) {
		if (wrapReadableMiddleObject.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			// boolean result = false;

			try {
				// result = asynOutputMessageQueue.offer(wrapReadableMiddleObject,
				// socketTimeOut, TimeUnit.MILLISECONDS);
				asynPublicMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
			} catch (InterruptedException e) {
				// FIXME!, 메시지 버린것에 대한 로그 남기기 필요함
			}
		} else {
			if (isInQueue()) {
				String errorMessage = String.format(
						"연결 클래스가 사용중이 아닙니다. 연결 클래스 큐 대기중, serverConnection=[%s], receivedLetter=[%s]",
						this.getSimpleConnectionInfo(), wrapReadableMiddleObject.toString());

				log.warn(errorMessage);
				return;
			}

			try {
				asynPrivateMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
			} catch (InterruptedException e) {
				// FIXME!, 메시지 버린것에 대한 로그 남기기 필요함
			}
		}
	}

	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource =
		// getMessageInputStreamResource();
		releaseResources();
		log.warn(String.format("NoShareAsynConnection 소멸::[%s]", toString()));
	}

	

}
