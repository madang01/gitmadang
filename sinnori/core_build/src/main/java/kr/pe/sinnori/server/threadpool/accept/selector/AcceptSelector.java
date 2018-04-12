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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.server.SocketResourceManagerIF;

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
	
	private Selector selector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;
	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;
	private ArrayBlockingQueue<SocketChannel> acceptQueue;
	private SocketResourceManagerIF socketResourceManager;

	
	public AcceptSelector(String projectName, String serverHost, int serverPort,  int maxClients,
			ArrayBlockingQueue<SocketChannel> acceptQueue, SocketResourceManagerIF socketResourceManager) {

		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.maxClients = maxClients;
		this.acceptQueue = acceptQueue;
		this.socketResourceManager = socketResourceManager;
		
		
		// FIXME!
		/*log.info("projectName={}, serverHost={}, serverPort={}, acceptSelectTimeout={}, maxClients={}", 
				projectName, serverHost, serverPort, acceptSelectTimeout, maxClients);*/
		
	}

	/**
	 * 서버 소켓을 생성하고 selector에 OP_ACCEPT로 등록한다.
	 * 
	 * @throws ServerSocketChannel관련
	 *             작업에서 발생한다.
	 */
	private void initServerSocket() {
		try {
			selector = Selector.open();

			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false); // non block 설정
			ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			
			InetSocketAddress address = new InetSocketAddress(serverHost, serverPort);
			ssc.socket().bind(address);

			ssc.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
			System.exit(1);
		}

	}

	@Override
	public void run() {
		// log.info("AcceptSelector::projectName[%s] start", projectName);

		initServerSocket();


		try {
			while (!Thread.currentThread().isInterrupted()) {
				int keyReady = selector.select();
				if (keyReady > 0) {
					
					Set<SelectionKey>  selectedKeySet = selector.selectedKeys();
					
					try {
						for (SelectionKey key: selectedKeySet)  {

							ServerSocketChannel readyChannel = (ServerSocketChannel) key
									.channel();		
							
							SocketChannel sc = readyChannel.accept();
							
							if (null == sc) {
								log.error("sc is null");
								System.exit(1);
							}
							
							int numberOfSocketResources = socketResourceManager.getNumberOfSocketResources();
							
							if (numberOfSocketResources < maxClients) {								
								// log.info("accepted socket channel=[{}]", sc.hashCode());
								acceptQueue.put(sc);
							} else {
								sc.setOption(StandardSocketOptions.SO_LINGER, 0);
								sc.close();
								log.warn("최대 소켓수[{}] 도달에 따른 소켓 닫기 sc[{}]",
										maxClients, sc.hashCode());
								
							}
						}
					} finally {
						selectedKeySet.clear();
					}
				}
			}
			log.warn(String.format("AcceptSelector[%s] loop exit", projectName));
		} catch (InterruptedException e) {
			log.warn(String.format("AcceptSelector::projectName[%s] stop", projectName), e);
		} catch (Exception e) {
			log.warn(String.format("AcceptSelector::projectName[%s] error", projectName), e);
		} finally {
			try {
				ssc.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}

			try {
				selector.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}
		}
	}
}
