package kr.pe.sinnori.common.mybatis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MybatisParsingInformationForModification {	
	private FileModificationChecker mybatisConfigFileModificationChecker = null;
	
	private List<FileModificationChecker> mapperFileModificationCheckerList = new ArrayList<FileModificationChecker>();
	
	public MybatisParsingInformationForModification(File mybatisConfigFile) {
		if (null == mybatisConfigFile) {
			throw new IllegalArgumentException("the parameter mybatisConfigFile is null");
		}
		
		this.mybatisConfigFileModificationChecker = new FileModificationChecker(mybatisConfigFile);
	}
	
	public void addMapperFile(File mapperFile) {
		if (null == mapperFile) {
			throw new IllegalArgumentException("the parameter mapperFile is null");
		}
		
		mapperFileModificationCheckerList.add(new FileModificationChecker(mapperFile));
	}
	
	
	public boolean isAllFileTypeResourceModified() {
		if (mybatisConfigFileModificationChecker.isModified()) return true;
		
		for (FileModificationChecker fileTypeResource : mapperFileModificationCheckerList) {
			if (fileTypeResource.isModified()) return true;
		}
		
		return false;
	}
	
	public final FileModificationChecker getMybatisConfigFileModificationChecker() {
		return mybatisConfigFileModificationChecker;
	}
	
	
	public List<File> getMapperFileList() {
		List<File> mapplerFileList = new ArrayList<File>();
		for (FileModificationChecker fileTypeResource : mapperFileModificationCheckerList) {
			mapplerFileList.add(fileTypeResource.getFile());
		}
		return mapplerFileList;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileTypeResourceManger [configFileTypeResource=");
		builder.append(mybatisConfigFileModificationChecker.toString());
		builder.append(", mapperFileModificationCheckerList=[");
		builder.append(mapperFileModificationCheckerList.toString());
		builder.append("]");
		return builder.toString();
	}
}
