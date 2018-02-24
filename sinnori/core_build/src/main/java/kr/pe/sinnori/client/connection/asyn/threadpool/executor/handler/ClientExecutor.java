package kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler;

import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class ClientExecutor extends Thread implements ClientExecutorIF {
	private Logger log = LoggerFactory.getLogger(ClientExecutor.class);
	
	
	private String projectName = null;
	private int index;
	
	private LinkedBlockingQueue<FromLetter> outputMessageQueue;
	
	private MessageProtocolIF messageProtocol = null;
	private ClientObjectCacheManagerIF clientObjectCacheManager = null;
	
	
	private Hashtable<SocketChannel, AbstractAsynConnection> sc2AsynConnectionHash = 
			new Hashtable<SocketChannel, AbstractAsynConnection>();
			
	
	public ClientExecutor(String projectName, int index,  
			LinkedBlockingQueue<FromLetter> outputMessageQueue,
			MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager) {
		this.projectName = projectName;
		this.index = index;		
		this.outputMessageQueue = outputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.clientObjectCacheManager = clientObjectCacheManager;
	}
	
	
	public void run() {
		log.warn("{} ClientExecutor[{}] start", projectName, index);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				FromLetter fromLetter = outputMessageQueue.take();

				SocketChannel fromSC = fromLetter.getFromSocketChannel();
				
				AbstractAsynConnection asynConnection = sc2AsynConnectionHash.get(fromSC);
				if (null == asynConnection) {
					log.warn("fail to get a asyn connection[isConnected={}] from hash, fromLetter={}", fromSC.isConnected(), fromLetter.toString());
					continue;
				}
				
				
				WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();
				
				AbstractClientTask clientTask = clientObjectCacheManager.getClientTask(messageID);
				
				clientTask.execute(index, messageID, asynConnection, wrapReadableMiddleObject, messageProtocol, clientObjectCacheManager);
			}
			log.warn("{} ClientExecutor[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} ClientExecutor[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = new StringBuilder(projectName)
					.append(" ClientExecutor[").append(index)
					.append("] unknown error::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
		}
	}
	
	

	@Override
	public void addNewAsynConnection(AbstractAsynConnection asynConnection) {
		log.info("add asynConnection[{}]", asynConnection.hashCode());		
		
		sc2AsynConnectionHash.put(asynConnection.getSocketChannel(), asynConnection);
	}

	@Override
	public int getNumberOfAsynConnection() {
		return sc2AsynConnectionHash.size();
	}

	@Override
	public void removeAsynConnection(AbstractAsynConnection asynConnection) {
		sc2AsynConnectionHash.remove(asynConnection.getSocketChannel());
	}

	@Override
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException {
		outputMessageQueue.put(fromLetter);
	}	

}
