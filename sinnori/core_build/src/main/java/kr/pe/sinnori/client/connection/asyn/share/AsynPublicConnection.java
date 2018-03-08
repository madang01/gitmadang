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

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;
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


public class AsynPublicConnection extends AbstractAsynConnection {
	private AsynPrivateMailboxMapperIF asynPrivateMailboxMapper = null;
	private AsynPrivateMailboxPoolIF asynPrivateMailboxPool = null;

	public AsynPublicConnection(String projectName, String host, int port, long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility, AsynPrivateMailboxPoolIF asynPrivateMailboxPool,
			AsynSocketResourceIF asynSocketResource)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, clientMessageUtility, asynSocketResource);

		this.asynPrivateMailboxPool = asynPrivateMailboxPool;

		this.asynPrivateMailboxMapper = asynPrivateMailboxPool.getAsynPrivateMailboxMapper();

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
			AsynPrivateMailboxIF asynPrivateMailbox = null;
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

	// @Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws InterruptedException, NoMoreDataPacketBufferException, DynamicClassCallException, 
			ServerTaskException, AccessDeniedException, BodyFormatException, IOException {
		ClassLoader classLoader = inObj.getClass().getClassLoader();
		WrapReadableMiddleObject wrapReadableMiddleObject = null;

		AsynPrivateMailboxIF asynPrivateMailbox = asynPrivateMailboxPool.poll(socketTimeOut);

		if (null == asynPrivateMailbox) {
			String errorMessage = String.format("입력 메시지[%s] 처리시 지정한 시간안에 개인 메일함 가져오기 실패", inObj.getMessageID());
			throw new SocketTimeoutException(errorMessage);
		}

		try {

			inObj.messageHeaderInfo.mailboxID = asynPrivateMailbox.getMailboxID();
			inObj.messageHeaderInfo.mailID = asynPrivateMailbox.getMailID();

			List<WrapBuffer> wrapBufferListOfInputMessage = clientMessageUtility
					.buildReadableWrapBufferList(classLoader, inObj);

			// writeInputMessageToSocketChannel(serverSC, wrapBufferListOfInputMessage);
			ToLetter toLetter = new ToLetter(serverSC, inObj.getMessageID(), inObj.messageHeaderInfo.mailboxID,
					inObj.messageHeaderInfo.mailID, wrapBufferListOfInputMessage);
			asynSocketResource.getInputMessageWriter().putIntoQueue(toLetter);

			wrapReadableMiddleObject = asynPrivateMailbox.getSyncOutputMessage();

		} finally {
			asynPrivateMailboxPool.offer(asynPrivateMailbox);
		}

		AbstractMessage outObj = clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject);

		if (outObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}

		return outObj;
	}

	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {

		}

		noticeThisConnectionWasRemovedFromReadyOnleySelector();

		// releaseResources();
		log.warn("소멸::projectName[{}], ShareAsynConnection[{}]", projectName, serverSC.hashCode());
	}

}
