package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.FileUpDownScreenIF;
import kr.pe.sinnori.gui.MainControllerIF;
import kr.pe.sinnori.gui.RemoteFileTreeNode;

public class RemoteTreeMouseListener extends MouseAdapter implements CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;
	

	public RemoteTreeMouseListener(
			JFrame mainFrame,
			MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen, 
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode,
			String remotePathSeperator) {
		super();
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
	}
	
	public void mousePressed(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			RemoteFileTreeNode selNode = (RemoteFileTreeNode) selPath
					.getLastPathComponent();
			if (selNode.isDirectory() && e.getClickCount() == 2
					&& !selNode.isRoot()) {
				// myDoubleClick(selRow, selPath);
				log.debug(String.format("selNode.getFileName=[%s]", selNode.getFileName()));

				if (null == remotePathSeperator) {
					remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
				}
				
				StringBuilder newWorkPathBuilder = new StringBuilder(
						remoteRootNode.getFileName());
				newWorkPathBuilder.append(remotePathSeperator);
				newWorkPathBuilder.append(selNode.getFileName());
				String newWorkPath = newWorkPathBuilder.toString();

				log.debug(String.format("newWorkPath=[%s]", newWorkPath));
				

				OutputMessage fileListOutObj = mainController
						.getRemoteFileList(newWorkPath);

				if (null != fileListOutObj) {
					log.debug(fileListOutObj.toString());

					remoteRootNode.removeAllChildren();
					try {
						fileUpDownScreen.makeRemoteTreeNode(fileListOutObj,
								remoteRootNode);
						
						fileUpDownScreen.repaintTree(remoteTree);
					} catch (MessageItemException e1) {
						log.warn("MessageItemException", e1);
						JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
						return;
					}
				}
			}
		}
	}
}
