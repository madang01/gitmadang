package kr.pe.sinnori.server;

import java.io.File;
import java.io.FileNotFoundException;

import kr.pe.sinnori.server.task.AbstractServerTask;

public class ServerTaskObjectInfo {
	private File serverTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractServerTask serverTask = null;
	
	public ServerTaskObjectInfo(File serverTaskClassFile, AbstractServerTask serverTask) {
		this.serverTaskClassFile = serverTaskClassFile;
		this.serverTask = serverTask;
		this.loadedTime = serverTaskClassFile.lastModified();
	}
	
	public boolean isModifed() throws FileNotFoundException {
		if (! serverTaskClassFile.exists()) {
			String errorMessage = new StringBuilder("the server task file[")
					.append(serverTaskClassFile.getAbsolutePath())
					.append("] was not found").toString();
			
			throw new FileNotFoundException(errorMessage);
		}
		long lastModifedTime = serverTaskClassFile.lastModified();
		return (loadedTime != lastModifedTime);
	}

	public AbstractServerTask getServerTask() {
		return serverTask;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerTaskObjectInfo [serverTaskClassFile=");
		builder.append(serverTaskClassFile.getAbsolutePath());
		builder.append(", loadedTime=");
		builder.append(loadedTime);
		builder.append(", serverTask hashCode=");
		builder.append(serverTask.hashCode());
		builder.append("]");
		return builder.toString();
	}	
}
