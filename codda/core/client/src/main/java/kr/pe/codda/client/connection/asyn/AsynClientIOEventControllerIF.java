package kr.pe.codda.client.connection.asyn;

import java.nio.channels.SelectionKey;

public interface AsynClientIOEventControllerIF {
	public void addUnregisteredAsynConnection(ClientInterestedConnectionIF asynInterestedConnection);
	
	public void startWrite(ClientInterestedConnectionIF asynInterestedConnection);
	public void endWrite(ClientInterestedConnectionIF asynInterestedConnection);
	
	public void cancel(SelectionKey selectedKey);
	
	public void callSelectorAlarm() throws InterruptedException;
}
