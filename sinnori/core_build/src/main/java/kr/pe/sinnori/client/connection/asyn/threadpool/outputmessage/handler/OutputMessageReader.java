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

package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드.
 * 
 * @author Won Jonghoon
 * 
 */
public class OutputMessageReader extends Thread implements OutputMessageReaderIF {
	private Logger log = LoggerFactory.getLogger(OutputMessageReader.class);

	// private final Object monitor = new Object();

	private String projectName = null;
	/** 출력 메시지를 읽는 쓰레드 번호 */
	private int index;
	private MessageProtocolIF messageProtocol = null;

	/** selector 에 등록할 신규 소켓 채널을 담고 있는 그릇 */
	// private final ArrayDeque<SocketChannel> notRegistedSocketChannelList = new ArrayDeque<SocketChannel>();
	private final Set<SocketChannel> notRegistedSocketChannelList = Collections
			.synchronizedSet(new HashSet<SocketChannel>());

	/** 읽기 전용 selecotr */
	private Selector selector = null;

	/** selector 를 깨우는 간격 */
	private long readSelectorWakeupInterval;

	private Hashtable<SocketChannel, AbstractAsynConnection> scToAsynConnectionHash = new Hashtable<SocketChannel, AbstractAsynConnection>();

	/**
	 * 생성자
	 * 
	 * @param index
	 *            순번
	 * @param readSelectorWakeupInterval
	 *            출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param projectPart
	 *            프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param messageProtocol
	 *            메시지 교환 프로토콜
	 */
	public OutputMessageReader(String projectName, int index, long readSelectorWakeupInterval,
			MessageProtocolIF messageProtocol) {
		this.projectName = projectName;
		this.index = index;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.messageProtocol = messageProtocol;
		try {
			selector = Selector.open();
		} catch (IOException ioe) {
			log.error("selector open fail", ioe);
			System.exit(1);
		}
	}

	@Override
	public int getNumberOfAsynConnection() {
		return (notRegistedSocketChannelList.size() + selector.keys().size());
	}

	/**
	 * 신규 채널를 selector 에 읽기 이벤트 등록한다.
	 */
	private void processNewConnection() {
		
		Iterator<SocketChannel> notRegistedSocketChannelIterator = notRegistedSocketChannelList.iterator();

		while (notRegistedSocketChannelIterator.hasNext()) {
			SocketChannel notRegistedSocketChannel = notRegistedSocketChannelIterator.next();
			notRegistedSocketChannelIterator.remove();
			
				try {
					notRegistedSocketChannel.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", 
							projectName, index, notRegistedSocketChannel.hashCode());
					
					scToAsynConnectionHash.get(notRegistedSocketChannel).noticeThisConnectionWasRemovedFromReadyOnleySelector();					
					scToAsynConnectionHash.remove(notRegistedSocketChannel);
				}
			}
		// }
	}

	@Override
	public void registerAsynConnection(AbstractAsynConnection asynConn) {
		SocketChannel newSC = asynConn.getSocketChannel();

		scToAsynConnectionHash.put(newSC, asynConn);
		
					
		notRegistedSocketChannelList.add(newSC);
		

		if (getState().equals(Thread.State.NEW)) {
			return;
		}

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
				// ignore
			}
		} while (newSC.isConnected() && !newSC.isRegistered());
	}

	@Override
	public void run() {
		log.info(String.format("%s OutputMessageReader[%d] start", projectName, index));

		// int numRead = 0;
		// long totalRead = 0;

		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();

				int numberOfKeys = selector.select();

				if (numberOfKeys > 0) {
					Set<SelectionKey> selectedKeySet = selector.selectedKeys();
					Iterator<SelectionKey> selectedKeyIterator = selectedKeySet.iterator();
					while (selectedKeyIterator.hasNext()) {
						SelectionKey selectedKey = selectedKeyIterator.next();
						selectedKeyIterator.remove();
						SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

						// log.info("11111111111111111");

						AbstractAsynConnection asynConnection = scToAsynConnectionHash.get(selectedSocketChannel);
						if (null == asynConnection) {
							log.warn("{} OutputMessageReader[{}] this socket channel[{}] has reached end-of-stream",
									projectName, index, selectedSocketChannel.hashCode());
							continue;
						}

						SocketOutputStream socketOutputStream = asynConnection.getSocketOutputStream();

						try {
							int numRead = socketOutputStream.read(selectedSocketChannel);
							
							if (numRead == -1) {
								String errorMessage = new StringBuilder("this socket channel[")										
										.append(selectedSocketChannel.hashCode())
										.append("] has reached end-of-stream in OutputMessageReader[")
										.append(index)
										.append("]").toString();
								
								log.warn(errorMessage);
								closeSocket(selectedKey, asynConnection);
								continue;
							}
							
							asynConnection.setFinalReadTime();

							ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = messageProtocol
									.S2MList(socketOutputStream);

							for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectList) {
								// FIXME!
								// log.info("wrapReadableMiddleObject={}", wrapReadableMiddleObject.toString());

								// asynConnection.putToOutputMessageQueue(new LetterFromServer(receivedLetter));
								FromLetter fromLetter = new FromLetter(selectedSocketChannel, wrapReadableMiddleObject);
								asynConnection.putToOutputMessageQueue(fromLetter);
							}
						} catch (Exception e) {
							log.warn(String.format("%s OutputMessageReader[%d]::%s",
									asynConnection.getSimpleConnectionInfo(), index, e.getMessage()), e);
							closeSocket(selectedKey, asynConnection);
							continue;
						}
					}
				}
			}

			log.warn(String.format("%s OutputMessageReader[%d] loop exit", projectName, index));
		} catch (Exception e) {
			log.error(String.format("%s OutputMessageReader[%d] unknown error", projectName, index), e);
			System.exit(1);
		}

		// log.warn(String.format("%s OutputMessageReader[%d] Thread end",
		// clientProjectConfig.getProjectName(), index));
	}

	/**
	 * selector 로 얻는 SelectionKey 가 가진 소켓 채널을 닫고 selector 에서 제거한다.
	 * 
	 * @param selectedKey
	 */
	private void closeSocket(SelectionKey selectedKey, AbstractAsynConnection selectedAsynConnection) {
		SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

		log.info("close the socket[{}]", selectedSocketChannel.hashCode());

		selectedKey.cancel();
		
		try {
			selectedSocketChannel.close();
		} catch (IOException e) {
			log.warn("fail to close the socket[{}]", selectedSocketChannel.hashCode());
		}
		selectedAsynConnection.noticeThisConnectionWasRemovedFromReadyOnleySelector();
		
		scToAsynConnectionHash.remove(selectedSocketChannel);
	}

	/*private void closeFailedSocket(SocketChannel failedSocketChannel) {
		log.info("2. close the socket[{}]", failedSocketChannel.hashCode());
		
		AbstractAsynConnection failedAsynConnection = scToAsynConnectionHash.get(failedSocketChannel);
		if (null != failedAsynConnection) {
			scToAsynConnectionHash.remove(failedSocketChannel);

			try {
				failedAsynConnection.closeSocket();
			} catch (IOException e) {
				log.warn("fail to close the socket[{}]", failedSocketChannel.hashCode());
			}
			failedAsynConnection.releaseResources();
		}
	}*/

}
