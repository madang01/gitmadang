package kr.pe.sinnori.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import kr.pe.sinnori.common.lib.CommonRootIF;

@SuppressWarnings("serial")
public abstract class AbstractFileTreeNode extends DefaultMutableTreeNode implements CommonRootIF {
	protected String fileName = null;
	protected long fileSize = 0;
	public enum FileType { File, Directory };
	protected FileType fileType;
	
	public static byte FILE = 0;
	public static byte DIRECTORY = 1;
	
	public AbstractFileTreeNode(Object userObject, long fileSize, byte fileTypeByte) {
		super(userObject, DIRECTORY == fileTypeByte);
		this.fileName = (String)userObject;
		
		this.fileSize = fileSize;
		
		if (DIRECTORY == fileTypeByte ) {
			fileType = FileType.Directory;
		} else if (FILE == fileTypeByte) {
			fileType = FileType.File;
		} else {
			String errorMessage = String.format("unkown file type[%d], D:Directory, F:File", fileTypeByte);
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public long getFileSize() {
		return fileSize;
	}

	public String getFileName() {
		return fileName;
	}
	
	public FileType getFileType() {
		return fileType;
	}
	
	public boolean isDirectory() {
		return (FileType.Directory == fileType);
	}
	
	/*
	@SuppressWarnings("unchecked")
	static public String encoding(FileTreeNode fileTreeNode) {
		StringBuilder builder = new StringBuilder();
		if (fileTreeNode.isRoot()) {
			builder.append(-1);
			
		} else {
			FileTreeNode parentFileTreeNode = (FileTreeNode)fileTreeNode.getParent();
			builder.append(" ");
			builder.append(parentFileTreeNode.getFileID());
		}
		
		builder.append(" ");
		builder.append(fileTreeNode.getFileID());
		
		if (FileType.File == fileTreeNode.getFileType()) {
			builder.append(" F");
		} else {
			builder.append(" D");
		}
		
		builder.append(" ");
		builder.append(fileTreeNode.getFileName());
		
		Enumeration<FileTreeNode> fileTreeNodes = fileTreeNode.children();
		while(fileTreeNodes.hasMoreElements()) {
			FileTreeNode childFileTreeNode = fileTreeNodes.nextElement();
			
			builder.append(encoding(childFileTreeNode));
		}
		
		return builder.toString();
	}
	
	
	public FileTreeNode decoding(String encodingStr) {
		
		return null;
	}
	*/
	
	public String toSummary() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectoryTreeNode [fileName=");
		builder.append(fileName);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", fileType=");
		builder.append(fileType);
		builder.append("]");
		return builder.toString();
	}
}
