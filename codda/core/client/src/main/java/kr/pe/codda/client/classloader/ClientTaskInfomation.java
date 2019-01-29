package kr.pe.codda.client.classloader;

import java.io.File;

import kr.pe.codda.client.task.AbstractClientTask;

public class ClientTaskInfomation {
	private File clientTaskClassFile = null;
	private long loadedTime = 0;
	private AbstractClientTask clientTask = null;
	
	public ClientTaskInfomation(File clientTaskClassFile, AbstractClientTask clientTask) {
		if (null == clientTaskClassFile) {
			String errorMessage = "the parmater clientTaskClassFile is null";			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! clientTaskClassFile.exists()) {
			String errorMessage = new StringBuilder("the client task file[")
					.append(clientTaskClassFile.getAbsolutePath())
					.append("] was not found").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == clientTask) {
			String errorMessage = "the parmater clientTask is null";			
			throw new IllegalArgumentException(errorMessage);
		}	
		
		
		this.clientTaskClassFile = clientTaskClassFile;
		this.clientTask = clientTask;
		this.loadedTime = clientTaskClassFile.lastModified();
	}
	
	public boolean isModifed() {
		long lastModifedTime = clientTaskClassFile.lastModified();
		return (loadedTime != lastModifedTime);
	}

	public AbstractClientTask getClientTask() {
		return clientTask;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientTaskInfomation [clientTaskClassFile=");
		builder.append(clientTaskClassFile.getAbsolutePath());
		builder.append(", loadedTime=");
		builder.append(loadedTime);
		builder.append(", clientTask hashCode=");
		builder.append(clientTask.hashCode());
		builder.append("]");
		return builder.toString();
	}
}

