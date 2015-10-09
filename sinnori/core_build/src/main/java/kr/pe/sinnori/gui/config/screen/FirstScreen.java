/*
 * Created by JFormDesigner on Sat Nov 29 11:23:08 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.gui.config.buildsystem.MainProjectInformation;
import kr.pe.sinnori.gui.config.buildsystem.MainProjectInformationManger;
import kr.pe.sinnori.gui.config.lib.WindowManger;
import kr.pe.sinnori.gui.util.PathSwingAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class FirstScreen extends JPanel {
	private Logger log = LoggerFactory.getLogger(FirstScreen.class);
	private JFrame mainFrame = null;
	private JFileChooser chooser = null;
	private MainProjectInformationManger mainProjectInformationManger = null;
	
	
	public FirstScreen() {
		initComponents();
		
	}
	public FirstScreen(JFrame mainFrame) {
		this.mainFrame = mainFrame;	
		initComponents();
	}

	private void mainProjectNameEditButtonActionPerformed(ActionEvent e) {
		if (mainProjectNameListComboBox.getSelectedIndex() > 0) {
			String mainProjectName = (String)mainProjectNameListComboBox.getSelectedItem();
			MainProjectInformation selectedMainProjectInformation = mainProjectInformationManger.getMainProjectInformation(mainProjectName);
			if (null == selectedMainProjectInformation) {
				JOptionPane.showMessageDialog(mainFrame, "프로젝트를 얻는데 실패하였습니다.");
				sinnoriInstalledPathInputTextField.requestFocusInWindow();
				return;
			}
			
			projectNameValueLabel.setText(selectedMainProjectInformation.getMainProjectName());
			// serverCheckBox.setSelected(selectedProject.get);
			appClientCheckBox.setSelected(selectedMainProjectInformation.isAppClient());
			webClientCheckBox.setSelected(selectedMainProjectInformation.isWebClient());
			
			try {
				WindowManger.getInstance().changeFirstScreenToProjectEditScreen(selectedMainProjectInformation);
			} catch (ConfigErrorException e1) {
				String errorMessage = String.format("fail to change the edit screen of project[%s],\nerrormessage=%s", 
						selectedMainProjectInformation.getMainProjectName(), e1.getMessage());
				log.warn(errorMessage);
				
				mainProjectNameListComboBox.requestFocusInWindow();
				JOptionPane.showMessageDialog(mainFrame, errorMessage);			
				return;
			}
		}
	}

	private void sinnoriInstalledPathAnalysisButtonActionPerformed(ActionEvent e) {
		String sinnoriInstalledPathString = sinnoriInstalledPathInputTextField.getText();
		if ( null == sinnoriInstalledPathString) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로를 입력해 주세요.");
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		sinnoriInstalledPathString = sinnoriInstalledPathString.trim();
		sinnoriInstalledPathInputTextField.setText(sinnoriInstalledPathString);
		
		if (sinnoriInstalledPathString.equals("")) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로를 다시 입력해 주세요.");
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = String.format("신놀이 설치 경로[%s] 가 존재하지 않습니다.", sinnoriInstalledPathString);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = String.format("신놀이 설치 경로[%s] 가 디렉토리가 아닙니다.", sinnoriInstalledPathString);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.canRead()) {
			String errorMessage = String.format("신놀이 설치 경로[%s] 에 대한 읽기 권한이 없습니다.", sinnoriInstalledPathString);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.canWrite()) {
			String errorMessage = String.format("신놀이 설치 경로[%s] 에 대한 쓰기 권한이 없습니다.", sinnoriInstalledPathString);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		try {
			sinnoriInstalledPathString = sinnoriInstalledPath.getCanonicalFile().getAbsolutePath();			
			sinnoriInstalledPathInputTextField.setText(sinnoriInstalledPathString);
		} catch (IOException e1) {
			String errorMessage = String.format("신놀이 설치 경로[%s]를 시스템 절대 경로로 변경하는데 실패하였습니다.", sinnoriInstalledPathString);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}
		
		
		
		try {
			mainProjectInformationManger = MainProjectInformationManger.getInstance(sinnoriInstalledPathString);
		} catch (ConfigErrorException e2) {
			JOptionPane.showMessageDialog(mainFrame, e2.getMessage());
			sinnoriInstalledPathInputTextField.requestFocusInWindow();
			return;
		}		
		
		mainProjectNameListComboBox.removeAllItems();
		mainProjectNameListComboBox.addItem("- project -");
		
		List<MainProjectInformation> mainProjectInformationList = mainProjectInformationManger.getMainProjectInformationList();
		for (MainProjectInformation mainProjectInformation : mainProjectInformationList) {
			mainProjectNameListComboBox.addItem(mainProjectInformation.getMainProjectName());
		}
		
		
		sinnoriInstalledPathInfoValueLabel.setText(sinnoriInstalledPathString);
		allProjectInstalledPathDevEnvUpdateButton.setEnabled(true);
		mainProjectNameTextField.setEnabled(true);
		projectNameAddButton.setEnabled(true);
		
		mainProjectNameListComboBox.setEnabled(true);
		mainProjectNameEditButton.setEnabled(true);
		mainProjectNameDeleteButton.setEnabled(true);
	}
	
	private void mainProjectNameListComboBoxItemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			
			if (mainProjectNameListComboBox.getSelectedIndex() > 0) {
				String mainProjectName = (String)e.getItem();
				MainProjectInformation selectedMainProjectInformation = mainProjectInformationManger.getMainProjectInformation(mainProjectName);
				if (null == selectedMainProjectInformation) {
					JOptionPane.showMessageDialog(mainFrame, "selectedProject is null");
					sinnoriInstalledPathInputTextField.requestFocusInWindow();
					return;
				}
				
				projectNameValueLabel.setText(selectedMainProjectInformation.getMainProjectName());
				appClientCheckBox.setSelected(selectedMainProjectInformation.isAppClient());
				webClientCheckBox.setSelected(selectedMainProjectInformation.isWebClient());
				//try {
					servletEnginLibinaryPathTextField.setText(selectedMainProjectInformation.getServletSystemLibrayPathString());
				/*} catch (ConfigErrorException e1) {
					log.warn("fail to get the value of 'tomcat.servletlib' in ant.properties", e1);
				}*/
			} else {
				projectNameValueLabel.setText("");
				servletEnginLibinaryPathTextField.setText("");
			}
		}
	}

	private void projectNameAddButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
		log.info("start");
		String newMainProjectName = mainProjectNameTextField.getText();
		if (null == newMainProjectName) {
			String errorMessage = "신규 메인 프로젝트 이름을 넣어 주세요.";
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}
		newMainProjectName = newMainProjectName.trim();
		mainProjectNameTextField.setText(newMainProjectName);
		if (newMainProjectName.equals("")) {
			String errorMessage = "신규 메인 프로젝트 이름을 다시 넣어 주세요.";
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}
		
		String newMainProjectNameTrim = newMainProjectName.trim();
		
		if (!newMainProjectName.equals(newMainProjectNameTrim)) {
			String errorMessage = "신규 메인 프로젝트 이름에 앞뒤로 공백을 넣을 수 없습니다.";
			log.warn(errorMessage);
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}

		try {
			// mainProjectInformationManger.addNewMainProject(newMainProjectName, servletEnginLibinaryPathTextField.getText());
			mainProjectInformationManger.addNewMainProject(newMainProjectName);
		} catch (ConfigErrorException e1) {
			String errorMessage = new StringBuilder("메인 프로젝트[")
			.append(newMainProjectName).append("] 신규 추가 실패::").append(e1.getMessage()).toString();
			log.warn(errorMessage);
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}
		
		mainProjectNameListComboBox.addItem(newMainProjectName);
		
		JOptionPane.showMessageDialog(mainFrame, "프로젝트 이름 추가 성공");
	}

	private void mainProjectNameDeleteButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
		int selectedIndex = mainProjectNameListComboBox.getSelectedIndex();
		if (0 == selectedIndex) {
			String errorMessage = String.format("메인 프로젝트를 선택해 주세요.");
			log.warn(errorMessage);
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}
		String selectedProjectName = (String)mainProjectNameListComboBox.getSelectedItem();
		try {
			mainProjectInformationManger.deleteMainProject(selectedProjectName);
		} catch (ConfigErrorException e1) {
			String errorMessage = new StringBuilder("선택한 메인 프로젝트[")
			.append(selectedProjectName).append("] 삭제 실패::").append(e1.getMessage()).toString();
			log.warn(errorMessage);
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
		}
		
		mainProjectNameListComboBox.removeItem(selectedProjectName);
	}

	/**
	 * 신놀이 전체 프로젝트들의 개발 환경을 설치 경로 기준으로 재 구축한다.
	 * @param e
	 */
	private void developmentEnvoromnetRenewButtonActionPerformed(ActionEvent e) {
		List<MainProjectInformation> mainProjectInformationList = mainProjectInformationManger.getMainProjectInformationList();
		for (MainProjectInformation mainProjectInformation : mainProjectInformationList) {
			try {
				mainProjectInformation.updateInformationBasedOnBuildSystem();
			} catch (ConfigErrorException e1) {
				String errorMessage = new StringBuilder("fail to update project[")
				.append(mainProjectInformation.getMainProjectName()).append("]'s configuration file based on installed path::").append(e1.getMessage()).toString();
				log.warn(errorMessage);
				allProjectInstalledPathDevEnvUpdateButton.requestFocusInWindow();
				JOptionPane.showMessageDialog(mainFrame, errorMessage);			
				return;
			}
		}
		
		
		JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로 기준으로 전체 프로젝트 개발환경 보정하기 완료");
	}
	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sinnoriInstalledPathInputLinePanel = new JPanel();
		sinnoriInstalledPathInputLabel = new JLabel();
		sinnoriInstalledPathInputTextField = new JTextField();
		sinnoriInstalledPathInputButton = new JButton();
		sinnoriInstalledPathAnalysisLinePanel = new JPanel();
		sinnoriInstalledPathAnalysisButton = new JButton();
		hSpacer1 = new JPanel(null);
		sinnoriInstalledPathInfoLinePanel = new JPanel();
		sinnoriInstalledPathInfoTitleLabel = new JLabel();
		sinnoriInstalledPathInfoValueLabel = new JLabel();
		allProjectWorkSaveLinePanel = new JPanel();
		allProjectInstalledPathDevEnvUpdateButton = new JButton();
		projectNameInputLinePanel = new JPanel();
		mainProjectNameLabel = new JLabel();
		mainProjectNameTextField = new JTextField();
		projectNameAddButton = new JButton();
		projectListLinePanel = new JPanel();
		mainProjectListLabel = new JLabel();
		projectListFuncPanel = new JPanel();
		mainProjectNameListComboBox = new JComboBox<>();
		mainProjectNameEditButton = new JButton();
		mainProjectNameDeleteButton = new JButton();
		hSpacer2 = new JPanel(null);
		projectNameLinePanel = new JPanel();
		projectNameTitleLabel = new JLabel();
		projectNameValueLabel = new JLabel();
		projectStructLinePanel = new JPanel();
		projectStructLabel = new JLabel();
		projectStructFuncPanel = new JPanel();
		serverCheckBox = new JCheckBox();
		appClientCheckBox = new JCheckBox();
		webClientCheckBox = new JCheckBox();
		servletEnginLibinaryPathLinePanel = new JPanel();
		servletEnginLibinaryPathLabel = new JLabel();
		servletEnginLibinaryPathTextField = new JTextField();
		projectConfigVeiwLinePanel = new JPanel();
		projectConfigVeiwButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"${growing-button}",
			"18dlu, 2*($lgap, default), $lgap, 13dlu, 8*($lgap, default)"));
		/** Post-initialization Code start */
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, chooser, sinnoriInstalledPathInputTextField);
		sinnoriInstalledPathInputButton.setAction(pathAction);
		allProjectInstalledPathDevEnvUpdateButton.setToolTipText(
						"<html>update all project'config and overwrite all project'shells based on installed path</html>");
		/** Post-initialization Code end */

		//======== sinnoriInstalledPathInputLinePanel ========
		{
			sinnoriInstalledPathInputLinePanel.setLayout(new FormLayout(
				"55dlu, $lcgap, ${growing-button}, $lcgap, 52dlu",
				"default"));

			//---- sinnoriInstalledPathInputLabel ----
			sinnoriInstalledPathInputLabel.setText("\uc2e0\ub180\uc774 \uc124\uce58 \uacbd\ub85c");
			sinnoriInstalledPathInputLinePanel.add(sinnoriInstalledPathInputLabel, CC.xy(1, 1));

			//---- sinnoriInstalledPathInputTextField ----
			sinnoriInstalledPathInputTextField.setText("d:\\gitsinnori\\sinnori");
			sinnoriInstalledPathInputLinePanel.add(sinnoriInstalledPathInputTextField, CC.xy(3, 1));

			//---- sinnoriInstalledPathInputButton ----
			sinnoriInstalledPathInputButton.setText("\uacbd\ub85c \uc120\ud0dd");
			sinnoriInstalledPathInputLinePanel.add(sinnoriInstalledPathInputButton, CC.xy(5, 1));
		}
		add(sinnoriInstalledPathInputLinePanel, CC.xy(1, 1));

		//======== sinnoriInstalledPathAnalysisLinePanel ========
		{
			sinnoriInstalledPathAnalysisLinePanel.setLayout(new BoxLayout(sinnoriInstalledPathAnalysisLinePanel, BoxLayout.X_AXIS));

			//---- sinnoriInstalledPathAnalysisButton ----
			sinnoriInstalledPathAnalysisButton.setText("\ud504\ub85c\uc81d\ud2b8 \uc815\ubcf4 \ucd94\ucd9c\ud558\uae30");
			sinnoriInstalledPathAnalysisButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sinnoriInstalledPathAnalysisButtonActionPerformed(e);
				}
			});
			sinnoriInstalledPathAnalysisLinePanel.add(sinnoriInstalledPathAnalysisButton);
		}
		add(sinnoriInstalledPathAnalysisLinePanel, CC.xy(1, 3));

		//---- hSpacer1 ----
		hSpacer1.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer1, CC.xy(1, 5));

		//======== sinnoriInstalledPathInfoLinePanel ========
		{
			sinnoriInstalledPathInfoLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 317dlu",
				"default"));

			//---- sinnoriInstalledPathInfoTitleLabel ----
			sinnoriInstalledPathInfoTitleLabel.setText("\uc2e0\ub180\uc774 \uc124\uce58 \uacbd\ub85c :");
			sinnoriInstalledPathInfoLinePanel.add(sinnoriInstalledPathInfoTitleLabel, CC.xy(1, 1));
			sinnoriInstalledPathInfoLinePanel.add(sinnoriInstalledPathInfoValueLabel, CC.xy(3, 1));
		}
		add(sinnoriInstalledPathInfoLinePanel, CC.xy(1, 7));

		//======== allProjectWorkSaveLinePanel ========
		{
			allProjectWorkSaveLinePanel.setLayout(new BoxLayout(allProjectWorkSaveLinePanel, BoxLayout.X_AXIS));

			//---- allProjectInstalledPathDevEnvUpdateButton ----
			allProjectInstalledPathDevEnvUpdateButton.setText("\uc2e0\ub180\uc774 \uc124\uce58 \uacbd\ub85c \uae30\uc900\uc73c\ub85c \uc804\uccb4 \ud504\ub85c\uc81d\ud2b8 \uac1c\ubc1c\ud658\uacbd \ubcf4\uc815\ud558\uae30");
			allProjectInstalledPathDevEnvUpdateButton.setEnabled(false);
			allProjectInstalledPathDevEnvUpdateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					developmentEnvoromnetRenewButtonActionPerformed(e);
				}
			});
			allProjectWorkSaveLinePanel.add(allProjectInstalledPathDevEnvUpdateButton);
		}
		add(allProjectWorkSaveLinePanel, CC.xy(1, 9));

		//======== projectNameInputLinePanel ========
		{
			projectNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- mainProjectNameLabel ----
			mainProjectNameLabel.setText("\uba54\uc778 \ud504\ub85c\uc81d\ud2b8 \uc774\ub984 :");
			projectNameInputLinePanel.add(mainProjectNameLabel, CC.xy(1, 1));

			//---- mainProjectNameTextField ----
			mainProjectNameTextField.setEnabled(false);
			projectNameInputLinePanel.add(mainProjectNameTextField, CC.xy(3, 1));

			//---- projectNameAddButton ----
			projectNameAddButton.setText("\ucd94\uac00");
			projectNameAddButton.setEnabled(false);
			projectNameAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					projectNameAddButtonActionPerformed(e);
				}
			});
			projectNameInputLinePanel.add(projectNameAddButton, CC.xy(5, 1));
		}
		add(projectNameInputLinePanel, CC.xy(1, 11));

		//======== projectListLinePanel ========
		{
			projectListLinePanel.setLayout(new FormLayout(
				"default, $lcgap, default",
				"default"));

			//---- mainProjectListLabel ----
			mainProjectListLabel.setText("\uc0dd\uc131\ub41c \uba54\uc778 \ud504\ub85c\uc81d\ud2b8 \ubaa9\ub85d");
			projectListLinePanel.add(mainProjectListLabel, CC.xy(1, 1));

			//======== projectListFuncPanel ========
			{
				projectListFuncPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

				//---- mainProjectNameListComboBox ----
				mainProjectNameListComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"- project -"
				}));
				mainProjectNameListComboBox.setEnabled(false);
				mainProjectNameListComboBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						mainProjectNameListComboBoxItemStateChanged(e);
					}
				});
				projectListFuncPanel.add(mainProjectNameListComboBox);

				//---- mainProjectNameEditButton ----
				mainProjectNameEditButton.setText("\ud3b8\uc9d1");
				mainProjectNameEditButton.setEnabled(false);
				mainProjectNameEditButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						mainProjectNameEditButtonActionPerformed(e);
					}
				});
				projectListFuncPanel.add(mainProjectNameEditButton);

				//---- mainProjectNameDeleteButton ----
				mainProjectNameDeleteButton.setText("\uc0ad\uc81c");
				mainProjectNameDeleteButton.setEnabled(false);
				mainProjectNameDeleteButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						mainProjectNameDeleteButtonActionPerformed(e);
					}
				});
				projectListFuncPanel.add(mainProjectNameDeleteButton);
			}
			projectListLinePanel.add(projectListFuncPanel, CC.xy(3, 1));
		}
		add(projectListLinePanel, CC.xy(1, 13));

		//---- hSpacer2 ----
		hSpacer2.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer2, CC.xy(1, 15));

		//======== projectNameLinePanel ========
		{
			projectNameLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 330dlu",
				"default"));

			//---- projectNameTitleLabel ----
			projectNameTitleLabel.setText("\ud504\ub85c\uc81d\ud2b8 \uc774\ub984 :");
			projectNameLinePanel.add(projectNameTitleLabel, CC.xy(1, 1));
			projectNameLinePanel.add(projectNameValueLabel, CC.xy(3, 1));
		}
		add(projectNameLinePanel, CC.xy(1, 17));

		//======== projectStructLinePanel ========
		{
			projectStructLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 330dlu",
				"default"));

			//---- projectStructLabel ----
			projectStructLabel.setText("\ud504\ub85c\uc81d\ud2b8 \uad6c\uc131 :");
			projectStructLinePanel.add(projectStructLabel, CC.xy(1, 1));

			//======== projectStructFuncPanel ========
			{
				projectStructFuncPanel.setLayout(new BoxLayout(projectStructFuncPanel, BoxLayout.X_AXIS));

				//---- serverCheckBox ----
				serverCheckBox.setText("\uc11c\ubc84");
				serverCheckBox.setEnabled(false);
				serverCheckBox.setSelected(true);
				projectStructFuncPanel.add(serverCheckBox);

				//---- appClientCheckBox ----
				appClientCheckBox.setText("\uc751\uc6a9 \ud074\ub77c\uc774\uc5b8\ud2b8");
				appClientCheckBox.setEnabled(false);
				appClientCheckBox.setSelected(true);
				projectStructFuncPanel.add(appClientCheckBox);

				//---- webClientCheckBox ----
				webClientCheckBox.setText("\uc6f9 \ud074\ub77c\uc774\uc5b8\ud2b8");
				webClientCheckBox.setEnabled(false);
				webClientCheckBox.setSelected(true);
				projectStructFuncPanel.add(webClientCheckBox);
			}
			projectStructLinePanel.add(projectStructFuncPanel, CC.xy(3, 1));
		}
		add(projectStructLinePanel, CC.xy(1, 19));

		//======== servletEnginLibinaryPathLinePanel ========
		{
			servletEnginLibinaryPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}",
				"default"));

			//---- servletEnginLibinaryPathLabel ----
			servletEnginLibinaryPathLabel.setText("\uc11c\ube14\ub9bf \uc5d4\uc9c4 \ub77c\uc774\ube0c\ub7ec\ub9ac \uacbd\ub85c :");
			servletEnginLibinaryPathLinePanel.add(servletEnginLibinaryPathLabel, CC.xy(1, 1));

			//---- servletEnginLibinaryPathTextField ----
			servletEnginLibinaryPathTextField.setEditable(false);
			servletEnginLibinaryPathLinePanel.add(servletEnginLibinaryPathTextField, CC.xy(3, 1));
		}
		add(servletEnginLibinaryPathLinePanel, CC.xy(1, 21));

		//======== projectConfigVeiwLinePanel ========
		{
			projectConfigVeiwLinePanel.setLayout(new BoxLayout(projectConfigVeiwLinePanel, BoxLayout.X_AXIS));

			//---- projectConfigVeiwButton ----
			projectConfigVeiwButton.setText("\uc124\uc815 \ud30c\uc77c \ub0b4\uc6a9 \ubcf4\uae30");
			projectConfigVeiwButton.setEnabled(false);
			projectConfigVeiwLinePanel.add(projectConfigVeiwButton);
		}
		add(projectConfigVeiwLinePanel, CC.xy(1, 23));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		
		// Logger.getGlobal().info("call");
		
		
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel sinnoriInstalledPathInputLinePanel;
	private JLabel sinnoriInstalledPathInputLabel;
	private JTextField sinnoriInstalledPathInputTextField;
	private JButton sinnoriInstalledPathInputButton;
	private JPanel sinnoriInstalledPathAnalysisLinePanel;
	private JButton sinnoriInstalledPathAnalysisButton;
	private JPanel hSpacer1;
	private JPanel sinnoriInstalledPathInfoLinePanel;
	private JLabel sinnoriInstalledPathInfoTitleLabel;
	private JLabel sinnoriInstalledPathInfoValueLabel;
	private JPanel allProjectWorkSaveLinePanel;
	private JButton allProjectInstalledPathDevEnvUpdateButton;
	private JPanel projectNameInputLinePanel;
	private JLabel mainProjectNameLabel;
	private JTextField mainProjectNameTextField;
	private JButton projectNameAddButton;
	private JPanel projectListLinePanel;
	private JLabel mainProjectListLabel;
	private JPanel projectListFuncPanel;
	private JComboBox<String> mainProjectNameListComboBox;
	private JButton mainProjectNameEditButton;
	private JButton mainProjectNameDeleteButton;
	private JPanel hSpacer2;
	private JPanel projectNameLinePanel;
	private JLabel projectNameTitleLabel;
	private JLabel projectNameValueLabel;
	private JPanel projectStructLinePanel;
	private JLabel projectStructLabel;
	private JPanel projectStructFuncPanel;
	private JCheckBox serverCheckBox;
	private JCheckBox appClientCheckBox;
	private JCheckBox webClientCheckBox;
	private JPanel servletEnginLibinaryPathLinePanel;
	private JLabel servletEnginLibinaryPathLabel;
	private JTextField servletEnginLibinaryPathTextField;
	private JPanel projectConfigVeiwLinePanel;
	private JButton projectConfigVeiwButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
