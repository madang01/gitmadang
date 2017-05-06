package kr.pe.sinnori.server.mysql;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTypeResourceManager {
	private List<FileTypeResource> fileTypeResourceList = new ArrayList<FileTypeResource>();
	
	public void add(File fileTypeResource2File) {
		fileTypeResourceList.add(new FileTypeResource(fileTypeResource2File));
	}
	
	public boolean isModified() {
		for (FileTypeResource fileTypeResource : fileTypeResourceList) {
			if (fileTypeResource.isModified()) return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileTypeResourceManger [fileTypeResourceList=");
		builder.append(fileTypeResourceList.toString());
		builder.append("]");
		return builder.toString();
	}
}
