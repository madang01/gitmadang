package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface AsynClientIOEventControllerIF {
	public void addUnregisteredAsynConnection(ClientInterestedConnectionIF asynInterestedConnection) throws IOException, InterruptedException;
	
	public void startWrite(ClientInterestedConnectionIF asynInterestedConnection);
	public void endWrite(ClientInterestedConnectionIF asynInterestedConnection);
	
	public void cancel(SelectionKey selectedKey);
	
	
	/*public void start();
	public void interrupt();*/
}
