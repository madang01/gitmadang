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

package kr.pe.sinnori.server.threadpool.accept.selector.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.server.SocketResourceManagerIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Logger log = LoggerFactory.getLogger(AcceptSelector.class);
	
	private Selector selector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;
	private String projectName;
	private String serverHost;
	private int serverPort;
	private long acceptSelectTimeout;
	private int maxClients;
	private LinkedBlockingQueue<SocketChannel> acceptQueue;
	private SocketResourceManagerIF socketResourceManager;

	/**
	 * 생성자
	 * 
	 * @param projectName
	 *            서버명
	 * @param serverHost
	 *            호스트
	 * @param serverPort
	 *            포트
	 * @param acceptSelectTimeout
	 *            소켓 채널 받아 들이기 타임 아웃
	 * @param maxClients
	 *            최대 연결 소켓 채널 수
	 * @param acceptQueue
	 *            생산자 접속 요청중인 소켓 채널 처리 쓰레드, 소비자 접속이 허용된 소켓 채널 등록 쓰레드인 큐
	 */
	public AcceptSelector(String projectName, String serverHost, int serverPort, long acceptSelectTimeout, int maxClients,
			LinkedBlockingQueue<SocketChannel> acceptQueue, SocketResourceManagerIF socketResourceManager) {

		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.acceptSelectTimeout = acceptSelectTimeout;
		this.maxClients = maxClients;
		this.acceptQueue = acceptQueue;
		this.socketResourceManager = socketResourceManager;
		
		
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
				int keyReady = selector.select(acceptSelectTimeout);
				if (keyReady > 0) {
					Iterator<SelectionKey> iter = selector.selectedKeys()
							.iterator();

					while (iter.hasNext()) {
						SelectionKey key = iter.next();
						iter.remove();

						ServerSocketChannel readyChannel = (ServerSocketChannel) key
								.channel();
						
						
						SocketChannel sc = readyChannel.accept();
						
						
						// 최대 등록 가능한 client만 허용
						if (socketResourceManager.getNumberOfSocketResources() < maxClients) {
							log.info(String.format("new sc[%d]", sc.hashCode()));

							acceptQueue.put(sc);
						} else {
							sc.close();
							log.warn(String.format(
									"MAX CLIENTS 도달로 소켓 닫기 sc[%d], max clients=[%d]",
									sc.hashCode(), maxClients));
						}
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
