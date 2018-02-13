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
package kr.pe.sinnori.client.connection.asyn.share;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
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
 * 클라이언트 공유 방식의 비동기 연결 클래스.<br/>
 * 참고) 공유 방식은 목록으로 관리되며 순차적으로 공유 방식의 비동기 연결 클래스를 배정한다. <br/>
 * 이렇게 배정 받은 공유 방식의 비동기 연결 클래스는 메시지 송수신 순간에 <br/>
 * 메일함 큐에서 메일함을 할당받아 메일함을 통해서 메시지 교환을 수행한다.<br/>
 * 자세한 내용은 기술 문서를 참고 하세요.<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @see AsynPrivateMailbox
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnection extends AbstractAsynConnection {
	private AsynPrivateMailboxMapper asynPrivateMailboxMapper = null;
	private AsynPrivateMailboxPool asynPrivateMailboxPool = null;

	
	public ShareAsynConnection(String projectName, int index, 
			String host, int port,
			long socketTimeOut, 
			boolean whetherToAutoConnect, 
			AsynPublicMailbox asynPublicMailbox,
			AsynPrivateMailboxMapper asynPrivateMailboxMapper,
			SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol,
			OutputMessageReaderIF outputMessageReader,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException {
		super(projectName, index, host, port, socketTimeOut, whetherToAutoConnect, asynPublicMailbox,
				outputMessageReader, 
				socketOutputStream, 
				messageProtocol, dataPacketBufferQueueManager, clientObjectCacheManager);

		this.asynPrivateMailboxMapper = asynPrivateMailboxMapper;

		// this.messageManger = messageManger;
		this.asynPrivateMailboxPool = new AsynPrivateMailboxPool(asynPrivateMailboxMapper);
				
		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				connectServer();
			} catch (IOException e) {
				log.warn(
						String.format("project[%s] ShareAsynConnection[%d] fail to connect server", projectName, index),
						e);
				// System.exit(1);
			}
		}

		log.info(String.format("project[%s] ShareAsynConnection[%d] 생성자 end", projectName, index));
	}

	

	@Override
	public void putToOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject) {
		if (wrapReadableMiddleObject.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			try {
				asynPublicMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
			} catch (InterruptedException e) {
				// FIXME!, 메시지 버린것에 대한 로그 남기기 필요함
			}
		} else {
			
			int mailboxID = wrapReadableMiddleObject.getMailboxID();
			AsynPrivateMailbox asynPrivateMailbox = null;
			try {
				asynPrivateMailbox = asynPrivateMailboxMapper.getAsynMailbox(mailboxID);
			} catch(IndexOutOfBoundsException e) {
				// FIXME!, 메시지 버린것에 대한 로그 남기기 필요함
			}

			try {
				asynPrivateMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
			} catch (InterruptedException e) {
				// FIXME!, 메시지 버린것에 대한 로그 남기기 필요함
			}
		}
	}

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws IOException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, 
			AccessDeniedException, InterruptedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		connectServer();

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		WrapReadableMiddleObject receivedLetter = null;
		

		AsynPrivateMailbox asynPrivateMailbox = asynPrivateMailboxPool.poll(socketTimeOut);
		
		if (null == asynPrivateMailbox) {
			String errorMessage = String.format("입력 메시지[%s] 처리시 지정한 시간안에 개인 메일함 가져오기 실패", 
					inObj.getMessageID());
			throw new SocketTimeoutException(errorMessage);
		}
			
		try {
			
			inObj.messageHeaderInfo.mailboxID = asynPrivateMailbox.getMailboxID();
			inObj.messageHeaderInfo.mailID = asynPrivateMailbox.getMailID();

			List<WrapBuffer> wrapBufferListOfInputMessage = getWrapBufferListOfInputMessage(classLoader, inObj);
			
							
			writeInputMessageToSocketChannel(wrapBufferListOfInputMessage);
			
			receivedLetter = asynPrivateMailbox.getSyncOutputMessage();				
			
		} finally {
			asynPrivateMailboxPool.add(asynPrivateMailbox);
		}
		
		
		AbstractMessage outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));

		
		return outObj;
	}
	

	@Override
	public void finalize() {
		socketOutputStream.close();
		log.warn("소멸::projectName[{}], ShareAsynConnection[{}], sc hashCode={}", 
				projectName, index, serverSC.hashCode());
	}

}
