package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ServerIOEvenetControllerIF {
	public void startWrite(ServerInterestedConnectionIF asynInterestedConnection);
	public void endWrite(ServerInterestedConnectionIF asynInterestedConnection);
	
	public void cancel(SelectionKey selectedKey);
}
