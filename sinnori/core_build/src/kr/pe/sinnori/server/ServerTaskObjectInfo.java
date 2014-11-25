package kr.pe.sinnori.server;

import java.io.File;

import kr.pe.sinnori.server.executor.AbstractServerTask;

public class ServerTaskObjectInfo {
	private ClassLoader loader = null;
	private File classFile = null;
	private long loadedTime = 0;
	private AbstractServerTask serverTask = null;
	
	public ServerTaskObjectInfo(ClassLoader loader, File classFile, AbstractServerTask serverTask) {
		this.loader = loader;
		this.classFile = classFile;
		this.serverTask = serverTask;
		this.loadedTime = classFile.lastModified();
	}
	
	public boolean isModifed() {
		long lastModifedTime = classFile.lastModified();
		return (loadedTime != lastModifedTime);
	}

	public AbstractServerTask getServerTask() {
		return serverTask;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerTaskObjectInfo [classFile=");
		builder.append(classFile.getAbsolutePath());
		builder.append(", loadedTime=");
		builder.append(loadedTime);
		builder.append(", loader hashCode=");
		builder.append(loader.hashCode());
		builder.append(", serverTask hashCode=");
		builder.append(serverTask.hashCode());
		builder.append("]");
		return builder.toString();
	}	
}
