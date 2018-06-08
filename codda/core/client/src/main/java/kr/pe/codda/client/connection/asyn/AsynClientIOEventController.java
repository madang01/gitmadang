package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class AsynClientIOEventController extends Thread implements AsynClientIOEventControllerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynClientIOEventController.class);
	
	private long clientSelectorWakeupInterval;
	private AsynConnectionPoolIF asynConnectionPool = null;
	
	private Selector ioEventSelector = null;
	private ConcurrentHashMap<SelectionKey, ClientInterestedConnectionIF> selectedKey2ConnectionHash = 
			new ConcurrentHashMap<SelectionKey, ClientInterestedConnectionIF>();
	private LinkedBlockingDeque<ClientInterestedConnectionIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<ClientInterestedConnectionIF>();
	
	
	
	public AsynClientIOEventController(long clientSelectorWakeupInterval, AsynConnectionPoolIF connectionPool) throws IOException, NoMoreDataPacketBufferException {
		this.clientSelectorWakeupInterval = clientSelectorWakeupInterval;
		this.asynConnectionPool = connectionPool;
		
		asynConnectionPool.setAsynSelectorManger(this);
		
		ioEventSelector = Selector.open();
		
		/** WARNING! 반듯이  AsynConnectionPoolIF#setAsynSelectorManger 를 통해 이 객체 등록이 선행되어야 한다 */
		while (connectionPool.isConnectionToAdd()) {
			connectionPool.addConnection();
		}
		
		// selectorAlarm = new SelectorAlarm(ioEventSelector, this.clientSelectorWakeupInterval);
		// selectorAlarm.start();
	}

	@Override
	public void addUnregisteredAsynConnection(ClientInterestedConnectionIF unregisteredAsynConnection) {
		/*if (getState().equals(Thread.State.NEW)) {
			unregisteredAsynConnection.close();			
			asynConnectionPool.removeUnregisteredConnection(unregisteredAsynConnection);
			return;
		}*/
		
		unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
		
		log.info("the unregisteredAsynConnection[{}] was registered to queue", unregisteredAsynConnection.hashCode());
	}
	
	public void callSelectorAlarm() throws InterruptedException {	
		// FIXME!
		// selectorAlarm.startWork();
		ioEventSelector.wakeup();
	}
	
	
	private void processNewConnection() {					
		while (! unregisteredAsynConnectionQueue.isEmpty()) {
			ClientInterestedConnectionIF unregisteredAsynConnection = unregisteredAsynConnectionQueue.removeFirst();
			
			// FIXME!
			// log.info("unregisteredAsynConnection[{}] start", unregisteredAsynConnection.hashCode());
			
			boolean isConnected;
			try {
				isConnected = unregisteredAsynConnection.doConect();
			} catch (IOException e) {
				unregisteredAsynConnection.close();
				asynConnectionPool.removeUnregisteredConnection(unregisteredAsynConnection);				
				continue;
			}
			
			try {
				SelectionKey registeredSelectionKey = null;
				if (isConnected) {
					registeredSelectionKey = unregisteredAsynConnection.register(ioEventSelector, SelectionKey.OP_READ);
					unregisteredAsynConnection.onConnect(registeredSelectionKey);
				} else {
					registeredSelectionKey = unregisteredAsynConnection.register(ioEventSelector, SelectionKey.OP_CONNECT);
				}				
				
				selectedKey2ConnectionHash.put(registeredSelectionKey, unregisteredAsynConnection);				
				
			} catch (ClosedChannelException e) {
				log.warn("fail to register the socket channel[{}] on selector, errmsg={}", 
						unregisteredAsynConnection.hashCode(),
						e.getMessage());
				unregisteredAsynConnection.close();
				asynConnectionPool.removeUnregisteredConnection(unregisteredAsynConnection);
			}
		}
	}
	
	@Override
	public void run() {
		log.info("AsynSelectorManger Thread start");
		
		try {
			while (! isInterrupted()) {
				processNewConnection();
				
				
				ioEventSelector.select(clientSelectorWakeupInterval);
				// log.info("111111111111");
				// selectorAlarm.stopWork();
				
				Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();
				for (SelectionKey selectedKey : selectedKeySet) {	
					/*if (! selectedKey.isValid()) {
						ClientInterestedConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						if (null == interestedAsynConnection) {
							log.info("this selectedKey2ConnectionHash map contains no mapping for the key that is the var selectedKey[hashcode={}, socket channel={}]", selectedKey.hashCode(), selectedKey.channel().hashCode());
							continue;
						}
						
						interestedAsynConnection.close();
						cancel(selectedKey);						
						continue;
					}*/
					
					if (selectedKey.isConnectable()) {
						ClientInterestedConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						interestedAsynConnection.onConnect(selectedKey);
					} 
					
					if (selectedKey.isReadable()) {
						ClientInterestedConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						interestedAsynConnection.onRead(selectedKey);
					}
					
					if (selectedKey.isWritable()) {
						ClientInterestedConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						interestedAsynConnection.onWrite(selectedKey);
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
		log.debug("Thread end");
	}

	@Override
	public void startWrite(ClientInterestedConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.warn("selectedKey is null");
			return;
		}
		
		// FIXME!
		// log.info("before interestOps={}", selectedKey.interestOps());
		selectedKey.interestOps(selectedKey.interestOps() | SelectionKey.OP_WRITE);
		ioEventSelector.wakeup();
		// log.info("after interestOps={}", selectedKey.interestOps());
		// selectorAlarm.startWork();
	}
	
	@Override
	public void endWrite(ClientInterestedConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.warn("selectedKey is null");
			return;
		}
		selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
		// ioEventSelector.wakeup();
	}
	
	public void cancel(SelectionKey selectedKey) {
		selectedKey2ConnectionHash.remove(selectedKey);
		selectedKey.channel();
	}
	
	
}
