package kr.pe.sinnori.gui;

@SuppressWarnings("serial")
public class RemoteFileTreeNode extends AbstractFileTreeNode {
	
	public RemoteFileTreeNode(String fileName, long fileSize, byte fileTypeByte) {
		super(fileName, fileSize, fileTypeByte);

		/*
		if (!this.isRoot()) {
			setFileName(String.format("%s %d byte(s)", fileName, fileSize));
		}
		*/
	}
	
	
	public void  setFileName(String newFileName) {
		this.fileName = newFileName;
		this.setUserObject(newFileName);
	}
}
