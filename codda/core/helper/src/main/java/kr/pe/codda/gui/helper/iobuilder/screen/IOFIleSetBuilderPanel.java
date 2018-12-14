/*
 * Created by JFormDesigner on Fri Feb 17 23:46:11 KST 2017
 */

package kr.pe.codda.gui.helper.iobuilder.screen;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.builder.IOPartDynamicClassFileContentsBuilderManager;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.type.ReadWriteMode;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.FileLastModifiedComparator;
import kr.pe.codda.common.util.XMLFileFilter;
import kr.pe.codda.gui.helper.iobuilder.table.BuildFunctionCellEditor;
import kr.pe.codda.gui.helper.iobuilder.table.BuildFunctionCellRenderer;
import kr.pe.codda.gui.helper.iobuilder.table.BuildFunctionCellValue;
import kr.pe.codda.gui.helper.iobuilder.table.FileFunctionCellEditor;
import kr.pe.codda.gui.helper.iobuilder.table.FileFunctionCellRenderer;
import kr.pe.codda.gui.helper.iobuilder.table.FileFunctionCellValue;
import kr.pe.codda.gui.helper.iobuilder.table.MessageInfoTableModel;
import kr.pe.codda.gui.helper.lib.ScreenManagerIF;
import kr.pe.codda.gui.util.PathSwingAction;

