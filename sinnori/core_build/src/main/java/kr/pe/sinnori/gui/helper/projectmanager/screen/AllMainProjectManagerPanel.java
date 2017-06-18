/*
 * Created by JFormDesigner on Sat Nov 29 11:23:08 KST 2014
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.buildsystem.BuildSystemSupporter;
import kr.pe.sinnori.common.buildsystem.MainProjectBuildSystemState;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class AllMainProjectManagerPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(AllMainProjectManagerPanel.class);

	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;

	public AllMainProjectManagerPanel() {
		initComponents();
	}

	public AllMainProjectManagerPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		

		initComponents();
	}

	public void setScreen(String sinnoriInstalledPathString) {
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);

		File projectBasePath = new File(projectBasePathString);
		if (!projectBasePath.exists()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't exist",
					sinnoriInstalledPathString, projectBasePathString);

			log.warn(errorMessage);

			showMessageDialog(errorMessage);
			return;
		}

		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] is not a direcotry",
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);

			showMessageDialog(errorMessage);
			return;
		}

		if (!projectBasePath.canRead()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't hava permission to read",
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);

			showMessageDialog(errorMessage);
			return;
		}

		List<String> mainProjectNameList = new ArrayList<String>();

		for (File fileOfList : projectBasePath.listFiles()) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format(
							"the sinnori project base path[%s] doesn't hava permission to read",
							fileOfList.getAbsolutePath());
					log.warn(errorMessage);

					showMessageDialog(errorMessage);
					return;
				}

				if (!fileOfList.canWrite()) {
					String errorMessage = String.format(
							"the sinnori project base path[%s] doesn't hava permission to write",
							fileOfList.getAbsolutePath());
					log.warn(errorMessage);

					showMessageDialog(errorMessage);
					return;
				}

				mainProjectNameList.add(fileOfList.getName());
			}
		}

		mainProjectNameListComboBox.removeAllItems();
		mainProjectNameListComboBox.addItem("- project -");

		for (String mainProjectName : mainProjectNameList) {
			mainProjectNameListComboBox.addItem(mainProjectName);
		}

		sinnoriInstalledPathInfoValueLabel.setText(sinnoriInstalledPathString);
		mainProjecNameListUpdatetButton.setEnabled(true);
		mainProjectNameTextField.setEnabled(true);
		projectNameAddButton.setEnabled(true);

		mainProjectNameListComboBox.setEnabled(true);
		mainProjectNameEditButton.setEnabled(true);
		mainProjectNameDeleteButton.setEnabled(true);
		applySinnoriInstalledPathButton.setEnabled(true);
	}

	private void mainProjectEditButtonActionPerformed(ActionEvent e) {
		if (mainProjectNameListComboBox.getSelectedIndex() > 0) {
			String mainProjectName = (String) mainProjectNameListComboBox.getSelectedItem();
			String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();

			MainProjectBuildSystemState selectedMainProjectBuildSystemState = null;

			try {
				
				selectedMainProjectBuildSystemState = BuildSystemSupporter.getMainProjectBuildSystemState(sinnoriInstalledPathString, mainProjectName);
				
			} catch (BuildSystemException e1) {
				log.warn("fail to load main project build system state", e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				return;
			}
			

			projectNameValueLabel.setText(selectedMainProjectBuildSystemState.getMainProjectName());
			appClientCheckBox.setSelected(selectedMainProjectBuildSystemState.isAppClient());
			webClientCheckBox.setSelected(selectedMainProjectBuildSystemState.isWebClient());			
			servletEnginLibinaryPathTextField.setText(selectedMainProjectBuildSystemState.getServletSystemLibrayPathString());

			screenManagerIF.moveToMainProjectEditScreen(selectedMainProjectBuildSystemState);
		}
	}

	private void mainProjectNameListComboBoxItemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {

			if (mainProjectNameListComboBox.getSelectedIndex() > 0) {
				String mainProjectName = (String) e.getItem();
				String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();

				MainProjectBuildSystemState mainProjectBuildSystemState = null;

				try {					
					mainProjectBuildSystemState = BuildSystemSupporter.getMainProjectBuildSystemState(sinnoriInstalledPathString, mainProjectName);
				} catch (BuildSystemException e2) {
					log.warn("fail to load main project build system state", e2);
					mainProjectNameListComboBox.setSelectedIndex(0);
					showMessageDialog(e2.getMessage());
					return;
				}

				projectNameValueLabel.setText(mainProjectBuildSystemState.getMainProjectName());
				serverCheckBox.setSelected(mainProjectBuildSystemState.isServer());
				appClientCheckBox.setSelected(mainProjectBuildSystemState.isAppClient());
				webClientCheckBox.setSelected(mainProjectBuildSystemState.isWebClient());
						
				servletEnginLibinaryPathTextField
						.setText(mainProjectBuildSystemState.getServletSystemLibrayPathString());
			} else {
				projectNameValueLabel.setText("");
				servletEnginLibinaryPathTextField.setText("");
			}
		}
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame,
				CommonStaticUtil.splitString(message, CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}

	private void projectNameAddButtonActionPerformed(ActionEvent e) {
		// log.info("start");
		String newMainProjectName = mainProjectNameTextField.getText();
		if (null == newMainProjectName) {
			String errorMessage = "Please input new main project name";
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}

		if (newMainProjectName.equals("")) {
			String errorMessage = "The new main project name is a empsty string";
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(newMainProjectName)) {
			String errorMessage = "The new main project name has leading or tailing white space";
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}

		String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = false;
		String servletSystemLibrayPathString = "";

		try {
			BuildSystemSupporter.createNewMainProjectBuildSystem(sinnoriInstalledPathString, newMainProjectName,
					isServer,  isAppClient,
					isWebClient, servletSystemLibrayPathString);
		} catch (IllegalArgumentException | BuildSystemException e1) {
			String errorMessage = "fail to create new main project build system";
			log.warn(errorMessage, e1);
			mainProjectNameTextField.requestFocusInWindow();

			showMessageDialog(
					new StringBuilder(errorMessage).append(", errormessage=").append(e1.getMessage()).toString());
			return;
		}

		mainProjectNameListComboBox.addItem(newMainProjectName);

		JOptionPane.showMessageDialog(mainFrame, "프로젝트 이름 추가 성공");
	}

	private void mainProjectNameDeleteButtonActionPerformed(ActionEvent e) {
		int selectedIndex = mainProjectNameListComboBox.getSelectedIndex();
		if (0 == selectedIndex) {
			String errorMessage = "메인 프로젝트를 선택해 주세요.";
			log.warn(errorMessage);
			mainProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		String selectedProjectName = (String) mainProjectNameListComboBox.getSelectedItem();

		String message = "Do you really delete main project[" + selectedProjectName + "]";
		String title = "main project deletion choice";
		int answer = JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.OK_OPTION) {
			mainProjectNameListComboBox.setSelectedIndex(0);
			
			String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();
			try {
				BuildSystemSupporter.dropProject(sinnoriInstalledPathString, selectedProjectName);
			} catch (BuildSystemException e1) {
				log.warn("fail to delete main project directory", e1);
				showMessageDialog(e1.getMessage());
				return;
			}
			mainProjectNameListComboBox.removeItem(selectedProjectName);
		}
	}

	/**
	 * 프로젝트 경로에 있는 프로젝트들 목록을 재 구성한다.
	 */
	private void mainProjectNameListUpdateButtonActionPerformed(ActionEvent e) {
		String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);

		File projectBasePath = new File(projectBasePathString);
		if (!projectBasePath.exists()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't exist",
					sinnoriInstalledPathString, projectBasePathString);
			showMessageDialog(errorMessage);
			return;
		}

		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] is not a direcotry",
					sinnoriInstalledPathString, projectBasePathString);
			showMessageDialog(errorMessage);
			return;
		}

		if (!projectBasePath.canRead()) {
			String errorMessage = String.format(
					"the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't hava permission to read",
					sinnoriInstalledPathString, projectBasePathString);
			showMessageDialog(errorMessage);
			return;
		}

		mainProjectNameListComboBox.removeAllItems();
		mainProjectNameListComboBox.addItem("- project -");

		for (File fileOfList : projectBasePath.listFiles()) {
			if (fileOfList.isDirectory()) {
				mainProjectNameListComboBox.addItem(fileOfList.getName());
			}
		}

		JOptionPane.showMessageDialog(mainFrame, "프로젝트 경로에 있는 프로젝트들 목록 갱신 완료");
	}

	private void applySinnoriInstalledPathToAllProject(ActionEvent e) {
		int itemCount = mainProjectNameListComboBox.getItemCount();
		if (itemCount <= 1) {
			showMessageDialog("Any main project doesn't exist");
			return;
		}

		String sinnoriInstalledPathString = sinnoriInstalledPathInfoValueLabel.getText();
		
		for (int i = 1; i < itemCount; i++) {
			String mainProjectName = mainProjectNameListComboBox.getItemAt(i);
			
			try {
				
				BuildSystemSupporter.applySinnoriInstalledPath(sinnoriInstalledPathString, mainProjectName);
			} catch (BuildSystemException e1) {
				log.warn(e1.getMessage(), e1);
				showMessageDialog(e1.getMessage());
				return;
			}
		}

		showMessageDialog("success to apply Sinnori installed path to all project");
	}

	private void firstScreenMoveButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sinnoriInstalledPathInfoLinePanel = new JPanel();
		sinnoriInstalledPathInfoTitleLabel = new JLabel();
		sinnoriInstalledPathInfoValueLabel = new JLabel();
		allProjectWorkSaveLinePanel = new JPanel();
		mainProjecNameListUpdatetButton = new JButton();
		applySinnoriInstalledPathButton = new JButton();
		prevButton = new JButton();
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
			"$ugap, ${growing-button}, $ugap",
			"$ugap, 4*(20dlu, $lgap), min, 4*($lgap, [20dlu,default]), $ugap"));

		//======== sinnoriInstalledPathInfoLinePanel ========
		{
			sinnoriInstalledPathInfoLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 317dlu",
				"default"));

			//---- sinnoriInstalledPathInfoTitleLabel ----
			sinnoriInstalledPathInfoTitleLabel.setText("Sinnori installed path :");
			sinnoriInstalledPathInfoLinePanel.add(sinnoriInstalledPathInfoTitleLabel, CC.xy(1, 1));
			sinnoriInstalledPathInfoLinePanel.add(sinnoriInstalledPathInfoValueLabel, CC.xy(3, 1));
		}
		add(sinnoriInstalledPathInfoLinePanel, CC.xy(2, 2));

		//======== allProjectWorkSaveLinePanel ========
		{
			allProjectWorkSaveLinePanel.setLayout(new FormLayout(
				"default, $ugap, default, $lcgap, default",
				"default:grow"));

			//---- mainProjecNameListUpdatetButton ----
			mainProjecNameListUpdatetButton.setText("update main project name list");
			mainProjecNameListUpdatetButton.setEnabled(false);
			mainProjecNameListUpdatetButton.setToolTipText("This button updates main project name list that is child direcotris of project base path");
			mainProjecNameListUpdatetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainProjectNameListUpdateButtonActionPerformed(e);
				}
			});
			allProjectWorkSaveLinePanel.add(mainProjecNameListUpdatetButton, CC.xy(1, 1));

			//---- applySinnoriInstalledPathButton ----
			applySinnoriInstalledPathButton.setText("apply Sinnori installed path to all project");
			applySinnoriInstalledPathButton.setEnabled(false);
			applySinnoriInstalledPathButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					applySinnoriInstalledPathToAllProject(e);
				}
			});
			allProjectWorkSaveLinePanel.add(applySinnoriInstalledPathButton, CC.xy(3, 1));

			//---- prevButton ----
			prevButton.setText("go to first screen");
			prevButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					firstScreenMoveButtonActionPerformed(e);
				}
			});
			allProjectWorkSaveLinePanel.add(prevButton, CC.xy(5, 1));
		}
		add(allProjectWorkSaveLinePanel, CC.xy(2, 4));

		//======== projectNameInputLinePanel ========
		{
			projectNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- mainProjectNameLabel ----
			mainProjectNameLabel.setText("New main project name :");
			mainProjectNameLabel.setToolTipText("new main project name that you want to add");
			projectNameInputLinePanel.add(mainProjectNameLabel, CC.xy(1, 1));

			//---- mainProjectNameTextField ----
			mainProjectNameTextField.setEnabled(false);
			projectNameInputLinePanel.add(mainProjectNameTextField, CC.xy(3, 1));

			//---- projectNameAddButton ----
			projectNameAddButton.setText("add");
			projectNameAddButton.setEnabled(false);
			projectNameAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					projectNameAddButtonActionPerformed(e);
				}
			});
			projectNameInputLinePanel.add(projectNameAddButton, CC.xy(5, 1));
		}
		add(projectNameInputLinePanel, CC.xy(2, 6));

		//======== projectListLinePanel ========
		{
			projectListLinePanel.setLayout(new FormLayout(
				"default, $lcgap, default",
				"min"));

			//---- mainProjectListLabel ----
			mainProjectListLabel.setText("Main project choose");
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
				mainProjectNameEditButton.setText("edit");
				mainProjectNameEditButton.setEnabled(false);
				mainProjectNameEditButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						mainProjectEditButtonActionPerformed(e);
					}
				});
				projectListFuncPanel.add(mainProjectNameEditButton);

				//---- mainProjectNameDeleteButton ----
				mainProjectNameDeleteButton.setText("delete");
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
		add(projectListLinePanel, CC.xy(2, 8));

		//---- hSpacer2 ----
		hSpacer2.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer2, CC.xy(2, 10));

		//======== projectNameLinePanel ========
		{
			projectNameLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 330dlu",
				"default"));

			//---- projectNameTitleLabel ----
			projectNameTitleLabel.setText("Main project name :");
			projectNameLinePanel.add(projectNameTitleLabel, CC.xy(1, 1));
			projectNameLinePanel.add(projectNameValueLabel, CC.xy(3, 1));
		}
		add(projectNameLinePanel, CC.xy(2, 12));

		//======== projectStructLinePanel ========
		{
			projectStructLinePanel.setLayout(new FormLayout(
				"default, $lcgap, 330dlu",
				"default"));

			//---- projectStructLabel ----
			projectStructLabel.setText("Project build type choose :");
			projectStructLinePanel.add(projectStructLabel, CC.xy(1, 1));

			//======== projectStructFuncPanel ========
			{
				projectStructFuncPanel.setLayout(new BoxLayout(projectStructFuncPanel, BoxLayout.X_AXIS));

				//---- serverCheckBox ----
				serverCheckBox.setText("server");
				serverCheckBox.setEnabled(false);
				serverCheckBox.setSelected(true);
				projectStructFuncPanel.add(serverCheckBox);

				//---- appClientCheckBox ----
				appClientCheckBox.setText("application client");
				appClientCheckBox.setEnabled(false);
				appClientCheckBox.setSelected(true);
				projectStructFuncPanel.add(appClientCheckBox);

				//---- webClientCheckBox ----
				webClientCheckBox.setText("web client");
				webClientCheckBox.setEnabled(false);
				webClientCheckBox.setSelected(true);
				projectStructFuncPanel.add(webClientCheckBox);
			}
			projectStructLinePanel.add(projectStructFuncPanel, CC.xy(3, 1));
		}
		add(projectStructLinePanel, CC.xy(2, 14));

		//======== servletEnginLibinaryPathLinePanel ========
		{
			servletEnginLibinaryPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}",
				"default"));

			//---- servletEnginLibinaryPathLabel ----
			servletEnginLibinaryPathLabel.setText("Servelt system library path :");
			servletEnginLibinaryPathLinePanel.add(servletEnginLibinaryPathLabel, CC.xy(1, 1));

			//---- servletEnginLibinaryPathTextField ----
			servletEnginLibinaryPathTextField.setEditable(false);
			servletEnginLibinaryPathLinePanel.add(servletEnginLibinaryPathTextField, CC.xy(3, 1));
		}
		add(servletEnginLibinaryPathLinePanel, CC.xy(2, 16));

		//======== projectConfigVeiwLinePanel ========
		{
			projectConfigVeiwLinePanel.setLayout(new BoxLayout(projectConfigVeiwLinePanel, BoxLayout.X_AXIS));

			//---- projectConfigVeiwButton ----
			projectConfigVeiwButton.setText("view config file");
			projectConfigVeiwButton.setEnabled(false);
			projectConfigVeiwLinePanel.add(projectConfigVeiwButton);
		}
		add(projectConfigVeiwLinePanel, CC.xy(2, 18));
		// //GEN-END:initComponents

		// Logger.getGlobal().info("call");

	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel sinnoriInstalledPathInfoLinePanel;
	private JLabel sinnoriInstalledPathInfoTitleLabel;
	private JLabel sinnoriInstalledPathInfoValueLabel;
	private JPanel allProjectWorkSaveLinePanel;
	private JButton mainProjecNameListUpdatetButton;
	private JButton applySinnoriInstalledPathButton;
	private JButton prevButton;
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
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
