package kr.pe.codda.client;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.exception.ConnectionPoolException;

public class SingleAyncShareConnectionAdder implements AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SingleAyncShareConnectionAdder.class);
	
	private final Object monitor = new Object();
	private AsynConnectionIF connectedAsynConnection = null;
	private boolean isSocketTimeout=false;
	 

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) throws ConnectionPoolException {
		synchronized (monitor) {
			if (isSocketTimeout) {
				log.warn("socket timeout occured so drop the connected asyn share connection");
				try {
					connectedAsynConnection.close();
				} catch (IOException e) {
				}
				return;
			}
			this.connectedAsynConnection = connectedAsynConnection;
			monitor.notify();
		}
		
		log.warn("add the connected asyn connection[{}]", connectedAsynConnection.hashCode());
	}

	@Override
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection) {
		log.warn("remove the interested asyn connection[{}]", interestedAsynConnection.hashCode());
	}
	
	
	public AsynConnectionIF poll(long socketTimeout) throws InterruptedException, SocketTimeoutException {
		synchronized (monitor) {
			if (null == connectedAsynConnection) {
				monitor.wait(socketTimeout);
				
				if (null == connectedAsynConnection) {
					isSocketTimeout = true;
					throw new SocketTimeoutException();
				}
			}
			
			return connectedAsynConnection;
		}
	}

}
