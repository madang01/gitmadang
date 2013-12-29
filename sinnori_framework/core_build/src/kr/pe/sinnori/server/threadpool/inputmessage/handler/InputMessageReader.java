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

package kr.pe.sinnori.server.threadpool.inputmessage.handler;

//import java.util.logging.Level;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.io.LetterFromClient;

/**
 * 서버 입력 메시지 소켓 읽기 담당 쓰레드
 * 
 * @author Jonghoon Won
 * 
 */
public class InputMessageReader extends Thread implements CommonRootIF,
		InputMessageReaderIF {
	// private final Object monitor = new Object();
	private int index;
	private long readSelectorWakeupInterval;
	private ServerProjectConfigIF serverProjectConfig; 
	private MessageExchangeProtocolIF messageProtocol;
	private MessageMangerIF messageManger = null;
	
	private ClientResourceManagerIF clientResourceManager;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	
	private LinkedBlockingQueue<SocketChannel> waitingSCQueue = new LinkedBlockingQueue<SocketChannel>();
	private Selector selector = null;
	



	/**
	 * 생성자
	 * @param index 순번
	 * @param readSelectorWakeupInterval 입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public InputMessageReader(int index, long readSelectorWakeupInterval,
			ServerProjectConfigIF serverProjectConfig, 
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientResourceManagerIF clientResourceManager) {
		this.index = index;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.serverProjectConfig = serverProjectConfig;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		this.clientResourceManager = clientResourceManager;
		this.inputMessageQueue = inputMessageQueue;

		try {
			selector = Selector.open();
		} catch (IOException ioe) {
			log.fatal(String.format("RequetProcessor[%d] selector open fail",
					index), ioe);
			System.exit(1);
		}
		
		
	}

	@Override
	public void addClient(SocketChannel sc) throws InterruptedException,
	NoMoreDataPacketBufferException {
		clientResourceManager.addNewClient(sc);		
		// newClients.put(sc, sc);
		waitingSCQueue.put(sc);

		if (getState().equals(Thread.State.NEW))
			return;

		/**
		 * <pre>
		 * (1) 소켓 채널을 selector 에 등록한다. 
		 * (2) 소켓 채널을 등록한 selector 를 깨우는 신호를 보낸후 
		 * (3) 설정 파일에서 지정된 시간 만큼 대기 후
		 * (4) 소켓 채널이 계속 연결된 상태이고 selector 에 등록이 안되었다면 
		 *     (2) 번항에서 부터 다시 반복한다. 
		 * (5) 소켓 채널이 계속 연결된 상태가 아니거나 혹은 selector 에 등록되었다면 루프를 종료한다.
		 * 참고) (3) 항을 수행하는 순간 selector 가 깨어나 있어 
		 *       소켓 읽기를 수행하는 과정에서 헤더 포맷 에러를 만날 경우 소켓을 닫고 동시에 selector 에 등록 취소한다.
		 *       이것이 소켓 채널이 연결된 상태인지를 검사해야 하는 이유이다.
		 * </pre> 
		 */
		do {
			selector.wakeup();
			Thread.sleep(readSelectorWakeupInterval);
		} while (sc.isConnected() && !sc.isRegistered());
	}

	@Override
	public int getCntOfClients() {
		return (waitingSCQueue.size() + selector.keys().size());
	}

	/**
	 * 신규 client들을 selector에 등록한다.
	 */
	private void processNewConnection() {		
		while(!waitingSCQueue.isEmpty()) {
			SocketChannel sc = waitingSCQueue.poll();
			// if (null != sc) {
				try {
					sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] fail to register selector", serverProjectConfig.getProjectName(), index, sc.hashCode()), e);
				}
			// }
		}
	}

	@Override
	public void run() {
		log.info(String.format("%s InputMessageReader[%d] start", serverProjectConfig.getProjectName(), index));

		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();
				int keyReady = selector.select();

				if (keyReady > 0) {
					Set<SelectionKey> selectionkey_set = selector
							.selectedKeys();
					Iterator<SelectionKey> selectionkey_iter = selectionkey_set
							.iterator();
					while (selectionkey_iter.hasNext()) {
						SelectionKey key = selectionkey_iter.next();
						selectionkey_iter.remove();
						SocketChannel clientSC = (SocketChannel) key.channel();
						ByteBuffer lastInputStreamBuffer = null;
						ClientResource clientResource = clientResourceManager
								.getClientResource(clientSC);
						
						if (null == clientResource) {
							log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] is no match for ClientResource", serverProjectConfig.getProjectName(), index, clientSC.hashCode()));
							continue;
						}
						
						MessageInputStreamResourcePerSocket messageInputStreamResource = clientResource.getMessageInputStreamResource();
						lastInputStreamBuffer = messageInputStreamResource.getLastBuffer();
						
						try {

							int positionBeforeReading = lastInputStreamBuffer.position();
							
							
							int numRead = clientSC.read(lastInputStreamBuffer);
							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										serverProjectConfig.getProjectName(), index));
								closeClient(key);
								continue;
							}
							numRead = clientSC.read(lastInputStreamBuffer); // 2번 읽기로 1byte
																// 읽기 방지
							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										serverProjectConfig.getProjectName(), index));
								closeClient(key);
								continue;
							}

							int positionAfterReading = lastInputStreamBuffer.position();

							if (positionAfterReading == positionBeforeReading) continue;
							
							clientResource.setFinalReadTime();

							// log.info("lastInputStreamBuffer[%s]",
							// lastInputStreamBuffer.toString());

							ArrayList<AbstractMessage> inputMessageList = null;

							try {
								inputMessageList = messageProtocol.S2MList(InputMessage.class, serverProjectConfig.getCharset(), messageInputStreamResource, messageManger);
								
								
							} catch (NoMoreDataPacketBufferException e) {
								log.warn(String.format("NoMoreDataPacketBufferException::%s", e.getMessage()), e);
								closeClient(key);
								continue;
							} catch (HeaderFormatException e) {
								log.warn(String.format("HeaderFormatException::%s",
										e.getMessage()), e);
								closeClient(key);
								continue;
							}

							int cntOfMesages = inputMessageList.size();
							for (int i = 0; i < cntOfMesages; i++) {
								InputMessage inObj = (InputMessage)inputMessageList.get(i);
								inputMessageQueue.put(new LetterFromClient(clientSC, inObj, clientResource));
							}
						} catch (NotYetConnectedException e) {
							log.warn("io error", e);
							closeClient(key);
							continue;
						} catch (IOException e) {
							log.warn(String.format("%s InputMessageReader[%d] error", serverProjectConfig.getProjectName(), index), e);
							closeClient(key);
							continue;
						}
					}
				}
			}

			log.warn(String.format("%s InputMessageReader[%d] loop exit", serverProjectConfig.getProjectName(), index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s InputMessageReader[%d] stop", serverProjectConfig.getProjectName(), index), e);
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageReader[%d] unknown error", serverProjectConfig.getProjectName(), index), e);
		}
	}

	/**
	 * client 접속이 끊겼거나, socket 읽기시 IO 에러, 인코딩 에러로 client 작업대상에서 제외할때 호출
	 * 
	 * @param key
	 *            selector 에서 얻는 SelectionKey로 작업대상에서 제외할 client
	 * @see ClientResouceManager#removeClient(java.nio.channels.SocketChannel)
	 */
	private void closeClient(SelectionKey key) {
		key.cancel();
		SocketChannel close_sc = (SocketChannel) key.channel();
		clientResourceManager.removeClient(close_sc);
	}
}
