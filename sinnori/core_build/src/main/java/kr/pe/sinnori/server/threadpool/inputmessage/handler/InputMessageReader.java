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

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;


public class InputMessageReader extends Thread implements InputMessageReaderIF {
	private Logger log = LoggerFactory.getLogger(InputMessageReader.class);
	
	private final Object monitor = new Object();
	
	
	private String projectName = null; 
	private int index;
	private long readSelectorWakeupInterval;	
	private MessageProtocolIF messageProtocol = null;
	
	private SocketResourceManagerIF socketResourceManager = null;
	
	private final LinkedList<SocketChannel> notRegistedSocketChannelList = new LinkedList<SocketChannel>();
	private Selector readEventOnlySelector = null;

	
	public InputMessageReader(
			String projectName,			
			int index,
			long readSelectorWakeupInterval,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			SocketResourceManagerIF socketResourceManager) {
		this.index = index;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.projectName = projectName;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;

		try {
			readEventOnlySelector = Selector.open();
		} catch (IOException ioe) {
			log.error(String.format("RequetProcessor[%d] selector open fail",
					index), ioe);
			System.exit(1);
		}
		
		
	}

	@Override
	public void addNewSocket(SocketChannel newSC) {
		// clientResourceManager.addNewSocketChannel(newSocketChannelToRegisterWithReadOnlySelector);	
		synchronized (monitor) {
			notRegistedSocketChannelList.addLast(newSC);
		}
		
		// waitingSCQueue.put(sc);

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
			readEventOnlySelector.wakeup();
			try {
				Thread.sleep(readSelectorWakeupInterval);
			} catch (InterruptedException e) {
			}
		} while (newSC.isConnected() && !newSC.isRegistered());
	}

	@Override
	public int getNumberOfSocket() {
		return (notRegistedSocketChannelList.size() + readEventOnlySelector.keys().size());
	}

	/**
	 * 미 등록된 소켓 채널들을 selector 에 등록한다.
	 */
	private void processNewConnection() {
		synchronized (monitor) {
			while (! notRegistedSocketChannelList.isEmpty()) {
				SocketChannel notRegistedSocketChannel = notRegistedSocketChannelList.removeFirst();
				try {
					notRegistedSocketChannel.register(readEventOnlySelector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", 
							projectName, index, notRegistedSocketChannel.hashCode());
					
					socketResourceManager.remove(notRegistedSocketChannel);
				}
			}
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
				int selectionKeyCount = readEventOnlySelector.select();

				if (selectionKeyCount > 0) {
					Set<SelectionKey> selectedKeySet = readEventOnlySelector
							.selectedKeys();
					Iterator<SelectionKey> selectionKeyIterator = selectedKeySet
							.iterator();
					while (selectionKeyIterator.hasNext()) {
						SelectionKey readableSelectionKey = selectionKeyIterator.next();
						selectionKeyIterator.remove();
						SocketChannel readableSocketChannel = (SocketChannel) readableSelectionKey.channel();
						// ByteBuffer lastInputStreamBuffer = null;
						SocketResource fromSocketResource = socketResourceManager
								.getSocketResource(readableSocketChannel);
						
						if (null == fromSocketResource) {
							log.warn(String.format("%s InputMessageReader[%d] socket channel[%d] is no match for ClientResource", projectName, index, readableSocketChannel.hashCode()));
							continue;
						}
						
						SocketOutputStream fromSocketOutputStream = fromSocketResource.getSocketOutputStream();
						ServerExecutorIF fromExecutor = fromSocketResource.getExecutor();
						
						
						try {
							/*lastInputStreamBuffer = clientSocketInputStream.getLastDataPacketBuffer();
							
							do {
								numRead = readableSocketChannel.read(lastInputStreamBuffer);
								if (numRead < 1) break;
								
								if (!lastInputStreamBuffer.hasRemaining()) {
									if (!clientSocketInputStream.canNextDataPacketBuffer()) break;
									lastInputStreamBuffer = clientSocketInputStream.nextDataPacketBuffer();
								}
							} while(true);*/
							
							numRead = fromSocketOutputStream.read(readableSocketChannel);
							
							if (numRead == -1) {
								log.warn(String.format(
										"%s InputMessageReader[%d] socket channel read -1, remove client",
										projectName, index));
								closeClient(readableSelectionKey);
								continue;
							}
							
							
							fromSocketResource.setFinalReadTime();
							
							ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = 
									messageProtocol.S2MList(fromSocketOutputStream);							

							for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectList) {
								fromExecutor.putIntoQueue(new FromLetter(readableSocketChannel, wrapReadableMiddleObject));
							}
						
						} catch (NoMoreDataPacketBufferException e) {
							log.warn(String.format("%s InputMessageReader[%d] NoMoreDataPacketBufferException::%s", 
									projectName, index, e.getMessage()), e);
							closeClient(readableSelectionKey);
							continue;
						} catch (HeaderFormatException e) {
							log.warn(String.format("%s InputMessageReader[%d] HeaderFormatException::%s", 
									projectName, index, e.getMessage()), e);
							closeClient(readableSelectionKey);
							continue;
						} catch (NotYetConnectedException e) {
							log.warn("io error", e);
							closeClient(readableSelectionKey);
							continue;
						} catch (IOException e) {
							log.warn(String.format("%s InputMessageReader[%d] error", projectName, index), e);
							closeClient(readableSelectionKey);
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
	 * @param readableSelectionKey
	 *            selector 에서 얻는 SelectionKey로 작업대상에서 제외할 client
	 * @see ClientResouceManager#removeClient(java.nio.channels.SocketChannel)
	 */
	private void closeClient(SelectionKey readableSelectionKey) {
		readableSelectionKey.cancel();
		SocketChannel readableSocketChannel = (SocketChannel) readableSelectionKey.channel();
		socketResourceManager.remove(readableSocketChannel);
	}
}
