package kr.pe.codda.client.connection.asyn;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
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

	private long clientSelectorWakeupInterval = 0L;

	private Selector ioEventSelector = null;
	private ConcurrentHashMap<SelectionKey, ClientIOEventHandlerIF> selectedKey2ConnectionHash = new ConcurrentHashMap<SelectionKey, ClientIOEventHandlerIF>();
	private LinkedBlockingDeque<ClientIOEventHandlerIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<ClientIOEventHandlerIF>();

	public ClientIOEventController(long clientSelectorWakeupInterval) throws IOException,
			NoMoreDataPacketBufferException {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;

		ioEventSelector = Selector.open();
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
				unregisteredAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();
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
				unregisteredAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();
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
					ClientIOEventHandlerIF interestedAsynConnection = selectedKey2ConnectionHash
							.get(selectedKey);
					
					if (null == interestedAsynConnection) {
						log.warn("the var interestedAsynConnection[{}] is null", selectedKey.channel().hashCode());
						continue;
					}
					
					try {
						if (selectedKey.isConnectable()) {
							interestedAsynConnection.onConnect(selectedKey);
							continue;
						}
					} catch (IOException e) {
						String errorMessage = new StringBuilder()
								.append("fail to finish connection[").append(interestedAsynConnection.hashCode())
								.append("] becase io error occured, errmsg=")
								.append(e.getMessage()).toString();
						log.warn(errorMessage);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					} catch (CancelledKeyException e) {
						String errorMessage = new StringBuilder()
								.append("this selector key[socket channel=")
								.append(interestedAsynConnection.hashCode())
								.append("] has been cancelled")
								.toString();
						log.warn(errorMessage);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("fail to finish connection[").append(interestedAsynConnection.hashCode())
								.append("] becase unknown error occured").toString();
						log.warn(errorMessage, e);

						interestedAsynConnection.close();
						interestedAsynConnection.doSubtractOneFromNumberOfUnregisteredConnections();

						continue;
					}
					
					try {
						if (selectedKey.isReadable()) {
							
							interestedAsynConnection.onRead(selectedKey);
						}

						if (selectedKey.isWritable()) {
							interestedAsynConnection.onWrite(selectedKey);

						}
					} catch (InterruptedException e) {
						String errorMessage = new StringBuilder()
								.append("InterruptedException occurred while reading the socket[")
								.append(interestedAsynConnection.hashCode()).append("]").toString();
						log.warn(errorMessage);
						interestedAsynConnection.close();
						throw e;
					} catch (NoMoreDataPacketBufferException e) {
						String errorMessage = new StringBuilder()
								.append("the no more data packet buffer error occurred while reading the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.warn(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (IOException e) {
						String errorMessage = new StringBuilder()
								.append("the io error occurred while reading or writing the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.warn(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (CancelledKeyException e) {
						String errorMessage = new StringBuilder()
						.append("this selector key[socket channel=")
						.append(interestedAsynConnection.hashCode())
						.append("] has been cancelled")
						.toString();
						
						log.warn(errorMessage);
						interestedAsynConnection.close();
						continue;
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("the unknown error occurred while reading or writing the socket[")
								.append(interestedAsynConnection.hashCode()).append("], errmsg=")
								.append(e.getMessage()).toString();
						log.warn(errorMessage, e);
						interestedAsynConnection.close();
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
		selectedKey.cancel();
	}

	@Override
	public void wakeup() {
		ioEventSelector.wakeup();
	}
}
