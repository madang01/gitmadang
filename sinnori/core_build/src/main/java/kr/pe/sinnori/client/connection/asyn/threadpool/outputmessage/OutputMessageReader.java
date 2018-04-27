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

package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
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
	private InternalLogger log = InternalLoggerFactory.getInstance(OutputMessageReader.class);

	// private final Object monitor = new Object();

	private String projectName = null;
	/** 출력 메시지를 읽는 쓰레드 번호 */
	private int index;
	private MessageProtocolIF messageProtocol = null;

	/** selector 에 등록할 신규 소켓 채널을 담고 있는 그릇 */
	// private final ArrayDeque<SocketChannel> notRegistedSocketChannelList = new
	// ArrayDeque<SocketChannel>();
	private final ConcurrentLinkedQueue<SocketChannel> notRegistedSocketChannelQueue 
	= new ConcurrentLinkedQueue<SocketChannel>();

	/** 읽기 전용 selecotr */
	private Selector selectorForReadEventOnly = null;

	/** selector 를 깨우는 간격 */
	private long wakeupIntervalOfSelectorForReadEventOnley;

	private ConcurrentHashMap<SocketChannel, IOEAsynConnectionIF> scToAsynConnectionHash = new ConcurrentHashMap<SocketChannel, IOEAsynConnectionIF>();

	public OutputMessageReader(String projectName, int index, long wakeupIntervalOfSelectorForReadEventOnley,
			MessageProtocolIF messageProtocol) {
		this.projectName = projectName;
		this.index = index;
		this.wakeupIntervalOfSelectorForReadEventOnley = wakeupIntervalOfSelectorForReadEventOnley;
		this.messageProtocol = messageProtocol;
		try {
			selectorForReadEventOnly = Selector.open();
		} catch (IOException ioe) {
			log.error("selector open fail", ioe);
			System.exit(1);
		}
	}

	@Override
	public int getNumberOfConnection() {
		return (notRegistedSocketChannelQueue.size() + selectorForReadEventOnly.keys().size());
	}

	/**
	 * 신규 채널를 selector 에 읽기 이벤트 등록한다.
	 */
	private void processNewConnection() {
		/*if (notRegistedSocketChannelHash.isEmpty()) {
			return;
		}
		
		Enumeration<SocketChannel> notRegistedSocketChannelEnumeration = notRegistedSocketChannelHash.keys();
		
		while (notRegistedSocketChannelEnumeration.hasMoreElements()) {
			SocketChannel notRegistedSocketChannel = notRegistedSocketChannelEnumeration.nextElement();
			
			notRegistedSocketChannelHash.remove(notRegistedSocketChannel);
			
			try {
				notRegistedSocketChannel.register(selectorForReadEventOnly, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", projectName, index,
						notRegistedSocketChannel.hashCode());

				IOEAsynConnectionIF asynConnection = scToAsynConnectionHash.get(notRegistedSocketChannel);
				if (null == asynConnection) {
					log.warn(
							"this scToAsynConnectionHash contains no mapping for the key[{}] that is the socket channel failed to be registered with the given selector",
							notRegistedSocketChannel.hashCode());
				} else {
					asynConnection.noticeThisConnectionWasRemovedFromReadyOnleySelector();
					scToAsynConnectionHash.remove(notRegistedSocketChannel);
				}
			}
		}	*/
		
		while (! notRegistedSocketChannelQueue.isEmpty()) {
			SocketChannel notRegistedSocketChannel = notRegistedSocketChannelQueue.poll();
			try {
				notRegistedSocketChannel.register(selectorForReadEventOnly, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", projectName, index,
						notRegistedSocketChannel.hashCode());

				IOEAsynConnectionIF asynConnection = scToAsynConnectionHash.get(notRegistedSocketChannel);
				if (null == asynConnection) {
					log.warn(
							"this scToAsynConnectionHash contains no mapping for the key[{}] that is the socket channel failed to be registered with the given selector",
							notRegistedSocketChannel.hashCode());
				} else {
					asynConnection.noticeThisConnectionWasRemovedFromReadyOnleySelector();
					scToAsynConnectionHash.remove(notRegistedSocketChannel);
				}
			}
		}
	}

	@Override
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection) throws InterruptedException {
		SocketChannel newSC = asynConnection.getSocketChannel();

		scToAsynConnectionHash.put(newSC, asynConnection);

		notRegistedSocketChannelQueue.add(newSC);

		if (getState().equals(Thread.State.NEW)) {
			return;
		}

		boolean loop = false;
		do {
			selectorForReadEventOnly.wakeup();

			try {
				Thread.sleep(wakeupIntervalOfSelectorForReadEventOnley);
			} catch (InterruptedException e) {
				log.info(
						"give up the test checking whether the new AsynConnection[{}] is registered with the Selector because the interrupt has occurred",
						asynConnection.hashCode());
				throw e;
			}

			if (!newSC.isOpen()) {
				log.info(
						"give up the test checking whether the new AsynConnection[{}] is registered with the Selector because the connection is not open",
						asynConnection.hashCode());
				return;
			}

			if (!newSC.isConnected()) {
				log.info(
						"give up the test checking whether the new AsynConnection[{}] is registered with the Selector because the connection is not connected",
						asynConnection.hashCode());
				return;
			}

			loop = !newSC.isRegistered();

		} while (loop);

		log.debug("{} OutputMessageReader[{}] new AsynConnection[{}][{}] added", projectName, index,
				asynConnection.hashCode());
	}

	@Override
	public void run() {
		log.info("{} OutputMessageReader[{}] start", projectName, index);

		// int numRead = 0;
		// long totalRead = 0;
		ArrayDeque<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayDeque<WrapReadableMiddleObject>();

		try {
			while (! isInterrupted()) {
				processNewConnection();

				int numberOfKeys = selectorForReadEventOnly.select();

				if (numberOfKeys > 0) {
					Set<SelectionKey> selectedKeySet = selectorForReadEventOnly.selectedKeys();
					
					for (SelectionKey selectedKey : selectedKeySet) {
						SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

						// FIXME!
						// log.info("{} OutputMessageReader[{}] selectedSocketChannel=[{}]", projectName, index, selectedSocketChannel.hashCode());

						// log.info("11111111111111111");

						IOEAsynConnectionIF asynConnection = scToAsynConnectionHash.get(selectedSocketChannel);
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
										.append("] has reached end-of-stream in OutputMessageReader[").append(index)
										.append("]").toString();

								log.warn(errorMessage);
								closeSocket(selectedKey, asynConnection);
								continue;
							}

							asynConnection.setFinalReadTime();

							messageProtocol.S2MList(socketOutputStream, wrapReadableMiddleObjectQueue);

							
							// final int wrapReadableMiddleObjectListSize = wrapReadableMiddleObjectList.size();
							
							
								while(! wrapReadableMiddleObjectQueue.isEmpty()) {
									
									WrapReadableMiddleObject wrapReadableMiddleObject =
											wrapReadableMiddleObjectQueue.pollFirst();
											
									
									wrapReadableMiddleObject.setFromSC(selectedSocketChannel);
									
									
									try {
										asynConnection.putToOutputMessageQueue(wrapReadableMiddleObject);
									} catch(InterruptedException e) {
										log.info("1.drop the input message[{}] becase of InterruptedException",
												wrapReadableMiddleObject.toString());

										wrapReadableMiddleObject.closeReadableMiddleObject();
										
										
										while(! wrapReadableMiddleObjectQueue.isEmpty()) {
											WrapReadableMiddleObject nextWrapReadableMiddleObject = 
													wrapReadableMiddleObjectQueue.pollFirst();
											
											nextWrapReadableMiddleObject.setFromSC(selectedSocketChannel);
											
											log.info("2.drop the input message[{}] becase of InterruptedException",
													nextWrapReadableMiddleObject.toString());

											nextWrapReadableMiddleObject.closeReadableMiddleObject();
										}
										throw e;
									}
								}
								
						} catch (NoMoreDataPacketBufferException e) {
							String errorMessage = String.format("%s OutputMessageReader[%d]::%s",
									asynConnection.getSimpleConnectionInfo(), index, e.getMessage());
							log.warn(errorMessage, e);
							closeSocket(selectedKey, asynConnection);
							continue;
						} catch (IOException e) {
							String errorMesage = String.format("%s OutputMessageReader[%d]::%s",
									asynConnection.getSimpleConnectionInfo(), index, e.getMessage());
							log.warn(errorMesage, e);
							closeSocket(selectedKey, asynConnection);
							continue;
						}
					}
					
					selectedKeySet.clear();
				}
			}

			log.warn("{} OutputMessageReader[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} OutputMessageReader[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = String.format("%s OutputMessageReader[%d] unknown error", projectName, index);
			log.warn(errorMessage, e);
		}

		// log.warn(String.format("%s OutputMessageReader[%d] Thread end",
		// clientProjectConfig.getProjectName(), index));
		log.info("{} OutputMessageReader[{}] end", projectName, index);
	}

	/**
	 * selector 로 얻는 SelectionKey 가 가진 소켓 채널을 닫고 selector 에서 제거한다.
	 * 
	 * @param selectedKey
	 */
	private void closeSocket(SelectionKey selectedKey, IOEAsynConnectionIF selectedAsynConnection) {
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

	/*
	 * private void closeFailedSocket(SocketChannel failedSocketChannel) {
	 * log.info("2. close the socket[{}]", failedSocketChannel.hashCode());
	 * 
	 * AbstractAsynConnection failedAsynConnection =
	 * scToAsynConnectionHash.get(failedSocketChannel); if (null !=
	 * failedAsynConnection) { scToAsynConnectionHash.remove(failedSocketChannel);
	 * 
	 * try { failedAsynConnection.closeSocket(); } catch (IOException e) {
	 * log.warn("fail to close the socket[{}]", failedSocketChannel.hashCode()); }
	 * failedAsynConnection.releaseResources(); } }
	 */
	
	public void finalize() {
		log.warn("{} OutputMessageReader[{}] finalize", projectName, index);
	}

}
