package kr.pe.sinnori.screen;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.lib.XMLFileFilter;
import kr.pe.sinnori.common.util.FileLastModifiedComparator;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.PathSwingAction;
import kr.pe.sinnori.gui.lib.MessageInfoManagerIF;
import kr.pe.sinnori.gui.message.builder.SourceFileBuilderManager;
import kr.pe.sinnori.gui.message.builder.info.MessageInfo;
import kr.pe.sinnori.gui.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.gui.table.MessageInfoFileCellEditor;
import kr.pe.sinnori.gui.table.MessageInfoFileCellRenderer;
import kr.pe.sinnori.gui.table.MessageInfoFileCellValue;
import kr.pe.sinnori.gui.table.SourceBuilderTableModel;
import kr.pe.sinnori.gui.table.SourceFileCellEditor;
import kr.pe.sinnori.gui.table.SourceFileCellRenderer;
import kr.pe.sinnori.gui.table.SourceFileCellValue;
import kr.pe.sinnori.gui.util.RegexLimitPlainDocume;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class MultiSourceBuilderScreen extends JPanel implements MessageInfoManagerIF, SourceManagerIF {
	private Logger logger =  Logger.getGlobal();
	
	private JFrame mainFrame = null;
	// private MainControllerIF mainController = null;
	
	private JComboBox<String> projectComboBox = null;  
	private JFileChooser chooser = null;
	private JTextField messageInfoPathTextField;
	private JTextField sourceBasePath1TextField;
	private JCheckBox sourceBasePath1CheckBox;
	private JTextField sourceBasePath2TextField;
	private JCheckBox sourceBasePath2CheckBox;
	private JTextField sourceBasePath3TextField;
	private JCheckBox sourceBasePath3CheckBox;
	private JTextField authorTextField;
	private JTextField searchKeywordTextField;
	private JTable table;
	
	private SourceFileBuilderManager sourceFileBuilderManager = SourceFileBuilderManager.getInstance();
	
	
	/** DefaultTableModel start */
	private SourceBuilderTableModel sourceBuilderTableModel = null;
	
	private String titles[] = {
			"메시지 이름", "최근작업일시", "메시지 통신 방향", "메시지 정보 파일 기능", "소스 파일 기능"
		};
	
	private Class<?>[] columnTypes = new Class[] {
		String.class, String.class, String.class, MessageInfoFileCellEditor.class, SourceFileCellEditor.class
	};
	
	private JScrollPane scrollPane = new JScrollPane();
	/** DefaultTableModel end */
	
	
	/**
	 * Create the panel.
	 */
	public MultiSourceBuilderScreen(final JFrame mainFrame, MainControllerIF mainController,
			String sinnoriInstallAbsPathName, 
			ArrayList<String> mainProjectList, HashMap<String, SequencedProperties> project2ConfigHash) {
		this.mainFrame = mainFrame;
		// this.mainController = mainController;
		
		String projectList[] = new String[mainProjectList.size()+1];
		projectList[0] = "- 선택 -";
		for (int i=1; i < projectList.length; i++) {
			projectList[i] = mainProjectList.get(i-1);
		}
		
		projectComboBox = new JComboBox<String>(projectList);
		class ProjectComboBoxAction implements ActionListener {
			private String sinnoriInstallAbsPathName = null;
			
			public ProjectComboBoxAction(String sinnoriInstallAbsPathName) {
				this.sinnoriInstallAbsPathName = sinnoriInstallAbsPathName;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String projectName = (String) jcmbType.getSelectedItem();
				
				// JOptionPane.showMessageDialog(mainFrame, projectName);
				
				int inx = jcmbType.getSelectedIndex();
				if (inx > 0) {
					StringBuilder baseStrBuilder = new StringBuilder(sinnoriInstallAbsPathName).append(File.separator).append("project")
							.append(File.separator).append(projectName);
					StringBuilder messageInfoStrBuilder = new StringBuilder(baseStrBuilder.toString()).append(File.separator).append("impl")
							.append(File.separator).append("message").append(File.separator).append("info");
					
					StringBuilder serverStrBuilder = new StringBuilder(baseStrBuilder.toString()).append(File.separator).append("server_build")
							.append(File.separator).append("src").append(File.separator).append("kr").append(File.separator).append("pe")
							.append(File.separator).append("sinnori").append(File.separator).append("impl").append(File.separator).append("message");
					
					StringBuilder clientAppStrBuilder = new StringBuilder(baseStrBuilder.toString()).append(File.separator).append("client_build")
							.append(File.separator).append("app_build").append(File.separator).append("src")
							.append(File.separator).append("kr").append(File.separator).append("pe")
							.append(File.separator).append("sinnori").append(File.separator).append("impl").append(File.separator).append("message");

					StringBuilder clientWebStrBuilder = new StringBuilder(baseStrBuilder.toString()).append(File.separator).append("client_build")
							.append(File.separator).append("web_build").append(File.separator).append("src")
							.append(File.separator).append("kr").append(File.separator).append("pe")
							.append(File.separator).append("sinnori").append(File.separator).append("impl").append(File.separator).append("message");
					
					messageInfoPathTextField.setText(messageInfoStrBuilder.toString());
					sourceBasePath1TextField.setText(serverStrBuilder.toString());
					sourceBasePath2TextField.setText(clientAppStrBuilder.toString());
					sourceBasePath3TextField.setText(clientWebStrBuilder.toString());
					
					sourceBasePath1CheckBox.setSelected(true);
					sourceBasePath2CheckBox.setSelected(true);
					sourceBasePath3CheckBox.setSelected(false);
				}
			}
		}
		projectComboBox.addActionListener(new ProjectComboBoxAction(sinnoriInstallAbsPathName));
		
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
		line01Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel projectListLabel = new JLabel("프로젝트 선택");
		projectListLabel.setFont(UIManager.getFont("Label.font"));
		line01Panel.add(projectListLabel, "3, 1");
		line01Panel.add(projectComboBox, "6, 1");
		add(line01Panel, "2, 2, fill, center");		
		
		JPanel messageInfoPathLinePanel = new JPanel();
		add(messageInfoPathLinePanel, "2, 4, fill, center");
		messageInfoPathLinePanel.setLayout(new FormLayout(new ColumnSpec[] {
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
		// messageInfoPathLabel.setFont(UIManager.getFont("Table.font"));
		messageInfoPathLinePanel.add(messageInfoPathLabel, "3, 1");
		
		messageInfoPathTextField = new JTextField();
		messageInfoPathTextField.setDocument(new RegexLimitPlainDocume(null, 250, null));
		messageInfoPathLinePanel.add(messageInfoPathTextField, "6, 1, fill, default");
		// messageInfoPathTextField.setColumns(10);
		
		JButton messageInfoPathButton = new JButton("경로 찾기");
		messageInfoPathButton.setAction(new PathSwingAction(mainFrame, chooser, messageInfoPathTextField));
		messageInfoPathLinePanel.add(messageInfoPathButton, "8, 1");
		
		JPanel sourceBasePath1LinePanel = new JPanel();
		add(sourceBasePath1LinePanel, "2, 6, fill, center");
		sourceBasePath1LinePanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
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
		sourceBasePath1LinePanel.add(sourceBasePath1Label, "3, 1");
		
		sourceBasePath1CheckBox = new JCheckBox();
		sourceBasePath1CheckBox.setSelected(true);
		sourceBasePath1LinePanel.add(sourceBasePath1CheckBox, "5, 1");
		
		sourceBasePath1TextField = new JTextField();
		sourceBasePath1TextField.setDocument(new RegexLimitPlainDocume(null, 250, null));
		sourceBasePath1LinePanel.add(sourceBasePath1TextField, "8, 1, fill, default");
		sourceBasePath1TextField.setColumns(10);	
		
		JButton sourceBasePath1Button = new JButton("경로 찾기");
		sourceBasePath1Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath1TextField));
		sourceBasePath1LinePanel.add(sourceBasePath1Button, "10, 1");
		
		JPanel sourceBasePath2LinePanel = new JPanel();
		add(sourceBasePath2LinePanel, "2, 8, fill, center");
		sourceBasePath2LinePanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
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
		sourceBasePath2LinePanel.add(sourceBasePath2Label, "3, 1");
		
		sourceBasePath2CheckBox = new JCheckBox();
		sourceBasePath2CheckBox.setSelected(true);
		sourceBasePath2LinePanel.add(sourceBasePath2CheckBox, "5, 1");
				
		sourceBasePath2TextField = new JTextField();
		sourceBasePath2TextField.setDocument(new RegexLimitPlainDocume(null, 250, null));
		sourceBasePath2LinePanel.add(sourceBasePath2TextField, "8, 1, fill, default");
		sourceBasePath2TextField.setColumns(10);
		
		JButton sourceBasePath2Button = new JButton("경로 찾기");
		sourceBasePath2Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath2TextField));
		sourceBasePath2LinePanel.add(sourceBasePath2Button, "10, 1");
		
		JPanel sourceBasePath3LinePanel = new JPanel();
		add(sourceBasePath3LinePanel, "2, 10, fill, center");
		sourceBasePath3LinePanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
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
		sourceBasePath3LinePanel.add(sourceBasePath3Label, "3, 1");
		
		sourceBasePath3CheckBox = new JCheckBox();
		sourceBasePath3CheckBox.setSelected(false);
		sourceBasePath3LinePanel.add(sourceBasePath3CheckBox, "5, 1");
		
		sourceBasePath3TextField = new JTextField();
		sourceBasePath3TextField.setDocument(new RegexLimitPlainDocume(null, 250, null));
		sourceBasePath3LinePanel.add(sourceBasePath3TextField, "8, 1, fill, default");
		sourceBasePath3TextField.setColumns(10);
		
		JButton sourceBasePath3Button = new JButton("경로 찾기");
		sourceBasePath3Button.setAction(new PathSwingAction(mainFrame, chooser, sourceBasePath3TextField));
		sourceBasePath3LinePanel.add(sourceBasePath3Button, "10, 1");
		
		//////////////////////////
		JPanel line06Panel = new JPanel();
		add(line06Panel, "2, 14, fill, center");
		line06Panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel authorLabel = new JLabel("작           성           자");
		line06Panel.add(authorLabel, "3, 1");
		
		authorTextField = new JTextField();
		authorTextField.setDocument(new RegexLimitPlainDocume(null, 20, null));
		line06Panel.add(authorTextField, "6, 1, fill, default");
		authorTextField.setColumns(20);
		
		//////////////////////////
		
		JPanel line07Panel = new JPanel();
		FlowLayout flowLayoutOfLine07Panel = (FlowLayout) line07Panel.getLayout();
		flowLayoutOfLine07Panel.setAlignment(FlowLayout.LEFT);
		add(line07Panel, "2, 16, fill, center");
		
		JButton allMessageInfoPathButton = new JButton("메시지 정보 전체 다시 읽기");
		class AllMessageInfoPathButtonAction implements ActionListener {
			private MessageInfoManagerIF messageInfoManager = null;
			
			public AllMessageInfoPathButtonAction(MessageInfoManagerIF messageInfoManager) {
				this.messageInfoManager = messageInfoManager;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageInfoPathTextField.getText());
				messageInfoManager.readAllMessageInfo();
				
				JOptionPane.showMessageDialog(mainFrame, "메시지 정보 전체 다시 읽기가 완료 되었습니다.");
			}
		}
		
		allMessageInfoPathButton.addActionListener(new AllMessageInfoPathButtonAction(this));
		line07Panel.add(allMessageInfoPathButton);
		
		JButton allSourceFileCreateButton = new JButton("IO와 방향성 옵션에 영향 받는 소스 전체 생성");
		class AllSourceFileCreateAction implements ActionListener {
			private SourceManagerIF sourceManager = null;
			
			public AllSourceFileCreateAction(SourceManagerIF sourceManager) {
				this.sourceManager = sourceManager;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageInfoPathTextField.getText());
				sourceManager.createAllSourceFiles();
			}
		}
		allSourceFileCreateButton.addActionListener(new AllSourceFileCreateAction(this));
		line07Panel.add(allSourceFileCreateButton);
		
		JPanel line08Panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) line08Panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(line08Panel, "2, 18, fill, center");
		
		JLabel messageSearchLabel = new JLabel(">> 메시지 검색");
		line08Panel.add(messageSearchLabel);
		
		JPanel line09Panel = new JPanel();
		add(line09Panel, "2, 20, fill, center");
		line09Panel.setLayout(new FormLayout(new ColumnSpec[] {
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
		line09Panel.add(messageNameLabel, "3, 1");
		
		searchKeywordTextField = new JTextField();
		line09Panel.add(searchKeywordTextField, "6, 1, fill, default");
		searchKeywordTextField.setColumns(10);
		
		JButton searchKeywordButton = new JButton("검색");
		class SearchKeyWordAction implements ActionListener {
			private MessageInfoManagerIF messageInfoManager = null;
			
			public SearchKeyWordAction(MessageInfoManagerIF messageInfoManager) {
				this.messageInfoManager = messageInfoManager;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageInfoPathTextField.getText());
				messageInfoManager.readMessageInfoWithSearchKeyword();
				
				JOptionPane.showMessageDialog(mainFrame, "검색 완료 되었습니다.");
			}
		}
		searchKeywordButton.addActionListener(new SearchKeyWordAction(this));
		line09Panel.add(searchKeywordButton, "8, 1");
		
		
		add(scrollPane, "2, 24, fill, fill");
		
		/*Object values[][] = {
				{"Echo", "양방향", new MessageInfoFileCellValue("Echo", mainFrame), new SourceFileCellValue("Echo", this.mainFrame)},
				{"AllDataType", "양방향", new MessageInfoFileCellValue("AllDataType", mainFrame), new SourceFileCellValue("AllDataType", this.mainFrame)},
			};
		
		
		DefaultTableModel dtm  = new SourceBuilderTableModel(values, titles, columnTypes);*/
		
		
			
		table = new JTable();
		table.setRowSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.setPreferredScrollableViewportSize(new Dimension(800, 300));
		table.setAutoCreateRowSorter(true);
		table.setRowHeight(38);
		
		scrollPane.setViewportView(table);
		
		readAllMessageInfo();		
		//initColumnSizes(table);
	}
	
	private ProgressMonitor progressMonitor = null;
	
	public enum MESSAGE_INFO_SEARCH_GUBUN {
		NO_SEARCH_KEYWORD, SEARCH_KEYWORD
	};
	class AllMessageInfoTask extends SwingWorker<Void, Void> {
		
		private MESSAGE_INFO_SEARCH_GUBUN messageInfoSearchGubun;
		
		public AllMessageInfoTask(MESSAGE_INFO_SEARCH_GUBUN messageInfoSearchGubun) {
			this.messageInfoSearchGubun = messageInfoSearchGubun;
		}
		
        @Override
        public Void doInBackground() {
        	// FIXME!
        	String messgaeInfoPathText = messageInfoPathTextField.getText();
    		if (null == messgaeInfoPathText) {
    			String errorMessage = String.format("메시지 정보 파일 경로를 넣어 주세요.", messgaeInfoPathText);
    			logger.log(Level.WARNING, errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		messgaeInfoPathText = messgaeInfoPathText.trim();
    		if (messgaeInfoPathText.equals("")) {
    			messageInfoPathTextField.setText(messgaeInfoPathText);
    			String errorMessage = String.format("메시지 정보 파일 경로를 다시 넣어 주세요.", messgaeInfoPathText);
    			logger.log(Level.WARNING, errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		File messgaeInfoPath = new File(messgaeInfoPathText);
    		if (!messgaeInfoPath.exists()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 존재하지 않습니다.", messgaeInfoPathText);
    			logger.log(Level.WARNING, errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		if (!messgaeInfoPath.isDirectory()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리가 아닙니다.", messgaeInfoPath.getAbsolutePath());    			
    			logger.log(Level.WARNING, errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		if (!messgaeInfoPath.canRead()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리 읽기 권한이 없습니다..", messgaeInfoPath.getAbsolutePath());
    			logger.log(Level.WARNING, errorMessage);
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			
    			createEmptyTable();
    			messageInfoPathTextField.requestFocusInWindow();
    			return null;
    		}
    		
    		/*if (!messgaeInfoPath.canWrite()) {
    			String errorMessage = String.format("메시지 정보 파일 경로[%s]가 디렉토리 쓰기 권한이 없습니다.", messgaeInfoPath.getAbsolutePath());
    			
    			logger.log(Level.WARNING, errorMessage);
    			
    			messageInfoPathTextField.requestFocusInWindow();
    			JOptionPane.showMessageDialog(mainFrame, errorMessage);
    			return null;
    		}*/
    		
    		ArrayList<File> messageInfoFileList = new ArrayList<File>();
    		ArrayList<kr.pe.sinnori.gui.message.builder.info.MessageInfo> messageInfoList = new ArrayList<kr.pe.sinnori.gui.message.builder.info.MessageInfo>();
    		
    		File messageInfoFiles[] = messgaeInfoPath.listFiles(new XMLFileFilter());
    		
    		
    		Arrays.sort(messageInfoFiles, new FileLastModifiedComparator());
    		
    		
    		for (File messageInfoFile : messageInfoFiles) {
    			if (!messageInfoFile.isFile()) {
    				String errorMessage = String.format("warning :: not file , file name=[%s]", messageInfoFile.getName());
    				logger.log(Level.WARNING, errorMessage);
    				continue;
    			}

    			if (!messageInfoFile.canRead()) {
    				String errorMessage = String.format("warning :: can't read, file name=[%s]", messageInfoFile.getName());
    				logger.log(Level.WARNING, errorMessage);
    				continue;
    			}
    			
    			MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(messageInfoFile, true);
    			kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo = messageInfoSAXParser.parse();
    			if (null != messageInfo) {
    				if (MESSAGE_INFO_SEARCH_GUBUN.SEARCH_KEYWORD == messageInfoSearchGubun) {
    					String fileName = messageInfoFile.getName();
    					if (fileName.indexOf(searchKeywordTextField.getText()) >= 0) {
    						messageInfoFileList.add(messageInfoFile);
            				messageInfoList.add(messageInfo);
    					}
    				} else {
    					messageInfoFileList.add(messageInfoFile);
        				messageInfoList.add(messageInfo);
    				}
    			}
    		}
    		
            Object values[][] = new Object[messageInfoList.size()][titles.length];
            
            
            // SourceBuilderTableModel sourceBuilderTableModel = new SourceBuilderTableModel(values, titles, columnTypes);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            
            for (int i=0; i < values.length; i++) {
            	kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo = messageInfoList.get(i);
            	File messageInfoFile = messageInfoFileList.get(i);
            	
    			String messageID = messageInfo.getMessageID();
    			values[i][0] = messageID;
    			values[i][1] = sdf.format(messageInfo.getLastModified());
    			if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL) {
    				values[i][2] = "양방향";
    			} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER) {
    				values[i][2] = "client -> server";
    			} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET) {
    				values[i][2] = "server -> client";
    			} else {
    				values[i][2] = "무방향";
    			}
    			    			
    			values[i][3] = new MessageInfoFileCellValue(i, messageInfoFile, messageInfo, MultiSourceBuilderScreen.this, mainFrame);
    			values[i][4] = new SourceFileCellValue(messageInfo, MultiSourceBuilderScreen.this, mainFrame);
    			if (progressMonitor.isCanceled())  {
    				return null;
    			}
    			progressMonitor.setNote(i+"/"+values.length);
    			progressMonitor.setProgress(i*100/values.length);
    		}
            
    		createTable(values);
            
            return null;
        }
        
        private void createEmptyTable() {
        	createTable(new Object[0][titles.length]);
        }
        private void createTable(Object values[][]) {
        	// Object values[][] = new Object[0][titles.length];
            // DefaultTableModel dtm = new SourceBuilderTableModel(values, titles, columnTypes);
        	sourceBuilderTableModel = new SourceBuilderTableModel(values, titles, columnTypes);
            
            /** 모델  교체중 repaint event 를 막기 위해서 잠시 visable 속성을 끔 */
            table.setVisible(false);
            table.setModel(sourceBuilderTableModel);            
    		
    		table.getColumnModel().getColumn(0).setPreferredWidth(170);
    		
    		// table.getColumnModel().getColumn(1).setResizable(false);    		
    		table.getColumnModel().getColumn(1).setPreferredWidth(95);
    		
    		// table.getColumnModel().getColumn(2).setResizable(false);
    		table.getColumnModel().getColumn(2).setPreferredWidth(85);
    		
    		table.getColumnModel().getColumn(3).setResizable(false);
    		table.getColumnModel().getColumn(3).setPreferredWidth(150);
    		
    		table.getColumnModel().getColumn(4).setResizable(false);
    		table.getColumnModel().getColumn(4).setPreferredWidth(175);
    		
    		table.getColumnModel().getColumn(3).setCellRenderer(new MessageInfoFileCellRenderer());
    		table.getColumnModel().getColumn(3).setCellEditor(new MessageInfoFileCellEditor(new JCheckBox()));
    		
    		table.getColumnModel().getColumn(4).setCellRenderer(new SourceFileCellRenderer());
    		table.getColumnModel().getColumn(4).setCellEditor(new SourceFileCellEditor(new JCheckBox()));
    		
    		/** 모델  교체중 repaint event 를 막기 위해서 잠시 visable 속성 복귀 */
    		table.setVisible(true);
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
		
		AllMessageInfoTask allMessageInfoTask = new AllMessageInfoTask(MESSAGE_INFO_SEARCH_GUBUN.NO_SEARCH_KEYWORD);
        // task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
	}
	
	@Override
	public void readMessageInfoWithSearchKeyword() {
		Toolkit.getDefaultToolkit().beep();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		progressMonitor = new ProgressMonitor(mainFrame, "Running a Task reading message info files", "", 0, 100);
		AllMessageInfoTask allMessageInfoTask = new AllMessageInfoTask(MESSAGE_INFO_SEARCH_GUBUN.SEARCH_KEYWORD);
		allMessageInfoTask.execute();
	}
	
	private File getPathFile(String pathText) {
		java.io.File sourcePath = new java.io.File(pathText);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("소스 경로[%s]가 존재하지 않습니다.", pathText);
			logger.log(Level.WARNING, errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			return null;
		}
		
		if (!sourcePath.canWrite()) {
			String errorMessage = String.format("소스 경로[%s] 쓰기 권한이 없습니다.", pathText);
			logger.log(Level.WARNING, errorMessage);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			return null;
		}
		
		return sourcePath;
	}
	
	@Override
	public boolean createSourceFile(boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo) {
		String sourcePath1Text = sourceBasePath1TextField.getText();
		String sourcePath2Text = sourceBasePath2TextField.getText();
		String sourcePath3Text = sourceBasePath3TextField.getText();
		String author = authorTextField.getText();
		
		if (null == sourcePath1Text) {
			sourcePath1Text="";			
		} else {
			sourcePath1Text = sourcePath1Text.trim();
		}
		sourceBasePath1TextField.setText(sourcePath1Text);
		
		if (null == sourcePath2Text) {
			sourcePath2Text="";			
		} else {
			sourcePath2Text = sourcePath2Text.trim();
		}		
		sourceBasePath2TextField.setText(sourcePath2Text);
		
		
		if (null == sourcePath3Text) {
			sourcePath3Text="";			
		} else {
			sourcePath3Text = sourcePath3Text.trim();
		}
		sourceBasePath3TextField.setText(sourcePath3Text);
		
		if (null == author) {
			author = "";			
		} else {
			author = author.trim();
		}
		authorTextField.setText(author);
		
		ArrayList<File> sourceBasePathList = new ArrayList<File>();
		
		if (sourceBasePath1CheckBox.isSelected()) {
			if (sourcePath1Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 1차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath1TextField.requestFocusInWindow();
				return false;
			}
			
			File oneSourcePath = getPathFile(sourcePath1Text);
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (sourceBasePath2CheckBox.isSelected()) {
			if (sourcePath2Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 2차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath2TextField.requestFocusInWindow();
				return false;
			}
			
			File oneSourcePath = getPathFile(sourcePath2Text);
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (sourceBasePath3CheckBox.isSelected()) {
			if (sourcePath3Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 3차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath3TextField.requestFocusInWindow();
				return false;
			}
			
			File oneSourcePath = getPathFile(sourcePath3Text);
			sourceBasePathList.add(oneSourcePath);
		}
				
		if (0 == sourceBasePathList.size()) {
			JOptionPane.showMessageDialog(mainFrame, "소스 저장 위치 3개중 최소 1개에 값이 있어야 합니다.");
			
			sourceBasePath1TextField.requestFocusInWindow();
			return false;
		}
		
		if (author.equals("")) {
			JOptionPane.showMessageDialog(mainFrame, "저자 이름을 넣어 주세요.");
			authorTextField.requestFocusInWindow();
			return false;
		}
		
		boolean result = doCreateSourceFile(sourceBasePathList, author, isSelectedIO, isSelectedDirection, messageInfo);
		
		return result;
	}
	
	public boolean doCreateSourceFile(ArrayList<File> sourceBasePathList, String author, 
			boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo) {
		FileOutputStream fos = null;
		
		for (File sourceBasePath : sourceBasePathList) {
			String messageID = messageInfo.getMessageID();
			String sourcePathText = sourceBasePath.getAbsolutePath()+File.separator+messageID;
			File oneSourcePath = new File(sourcePathText);
			if (! oneSourcePath.exists()) {
				boolean result = oneSourcePath.mkdir();
				if (!result) {
					String errorMessage = String.format("소스 경로[%s] 생성 실패", sourcePathText);
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					continue;
				}
			}
			
			if (! oneSourcePath.canWrite()) {
				String errorMessage = String.format("소스 경로[%s] 쓰기 권한이 없습니다.", oneSourcePath.getAbsolutePath());
				logger.log(Level.WARNING, errorMessage);
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
						logger.log(Level.WARNING, errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return false;
					}
				}
				
				if (!messageFile.canWrite()) {
					String errorMessage = String.format("메시지 파일[%s] 쓰기 권한이 없습니다.", messageFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				}
				
				try {
					fos = new FileOutputStream(messageFile);
					
					String messageSourceText = sourceFileBuilderManager.getMessageSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 파일[%s] 이 존재하지 않습니다.", messageFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 파일[%s] 쓰기 에러, %s", messageFile.getAbsolutePath(), e.getMessage());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
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
						logger.log(Level.WARNING, errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return false;
					}
				}
				
				if (!messageEncoderFile.canWrite()) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 쓰기 권한이 없습니다.", messageEncoderFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				}
				
				try {
					fos = new FileOutputStream(messageEncoderFile);
					
					String messageEncoderSourceText = sourceFileBuilderManager.getEncoderSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageEncoderSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 이 존재하지 않습니다.", messageEncoderFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 인코더 파일[%s] 쓰기 에러, %s", messageEncoderFile.getAbsolutePath(), e.getMessage());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
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
						logger.log(Level.WARNING, errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return false;
					}
				}
				
				if (!messageDecoderFile.canWrite()) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 쓰기 권한이 없습니다.", messageDecoderFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				}
				
				try {
					fos = new FileOutputStream(messageDecoderFile);
					
					String messageDecoderSourceText = sourceFileBuilderManager.getDecoderSourceFileBuilder().toString(messageID, author, messageInfo);
					
					fos.write(messageDecoderSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 이 존재하지 않습니다.", messageDecoderFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 디코더 파일[%s] 쓰기 에러, %s", messageDecoderFile.getAbsolutePath(), e.getMessage());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
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
						logger.log(Level.WARNING, errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return false;
					}
				}
				
				if (!messageServerCodecFile.canWrite()) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 쓰기 권한이 없습니다.", messageServerCodecFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				}
				
				try {
					fos = new FileOutputStream(messageServerCodecFile);
					
					String messageSourceText = sourceFileBuilderManager.getServerCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageID, author);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 이 존재하지 않습니다.", messageServerCodecFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 서버 코덱 파일[%s] 쓰기 에러, %s", messageServerCodecFile.getAbsolutePath(), e.getMessage());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
				
				if (!messageClientCodecFile.exists()) {
					try {
						messageClientCodecFile.createNewFile();
					} catch (IOException e) {
						String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 신규 생성 실패", messageClientCodecFile.getAbsolutePath());
						logger.log(Level.WARNING, errorMessage);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return false;
					}
				}
				
				if (!messageClientCodecFile.canWrite()) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 쓰기 권한이 없습니다.", messageClientCodecFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				}
				
				try {
					fos = new FileOutputStream(messageClientCodecFile);
					
					String messageSourceText = sourceFileBuilderManager.getClientCodecSourceFileBuilder().toString(messageInfo.getDirection(), messageID, author);
					
					fos.write(messageSourceText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
				} catch (FileNotFoundException e) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 이 존재하지 않습니다.", messageClientCodecFile.getAbsolutePath());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} catch (IOException e) {
					String errorMessage = String.format("메시지 클라이언트 코덱 파일[%s] 쓰기 에러, %s", messageClientCodecFile.getAbsolutePath(), e.getMessage());
					logger.log(Level.WARNING, errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return false;
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return true;
	}
	
	
	// FIXME!
	@Override
	public void createAllSourceFiles() {
		String sourcePath1Text = sourceBasePath1TextField.getText();
		String sourcePath2Text = sourceBasePath2TextField.getText();
		String sourcePath3Text = sourceBasePath3TextField.getText();
		String author = authorTextField.getText();
		
		if (null == sourcePath1Text) {
			sourcePath1Text="";			
		} else {
			sourcePath1Text = sourcePath1Text.trim();
		}
		sourceBasePath1TextField.setText(sourcePath1Text);
		
		if (null == sourcePath2Text) {
			sourcePath2Text="";			
		} else {
			sourcePath2Text = sourcePath2Text.trim();
		}		
		sourceBasePath2TextField.setText(sourcePath2Text);
		
		
		if (null == sourcePath3Text) {
			sourcePath3Text="";			
		} else {
			sourcePath3Text = sourcePath3Text.trim();
		}
		sourceBasePath3TextField.setText(sourcePath3Text);
		
		if (null == author) {
			author = "";			
		} else {
			author = author.trim();
		}
		authorTextField.setText(author);
		
		ArrayList<File> sourceBasePathList = new ArrayList<File>();
		
		if (sourceBasePath1CheckBox.isSelected()) {
			if (sourcePath1Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 1차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath1TextField.requestFocusInWindow();
				return;
			}
			
			File oneSourcePath = getPathFile(sourcePath1Text);
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (sourceBasePath2CheckBox.isSelected()) {
			if (sourcePath2Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 2차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath2TextField.requestFocusInWindow();
				return;
			}
			
			File oneSourcePath = getPathFile(sourcePath2Text);
			sourceBasePathList.add(oneSourcePath);
		}
		
		if (sourceBasePath3CheckBox.isSelected()) {
			if (sourcePath3Text.equals("")) {
				JOptionPane.showMessageDialog(mainFrame, "선택한 3차 소스 저장 위치 값을 넣어 주세요.");
				sourceBasePath3TextField.requestFocusInWindow();
				return;
			}
			
			File oneSourcePath = getPathFile(sourcePath3Text);
			sourceBasePathList.add(oneSourcePath);
		}
				
		if (0 == sourceBasePathList.size()) {
			JOptionPane.showMessageDialog(mainFrame, "소스 저장 위치 3개중 최소 1개에 값이 있어야 합니다.");
			
			sourceBasePath1TextField.requestFocusInWindow();
			return;
		}
		
		if (author.equals("")) {
			JOptionPane.showMessageDialog(mainFrame, "저자 이름을 넣어 주세요.");
			authorTextField.requestFocusInWindow();
			return;
		}
		
		if (null == sourceBuilderTableModel) {
			JOptionPane.showMessageDialog(mainFrame, "메시지 정보가 없습니다. 메시지 정보 전체 다시 읽기를 수행해 주세요.");
			authorTextField.requestFocusInWindow();
			return;
		}
		
		int rowCount = sourceBuilderTableModel.getRowCount();
		
		boolean result = true;
		
		for (int i=0; i < rowCount; i++) {
			SourceFileCellValue sourceFileCellValue = (SourceFileCellValue)sourceBuilderTableModel.getValueAt(i, 4);
						
			result = doCreateSourceFile(sourceBasePathList, author, 
					sourceFileCellValue.isSelectedIO(), 
					sourceFileCellValue.isSelectedDirection(), 
					sourceFileCellValue.getMessageInfo());
			
			if (!result) break;
		}
		
		if (result) JOptionPane.showMessageDialog(mainFrame, "IO와 방향성 옵션에 영향 받는 소스 전체 생성이 완료 되었습니다.");
	}

	@Override
	public void retry(int row, MessageInfo messageInfo) {
		String directionStr = null;
		if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL) {
			directionStr = "양방향";
		} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_CLIENT_TO_SERVER) {
			directionStr = "client -> server";
		} else if (messageInfo.getDirection() == CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_SERVER_TO_CLINET) {
			directionStr = "server -> client";
		} else {
			directionStr = "무방향";
		}
		sourceBuilderTableModel.setValueAt(directionStr, row, 2);
		SourceFileCellValue sourceFileCellValue = (SourceFileCellValue)sourceBuilderTableModel.getValueAt(row, 4);
		sourceFileCellValue.setMessageInfo(messageInfo);
		scrollPane.repaint();
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