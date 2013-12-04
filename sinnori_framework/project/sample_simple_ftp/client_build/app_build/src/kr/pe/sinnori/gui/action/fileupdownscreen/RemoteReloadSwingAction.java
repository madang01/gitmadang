package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.FileUpDownScreenIF;

@SuppressWarnings("serial")
public class RemoteReloadSwingAction extends AbstractAction implements
		CommonRootIF {
	private FileUpDownScreenIF fileUpDownScreen = null;
	
	
	public RemoteReloadSwingAction(FileUpDownScreenIF fileUpDownScreen) {
		this.fileUpDownScreen = fileUpDownScreen;
		
		putValue(NAME, "remoteReload");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));
		/*
		OutputMessage fileListOutObj = mainController
				.getRemoteFileList(remoteRootNode.getFileName());
		if (null == fileListOutObj) return;
		

		remoteRootNode.removeAllChildren();
		fileUpDownScreen.makeRemoteTreeNode(fileListOutObj, remoteRootNode);
		fileUpDownScreen.repaintTree(remoteTree);
		*/
		fileUpDownScreen.reloadRemoteFileList();
	}
}
