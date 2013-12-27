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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드.
 * 
 * @author Jonghoon Won
 * 
 */
public class OutputMessageReader extends Thread implements
		OutputMessageReaderIF, CommonRootIF {
	/** 출력 메시지를 읽는 쓰레드 번호 */
	private int index;
	private CommonProjectInfo commonProjectInfo = null;
	private MessageExchangeProtocolIF messageProtocol = null;
	private MessageMangerIF messageManger = null;
	
	/** 소켓 채널를 통해 연결 클래스를 얻기 위한 해쉬 */
	private Map<SocketChannel, AbstractAsynConnection> scToConnectionHash = new Hashtable<SocketChannel, AbstractAsynConnection>();
	/** selector 에 등록할 신규 소켓 채널을 담고 있는 그릇 */
	private final HashSet<SocketChannel> newClients = new HashSet<SocketChannel>();
	/** 읽기 전용 selecotr */
	private Selector selector = null;

	/** selector 를 깨우는 간격 */
	private long readSelectorWakeupInterval;
	
	
	/**
	 * 생성자
	 * @param index 순번
	 * @param readSelectorWakeupInterval 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param commonProjectInfo 공통 프로젝트 정보
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger  메시지 관리자
	 */
	public OutputMessageReader(int index, long readSelectorWakeupInterval,
			CommonProjectInfo commonProjectInfo,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger) {
		this.index = index;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.commonProjectInfo = commonProjectInfo;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		// this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		try {
			selector = Selector.open();
		} catch (IOException ioe) {
			log.fatal("selector open fail", ioe);
			System.exit(1);
		}
	}

	@Override
	public int getCntOfClients() {
		return scToConnectionHash.size();
	}

	/**
	 * 신규 채널를 selector 에 읽기 이벤트 등록한다.
	 */
	private void processNewConnection() {
		Iterator<SocketChannel> iter = newClients.iterator();

		while (iter.hasNext()) {
			SocketChannel sc = iter.next();
			try {
				sc.register(selector, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				closeServer(sc);
			}
			iter.remove();
		}
	}

	@Override
	public void run() {
		log.info(String.format("OutputMessageReader[%d] Thread start", index));

		
		

		int numRead = 0;
		// long totalRead = 0;

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
						SelectionKey selectionKey = selectionkey_iter.next();
						selectionkey_iter.remove();
						SocketChannel serverSC = (SocketChannel) selectionKey
								.channel();

						// log.info("11111111111111111");

						AbstractAsynConnection clientConnection = scToConnectionHash
								.get(serverSC);
						if (null == clientConnection) {
							log.warn(String.format(
									"sc[%s] connection match fail",
									serverSC.hashCode()));
							continue;
						}

						MessageInputStreamResourcePerSocket messageInputStreamResource = clientConnection.getMessageInputStreamResource();
						ByteBuffer lastInputStreamBuffer = messageInputStreamResource.getLastBuffer();						
						// ByteOrder clientByteOrder = clientConnection.getByteOrder();
						Charset charsetOfProject = clientConnection.getCharsetOfProject();
						
						int positionBeforeWork = lastInputStreamBuffer.position();

						try {
							// synchronized (serverSC) {
								numRead = serverSC.read(lastInputStreamBuffer);

								// if (numRead > 0) log.info("1.numRead=[%d]",
								// numRead);

								if (numRead == -1) {
									log.warn(String.format("1.serverSC[%d] read -1, remove client", serverSC.hashCode()));
									closeServer(selectionKey, clientConnection);
									continue;
								}

								numRead = serverSC.read(lastInputStreamBuffer);

								// if (numRead > 0) log.info("1.numRead=[%d]",
								// numRead);

								if (numRead == -1) {
									log.warn(String.format("2.serverSC[%d] read -1, remove client", serverSC.hashCode()));
									closeServer(selectionKey, clientConnection);
									continue;
								}
							// }
							
							
							if (lastInputStreamBuffer.position() == positionBeforeWork)
								continue;

							clientConnection.setFinalReadTime();
							
							ArrayList<AbstractMessage> outputMessageList = null;	
							outputMessageList = messageProtocol.S2MList(OutputMessage.class, charsetOfProject, messageInputStreamResource, messageManger);
							
							int cntOfMesages = outputMessageList.size();
							for (int i = 0; i < cntOfMesages; i++) {
								OutputMessage outObj = (OutputMessage)outputMessageList.get(i);
								clientConnection.putToOutputMessageQueue(outObj);
							}
						} catch (NotYetConnectedException e) {
							log.warn(String.format("NotYetConnectedException, %s", clientConnection.getSimpleConnectionInfo()), e);
							closeServer(selectionKey, clientConnection);
							continue;
						} catch (IOException e) {
							log.warn(String.format("IOException, %s", clientConnection.getSimpleConnectionInfo()), e);
							closeServer(selectionKey, clientConnection);
							continue;
						} catch(HeaderFormatException e) {
							log.warn(String.format("HeaderFormatException::%s", e.getMessage()), e);
							closeServer(selectionKey, clientConnection);
							continue;
						} catch (NoMoreDataPacketBufferException e) {
							log.warn(String.format("NoMoreDataPacketBufferException::%s", e.getMessage()), e);
							closeServer(selectionKey, clientConnection);
							continue;
						}
					}
				}
			}

			log.warn(String.format("%s index[%d] loop exit", commonProjectInfo.projectName, index));
		} catch (IOException e) {
			log.fatal(String.format("%s index[%d] unknown error", commonProjectInfo.projectName,
					index), e);
			System.exit(1);
		}

		log.warn(String.format("%s index[%d] Thread end", commonProjectInfo.projectName, index));
	}

	/**
	 * 소켓을 닫는다.
	 * 
	 * @param close_sc
	 */
	private void closeServer(SocketChannel close_sc) {
		AbstractAsynConnection clientConnection = scToConnectionHash
				.get(close_sc);
		scToConnectionHash.remove(close_sc);
		if (null != clientConnection) clientConnection.serverClose();
	}

	/**
	 * selector 로 얻는 SelectionKey 가 가진 소켓 채널을 닫고 selector 에서 제거한다.
	 * 
	 * @param selectionKey
	 */
	private void closeServer(SelectionKey selectionKey,
			AbstractAsynConnection clientConnection) {
		SocketChannel close_sc = null;
		selectionKey.cancel();
		close_sc = (SocketChannel) selectionKey.channel();

		scToConnectionHash.remove(close_sc);
		clientConnection.serverClose();

	}

	@Override
	public void addNewServer(AbstractAsynConnection clientConnection)
			throws InterruptedException {
		
		clientConnection.register(scToConnectionHash, newClients);
		/*
		SocketChannel sc = clientConnection.getSocketChannel();

		log.info(String.format(
				"project[%s] asyn connection[%02d] new serverSC=[%d]",
				clientConnection.getProjectName(), clientConnection.getIndex(),
				sc.hashCode()));

		scToConnectionHash.put(sc, clientConnection);
		newClients.add(sc);
		*/

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
		
		// } while (sc.isConnected() && !sc.isRegistered());
		} while (clientConnection.isConnected() && !clientConnection.isRegistered());
	}

}
