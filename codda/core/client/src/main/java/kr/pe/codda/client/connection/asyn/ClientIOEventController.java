package kr.pe.codda.client.connection.asyn;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class ClientIOEventController extends Thread implements
		ClientIOEventControllerIF {
	private InternalLogger log = InternalLoggerFactory
			.getInstance(ClientIOEventController.class);

	private long clientSelectorWakeupInterval;
	// private AsynConnectionPoolIF asynConnectionPool = null;

	private Selector ioEventSelector = null;
	private ConcurrentHashMap<SelectionKey, ClientIOEventHandlerIF> selectedKey2ConnectionHash = new ConcurrentHashMap<SelectionKey, ClientIOEventHandlerIF>();
	private LinkedBlockingDeque<ClientIOEventHandlerIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<ClientIOEventHandlerIF>();

	public ClientIOEventController(long clientSelectorWakeupInterval) throws IOException,
			NoMoreDataPacketBufferException {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;

		ioEventSelector = Selector.open();

		/**
		 * WARNING! 반듯이 AsynConnectionPoolIF#setAsynSelectorManger 를 통해 이 객체 등록이
		 * 선행되어야 한다
		 */
		/*try {
			asynConnectionPool.fillAllConnection();
		} catch (InterruptedException e) {
			log.warn("연결 폴 구성을 위한 추가 작업중 인터럽트가 발생하였지만 인터럽트 무시하고 진행");
		}*/
	}

	@Override
	public void addUnregisteredAsynConnection(
			ClientIOEventHandlerIF unregisteredAsynConnection) {
		unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
		log.info("the unregisteredAsynConnection[{}] was registered to queue",
				unregisteredAsynConnection.hashCode());
	}

	private void processNewConnection() {
		while (! unregisteredAsynConnectionQueue.isEmpty()) {
			ClientIOEventHandlerIF unregisteredAsynConnection = unregisteredAsynConnectionQueue
					.removeFirst();

			boolean isConnectionFinshined;
			try {
				isConnectionFinshined = unregisteredAsynConnection.doConnect();			
			} catch (Exception e) {
				log.warn("fail to connect becase of error", e);
				unregisteredAsynConnection.close();
				unregisteredAsynConnection.doRemoveUnregisteredConnection();
				continue;
			}			

			try {
				SelectionKey registeredSelectionKey = null;
				if (isConnectionFinshined) {					
					registeredSelectionKey = unregisteredAsynConnection
							.register(ioEventSelector, SelectionKey.OP_READ);
					unregisteredAsynConnection.doFinishConnect(registeredSelectionKey);
				} else {
					registeredSelectionKey = unregisteredAsynConnection
							.register(ioEventSelector, SelectionKey.OP_CONNECT);
				}
				
				selectedKey2ConnectionHash.put(registeredSelectionKey,
						unregisteredAsynConnection);

			} catch (Exception e) {
				String errorMessage = new StringBuilder()
				.append("fail to register the socket channel[")
				.append(unregisteredAsynConnection.hashCode())
				.append("] on selector").toString();
				log.warn(errorMessage, e);
				unregisteredAsynConnection.close();
				unregisteredAsynConnection.doRemoveUnregisteredConnection();
			}
		}
	}
	

	@Override
	public void run() {
		log.info("ClientIOEventController Thread start");

		try {
			while (! Thread.currentThread().isInterrupted()) {				
				processNewConnection();

				ioEventSelector.select(clientSelectorWakeupInterval);

				Set<SelectionKey> selectedKeySet = ioEventSelector
						.selectedKeys();
				for (SelectionKey selectedKey : selectedKeySet) {
					try {
						if (selectedKey.isConnectable()) {
							ClientIOEventHandlerIF interestedAsynConnection = selectedKey2ConnectionHash
									.get(selectedKey);
							interestedAsynConnection.onConnect(selectedKey);
						}
						
						if (selectedKey.isReadable()) {
							ClientIOEventHandlerIF interestedAsynConnection = selectedKey2ConnectionHash
									.get(selectedKey);
							interestedAsynConnection.onRead(selectedKey);
						}

						if (selectedKey.isWritable()) {
							ClientIOEventHandlerIF interestedAsynConnection = selectedKey2ConnectionHash
									.get(selectedKey);

							interestedAsynConnection.onWrite(selectedKey);

						}
					} catch (InterruptedException e) {
						throw e;						
					} catch (Exception e) {						
						log.warn("error", e);
						continue;
					}
				}
				selectedKeySet.clear();
			}
		} catch (InterruptedException e) {
			log.warn("Thread stop", e);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().toString();
			log.warn(errorMessage, e);
		}
		log.info("ClientIOEventController Thread end");
	}

	public void cancel(SelectionKey selectedKey) {
		if (null == selectedKey) {
			return;
		}
		selectedKey2ConnectionHash.remove(selectedKey);
		selectedKey.channel();
	}

	@Override
	public void wakeup() {
		ioEventSelector.wakeup();
	}
}
