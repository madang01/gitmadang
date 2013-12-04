package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.FileUpDownScreenIF;
import kr.pe.sinnori.gui.MainControllerIF;
import kr.pe.sinnori.gui.RemoteFileTreeNode;

@SuppressWarnings("serial")
public class RemoteParentSwingAction extends AbstractAction implements
		CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;

	public RemoteParentSwingAction(JFrame mainFrame,
			MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen, JTree remoteTree,
			RemoteFileTreeNode remoteRootNode, String remotePathSeperator) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;

		putValue(NAME, "..");
		putValue(SHORT_DESCRIPTION, "원격지 작업 경로를 부모 경로로 변경하는 이벤트");
	}

	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));
		

		if (null == remotePathSeperator) {
			remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
		}

		// root directory 이면 더 이상의 부모 디렉토리 없으므로 종료한다.
		String remoteRootFileName = remoteRootNode.getFileName();
		int firstInxOfPathSeparator = remoteRootFileName
				.indexOf(remotePathSeperator);
		int lastInxOfPathSeparator = remoteRootFileName
				.lastIndexOf(remotePathSeperator);

		if (firstInxOfPathSeparator == lastInxOfPathSeparator
				&& remoteRootFileName.length() == firstInxOfPathSeparator + 1) {
			JOptionPane.showMessageDialog(mainFrame,
					"원격지 루트 디렉토리로 상위 디렉토리가 없습니다.");
			return;
		}

		StringBuilder newRemotePathNameBuffer = new StringBuilder(
				remoteRootFileName);
		newRemotePathNameBuffer.append(remotePathSeperator);
		newRemotePathNameBuffer.append("..");
		String newRemotePathName = newRemotePathNameBuffer.toString();

		OutputMessage fileListOutObj = mainController
				.getRemoteFileList(newRemotePathName);
		if (null == fileListOutObj)
			return;

		remoteRootNode.removeAllChildren();
		try {
			fileUpDownScreen.makeRemoteTreeNode(fileListOutObj, remoteRootNode);
			fileUpDownScreen.repaintTree(remoteTree);
		} catch (MessageItemException e1) {
			log.warn("MessageItemException", e1);
			JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
			return;
		}
		
	}
}