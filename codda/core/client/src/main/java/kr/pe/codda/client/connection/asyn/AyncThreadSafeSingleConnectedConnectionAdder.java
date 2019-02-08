package kr.pe.codda.client.connection.asyn;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.SocketTimeoutException;

public class AyncThreadSafeSingleConnectedConnectionAdder implements AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AyncThreadSafeSingleConnectedConnectionAdder.class);
	
	private final Object monitor = new Object();
	private AsynConnectionIF connectedAsynConnection = null;
	private boolean isSocketTimeout=false;	 

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		synchronized (monitor) {
			if (isSocketTimeout) {
				log.warn("socket timeout occured so drop the connected asyn share connection");
				connectedAsynConnection.close();
				return;
			}
			this.connectedAsynConnection = connectedAsynConnection;
			monitor.notify();
		}
		
		log.warn("add the connected asyn connection[{}]", connectedAsynConnection.hashCode());
	}

	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		log.warn("remove the unregistered asyn connection[{}]", unregisteredAsynConnection.hashCode());
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
