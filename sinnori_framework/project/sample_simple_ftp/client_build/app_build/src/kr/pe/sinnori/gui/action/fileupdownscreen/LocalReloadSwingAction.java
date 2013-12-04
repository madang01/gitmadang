package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.FileUpDownScreenIF;

@SuppressWarnings("serial")
public class LocalReloadSwingAction extends AbstractAction implements
		CommonRootIF {
	
	private FileUpDownScreenIF fileUpDownScreen = null;
	
	
	public LocalReloadSwingAction(FileUpDownScreenIF fileUpDownScreen) {
		this.fileUpDownScreen = fileUpDownScreen;
		
		putValue(NAME, "reload");
		putValue(SHORT_DESCRIPTION, "로컬 작업 경로의 파일 목록 재 읽기 이벤트");
	}

	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));
		/*
		localRootNode.removeAllChildren();
		fileUpDownScreen.makeLocalTreeNode(localRootNode);
		fileUpDownScreen.repaintTree(localTree);
		*/
		fileUpDownScreen.reloadLocalFileList();
	}
}
