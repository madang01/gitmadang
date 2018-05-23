package kr.pe.codda.client.connection.asyn;

import java.io.IOException;

public interface AsynClientIOEventControllerIF {
	public void addUnregisteredAsynConnection(InterestedAsynConnectionIF asynInterestedConnection) throws IOException;
	
	public void startWrite(InterestedAsynConnectionIF asynInterestedConnection);
	public void endWrite(InterestedAsynConnectionIF asynInterestedConnection);
	
	/*public void start();
	public void interrupt();*/
}
