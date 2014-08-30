package kr.pe.sinnori.screen;


import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import kr.pe.sinnori.gui.table.MessageInfoFileCellEditor;
import kr.pe.sinnori.gui.table.MessageInfoFileCellRenderer;
import kr.pe.sinnori.gui.table.MessageInfoFileCellValue;
import kr.pe.sinnori.gui.table.SourceBuilderTableModel;
import kr.pe.sinnori.gui.table.SourceFileCellEditor;
import kr.pe.sinnori.gui.table.SourceFileCellRenderer;
import kr.pe.sinnori.gui.table.SourceFileCellValue;
import kr.pe.sinnori.lib.PathSwingAction;


import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Font;

@SuppressWarnings("serial")
public class NoConfigFileScreen extends JPanel implements AllMessageInfoManagerIF {
	private JFrame mainFrame = null;
	// private MainControllerIF mainController = null;
	private JFileChooser chooser = null;
	private JTextField messageInfoPathTextField;
	private JTextField sourceFilePath1TextField;
	private JTextField sourceFilePath2TextField;
	private JTextField sourceFilePath3TextField;
	private JTextField messageNameTextField;
	private JTable table;
	
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
		
		JLabel sourceFilePath1Label = new JLabel("1차 소스 저장 위치");
		sourceFilePath1Label.setFont(UIManager.getFont("Label.font"));
		line02Panel.add(sourceFilePath1Label, "3, 1");
		
		sourceFilePath1TextField = new JTextField();
		line02Panel.add(sourceFilePath1TextField, "6, 1, fill, default");
		sourceFilePath1TextField.setColumns(10);
		
		JButton sourceFilePath1Button = new JButton("경로 찾기");
		sourceFilePath1Button.setAction(new PathSwingAction(mainFrame, chooser, sourceFilePath1TextField));
		line02Panel.add(sourceFilePath1Button, "8, 1");
		
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
		
		JLabel sourceFilePath2Label = new JLabel("2차 소스 저장 위치");
		line03Panel.add(sourceFilePath2Label, "3, 1");
		
		sourceFilePath2TextField = new JTextField();
		line03Panel.add(sourceFilePath2TextField, "6, 1, fill, default");
		sourceFilePath2TextField.setColumns(10);
		
		JButton sourceFilePath2Button = new JButton("경로 찾기");
		sourceFilePath2Button.setAction(new PathSwingAction(mainFrame, chooser, sourceFilePath2TextField));
		line03Panel.add(sourceFilePath2Button, "8, 1");
		
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
		
		JLabel sourceFilePath3Label = new JLabel("3차 소스 저장 위치");
		line04Panel.add(sourceFilePath3Label, "3, 1");
		
		sourceFilePath3TextField = new JTextField();
		line04Panel.add(sourceFilePath3TextField, "6, 1, fill, default");
		sourceFilePath3TextField.setColumns(10);
		
		JButton sourceFilePath3Button = new JButton("경로 찾기");
		sourceFilePath3Button.setAction(new PathSwingAction(mainFrame, chooser, sourceFilePath3TextField));
		line04Panel.add(sourceFilePath3Button, "8, 1");
		
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
		
		Object values[][] = {
				{"Echo", "양방향", new MessageInfoFileCellValue("Echo", mainFrame), new SourceFileCellValue("Echo", this.mainFrame)},
				{"AllDataType", "양방향", new MessageInfoFileCellValue("AllDataType", mainFrame), new SourceFileCellValue("AllDataType", this.mainFrame)},
			};
		
		
		DefaultTableModel dtm  = new SourceBuilderTableModel(values, titles, columnTypes);
			
		table = new JTable();
		table.setRowSelectionAllowed(false);
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
		
		table.setRowHeight(40);
		
		//initColumnSizes(table);
		
		scrollPane.setViewportView(table);
	}
	
	private ProgressMonitor progressMonitor = null;
	
	class AllMessageInfoTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            Object values[][] = new Object[10000][titles.length];
            
            for (int i=0; i < values.length; i++) {
    			String messageID = "Echo"+i;
    			values[i][0] = messageID;
    			values[i][1] = "양방향";
    			values[i][2] = new MessageInfoFileCellValue(messageID, mainFrame);
    			values[i][3] = new SourceFileCellValue(messageID, mainFrame);
    			if (progressMonitor.isCanceled())  {
    				return null;
    			}
    			progressMonitor.setNote(i+"/"+values.length);
    			progressMonitor.setProgress(i*100/values.length);
    		}
            
            DefaultTableModel dtm = new SourceBuilderTableModel(values, titles, columnTypes);
    		dtm.setDataVector(values, columnTypes);
    		
    		table.setModel(dtm);
    		
    		table.getColumnModel().getColumn(2).setCellRenderer(new MessageInfoFileCellRenderer());
    		table.getColumnModel().getColumn(2).setCellEditor(new MessageInfoFileCellEditor(new JCheckBox()));
    		
    		table.getColumnModel().getColumn(3).setCellRenderer(new SourceFileCellRenderer());
    		table.getColumnModel().getColumn(3).setCellEditor(new SourceFileCellEditor(new JCheckBox()));
    		//table.repaint();
    		scrollPane.repaint();
            
            return null;
        }
 
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
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
		/*String messgaeInfoPathText = messageInfoPathTextField.getText();
		if (null == messgaeInfoPathText) return;
		messgaeInfoPathText = messgaeInfoPathText.trim();
		
		File messgaeInfoPath = new File(messgaeInfoPathText);*/
		//Toolkit.getDefaultToolkit().beep();
		
		progressMonitor = new ProgressMonitor(mainFrame, "Running a Task reading message info files", "", 0, 100);
		// progressMonitor.close();
		
		AllMessageInfoTask allMessageInfoTask = new AllMessageInfoTask();
        // task.addPropertyChangeListener(mainFrame);
		allMessageInfoTask.execute();
		
			
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
