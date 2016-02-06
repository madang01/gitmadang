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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.project.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.io.LetterFromClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서버 입력 메시지 소켓 읽기 담당 쓰레드
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageReader extends Thread implements InputMessageReaderIF {
	private Logger log = LoggerFactory.getLogger(InputMessageReader.class);
	
	// private final Object monitor = new Object();
	private String projectName = null; 
	private int index;
	private Charset charsetOfProject = null;
	private long readSelectorWakeupInterval;	
	private MessageProtocolIF messageProtocol;
	
	private ClientResourceManagerIF clientResourceManager;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	
	// private final Set<SocketChannel> newClients = new HashSet<SocketChannel>();
	private final Set<SocketChannel> newClients = Collections.synchronizedSet(new HashSet<SocketChannel>());
	// private LinkedBlockingQueue<SocketChannel> waitingSCQueue = new LinkedBlockingQueue<SocketChannel>();
	private Selector selector = null;

	/**
	 * 생성자
	 * @param index 순번
	 * @param readSelectorWakeupInterval 입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param projectPart 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public InputMessageReader(
			String projectName,			
			int index, 
			Charset charsetOfProject,
			long readSelectorWakeupInterval,			 
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientResourceManagerIF clientResourceManager) {
		this.index = index;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.projectName = projectName;
		this.charsetOfProject  =charsetOfProject;
		this.messageProtocol = messageProtocol;
		this.clientResourceManager = clientResourceManager;
		this.inputMessageQueue = inputMessageQueue;

		try {
			selector = Selector.open();
		} catch (IOException ioe) {
			log.error(String.format("RequetProcessor[%d] selector open fail",
					index), ioe);
			System.exit(1);
		}
		
		
	}

	@Override
	public void addClient(SocketChannel sc) throws NoMoreDataPacketBufferException {
		clientResourceManager.addNewClient(sc);		
		newClients.add(sc);
		// waitingSCQueue.put(sc);

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
			try {
				Thread.sleep(readSelectorWakeupInterval);
			} catch (InterruptedException e) {
			}
		} while (sc.isConnected() && !sc.isRegistered());
	}

	@Override
	public int getCntOfClients() {
		return (newClients.size() + selector.keys().size());
	}

	/**
	 * 신규 client들을 selector에 등록한다.
	 */
	private void processNewConnection() {
		for (SocketChannel sc : newClients) {
			try {
				sc.register(selector, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] fail to register selector", projectName, index, sc.hashCode()), e);
			}
			newClients.remove(sc);
		}
		/*while(!waitingSCQueue.isEmpty()) {
			SocketChannel sc = waitingSCQueue.poll();
			// if (null != sc) {
				try {
					sc.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] fail to register selector", serverProjectConfig.getProjectName(), index, sc.hashCode()), e);
				}
			// }
		}*/
	}

	@Override
	public void run() {
		log.info(String.format("%s InputMessageReader[%d] start", projectName, index));

		int numRead = 0;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();
				int keyReady = selector.select();

				if (keyReady > 0) {
					Set<SelectionKey> selectionKeySet = selector
							.selectedKeys();
					Iterator<SelectionKey> selectionKeyIter = selectionKeySet
							.iterator();
					while (selectionKeyIter.hasNext()) {
						SelectionKey key = selectionKeyIter.next();
						selectionKeyIter.remove();
						SocketChannel clientSC = (SocketChannel) key.channel();
						ByteBuffer lastInputStreamBuffer = null;
						ClientResource clientResource = clientResourceManager
								.getClientResource(clientSC);
						
						if (null == clientResource) {
							log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] is no match for ClientResource", projectName, index, clientSC.hashCode()));
							continue;
						}
						
						SocketInputStream messageInputStreamResource = clientResource.getMessageInputStreamResource();
						
						
						try {
							lastInputStreamBuffer = messageInputStreamResource.getLastDataPacketBuffer();
							/*log.info(String.format("1. %s InputMessageReader[%d] lastInputStreamBuffer[%s]", 
									serverProjectConfig.getProjectName(), index, lastInputStreamBuffer.toString()));*/
							
							// int positionBeforeReading = lastInputStreamBuffer.position();							
							/*
							numRead = clientSC.read(lastInputStreamBuffer);
							
							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										serverProjectConfig.getProjectName(), index));
								closeClient(key);
								continue;
							}
							
							log.info(String.format("2. %s InputMessageReader[%d] numRead=[%d] lastInputStreamBuffer[%s]", 
									serverProjectConfig.getProjectName(), index, numRead, lastInputStreamBuffer.toString()));
							
							
							// 2번 읽기로 1byte 읽기 방지
							numRead = clientSC.read(lastInputStreamBuffer);

							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										serverProjectConfig.getProjectName(), index));
								closeClient(key);
								continue;
							}
							
							log.info(String.format("3. %s InputMessageReader[%d] numRead=[%d] lastInputStreamBuffer[%s]", 
									serverProjectConfig.getProjectName(), index, numRead, lastInputStreamBuffer.toString()));
														
							
							int positionAfterReading = lastInputStreamBuffer.position();

							if (positionAfterReading == positionBeforeReading) continue;
							*/
							
							do {
								numRead = clientSC.read(lastInputStreamBuffer);
								if (numRead < 1) break;
								
								if (!lastInputStreamBuffer.hasRemaining()) {
									if (!messageInputStreamResource.canNextDataPacketBuffer()) break;
									lastInputStreamBuffer = messageInputStreamResource.nextDataPacketBuffer();
								}
							} while(true);
							
							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										projectName, index));
								closeClient(key);
								continue;
							}
							
							
							clientResource.setFinalReadTime();
												
							
							ArrayList<ReceivedLetter> inputMessageList = 
									messageProtocol.S2MList(charsetOfProject, messageInputStreamResource);

							// int cntOfMesages = inputMessageList.size();
							
							/*log.info(String.format("%s InputMessageReader[%d] cntOfMesages=[%d]", 
									serverProjectConfig.getProjectName(), index, cntOfMesages));*/
							
							/*for (int i = 0; i < cntOfMesages; i++) {
								AbstractMessage inObj = (AbstractMessage)inputMessageList.get(i);
								inputMessageQueue.put(new LetterFromClient(clientSC, clientResource, inObj));
							}		*/					

							for (ReceivedLetter receivedLetter : inputMessageList) {
								inputMessageQueue.put(new LetterFromClient(clientSC, clientResource, receivedLetter));
							}
						
						} catch (NoMoreDataPacketBufferException e) {
							log.warn(String.format("%s InputMessageReader[%d] NoMoreDataPacketBufferException::%s", 
									projectName, index, e.getMessage()), e);
							closeClient(key);
							continue;
						} catch (HeaderFormatException e) {
							log.warn(String.format("%s InputMessageReader[%d] HeaderFormatException::%s", 
									projectName, index, e.getMessage()), e);
							closeClient(key);
							continue;
						} catch (NotYetConnectedException e) {
							log.warn("io error", e);
							closeClient(key);
							continue;
						} catch (IOException e) {
							log.warn(String.format("%s InputMessageReader[%d] error", projectName, index), e);
							closeClient(key);
							continue;
						}
					}
				}
			}

			log.warn(String.format("%s InputMessageReader[%d] loop exit", projectName, index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s InputMessageReader[%d] stop", projectName, index), e);
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageReader[%d] unknown error", projectName, index), e);
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