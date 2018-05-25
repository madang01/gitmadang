package kr.pe.codda.server;

public interface ServerIOEvenetControllerIF {
	public void startWrite(InterestedConnectionIF asynInterestedConnection);
	public void endWrite(InterestedConnectionIF asynInterestedConnection);
	
	public void setSocketResourceManager(AcceptedConnectionManagerIF socketResourceManager);
}
