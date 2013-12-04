package kr.pe.sinnori.gui;

import java.io.File;


@SuppressWarnings("serial")
public class LocalFileTreeNode extends AbstractFileTreeNode {
	private File fileObj = null;
	// private String absolutePath = null;
	
	public LocalFileTreeNode(File fileObj, long fileSize, byte fileTypeByte) {
		super(fileObj.getName(), fileSize, fileTypeByte);
		
		this.fileObj = fileObj;
		
		// this.absolutePath = fileObj.getAbsolutePath();
		/*
		if (this.isRoot()) {
			this.setUserObject(fileObj.getAbsolutePath());
		}
		*/
		
		log.info(String.format("fileName=[%s], userObject=[%s], isRoot=[%s]", fileName, getUserObject(), isRoot()));
	}
	
	public File getFileObj() {
		return fileObj;
	}
	
	public String getAbsolutePath() {
		return fileObj.getAbsolutePath();
	}
	
	public void changeFile(File newFileObj) {
		this.fileObj = newFileObj;
		this.fileName = newFileObj.getName();
		//this.absolutePath = newFileObj.getAbsolutePath();
		
		if (this.isRoot()) {
			this.setUserObject(fileObj.getAbsolutePath());
		} else {
			this.setUserObject(fileName);
		}
	}
}
