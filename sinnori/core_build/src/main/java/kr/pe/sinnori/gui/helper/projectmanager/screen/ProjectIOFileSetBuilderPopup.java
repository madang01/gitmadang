/*
 * Created by JFormDesigner on Sat Feb 11 21:46:54 KST 2017
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.builder.IOFileSetContentsBuilderManager;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.FileLastModifiedComparator;
import kr.pe.sinnori.common.util.XMLFileFilter;
import kr.pe.sinnori.gui.helper.iobuilder.screen.BuildFunctionManagerIF;
import kr.pe.sinnori.gui.helper.iobuilder.screen.FileFunctionManagerIF;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.BuildFunctionCellEditorForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.BuildFunctionCellRendererForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.BuildFunctionCellValueForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.FileFunctionCellEditorForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.FileFunctionCellRendererForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.FileFunctionCellValueForProject;
import kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo.MessageInfoTableModelForProject;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class ProjectIOFileSetBuilderPopup extends JDialog implements FileFunctionManagerIF, BuildFunctionManagerIF {
	private Logger log = LoggerFactory.getLogger(ProjectIOFileSetBuilderPopup.class);

	private Frame ownerFrame = null;
	private String sinnoriInstalledPathString;
	private String mainProjectName;
	private ArrayList<String> otherMainProjectNameList;

	/** MessageInfo Table Model start */
	private MessageInfoTableModelForProject messageInfoTableModel = null;

	private String titles[] = { "message id", "recently modified date", "direction", "mesg info file function",
			"io source build function" };

	private Class<?>[] columnTypes = new Class[] { String.class, String.class, String.class,
			FileFunctionCellValueForProject.class, BuildFunctionCellValueForProject.class };
	/** MessageInfo Table Model end */

	private IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
			.getInstance();

	private enum SEARCH_MODE {
		ALL, SEARCH
	};

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(ownerFrame,
				CommonStaticUtil.splitString(message, CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}

	private void rebuildMessageInfoTable(SEARCH_MODE searchMode) {
		Toolkit.getDefaultToolkit().beep();
		Cursor oldCursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		MessageInfoListSwingWorker allMessageInfoTask = new MessageInfoListSwingWorker(searchMode);
		// task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
		ArrayList<MessageInfo> messageInfoList = null;
		try {
			messageInfoList = allMessageInfoTask.get();

		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
			showMessageDialog(e.toString());
			return;
		} catch (ExecutionException e) {
			log.warn("ExecutionException", e);
			showMessageDialog(e.toString());
			return;
		}

		Object values[][] = new Object[messageInfoList.size()][titles.length];

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

		for (int i = 0; i < values.length; i++) {
			MessageInfo messageInfo = messageInfoList.get(i);

			String messageID = messageInfo.getMessageID();
			values[i][0] = messageID;
			values[i][1] = sdf.format(messageInfo.getLastModified());
			if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL) {
				values[i][2] = "client <-> server";
			} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER) {
				values[i][2] = "client -> server";
			} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET) {
				values[i][2] = "server -> client";
			} else {
				values[i][2] = "no direction";
			}

			values[i][3] = new FileFunctionCellValueForProject(i, messageInfo, ProjectIOFileSetBuilderPopup.this,
					ownerFrame);
			values[i][4] = new BuildFunctionCellValueForProject(messageInfo, ProjectIOFileSetBuilderPopup.this,
					ownerFrame);
		}

		createTable(values);
		setCursor(oldCursor);
	}

	private void createEmptyTable() {
		createTable(new Object[0][titles.length]);
	}

	private void createTable(Object values[][]) {
		messageInfoTableModel = new MessageInfoTableModelForProject(values, titles, columnTypes);

		/** 모델 교체중 repaint event 를 막기 위해서 잠시 visable 속성을 끔 */
		messageInfoTable.setEnabled(false);
		messageInfoTable.setVisible(false);
		messageInfoTable.setModel(messageInfoTableModel);

		messageInfoTable.getColumnModel().getColumn(0).setPreferredWidth(120);

		// table.getColumnModel().getColumn(1).setResizable(false);
		messageInfoTable.getColumnModel().getColumn(1).setPreferredWidth(95);

		// table.getColumnModel().getColumn(2).setResizable(false);
		messageInfoTable.getColumnModel().getColumn(2).setPreferredWidth(75);

		messageInfoTable.getColumnModel().getColumn(3).setResizable(false);
		messageInfoTable.getColumnModel().getColumn(3).setPreferredWidth(150);

		messageInfoTable.getColumnModel().getColumn(4).setResizable(false);
		messageInfoTable.getColumnModel().getColumn(4).setPreferredWidth(175);

		messageInfoTable.getColumnModel().getColumn(3).setCellRenderer(new FileFunctionCellRendererForProject());
		messageInfoTable.getColumnModel().getColumn(3)
				.setCellEditor(new FileFunctionCellEditorForProject(new JCheckBox()));

		messageInfoTable.getColumnModel().getColumn(4).setCellRenderer(new BuildFunctionCellRendererForProject());
		messageInfoTable.getColumnModel().getColumn(4)
				.setCellEditor(new BuildFunctionCellEditorForProject(new JCheckBox()));

		// messageInfoTable.repaint();

		if (values.length > 0) {
			/**
			 * 한줄 이상일 경우 첫번째 줄의 각 칼럼들에 쓰인 렌더러의 높이와 줄 높이중 가장 큰수를 줄의 높이로 정한다.
			 */
			int rowHeight = messageInfoTable.getRowHeight();
			for (int column = 0; column < messageInfoTable.getColumnCount(); column++) {
				Component comp = messageInfoTable.prepareRenderer(messageInfoTable.getCellRenderer(0, column), 0,
						column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}
			messageInfoTable.setRowHeight(rowHeight);
		}

		/** 모델 교체중 repaint event 를 막기 위해서 잠시 visible 속성 복귀 */
		messageInfoTable.setEnabled(true);
		messageInfoTable.setVisible(true);
		messageInfoScrollPane.repaint();
	}

	private File getWritablePathFile(String pathText) throws FileNotFoundException, AccessDeniedException {
		java.io.File sourcePath = new java.io.File(pathText);
		if (!sourcePath.exists()) {
			throw new FileNotFoundException();
		}

		if (!sourcePath.canWrite()) {
			throw new AccessDeniedException(pathText, null, "not writable path");
		}

		return sourcePath;
	}

	private void copyTransferToFile(File sourceFile, File targetFile) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);

			FileChannel souceFileChannel = fis.getChannel();
			FileChannel targetFileChannel = fos.getChannel();

			souceFileChannel.transferTo(0, souceFileChannel.size(), targetFileChannel);
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (Exception e) {
				log.warn("fail to close source file[" + sourceFile.getAbsolutePath() + "] input stream", e);
			}
			try {
				if (null != fos)
					fos.close();
			} catch (Exception e) {
				log.warn("fail to close target file[" + sourceFile.getAbsolutePath() + "] output stream", e);
			}
		}
	}

	private void saveFile(String title, File targetFile, String contents) throws IOException {
		if (null == title)
			throw new IllegalArgumentException("the parameter 'title' is null");
		if (null == targetFile)
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		if (null == contents)
			throw new IllegalArgumentException("the parameter 'contents' is null");

		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}

		if (!targetFile.canWrite()) {
			String errorMessage = "the file can't be written";
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				log.warn("fail to close the file[{}][{}] output stream", title, targetFile.getAbsolutePath());
			}
		}
	}

	
	private boolean saveIOFileSetToTargetPath(ArrayList<File> listOfTargetPathSavingIOFileSet, String author,
			boolean isSelectedIO, boolean isSelectedDirection, MessageInfo messageInfo) {

		for (File targetPathSavingIOFileSet : listOfTargetPathSavingIOFileSet) {
			String messageID = messageInfo.getMessageID();
			String messageTargetPathString = targetPathSavingIOFileSet.getAbsolutePath() + File.separator + messageID;
			File messageTargetPath = new File(messageTargetPathString);
			if (!messageTargetPath.exists()) {
				boolean result = messageTargetPath.mkdir();
				if (!result) {
					String errorMessage = String.format("fail to create the message[%s] target path[%s]", messageID,
							messageTargetPathString);
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}
			}

			if (!messageTargetPath.canWrite()) {
				String errorMessage = String.format("the message[%s] target path[%s] can't be written", messageID,
						messageTargetPath.getAbsolutePath());
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				continue;
			}

			if (isSelectedIO) {
				File messageFile = new File(messageTargetPath.getAbsolutePath() + File.separator + messageID + ".java");
				File messageEncoderFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "Encoder.java");
				File messageDecoderFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "Decoder.java");

				String title = null;
				title = "the message class";
				try {
					saveFile(title, messageFile, ioFileSetContentsBuilderManager.getMessageSourceFileContents(messageID,
							author, messageInfo));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title,
							messageFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				title = "the message encoder class";
				try {
					saveFile(title, messageEncoderFile, ioFileSetContentsBuilderManager
							.getEncoderSourceFileContents(messageID, author, messageInfo));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title,
							messageEncoderFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				title = "the message decoder class";
				try {
					saveFile(title, messageDecoderFile, ioFileSetContentsBuilderManager
							.getDecoderSourceFileContents(messageID, author, messageInfo));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title,
							messageDecoderFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

			}

			if (isSelectedDirection) {
				File messageServerCodecFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "ServerCodec.java");
				File messageClientCodecFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "ClientCodec.java");

				String title = null;
				title = "the server codec class";
				try {
					saveFile(title, messageServerCodecFile, ioFileSetContentsBuilderManager
							.getServerCodecSourceFileContents(messageInfo.getDirection(), messageID, author));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title,
							messageServerCodecFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				title = "the client codec class";
				try {
					saveFile(title, messageClientCodecFile, ioFileSetContentsBuilderManager
							.getClientCodecSourceFileContents(messageInfo.getDirection(), messageID, author));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title,
							messageClientCodecFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
			}
		}

		return true;
	}

	private ArrayList<File> getListOfWritablePathSavingIOFileSet(String selectedMainProjectName) {
		ArrayList<File> listOfPathSavingIOFileSet = new ArrayList<File>();

		/*String selectedMainProjectName = null;
		if (isOtherMainProjectCheckBox.isSelected()) {
			if (otherMainProjectComboBox.getItemCount() <= 1) {
				showMessageDialog("other project list empty!");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return null;
			}
			
			if (0 == otherMainProjectComboBox.getSelectedIndex()) {
				showMessageDialog("you need to select one of other project list");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return null;
			}
			
			selectedMainProjectName = (String) otherMainProjectComboBox.getSelectedItem();
		} else {
			selectedMainProjectName = mainProjectName;
		}*/

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(selectedMainProjectName,
				selectedMainProjectName);
		File serverBuildPath = null;
		try {
			serverBuildPath = getWritablePathFile(serverBuildPathString);
			listOfPathSavingIOFileSet.add(serverBuildPath);
		} catch (FileNotFoundException e) {
			log.info("skip add the project[{}]'s server build path saving io file set because it doesn't exist",
					selectedMainProjectName);
		} catch (AccessDeniedException e) {
			log.info("skip add the project[{}]'s server build path saving io file set because it is not writable",
					selectedMainProjectName);
		}

		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(selectedMainProjectName,
				selectedMainProjectName);
		File appClientBuildPath = null;
		try {
			appClientBuildPath = getWritablePathFile(appClientBuildPathString);
			listOfPathSavingIOFileSet.add(appClientBuildPath);
		} catch (FileNotFoundException e) {
			log.info("skip add the project[{}]'s applicaton client build path saving io file set because it doesn't exist",
					selectedMainProjectName);
		} catch (AccessDeniedException e) {
			log.info("skip add the project[{}]'s applicaton client build path saving io file set because it is not writable",
					selectedMainProjectName);
		}

		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(selectedMainProjectName,
				selectedMainProjectName);
		File webClientBuildPath = null;
		try {
			webClientBuildPath = getWritablePathFile(webClientBuildPathString);
			listOfPathSavingIOFileSet.add(webClientBuildPath);
		} catch (FileNotFoundException e) {
			log.info("skip add the project[{}]'s web client build path saving io file set because it doesn't exist",
					selectedMainProjectName);
		} catch (AccessDeniedException e) {
			log.info("skip add the project[{}]'s web client build path saving io file set because it is not writable",
					selectedMainProjectName);
		}

		return listOfPathSavingIOFileSet;
	}

	class MessageInfoListSwingWorker extends SwingWorker<ArrayList<MessageInfo>, Void> {

		private SEARCH_MODE searchMode;

		public MessageInfoListSwingWorker(SEARCH_MODE searchMode) {
			this.searchMode = searchMode;
		}

		@Override
		public ArrayList<MessageInfo> doInBackground() {
			ArrayList<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();
			ProgressMonitor progressMonitor = new ProgressMonitor(ownerFrame,
					"Running a Task reading message info files", "", 0, 100);

			String messgaeInfoPathText = messageInfoFilePathTextField.getText();
			if (null == messgaeInfoPathText) {
				String errorMessage = String.format("Please insert path of message info file(s)", messgaeInfoPathText);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);

				createEmptyTable();
				messageInfoFilePathTextField.requestFocusInWindow();
				return messageInfoList;
			}
			messgaeInfoPathText = messgaeInfoPathText.trim();
			if (messgaeInfoPathText.equals("")) {
				messageInfoFilePathTextField.setText(messgaeInfoPathText);
				String errorMessage = String.format("Please insert path of message info file(s) again",
						messgaeInfoPathText);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);

				createEmptyTable();
				messageInfoFilePathTextField.requestFocusInWindow();
				return messageInfoList;
			}

			File messgaeInfoPath = new File(messgaeInfoPathText);
			if (!messgaeInfoPath.exists()) {
				String errorMessage = String.format("The message info file(s)'s path[%s] doesn't exist",
						messgaeInfoPathText);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);

				createEmptyTable();
				messageInfoFilePathTextField.requestFocusInWindow();
				return messageInfoList;
			}

			if (!messgaeInfoPath.isDirectory()) {
				String errorMessage = String.format("The message info file(s)'s path[%s] is not a directory",
						messgaeInfoPath.getAbsolutePath());
				log.warn(errorMessage);
				showMessageDialog(errorMessage);

				createEmptyTable();
				messageInfoFilePathTextField.requestFocusInWindow();
				return messageInfoList;
			}

			if (!messgaeInfoPath.canRead()) {
				String errorMessage = String.format(
						"Please check read permission of the message info file(s)'s path[%s]",
						messgaeInfoPath.getAbsolutePath());
				log.warn(errorMessage);
				showMessageDialog(errorMessage);

				createEmptyTable();
				messageInfoFilePathTextField.requestFocusInWindow();
				return messageInfoList;
			}

			File messageInfoXMLFiles[] = messgaeInfoPath.listFiles(new XMLFileFilter());

			Arrays.sort(messageInfoXMLFiles, new FileLastModifiedComparator());

			String fileNameSearchKeyword = "";

			if (SEARCH_MODE.SEARCH == searchMode) {
				fileNameSearchKeyword = fileNameSearchTextField.getText().trim();
				fileNameSearchTextField.setText(fileNameSearchKeyword);

				if (0 == fileNameSearchKeyword.length()) {
					String errorMessage = "Please insert search keyword again";
					log.warn(errorMessage);
					showMessageDialog(errorMessage);

					createEmptyTable();
					fileNameSearchTextField.requestFocusInWindow();
					return messageInfoList;
				}

			}

			for (int i = 0; i < messageInfoXMLFiles.length; i++) {
				File messageInfoFile = messageInfoXMLFiles[i];

				if (!messageInfoFile.isFile()) {
					String errorMessage = String.format("warning :: not file , file name=[%s]",
							messageInfoFile.getName());
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}

				if (!messageInfoFile.canRead()) {
					String errorMessage = String.format("warning :: can't read, file name=[%s]",
							messageInfoFile.getName());
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}

				MessageInfoSAXParser messageInfoSAXParser = null;
				try {
					messageInfoSAXParser = new MessageInfoSAXParser();
				} catch (MessageInfoSAXParserException e) {
					String errorMessage = e.toString();
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}
				MessageInfo messageInfo = null;
				try {
					messageInfo = messageInfoSAXParser.parse(messageInfoFile, true);
				} catch (IllegalArgumentException | SAXException | IOException e) {
					String errorMessage = e.toString();
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}
				if (null != messageInfo) {
					if (SEARCH_MODE.SEARCH == searchMode) {
						String fileName = messageInfoFile.getName();
						if (fileName.indexOf(fileNameSearchKeyword) >= 0) {
							messageInfoList.add(messageInfo);
						}
					} else {
						messageInfoList.add(messageInfo);
					}
				}
				if (progressMonitor.isCanceled()) {
					return messageInfoList;
				}

				progressMonitor.setNote(i + "/" + messageInfoXMLFiles.length);
				progressMonitor.setProgress(i * 100 / messageInfoXMLFiles.length);
			}

			return messageInfoList;
		}
	}

	private void postInitComponents() {

		sinnoriInstalledPathTextField.setText(sinnoriInstalledPathString);
		mainProjectNameTextField.setText(mainProjectName);
		messageInfoFilePathTextField.setText(
				BuildSystemPathSupporter.getMessageInfoPathString(mainProjectName, sinnoriInstalledPathString));

		otherMainProjectComboBox.setEnabled(false);
		// otherMainProjectComboBox.addItem("- select other main project -");

		for (String otherMainProjectName : otherMainProjectNameList) {
			otherMainProjectComboBox.addItem(otherMainProjectName);
		}

		// readAllMessageInfo();
		messageInfoTable.setRowSelectionAllowed(false);
		messageInfoTable.setFillsViewportHeight(true);
		// messageInfoTable.setPreferredScrollableViewportSize(new
		// Dimension(800, 300));
		messageInfoTable.setAutoCreateRowSorter(true);
		// messageInfoTable.setRowHeight(38);
		// messageInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// messageInfoTable.setMaximumSize(new Dimension(700, 600));

	}

	public ProjectIOFileSetBuilderPopup(Frame ownerFrame, String sinnoriInstalledPathString, String mainProjectName,
			ArrayList<String> otherMainProjectNameList) {
		super(ownerFrame);
		this.ownerFrame = ownerFrame;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.mainProjectName = mainProjectName;
		this.otherMainProjectNameList = otherMainProjectNameList;

		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		projectIOFileSetBuilderPanel = new JPanel();
		sinnoriInstalledPathPanel = new JPanel();
		sinnoriInstalledPathLabel = new JLabel();
		sinnoriInstalledPathTextField = new JTextField();
		mainProjectNamePanel = new JPanel();
		mainProjectNameLabel = new JLabel();
		mainProjectNameTextField = new JTextField();
		messageInfoXMLPathPanel = new JPanel();
		messageInfoXMLPathLabel = new JLabel();
		messageInfoFilePathTextField = new JTextField();
		targetPanel = new JPanel();
		targetLabel = new JLabel();
		isOtherMainProjectCheckBox = new JCheckBox();
		otherMainProjectComboBox = new JComboBox<>();
		writerPanel = new JPanel();
		writerLabel = new JLabel();
		writerTextField = new JTextField();
		menuPanel = new JPanel();
		allMessageInfoCreationButton = new JButton();
		allMessageIOFileSetCreationButton = new JButton();
		resultLabel = new JLabel();
		messageIDSearchPanel = new JPanel();
		messageIDSearchLabel = new JLabel();
		fileNameSearchTextField = new JTextField();
		fileNameSearchButton = new JButton();
		messageInfoScrollPane = new JScrollPane();
		messageInfoTable = new JTable();

		// ======== this ========
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		/** Post-initialization Code start */
		postInitComponents();
		/** Post-initialization Code end */

		// ======== projectIOFileSetBuilderPanel ========
		{
			projectIOFileSetBuilderPanel.setLayout(
					new FormLayout("$ugap, default:grow, $ugap", "8*($lgap, default), $lgap, 70dlu:grow, $ugap"));

			// ======== sinnoriInstalledPathPanel ========
			{
				sinnoriInstalledPathPanel.setLayout(new FormLayout("78dlu, $ugap, 125dlu:grow", "default"));

				// ---- sinnoriInstalledPathLabel ----
				sinnoriInstalledPathLabel.setText("Sinnori installed path");
				sinnoriInstalledPathPanel.add(sinnoriInstalledPathLabel, CC.xy(1, 1));

				// ---- sinnoriInstalledPathTextField ----
				sinnoriInstalledPathTextField.setEditable(false);
				sinnoriInstalledPathPanel.add(sinnoriInstalledPathTextField, CC.xy(3, 1));
			}
			projectIOFileSetBuilderPanel.add(sinnoriInstalledPathPanel, CC.xy(2, 2));

			// ======== mainProjectNamePanel ========
			{
				mainProjectNamePanel.setLayout(new FormLayout("78dlu, $ugap, default:grow", "default"));

				// ---- mainProjectNameLabel ----
				mainProjectNameLabel.setText("Project name");
				mainProjectNamePanel.add(mainProjectNameLabel, CC.xy(1, 1));

				// ---- mainProjectNameTextField ----
				mainProjectNameTextField.setEditable(false);
				mainProjectNamePanel.add(mainProjectNameTextField, CC.xy(3, 1));
			}
			projectIOFileSetBuilderPanel.add(mainProjectNamePanel, CC.xy(2, 4));

			// ======== messageInfoXMLPathPanel ========
			{
				messageInfoXMLPathPanel.setLayout(new FormLayout("78dlu, $ugap, default:grow", "default"));

				// ---- messageInfoXMLPathLabel ----
				messageInfoXMLPathLabel.setText("Message info xml path");
				messageInfoXMLPathPanel.add(messageInfoXMLPathLabel, CC.xy(1, 1));

				// ---- messageInfoFilePathTextField ----
				messageInfoFilePathTextField.setEditable(false);
				messageInfoXMLPathPanel.add(messageInfoFilePathTextField, CC.xy(3, 1));
			}
			projectIOFileSetBuilderPanel.add(messageInfoXMLPathPanel, CC.xy(2, 6));

			// ======== targetPanel ========
			{
				targetPanel.setLayout(new FormLayout("78dlu, 2*($ugap, default)", "default"));

				// ---- targetLabel ----
				targetLabel.setText("Target");
				targetPanel.add(targetLabel, CC.xy(1, 1));

				// ---- isOtherMainProjectCheckBox ----
				isOtherMainProjectCheckBox.setText("other project");
				isOtherMainProjectCheckBox.addActionListener(e -> {
					isOtherProjectCheckBoxActionPerformed(e);
					isOtherProjectCheckBoxActionPerformed(e);
				});
				targetPanel.add(isOtherMainProjectCheckBox, CC.xy(3, 1));

				// ---- otherMainProjectComboBox ----
				otherMainProjectComboBox
						.setModel(new DefaultComboBoxModel<>(new String[] { "- select other main project -" }));
				otherMainProjectComboBox.setEnabled(false);
				targetPanel.add(otherMainProjectComboBox, CC.xy(5, 1));
			}
			projectIOFileSetBuilderPanel.add(targetPanel, CC.xy(2, 8));

			// ======== writerPanel ========
			{
				writerPanel.setLayout(new FormLayout("78dlu, $ugap, default:grow", "default"));

				// ---- writerLabel ----
				writerLabel.setText("Writer");
				writerPanel.add(writerLabel, CC.xy(1, 1));
				writerPanel.add(writerTextField, CC.xy(3, 1));
			}
			projectIOFileSetBuilderPanel.add(writerPanel, CC.xy(2, 10));

			// ======== menuPanel ========
			{
				menuPanel.setLayout(new FormLayout("default, $ugap, default", "default"));

				// ---- allMessageInfoCreationButton ----
				allMessageInfoCreationButton.setText("Reread all message infomation file");
				allMessageInfoCreationButton.addActionListener(e -> allMessageInfoCreationButtonActionPerformed(e));
				menuPanel.add(allMessageInfoCreationButton, CC.xy(1, 1));

				// ---- allMessageIOFileSetCreationButton ----
				allMessageIOFileSetCreationButton.setText("Build All IO source file set");
				allMessageIOFileSetCreationButton
						.addActionListener(e -> allMessageIOFileSetCreationButtonActionPerformed(e));
				menuPanel.add(allMessageIOFileSetCreationButton, CC.xy(3, 1));
			}
			projectIOFileSetBuilderPanel.add(menuPanel, CC.xy(2, 12));

			// ---- resultLabel ----
			resultLabel.setText(">> result rereading all message infomation file");
			projectIOFileSetBuilderPanel.add(resultLabel, CC.xy(2, 14));

			// ======== messageIDSearchPanel ========
			{
				messageIDSearchPanel
						.setLayout(new FormLayout("default, $ugap, default:grow, $ugap, default", "default"));

				// ---- messageIDSearchLabel ----
				messageIDSearchLabel.setText("message ID");
				messageIDSearchPanel.add(messageIDSearchLabel, CC.xy(1, 1));
				messageIDSearchPanel.add(fileNameSearchTextField, CC.xy(3, 1));

				// ---- fileNameSearchButton ----
				fileNameSearchButton.setText("Search");
				fileNameSearchButton.addActionListener(e -> fileNameSearchButtonActionPerformed(e));
				messageIDSearchPanel.add(fileNameSearchButton, CC.xy(5, 1));
			}
			projectIOFileSetBuilderPanel.add(messageIDSearchPanel, CC.xy(2, 16));

			// ======== messageInfoScrollPane ========
			{

				// ---- messageInfoTable ----
				messageInfoTable.setModel(new DefaultTableModel(
						new Object[][] { { null, null, "", null, null }, { null, null, null, null, null }, },
						new String[] { "message id", "recently modified date", "direction", "mesg info file function",
								"io source build function" }) {
					Class<?>[] columnTypes = new Class<?>[] { String.class, String.class, String.class, Object.class,
							Object.class };

					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				messageInfoTable.setFillsViewportHeight(true);
				messageInfoScrollPane.setViewportView(messageInfoTable);
			}
			projectIOFileSetBuilderPanel.add(messageInfoScrollPane, CC.xy(2, 18));
		}
		contentPane.add(projectIOFileSetBuilderPanel);
		pack();
		setLocationRelativeTo(getOwner());
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel projectIOFileSetBuilderPanel;
	private JPanel sinnoriInstalledPathPanel;
	private JLabel sinnoriInstalledPathLabel;
	private JTextField sinnoriInstalledPathTextField;
	private JPanel mainProjectNamePanel;
	private JLabel mainProjectNameLabel;
	private JTextField mainProjectNameTextField;
	private JPanel messageInfoXMLPathPanel;
	private JLabel messageInfoXMLPathLabel;
	private JTextField messageInfoFilePathTextField;
	private JPanel targetPanel;
	private JLabel targetLabel;
	private JCheckBox isOtherMainProjectCheckBox;
	private JComboBox<String> otherMainProjectComboBox;
	private JPanel writerPanel;
	private JLabel writerLabel;
	private JTextField writerTextField;
	private JPanel menuPanel;
	private JButton allMessageInfoCreationButton;
	private JButton allMessageIOFileSetCreationButton;
	private JLabel resultLabel;
	private JPanel messageIDSearchPanel;
	private JLabel messageIDSearchLabel;
	private JTextField fileNameSearchTextField;
	private JButton fileNameSearchButton;
	private JScrollPane messageInfoScrollPane;
	private JTable messageInfoTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	@Override
	public boolean saveIOFileSetOfSelectedMessageInfo(boolean isSelectedIO, boolean isSelectedDirection, MessageInfo sourceMessageInfo) {
		

		String writer = writerTextField.getText();

		if (null == writer) {
			writer = "";
		} else {
			writer = writer.trim();
		}
		writerTextField.setText(writer);

		if (writer.equals("")) {
			showMessageDialog("Please insert writer");
			writerTextField.requestFocusInWindow();
			return false;
		}
		
		String messageInfoSourcePathString = BuildSystemPathSupporter.getMessageInfoPathString(mainProjectName,
				sinnoriInstalledPathString);
		
		File messageInfoSourcePath = new File(messageInfoSourcePathString);
		if (messageInfoSourcePath.exists()) {
			String errorMessage = String.format("source project[%s]'s message info path[%s] doesn't exist",
					mainProjectName, messageInfoSourcePathString);
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return false;
		}
		if (messageInfoSourcePath.canRead()) {
			String errorMessage = String.format("source project[%s]'s message info path[%s] can't be read",
					mainProjectName, messageInfoSourcePathString);
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return false;
		}
		
		String selectedMainProjectName = mainProjectName;
		
		if (isOtherMainProjectCheckBox.isSelected()) {
			if (otherMainProjectComboBox.getItemCount() <= 1) {
				showMessageDialog("other project list empty!");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return false;
			}
			
			if (0 == otherMainProjectComboBox.getSelectedIndex()) {
				showMessageDialog("you need to select one of other project list");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return false;
			}
		
			
			String targetProjectName = selectedMainProjectName = (String) otherMainProjectComboBox.getSelectedItem();
			String messageInfoTargetPathString = BuildSystemPathSupporter.getMessageInfoPathString(targetProjectName,
					sinnoriInstalledPathString);
		
			File targetMessageInfoXMLPath = new File(messageInfoTargetPathString);
			if (targetMessageInfoXMLPath.exists()) {
				String errorMessage = String.format("target project[%s]'s message info path[%s] doesn't exist",
						targetProjectName, messageInfoTargetPathString);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				return false;
			}
			if (targetMessageInfoXMLPath.canWrite()) {
				String errorMessage = String.format("target project[%s]'s message info path[%s] can't be written",
						targetProjectName, messageInfoTargetPathString);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				return false;
			}
			
			File sourceMessageInfoXMLFIle = sourceMessageInfo.getMessageInfoXMLFile();
			String sourceLastFileName = sourceMessageInfoXMLFIle.getName();
			File targetMessageInfoXMLFile = new File(new StringBuilder().append(messageInfoTargetPathString)
					.append(File.separator).append(sourceLastFileName).toString());

			
			
			if (targetMessageInfoXMLFile.exists()) {
				String confirmDialogTitle = "message info xml file copy";
				String confirmDialogMessage = String.format(
						"The target project[%s]'s message information XML file[%s] exist.\nDo you want to overwrite it with the soruce proejct[%s]'s message information XML File?",
						targetProjectName, sourceMessageInfo.getMessageID(), mainProjectName);

				int answer = JOptionPane.showConfirmDialog(ownerFrame, confirmDialogMessage, confirmDialogTitle,
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

				if (answer != JOptionPane.OK_OPTION) {
					log.info(
							"The source project[{}]'s message information XML file[{}] was canceled overwriting on target project[{}]",
							mainProjectName, sourceMessageInfo.getMessageID(), targetProjectName);
					
					return false;
				}
			}
			
			try {
				copyTransferToFile(sourceMessageInfoXMLFIle, targetMessageInfoXMLFile);
			} catch (IOException e1) {
				String errorMessage = String.format("fail to copy souce file[%s] to target file[%s]",
						sourceMessageInfoXMLFIle.getAbsolutePath(), targetMessageInfoXMLFile.getAbsolutePath());
				log.warn(errorMessage, e1);
				showMessageDialog(errorMessage);
				return false;
			}
		}
		
		ArrayList<File> listOfTargetPathSavingIOFileSet = getListOfWritablePathSavingIOFileSet(selectedMainProjectName);
		if (0 == listOfTargetPathSavingIOFileSet.size()) {
			String errorMessage = String.format("the selected project[%s] hasn't any build paths",
					selectedMainProjectName);
			showMessageDialog(errorMessage);
			
			return false;
		}
		
		boolean resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer, isSelectedIO,
					isSelectedDirection, sourceMessageInfo);
				

		return resultSavingFile;
	}

	@Override
	public void updateRowOfMessageInfoTableAccordingToNewMessageInfoUpdate(int row, MessageInfo newMessageInfo) {
		String directionStr = null;
		if (newMessageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL) {
			directionStr = "client <-> server";
		} else if (newMessageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER) {
			directionStr = "client -> server";
		} else if (newMessageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET) {
			directionStr = "server -> client";
		} else {
			directionStr = "no direction";
		}
		messageInfoTableModel.setValueAt(directionStr, row, 1);

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		messageInfoTableModel.setValueAt(sdf.format(newMessageInfo.getLastModified()), row, 2);

		BuildFunctionCellValueForProject sourceFileCellValue = (BuildFunctionCellValueForProject) messageInfoTableModel
				.getValueAt(row, 4);
		sourceFileCellValue.setMessageInfo(newMessageInfo);
		messageInfoScrollPane.repaint();
	}

	private void isOtherProjectCheckBoxActionPerformed(ActionEvent e) {
		if (isOtherMainProjectCheckBox.isSelected()) {
			otherMainProjectComboBox.setEnabled(true);
		} else {
			otherMainProjectComboBox.setEnabled(false);
		}
	}

	private void allMessageInfoCreationButtonActionPerformed(ActionEvent e) {
		rebuildMessageInfoTable(SEARCH_MODE.ALL);
	}

	private void allMessageIOFileSetCreationButtonActionPerformed(ActionEvent e) {

		String writer = writerTextField.getText();

		if (null == writer) {
			writer = "";
		} else {
			writer = writer.trim();
		}
		writerTextField.setText(writer);

		if (writer.equals("")) {
			showMessageDialog("Please insert writer");
			writerTextField.requestFocusInWindow();
			return;
		}

		if (null == messageInfoTableModel) {
			showMessageDialog("You need to reread all message information file");
			writerTextField.requestFocusInWindow();
			return;
		}

		int rowCount = messageInfoTableModel.getRowCount();

		if (0 == rowCount) {
			showMessageDialog(
					"Any message information file doesn't exist. if you didn't reread all message information file then you do it");
			writerTextField.requestFocusInWindow();
			return;
		}
		
		String messageInfoSourcePathString = BuildSystemPathSupporter.getMessageInfoPathString(mainProjectName,
				sinnoriInstalledPathString);
		

		File messageInfoSourcePath = new File(messageInfoSourcePathString);
		if (messageInfoSourcePath.exists()) {
			String errorMessage = String.format("source project[%s]'s message info path[%s] doesn't exist",
					mainProjectName, messageInfoSourcePathString);
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return;
		}
		if (messageInfoSourcePath.canRead()) {
			String errorMessage = String.format("source project[%s]'s message info path[%s] can't be read",
					mainProjectName, messageInfoSourcePathString);
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return;
		}

		boolean resultSavingFile = true;
		ArrayList<File> listOfTargetPathSavingIOFileSet = null;		

		if (isOtherMainProjectCheckBox.isSelected()) {
			if (otherMainProjectComboBox.getItemCount() <= 1) {
				showMessageDialog("other project list empty!");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return;
			}
			
			if (0 == otherMainProjectComboBox.getSelectedIndex()) {
				showMessageDialog("you need to select one of other project list");
				isOtherMainProjectCheckBox.requestFocusInWindow();
				return;
			}
			
			String targetProjectName = (String) otherMainProjectComboBox.getSelectedItem();
			String messageInfoTargetPathString = BuildSystemPathSupporter.getMessageInfoPathString(targetProjectName,
					sinnoriInstalledPathString);

			File targetMessageInfoXMLPath = new File(messageInfoTargetPathString);
			if (targetMessageInfoXMLPath.exists()) {
				String errorMessage = String.format("target project[%s]'s message info path[%s] doesn't exist",
						targetProjectName, messageInfoTargetPathString);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				return;
			}
			if (targetMessageInfoXMLPath.canWrite()) {
				String errorMessage = String.format("target project[%s]'s message info path[%s] can't be written",
						targetProjectName, messageInfoTargetPathString);
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				return;
			}
			
			listOfTargetPathSavingIOFileSet = getListOfWritablePathSavingIOFileSet(targetProjectName);
			if (0 == listOfTargetPathSavingIOFileSet.size()) {
				String errorMessage = String.format("the selected project[%s] hasn't any build paths",
						targetProjectName);
				showMessageDialog(errorMessage);				
				return;
			}	
			
			for (int i = 0; resultSavingFile && i < rowCount; i++) {
				BuildFunctionCellValueForProject buildFunctionCellValue = (BuildFunctionCellValueForProject) messageInfoTableModel
						.getValueAt(i, 4);

				MessageInfo sourceMessageInfo = buildFunctionCellValue.getMessageInfo();

				File sourceMessageInfoXMLFIle = sourceMessageInfo.getMessageInfoXMLFile();
				String sourceLastFileName = sourceMessageInfoXMLFIle.getName();
				File targetMessageInfoXMLFile = new File(new StringBuilder().append(messageInfoTargetPathString)
						.append(File.separator).append(sourceLastFileName).toString());

				/**
				 * 파일 중복시 의견을 물어 중복 허용시에만 메시지 복사와 입출력 셋 생성 진행한다. 만약 중복 불허시 해당
				 * 메시지는 건너 뛰어 진행한다.
				 */
				if (targetMessageInfoXMLFile.exists()) {
					String confirmDialogTitle = "message info xml file copy";
					String confirmDialogMessage = String.format(
							"The target project[%s]'s message information XML file[%s] exist.\nDo you want to overwrite it with the soruce proejct[%s]'s message information XML File?",
							targetProjectName, sourceMessageInfo.getMessageID(), mainProjectName);

					int answer = JOptionPane.showConfirmDialog(ownerFrame, confirmDialogMessage, confirmDialogTitle,
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

					if (answer != JOptionPane.OK_OPTION) {
						log.info(
								"The source project[{}]'s message information XML file[{}] was canceled overwriting on target project[{}]",
								mainProjectName, sourceMessageInfo.getMessageID(), targetProjectName);
						continue;
					}
				}

				try {
					copyTransferToFile(sourceMessageInfoXMLFIle, targetMessageInfoXMLFile);
				} catch (IOException e1) {
					String errorMessage = String.format("fail to copy souce file[%s] to target file[%s]",
							sourceMessageInfoXMLFIle.getAbsolutePath(), targetMessageInfoXMLFile.getAbsolutePath());
					log.warn(errorMessage, e1);
					showMessageDialog(errorMessage);
					return;
				}

				resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer,
						buildFunctionCellValue.isSelectedIO(), buildFunctionCellValue.isSelectedDirection(),
						sourceMessageInfo);
			}
		} else {
			listOfTargetPathSavingIOFileSet = getListOfWritablePathSavingIOFileSet(mainProjectName);
			if (0 == listOfTargetPathSavingIOFileSet.size()) {
				String errorMessage = String.format("the selected project[%s] hasn't any build paths",
						mainProjectName);
				showMessageDialog(errorMessage);
				return;
			}	
			
			for (int i = 0; resultSavingFile && i < rowCount; i++) {
				BuildFunctionCellValueForProject buildFunctionCellValue = (BuildFunctionCellValueForProject) messageInfoTableModel
						.getValueAt(i, 4);

				MessageInfo sourceMessageInfo = buildFunctionCellValue.getMessageInfo();
				resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer,
						buildFunctionCellValue.isSelectedIO(), buildFunctionCellValue.isSelectedDirection(),
						sourceMessageInfo);
			}
		}

		if (resultSavingFile)
			showMessageDialog("Sucessfully all io source file set was built");
	}

	private void fileNameSearchButtonActionPerformed(ActionEvent e) {
		rebuildMessageInfoTable(SEARCH_MODE.SEARCH);
	}

}
