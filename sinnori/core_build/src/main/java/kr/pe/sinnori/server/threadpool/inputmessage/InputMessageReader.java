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

package kr.pe.sinnori.server.threadpool.inputmessage;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;

public class InputMessageReader extends Thread implements InputMessageReaderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(InputMessageReader.class);

	// private final Object monitor = new Object();

	private String projectName = null;
	private int index;
	private long wakeupIntervalOfSelectorForReadEventOnley;
	private MessageProtocolIF messageProtocol = null;

	private SocketResourceManagerIF socketResourceManager = null;

	
	private final ConcurrentHashMap<SocketChannel, SocketChannel> notRegistedSocketChannelHash 
		= new ConcurrentHashMap<SocketChannel, SocketChannel>();

	private Selector selectorForReadEventOnly = null;

	public InputMessageReader(String projectName, int index, long wakeupIntervalOfSelectorForReadEventOnley,
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager) {
		this.index = index;
		this.wakeupIntervalOfSelectorForReadEventOnley = wakeupIntervalOfSelectorForReadEventOnley;
		this.projectName = projectName;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;

		try {
			selectorForReadEventOnly = Selector.open();
		} catch (IOException ioe) {
			log.error(String.format("RequetProcessor[%d] selector open fail", index), ioe);
			System.exit(1);
		}

	}

	@Override
	public void addNewSocket(SocketChannel newSC) throws InterruptedException {
		// log.info("call newSC={}", newSC.hashCode());
		
		// clientResourceManager.addNewSocketChannel(newSocketChannelToRegisterWithReadOnlySelector);
		// synchronized (monitor) {
		notRegistedSocketChannelHash.put(newSC, newSC);
		// }

		// waitingSCQueue.put(sc);

		if (getState().equals(Thread.State.NEW)) {
			return;
		}
		
		boolean loop = false;
		do {
			selectorForReadEventOnly.wakeup();

			try {
				Thread.sleep(wakeupIntervalOfSelectorForReadEventOnley);
			} catch (InterruptedException e) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket has occurred", newSC.hashCode());
				throw e;
			}
			if (! newSC.isOpen()) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket is not open", newSC.hashCode());
				return;
			}
			
			if (! newSC.isConnected()) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket is not connected", newSC.hashCode());
				return;
			}
			
			loop = ! newSC.isRegistered();
			
			// log.info("loop={}", loop);
			
		} while (loop);
		
		log.debug("{} InputMessageReader[{}] new newSC[{}] added", projectName, index, newSC.hashCode());
	}

	@Override
	public int getNumberOfConnection() {
		return (notRegistedSocketChannelHash.size() + selectorForReadEventOnly.keys().size());
	}

	/**
	 * 미 등록된 소켓 채널들을 selector 에 등록한다.
	 */
	private void processNewConnection() {
		if (notRegistedSocketChannelHash.isEmpty()) {
			return;
		}
		
		Enumeration<SocketChannel> notRegistedSocketChannelEnumeration = notRegistedSocketChannelHash.keys();
		
		while(notRegistedSocketChannelEnumeration.hasMoreElements()) {
			SocketChannel notRegistedSocketChannel = notRegistedSocketChannelEnumeration.nextElement();
			
			notRegistedSocketChannelHash.remove(notRegistedSocketChannel);
			
			try {
				notRegistedSocketChannel.register(selectorForReadEventOnly, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", projectName, index,
						notRegistedSocketChannel.hashCode());

				SocketResource failedSocketResource = socketResourceManager.getSocketResource(notRegistedSocketChannel);
				if (null == failedSocketResource) {
					log.warn("this scToAsynConnectionHash contains no mapping for the key[{}] that is the socket channel failed to be registered with the given selector", notRegistedSocketChannel.hashCode());
				} else {
					failedSocketResource.close();
					socketResourceManager.remove(notRegistedSocketChannel);
				}
				
			}
		}
	}

	@Override
	public void run() {
		log.info("{} InputMessageReader[{}] start", projectName, index);

		int numRead = 0;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();
				int selectionKeyCount = selectorForReadEventOnly.select();

				if (selectionKeyCount > 0) {
					Set<SelectionKey> selectedKeySet = selectorForReadEventOnly.selectedKeys();
					
					for (SelectionKey selectedKey : selectedKeySet) {
						SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();
						
						// FIXME!
						// log.info("{} InputMessageReader[{}] selectedSocketChannel[{}]", projectName, index, selectedSocketChannel.hashCode());
						
						
						// ByteBuffer lastInputStreamBuffer = null;
						SocketResource fromSocketResource = socketResourceManager
								.getSocketResource(selectedSocketChannel);

						if (null == fromSocketResource) {
							log.warn("{} InputMessageReader[{}] this socket channel[{}] resoruce was not found",
									projectName, index, selectedSocketChannel.hashCode());
							continue;
						}
						
						// log.info("getNumberOfSocketResources={}", socketResourceManager.getNumberOfSocketResources());

						SocketOutputStream fromSocketOutputStream = fromSocketResource.getSocketOutputStream();
						ServerExecutorIF fromExecutor = fromSocketResource.getExecutor();

						try {
							/*
							 * lastInputStreamBuffer = clientSocketInputStream.getLastDataPacketBuffer();
							 * 
							 * do { numRead = readableSocketChannel.read(lastInputStreamBuffer); if (numRead
							 * < 1) break;
							 * 
							 * if (!lastInputStreamBuffer.hasRemaining()) { if
							 * (!clientSocketInputStream.canNextDataPacketBuffer()) break;
							 * lastInputStreamBuffer = clientSocketInputStream.nextDataPacketBuffer(); } }
							 * while(true);
							 */

							numRead = fromSocketOutputStream.read(selectedSocketChannel);

							if (numRead == -1) {								
								log.warn("{} InputMessageReader[{}] this socket channel[{}] has reached end-of-stream",
												projectName, index, selectedSocketChannel.hashCode());
								closeClient(selectedKey, fromSocketResource);
								continue;
							}
							
							fromSocketResource.setFinalReadTime();

							List<WrapReadableMiddleObject> wrapReadableMiddleObjectList = messageProtocol
									.S2MList(fromSocketOutputStream);
							
							Iterator<WrapReadableMiddleObject> wrapReadableMiddleObjectIterator =
									wrapReadableMiddleObjectList.iterator();
							
							try {
								while (wrapReadableMiddleObjectIterator.hasNext()) {
									WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectIterator.next();
									
									fromExecutor
											.putIntoQueue(new FromLetter(selectedSocketChannel, wrapReadableMiddleObject));
									
									
								}
							} catch(InterruptedException e) {
								while (wrapReadableMiddleObjectIterator.hasNext()) {
									WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectIterator.next();
									
									log.info("drop the input message[{}] becase of InterruptedException", wrapReadableMiddleObject.toString());
									
									wrapReadableMiddleObject.closeReadableMiddleObject();								
								}
								throw e;
							}

						} catch (NoMoreDataPacketBufferException e) {
							String errorMessage = String.format(
									"%s InputMessageReader[%d] NoMoreDataPacketBufferException::%s", projectName, index,
									e.getMessage());
							log.warn(errorMessage, e);
							closeClient(selectedKey, fromSocketResource);
							continue;						
						} catch (IOException e) {
							String errorMessage = String.format("%s InputMessageReader[%d] IOException::%s",
									projectName, index, e.getMessage());
							log.warn(errorMessage, e);
							closeClient(selectedKey, fromSocketResource);
							continue;
						}
					}
					
					selectedKeySet.clear();
				}
			}

			log.warn("{} InputMessageReader[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} InputMessageReader[{}] stop", projectName, index);			
		} catch (Exception e) {
			String errorMessage = String.format("%s InputMessageReader[%d] unknown error", projectName, index); 
			log.warn(errorMessage, e);
		}
	}

	private void closeClient(SelectionKey selectedKey, SocketResource fromSocketResource) {
		SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

		log.info("close the socket[{}]", selectedSocketChannel.hashCode());

		selectedKey.cancel();

		try {
			selectedSocketChannel.close();
		} catch (IOException e) {
			log.warn("fail to close the socket[{}]", selectedSocketChannel.hashCode());
		}

		fromSocketResource.close();

		socketResourceManager.remove(selectedSocketChannel);
	}
	
	public void finalize() {
		log.warn("{} InputMessageReader[{}] finalize", projectName, index);
	}
}
