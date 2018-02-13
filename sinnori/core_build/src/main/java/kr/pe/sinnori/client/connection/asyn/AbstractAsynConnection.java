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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderThread;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

/**
 * 클라이언트 소켓 채널 블락킹 모드가 넌블락인 비동기 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널관련 서비스관련 구현을 하는 클래스를 연결 클래스로 명명한다.
 * 
 * @author Won Jonghoon
 */
public abstract class AbstractAsynConnection extends AbstractConnection {
	protected SocketChannel serverSC = null;

	protected AsynPublicMailbox asynPublicMailbox = null;
	// protected AsynPrivateMailbox asynPrivateMailbox = null;

	protected OutputMessageReaderIF outputMessageReader = null;

	public AbstractAsynConnection(String projectName, int index, String host, int port, long socketTimeOut,
			boolean whetherToAutoConnect, AsynPublicMailbox asynPublicMailbox,
			OutputMessageReaderIF outputMessageReader, SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol, DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException {
		super(projectName, index, host, port, socketTimeOut, whetherToAutoConnect, socketOutputStream, messageProtocol,
				dataPacketBufferQueueManager, clientObjectCacheManager);
		this.asynPublicMailbox = asynPublicMailbox;
		this.outputMessageReader = outputMessageReader;
	}

	public void serverClose() {
		synchronized (monitor) {
			try {
				serverSC.shutdownInput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownInput fail", projectName), e);
			}
			try {
				serverSC.shutdownOutput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownOutput fail", projectName), e);
			}

			try {
				serverSC.close();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel close fail", projectName), e);
			}
		}
	}

	@Override
	public boolean isConnected() {
		return serverSC.isConnected();
	}

	/**
	 * 비동기 방식의 소켓 채널의 신규 생성시 후기 작업을 수행한다. 참고) 비동기 방식의 소켓 채널의 읽기 담당 클래스는 selector 를
	 * 가지고 있는데 여기에 소켓 채널을 등록시켜야 한다.
	 * 
	 * @see AsynServerAdderIF
	 *//*
	protected void registerSocketToReadOnlySelector() throws InterruptedException {
		outputMessageReader.registerSocketToReadOnlySelector(this);
	}*/

	/**
	 * 연결 클래스가 가진 출력 메시지 큐로 출력 메시지를 넣는다. 비동기 방식의 소켓 채널에서는 소켓의 읽기/쓰기 담당 클래스가 따로 있는데,
	 * 출력 메시지 큐로
	 * 
	 * @param outObj
	 */
	abstract public void putToOutputMessageQueue(WrapReadableMiddleObject receivedLetter);

	/**
	 * {@link OutputMessageReaderThread } 가 운영하는 소켓 읽기 전용 selector 에 등록과 운영을 위한 hash
	 * 와 set 에 connection 을 등록한다.
	 * 
	 * @param hash
	 *            소켓 읽기 전용 selector 에 등록된 소켓에 대응하는 connection 객체 정보를 가지고 있는 해쉬
	 * @param newClients
	 *            소켓 읽기 전용 selector 에 등록되어야할 신규 클라이언트들
	 */
	public void register(Map<SocketChannel, AbstractAsynConnection> hash, Set<SocketChannel> newClients) {
		// waitingSCQueue.put(serverSC);
		newClients.add(serverSC);
		hash.put(serverSC, this);
	}

	/*
	 * protected ToLetter getLetterToServer(ClassLoader classLoader, AbstractMessage
	 * messageToClient) throws DynamicClassCallException,
	 * NoMoreDataPacketBufferException, BodyFormatException { ToLetter
	 * letterToServer = new ToLetter(this, messageToClient.getMessageID() ,
	 * messageToClient.messageHeaderInfo.mailboxID,
	 * messageToClient.messageHeaderInfo.mailID, getWrapBufferList(classLoader,
	 * messageToClient));
	 * 
	 * return letterToServer; }
	 */

	/**
	 * 비공유 + 비동기 연결 전용 소켓 채널 열기
	 * 
	 * @throws IOException
	 *             소켓 채널을 개방할때 혹은 비공유 + 비동기 에 맞도록 설정할때 에러 발생시 던지는 예외
	 */
	private void openAsynSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		// serverSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF,
		// clientProjectConfig.getDataPacketBufferSize()*2);
		// SO_SNDBUF

		StringBuilder infoBuilder = null;

		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] asyn+noshare connection[");
		infoBuilder.append(index);
		infoBuilder.append("]");
		log.info(infoBuilder.append(" (re)open new serverSC=").append(serverSC.hashCode()).toString());
	}

	@Override
	public void connectServer() throws IOException, InterruptedException {

		synchronized (monitor) {
			if (null == serverSC || !serverSC.isOpen()) {
				openAsynSocketChannel();
			}

			// (재)연결 판단 로직, 2번이상 SocketChannel.open() 호출하는것을 막는 역활을 한다.
			if (serverSC.isConnected()) {
				return;
			}

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

					if (!serverSC.finishConnect()) {
						String errorMessage = String.format("the socket[sc hascode=%d] has an error pending",
								serverSC.hashCode());
						throw new SocketTimeoutException(errorMessage);
					}
				}
			} finally {
				connectionEventOnlySelector.close();
			}

			// registerSocketToReadOnlySelector();
			outputMessageReader.registerAsynConnection(this);

			StringBuilder infoBuilder = null;

			infoBuilder = new StringBuilder("projectName[");
			infoBuilder.append(projectName);
			infoBuilder.append("] asyn connection[");
			infoBuilder.append(index);
			infoBuilder.append("] serverSC[");
			infoBuilder.append(serverSC.hashCode());
			infoBuilder.append("]");
			log.info(new StringBuilder(infoBuilder.toString()).append(" connect").toString());
			// log.info(new StringBuilder(info).append(" after
			// connect").toString());
		}

		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}

	protected void writeInputMessageToSocketChannel(List<WrapBuffer> wrapBufferListOfInputMessage) throws IOException {
		synchronized (serverSC) {
			for (WrapBuffer wrapBuffer : wrapBufferListOfInputMessage) {
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
				do {
					int numberOfBytesWritten = 0;
					try {
						numberOfBytesWritten = serverSC.write(byteBuffer);
					} catch (IOException e) {
						log.warn("this socket channel[{}] is closed becase of IO error", serverSC.hashCode());
						try {
							serverSC.close();
						} catch (IOException e1) {
						}
						throw e;
					}
					if (0 == numberOfBytesWritten) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							log.warn("when the number of bytes written is zero,  this thread must sleep. but it fails.",
									e);
						}
					}
				} while (byteBuffer.hasRemaining());
			}
		}
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inObj) throws IOException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, NotSupportedException, InterruptedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		connectServer();

		ClassLoader classLoader = inObj.getClass().getClassLoader();

		inObj.messageHeaderInfo.mailboxID = asynPublicMailbox.getMailboxID();
		inObj.messageHeaderInfo.mailID = asynPublicMailbox.getMailID();

		List<WrapBuffer> wrapBufferListOfInputMessage = getWrapBufferListOfInputMessage(classLoader, inObj);

		writeInputMessageToSocketChannel(wrapBufferListOfInputMessage);

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));
	}

	public SocketChannel getSocketChannel() {
		return serverSC;
	}

	public SocketOutputStream getSocketOutputStream() {
		return socketOutputStream;
	}

	public String getSimpleConnectionInfo() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(projectName);
		strBuffer.append("], NoShareAsynConnection=[");
		strBuffer.append(index);
		strBuffer.append("], socket channel=[");
		strBuffer.append(serverSC.hashCode());
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
