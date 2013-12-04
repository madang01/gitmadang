package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.FileUpDownScreenIF;
import kr.pe.sinnori.gui.LocalFileTreeNode;

@SuppressWarnings("serial")
public class LocalDriverChangeAction extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree localTree = null;
	private LocalFileTreeNode localRootNode = null;
	
	public LocalDriverChangeAction(JFrame mainFrame, 
			FileUpDownScreenIF fileUpDownScreen, 
			JTree localTree,
			LocalFileTreeNode localRootNode) {
		this.mainFrame = mainFrame;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>)e.getSource();

		int selectedInx = cb.getSelectedIndex();
		if (selectedInx > 0) {
			String driverName = (String)cb.getSelectedItem();
			
			StringBuilder newWorkPathBuilder = new StringBuilder(driverName);
			// newWorkPathBuilder.append(File.separator);
			// newWorkPathBuilder.append(selNode.getFileName());
			String newWorkPath = newWorkPathBuilder.toString();

			log.debug(String.format("newWorkPath=[%s]", newWorkPath));
			

			File localSelectedPathFile = new File(newWorkPath);

			if (!localSelectedPathFile.exists()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 존재하지 않습니다.", newWorkPath));

				JOptionPane.showMessageDialog(mainFrame, "선택된 디렉토리가 존재하지 않습니다");
				return;
			}

			if (!localSelectedPathFile.isDirectory()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 디렉토리가 아닙니다.", newWorkPath));
				
				JOptionPane.showMessageDialog(mainFrame,
						"선택된 디렉토리가 디렉토리가 아닙니다.");
				return;
			}

			localRootNode.changeFile(localSelectedPathFile);
			localRootNode.removeAllChildren();
			fileUpDownScreen.makeLocalTreeNode(localRootNode);
			fileUpDownScreen.repaintTree(localTree);
		}
	}
}
