package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
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
public class RemoteDriverChangeAction extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;
	

	public RemoteDriverChangeAction(JFrame mainFrame, MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen, 
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode,
			String remotePathSeperator) {
		this.mainFrame= mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
		
		
		putValue(NAME, "remoteDriverChange");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>)e.getSource();
		int selectedInx = cb.getSelectedIndex();
		if (selectedInx > 0) {
			if (null == remotePathSeperator) {
				remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
			}
			
			String driverName = (String)cb.getSelectedItem();
			
			StringBuilder newWorkPathBuilder = new StringBuilder(driverName);
			
			// newWorkPathBuilder.append(remotePathSeperator);
			// newWorkPathBuilder.append(selNode.getFileName());
			String newWorkPath = newWorkPathBuilder.toString();

			log.debug(String.format("newWorkPath=[%s]", newWorkPath));
			

			OutputMessage fileListOutObj = mainController
					.getRemoteFileList(newWorkPath);

			if (null != fileListOutObj) {
				log.debug(fileListOutObj.toString());

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
	}

}
