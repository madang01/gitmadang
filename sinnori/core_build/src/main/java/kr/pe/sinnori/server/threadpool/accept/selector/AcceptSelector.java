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

package kr.pe.sinnori.server.threadpool.accept.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;

/**
 * 서버에 접속하는 클라이언트 접속 승인 처리 쓰레드<br/>
 * 서버 소켓을 1개 열고 OP_ACCEPT 전용 selector 에 등록하여 <br/>
 * 총 소켓 채널 수 만큼만 접속을 허용하게한다.<br/>
 * 총 소켓 채널수를 넘었다면 해당 소켓 채널을 닫는다.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptSelector extends Thread {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptSelector.class);

	private Selector acceptReadSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;
	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;
	private MessageProtocolIF messageProtocol;
	private SocketResourceManagerIF socketResourceManager;

	public AcceptSelector(String projectName, String serverHost, int serverPort, int maxClients,
			MessageProtocolIF messageProtocol, SocketResourceManagerIF socketResourceManager) {

		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.maxClients = maxClients;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;

		// FIXME!
		/*
		 * log.
		 * info("projectName={}, serverHost={}, serverPort={}, acceptSelectTimeout={}, maxClients={}"
		 * , projectName, serverHost, serverPort, acceptSelectTimeout, maxClients);
		 */

	}

	/**
	 * 서버 소켓을 생성하고 selector에 OP_ACCEPT로 등록한다.
	 * 
	 * @throws ServerSocketChannel관련
	 *             작업에서 발생한다.
	 */
	private void initServerSocket() {
		try {
			acceptReadSelector = Selector.open();

			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false); // non block 설정
			ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

			InetSocketAddress address = new InetSocketAddress(serverHost, serverPort);
			ssc.socket().bind(address);

			ssc.register(acceptReadSelector, SelectionKey.OP_ACCEPT);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
			System.exit(1);
		}

	}

	@Override
	public void run() {
		log.info("AcceptSelector::projectName[%s] start", projectName);

		initServerSocket();

		int numRead = 0;
		ArrayDeque<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayDeque<WrapReadableMiddleObject>();

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int keyReady = acceptReadSelector.select();
				if (keyReady > 0) {

					Set<SelectionKey> selectedKeySet = acceptReadSelector.selectedKeys();

					try {
						for (SelectionKey key : selectedKeySet) {

							if (key.isAcceptable()) {
								long startTime = 0, endTime = 0;
								startTime = System.nanoTime();

								ServerSocketChannel readyChannel = (ServerSocketChannel) key.channel();

								SocketChannel socketChannelBeforeAccpet = readyChannel.accept();

								if (null == socketChannelBeforeAccpet) {
									log.warn("socketChannelBeforeAccpet is null");
									continue;
								}

								int numberOfSocketResources = socketResourceManager.getNumberOfSocketResources();

								if (numberOfSocketResources < maxClients) {
									// log.info("accepted socket channel=[{}]", sc.hashCode());
									setupSocketChannel(socketChannelBeforeAccpet);

									try {
										socketChannelBeforeAccpet.register(acceptReadSelector, SelectionKey.OP_READ);

										socketResourceManager.addNewAcceptedSocketChannel(socketChannelBeforeAccpet);
									} catch (ClosedChannelException e) {
										log.warn(
												"fail to register this channel[{}] with the given selector having a the interest set OP_READ",
												socketChannelBeforeAccpet.hashCode());

										continue;
									}

								} else {
									socketChannelBeforeAccpet.setOption(StandardSocketOptions.SO_LINGER, 0);
									socketChannelBeforeAccpet.close();
									log.warn("max clients[{}] researched so close the selected socket channel[{}]",
											maxClients, socketChannelBeforeAccpet.hashCode());

								}

								endTime = System.nanoTime();

								long elaspedTime = endTime - startTime;
								if (elaspedTime > 1000000) {
									log.info("accept elasped time={}", TimeUnit.MICROSECONDS.convert(elaspedTime, TimeUnit.NANOSECONDS));
								}
								
							} else if (key.isReadable()) {
								/*long startTime = 0, endTime = 0;
								startTime = System.nanoTime();*/

								SocketChannel acceptedSocketChannel = (SocketChannel) key.channel();

								SocketResource fromSocketResource = socketResourceManager
										.getSocketResource(acceptedSocketChannel);

								if (null == fromSocketResource) {
									log.warn("this accpeted socket channel[{}] has no resoruce",
											acceptedSocketChannel.hashCode());
									continue;
								}

								SocketOutputStream fromSocketOutputStream = fromSocketResource.getSocketOutputStream();
								ServerExecutorIF fromExecutor = fromSocketResource.getExecutor();

								try {
									numRead = fromSocketOutputStream.read(acceptedSocketChannel);

									if (numRead == -1) {
										log.warn("this socket channel[{}] has reached end-of-stream",
												acceptedSocketChannel.hashCode());
										closeClient(key, fromSocketResource);
										continue;
									}

									fromSocketResource.setFinalReadTime();

									messageProtocol.S2MList(fromSocketOutputStream, wrapReadableMiddleObjectQueue);

									// final int wrapReadableMiddleObjectListSize =
									// wrapReadableMiddleObjectList.size();

									while (!wrapReadableMiddleObjectQueue.isEmpty()) {
										WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectQueue
												.pollFirst();
										wrapReadableMiddleObject.setFromSC(acceptedSocketChannel);

										try {
											fromExecutor.putIntoQueue(wrapReadableMiddleObject);
										} catch (InterruptedException e) {
											log.info("1.drop the input message[{}] becase of InterruptedException",
													wrapReadableMiddleObject.toString());

											wrapReadableMiddleObject.closeReadableMiddleObject();

											while (!wrapReadableMiddleObjectQueue.isEmpty()) {
												wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.pollFirst();

												wrapReadableMiddleObject.setFromSC(acceptedSocketChannel);

												log.info("2.drop the input message[{}] becase of InterruptedException",
														wrapReadableMiddleObject.toString());

												wrapReadableMiddleObject.closeReadableMiddleObject();
											}
											throw e;
										}
									}
								} catch (NoMoreDataPacketBufferException e) {
									String errorMessage = new StringBuilder()
											.append("NoMoreDataPacketBufferException::").append(e.getMessage())
											.toString();

									log.warn(errorMessage, e);
									closeClient(key, fromSocketResource);
									continue;
								} catch (IOException e) {
									String errorMessage = new StringBuilder().append("IOException::")
											.append(e.getMessage()).toString();

									log.warn(errorMessage, e);
									closeClient(key, fromSocketResource);
									continue;
								}

								/*endTime = System.nanoTime();
								long elaspedTime = endTime - startTime;
								if (elaspedTime > 1000000) {
									log.debug("read elasped time={}", TimeUnit.MICROSECONDS.convert(elaspedTime, TimeUnit.NANOSECONDS));
								}*/
								
							}
						}
					} finally {
						selectedKeySet.clear();
					}
				}
			}
			log.warn("{} AcceptSelector loop exit", projectName);
		} catch (InterruptedException e) {
			log.warn("{} AcceptSelector stop", projectName);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append(projectName)
					.append(" AcceptSelector unknown error, errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
		} finally {
			try {
				ssc.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}

			try {
				acceptReadSelector.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}
		}
	}

	private void setupSocketChannel(SocketChannel acceptedSocketChannel) throws IOException {
		acceptedSocketChannel.configureBlocking(false);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	private void closeClient(SelectionKey selectedKey, SocketResource fromSocketResource) {
		SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

		log.info("close the socket[{}] that is registed with a accept/read selector", selectedSocketChannel.hashCode());

		selectedKey.cancel();

		try {
			selectedSocketChannel.close();
		} catch (IOException e) {
			log.warn("fail to close the socket[{}] that is registed with a accept/read selector",
					selectedSocketChannel.hashCode());
		}

		fromSocketResource.close();

		socketResourceManager.remove(selectedSocketChannel);
	}
}
