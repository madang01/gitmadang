package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.FileUpDownScreenIF;
import kr.pe.sinnori.gui.LocalFileTreeNode;

@SuppressWarnings("serial")
public class LocalParentSwingAction extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree localTree = null;
	private LocalFileTreeNode localRootNode = null;
	
	public LocalParentSwingAction(JFrame mainFrame, 
			FileUpDownScreenIF fileUpDownScreen, 
			JTree localTree,
			LocalFileTreeNode localRootNode) {
		this.mainFrame = mainFrame;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
		
		putValue(NAME, "..");
		putValue(SHORT_DESCRIPTION, "로컬 작업 경로를 부모 경로로 변경하는 이벤트");
	}

	public void actionPerformed(ActionEvent e) {
		System.out.printf(
				"localParentSwingAction::call actionPerformed [%d]",
				e.getID());
		System.out.println("");

		File localParntePathFile = localRootNode.getFileObj().getParentFile();

		if (null == localParntePathFile) {
			// log.debug("localParntePathFile is null");

			JOptionPane.showMessageDialog(mainFrame,
					"로컬 루트 디렉토리로 상위 디렉토리가 없습니다.");
			return;
		}

		localRootNode.changeFile(localParntePathFile);
		localRootNode.removeAllChildren();

		fileUpDownScreen.makeLocalTreeNode(localRootNode);
		fileUpDownScreen.repaintTree(localTree);
	}
}
