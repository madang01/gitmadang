/*
 * Created by JFormDesigner on Sat Feb 04 13:29:06 KST 2017
 */

package kr.pe.sinnori.gui.helper.iobuilder.screen;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.builder.IOFileSetContentsBuilderManager;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.FileLastModifiedComparator;
import kr.pe.sinnori.common.util.XMLFileFilter;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;
import kr.pe.sinnori.gui.helper.iobuilder.table.BuildFunctionCellEditor;
import kr.pe.sinnori.gui.helper.iobuilder.table.BuildFunctionCellRenderer;
import kr.pe.sinnori.gui.helper.iobuilder.table.BuildFunctionCellValue;
import kr.pe.sinnori.gui.helper.iobuilder.table.FileFunctionCellEditor;
import kr.pe.sinnori.gui.helper.iobuilder.table.FileFunctionCellRenderer;
import kr.pe.sinnori.gui.helper.iobuilder.table.FileFunctionCellValue;
import kr.pe.sinnori.gui.helper.iobuilder.table.MessageInfoTableModel;
import kr.pe.sinnori.gui.util.PathSwingAction;


/**
 * @author Won Jonghoobn
 */
@SuppressWarnings("serial")
public class IOBuilderPanel extends JPanel implements FileFunctionManagerIF, BuildFunctionManagerIF {
	private Logger log = LoggerFactory.getLogger(IOBuilderPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	private JFileChooser pathChooser = null;
	
	/** MessageInfo Table Model start */
	private MessageInfoTableModel messageInfoTableModel = null;
	
	private String titles[] = {
			"message id", "recently modified date", "direction", "mesg info file function", "io source build function"
		};	
	
	private Class<?>[] columnTypes = new Class[] {
		String.class, String.class, String.class, FileFunctionCellValue.class, BuildFunctionCellValue.class
	};	
	/** MessageInfo Table Model end */
	
	private IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();
	
	private enum SEARCH_MODE {
		ALL, SEARCH
	};
		

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message,
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}
	
