package kr.pe.sinnori.common.mybatis;

import java.io.File;

public class FileModificationChecker {
	private File sourceFile = null;
	private long lastModifiedTime = 0;
	
	public FileModificationChecker(File sourceFile) {
		this.sourceFile = sourceFile;
		this.lastModifiedTime = sourceFile.lastModified();		
	}
	
	public boolean isModified() {
		long nowLastModifiedTime = sourceFile.lastModified();
		return (lastModifiedTime != nowLastModifiedTime);
	}
	
	
	public final File getFile() {
		return sourceFile;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MybatisFileTypeResource.java [sourceFile=");
		builder.append(sourceFile);
		builder.append(", lastModifiedTime=");
		builder.append(lastModifiedTime);
		builder.append("]");
		return builder.toString();
	}
	
	
}
