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
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;
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

	
	public ShareAsynConnection(String projectName, 
			String host, int port,
			long socketTimeOut,  
			AsynPrivateMailboxMapper asynPrivateMailboxMapper,
			AsynSocketResourceIF asynSocketResource,
			MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, 
				asynSocketResource, messageProtocol, clientObjectCacheManager);


		this.asynPrivateMailboxMapper = asynPrivateMailboxMapper;
		this.asynPrivateMailboxPool = new AsynPrivateMailboxPool(asynPrivateMailboxMapper);
				
		
		log.info(String.format("project[%s] ShareAsynConnection[%d] 생성자 end", projectName, serverSC.hashCode()));
	}

	

	@Override
	public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException {
		WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
		
		if (wrapReadableMiddleObject.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			try {
				asynSocketResource.getClientExecutor().putIntoQueue(fromLetter);
			} catch (InterruptedException e) {
				log.warn("인터럽트 발생에 의한 비동기 출력 메시지[{}] 버림", fromLetter.toString());
				throw e;
			}
		} else {

			int mailboxID = wrapReadableMiddleObject.getMailboxID();
			AsynPrivateMailbox asynPrivateMailbox = null;
			try {
				asynPrivateMailbox = asynPrivateMailboxMapper.getAsynMailbox(mailboxID);
			} catch (IndexOutOfBoundsException e) {
				log.warn("비동기 출력 메시지[{}]와 매치하는 메일 박스가 없어 버림", fromLetter.toString());
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

	//@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		WrapReadableMiddleObject receivedLetter = null;

		AsynPrivateMailbox asynPrivateMailbox = asynPrivateMailboxPool.poll(socketTimeOut);

		if (null == asynPrivateMailbox) {
			String errorMessage = String.format("입력 메시지[%s] 처리시 지정한 시간안에 개인 메일함 가져오기 실패", inObj.getMessageID());
			throw new SocketTimeoutException(errorMessage);
		}

		try {

			inObj.messageHeaderInfo.mailboxID = asynPrivateMailbox.getMailboxID();
			inObj.messageHeaderInfo.mailID = asynPrivateMailbox.getMailID();

			List<WrapBuffer> wrapBufferListOfInputMessage = buildReadableWrapBufferList(classLoader, inObj);

			// writeInputMessageToSocketChannel(serverSC, wrapBufferListOfInputMessage);
			ToLetter toLetter = new ToLetter(serverSC, inObj.getMessageID(), 
					inObj.messageHeaderInfo.mailboxID, 
					inObj.messageHeaderInfo.mailID, 
					wrapBufferListOfInputMessage);
			asynSocketResource.getInputMessageWriter().putIntoQueue(toLetter);

			receivedLetter = asynPrivateMailbox.getSyncOutputMessage();

		} finally {
			asynPrivateMailboxPool.add(asynPrivateMailbox);
		}

		AbstractMessage outObj = buildOutputMessage(classLoader, receivedLetter);

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));

		return outObj;
	}
	

	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {
			
		}
		
		releaseSocketResources();
		
		// releaseResources();
		log.warn("소멸::projectName[{}], ShareAsynConnection[{}]", 
				projectName, serverSC.hashCode());	
	}

}
