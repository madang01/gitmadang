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
import javax.swing.JDialog;
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
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.gui.syncfileupdown.lib.AbstractFileTreeNode.FileType;
import kr.pe.sinnori.gui.syncfileupdown.lib.FileTransferTaskIF;
import kr.pe.sinnori.gui.syncfileupdown.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.syncfileupdown.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.syncfileupdown.lib.ScreenManagerIF;
import kr.pe.sinnori.gui.syncfileupdown.lib.TreeCellRenderer;
import kr.pe.sinnori.gui.syncfileupdown.lib.UserMessageManger;
import kr.pe.sinnori.impl.message.FileListReq.FileListReq;
import kr.pe.sinnori.impl.message.FileListRes.FileListRes;
import kr.pe.sinnori.impl.message.ReadyToUploadReq.ReadyToUploadReq;
import kr.pe.sinnori.impl.message.ReadyToUploadRes.ReadyToUploadRes;
import kr.pe.sinnori.impl.message.SyncCancelUploadReq.SyncCancelUploadReq;
import kr.pe.sinnori.impl.message.SyncCancelUploadRes.SyncCancelUploadRes;
import kr.pe.sinnori.impl.message.SyncUploadReq.SyncUploadReq;
import kr.pe.sinnori.impl.message.SyncUploadRes.SyncUploadRes;

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

	private String loginID = null;

	private enum UserSelectableMode {
		NON_USER_SELECTABLE, USER_SELECTABLE
	};

	private LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager
			.getInstance();
	private MainProejctSyncConnectionManager mainProejctSyncConnectionManager = MainProejctSyncConnectionManager
			.getInstance();
	// private UserMessageManger userMessageManger =
	// UserMessageManger.getInstance();

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
			loginID = MainProejctSyncConnectionManager.getInstance().getLoginID();
			remoteRootNode.chageFileName(".");
			reloadRemoteFileList();
		}
		super.setVisible(aFlag);
	}

	private FileListRes getFileListRes(String requestPathString) {

		FileListReq fileListReq = new FileListReq();
		fileListReq.setRequestPathName(requestPathString);

		AbstractMessage outObj = null;
		try {
			outObj = mainProejctSyncConnectionManager.sendSyncInputMessage(fileListReq);
		} catch (SocketTimeoutException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			mainProejctSyncConnectionManager.closeConnection();
			screenManager.goToLoginScreen();
			return null;
		} catch (ServerNotReadyException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			mainProejctSyncConnectionManager.closeConnection();
			screenManager.goToLoginScreen();
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (BodyFormatException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (ServerTaskException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (NotLoginException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (ConnectionPoolTimeoutException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
		}

		if (!(outObj instanceof FileListRes)) {
			String errorMessage = String.format("FileListReq 입력 메시지에 대한 출력 메시지가 FileListRes 가 아닙니다. 출력 메시지 식별자=[%s]",
					outObj.getMessageID());
			log.error(errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			System.exit(1);
		}

		FileListRes fileListRes = (FileListRes) outObj;

		String isSuccess = fileListRes.getIsSuccess();
		if (!isSuccess.equals("N") && !isSuccess.equals("Y")) {
			log.error("출력 메시지 FileListRes 의 성공 여부 값[{}]이 잘못 되었습니다. 성공 여부[isSuccess]의 값은 Y 혹은 N 이어야 합니다.", isSuccess);
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

		if (!fileListRes.getPathSeperator().equals("/")) {
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
		if (!File.separator.equals("/")) {
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
			LocalFileTreeNode selNode = (LocalFileTreeNode) selPath.getLastPathComponent();

			if (selNode.isDirectory() && e.getClickCount() == 2 && !selNode.isRoot()) {
				/**
				 * 루트 노드를 제외한 "디렉토리 노드" 더블 클릭시 처리 로직. 작업 경로를 더블 클릭한 디렉토리로 이동한다.
				 * double-click logic when a directory node which is not a root
				 * node is clicked.
				 */
				log.debug(String.format("selNode=[%s]", selNode.getFileName()));

				StringBuilder newWorkPathBuilder = new StringBuilder(localRootNode.getAbsolutePath());
				newWorkPathBuilder.append(File.separator);
				newWorkPathBuilder.append(selNode.getFileName());
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
				JOptionPane.showMessageDialog(mainFrame, "원격지 루트 디렉토리로 상위 디렉토리가 없습니다.");
				return;
			}
		} else {
			/** dos */
			if (remoteCurrentWorkingPathString.matches("^[a-zA-Z]:\\\\$")) {
				JOptionPane.showMessageDialog(mainFrame, "원격지 루트 디렉토리로 상위 디렉토리가 없습니다.");
				return;
			}
		}

		String parentPathStringOfRemoteCurrentWorkingPath = new StringBuilder(remoteCurrentWorkingPathString)
				.append(remotePathSeperator).append("..").toString();

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
			JOptionPane.showMessageDialog(mainFrame, "unknwon error" + e1.getMessage());
			return;
		}
	}

	private void remoteDriverComboBoxActionPerformed(ActionEvent e) {
		int selectedInx = remoteDriverComboBox.getSelectedIndex();
		if (selectedInx > 0) {
			String driverName = (String) remoteDriverComboBox.getSelectedItem();

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
				JOptionPane.showMessageDialog(mainFrame, "unknwon error::" + e1.getMessage());
				return;
			}
		}
	}

	private void remoteTreeMouseClicked(MouseEvent e) {

		int selRow = remoteTree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = remoteTree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			RemoteFileTreeNode selNode = (RemoteFileTreeNode) selPath.getLastPathComponent();
			if (selNode.isDirectory() && e.getClickCount() == 2 && !selNode.isRoot()) {
				// myDoubleClick(selRow, selPath);
				log.debug(String.format("selNode.getFileName=[%s]", selNode.getFileName()));

				String requestPathString = new StringBuilder(remoteRootNode.getFileName()).append(remotePathSeperator)
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
					JOptionPane.showMessageDialog(mainFrame, "unknown error::" + e1.getMessage());
					return;
				}
			}
		}
	}

	private int getFileBlockSize() {
		return (1024 * 63);
	}

	private RemoteFileTreeNode getRemoteTreeNodeHavingSameFileName(String localFileName) {

		int cntOfChild = remoteRootNode.getChildCount();
		for (int i = 0; i < cntOfChild; i++) {
			RemoteFileTreeNode remoteFileTreeNode = (RemoteFileTreeNode) remoteRootNode.getChildAt(i);
			String remoteTempFileName = remoteFileTreeNode.getFileName();

			if (remoteTempFileName.equals(localFileName)) {
				return remoteFileTreeNode;
			}
		}
		return null;
	}

	private void uploadButtonActionPerformed(ActionEvent e) {
		try {
			uploadButton.setEnabled(false);

			TreePath localSelectedPath = localTree.getSelectionPath();
			if (null == localSelectedPath) {
				JOptionPane.showMessageDialog(mainFrame, "로컬 파일을 선택해 주세요.");
				return;
			}

			LocalFileTreeNode localSelectedNode = (LocalFileTreeNode) localSelectedPath.getLastPathComponent();

			if (localSelectedNode.isDirectory()) {
				String errorMessage = String.format("로컬 디렉토리[%s]를 선택하였습니다. 로컬 파일을 선택해 주세요.",
						localSelectedNode.getFileName());
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			}

			String localFilePathName = (String) localRootNode.getUserObject();
			String localFileName = localSelectedNode.getFileName();
			long localFileSize = localSelectedNode.getFileSize();
			if (0 == localFileSize) {
				String errorMessage = "업 로드할 파일 크기가 0 입니다.";
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			}

			String remoteFilePathName = (String) remoteRootNode.getUserObject();
			String remoteFileName = "";
			long remoteFileSize = 0L;
			int fileBlockSize = getFileBlockSize();
			boolean append = false;

			RemoteFileTreeNode remoteFileTreeNode = null;

			UserSelectableMode userSelectableMode = UserSelectableMode.NON_USER_SELECTABLE;
			// long totalReceivedDataSize = 0L;

			TreePath remoteSelectedPath = remoteTree.getSelectionPath();
			if (null != remoteSelectedPath) {
				RemoteFileTreeNode remoteSelectedNode = (RemoteFileTreeNode) remoteSelectedPath.getLastPathComponent();

				if (remoteSelectedNode.isRoot()) {
					remoteFileTreeNode = getRemoteTreeNodeHavingSameFileName(localFileName);

				} else {
					userSelectableMode = UserSelectableMode.USER_SELECTABLE;
					remoteFileTreeNode = remoteSelectedNode;
				}
			} else {
				remoteFileTreeNode = getRemoteTreeNodeHavingSameFileName(localFileName);
			}

			if (null == remoteFileTreeNode) {
				/** 중복된 이름을 갖는 원격지 트리 노드가 없다면 덮어쓰기로 설정 */
				append = false;
			} else {
				remoteFileName = remoteFileTreeNode.getFileName();
				remoteFileSize = remoteFileTreeNode.getFileSize();

				if (remoteFileTreeNode.getFileType() == FileType.Directory) {
					if (userSelectableMode == UserSelectableMode.NON_USER_SELECTABLE) {
						/**
						 * <pre>
						 *  
						 * 사용자가 직접 업로드 파일이 위치할 경로를 지정 하지 않았을 경우
						 * 업로드 하고자 하는 파일과 동일한 이름의 경로가 존재하므로
						 * 수행 불가 메시지를 보여주고 처리 종료.
						 * </pre>
						 */
						JOptionPane.showMessageDialog(mainFrame, "업로드 하고자 하는 로컬 파일과 동일한 이름을 갖는 원격지 경로가 존재합니다.");
						remoteTree.setSelectionPath(new TreePath(remoteFileTreeNode.getPath()));
						return;
					} else {
						/**
						 * 사용자가 직접 업로드 파일이 위치할 경로를 지정 했을 경우 경로명과 파일명 재 조정후 덮어쓰기로
						 * 설정
						 */
						StringBuilder targetPathBuilder = new StringBuilder(remoteFilePathName);
						targetPathBuilder.append(remotePathSeperator);
						targetPathBuilder.append(remoteFileName);
						remoteFilePathName = targetPathBuilder.toString();
						remoteFileName = "";

						/** 덮어쓰기 */
						append = false;
					}
				} else {
					if (remoteFileSize > 0) {
						/**
						 * 업로드 하고자 하는 파일과 동일한 이름의 파일의 크기가 0보다 큰 경우 이어붙이기/덮어쓰기/취소
						 * 여부 묻기
						 */

						Object[] options = { "이어받기", "덮어쓰기", "취소" };
						int selectedOption = JOptionPane.showOptionDialog(mainFrame,
								String.format("로컬 파일[%s]과 동일한 파일이 원격지 작업 경로[%s]에 존재합니다. 이어받기/덮어쓰기/취소를 선택하세요",
										localFileName, remoteFilePathName),
								"이어받기 확인창", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
								options, options[0]);

						/** 취소 */
						if (JOptionPane.CANCEL_OPTION == selectedOption)
							return;

						if (JOptionPane.NO_OPTION == selectedOption) {
							/** 덮어쓰기 */
							append = false;
						} else {
							/** 이어 받기 */
							append = true;
						}
					} else {
						/** 업로드 하고자 하는 파일과 동일한 이름의 파일의 크기가 0인 경우 덮어쓰기로 설정 */
						append = false;
					}
				}
			}

			// FIXME!
			log.info(String.format(
					"copy localFilePathName[%s] localFileName[%s] to remoteFilePathName[%s] remoteFileName[%s]",
					localFilePathName, localFileName, remoteFilePathName, remoteFileName));

			LocalSourceFileResource localSourceFileResource = null;
			try {
				localSourceFileResource = localSourceFileResourceManager.registerNewLocalSourceFileResource(loginID,
						append, localFilePathName, localFileName, localFileSize, remoteFilePathName, remoteFileName,
						remoteFileSize, fileBlockSize);
			} catch (IllegalArgumentException e1) {
				String errorMessage = e1.toString();
				log.warn(errorMessage, e1);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			} catch (UpDownFileException e1) {
				String errorMessage = e1.toString();
				log.warn(errorMessage, e1);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			}

			if (null == localSourceFileResource) {
				JOptionPane.showMessageDialog(mainFrame, "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
				return;
			}

			ReadyToUploadReq readyToUploadReq = new ReadyToUploadReq();
			if (append)
				readyToUploadReq.setWhetherToAppend((byte) 1);
			else
				readyToUploadReq.setWhetherToAppend((byte) 0);
			readyToUploadReq.setClientSourceFileID(localSourceFileResource.getSourceFileID());
			readyToUploadReq.setLocalFilePathName(localFilePathName);
			readyToUploadReq.setLocalFileName(localFileName);
			readyToUploadReq.setLocalFileSize(localFileSize);
			readyToUploadReq.setRemoteFilePathName(remoteFilePathName);
			readyToUploadReq.setRemoteFileName(remoteFileName);
			readyToUploadReq.setRemoteFileSize(remoteFileSize);
			readyToUploadReq.setFileBlockSize(fileBlockSize);

			AbstractMessage outObj = null;
			try {
				outObj = mainProejctSyncConnectionManager.sendSyncInputMessage(readyToUploadReq);
			} catch (SocketTimeoutException e1) {
				log.warn(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				mainProejctSyncConnectionManager.closeConnection();
				screenManager.goToLoginScreen();
				return;
			} catch (ServerNotReadyException e1) {
				log.warn(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				mainProejctSyncConnectionManager.closeConnection();
				screenManager.goToLoginScreen();
				return;
			} catch (NoMoreDataPacketBufferException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (BodyFormatException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (DynamicClassCallException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (ServerTaskException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (NotLoginException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (ConnectionPoolTimeoutException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				System.exit(1);
			} catch (InterruptedException e1) {
				log.error(e1.getMessage(), e1);
				JOptionPane.showMessageDialog(mainFrame,
						UserMessageManger.getInstance().getMessageWhenInterruptedException());
				System.exit(1);
			}

			log.debug(outObj.toString());

			if (!(outObj instanceof ReadyToUploadRes)) {
				String errorMessage = String.format(
						"ReadyToUploadReq 입력 메시지에 대한 출력 메시지가 ReadyToUploadRes 가 아닙니다. 출력 메시지 식별자=[%s]",
						outObj.getMessageID());
				log.error(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				System.exit(1);
			}

			ReadyToUploadRes readyToUploadRes = (ReadyToUploadRes) outObj;

			String taskResult = readyToUploadRes.getTaskResult();
			String resultMessage = readyToUploadRes.getResultMessage();
			int serverTargetFileID = readyToUploadRes.getServerTargetFileID();
			int clientSourceFileID = readyToUploadRes.getClientSourceFileID();

			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return;
			}

			int workingClientSourceFileID = localSourceFileResource.getSourceFileID();

			if (clientSourceFileID != workingClientSourceFileID) {
				String errorMessage = String.format("서버 clientSourceFileID[%d] 와 클라이언트 clientSourceFileID[%d] 불일치",
						clientSourceFileID, workingClientSourceFileID);
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			}

			localSourceFileResource.setTargetFileID(serverTargetFileID);

			

			SyncUploadFileTransferTask syncUploadFileTransferTask = new SyncUploadFileTransferTask(
					localSourceFileResource);

			syncUploadFileTransferTask.start();

		} finally {
			uploadButton.setEnabled(true);
		}
	}

	public class SyncUploadFileTransferTask extends Thread implements FileTransferTaskIF {
		private Logger log = LoggerFactory.getLogger(SyncUploadFileTransferTask.class);

		private LocalSourceFileResource localSourceFileResource = null;
		// private FileTranferProcessDialogIF fileTranferProcessDialog = null;
		private int sourceFileID = -1;

		public SyncUploadFileTransferTask(LocalSourceFileResource localSourceFileResource) {
			this.localSourceFileResource = localSourceFileResource;
			this.sourceFileID = localSourceFileResource.getSourceFileID();
			
			FileTranferProcessInformationDialog fileTranferProcessInfomationDialog = new FileTranferProcessInformationDialog(
					mainFrame, "Upload Process Bar Dialog", localSourceFileResource.getSourceFileSize(),
					localSourceFileResource.getStartOffset(), this);
			fileTranferProcessInfomationDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			/*
			 * fileTranferProcessInfomationDialog.addWindowListener(new
			 * WindowAdapter() { public void windowClosing(WindowEvent we) { //
			 * nothing } });
			 */
			fileTranferProcessInfomationDialog.setVisible(true);

			localSourceFileResource.setFileTranferProcessInformationDialog(fileTranferProcessInfomationDialog);
		}

		private void doCancel() {
			SyncCancelUploadReq syncCancelUploadReq = new SyncCancelUploadReq();
			syncCancelUploadReq.setClientSourceFileID(localSourceFileResource.getSourceFileID());
			syncCancelUploadReq.setServerTargetFileID(localSourceFileResource.getTargetFileID());

			AbstractMessage outObj = null;
			try {
				outObj = mainProejctSyncConnectionManager.sendSyncInputMessage(syncCancelUploadReq);
			} catch (SocketTimeoutException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());

				localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);
				mainProejctSyncConnectionManager.closeConnection();
				screenManager.goToLoginScreen();
				return;
			} catch (ServerNotReadyException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());

				localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);
				mainProejctSyncConnectionManager.closeConnection();
				screenManager.goToLoginScreen();
				return;
			} catch (NoMoreDataPacketBufferException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (BodyFormatException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (DynamicClassCallException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (ServerTaskException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (NotLoginException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (ConnectionPoolTimeoutException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				System.exit(1);
			}

			log.debug(outObj.toString());

			if (!(outObj instanceof SyncCancelUploadRes)) {
				String errorMessage = String.format(
						"SyncCancelUploadReq 입력 메시지에 대한 출력 메시지가 SyncCancelUploadRes 가 아닙니다. 출력 메시지 식별자=[%s]",
						outObj.getMessageID());
				log.error(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				System.exit(1);
			}

			SyncCancelUploadRes syncCancelUploadRes = (SyncCancelUploadRes) outObj;
			String taskResult = syncCancelUploadRes.getTaskResult();
			String resultMessage = syncCancelUploadRes.getResultMessage();

			localSourceFileResource.setWorkStep(LocalSourceFileResource.WorkStep.CANCEL_DONE);
			localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);

			if (taskResult.equals("Y")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
			} else {
				JOptionPane.showMessageDialog(mainFrame, String.format("go to login screen becase %s", resultMessage));

				mainProejctSyncConnectionManager.closeConnection();
				screenManager.goToLoginScreen();
			}
		}

		@Override
		public void run() {
			final int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();
			final int startFileBlockNo = localSourceFileResource.getStartFileBlockNo();

			for (int currentFileBlockNo=startFileBlockNo; currentFileBlockNo <= endFileBlockNo; currentFileBlockNo++) {
				if (localSourceFileResource.getWorkStep() == LocalSourceFileResource.WorkStep.CANCEL_WORKING) {
					doCancel();
					return;
				}

				// byte fileData[] = localSourceFileResource.getByteArrayOfFileBlockNo(currentFileBlockNo);
				byte fileData[] = null;

				try {
					fileData = localSourceFileResource.readSourceFileData(sourceFileID, currentFileBlockNo);
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (UpDownFileException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				}
				SyncUploadReq syncUploadReq = new SyncUploadReq();
				syncUploadReq.setClientSourceFileID(localSourceFileResource.getSourceFileID());
				syncUploadReq.setServerTargetFileID(localSourceFileResource.getTargetFileID());
				syncUploadReq.setFileBlockNo(currentFileBlockNo);
				syncUploadReq.setFileData(fileData);				

				AbstractMessage outObj = null;
				try {
					outObj = mainProejctSyncConnectionManager.sendSyncInputMessage(syncUploadReq);
				} catch (SocketTimeoutException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());

					localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);
					mainProejctSyncConnectionManager.closeConnection();
					screenManager.goToLoginScreen();
					return;
				} catch (ServerNotReadyException e) {
					log.warn(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());

					localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);
					mainProejctSyncConnectionManager.closeConnection();
					screenManager.goToLoginScreen();
					return;
				} catch (NoMoreDataPacketBufferException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (BodyFormatException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (DynamicClassCallException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (ServerTaskException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (NotLoginException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (ConnectionPoolTimeoutException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage());
					System.exit(1);
				}

				// log.debug(outObj.toString());

				if (!(outObj instanceof SyncUploadRes)) {
					String errorMessage = String.format(
							"SyncUploadReq 입력 메시지에 대한 출력 메시지가 SyncUploadRes 가 아닙니다. 출력 메시지 식별자=[%s]",
							outObj.getMessageID());
					log.error(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					System.exit(1);
				}

				SyncUploadRes syncUploadRes = (SyncUploadRes) outObj;
				String taskResult = syncUploadRes.getTaskResult();
				String resultMessage = syncUploadRes.getResultMessage();

				if (taskResult.equals("N")) {
					/**
					 * FIXME! 재시도와 중지 여부를 물어 본다.
					 * 
					 */
					String questionMessage = String.format("에러 내용 : %s,%s재시도 혹은 중지 여부를 선택해주세요", resultMessage,
							CommonStaticFinalVars.NEWLINE);
					Object[] options = { "Retry", "Stop upload" };
					int n = JOptionPane.showOptionDialog(mainFrame, questionMessage,
							"Retry or Stop Option Choose Dialog", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

					/**
					 * 에러가 난 데이터 패킷 번호를 유지하기 위해서 1 차감한다.
					 */
					if (JOptionPane.YES_OPTION == n) {
						/** 재시도 */
						log.info("{} 번째 업로드 데이터 패킷 에러에 따른 사용자 재시도 요청", currentFileBlockNo);

					} else {
						/** 중지 */
						log.info("{} 번째 업로드 데이터 패킷 에러에 따른 사용자 중지 요청", currentFileBlockNo);
						localSourceFileResource.setWorkStep(LocalSourceFileResource.WorkStep.CANCEL_WORKING);
					}
					currentFileBlockNo--;
					continue;
				}

				localSourceFileResource.turnOnWorkedFileBlockBitSetAt(currentFileBlockNo);
			}

			// FIXME!
			// log.info("whetherTransferIsCompletedAsWanted={}",
			// localSourceFileResource.whetherTransferIsCompletedAsWanted());

			localSourceFileResource.setWorkStep(LocalSourceFileResource.WorkStep.TRANSFER_DONE);
			localSourceFileResourceManager.removeWithUnlockFile(localSourceFileResource);

		}

		@Override
		public void cancelTask() {
			log.info("사용자의 업로드 중지 요청");
			localSourceFileResource.setWorkStep(LocalSourceFileResource.WorkStep.CANCEL_WORKING);
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

		// ======== this ========
		setPreferredSize(new Dimension(800, 600));
		setLayout(new FormLayout("$ugap, 160dlu:grow, $ugap, center:[50dlu,pref]:grow, $ugap, 160dlu:grow, $ugap",
				"$lgap, default, $lgap, fill:265dlu:grow, $lgap"));

		// ---- localTitleLabel ----
		localTitleLabel.setText("local");
		add(localTitleLabel, CC.xy(2, 2, CC.CENTER, CC.DEFAULT));

		// ---- prevButton ----
		prevButton.setAction(null);
		prevButton.setToolTipText("go to login screen after disconnection");
		prevButton.setText("go to login screen");
		prevButton.addActionListener(e -> prevButtonActionPerformed(e));
		add(prevButton, CC.xy(4, 2));

		// ---- remoteTitleLabel ----
		remoteTitleLabel.setText("remote");
		add(remoteTitleLabel, CC.xy(6, 2, CC.CENTER, CC.DEFAULT));

		// ======== localPanel ========
		{
			localPanel.setLayout(new FormLayout("default:grow", "default, $lcgap, fill:217dlu:grow"));

			// ======== localMenuPanel ========
			{
				localMenuPanel.setLayout(new FormLayout("default:grow, 2*($lcgap, default)", "default"));

				// ---- localDriverComboBox ----
				localDriverComboBox.setAction(null);
				localDriverComboBox.setMaximumSize(new Dimension(63, 27));
				localDriverComboBox.addItemListener(e -> localDriverComboBoxItemStateChanged(e));
				localMenuPanel.add(localDriverComboBox, CC.xy(1, 1));

				// ---- localParentDirecotryMoveButton ----
				localParentDirecotryMoveButton.setAction(null);
				localParentDirecotryMoveButton.setText("..");
				localParentDirecotryMoveButton.addActionListener(e -> localParentDirecotryMoveButtonActionPerformed(e));
				localMenuPanel.add(localParentDirecotryMoveButton, CC.xy(3, 1));

				// ---- localReloadButton ----
				localReloadButton.setAction(null);
				localReloadButton.setText("reload");
				localReloadButton.addActionListener(e -> localReloadButtonActionPerformed(e));
				localMenuPanel.add(localReloadButton, CC.xy(5, 1));
			}
			localPanel.add(localMenuPanel, CC.xy(1, 1));

			// ======== localScrollPane ========
			{

				// ---- localTree ----
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

		// ======== menuPanel ========
		{
			menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

			// ---- uploadButton ----
			uploadButton.setText("upload");
			uploadButton.addActionListener(e -> uploadButtonActionPerformed(e));
			menuPanel.add(uploadButton);

			// ---- menuVerticalSpacer ----
			menuVerticalSpacer.setMinimumSize(new Dimension(12, 12));
			menuVerticalSpacer.setRequestFocusEnabled(false);
			menuVerticalSpacer.setVerifyInputWhenFocusTarget(false);
			menuVerticalSpacer.setPreferredSize(new Dimension(10, 15));
			menuPanel.add(menuVerticalSpacer);

			// ---- downloadButton ----
			downloadButton.setText("download");
			menuPanel.add(downloadButton);
		}
		add(menuPanel, CC.xy(4, 4, CC.CENTER, CC.CENTER));

		// ======== remotePanel ========
		{
			remotePanel.setLayout(new FormLayout("default:grow", "default, $lgap, fill:default:grow"));

			// ======== remoteMenuPanel ========
			{
				remoteMenuPanel.setLayout(new FormLayout("default:grow, 2*($lcgap, default)", "default"));

				// ---- remoteDriverComboBox ----
				remoteDriverComboBox.addActionListener(e -> remoteDriverComboBoxActionPerformed(e));
				remoteMenuPanel.add(remoteDriverComboBox, CC.xy(1, 1));

				// ---- remoteParentDirectoryMoveButton ----
				remoteParentDirectoryMoveButton.setText("..");
				remoteParentDirectoryMoveButton.setToolTipText("go to remote parent direcotory");
				remoteParentDirectoryMoveButton
						.addActionListener(e -> remoteParentDirectoryMoveButtonActionPerformed(e));
				remoteMenuPanel.add(remoteParentDirectoryMoveButton, CC.xy(3, 1));

				// ---- remoteRealodButton ----
				remoteRealodButton.setText("reload");
				remoteRealodButton.addActionListener(e -> remoteRealodButtonActionPerformed(e));
				remoteMenuPanel.add(remoteRealodButton, CC.xy(5, 1));
			}
			remotePanel.add(remoteMenuPanel, CC.xy(1, 1));

			// ======== remoteScrollPane ========
			{

				// ---- remoteTree ----
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