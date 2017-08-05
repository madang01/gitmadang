/*
 * Created by JFormDesigner on Sun Jul 30 15:16:05 KST 2017
 */

package kr.pe.sinnori.gui.syncfileupdown.screen;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.applib.MainProejctSyncConnectionManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.gui.syncfileupdown.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.syncfileupdown.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.syncfileupdown.lib.ScreenManagerIF;
import kr.pe.sinnori.gui.syncfileupdown.lib.TreeCellRenderer;
import kr.pe.sinnori.impl.message.FileListReq.FileListReq;
import kr.pe.sinnori.impl.message.FileListRes.FileListRes;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class SyncFileUpDownPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(SyncFileUpDownPanel.class);

	private Frame mainFrame = null;
	private ScreenManagerIF screenManager = null;
	private LocalFileTreeNode localRootNode = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;

	public SyncFileUpDownPanel(Frame mainFrame, ScreenManagerIF screenManager) {
		super();
		this.mainFrame = mainFrame;
		this.screenManager = screenManager;
		initComponents();
		postInitComponents();
	}

	private void postInitComponents() {
		TreeCellRenderer treeCellRenderer = new TreeCellRenderer();

		localTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		localTree.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(localTree);
		localTree.setCellRenderer(treeCellRenderer);		

		remoteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		remoteTree.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(remoteTree);
		remoteTree.setCellRenderer(treeCellRenderer);		

		localDriverComboBox.setMaximumRowCount(24);
		makeLocalDriverComboBox();
		repaintTree(localTree);

		remoteDriverComboBox.setMaximumRowCount(24);
		remoteDriverComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "- select drive -" }));
	}
		
	
	@Override
	public void setVisible(boolean aFlag) {		
		if (aFlag) {
			remoteRootNode.chageFileName(".");
			reloadRemoteFileList();
		}
		super.setVisible(aFlag);
	}
	
	private FileListRes getFileListRes(String requestPathString) {
		MainProejctSyncConnectionManager mainProejctSyncConnectionManager = MainProejctSyncConnectionManager.getInstance();
		
		if (! mainProejctSyncConnectionManager.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			screenManager.goToLoginScreen();
			return null;
		}
		
		FileListReq fileListReq = new FileListReq();
		fileListReq.setRequestPathName(requestPathString);
		
		AbstractMessage outObj = null;
		try {
			outObj = mainProejctSyncConnectionManager.sendSyncInputMessage(fileListReq);
		} catch (SocketTimeoutException e1) {
			log.warn(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "소켓 타임아웃 발생");
			screenManager.goToLoginScreen();
			return null;
		} catch (ServerNotReadyException e1) {
			log.warn(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			screenManager.goToLoginScreen();
			return null;
		} catch (NoMoreDataPacketBufferException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "데이터 송수신에 사용할 데이터 패킷용 버퍼가 부족합니다.");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (BodyFormatException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "바디 포맷이 잘못되었습니다.");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (DynamicClassCallException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "FileListReq 관련 동적 클래스 호출 실패");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (ServerTaskException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "FileListReq  서버 타스크 에러");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (NotLoginException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "로그인 서비스에 로그인 하지 않고 접근하였습니다.");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (ConnectionTimeoutException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "공유+비동기 연결 객체에서 지정된 시간 동안 메일 박스를 얻는데 실패하였습니다.");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		} catch (InterruptedException e1) {
			log.error(e1.getMessage(), e1);
			JOptionPane.showMessageDialog(mainFrame, "인터럽트 호출로 인한 종료");
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		}
		
		
		if (! (outObj instanceof FileListRes)) {
			String errorMessage = String.format("FileListReq 입력 메시지에 대한 출력 메시지가 FileListRes 가 아닙니다. 출력 메시지 식별자=[%s]", outObj.getMessageID());
			log.error(errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		}
		
		
		FileListRes fileListRes = (FileListRes)outObj;
		
		String isSuccess = fileListRes.getIsSuccess();
		if (! isSuccess.equals("N") && ! isSuccess.equals("Y")) {
			log.error("출력 메시지 FileListRes 의 성공 여부 값[{}]이 잘못 되었습니다. 성공 여부[isSuccess]의 값은 Y 혹은 N 이어야 합니다.", isSuccess);
			mainProejctSyncConnectionManager.closeConnection();
			System.exit(1);
		}
		
		if (null == remotePathSeperator) {
			remotePathSeperator = fileListRes.getPathSeperator();
		}	
		
		return fileListRes;
	}
	
	
	
	private void reloadRemoteFileList() {
		FileListRes fileListRes = getFileListRes(remoteRootNode.getFileName());		
		if (null == fileListRes) {
			/** if null then nothing */
			return;
		}
		
		// FIXME!
		log.info("fileListRes=[{}]", fileListRes.toString());
					
		String isSuccess = fileListRes.getIsSuccess();
		String resultMessage = fileListRes.getResultMessage();
		
		
		if (isSuccess.equals("N")) {
			JOptionPane.showMessageDialog(mainFrame, resultMessage);
			return;
		}
		
		
		try {
			rebuildRemoteDriverComboBox(fileListRes);
			remoteRootNode.chageFileName(fileListRes.getRequestPathName());
			remoteRootNode.rebuildChildTreeNodes(fileListRes);
			repaintTree(remoteTree);
		} catch (Exception e) {
			log.warn("unknown error", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return;
		}
	}

	private void rebuildRemoteDriverComboBox(FileListRes fileListRes) {
		remoteDriverComboBox.setVisible(false);
		remoteDriverComboBox.removeAllItems();
		remoteDriverComboBox.addItem("- select drive -");
		
		int cntOfDriver = fileListRes.getCntOfDriver();
		if (cntOfDriver > 0) {			
			List<FileListRes.Driver> driverList = fileListRes.getDriverList();
			for (FileListRes.Driver driver : driverList) {
				String driverName = driver.getDriverName();
				remoteDriverComboBox.addItem(driverName);
			}
		}
		
		if (! fileListRes.getPathSeperator().equals("/")) {
			remoteDriverComboBox.setVisible(true);
		}
	}
	
	
	private void makeLocalDriverComboBox() {		
		if (File.separator.equals("/")) {
			localDriverComboBox.setVisible(false);
		} else {
			rebuildLocalDriverComboBox();
		}
	}
	
	private void rebuildLocalDriverComboBox() {
		localDriverComboBox.setVisible(false);
		localDriverComboBox.removeAllItems();
		localDriverComboBox.addItem("- select drive -");

		File[] roots = File.listRoots();
		for (File root : roots) {
			String drive = root.getAbsolutePath();
			localDriverComboBox.addItem(drive);
		}
		localDriverComboBox.setVisible(true);
	}

	

	private void repaintTree(JTree targetTree) {
		DefaultTreeModel localDefaultTreeModel = (DefaultTreeModel) targetTree.getModel();
		localDefaultTreeModel.reload();
		targetTree.repaint();
	}


	private void localParentDirecotryMoveButtonActionPerformed(ActionEvent e) {
		File localParntePathFile = localRootNode.getFileObj().getParentFile();

		if (null == localParntePathFile) {
			// log.debug("localParntePathFile is null");

			JOptionPane.showMessageDialog(mainFrame, "로컬 루트 디렉토리로 상위 디렉토리가 없습니다.");
			return;
		}

		localRootNode.changeFileObj(localParntePathFile);
		repaintTree(localTree);
	}

	private void localDriverComboBoxItemStateChanged(ItemEvent e) {
		int selectedInx = localDriverComboBox.getSelectedIndex();
		if (selectedInx > 0) {
			String driveName = localDriverComboBox.getItemAt(selectedInx);

			StringBuilder newWorkPathBuilder = new StringBuilder(driveName);
			// newWorkPathBuilder.append(File.separator);
			// newWorkPathBuilder.append(selNode.getFileName());
			String newWorkPath = newWorkPathBuilder.toString();

			log.debug(String.format("newWorkPath=[%s]", newWorkPath));

			File localSelectedPathFile = new File(newWorkPath);

			if (!localSelectedPathFile.exists()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 존재하지 않습니다.",
				// newWorkPath));

				JOptionPane.showMessageDialog(mainFrame, "선택된 디렉토리가 존재하지 않습니다");
				return;
			}

			if (!localSelectedPathFile.isDirectory()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 디렉토리가 아닙니다.",
				// newWorkPath));

				JOptionPane.showMessageDialog(mainFrame, "선택된 디렉토리가 디렉토리가 아닙니다.");
				return;
			}

			localRootNode.changeFileObj(localSelectedPathFile);
			repaintTree(localTree);
		}
	}

	private void prevButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
		screenManager.goToLoginScreen();
	}

	private void localReloadButtonActionPerformed(ActionEvent e) {
		if (! File.separator.equals("/")) {
			rebuildLocalDriverComboBox();
		}
		
		localRootNode.rebuildChildTreeNodes();
		repaintTree(localTree);
	}

	private void localTreeMouseClicked(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			LocalFileTreeNode selNode = (LocalFileTreeNode) selPath
					.getLastPathComponent();
			
			if (selNode.isDirectory() && e.getClickCount() == 2
					&& !selNode.isRoot()) {
				/**
				 * 루트 노드를 제외한 "디렉토리 노드" 더블 클릭시 처리 로직. 작업 경로를 더블 클릭한 디렉토리로 이동한다.
				 * double-click logic when a directory node which is not a root node is clicked.
				 */
				log.debug(String.format("selNode=[%s]", selNode.getFileName()));

				StringBuilder newWorkPathBuilder = new StringBuilder(localRootNode.getAbsolutePath());
				newWorkPathBuilder.append(File.separator);
				newWorkPathBuilder.append(selNode.getFileName());
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

				localRootNode.changeFileObj(localSelectedPathFile);
				repaintTree(localTree);
			}
		}
	}

	private void remoteRealodButtonActionPerformed(ActionEvent e) {
		reloadRemoteFileList();
	}

	private void remoteParentDirectoryMoveButtonActionPerformed(ActionEvent e) {
		String remoteCurrentWorkingPathString = remoteRootNode.getFileName();
		
		// root directory 이면 더 이상의 부모 디렉토리 없으므로 종료한다.
		if (remotePathSeperator.equals("/")) {
			/** unix */
			if (remoteCurrentWorkingPathString.equals("/")) {
				JOptionPane.showMessageDialog(mainFrame,
						"원격지 루트 디렉토리로 상위 디렉토리가 없습니다.");
				return;
			}
		} else {
			/** dos */
			if (remoteCurrentWorkingPathString.matches("^[a-zA-Z]:\\\\$")) {
				JOptionPane.showMessageDialog(mainFrame,
						"원격지 루트 디렉토리로 상위 디렉토리가 없습니다.");
				return;
			}
		}
				
		String parentPathStringOfRemoteCurrentWorkingPath = new StringBuilder(
				remoteCurrentWorkingPathString).append(remotePathSeperator).append("..").toString();

		FileListRes fileListRes = getFileListRes(parentPathStringOfRemoteCurrentWorkingPath);
		if (null == fileListRes) {
			/** if null then nothing */
			return;
		}
		
		log.debug(fileListRes.toString());
		
		try {
			remoteRootNode.chageFileName(fileListRes.getRequestPathName());
			remoteRootNode.rebuildChildTreeNodes(fileListRes);
			repaintTree(remoteTree);
		} catch (Exception e1) {
			log.warn("unknown error", e1);
			JOptionPane.showMessageDialog(mainFrame, "unknwon error"+e1.getMessage());
			return;
		}
	}

	private void remoteDriverComboBoxActionPerformed(ActionEvent e) {		
		int selectedInx = remoteDriverComboBox.getSelectedIndex();
		if (selectedInx > 0) {			
			String driverName = (String)remoteDriverComboBox.getSelectedItem();
			
			String requestPathString = new StringBuilder(driverName).toString();

			log.debug(String.format("newWorkPath=[%s]", requestPathString));
			

			FileListRes fileListRes = getFileListRes(requestPathString);
			if (null == fileListRes) {
				/** if null then nothing */
				return;
			}
			
			log.debug(fileListRes.toString());

			try {
				remoteRootNode.chageFileName(fileListRes.getRequestPathName());
				remoteRootNode.rebuildChildTreeNodes(fileListRes);
				repaintTree(remoteTree);
			} catch (Exception e1) {
				log.warn("Exception", e1);
				JOptionPane.showMessageDialog(mainFrame, "unknwon error::"+e1.getMessage());
				return;
			}
		} 
	}

	private void remoteTreeMouseClicked(MouseEvent e) {
		
		int selRow = remoteTree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = remoteTree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			RemoteFileTreeNode selNode = (RemoteFileTreeNode) selPath
					.getLastPathComponent();
			if (selNode.isDirectory() && e.getClickCount() == 2
					&& !selNode.isRoot()) {
				// myDoubleClick(selRow, selPath);
				log.debug(String.format("selNode.getFileName=[%s]", selNode.getFileName()));
								
				String requestPathString = new StringBuilder(remoteRootNode.getFileName())
						.append(remotePathSeperator)
						.append(selNode.getFileName()).toString();

				log.debug(String.format("requestPathString=[%s]", requestPathString));
				

				FileListRes fileListRes = getFileListRes(requestPathString);
				if (null == fileListRes) {
					/** if null then nothing */
					return;
				}
				
				log.debug(fileListRes.toString());
			
				try {
					remoteRootNode.chageFileName(fileListRes.getRequestPathName());
					remoteRootNode.rebuildChildTreeNodes(fileListRes);
					repaintTree(remoteTree);
				} catch (Exception e1) {
					log.warn("Exception", e1);
					JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e1.getMessage());
					return;
				}
			}
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		localTitleLabel = new JLabel();
		prevButton = new JButton();
		remoteTitleLabel = new JLabel();
		localPanel = new JPanel();
		localMenuPanel = new JPanel();
		localDriverComboBox = new JComboBox<>();
		localParentDirecotryMoveButton = new JButton();
		localReloadButton = new JButton();
		localScrollPane = new JScrollPane();
		/** localTree :: Pre-Creation Code start */
		try {
					localRootNode = new LocalFileTreeNode(new File(".").getCanonicalFile(), 0L, RemoteFileTreeNode.DIRECTORY);		
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					System.exit(1);
				}
				localRootNode.rebuildChildTreeNodes();
		/** localTree :: Pre-Creation Code end */
		localTree = new JTree(localRootNode);
		menuPanel = new JPanel();
		uploadButton = new JButton();
		menuVerticalSpacer = new JPanel(null);
		downloadButton = new JButton();
		remotePanel = new JPanel();
		remoteMenuPanel = new JPanel();
		remoteDriverComboBox = new JComboBox<>();
		remoteParentDirectoryMoveButton = new JButton();
		remoteRealodButton = new JButton();
		remoteScrollPane = new JScrollPane();
		remoteRootNode = new RemoteFileTreeNode(".", 0L, RemoteFileTreeNode.DIRECTORY);
		remoteTree = new JTree(remoteRootNode);

		//======== this ========
		setPreferredSize(new Dimension(800, 600));
		setLayout(new FormLayout(
			"$ugap, 160dlu:grow, $ugap, center:[50dlu,pref]:grow, $ugap, 160dlu:grow, $ugap",
			"$lgap, default, $lgap, fill:265dlu:grow, $lgap"));

		//---- localTitleLabel ----
		localTitleLabel.setText("local");
		add(localTitleLabel, CC.xy(2, 2, CC.CENTER, CC.DEFAULT));

		//---- prevButton ----
		prevButton.setAction(null);
		prevButton.setToolTipText("go to login screen after disconnection");
		prevButton.setText("go to login screen");
		prevButton.addActionListener(e -> prevButtonActionPerformed(e));
		add(prevButton, CC.xy(4, 2));

		//---- remoteTitleLabel ----
		remoteTitleLabel.setText("remote");
		add(remoteTitleLabel, CC.xy(6, 2, CC.CENTER, CC.DEFAULT));

		//======== localPanel ========
		{
			localPanel.setLayout(new FormLayout(
				"default:grow",
				"default, $lcgap, fill:217dlu:grow"));

			//======== localMenuPanel ========
			{
				localMenuPanel.setLayout(new FormLayout(
					"default:grow, 2*($lcgap, default)",
					"default"));

				//---- localDriverComboBox ----
				localDriverComboBox.setAction(null);
				localDriverComboBox.setMaximumSize(new Dimension(63, 27));
				localDriverComboBox.addItemListener(e -> localDriverComboBoxItemStateChanged(e));
				localMenuPanel.add(localDriverComboBox, CC.xy(1, 1));

				//---- localParentDirecotryMoveButton ----
				localParentDirecotryMoveButton.setAction(null);
				localParentDirecotryMoveButton.setText("..");
				localParentDirecotryMoveButton.addActionListener(e -> localParentDirecotryMoveButtonActionPerformed(e));
				localMenuPanel.add(localParentDirecotryMoveButton, CC.xy(3, 1));

				//---- localReloadButton ----
				localReloadButton.setAction(null);
				localReloadButton.setText("reload");
				localReloadButton.addActionListener(e -> localReloadButtonActionPerformed(e));
				localMenuPanel.add(localReloadButton, CC.xy(5, 1));
			}
			localPanel.add(localMenuPanel, CC.xy(1, 1));

			//======== localScrollPane ========
			{

				//---- localTree ----
				localTree.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						localTreeMouseClicked(e);
					}
				});
				localScrollPane.setViewportView(localTree);
			}
			localPanel.add(localScrollPane, CC.xy(1, 3));
		}
		add(localPanel, CC.xy(2, 4));

		//======== menuPanel ========
		{
			menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

			//---- uploadButton ----
			uploadButton.setText("upload");
			menuPanel.add(uploadButton);

			//---- menuVerticalSpacer ----
			menuVerticalSpacer.setMinimumSize(new Dimension(12, 12));
			menuVerticalSpacer.setRequestFocusEnabled(false);
			menuVerticalSpacer.setVerifyInputWhenFocusTarget(false);
			menuVerticalSpacer.setPreferredSize(new Dimension(10, 15));
			menuPanel.add(menuVerticalSpacer);

			//---- downloadButton ----
			downloadButton.setText("download");
			menuPanel.add(downloadButton);
		}
		add(menuPanel, CC.xy(4, 4, CC.CENTER, CC.CENTER));

		//======== remotePanel ========
		{
			remotePanel.setLayout(new FormLayout(
				"default:grow",
				"default, $lgap, fill:default:grow"));

			//======== remoteMenuPanel ========
			{
				remoteMenuPanel.setLayout(new FormLayout(
					"default:grow, 2*($lcgap, default)",
					"default"));

				//---- remoteDriverComboBox ----
				remoteDriverComboBox.addActionListener(e -> remoteDriverComboBoxActionPerformed(e));
				remoteMenuPanel.add(remoteDriverComboBox, CC.xy(1, 1));

				//---- remoteParentDirectoryMoveButton ----
				remoteParentDirectoryMoveButton.setText("..");
				remoteParentDirectoryMoveButton.setToolTipText("go to remote parent direcotory");
				remoteParentDirectoryMoveButton.addActionListener(e -> remoteParentDirectoryMoveButtonActionPerformed(e));
				remoteMenuPanel.add(remoteParentDirectoryMoveButton, CC.xy(3, 1));

				//---- remoteRealodButton ----
				remoteRealodButton.setText("reload");
				remoteRealodButton.addActionListener(e -> remoteRealodButtonActionPerformed(e));
				remoteMenuPanel.add(remoteRealodButton, CC.xy(5, 1));
			}
			remotePanel.add(remoteMenuPanel, CC.xy(1, 1));

			//======== remoteScrollPane ========
			{

				//---- remoteTree ----
				remoteTree.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						remoteTreeMouseClicked(e);
					}
				});
				remoteScrollPane.setViewportView(remoteTree);
			}
			remotePanel.add(remoteScrollPane, CC.xy(1, 3));
		}
		add(remotePanel, CC.xy(6, 4));
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JLabel localTitleLabel;
	private JButton prevButton;
	private JLabel remoteTitleLabel;
	private JPanel localPanel;
	private JPanel localMenuPanel;
	private JComboBox<String> localDriverComboBox;
	private JButton localParentDirecotryMoveButton;
	private JButton localReloadButton;
	private JScrollPane localScrollPane;
	private JTree localTree;
	private JPanel menuPanel;
	private JButton uploadButton;
	private JPanel menuVerticalSpacer;
	private JButton downloadButton;
	private JPanel remotePanel;
	private JPanel remoteMenuPanel;
	private JComboBox<String> remoteDriverComboBox;
	private JButton remoteParentDirectoryMoveButton;
	private JButton remoteRealodButton;
	private JScrollPane remoteScrollPane;
	private JTree remoteTree;
	// JFormDesigner - End of variables declaration //GEN-END:variables

}
