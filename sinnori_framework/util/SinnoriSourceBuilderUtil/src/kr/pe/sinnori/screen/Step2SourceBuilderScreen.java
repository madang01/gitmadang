package kr.pe.sinnori.screen;


import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import kr.pe.sinnori.common.util.SequencedProperties;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class Step2SourceBuilderScreen extends JPanel {
	// private JFrame mainFrame = null;
	// private MainControllerIF mainController = null;
	
	
	
	
	/**
	 * Create the panel.
	 */
	public Step2SourceBuilderScreen(final JFrame mainFrame, MainControllerIF mainController,
			String sinnoriInstallAbsPathName, 
			ArrayList<String> mainProjectList, HashMap<String, SequencedProperties> project2ConfigHash) {
		// this.mainFrame = mainFrame;
		// this.mainController = mainController;
		
				
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.PREF_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		JLabel titleLabel = new JLabel("신놀이 메시지 관련 소스 생성기");
		add(titleLabel, "2, 2");
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, "2, 4, fill, fill");
		
		MultiSourceBuilderScreen multiSourceBuilderPanel = 
				new MultiSourceBuilderScreen(mainFrame, mainController, sinnoriInstallAbsPathName, mainProjectList, project2ConfigHash);
		tabbedPane.addTab("다중 파일 소스빌더", null, multiSourceBuilderPanel, null);
		
		SingleSourceBuilderPanel singleSourceBuilderPanel = new SingleSourceBuilderPanel(mainFrame);
		tabbedPane.addTab("단일 파일 소스빌더", null, singleSourceBuilderPanel, null);
	}
	
}
