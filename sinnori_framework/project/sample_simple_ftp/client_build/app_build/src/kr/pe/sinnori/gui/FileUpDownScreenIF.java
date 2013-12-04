package kr.pe.sinnori.gui;

import javax.swing.JTree;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.message.OutputMessage;

public interface FileUpDownScreenIF {
	public void makeLocalTreeNode(LocalFileTreeNode localWorkNode);
	public void repaintTree(JTree targetTree);
	public String getRemotePathSeperator();
	public void reloadLocalFileList();
	public void reloadRemoteFileList();
	public void makeRemoteTreeNode(OutputMessage fileListOutObj, RemoteFileTreeNode remoteRootNode) throws MessageItemException;
}
