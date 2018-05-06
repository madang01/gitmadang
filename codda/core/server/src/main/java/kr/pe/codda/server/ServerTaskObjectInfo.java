package kr.pe.codda.server;

import java.io.File;
import java.io.FileNotFoundException;

import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.server.task.AbstractServerTask;

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
		/** 클래스로더가 SimpleClassLoader 가 아니라면 해당 클래스는 시스템 클래스로더 대상이므로 수정 여부는 무조건 거짓(=false)으로 반환한다  */
		if (! (serverTask.getClass().getClassLoader() instanceof SimpleClassLoader)) {
			return false;
		}
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
