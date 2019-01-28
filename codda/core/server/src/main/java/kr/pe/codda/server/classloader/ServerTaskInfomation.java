package kr.pe.codda.server.classloader;

import java.io.File;
import java.io.FileNotFoundException;

import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.server.task.AbstractServerTask;

public class ServerTaskInfomation {
	private File serverTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractServerTask serverTask = null;
	
	public ServerTaskInfomation(File serverTaskClassFile, AbstractServerTask serverTask) {
		if (null == serverTaskClassFile) {
			String errorMessage = "the parmater serverTaskClassFile is null";			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! serverTaskClassFile.exists()) {
			String errorMessage = new StringBuilder("the server task file[")
					.append(serverTaskClassFile.getAbsolutePath())
					.append("] was not found").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == serverTask) {
			String errorMessage = "the parmater serverTask is null";			
			throw new IllegalArgumentException(errorMessage);
		}		
		
		if (! (serverTask.getClass().getClassLoader() instanceof SimpleClassLoader)) {
			throw new IllegalArgumentException("the parameter serverTask is not a instance of SimpleClassLoader class");
		}		
		
		
		this.serverTaskClassFile = serverTaskClassFile;
		this.serverTask = serverTask;
		this.loadedTime = serverTaskClassFile.lastModified();
	}
	
	public boolean isModifed() throws FileNotFoundException {
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
