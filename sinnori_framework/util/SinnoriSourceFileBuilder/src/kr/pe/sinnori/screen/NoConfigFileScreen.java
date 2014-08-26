package kr.pe.sinnori.screen;


import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.XMLFileFilter;
import kr.pe.sinnori.gui.PathSwingAction;
import kr.pe.sinnori.gui.table.MessageInfoFileCellEditor;
import kr.pe.sinnori.gui.table.MessageInfoFileCellRenderer;
import kr.pe.sinnori.gui.table.MessageInfoFileCellValue;
import kr.pe.sinnori.gui.table.SourceBuilderTableModel;
import kr.pe.sinnori.gui.table.SourceFileCellEditor;
import kr.pe.sinnori.gui.table.SourceFileCellRenderer;
import kr.pe.sinnori.gui.table.SourceFileCellValue;
import kr.pe.sinnori.message.MessageInfoSAXParser;
import kr.pe.sinnori.source_file_builder.SourceFileBuilderManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class NoConfigFileScreen extends JPanel implements AllMessageInfoManagerIF, SourceManagerIF {
	private JFrame mainFrame = null;
	// private MainControllerIF mainController = null;
	private JFileChooser chooser = null;
	private JTextField messageInfoPathTextField;
	private JTextField sourceBasePath1TextField;
	private JTextField sourceBasePath2TextField;
	private JTextField sourceBasePath3TextField;
	private JTextField messageNameTextField;
	private JTable table;
	
	private SourceFileBuilderManager sourceFileBuilderManager = SourceFileBuilderManager.getInstance();
	// private final Action okAction = new OKButtonAction();
	
	/** DefaultTableModel start */
	private String titles[] = {
			"메시지 이름", "메시지 통신 방향", "메시지 정보 파일 기능", "소스 파일 기능"
		};
	
	private Class<?>[] columnTypes = new Class[] {
		String.class, String.class, Object.class, SourceFileCellValue.class
	};
	
	// private DefaultTableModel dtm = null;
	private JScrollPane scrollPane = new JScrollPane();
	/** DefaultTableModel end */
	
	/**
	 * Create the panel.
	 */
	public NoConfigFileScreen(JFrame mainFrame) {
		this.mainFrame = mainFrame;
		// this.mainController = mainController;
		
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// chooser.setCurrentDirectory(new File(""));
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(297dlu;pref):grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("max(120dlu;min):grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		JPanel line01Panel = new JPanel();
		add(line01Panel, "2, 2, fill, center");
		line01Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel messageInfoPathLabel = new JLabel("메시지 정보  파일 위치");
		messageInfoPathLabel.setFont(UIManager.getFont("Label.font"));
		line01Panel.add(messageInfoPathLabel, "3, 1");
		
		messageInfoPathTextField = new JTextField();
		line01Panel.add(messageInfoPathTextField, "6, 1, fill, default");
		messageInfoPathTextField.setColumns(10);
		
		JButton messageInfoPathButton = new JButton("경로 찾기");
		messageInfoPathButton.setAction(new PathSwingAction(mainFrame, chooser, messageInfoPathTextField));
		line01Panel.add(messageInfoPathButton, "8, 1");
		
		JPanel line02Panel = new JPanel();
		add(line02Panel, "2, 4, fill, center");
		line02Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(232dlu;default):grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel sourceBasePath1Label = new JLabel("1차 소스 저장 위치");
		sourceBasePath1Label.setFont(UIManager.getFont("Label.font"));
		line02Panel.add(sourceBasePath1Label, "3, 1");
		
		sourceBasePath1TextField = new JTextField();
		line02Panel.add(sourceBasePath1TextField, "6, 1, fill, default");
		sourceBasePath1TextField.setColumns(10);
		
		JButton sourceBasePath1Button = new JButton("경로 찾기");
		sourceBasePath1Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath1TextField));
		line02Panel.add(sourceBasePath1Button, "8, 1");
		
		JPanel line03Panel = new JPanel();
		add(line03Panel, "2, 6, fill, center");
		line03Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel sourceBasePath2Label = new JLabel("2차 소스 저장 위치");
		line03Panel.add(sourceBasePath2Label, "3, 1");
		
		sourceBasePath2TextField = new JTextField();
		line03Panel.add(sourceBasePath2TextField, "6, 1, fill, default");
		sourceBasePath2TextField.setColumns(10);
		
		JButton sourceBasePath2Button = new JButton("경로 찾기");
		sourceBasePath2Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath2TextField));
		line03Panel.add(sourceBasePath2Button, "8, 1");
		
		JPanel line04Panel = new JPanel();
		add(line04Panel, "2, 8, fill, center");
		line04Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel sourceBasePath3Label = new JLabel("3차 소스 저장 위치");
		line04Panel.add(sourceBasePath3Label, "3, 1");
		
		sourceBasePath3TextField = new JTextField();
		line04Panel.add(sourceBasePath3TextField, "6, 1, fill, default");
		sourceBasePath3TextField.setColumns(10);
		
		JButton sourceBasePath3Button = new JButton("경로 찾기");
		sourceBasePath3Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath3TextField));
		line04Panel.add(sourceBasePath3Button, "8, 1");
		
		JPanel line05Panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) line05Panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(line05Panel, "2, 12, fill, center");
		
		JButton allMessageInfoPathButton = new JButton("메시지 정보 전체 다시 읽기");
		class AllMessageInfoPathButtonAction implements ActionListener {
			private AllMessageInfoManagerIF allMessageInfoManager = null;
			
			public AllMessageInfoPathButtonAction(AllMessageInfoManagerIF allMessageInfoManager) {
				this.allMessageInfoManager = allMessageInfoManager;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("AllMessageInfoPathReadButtonAction::"+e.toString());
				// JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageInfoPathTextField.getText());
				allMessageInfoManager.readAllMessageInfo();
			}
		}
		
		allMessageInfoPathButton.addActionListener(new AllMessageInfoPathButtonAction(this));
		line05Panel.add(allMessageInfoPathButton);
		
		JButton allSourceFileCreateButton = new JButton("소스 전체 생성");
		line05Panel.add(allSourceFileCreateButton);
		
		JPanel line06Panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) line06Panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(line06Panel, "2, 16, fill, center");
		
		JLabel messageSearchLabel = new JLabel(">> 메시지 검색");
		line06Panel.add(messageSearchLabel);
		
		JPanel line07Panel = new JPanel();
		add(line07Panel, "2, 18, fill, center");
		line07Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel messageNameLabel = new JLabel("메시지 이름");
		line07Panel.add(messageNameLabel, "3, 1");
		
		messageNameTextField = new JTextField();
		line07Panel.add(messageNameTextField, "6, 1, fill, default");
		messageNameTextField.setColumns(10);
		
		JButton messageNameButton = new JButton("검색");
		line07Panel.add(messageNameButton, "8, 1");
		
		
		add(scrollPane, "2, 22, fill, fill");
		
		/*Object values[][] = {
				{"Echo", "양방향", new MessageInfoFileCellValue("Echo", mainFrame), new SourceFileCellValue("Echo", this.mainFrame)},
				{"AllDataType", "양방향", new MessageInfoFileCellValue("AllDataType", mainFrame), new SourceFileCellValue("AllDataType", this.mainFrame)},
			};
		
		
		DefaultTableModel dtm  = new SourceBuilderTableModel(values, titles, columnTypes);*/
			
		table = new JTable();
		table.setRowSelectionAllowed(false);
		// table.setModel(dtm);
		table.setRowHeight(40);
		
		readAllMessageInfo();
		
		//initColumnSizes(table);
		
		scrollPane.setViewportView(table);
	}
	
	private ProgressMonitor progressMonitor = null;
	
	class AllMessageInfoTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
        	// FIXME!
        	String messgaeInfoPathText = messageInfoPathTextField.getText();
    		if (null == messgaeInfoPathText) {
    			String errorMessage = String.format("메시지 정보 파일 경로를 넣어 주세요.", messgaeInfoPathText);
    			System.out.println(errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		messgaeInfoPathText = messgaeInfoPathText.trim();
    		if (messgaeInfoPathText.equals("")) {
    			messageInfoPathTextField.setText(messgaeInfoPathText);
    			String errorMessage = String.format("메시지 정보 파일 경로를 다시 넣어 주세요.", messgaeInfoPathText);
    			System.out.println(errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		File messgaeInfoPath = new File(messgaeInfoPathText);
    		if (!messgaeInfoPath.exists()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 존재하지 않습니다.", messgaeInfoPathText);
    			System.out.println(errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		if (!messgaeInfoPath.isDirectory()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리가 아닙니다.", messgaeInfoPath.getAbsolutePath());    			
    			System.out.println(errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		if (!messgaeInfoPath.canRead()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리 읽기 권한이 없습니다..", messgaeInfoPath.getAbsolutePath());
    			System.out.println(errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		/*if (!messgaeInfoPath.canWrite()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리 쓰기 권한이 없습니다.", messgaeInfoPath.getAbsolutePath());
    			
    			System.out.println(errorMessage);
    			
    			messageInfoPathTextField.requestFocusInWindow();
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			return null;
    		}*/
    		
    		ArrayList<File> messageInfoFileList = new ArrayList<File>();
    		ArrayList<kr.pe.sinnori.message.MessageInfo> messageInfoList = new ArrayList<kr.pe.sinnori.message.MessageInfo>();
    		
    		File messageInfoFiles[] = messgaeInfoPath.listFiles(new XMLFileFilter());
    		for (File messageInfoFile : messageInfoFiles) {
    			if (!messageInfoFile.isFile()) {
    				String errorMessage = String.format("warning :: not file , file name=[%s]", messageInfoFile.getName());
    				System.out.println(errorMessage);
    				continue;
    			}

    			if (!messageInfoFile.canRead()) {
    				String errorMessage = String.format("warning :: can't read, file name=[%s]", messageInfoFile.getName());
    				System.out.println(errorMessage);
    				continue;
    			}
    			
    			MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(messageInfoFile);
    			kr.pe.sinnori.message.MessageInfo messageInfo = messageInfoSAXParser.parse();
    			if (null != messageInfo) {
    				messageInfoFileList.add(messageInfoFile);
    				messageInfoList.add(messageInfo);
    			}
    		}
    		
            Object values[][] = new Object[messageInfoList.size()][titles.length];
            SourceBuilderTableModel sourceBuilderTableModel = new SourceBuilderTableModel(values, titles, columnTypes);
            
            for (int i=0; i < values.length; i++) {
            	kr.pe.sinnori.message.MessageInfo messageInfo = messageInfoList.get(i);
            	File messageInfoFile = messageInfoFileList.get(i);
            	
    			String messageID = messageInfo.getMessageID();
    			values[i][0] = messageID;
    			values[i][1] = messageInfo.getDirection().toString();
    			values[i][2] = new MessageInfoFileCellValue(messageInfoFile, messageInfo, sourceBuilderTableModel, i, mainFrame);
    			values[i][3] = new SourceFileCellValue(messageInfo, NoConfigFileScreen.this);
    			if (progressMonitor.isCanceled())  {
    				return null;
    			}
    			progressMonitor.setNote(i+"/"+values.length);
    			progressMonitor.setProgress(i*100/values.length);
    			
    			sourceBuilderTableModel.setValueAt(values[i][0], i, 0);
    			sourceBuilderTableModel.setValueAt(values[i][1], i, 1);
    			sourceBuilderTableModel.setValueAt(values[i][2], i, 2);
    			sourceBuilderTableModel.setValueAt(values[i][3], i, 3);
    		}
    		// dtm.setDataVector(values, columnTypes);
    		
    		table.setModel(sourceBuilderTableModel);
    		
    		table.getColumnModel().getColumn(0).setPreferredWidth(153);
    		table.getColumnModel().getColumn(1).setResizable(false);
    		table.getColumnModel().getColumn(1).setPreferredWidth(147);
    		table.getColumnModel().getColumn(2).setResizable(false);
    		table.getColumnModel().getColumn(2).setPreferredWidth(154);
    		table.getColumnModel().getColumn(3).setResizable(false);
    		table.getColumnModel().getColumn(3).setPreferredWidth(175);
    		
    		table.getColumnModel().getColumn(2).setCellRenderer(new MessageInfoFileCellRenderer());
    		table.getColumnModel().getColumn(2).setCellEditor(new MessageInfoFileCellEditor(new JCheckBox()));
    		
    		table.getColumnModel().getColumn(3).setCellRenderer(new SourceFileCellRenderer());
    		table.getColumnModel().getColumn(3).setCellEditor(new SourceFileCellEditor(new JCheckBox()));
    		//table.repaint();
    		scrollPane.repaint();
            
            return null;
        }
        
        private void createEmptyTable() {
        	createTable(new Object[0][titles.length]);
        }
        private void createTable(Object values[][]) {
        	// Object values[][] = new Object[0][titles.length];
            DefaultTableModel dtm = new SourceBuilderTableModel(values, titles, columnTypes);
            
            table.setModel(dtm);
    		
    		table.getColumnModel().getColumn(0).setPreferredWidth(193);
    		table.getColumnModel().getColumn(1).setResizable(false);
    		table.getColumnModel().getColumn(1).setPreferredWidth(107);
    		table.getColumnModel().getColumn(2).setResizable(false);
    		table.getColumnModel().getColumn(2).setPreferredWidth(154);
    		table.getColumnModel().getColumn(3).setResizable(false);
    		table.getColumnModel().getColumn(3).setPreferredWidth(175);
    		
    		table.getColumnModel().getColumn(2).setCellRenderer(new MessageInfoFileCellRenderer());
    		table.getColumnModel().getColumn(2).setCellEditor(new MessageInfoFileCellEditor(new JCheckBox()));
    		
    		table.getColumnModel().getColumn(3).setCellRenderer(new SourceFileCellRenderer());
    		table.getColumnModel().getColumn(3).setCellEditor(new SourceFileCellEditor(new JCheckBox()));
    		
    		scrollPane.repaint();
        }
 
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            setCursor(null);
            // startButton.setEnabled(true);
            // progressMonitor.setProgress(0);
            if (progressMonitor.isCanceled())  {
            	JOptionPane.showMessageDialog(mainFrame, "전체 메시지 정보 파일 읽기가 취소 되었습니다.");
            } else {
            	progressMonitor.close();
            }
        }
    }
 
	
	@Override
	public void readAllMessageInfo() {
		Toolkit.getDefaultToolkit().beep();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		progressMonitor = new ProgressMonitor(mainFrame, "Running a Task reading message info files", "", 0, 100);
		
		AllMessageInfoTask allMessageInfoTask = new AllMessageInfoTask();
        // task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
		
			
	}
	
	private File getPathFile(String pathText) {
		java.io.File sourcePath = new java.io.File(pathText);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("소스 경로[%s]가 존재하지 않습니다.", pathText);
			System.out.println(errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			return null;
		}
		
		if (!sourcePath.canWrite()) {
			String errorMessage = String.format("소스 경로[%s] 쓰기 권한이 없습니다.", pathText);
			System.out.println(errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			return null;
		}
		
		return sourcePath;
	}
	
	public void createSourceFile(boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.message.MessageInfo messageInfo) {
		String sourcePath1Text = sourceBasePath1TextField.getText();
		String sourcePath2Text = sourceBasePath2TextField.getText();
		String sourcePath3Text = sourceBasePath3TextField.getText();
		
		if (null == sourcePath1Text) sourcePath1Text="";
		if (null == sourcePath2Text) sourcePath2Text="";
		if (null == sourcePath3Text) sourcePath3Text="";
		
		ArrayList<File> sourceBasePathList = new ArrayList<File>();
		if (!sourcePath1Text.equals("")) {
			File oneSourcePath = getPathFile(sourcePath1Text);
			
			if (null == oneSourcePath) {
				sourceBasePath1TextField.requestFocusInWindow();
				return;
			}
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (!sourcePath2Text.equals("")) {
			File oneSourcePath = getPathFile(sourcePath2Text);
			
			if (null == oneSourcePath) {
				sourceBasePath2TextField.requestFocusInWindow();
				return;
			}
			
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (!sourcePath3Text.equals("")) {
			File oneSourcePath = getPathFile(sourcePath3Text);
			
			if (null == oneSourcePath) {
				sourceBasePath3TextField.requestFocusInWindow();
				return;
			}
			
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (0 == sourceBasePathList.size()) {
			JOptionPane.showMessageDialog(mainFrame, "소스 저장 위치 3개중 최소 1개에 값이 있어야 합니다.");
			
			sourceBasePath1TextField.requestFocusInWindow();
			return;
		}
		
		FileOutputStream fos = null;
		String author = "Jonghoon Won";
		
		for (File sourceBasePath : sourceBasePathList) {
			String messageID = messageInfo.getMessageID();
			String sourcePathText = sourceBasePath.getAbsolutePath()+File.separator+messageID;
			File oneSourcePath = new File(sourcePathText);
			if (! oneSourcePath.exists()) {
				try {
					oneSourcePath.createNewFile();
				} catch (IOException e) {
					// e.printStackTrace();
					String errorMessage = String.format("소스 경로[%s] 신규 생성이 실패했습니다.", sourcePathText);
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					continue;
				}
			}
			
			if (! oneSourcePath.canWrite()) {
				String errorMessage = String.format("소스 경로[%s] 쓰기 권한이 없습니다.", oneSourcePath.getAbsolutePath());
				System.out.println(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				continue;
			}
			
			if (isSelectedIO) {
				File messageFile = new File(oneSourcePath.getAbsolutePath()+File.separator+messageID+".java");
				File messageEncoderFile = new File(oneSourcePath.getAbsolutePath()+File.separator+messageID+"Encoder.java");
				File messageDecoderFile = new File(oneSourcePath.getAbsolutePath()+File.separator+messageID+"Decoder.java");
				
				if (!messageFile.exists()) {
					try {
						messageFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 파일[%s] 신규 생성 실패", messageFile.getAbsolutePath());
						System.out.println(errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
				}
				
				if (!messageFile.canWrite()) {
					String errorMessage = String.format("메시지 파일[%s] 쓰기 권한이 없습니다.", messageFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				}
				
				try {
					fos = new FileOutputStream(messageFile);
					
					String messageSourceText = sourceFileBuilderManager.getMessageSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 파일[%s] 이 존재하지 않습니다.", messageFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 파일[%s] 쓰기 에러, %s", messageFile.getAbsolutePath(), e.getMessage());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (!messageEncoderFile.exists()) {
					try {
						messageEncoderFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 인코더 파일[%s] 신규 생성 실패", messageEncoderFile.getAbsolutePath());
						System.out.println(errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
				}
				
				if (!messageEncoderFile.canWrite()) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 쓰기 권한이 없습니다.", messageEncoderFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				}
				
				try {
					fos = new FileOutputStream(messageEncoderFile);
					
					String messageEncoderSourceText = sourceFileBuilderManager.getEncoderSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageEncoderSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 이 존재하지 않습니다.", messageEncoderFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 쓰기 에러, %s", messageEncoderFile.getAbsolutePath(), e.getMessage());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (!messageDecoderFile.exists()) {
					try {
						messageDecoderFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 디코더 파일[%s] 신규 생성 실패", messageDecoderFile.getAbsolutePath());
						System.out.println(errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
				}
				
				if (!messageDecoderFile.canWrite()) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 쓰기 권한이 없습니다.", messageDecoderFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				}
				
				try {
					fos = new FileOutputStream(messageDecoderFile);
					
					String messageDecoderSourceText = sourceFileBuilderManager.getDecoderSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageDecoderSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 이 존재하지 않습니다.", messageDecoderFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 쓰기 에러, %s", messageDecoderFile.getAbsolutePath(), e.getMessage());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (isSelectedDirection) {
				File messageServerCodecFile = new File(oneSourcePath.getAbsolutePath()+File.separator+messageID+"ServerCodec.java");
				File messageClientCodecFile = new File(oneSourcePath.getAbsolutePath()+File.separator+messageID+"ClientCodec.java");
				
				if (!messageServerCodecFile.exists()) {
					try {
						messageServerCodecFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 서버 코덱 파일[%s] 신규 생성 실패", messageServerCodecFile.getAbsolutePath());
						System.out.println(errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
				}
				
				if (!messageServerCodecFile.canWrite()) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 쓰기 권한이 없습니다.", messageServerCodecFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				}
				
				try {
					fos = new FileOutputStream(messageServerCodecFile);
					
					String messageSourceText = sourceFileBuilderManager.getServerCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageID, author);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 이 존재하지 않습니다.", messageServerCodecFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 쓰기 에러, %s", messageServerCodecFile.getAbsolutePath(), e.getMessage());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
				
				if (!messageClientCodecFile.exists()) {
					try {
						messageServerCodecFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 신규 생성 실패", messageClientCodecFile.getAbsolutePath());
						System.out.println(errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
				}
				
				if (!messageClientCodecFile.canWrite()) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 쓰기 권한이 없습니다.", messageClientCodecFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				}
				
				try {
					fos = new FileOutputStream(messageClientCodecFile);
					
					String messageSourceText = sourceFileBuilderManager.getClientCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageID, author);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 이 존재하지 않습니다.", messageClientCodecFile.getAbsolutePath());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 쓰기 에러, %s", messageClientCodecFile.getAbsolutePath(), e.getMessage());
					System.out.println(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
 


	/*private class OKButtonAction extends AbstractAction {
		public OKButtonAction() {
			putValue(NAME, "Next");
			putValue(SHORT_DESCRIPTION, "확인 버튼 액션");
		}
		public void actionPerformed(ActionEvent e) {
			if (!mainController.setSinnoriProperteies(configFileTextField.getText())) {
				return;
			}
		}
	}*/
}
