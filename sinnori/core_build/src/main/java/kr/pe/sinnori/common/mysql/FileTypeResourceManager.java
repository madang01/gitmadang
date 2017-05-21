package kr.pe.sinnori.common.mysql;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTypeResourceManager {	
	private FileTypeResource configFileTypeResource = null;
	
	private List<FileTypeResource> mapperFileTypeResourceList = new ArrayList<FileTypeResource>();
	
	public FileTypeResourceManager(File mybatisConfigFile) {
		if (null == mybatisConfigFile) {
			throw new IllegalArgumentException("the parameter mybatisConfigFile is null");
		}
		
		this.configFileTypeResource = new FileTypeResource(mybatisConfigFile);
	}
	
	public void addMapperFile(File mapperFile) {
		if (null == mapperFile) {
			throw new IllegalArgumentException("the parameter mapperFile is null");
		}
		
		mapperFileTypeResourceList.add(new FileTypeResource(mapperFile));
	}
	
	
	public boolean isModified() {
		if (configFileTypeResource.isModified()) return true;
		
		for (FileTypeResource fileTypeResource : mapperFileTypeResourceList) {
			if (fileTypeResource.isModified()) return true;
		}
		
		return false;
	}
	
	public final FileTypeResource getMybatisConfigFIleTypeResoruce() {
		return configFileTypeResource;
	}
	
	
	public List<File> getMapperFileList() {
		List<File> mapplerFileList = new ArrayList<File>();
		for (FileTypeResource fileTypeResource : mapperFileTypeResourceList) {
			mapplerFileList.add(fileTypeResource.getFile());
		}
		return mapplerFileList;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileTypeResourceManger [configFileTypeResource=");
		builder.append(configFileTypeResource.toString());
		builder.append(", mapperFileTypeResourceList=[");
		builder.append(mapperFileTypeResourceList.toString());
		builder.append("]");
		return builder.toString();
	}
}
