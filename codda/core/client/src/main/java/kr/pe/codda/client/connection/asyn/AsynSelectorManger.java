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

public class AsynSelectorManger extends Thread implements AsynSelectorMangerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynSelectorManger.class);
	
	private Selector ioEventSelector = null;
	

	private AsynConnectionPoolIF asynConnectionPool = null;
	private ConcurrentHashMap<SelectionKey, InterestedAsynConnectionIF> selectedKey2ConnectionHash = 
			new ConcurrentHashMap<SelectionKey, InterestedAsynConnectionIF>();
	
	private LinkedBlockingDeque<InterestedAsynConnectionIF> unregisteredAsynConnectionQueue = new LinkedBlockingDeque<InterestedAsynConnectionIF>();
	
	public AsynSelectorManger(AsynConnectionPoolIF connectionPool) throws IOException, NoMoreDataPacketBufferException {
		this.asynConnectionPool = connectionPool;
		
		asynConnectionPool.setAsynSelectorManger(this);
		
		ioEventSelector = Selector.open();
		
		while (connectionPool.isConnectionToAdd()) {
			InterestedAsynConnectionIF unregisteredAsynConnection = connectionPool.newUnregisteredConnection();
			connectionPool.addCountOfUnregisteredConnection();			
			unregisteredAsynConnectionQueue.addLast(unregisteredAsynConnection);
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
		log.debug("Thread start");
		
		try {
			while (! isInterrupted()) {
				processNewConnection();
				
				ioEventSelector.select();
				Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();
				for (SelectionKey selectedKey : selectedKeySet) {
					InterestedAsynConnectionIF  interestedAsynConnection = selectedKey2ConnectionHash.get(selectedKey);
					if (selectedKey.isConnectable()) {
						interestedAsynConnection.onConnect(selectedKey);						
					} else if (selectedKey.isReadable()) {
						interestedAsynConnection.onRead(selectedKey);
					} else if (selectedKey.isWritable()) {
						interestedAsynConnection.onWrite(selectedKey);
					}
				}
				selectedKeySet.clear();
				
				if (true) {
					throw new InterruptedException();
				}
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
		assert(null == selectedKey);
		selectedKey.interestOps(selectedKey.interestOps() | SelectionKey.OP_WRITE);
	}
	
	@Override
	public void endWrite(InterestedAsynConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		assert(null == selectedKey);
		selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
	}
}
