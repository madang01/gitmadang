package kr.pe.codda.client.connection.asyn;

import java.nio.channels.SelectionKey;

public interface ClientIOEventControllerIF {
	public void addUnregisteredAsynConnection(ClientIOEventHandlerIF asynInterestedConnection);
	
	public void wakeup();	
	public void cancel(SelectionKey selectedKey);
}
