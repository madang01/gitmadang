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
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReader;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 소켓 채널 블락킹 모드가 넌블락인 비동기 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널관련 서비스관련 구현을 하는 클래스를 연결 클래스로 명명한다.
 * 
 * @author Won Jonghoon
 */
public abstract class AbstractAsynConnection extends AbstractConnection {
	protected AsynSocketResourceIF asynSocketResource = null;

	public AbstractAsynConnection(String projectName, String host, int port, long socketTimeOut,
			AsynSocketResourceIF asynSocketResource,
			MessageProtocolIF messageProtocol, 
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, messageProtocol, clientObjectCacheManager);
		
		this.asynSocketResource = asynSocketResource;
		asynSocketResource.setOwnerAsynConnection(this);
	}
	
	protected void doReleaseSocketResources() {
		/**  nothing */
	}

	protected void openSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSelectableChannel = serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);		
	
		StringBuilder infoBuilder = null;
	
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] create a new asyn connection[");
		infoBuilder.append(serverSC.hashCode());
		infoBuilder.append("]");
		
		log.info(infoBuilder.toString());
	}

	protected void doConnect() throws IOException {
		Selector connectionEventOnlySelector = Selector.open();
	
		try {
			serverSC.register(connectionEventOnlySelector, SelectionKey.OP_CONNECT);
	
			InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
			if (! serverSC.connect(remoteAddr)) {
				int numberOfKeys = connectionEventOnlySelector.select(socketTimeOut);
	
				log.info("numberOfKeys={}", numberOfKeys);
	
				Iterator<SelectionKey> selectionKeyIterator = connectionEventOnlySelector.selectedKeys().iterator();
				if (!selectionKeyIterator.hasNext()) {
	
					String errorMessage = String.format("1.the socket[sc hascode=%d] timeout", serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}
				
				selectionKeyIterator.remove();
	
				if (! serverSC.finishConnect()) {
					String errorMessage = String.format("the socket[sc hascode=%d] has an error pending",
							serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}
			}
		} finally {
			connectionEventOnlySelector.close();
		}
	
		// registerSocketToReadOnlySelector();
		asynSocketResource.getOutputMessageReader().registerAsynConnection(this);
	
		StringBuilder infoBuilder = null;
	
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] asyn connection[");		
		infoBuilder.append(serverSC.hashCode());
		infoBuilder.append("]");
		log.info(new StringBuilder(infoBuilder.toString()).append(" connected").toString());
	}

	abstract public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException;
	
	public SocketOutputStream getSocketOutputStream() {
		return asynSocketResource.getSocketOutputStream();
	}
	
	
	/**
	 * <pre>
	 * 비동기 소켓에서 실질적인 자원 해제 메소드로 {@link OutputMessageReader#run()} 에서 소켓이 닫혔을때 딱 1번 호출된다.
	 * 이는 OP_READ 전용 selector 는 소켓이 닫히면 OP_READ 이벤트를 발생하는 특성을 이용한것이다.
	 * </pre> 
	 */
	public void releaseSocketResources() {
		asynSocketResource.releaseSocketResources();
	}

	
	public void sendAsynInputMessage(AbstractMessage inObj)
			throws NotSupportedException, IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			InterruptedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// connectServer();

		ClassLoader classLoader = inObj.getClass().getClassLoader();

		inObj.messageHeaderInfo.mailboxID = AsynPublicMailbox.getMailboxID();
		inObj.messageHeaderInfo.mailID = AsynPublicMailbox.getNextMailID();

		List<WrapBuffer> wrapBufferListOfInputMessage = buildReadableWrapBufferList(classLoader, inObj);
		
		ToLetter toLetter = new ToLetter(serverSC, inObj.getMessageID(), 
				inObj.messageHeaderInfo.mailboxID, 
				inObj.messageHeaderInfo.mailID, 
				wrapBufferListOfInputMessage);
		
		
		asynSocketResource.getInputMessageWriter().putIntoQueue(toLetter);

		// writeInputMessageToSocketChannel(serverSC, wrapBufferListOfInputMessage);

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));
	}
	

	
	
	public int hashCode() {
		return serverSC.hashCode();
	}
}
