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
	
	private Selector ioEventSelector = null;
	

	private AsynConnectionPoolIF asynConnectionPool = null;
	private ConcurrentHashMap<SelectionKey, InterestedAsynConnectionIF> selectedKey2ConnectionHash = 
			new ConcurrentHashMap<SelectionKey, InterestedAsynConnectionIF>();
	
	private LinkedBlockingDeque<InterestedAsynConnectionIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<InterestedAsynConnectionIF>();
	
	public AsynClientIOEventController(AsynConnectionPoolIF connectionPool) throws IOException, NoMoreDataPacketBufferException {
		this.asynConnectionPool = connectionPool;
		
		asynConnectionPool.setAsynSelectorManger(this);
		
		ioEventSelector = Selector.open();
		
		while (connectionPool.isConnectionToAdd()) {
			InterestedAsynConnectionIF unregisteredAsynConnection = connectionPool.newUnregisteredConnection();
			connectionPool.addCountOfUnregisteredConnection();			
			unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
			
			// FIXME!
			log.info("the unregisteredAsynConnection[{}] was registered to queue", unregisteredAsynConnection.hashCode());
		}
	}

	@Override
	public void addUnregisteredAsynConnection(InterestedAsynConnectionIF unregisteredAsynConnection) throws IOException {
		if (getState().equals(Thread.State.NEW)) {
			try {
				unregisteredAsynConnection.close();
			} catch (IOException e) {
				log.warn("fail to close the socket channel[{}] becase this thread state has not yet started, errmsg={}", 
						unregisteredAsynConnection.hashCode(), e.getMessage());
			}
			unregisteredAsynConnection.releaseResources();			
			asynConnectionPool.removeUnregisteredConnection(unregisteredAsynConnection);
			return;
		}
		
		unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
		
		
		ioEventSelector.wakeup();		
	}
	
	private void processNewConnection() {					
		while (! unregisteredAsynConnectionQueue.isEmpty()) {
			InterestedAsynConnectionIF unregisteredAsynConnection = unregisteredAsynConnectionQueue.removeFirst();
			
			// FIXME!
			// log.info("unregisteredAsynConnection[{}] start", unregisteredAsynConnection.hashCode());
			
			boolean isConnected;
			try {
				isConnected = unregisteredAsynConnection.doConect();
			} catch (IOException e) {
				try {
					unregisteredAsynConnection.close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
							unregisteredAsynConnection.hashCode(), e1.getMessage());
				}
				unregisteredAsynConnection.releaseResources();
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

				try {
					unregisteredAsynConnection.close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}], errmsg={}", 
							unregisteredAsynConnection.hashCode(), e1.getMessage());
				}
				unregisteredAsynConnection.releaseResources();
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
				
				ioEventSelector.select();
				Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();
				for (SelectionKey selectedKey : selectedKeySet) {					
					if (selectedKey.isConnectable()) {
						InterestedAsynConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						interestedAsynConnection.onConnect(selectedKey);
					} else if (selectedKey.isReadable()) {
						InterestedAsynConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
						interestedAsynConnection.onRead(selectedKey);
					} else if (selectedKey.isWritable()) {
						InterestedAsynConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
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
	public void startWrite(InterestedAsynConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.error("selectedKey is null");
			System.exit(1);
		}
		
		// FIXME!
		// log.info("before interestOps={}", selectedKey.interestOps());
		selectedKey.interestOps(selectedKey.interestOps() | SelectionKey.OP_WRITE);
		// log.info("after interestOps={}", selectedKey.interestOps());
		
		ioEventSelector.wakeup();
	}
	
	@Override
	public void endWrite(InterestedAsynConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.error("selectedKey is null");
			System.exit(1);
		}
		selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
		ioEventSelector.wakeup();
	}
}
