package kr.pe.sinnori.server.mysql;

import java.io.File;

public class FileTypeResource {
	private File sourceFile = null;
	private long lastModifiedTime = 0;
	
	public FileTypeResource(File sourceFile) {
		this.sourceFile = sourceFile;
		this.lastModifiedTime = sourceFile.lastModified();
		
	}
	
	public boolean isModified() {
		long nowLastModifiedTime = sourceFile.lastModified();
		return (lastModifiedTime != nowLastModifiedTime);
	}/*
	
	public void update() {
		this.lastModifiedTime = sourceFile.lastModified();
	}*/

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileTypeResource [sourceFile=");
		builder.append(sourceFile);
		builder.append(", lastModifiedTime=");
		builder.append(lastModifiedTime);
		builder.append("]");
		return builder.toString();
	}
	
	
}
