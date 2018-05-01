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
package kr.pe.sinnori.client.connection.asyn;

import java.io.IOException;
import java.util.ArrayDeque;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReader;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;


public abstract class AbstractAsynConnection extends AbstractConnection implements IOEAsynConnectionIF {
	protected AsynSocketResourceIF asynSocketResource = null;

	public AbstractAsynConnection(ConnectionFixedParameter connectionFixedParameter, AsynSocketResourceIF asynSocketResource)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(connectionFixedParameter);
		
		if (null == asynSocketResource) {
			throw new IllegalArgumentException("the parameter asynSocketResource is null");
		}
		

		this.asynSocketResource = asynSocketResource;

		asynSocketResource.setOwnerAsynConnection(this);
		asynSocketResource.getInputMessageWriter().registerAsynConnection(this);
		asynSocketResource.getClientExecutor().registerAsynConnection(this);
		asynSocketResource.getOutputMessageReader().registerAsynConnection(this);
	}

	protected void doReleaseSocketResources() {
		/** nothing */
	}

	

	abstract public void putToOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;

	public SocketOutputStream getSocketOutputStream() {
		return asynSocketResource.getSocketOutputStream();
	}

	/**
	 * <pre>
	 * 비동기 소켓에서 실질적인 자원 해제 메소드로 {@link OutputMessageReader#run()} 에서 소켓이 닫혔을때 딱 1번 호출된다.
	 * 이는 OP_READ 전용 selector 는 소켓이 닫히면 OP_READ 이벤트를 발생하는 특성을 이용한것이다.
	 * </pre>
	 */
	public void noticeThisConnectionWasRemovedFromReadyOnleySelector() {
		asynSocketResource.releaseSocketResources();
	}

	public void sendAsynInputMessage(AbstractMessage inObj) throws NotSupportedException, InterruptedException,
			DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		ClassLoader classLoader = inObj.getClass().getClassLoader();

		inObj.messageHeaderInfo.mailboxID = AsynMailbox.getMailboxID();
		inObj.messageHeaderInfo.mailID = AsynMailbox.getNextMailID();

		ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = clientMessageUtility.buildReadableWrapBufferList(classLoader,
				inObj);

		ToLetter toLetter = new ToLetter(serverSC, inObj.getMessageID(), inObj.messageHeaderInfo.mailboxID,
				inObj.messageHeaderInfo.mailID, wrapBufferListOfInputMessage);

		asynSocketResource.getInputMessageWriter().putIntoQueue(toLetter);
	}

	
}