import org.xml.sax.SAXException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class IOFIleSetBuilderPanel extends JPanel implements FileFunctionManagerIF, BuildFunctionManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(IOFIleSetBuilderPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	private JFileChooser messageInfoPathChooser = null;
	private JFileChooser firstPathSavingIOFileSetChooser = null;
	private JFileChooser secondPathSavingIOFileSetChooser = null;
	private JFileChooser thirdPathSavingIOFileSetChooser = null;

	/** MessageInfo Table Model start */
	private MessageInfoTableModel messageInfoTableModel = null;

	private String titles[] = { "message id", "recently modified date", "direction", "mesg info file function",
			"io source build function" };

	private Class<?>[] columnTypes = new Class[] { String.class, String.class, String.class,
			FileFunctionCellValue.class, BuildFunctionCellValue.class };
	/** MessageInfo Table Model end */

	private IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager
			.getInstance();

	private enum SEARCH_MODE {
		ALL, SEARCH
	};

	
	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		messageInfoPathChooser = new JFileChooser();
		messageInfoPathChooser.setMultiSelectionEnabled(false);
		messageInfoPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		firstPathSavingIOFileSetChooser = new JFileChooser();
		firstPathSavingIOFileSetChooser.setMultiSelectionEnabled(false);
		firstPathSavingIOFileSetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		secondPathSavingIOFileSetChooser = new JFileChooser();
		secondPathSavingIOFileSetChooser.setMultiSelectionEnabled(false);
		secondPathSavingIOFileSetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		thirdPathSavingIOFileSetChooser = new JFileChooser();
		thirdPathSavingIOFileSetChooser.setMultiSelectionEnabled(false);
		thirdPathSavingIOFileSetChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		messageInfoPathButton
				.setAction(new PathSwingAction(mainFrame, messageInfoPathChooser, messageInfoPathButton.getText(), messageInfoPathTextField));
		firstPathSavingIOFileSetButton.setAction(
				new PathSwingAction(mainFrame, firstPathSavingIOFileSetChooser, firstPathSavingIOFileSetButton.getText(), firstPathSavingIOFileSetTextField));
		secondPathSavingIOFileSetButton.setAction(
				new PathSwingAction(mainFrame, secondPathSavingIOFileSetChooser, secondPathSavingIOFileSetButton.getText(), secondPathSavingIOFileSetTextField));
		thirdPathSavingIOFileSetButton.setAction(
				new PathSwingAction(mainFrame, thirdPathSavingIOFileSetChooser, thirdPathSavingIOFileSetButton.getText(), thirdPathSavingIOFileSetTextField));

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

	public IOFIleSetBuilderPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;

		initComponents();

		postInitComponents();
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame,
				CommonStaticUtil.splitString(message, LineSeparatorType.NEWLINE, 100));
	}
	
	/**
	 * 
	 * @param sourcePathTextField the parameter sourcePathTextField is TextField component whose value is path
	 * @param readWriteMode read/write mode
	 * @return the valid path
	 * @throws RuntimeException if the file is not a valid path. then throw it
	 */
	private File getValidPathFromTextField(JTextField sourcePathTextField, ReadWriteMode readWriteMode) throws RuntimeException {
		String sourcePathString = sourcePathTextField.getText();
		if (null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is null",
					sourcePathTextField.getName());
			throw new RuntimeException(errorMessage);
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);

		File sourcePath = null;
		try {
			sourcePath =CommonStaticUtil.getValidPath(sourcePathString, readWriteMode);
		} catch(RuntimeException e) {
			String errorMessage = e.toString();
			throw new RuntimeException(String.format("parameter sourcePathTextField[%s]'s value is not a valid path::%s", sourcePathTextField.getName(), errorMessage));
		}

		return sourcePath;
	}

	class MessageInfoListSwingWorker extends SwingWorker<ArrayList<MessageInfo>, Void> {

		private SEARCH_MODE searchMode;

		public MessageInfoListSwingWorker(SEARCH_MODE searchMode) {
			this.searchMode = searchMode;
		}

		@Override
		public ArrayList<MessageInfo> doInBackground() throws RuntimeException {
			ArrayList<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();
			

			File messageInfoPath = getValidPathFromTextField(messageInfoPathTextField,  ReadWriteMode.ONLY_READ);
			
			File messageInfoXMLFiles[] = messageInfoPath.listFiles(new XMLFileFilter());
			
			if (null == messageInfoXMLFiles) {
				throw new RuntimeException("the var messageInfoXMLFiles is null");
			}
			
			if (0 == messageInfoXMLFiles.length) {
				String errorMessage = String.format("there is no XML file in the message information path[%s]", 
						messageInfoPath.getAbsolutePath());
				throw new RuntimeException(errorMessage);
			}
			

			Arrays.sort(messageInfoXMLFiles, new FileLastModifiedComparator());

			String fileNameSearchKeyword = "";

			if (SEARCH_MODE.SEARCH == searchMode) {
				fileNameSearchKeyword = fileNameSearchTextField.getText().trim();
				fileNameSearchTextField.setText(fileNameSearchKeyword);

				if (0 == fileNameSearchKeyword.length()) {
					String errorMessage = "Please insert search keyword again";
					throw new RuntimeException(errorMessage);
				}

			}
			
			ProgressMonitor progressMonitor = new ProgressMonitor(mainFrame,
					"Running a Task reading message info files", "0% process", 0, messageInfoXMLFiles.length);

			for (int i = 0; i < messageInfoXMLFiles.length; i++) {
				File messageInfoFile = messageInfoXMLFiles[i];

				if (!messageInfoFile.isFile()) {
					String errorMessage = String.format("warning :: not file , file name=[%s]",
							messageInfoFile.getName());
					log.info(errorMessage);
					continue;
				}

				if (!messageInfoFile.canRead()) {
					String errorMessage = String.format("warning :: can't read, file name=[%s]",
							messageInfoFile.getName());
					log.warn(errorMessage);
					continue;
				}

				MessageInfoSAXParser messageInfoSAXParser = null;
				try {
					messageInfoSAXParser = new MessageInfoSAXParser();
				} catch (SAXException e) {
					String errorMessage = e.toString();
					log.warn(errorMessage, e);
					continue;
				}
				MessageInfo messageInfo = null;
				try {
					messageInfo = messageInfoSAXParser.parse(messageInfoFile, true);
				} catch (IllegalArgumentException | SAXException | IOException e) {
					String errorMessage = e.toString();
					log.warn(errorMessage, e);
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

				progressMonitor.setNote((i+1) + "/" + messageInfoXMLFiles.length+" process");
				progressMonitor.setProgress(i+1);
				
				// log.info("progressMonitor [{}/{}] process", i+1, messageInfoXMLFiles.length);				
			}
			
			// progressMonitor.close();

			return messageInfoList;
		}
	}

	private void createEmptyTable() {
		createTable(new Object[0][titles.length]);
	}

	private void createTable(Object values[][]) {
		messageInfoTableModel = new MessageInfoTableModel(values, titles, columnTypes);

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

		messageInfoTable.getColumnModel().getColumn(3).setCellRenderer(new FileFunctionCellRenderer());
		messageInfoTable.getColumnModel().getColumn(3).setCellEditor(new FileFunctionCellEditor(new JCheckBox()));

		messageInfoTable.getColumnModel().getColumn(4).setCellRenderer(new BuildFunctionCellRenderer());
		messageInfoTable.getColumnModel().getColumn(4).setCellEditor(new BuildFunctionCellEditor(new JCheckBox()));

		// messageInfoTable.repaint();

		/**
		 * 한줄 이상일 경우 첫번째 줄의 각 칼럼들에 쓰인 렌더러의 높이와 줄 높이중 가장 큰수를 줄의 높이로 정한다.
		 */
		if (messageInfoTable.getRowCount() > 0) {
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

	

	private boolean saveIOFileSetToTargetPath(ArrayList<File> listOfTargetPathSavingIOFileSet, String author,
			boolean isSelectedIOCore, boolean isSelectedDirection, MessageInfo messageInfo) {

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

			if (isSelectedIOCore) {
				File messageFile = new File(messageTargetPath.getAbsolutePath() + File.separator + messageID + ".java");
				File messageEncoderFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "Encoder.java");
				File messageDecoderFile = new File(
						messageTargetPath.getAbsolutePath() + File.separator + messageID + "Decoder.java");

				String fileNickname = null;
				fileNickname = "the message class";
				try {
					CommonStaticUtil.saveFile(messageFile, 
							ioFileSetContentsBuilderManager.getMessageSourceFileContents(author, messageInfo),
							CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				fileNickname = "the message encoder class";
				try {
					CommonStaticUtil.saveFile(messageEncoderFile, ioFileSetContentsBuilderManager
							.getEncoderSourceFileContents(author, messageInfo), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageEncoderFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				fileNickname = "the message decoder class";
				try {
					CommonStaticUtil.saveFile(messageDecoderFile, ioFileSetContentsBuilderManager
							.getDecoderSourceFileContents(author, messageInfo), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
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

				String fileNickname = null;
				fileNickname = "the server codec class";
				try {
					CommonStaticUtil.saveFile(messageServerCodecFile, ioFileSetContentsBuilderManager
							.getServerCodecSourceFileContents(messageInfo.getDirection(), messageID, author), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageServerCodecFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}

				fileNickname = "the client codec class";
				try {
					CommonStaticUtil.saveFile(messageClientCodecFile, ioFileSetContentsBuilderManager
							.getClientCodecSourceFileContents(messageInfo.getDirection(), messageID, author), CommonStaticFinalVars.SOURCE_FILE_CHARSET);
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", fileNickname,
							messageClientCodecFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
			}
		}

		return true;
	}

	private ArrayList<File> getListOfPathSavingIOFileSet() {
		ArrayList<File> listOfPathSavingIOFileSet = new ArrayList<File>();

		if (firstPathSavingIOFileSetCheckBox.isSelected()) {
			File firstPathSavingIOFileSet = null;
			try {
				firstPathSavingIOFileSet = getValidPathFromTextField(firstPathSavingIOFileSetTextField, ReadWriteMode.READ_WRITE);
			} catch(RuntimeException e) {
				String errorMessage  = e.toString();
				log.warn(errorMessage, e);
				showMessageDialog(errorMessage);
				firstPathSavingIOFileSetTextField.requestFocusInWindow();
				return null;
			}
			
			listOfPathSavingIOFileSet.add(firstPathSavingIOFileSet);
		}

		if (secondPathSavingIOFileSetCheckBox.isSelected()) {
			File secondPathSavingIOFileSet = null;
			try {
				secondPathSavingIOFileSet = getValidPathFromTextField(secondPathSavingIOFileSetTextField, ReadWriteMode.READ_WRITE);
			} catch(RuntimeException e) {
				String errorMessage  = e.toString();
				log.warn(errorMessage, e);				
				showMessageDialog(errorMessage);
				secondPathSavingIOFileSetTextField.requestFocusInWindow();
				return null;
			}
			listOfPathSavingIOFileSet.add(secondPathSavingIOFileSet);
		}

		if (thirdPathSavingIOFileSetCheckBox.isSelected()) {
			File thirdPathSavingIOFileSet = null;
			try {
				thirdPathSavingIOFileSet = getValidPathFromTextField(thirdPathSavingIOFileSetTextField,  ReadWriteMode.READ_WRITE);
			} catch(RuntimeException e) {
				String errorMessage  = e.toString();
				log.warn(errorMessage, e);				
				showMessageDialog(errorMessage);
				thirdPathSavingIOFileSetTextField.requestFocusInWindow();
				return null;
			}
			listOfPathSavingIOFileSet.add(thirdPathSavingIOFileSet);
		}

		if (0 == listOfPathSavingIOFileSet.size()) {
			String errorMessage  = "Please select one more path";
			log.info(errorMessage);	
			showMessageDialog(errorMessage);
			firstPathSavingIOFileSetTextField.requestFocusInWindow();
			return null;
		}

		return listOfPathSavingIOFileSet;
	}
	
	public ArrayList<MessageInfo> getMessageInfoList(SEARCH_MODE searchMode) throws RuntimeException {
		ArrayList<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();
		

		File messageInfoPath = getValidPathFromTextField(messageInfoPathTextField,  ReadWriteMode.ONLY_READ);
		
		File messageInfoXMLFiles[] = messageInfoPath.listFiles(new XMLFileFilter());
		
		if (null == messageInfoXMLFiles) {
			throw new RuntimeException("the var messageInfoXMLFiles is null");
		}
		
		if (0 == messageInfoXMLFiles.length) {
			String errorMessage = String.format("there is no XML file in the message information path[%s]", 
					messageInfoPath.getAbsolutePath());
			throw new RuntimeException(errorMessage);
		}
		

		Arrays.sort(messageInfoXMLFiles, new FileLastModifiedComparator());

		String fileNameSearchKeyword = "";

		if (SEARCH_MODE.SEARCH == searchMode) {
			fileNameSearchKeyword = fileNameSearchTextField.getText().trim();
			fileNameSearchTextField.setText(fileNameSearchKeyword);

			if (0 == fileNameSearchKeyword.length()) {
				String errorMessage = "Please insert search keyword again";
				throw new RuntimeException(errorMessage);
			}

		}
		
		
		for (int i = 0; i < messageInfoXMLFiles.length; i++) {
			File messageInfoFile = messageInfoXMLFiles[i];

			if (!messageInfoFile.isFile()) {
				String errorMessage = String.format("warning :: not file , file name=[%s]",
						messageInfoFile.getName());
				log.info(errorMessage);
				continue;
			}

			if (!messageInfoFile.canRead()) {
				String errorMessage = String.format("warning :: can't read, file name=[%s]",
						messageInfoFile.getName());
				log.warn(errorMessage);
				continue;
			}

			MessageInfoSAXParser messageInfoSAXParser = null;
			try {
				messageInfoSAXParser = new MessageInfoSAXParser();
			} catch (SAXException e) {
				String errorMessage = e.toString();
				log.warn(errorMessage, e);
				continue;
			}
			MessageInfo messageInfo = null;
			try {
				messageInfo = messageInfoSAXParser.parse(messageInfoFile, true);
			} catch (IllegalArgumentException | SAXException | IOException e) {
				String errorMessage = e.toString();
				log.warn(errorMessage, e);
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
			// log.info("progressMonitor [{}/{}] process", i+1, messageInfoXMLFiles.length);				
		}
		

		return messageInfoList;
	}

	private void rebuildMessageInfoTable(SEARCH_MODE searchMode) {
		Toolkit.getDefaultToolkit().beep();
		Cursor oldCursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		/*MessageInfoListSwingWorker allMessageInfoTask = new MessageInfoListSwingWorker(searchMode);
		// task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
		
		// FIXME!
		log.info("MessageInfoListSwingWorker#execute done");
		
		
		ArrayList<MessageInfo> messageInfoList = null;
		try {
			messageInfoList = allMessageInfoTask.get();
			
			// FIXME!
			log.info("MessageInfoListSwingWorker#get done");

		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
			showMessageDialog(e.toString());
			
			createEmptyTable();
			messageInfoPathTextField.requestFocusInWindow();
			return;
		} catch (ExecutionException e) {
			log.warn("ExecutionException", e);
			showMessageDialog(e.toString());
			
			createEmptyTable();
			messageInfoPathTextField.requestFocusInWindow();
			return;
		} finally {
			setCursor(oldCursor);
		}*/
		
		ArrayList<MessageInfo> messageInfoList = null;
		
		try {
			messageInfoList = getMessageInfoList(searchMode);
		} catch(Exception e) {
			log.warn("unknow error", e);
			showMessageDialog("errmsg="+e.toString());
			
			createEmptyTable();
			messageInfoPathTextField.requestFocusInWindow();
			return;
		}
		
		setCursor(oldCursor);
		

		Object values[][] = new Object[messageInfoList.size()][titles.length];

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		

		for (int i = 0; i < values.length; i++) {
			// FIXME!
			log.info("loop start[{}]", i);
			
			MessageInfo messageInfo = messageInfoList.get(i);

			String messageID = messageInfo.getMessageID();
			values[i][0] = messageID;
			values[i][1] = sdf.format(messageInfo.getLastModified());
			if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_ALL_TO_ALL) {
				values[i][2] = "client <-> server";
			} else if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_CLIENT_TO_SERVER) {
				values[i][2] = "client -> server";
			} else if (messageInfo.getDirection() == MessageTransferDirectionType.FROM_SERVER_TO_CLINET) {
				values[i][2] = "server -> client";
			} else {
				values[i][2] = "no direction";
			}

			values[i][3] = new FileFunctionCellValue(i, messageInfo, IOFIleSetBuilderPanel.this, mainFrame);
			values[i][4] = new BuildFunctionCellValue(messageInfo, IOFIleSetBuilderPanel.this, mainFrame);
		}
		
		// FIXME!
		log.info("loop done");

		createTable(values);
	}

	@Override
	public boolean saveIOFileSetOfSelectedMessageInfo(boolean isSelectedIOCore, boolean isSelectedDirection,
			MessageInfo messageInfo) {
		ArrayList<File> listOfTargetPathSavingIOFileSet = getListOfPathSavingIOFileSet();
		if (null == listOfTargetPathSavingIOFileSet) {
			return false;
		}

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

		boolean resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer, isSelectedIOCore,
				isSelectedDirection, messageInfo);

		return resultSavingFile;
	}

	@Override
	public void updateRowOfMessageInfoTableAccordingToNewMessageInfoUpdate(int row, MessageInfo newMessageInfo) {
		String directionStr = null;
		if (newMessageInfo.getDirection() == MessageTransferDirectionType.FROM_ALL_TO_ALL) {
			directionStr = "client <-> server";
		} else if (newMessageInfo.getDirection() == MessageTransferDirectionType.FROM_CLIENT_TO_SERVER) {
			directionStr = "client -> server";
		} else if (newMessageInfo.getDirection() == MessageTransferDirectionType.FROM_SERVER_TO_CLINET) {
			directionStr = "server -> client";
		} else {
			directionStr = "no direction";
		}
		messageInfoTableModel.setValueAt(directionStr, row, 2);

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		messageInfoTableModel.setValueAt(sdf.format(newMessageInfo.getLastModified()), row, 1);

		BuildFunctionCellValue sourceFileCellValue = (BuildFunctionCellValue) messageInfoTableModel.getValueAt(row, 4);
		sourceFileCellValue.setMessageInfo(newMessageInfo);
		messageInfoScrollPane.repaint();

		showMessageDialog(new StringBuilder("Successful update of table row with new message information[")
				.append(newMessageInfo.getMessageID()).append("]").toString());

	}	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		ioFileSetBuildPanel = new JPanel();
		screenMovePanel = new JPanel();
		firstScreenButton = new JButton();
		eachIOFileTypeBuilderScreenButton = new JButton();
		messageInfoFilePathPanel = new JPanel();
		messageInfoPathLabel = new JLabel();
		messageInfoPathTextField = new JTextField();
		messageInfoPathButton = new JButton();
		firstPathSavingIOFileSetPanel = new JPanel();
		firstPathSavingIOFileSetTitleLabel = new JLabel();
		firstPathSavingIOFileSetCheckBox = new JCheckBox();
		firstPathSavingIOFileSetTextField = new JTextField();
		firstPathSavingIOFileSetButton = new JButton();
		secondPathSavingIOFileSetPanel = new JPanel();
		secondPathSavingIOFileSetLabel = new JLabel();
		secondPathSavingIOFileSetCheckBox = new JCheckBox();
		secondPathSavingIOFileSetTextField = new JTextField();
		secondPathSavingIOFileSetButton = new JButton();
		thirdIPathSavingIOFileSetPanel = new JPanel();
		thirdPathSavingIOFileSetLabel = new JLabel();
		thirdPathSavingIOFileSetCheckBox = new JCheckBox();
		thirdPathSavingIOFileSetTextField = new JTextField();
		thirdPathSavingIOFileSetButton = new JButton();
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

		//======== this ========
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		//======== ioFileSetBuildPanel ========
		{
			ioFileSetBuildPanel.setLayout(new FormLayout(
				"$ugap, ${growing-button}, $ugap",
				"9*($lgap, default), $lgap, fill:[70dlu,min,150dlu]:grow, $ugap"));

			//======== screenMovePanel ========
			{
				screenMovePanel.setLayout(new FormLayout(
					"default, $ugap, default",
					"default"));

				//---- firstScreenButton ----
				firstScreenButton.setText("go back to 'first screen'");
				firstScreenButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						firstScreenButtonActionPerformed(e);
					}
				});
				screenMovePanel.add(firstScreenButton, CC.xy(1, 1));

				//---- eachIOFileTypeBuilderScreenButton ----
				eachIOFileTypeBuilderScreenButton.setText("go to 'each IO file type builder screen'");
				eachIOFileTypeBuilderScreenButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						eachIOFileTypeBuilderScreenButtonActionPerformed(e);
					}
				});
				screenMovePanel.add(eachIOFileTypeBuilderScreenButton, CC.xy(3, 1));
			}
			ioFileSetBuildPanel.add(screenMovePanel, CC.xy(2, 2));

			//======== messageInfoFilePathPanel ========
			{
				messageInfoFilePathPanel.setLayout(new FormLayout(
					"116dlu, $ugap, [236dlu,pref]:grow, $ugap, default",
					"default"));

				//---- messageInfoPathLabel ----
				messageInfoPathLabel.setText("Message information path");
				messageInfoFilePathPanel.add(messageInfoPathLabel, CC.xy(1, 1));
				messageInfoFilePathPanel.add(messageInfoPathTextField, CC.xy(3, 1));

				//---- messageInfoPathButton ----
				messageInfoPathButton.setText("Path");
				messageInfoFilePathPanel.add(messageInfoPathButton, CC.xy(5, 1));
			}
			ioFileSetBuildPanel.add(messageInfoFilePathPanel, CC.xy(2, 4));

			//======== firstPathSavingIOFileSetPanel ========
			{
				firstPathSavingIOFileSetPanel.setLayout(new FormLayout(
					"98dlu, $ugap, default, $ugap, ${growing-button}, $ugap, default",
					"default"));

				//---- firstPathSavingIOFileSetTitleLabel ----
				firstPathSavingIOFileSetTitleLabel.setText("First path saving IO file set");
				firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTitleLabel, CC.xy(1, 1));
				firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetCheckBox, CC.xy(3, 1));
				firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTextField, CC.xy(5, 1));

				//---- firstPathSavingIOFileSetButton ----
				firstPathSavingIOFileSetButton.setText("Path");
				firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetButton, CC.xy(7, 1));
			}
			ioFileSetBuildPanel.add(firstPathSavingIOFileSetPanel, CC.xy(2, 6));

			//======== secondPathSavingIOFileSetPanel ========
			{
				secondPathSavingIOFileSetPanel.setLayout(new FormLayout(
					"2*(default, $ugap), ${growing-button}, $ugap, default",
					"default"));

				//---- secondPathSavingIOFileSetLabel ----
				secondPathSavingIOFileSetLabel.setText("Secound path saving IO file set");
				secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetLabel, CC.xy(1, 1));
				secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetCheckBox, CC.xy(3, 1));
				secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetTextField, CC.xy(5, 1));

				//---- secondPathSavingIOFileSetButton ----
				secondPathSavingIOFileSetButton.setText("Path");
				secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetButton, CC.xy(7, 1));
			}
			ioFileSetBuildPanel.add(secondPathSavingIOFileSetPanel, CC.xy(2, 8));

			//======== thirdIPathSavingIOFileSetPanel ========
			{
				thirdIPathSavingIOFileSetPanel.setLayout(new FormLayout(
					"98dlu, $ugap, default, $ugap, ${growing-button}, $ugap, default",
					"default"));

				//---- thirdPathSavingIOFileSetLabel ----
				thirdPathSavingIOFileSetLabel.setText("Third path saving IO file set");
				thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetLabel, CC.xy(1, 1));
				thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetCheckBox, CC.xy(3, 1));
				thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetTextField, CC.xy(5, 1));

				//---- thirdPathSavingIOFileSetButton ----
				thirdPathSavingIOFileSetButton.setText("Path");
				thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetButton, CC.xy(7, 1));
			}
			ioFileSetBuildPanel.add(thirdIPathSavingIOFileSetPanel, CC.xy(2, 10));

			//======== writerPanel ========
			{
				writerPanel.setLayout(new FormLayout(
					"116dlu, $ugap, ${growing-button}",
					"default"));

				//---- writerLabel ----
				writerLabel.setText("Writer");
				writerPanel.add(writerLabel, CC.xy(1, 1));
				writerPanel.add(writerTextField, CC.xy(3, 1));
			}
			ioFileSetBuildPanel.add(writerPanel, CC.xy(2, 12));

			//======== menuPanel ========
			{
				menuPanel.setLayout(new FormLayout(
					"default, $ugap, default",
					"default"));

				//---- allMessageInfoCreationButton ----
				allMessageInfoCreationButton.setText("Reread all message infomation file");
				allMessageInfoCreationButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						allMessageInfoCreationButtonActionPerformed(e);
					}
				});
				menuPanel.add(allMessageInfoCreationButton, CC.xy(1, 1));

				//---- allMessageIOFileSetCreationButton ----
				allMessageIOFileSetCreationButton.setText("Build All IO source file set");
				allMessageIOFileSetCreationButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						allMessageIOFileSetCreationButtonActionPerformed(e);
					}
				});
				menuPanel.add(allMessageIOFileSetCreationButton, CC.xy(3, 1));
			}
			ioFileSetBuildPanel.add(menuPanel, CC.xy(2, 14));

			//---- resultLabel ----
			resultLabel.setText(">> result rereading all message infomation file");
			ioFileSetBuildPanel.add(resultLabel, CC.xy(2, 16));

			//======== messageIDSearchPanel ========
			{
				messageIDSearchPanel.setLayout(new FormLayout(
					"default, $ugap, ${growing-button}, $ugap, default",
					"default"));

				//---- messageIDSearchLabel ----
				messageIDSearchLabel.setText("message ID");
				messageIDSearchPanel.add(messageIDSearchLabel, CC.xy(1, 1));
				messageIDSearchPanel.add(fileNameSearchTextField, CC.xy(3, 1));

				//---- fileNameSearchButton ----
				fileNameSearchButton.setText("Search");
				fileNameSearchButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fileNameSearchButtonActionPerformed(e);
					}
				});
				messageIDSearchPanel.add(fileNameSearchButton, CC.xy(5, 1));
			}
			ioFileSetBuildPanel.add(messageIDSearchPanel, CC.xy(2, 18));

			//======== messageInfoScrollPane ========
			{

				//---- messageInfoTable ----
				messageInfoTable.setModel(new DefaultTableModel(
					new Object[][] {
						{null, null, "", null, null},
						{null, null, null, null, null},
					},
					new String[] {
						"message id", "recently modified date", "direction", "mesg info file function", "io source build function"
					}
				) {
					Class<?>[] columnTypes = new Class<?>[] {
						String.class, String.class, String.class, Object.class, Object.class
					};
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				messageInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				messageInfoTable.setFillsViewportHeight(true);
				messageInfoScrollPane.setViewportView(messageInfoTable);
			}
			ioFileSetBuildPanel.add(messageInfoScrollPane, CC.xy(2, 20, CC.FILL, CC.DEFAULT));
		}
		add(ioFileSetBuildPanel);
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel ioFileSetBuildPanel;
	private JPanel screenMovePanel;
	private JButton firstScreenButton;
	private JButton eachIOFileTypeBuilderScreenButton;
	private JPanel messageInfoFilePathPanel;
	private JLabel messageInfoPathLabel;
	private JTextField messageInfoPathTextField;
	private JButton messageInfoPathButton;
	private JPanel firstPathSavingIOFileSetPanel;
	private JLabel firstPathSavingIOFileSetTitleLabel;
	private JCheckBox firstPathSavingIOFileSetCheckBox;
	private JTextField firstPathSavingIOFileSetTextField;
	private JButton firstPathSavingIOFileSetButton;
	private JPanel secondPathSavingIOFileSetPanel;
	private JLabel secondPathSavingIOFileSetLabel;
	private JCheckBox secondPathSavingIOFileSetCheckBox;
	private JTextField secondPathSavingIOFileSetTextField;
	private JButton secondPathSavingIOFileSetButton;
	private JPanel thirdIPathSavingIOFileSetPanel;
	private JLabel thirdPathSavingIOFileSetLabel;
	private JCheckBox thirdPathSavingIOFileSetCheckBox;
	private JTextField thirdPathSavingIOFileSetTextField;
	private JButton thirdPathSavingIOFileSetButton;
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

	

	private void eachIOFileTypeBuilderScreenButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToEachIOFileTypeBuilderScreen();
	}
	
	private void firstScreenButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}

	private void allMessageInfoCreationButtonActionPerformed(ActionEvent e) {
		rebuildMessageInfoTable(SEARCH_MODE.ALL);
	}

	private void fileNameSearchButtonActionPerformed(ActionEvent e) {
		rebuildMessageInfoTable(SEARCH_MODE.SEARCH);
	}

	private void allMessageIOFileSetCreationButtonActionPerformed(ActionEvent e) {
		ArrayList<File> listOfTargetPathSavingIOFileSet = getListOfPathSavingIOFileSet();
		if (null == listOfTargetPathSavingIOFileSet) {
			return;
		}

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

		boolean resultSavingFile = true;

		for (int i = 0; resultSavingFile && i < rowCount; i++) {
			BuildFunctionCellValue buildFunctionCellValue = (BuildFunctionCellValue) messageInfoTableModel.getValueAt(i,
					4);

			resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer,
					buildFunctionCellValue.isSelectedIO(), buildFunctionCellValue.isSelectedDirection(),
					buildFunctionCellValue.getMessageInfo());
		}

		if (resultSavingFile) {
			showMessageDialog("Sucessfully all io source file set was built");
		}
	}
}