	/**
	 * @param sourcePathTextField TextField whose value is path   
	 * @param sourcePathTextFieldName parameter sourcePathTextField's name
	 * @return the writable and readable path, but if parameter sourceTextField's value is not a useful path then return null.
	 */
	private File getWitableAndReadablePathFromTextField(JTextField sourcePathTextField, String sourcePathTextFieldName) {
		String sourcePathString = sourcePathTextField.getText();
		if ( null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is nul", sourcePathTextFieldName);
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);
		
		if (sourcePathString.equals("")) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is empty", sourcePathTextFieldName);
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s][%s] doesn't exist", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s][%s] is not a directory", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.canRead()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to read", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.canWrite()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to write", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		
		return sourcePath;
	}
	
	class MessageInfoListSwingWorker extends SwingWorker<ArrayList<MessageInfo>, Void> {
		
		private SEARCH_MODE searchMode;
		
		public MessageInfoListSwingWorker(SEARCH_MODE searchMode) {
			this.searchMode = searchMode;
		}
		
		
        @Override
        public ArrayList<MessageInfo> doInBackground() {
        	ArrayList<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();
        	ProgressMonitor progressMonitor = new ProgressMonitor(mainFrame, "Running a Task reading message info files", "", 0, 100);
        	
        	File messageInfoPath = getWitableAndReadablePathFromTextField(messageInfoPathTextField, "the path to the message information files");
    		if (null == messageInfoPath) {
    			return messageInfoList;
    		}
    		File messageInfoXMLFiles[] = messageInfoPath.listFiles(new XMLFileFilter());
    		
    		
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
    		
    		for (int i=0; i < messageInfoXMLFiles.length; i++) {
    			File messageInfoFile = messageInfoXMLFiles[i];
    			
    			if (!messageInfoFile.isFile()) {
    				String errorMessage = String.format("warning :: not file , file name=[%s]", messageInfoFile.getName());
    				log.warn(errorMessage);
    				showMessageDialog(errorMessage);
    				continue;
    			}

    			if (!messageInfoFile.canRead()) {
    				String errorMessage = String.format("warning :: can't read, file name=[%s]", messageInfoFile.getName());
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
    			if (progressMonitor.isCanceled())  {
    				return messageInfoList;
    			}
    			
    			progressMonitor.setNote(i+"/"+messageInfoXMLFiles.length);
    			progressMonitor.setProgress(i*100/messageInfoXMLFiles.length);
    		}
    		
    		
            
            return messageInfoList;
        }
	}

	private void createEmptyTable() {
    	createTable(new Object[0][titles.length]);
    }
	
    private void createTable(Object values[][]) {
    	messageInfoTableModel = new MessageInfoTableModel(values, titles, columnTypes);
        
        /** 모델  교체중 repaint event 를 막기 위해서 잠시 visable 속성을 끔 */
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
            for (int column = 0; column < messageInfoTable.getColumnCount(); column++)
            {
                Component comp = messageInfoTable.prepareRenderer(messageInfoTable.getCellRenderer(0, column), 0, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            messageInfoTable.setRowHeight(rowHeight);
    	}		
    	

    	/** 모델  교체중 repaint event 를 막기 위해서 잠시 visible 속성 복귀 */
    	messageInfoTable.setEnabled(true);
    	messageInfoTable.setVisible(true);
    	messageInfoScrollPane.repaint();
    }
    
   
    
    private void saveFile(String title, File targetFile, String contents) throws IOException {
    	if (null == title) throw new IllegalArgumentException("the parameter 'title' is null");
    	if (null == targetFile) throw new IllegalArgumentException("the parameter 'targetFile' is null");
    	if (null == contents) throw new IllegalArgumentException("the parameter 'contents' is null");
    	
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
				if (null != fos) fos.close();
			} catch (IOException e) {
				log.warn("fail to close the file[{}][{}] output stream", title, targetFile.getAbsolutePath());
			}
		}
    }
    
    private boolean saveIOFileSetToTargetPath(ArrayList<File> listOfTargetPathSavingIOFileSet, String author, 
			boolean isSelectedIO, boolean isSelectedDirection, MessageInfo messageInfo) {
		
		for (File targetPathSavingIOFileSet : listOfTargetPathSavingIOFileSet) {
			String messageID = messageInfo.getMessageID();
			String messageTargetPathString = targetPathSavingIOFileSet.getAbsolutePath()+File.separator+messageID;
			File messageTargetPath = new File(messageTargetPathString);
			if (! messageTargetPath.exists()) {
				boolean result = messageTargetPath.mkdir();
				if (!result) {
					String errorMessage = String.format("fail to create the message[%s] target path[%s]", messageID, messageTargetPathString);
					log.warn(errorMessage);
					showMessageDialog(errorMessage);
					continue;
				}
			}
			
			if (! messageTargetPath.canWrite()) {
				String errorMessage = String.format("the message[%s] target path[%s] can't be written", messageID, messageTargetPath.getAbsolutePath());
				log.warn(errorMessage);
				showMessageDialog(errorMessage);
				continue;
			}
			
			if (isSelectedIO) {
				File messageFile = new File(messageTargetPath.getAbsolutePath()+File.separator+messageID+".java");
				File messageEncoderFile = new File(messageTargetPath.getAbsolutePath()+File.separator+messageID+"Encoder.java");
				File messageDecoderFile = new File(messageTargetPath.getAbsolutePath()+File.separator+messageID+"Decoder.java");
				
				String title = null;
				title = "the message class";
				try {
					saveFile(title,  messageFile, ioFileSetContentsBuilderManager.getMessageSourceFileContents(messageID, author, messageInfo));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title, messageFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
				
				title = "the message encoder class";
				try {
					saveFile(title,  messageEncoderFile, ioFileSetContentsBuilderManager.getEncoderSourceFileContents(messageID, author, messageInfo));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title, messageEncoderFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
				
				title = "the message decoder class";
				try {
					saveFile(title,  messageDecoderFile, ioFileSetContentsBuilderManager.getDecoderSourceFileContents(messageID, author, messageInfo));				
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title, messageDecoderFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
			}
			
			if (isSelectedDirection) {
				File messageServerCodecFile = new File(messageTargetPath.getAbsolutePath()+File.separator+messageID+"ServerCodec.java");
				File messageClientCodecFile = new File(messageTargetPath.getAbsolutePath()+File.separator+messageID+"ClientCodec.java");
				
				String title = null;
				title = "the server codec class";
				try {
					saveFile(title,  messageServerCodecFile, ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(messageInfo.getDirection(), messageID, author));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title, messageServerCodecFile.getAbsolutePath(), e.toString());
					log.warn(errorMessage, e);
					showMessageDialog(errorMessage);
					return false;
				}
				
				title = "the client codec class";
				try {
					saveFile(title,  messageClientCodecFile, ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(messageInfo.getDirection(), messageID, author));
				} catch (IOException e) {
					String errorMessage = String.format("fail to save file[%s][%s]::%s", title, messageClientCodecFile.getAbsolutePath(), e.toString());
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
			File firstPathSavingIOFileSet = getWitableAndReadablePathFromTextField(firstPathSavingIOFileSetTextField, "the first path saving io file set");
			if (null == firstPathSavingIOFileSet) {
				return null;
			}
			listOfPathSavingIOFileSet.add(firstPathSavingIOFileSet);
		}
		
		if (secondPathSavingIOFileSetCheckBox.isSelected()) {			
			File secondPathSavingIOFileSet = getWitableAndReadablePathFromTextField(secondPathSavingIOFileSetTextField, "the second path saving io file set");
			if (null == secondPathSavingIOFileSet) {
				return null;
			}
			listOfPathSavingIOFileSet.add(secondPathSavingIOFileSet);
		}
		
		if (thirdPathSavingIOFileSetCheckBox.isSelected()) {
			File thirdPathSavingIOFileSet = getWitableAndReadablePathFromTextField(thirdPathSavingIOFileSetTextField, "the third path saving io file set");
			if (null == thirdPathSavingIOFileSet) {
				return null;
			}
			listOfPathSavingIOFileSet.add(thirdPathSavingIOFileSet);
		}
				
		if (0 == listOfPathSavingIOFileSet.size()) {
			showMessageDialog( "Please select one more path");
			
			firstPathSavingIOFileSetTextField.requestFocusInWindow();
			return null;
		}
		
    	return listOfPathSavingIOFileSet;
    }
    
	private void rebuildMessageInfoTable(SEARCH_MODE searchMode) {
		Toolkit.getDefaultToolkit().beep();
		Cursor oldCursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		MessageInfoListSwingWorker allMessageInfoTask = new MessageInfoListSwingWorker(searchMode);
        // task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
		ArrayList<MessageInfo> messageInfoList  = null;
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
        
        for (int i=0; i < values.length; i++) {
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
			    			
			values[i][3] = new FileFunctionCellValue(i, messageInfo, IOBuilderPanel.this, mainFrame);
			values[i][4] = new BuildFunctionCellValue(messageInfo, IOBuilderPanel.this, mainFrame);
		}
        
		createTable(values);
		setCursor(oldCursor);
	}


    @Override
	public boolean saveIOFileSetOfSelectedMessageInfo(boolean isSelectedIO, boolean isSelectedDirection, MessageInfo messageInfo) {		
		ArrayList<File> listOfTargetPathSavingIOFileSet = getListOfPathSavingIOFileSet();	
		if (null == listOfTargetPathSavingIOFileSet) return false;
		
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
		
		boolean resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer, isSelectedIO, isSelectedDirection, messageInfo);
		
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
		
		BuildFunctionCellValue sourceFileCellValue = (BuildFunctionCellValue)messageInfoTableModel.getValueAt(row, 4);
		sourceFileCellValue.setMessageInfo(newMessageInfo);
		messageInfoScrollPane.repaint();
		
	}
	

	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		pathChooser = new JFileChooser();
		pathChooser.setMultiSelectionEnabled(true);
		pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		messageInfoPathTextField.setAction(new PathSwingAction(mainFrame, pathChooser, messageInfoPathTextField));
		firstPathSavingIOFileSetButton.setAction(new PathSwingAction(mainFrame, pathChooser, firstPathSavingIOFileSetTextField));
		secondPathSavingIOFileSetButton.setAction(new PathSwingAction(mainFrame, pathChooser, secondPathSavingIOFileSetTextField));
		thirdPathSavingIOFileSetButton.setAction(new PathSwingAction(mainFrame, pathChooser, thirdPathSavingIOFileSetTextField));
		
		// readAllMessageInfo();
		messageInfoTable.setRowSelectionAllowed(false);
		messageInfoTable.setFillsViewportHeight(true);
		// messageInfoTable.setPreferredScrollableViewportSize(new Dimension(800, 300));
		messageInfoTable.setAutoCreateRowSorter(true);
		// messageInfoTable.setRowHeight(38);
		// messageInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// messageInfoTable.setMaximumSize(new Dimension(700, 600));
		
	}
	
	public IOBuilderPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		IOBuilderPanel = new JPanel();
		topMenuPanel = new JPanel();
		prevButton = new JButton();
		sourceBuilderTabbedPane = new JTabbedPane();
		ioFileSetBuildPanel = new JPanel();
		messageInfoFilePathPanel = new JPanel();
		messageInfoFilePathLabel = new JLabel();
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
		eachIOFileTypeBuildPanel = new JPanel();
		ioFileTypBuildMenuPanel = new JPanel();
		messageRadioButton = new JRadioButton();
		encoderRadioButton = new JRadioButton();
		decoderRadioButton = new JRadioButton();
		serverCodecRadioButton = new JRadioButton();
		clientCodecRadioButton = new JRadioButton();
		eachIOFileTypeBuildButton = new JButton();
		messageInfoXMLInputTitleLabel = new JLabel();
		messageInfoXMLScrollPane = new JScrollPane();
		messageInfoXMLTextArea = new JTextArea();
		eachIOFileTypeResultLabel = new JLabel();
		eachIOFileTypeResultScrollPane = new JScrollPane();
		eachIOFileTypeResulTextArea = new JTextArea();

		//======== this ========
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		//======== IOBuilderPanel ========
		{
			IOBuilderPanel.setLayout(new FormLayout(
				"$ugap, 430dlu:grow, 7dlu",
				"$ugap, default, fill:320dlu:grow, $ugap"));
			/** Post-initialization Code start */
			postInitComponents();
			/** Post-initialization Code end */

			//======== topMenuPanel ========
			{
				topMenuPanel.setLayout(new FormLayout(
					"default",
					"$lgap, default, $lgap"));

				//---- prevButton ----
				prevButton.setText("go back to first screen");
				prevButton.addActionListener(e -> {
			prevButtonActionPerformed(e);
			prevButtonActionPerformed(e);
		});
				topMenuPanel.add(prevButton, CC.xy(1, 2));
			}
			IOBuilderPanel.add(topMenuPanel, CC.xy(2, 2));

			//======== sourceBuilderTabbedPane ========
			{
				sourceBuilderTabbedPane.setPreferredSize(new Dimension(740, 400));
				sourceBuilderTabbedPane.setMinimumSize(new Dimension(740, 400));

				//======== ioFileSetBuildPanel ========
				{
					ioFileSetBuildPanel.setLayout(new FormLayout(
						"$ugap, ${growing-button}, $ugap",
						"8*($lgap, default), $lgap, fill:70dlu:grow, $lgap"));

					//======== messageInfoFilePathPanel ========
					{
						messageInfoFilePathPanel.setLayout(new FormLayout(
							"116dlu, $ugap, [236dlu,pref]:grow, $ugap, default",
							"default"));

						//---- messageInfoFilePathLabel ----
						messageInfoFilePathLabel.setText("Message info file path");
						messageInfoFilePathPanel.add(messageInfoFilePathLabel, CC.xy(1, 1));
						messageInfoFilePathPanel.add(messageInfoPathTextField, CC.xy(3, 1));

						//---- messageInfoPathButton ----
						messageInfoPathButton.setText("Path");
						messageInfoPathButton.addActionListener(e -> messageInfoPathButtonActionPerformed(e));
						messageInfoFilePathPanel.add(messageInfoPathButton, CC.xy(5, 1));
					}
					ioFileSetBuildPanel.add(messageInfoFilePathPanel, CC.xy(2, 2));

					//======== firstPathSavingIOFileSetPanel ========
					{
						firstPathSavingIOFileSetPanel.setLayout(new FormLayout(
							"98dlu, $ugap, default, $ugap, ${growing-button}, $ugap, default",
							"default"));

						//---- firstPathSavingIOFileSetTitleLabel ----
						firstPathSavingIOFileSetTitleLabel.setText("First path saving IO file set");
						firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTitleLabel, CC.xy(1, 1));
						firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetCheckBox, CC.xy(3, 1));

						//---- firstPathSavingIOFileSetTextField ----
						firstPathSavingIOFileSetTextField.addActionListener(e -> firstPathSavingIOFileSetTextFieldActionPerformed(e));
						firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTextField, CC.xy(5, 1));

						//---- firstPathSavingIOFileSetButton ----
						firstPathSavingIOFileSetButton.setText("Path");
						firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetButton, CC.xy(7, 1));
					}
					ioFileSetBuildPanel.add(firstPathSavingIOFileSetPanel, CC.xy(2, 4));

					//======== secondPathSavingIOFileSetPanel ========
					{
						secondPathSavingIOFileSetPanel.setLayout(new FormLayout(
							"2*(default, $ugap), ${growing-button}, $ugap, default",
							"default"));

						//---- secondPathSavingIOFileSetLabel ----
						secondPathSavingIOFileSetLabel.setText("Secound path saving IO file set");
						secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetLabel, CC.xy(1, 1));
						secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetCheckBox, CC.xy(3, 1));

						//---- secondPathSavingIOFileSetTextField ----
						secondPathSavingIOFileSetTextField.addActionListener(e -> secondPathSavingIOFileSetTextFieldActionPerformed(e));
						secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetTextField, CC.xy(5, 1));

						//---- secondPathSavingIOFileSetButton ----
						secondPathSavingIOFileSetButton.setText("Path");
						secondPathSavingIOFileSetPanel.add(secondPathSavingIOFileSetButton, CC.xy(7, 1));
					}
					ioFileSetBuildPanel.add(secondPathSavingIOFileSetPanel, CC.xy(2, 6));

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
						thirdPathSavingIOFileSetButton.addActionListener(e -> thirdPathSavingIOFileSetButtonActionPerformed(e));
						thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetButton, CC.xy(7, 1));
					}
					ioFileSetBuildPanel.add(thirdIPathSavingIOFileSetPanel, CC.xy(2, 8));

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
					ioFileSetBuildPanel.add(writerPanel, CC.xy(2, 10));

					//======== menuPanel ========
					{
						menuPanel.setLayout(new FormLayout(
							"default, $ugap, default",
							"default"));

						//---- allMessageInfoCreationButton ----
						allMessageInfoCreationButton.setText("Reread all message infomation file");
						allMessageInfoCreationButton.addActionListener(e -> {
			allMessageInfoCreationButtonActionPerformed(e);
			allMessageInfoCreationButtonActionPerformed(e);
		});
						menuPanel.add(allMessageInfoCreationButton, CC.xy(1, 1));

						//---- allMessageIOFileSetCreationButton ----
						allMessageIOFileSetCreationButton.setText("Build All IO source file set");
						allMessageIOFileSetCreationButton.addActionListener(e -> allMessageIOFileSetCreationButtonActionPerformed(e));
						menuPanel.add(allMessageIOFileSetCreationButton, CC.xy(3, 1));
					}
					ioFileSetBuildPanel.add(menuPanel, CC.xy(2, 12));

					//---- resultLabel ----
					resultLabel.setText(">> result rereading all message infomation file");
					ioFileSetBuildPanel.add(resultLabel, CC.xy(2, 14));

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
						fileNameSearchButton.addActionListener(e -> fileNameSearchButtonActionPerformed(e));
						messageIDSearchPanel.add(fileNameSearchButton, CC.xy(5, 1));
					}
					ioFileSetBuildPanel.add(messageIDSearchPanel, CC.xy(2, 16));

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
						messageInfoTable.setFillsViewportHeight(true);
						messageInfoScrollPane.setViewportView(messageInfoTable);
					}
					ioFileSetBuildPanel.add(messageInfoScrollPane, CC.xy(2, 18));
				}
				sourceBuilderTabbedPane.addTab("source builder for IO file set", ioFileSetBuildPanel);

				//======== eachIOFileTypeBuildPanel ========
				{
					eachIOFileTypeBuildPanel.setLayout(new FormLayout(
						"$ugap, 308dlu:grow, $ugap",
						"2*($lgap, default), $lgap, fill:81dlu:grow, $lgap, default, $lgap, fill:81dlu"));

					//======== ioFileTypBuildMenuPanel ========
					{
						ioFileTypBuildMenuPanel.setLayout(new BoxLayout(ioFileTypBuildMenuPanel, BoxLayout.X_AXIS));

						//---- messageRadioButton ----
						messageRadioButton.setText("Message");
						ioFileTypBuildMenuPanel.add(messageRadioButton);

						//---- encoderRadioButton ----
						encoderRadioButton.setText("Encoder");
						ioFileTypBuildMenuPanel.add(encoderRadioButton);

						//---- decoderRadioButton ----
						decoderRadioButton.setText("Decoder");
						ioFileTypBuildMenuPanel.add(decoderRadioButton);

						//---- serverCodecRadioButton ----
						serverCodecRadioButton.setText("ServerCodec");
						ioFileTypBuildMenuPanel.add(serverCodecRadioButton);

						//---- clientCodecRadioButton ----
						clientCodecRadioButton.setText("ClientCodec");
						ioFileTypBuildMenuPanel.add(clientCodecRadioButton);

						//---- eachIOFileTypeBuildButton ----
						eachIOFileTypeBuildButton.setText("Build");
						eachIOFileTypeBuildButton.addActionListener(e -> eachIOFileTypeBuildButtonActionPerformed(e));
						ioFileTypBuildMenuPanel.add(eachIOFileTypeBuildButton);
					}
					eachIOFileTypeBuildPanel.add(ioFileTypBuildMenuPanel, CC.xy(2, 2));

					//---- messageInfoXMLInputTitleLabel ----
					messageInfoXMLInputTitleLabel.setText(">> Message Infomation XML Input");
					eachIOFileTypeBuildPanel.add(messageInfoXMLInputTitleLabel, CC.xy(2, 4));

					//======== messageInfoXMLScrollPane ========
					{
						messageInfoXMLScrollPane.setViewportView(messageInfoXMLTextArea);
					}
					eachIOFileTypeBuildPanel.add(messageInfoXMLScrollPane, CC.xy(2, 6));

					//---- eachIOFileTypeResultLabel ----
					eachIOFileTypeResultLabel.setText(">> Result");
					eachIOFileTypeBuildPanel.add(eachIOFileTypeResultLabel, CC.xy(2, 8));

					//======== eachIOFileTypeResultScrollPane ========
					{

						//---- eachIOFileTypeResulTextArea ----
						eachIOFileTypeResulTextArea.setEditable(false);
						eachIOFileTypeResultScrollPane.setViewportView(eachIOFileTypeResulTextArea);
					}
					eachIOFileTypeBuildPanel.add(eachIOFileTypeResultScrollPane, CC.xy(2, 10));
				}
				sourceBuilderTabbedPane.addTab("source builder for each IO file type", eachIOFileTypeBuildPanel);
			}
			IOBuilderPanel.add(sourceBuilderTabbedPane, CC.xy(2, 3, CC.FILL, CC.FILL));
		}
		add(IOBuilderPanel);

		//---- ioFileTypeButtonGroup ----
		ButtonGroup ioFileTypeButtonGroup = new ButtonGroup();
		ioFileTypeButtonGroup.add(messageRadioButton);
		ioFileTypeButtonGroup.add(encoderRadioButton);
		ioFileTypeButtonGroup.add(decoderRadioButton);
		ioFileTypeButtonGroup.add(serverCodecRadioButton);
		ioFileTypeButtonGroup.add(clientCodecRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel IOBuilderPanel;
	private JPanel topMenuPanel;
	private JButton prevButton;
	private JTabbedPane sourceBuilderTabbedPane;
	private JPanel ioFileSetBuildPanel;
	private JPanel messageInfoFilePathPanel;
	private JLabel messageInfoFilePathLabel;
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
	private JPanel eachIOFileTypeBuildPanel;
	private JPanel ioFileTypBuildMenuPanel;
	private JRadioButton messageRadioButton;
	private JRadioButton encoderRadioButton;
	private JRadioButton decoderRadioButton;
	private JRadioButton serverCodecRadioButton;
	private JRadioButton clientCodecRadioButton;
	private JButton eachIOFileTypeBuildButton;
	private JLabel messageInfoXMLInputTitleLabel;
	private JScrollPane messageInfoXMLScrollPane;
	private JTextArea messageInfoXMLTextArea;
	private JLabel eachIOFileTypeResultLabel;
	private JScrollPane eachIOFileTypeResultScrollPane;
	private JTextArea eachIOFileTypeResulTextArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables	


	
	private void messageInfoPathButtonActionPerformed(ActionEvent e) {
		File messageInfoPath = getWitableAndReadablePathFromTextField(messageInfoPathTextField, "the path to the message information files");
		if (null == messageInfoPath) {
			return;
		}
		
		pathChooser.setCurrentDirectory(messageInfoPath);
	}

	private void firstPathSavingIOFileSetTextFieldActionPerformed(ActionEvent e) {
		File firstPathSavingIOFileSet = getWitableAndReadablePathFromTextField(firstPathSavingIOFileSetTextField, "the first path saving io file set");
		if (null == firstPathSavingIOFileSet) {
			return;
		}
		
		pathChooser.setCurrentDirectory(firstPathSavingIOFileSet);
	}

	private void secondPathSavingIOFileSetTextFieldActionPerformed(ActionEvent e) {
		File secondPathSavingIOFileSet = getWitableAndReadablePathFromTextField(secondPathSavingIOFileSetTextField, "the second path saving io file set");
		if (null == secondPathSavingIOFileSet) {
			return;
		}
		
		pathChooser.setCurrentDirectory(secondPathSavingIOFileSet);
	}

	private void thirdPathSavingIOFileSetButtonActionPerformed(ActionEvent e) {
		File thirdPathSavingIOFileSet = getWitableAndReadablePathFromTextField(thirdPathSavingIOFileSetTextField, "the third path saving io file set");
		if (null == thirdPathSavingIOFileSet) {
			return;
		}
		
		pathChooser.setCurrentDirectory(thirdPathSavingIOFileSet);
	}
	
	private void prevButtonActionPerformed(ActionEvent e) {
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
		if (null == listOfTargetPathSavingIOFileSet) return;
		
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
			showMessageDialog("Any message information file doesn't exist. if you didn't reread all message information file then you do it");
			writerTextField.requestFocusInWindow();
			return;
		}
		
		boolean resultSavingFile = true;
		
		for (int i=0; resultSavingFile && i < rowCount; i++) {
			BuildFunctionCellValue buildFunctionCellValue = (BuildFunctionCellValue)messageInfoTableModel.getValueAt(i, 4);
						
			resultSavingFile = saveIOFileSetToTargetPath(listOfTargetPathSavingIOFileSet, writer, 
					buildFunctionCellValue.isSelectedIO(), 
					buildFunctionCellValue.isSelectedDirection(), 
					buildFunctionCellValue.getMessageInfo());
		}
		
		if (resultSavingFile) showMessageDialog("Sucessfully all io source file set was built");
	}

	private void eachIOFileTypeBuildButtonActionPerformed(ActionEvent e) {
		String messageInfoText = messageInfoXMLTextArea.getText();
		if (null == messageInfoText) {
			JOptionPane.showMessageDialog(mainFrame, "메시지 정보 파일 내용을 넣어주세요.");
			messageInfoXMLTextArea.requestFocusInWindow();
			return;
		}
		messageInfoText = messageInfoText.trim();
		messageInfoXMLTextArea.setText(messageInfoText);
		if (messageInfoText.equals("")) {
			showMessageDialog("메시지 정보 파일 내용을 넣어주세요.");
			messageInfoXMLTextArea.requestFocusInWindow();
			return;
		}
		
		File tempMessageInfoFile = null;
		FileOutputStream fos = null;
		
		try {
			tempMessageInfoFile = File.createTempFile("sinnoriMessageInfo", null);
			
			fos = new FileOutputStream(tempMessageInfoFile);
			fos.write(messageInfoText.getBytes("UTF-8"));
			
			// logger.log(Level.INFO, String.format("사용자가 입력한 메시지 정보 파일 내용을 저장할 임시 파일=[%s]", messageInfoFile.getAbsolutePath()));
		} catch (FileNotFoundException e1) {
			log.warn("1.fail to create temp file", e1);
			showMessageDialog("1.fail to create temp file::"+e1.toString());
			return;
		} catch (UnsupportedEncodingException e1) {
			log.warn("2.fail to create temp file", e1);
			showMessageDialog("2.fail to create temp file::"+e1.toString());
			return;
		} catch (IOException e1) {
			log.warn("3.fail to create temp file", e1);
			showMessageDialog("3.fail to create temp file::"+e1.toString());
			return;
		} finally {
			try {
				if (null != fos) fos.close();
			} catch(Exception e1) {
				
			}
		}

		IOFileSetContentsBuilderManager sourceFileBuilderManager = IOFileSetContentsBuilderManager.getInstance();
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e1) {
			String errorMessage = e1.toString();
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return;
		}
		MessageInfo messageInfo = null;
		try {
			messageInfo = messageInfoSAXParser.parse(tempMessageInfoFile, false);
		} catch (IllegalArgumentException | SAXException | IOException e1) {
			String errorMessage = e.toString();
			log.warn(errorMessage, e1);
			showMessageDialog(errorMessage);
			return;
		}
	
		// ButtonModel selectedObject = sourceTypeRadioGroup.getSelection();
		// String actionCommand = selectedObject.getActionCommand();
		
		String result = null;
		if (messageRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getMessageSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (encoderRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getEncoderSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (decoderRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getDecoderSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (serverCodecRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getServerCodecSourceFileContents(messageInfo.getDirection(), messageInfo.getMessageID(), "");
		} else if (clientCodecRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getClientCodecSourceFileContents(messageInfo.getDirection(), messageInfo.getMessageID(), "");
		} else {
			showMessageDialog("Warning::unknow IO file type radio button");
			return;
		}
		
		eachIOFileTypeResulTextArea.setText(result);
		
		if (null != tempMessageInfoFile) tempMessageInfoFile.delete();
	}
}	
