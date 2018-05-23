package kr.pe.codda.server;

public interface ServerIOEvenetControllerIF {
	public void startWrite(InterestedResoruceIF asynInterestedConnection);
	public void endWrite(InterestedResoruceIF asynInterestedConnection);
	
	public void setSocketResourceManager(SocketResourceManagerIF socketResourceManager);
}
