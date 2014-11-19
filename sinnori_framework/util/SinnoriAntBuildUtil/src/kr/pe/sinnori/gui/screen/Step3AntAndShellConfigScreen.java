package kr.pe.sinnori.gui.screen;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.util.PathSwingAction;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


/**
 * 3단계 Ant및 셀 담당 화면
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class Step3AntAndShellConfigScreen extends JPanel {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;

	// FIXME!
	private String sinnoriInstallAbsPathName = null;
	private ArrayList<String> mainProjectList = null;
	
	private boolean isUnix = false;
	
	private SequencedProperties antProperties = null;
	
	private JRadioButton tomcatInstallRadioButton[] = null;
	private JRadioButton tomcatNotInstallRadioButton[] = null;
	private JTextField tomcatLibPathTextField[] = null;;
	private JTextArea serverShellTextArea[] = null;
	private JTextArea appClientShellTextArea[] = null;

	public static final String NEWLINE = System.getProperty("line.separator");
	
	private JFileChooser chooser = null;
	
	/**
	 * Create the panel.
	 */
	public Step3AntAndShellConfigScreen(final JFrame mainFrame, MainControllerIF mainController,
			String sinnoriInstallAbsPathName, 
			ArrayList<String> mainProjectList) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		
		this.sinnoriInstallAbsPathName = sinnoriInstallAbsPathName;
		this.mainProjectList = mainProjectList;
		
		String OSName = System.getProperty("os.name").toLowerCase();
		System.out.printf("OSName : %s", OSName);
		System.out.println("");
		
		
		String shellLineSeparator = null;
		
		if (OSName.contains("win")) {
			isUnix = false;
			shellLineSeparator = "^";
		} else {
			isUnix = true;
			shellLineSeparator = "\\";
		}
		
		
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		JLabel titelLabel = new JLabel("3단계 ant및 shell 설정");
		titelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(titelLabel, "2, 2");
		
		JTabbedPane projectListTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(projectListTabbedPane, "2, 4, fill, fill");
		
		int projectCnt = this.mainProjectList.size();
		
		tomcatInstallRadioButton = new JRadioButton[projectCnt];
		tomcatNotInstallRadioButton = new JRadioButton[projectCnt];
		tomcatLibPathTextField = new JTextField[projectCnt];
		serverShellTextArea = new JTextArea[projectCnt];
		appClientShellTextArea = new JTextArea[projectCnt];
		
		for (int i=0; i < projectCnt; i++) {
			String projectName = this.mainProjectList.get(i);
			
			JPanel projectPanel = new JPanel();
			projectPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					ColumnSpec.decode("center:max(262dlu;default):grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.MIN_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC}));
			
			projectListTabbedPane.addTab(projectName, null, projectPanel, null);
			
			JTabbedPane projectTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			projectPanel.add(projectTabbedPane, "2, 2, fill, fill");
			
			JPanel antPartPanel = new JPanel();
			projectTabbedPane.addTab(MainControllerIF.ANT_CONFIG_FILE_NAME, null, antPartPanel, null);
			antPartPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.MIN_COLSPEC,
					FormFactory.GROWING_BUTTON_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.MIN_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.MIN_ROWSPEC,}));
			
			JLabel tomcatLabel = new JLabel("tomcat 설치 여부");
			antPartPanel.add(tomcatLabel, "2, 2");
			
			JPanel tomcatValuePanel = new JPanel();
			antPartPanel.add(tomcatValuePanel, "3, 2, fill, fill");
			tomcatValuePanel.setLayout(new BoxLayout(tomcatValuePanel, BoxLayout.X_AXIS));
			
			tomcatInstallRadioButton[i] = new JRadioButton("설치");
			tomcatInstallRadioButton[i].setMnemonic(KeyEvent.VK_C);
			tomcatInstallRadioButton[i].setActionCommand("tomcatInstall");
			
			
			tomcatValuePanel.add(tomcatInstallRadioButton[i]);
			
			tomcatNotInstallRadioButton[i] = new JRadioButton("미설치");
			tomcatNotInstallRadioButton[i].setMnemonic(KeyEvent.VK_C);
			tomcatNotInstallRadioButton[i].setActionCommand("tomcatNotInstall");
			
			
			tomcatValuePanel.add(tomcatNotInstallRadioButton[i]);
			
			ButtonGroup tomcatGroup = new ButtonGroup();
			tomcatGroup.add(tomcatInstallRadioButton[i]);
			tomcatGroup.add(tomcatNotInstallRadioButton[i]);
			
			
			JLabel tomcatLibPathLabel = new JLabel("tomcat lib 경로");
			antPartPanel.add(tomcatLibPathLabel, "2, 4");
			
			JPanel tomcatLibPathPanel = new JPanel();
			antPartPanel.add(tomcatLibPathPanel, "3, 4, fill, fill");
			tomcatLibPathPanel.setLayout(new BoxLayout(tomcatLibPathPanel, BoxLayout.X_AXIS));
			
			tomcatLibPathTextField[i] = new JTextField();
			tomcatLibPathPanel.add(tomcatLibPathTextField[i]);
			tomcatLibPathTextField[i].setColumns(10);
			
			JButton tomcatLibPathButton = new JButton("경로선택");
			PathSwingAction tomcatLibPathAction = new PathSwingAction(this.mainFrame, chooser, tomcatLibPathTextField[i]);
			tomcatLibPathButton.setAction(tomcatLibPathAction);
			
			tomcatLibPathPanel.add(tomcatLibPathButton);
			
			// FIXME!
			antProperties = getAntProperties(projectName);
			// tomcatInstallRadioButton[i]
			// tomcatNotInstallRadioButton[i].setSelected(true);
			String propIsTomcat = antProperties.getProperty("is.tomcat");
			if (null == propIsTomcat) propIsTomcat ="";
			
			String propTomcatServeletLib = antProperties.getProperty("tomcat.servletlib");
			if (null == propTomcatServeletLib) propTomcatServeletLib="";
			
			tomcatLibPathTextField[i].setText(propTomcatServeletLib);
			
			boolean isTomcat = propIsTomcat.equals("true");
			
			if (isTomcat) {
				tomcatInstallRadioButton[i].setSelected(true);
				tomcatNotInstallRadioButton[i].setSelected(false);
				
				tomcatLibPathTextField[i].setEditable(true);
				tomcatLibPathButton.setEnabled(true);
			} else {
				tomcatInstallRadioButton[i].setSelected(false);
				tomcatNotInstallRadioButton[i].setSelected(true);
				
				tomcatLibPathTextField[i].setEditable(false);
				tomcatLibPathButton.setEnabled(false);
			}
			
			TomcatOnSwingAction tomcatOnSwingAction = new TomcatOnSwingAction(tomcatLibPathTextField[i], tomcatLibPathButton);
			tomcatInstallRadioButton[i].setAction(tomcatOnSwingAction);
			
			TomcatOffSwingAction tomcatOffSwingAction = new TomcatOffSwingAction(tomcatLibPathTextField[i], tomcatLibPathButton);
			tomcatNotInstallRadioButton[i].setAction(tomcatOffSwingAction);
			
			String serverPartTitle = null;
			String serverPartText = null;
			String clientPartTitle = null;
			String clientPartText = null;
			
			serverPartTitle = MainControllerIF.SINNORI_SERVER_SHELL_NAME;
			clientPartTitle = MainControllerIF.SINNORI_CLIENT_SHELL_NAME;
			
			String projectHomePath = new StringBuilder(sinnoriInstallAbsPathName)
			.append(File.separator)
			.append("project")
			.append(File.separator)
			.append(projectName)
			.toString();
			
			// -Dlogback.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\logback.xml
			String logbackConfigurationFile = new StringBuilder("-Dlogback.configurationFile=")
			.append(projectHomePath)
			.append(File.separator)
			.append("config")
			.append(File.separator)
			.append(MainControllerIF.SINNORI_LOGBACK_LOG_FILE_NAME)
			.toString();
			
			// -Dsinnori.logPath=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\log\server
			String sinnoriLogBasePath = new StringBuilder("-Dsinnori.logPath=")
			.append(projectHomePath)
			.append(File.separator)
			.append("log").toString();
			
			// -Dsinnori.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\sinnori.properties
			String sinnoriConfigurationFile = new StringBuilder("-Dsinnori.configurationFile=")
			.append(projectHomePath)
			.append(File.separator)
			.append("config")
			.append(File.separator)
			.append(MainControllerIF.SINNORI_CONFIG_FILE_NAME)
			.toString();
			
			
			StringBuilder serverPartTextBuilder = new StringBuilder("java -d64 -server -Xmx1024m -Xms1024m");
				
			// -Dlogback.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\logback.xml
			serverPartTextBuilder.append(" ");
			serverPartTextBuilder.append(shellLineSeparator);
			serverPartTextBuilder.append(NEWLINE);
			serverPartTextBuilder.append(logbackConfigurationFile);
				
			// -Dsinnori.logPath=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\log\server
			serverPartTextBuilder.append(" ");
			serverPartTextBuilder.append(shellLineSeparator);
			serverPartTextBuilder.append(NEWLINE);
			serverPartTextBuilder.append(sinnoriLogBasePath);
			serverPartTextBuilder.append(File.separator);
			serverPartTextBuilder.append("server");
				
			// -Dsinnori.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\sinnori.properties
			serverPartTextBuilder.append(" ");
			serverPartTextBuilder.append(shellLineSeparator);
			serverPartTextBuilder.append(NEWLINE);
			serverPartTextBuilder.append(sinnoriConfigurationFile);
			
			// -Dsinnori.projectName=sample_fileupdown
			serverPartTextBuilder.append(" ");
			serverPartTextBuilder.append(shellLineSeparator);
			serverPartTextBuilder.append(NEWLINE);
			serverPartTextBuilder.append("-Dsinnori.projectName=");
			serverPartTextBuilder.append(projectName);
			
			// -jar D:\gitsinnori\sinnori_framework\project\sample_fileupdown\server_build\dist\SinnoriServerMain.jar
			serverPartTextBuilder.append(" ");
			serverPartTextBuilder.append(shellLineSeparator);
			serverPartTextBuilder.append(NEWLINE);
			serverPartTextBuilder.append("-jar ");
			serverPartTextBuilder.append(projectHomePath);
			serverPartTextBuilder.append(File.separator);
			serverPartTextBuilder.append("server_build");
			serverPartTextBuilder.append(File.separator);
			serverPartTextBuilder.append("dist");
			serverPartTextBuilder.append(File.separator);
			serverPartTextBuilder.append("SinnoriServerMain.jar");
			
			serverPartText = serverPartTextBuilder.toString();
			
			StringBuilder clientPartTextBuilder = new StringBuilder("java -Xmx1024m -Xms1024m");
			
			// -Dlogback.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\logback.xml
			clientPartTextBuilder.append(" ");
			clientPartTextBuilder.append(shellLineSeparator);
			clientPartTextBuilder.append(NEWLINE);
			clientPartTextBuilder.append(logbackConfigurationFile);
			
			// -Dsinnori.logPath=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\log\server
			clientPartTextBuilder.append(" ");
			clientPartTextBuilder.append(shellLineSeparator);
			clientPartTextBuilder.append(NEWLINE);
			clientPartTextBuilder.append(sinnoriLogBasePath);
			clientPartTextBuilder.append(File.separator);
			clientPartTextBuilder.append("client");
			
			// -Dsinnori.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_fileupdown\config\sinnori.properties
			clientPartTextBuilder.append(" ");
			clientPartTextBuilder.append(shellLineSeparator);
			clientPartTextBuilder.append(NEWLINE);
			clientPartTextBuilder.append(sinnoriConfigurationFile);
			
			// -Dsinnori.projectName=sample_fileupdown
			clientPartTextBuilder.append(" ");
			clientPartTextBuilder.append(shellLineSeparator);
			clientPartTextBuilder.append(NEWLINE);
			clientPartTextBuilder.append("-Dsinnori.projectName=");
			clientPartTextBuilder.append(projectName);
			
			// -jar D:\gitsinnori\sinnori_framework\project\sample_fileupdown\client_build\app_build\dist\SinnoriServerMain.jar
			clientPartTextBuilder.append(" ");
			clientPartTextBuilder.append(shellLineSeparator);
			clientPartTextBuilder.append(NEWLINE);
			clientPartTextBuilder.append("-jar ");
			clientPartTextBuilder.append(projectHomePath);
			clientPartTextBuilder.append(File.separator);
			clientPartTextBuilder.append("client_build");
			clientPartTextBuilder.append(File.separator);
			clientPartTextBuilder.append("app_build");
			clientPartTextBuilder.append(File.separator);
			clientPartTextBuilder.append("dist");
			clientPartTextBuilder.append(File.separator);
			clientPartTextBuilder.append("SinnoriAppClientMain.jar");

			clientPartText = clientPartTextBuilder.toString();
			
			/** server shell start */
			StringBuilder serverShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
			serverShellFileNameBuilder.append(File.separator);
			serverShellFileNameBuilder.append("project");
			serverShellFileNameBuilder.append(File.separator);
			serverShellFileNameBuilder.append(projectName);
			serverShellFileNameBuilder.append(File.separator);
			serverShellFileNameBuilder.append("server_build");
			
			File serverPath = new File(serverShellFileNameBuilder.toString());
			if (serverPath.exists()) {
				JPanel serverPartPanel = new JPanel();
				projectTabbedPane.addTab(serverPartTitle, null, serverPartPanel, null);
				serverPartPanel.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("636px:grow"),},
					new RowSpec[] {
						FormFactory.LINE_GAP_ROWSPEC,
						RowSpec.decode("max(120dlu;min):grow"),}));
				
				serverShellTextArea[i] = new JTextArea(10, 20);
				serverShellTextArea[i].setEditable(false);
				serverShellTextArea[i].setText(serverPartText);
				
				JScrollPane serverPartScrollPane = new JScrollPane(serverShellTextArea[i]);
				serverPartPanel.add(serverPartScrollPane, "1, 2, fill, fill");
			}			
			
			/** client shell start */
			StringBuilder appClientShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
			appClientShellFileNameBuilder.append(File.separator);
			appClientShellFileNameBuilder.append("project");
			appClientShellFileNameBuilder.append(File.separator);
			appClientShellFileNameBuilder.append(projectName);
			appClientShellFileNameBuilder.append(File.separator);
			appClientShellFileNameBuilder.append("client_build");
			appClientShellFileNameBuilder.append(File.separator);
			appClientShellFileNameBuilder.append("app_build");
			File appClientPath = new File(appClientShellFileNameBuilder.toString());
			if (appClientPath.exists()) {
				JPanel appClientPartPanel = new JPanel();
				projectTabbedPane.addTab(clientPartTitle, null, appClientPartPanel, null);
				appClientPartPanel.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("633px:grow"),},
					new RowSpec[] {
						FormFactory.LINE_GAP_ROWSPEC,
						RowSpec.decode("min:grow"),}));
				
				JScrollPane appClientPartScrollPane = new JScrollPane();
				appClientPartPanel.add(appClientPartScrollPane, "1, 2, fill, fill");
				
				appClientShellTextArea[i] = new JTextArea();
				appClientShellTextArea[i].setEditable(false);
				appClientShellTextArea[i].setText(clientPartText);
				appClientPartScrollPane.setViewportView(appClientShellTextArea[i]);
			}
		}
		
		JPanel bottomPanel = new JPanel();
		add(bottomPanel, "2, 6, center, center");
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		
		JButton finishButton = new JButton("완료");
		FinishSwingAction finishSwingAction = new FinishSwingAction();
		finishButton.setAction(finishSwingAction);
		bottomPanel.add(finishButton);

	}

	/**
	 * 완료 이벤트 처리 클래스
	 * @author Won Jonghoon
	 *
	 */
	private class FinishSwingAction extends AbstractAction {
		/**
		 * 생성자
		 */
		public FinishSwingAction() {
			putValue(NAME, "완료");
			putValue(SHORT_DESCRIPTION, "ant 와 shell 저장후 완료");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isTomcat = false;
			
			for (int i=0; i < tomcatInstallRadioButton.length; i++) {
				String projectName = mainProjectList.get(i);
				
				String tomcatLibPath = tomcatLibPathTextField[i].getText();
				if (null == tomcatLibPath) tomcatLibPath = "";
				
				if (tomcatInstallRadioButton[i].isSelected()) {
					// 톰캣을 설치했다면 톰켓 라이브리 경로 검사
					File fileObj = new File(tomcatLibPath);
					if (!fileObj.exists()) {
						String errorMessage = String.format("프로젝트[%s]의 tomcat 라이브러리 경로[%s]가 존재하지 않습니다.", projectName, tomcatLibPath);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						
						tomcatLibPathTextField[i].requestFocus();
						tomcatLibPathTextField[i].grabFocus();
						return;
					}
					
					if (!fileObj.isDirectory()) {
						String errorMessage = String.format("프로젝트[%s]의 tomcat 라이브러리 경로 값[%s]은 디렉토리가 아닙니다.", projectName, tomcatLibPath);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						
						tomcatLibPathTextField[i].requestFocus();
						tomcatLibPathTextField[i].grabFocus();
						return;
					}
					
					isTomcat = true;
				} else {
					isTomcat = false;
				}
				
				if (! saveAntProperties(projectName, isTomcat, tomcatLibPath)) return;
				
				StringBuilder serverShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
				serverShellFileNameBuilder.append(File.separator);
				serverShellFileNameBuilder.append("project");
				serverShellFileNameBuilder.append(File.separator);
				serverShellFileNameBuilder.append(projectName);
				serverShellFileNameBuilder.append(File.separator);
				serverShellFileNameBuilder.append("server_build");
				serverShellFileNameBuilder.append(File.separator);
				
				File serverPath = new File(serverShellFileNameBuilder.toString());
				if (serverPath.exists()) {
					String serverShellText = serverShellTextArea[i].getText();
					if (null == serverShellText) serverShellText = "";
					serverShellText = serverShellText.trim();
									
					if (! saveServerShell(projectName, serverShellText)) return;
				}
				
				StringBuilder appClientShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
				appClientShellFileNameBuilder.append(File.separator);
				appClientShellFileNameBuilder.append("project");
				appClientShellFileNameBuilder.append(File.separator);
				appClientShellFileNameBuilder.append(projectName);
				appClientShellFileNameBuilder.append(File.separator);
				appClientShellFileNameBuilder.append("client_build");
				appClientShellFileNameBuilder.append(File.separator);
				appClientShellFileNameBuilder.append("app_build");
				
				File appClientPath = new File(appClientShellFileNameBuilder.toString());
				if (appClientPath.exists()) {
					String appClientShellText = appClientShellTextArea[i].getText();
					if (null == appClientShellText) appClientShellText = "";
					appClientShellText = appClientShellText.trim();
					
					if (! saveAppClientShell(projectName, appClientShellText)) return;
				}
			}
			mainController.finish();
		}
	}
	
	private void setupDefaultAntProperteis(SequencedProperties antProperties) {
		antProperties.setProperty("is.tomcat", "false");
		antProperties.setProperty("tomcat.servletlib", ".");
	}
	
	/**
	 * 지정된 프로젝트의 ant 프로퍼티 파일을 읽어 온다.
	 * @param projectName 프로젝트명
	 * @return 지정된 프로젝트의 ant 프로퍼티 객체 
	 */
	private SequencedProperties getAntProperties(String projectName) {
		StringBuilder antPropertiesFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append("project");
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append(projectName);
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append(MainControllerIF.ANT_CONFIG_FILE_NAME);
		
		SequencedProperties antProperties = new SequencedProperties();
		
		String antPropertiesFileName = antPropertiesFileNameBuilder.toString();
		File antPropertiesFileObj = new File(antPropertiesFileName);
		if (!antPropertiesFileObj.exists()) {
			try {
				antPropertiesFileObj.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				
				String errorMessage = String.format("프로젝트[%s] ant 환경 설정 프로퍼티 파일[%s] unknown error", projectName, antPropertiesFileObj.getAbsolutePath());
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				
				// is.tomcat=false
				// tomcat.servletlib=/usr/share/tomcat7/lib
				setupDefaultAntProperteis(antProperties);
				return antProperties;
			}
		}
		
		FileInputStream antPropertiesFIS = null;
		InputStreamReader antPropertiesISR = null;
		try {
			antPropertiesFIS = new FileInputStream(antPropertiesFileObj);
			antPropertiesISR = new InputStreamReader(antPropertiesFIS, "UTF-8");
				
			antProperties.load(antPropertiesISR);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			String errorMessage = String.format("프로젝트[%s] ant 환경 설정 프로퍼티 파일[%s] unknown error", projectName, antPropertiesFileObj.getAbsolutePath());
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			setupDefaultAntProperteis(antProperties);
		} finally {
			try {
				if (antPropertiesISR != null)
					antPropertiesISR.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				if (antPropertiesFIS != null)
					antPropertiesFIS.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return antProperties;
	}
	
	/**
	 * 지정된 프로젝트의 ant 프로퍼티를 저장한다. ant 프로퍼티는 톰켓 설치여부, 톰켓 라이브러리 경로, 자바 디버깅 여부로 구성된다.
	 * @param projectName 프로젝트 이름
	 * @param isTomcat 톰켓 설치 여부
	 * @param tomcatLibPath 톰켓 라이브러리 경로
	 * @return
	 */
	private boolean saveAntProperties(String projectName, boolean isTomcat, String tomcatLibPath) {
		antProperties.setProperty("is.tomcat", String.valueOf(isTomcat));
		antProperties.setProperty("tomcat.servletlib", tomcatLibPath);
		
		
		StringBuilder antPropertiesFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append("project");
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append(projectName);
		antPropertiesFileNameBuilder.append(File.separator);
		antPropertiesFileNameBuilder.append(MainControllerIF.ANT_CONFIG_FILE_NAME);
		
		String antPropertiesFileName = antPropertiesFileNameBuilder.toString();
		File antPropertiesFileObj = new File(antPropertiesFileName);
		
		FileOutputStream antPropertiesFOS = null;
		OutputStreamWriter antPropertiesOSW = null;
		try {
			antPropertiesFOS = new FileOutputStream(antPropertiesFileObj);
			antPropertiesOSW = new OutputStreamWriter(antPropertiesFOS, "UTF-8");
			/*
			// ## 신놀이 개별 프로젝트에서 사용하는 ant 프로퍼티 값들
			antPropertiesOSW.write("## 신놀이 개별 프로젝트에서 사용하는 ant 프로퍼티 값들");
			antPropertiesOSW.write(NEWLINE);
			antPropertiesOSW.write("is.tomcat=");
			antPropertiesOSW.write(String.valueOf(isTomcat));
			antPropertiesOSW.write(NEWLINE);
			antPropertiesOSW.write("tomcat.servletlib=");
			antPropertiesOSW.write(tomcatLibPath);
			antPropertiesOSW.write(NEWLINE);
			antPropertiesOSW.write("java.debug=true");
			*/
			antProperties.store(antPropertiesOSW, String.format("Project[%s]'s ant properties file", projectName));
			
		} catch (Exception e) {
			e.printStackTrace();
			
			String errorMessage = String.format("프로젝트[%s] ant 환경 설정 프로퍼티 파일[%s] unknown error", projectName, antPropertiesFileObj.getAbsolutePath());
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		} finally {
			try {
				if (antPropertiesOSW != null)
					antPropertiesOSW.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				if (antPropertiesFOS != null)
					antPropertiesFOS.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * 지정된 프로젝트의 서버 셀 내용을 저장한다.
	 * @param projectName 프로젝트 이름
	 * @param serverShellText 서버 셀 내용
	 * @return 저장 성공 여부
	 */
	private boolean saveServerShell(String projectName, String serverShellText) {
		StringBuilder serverShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
		serverShellFileNameBuilder.append(File.separator);
		serverShellFileNameBuilder.append("project");
		serverShellFileNameBuilder.append(File.separator);
		serverShellFileNameBuilder.append(projectName);
		serverShellFileNameBuilder.append(File.separator);
		serverShellFileNameBuilder.append("server_build");
		serverShellFileNameBuilder.append(File.separator);
		serverShellFileNameBuilder.append(projectName);
		serverShellFileNameBuilder.append(MainControllerIF.SINNORI_SERVER_SHELL_NAME);
		if (isUnix) {
			serverShellFileNameBuilder.append(".sh");
		} else {
			serverShellFileNameBuilder.append(".bat");
		}
		
		
		String serverShellFileName = serverShellFileNameBuilder.toString();
		File serverShellFileObj = new File(serverShellFileName);
		
		FileOutputStream serverShellFOS = null;
		OutputStreamWriter serverShellOSW = null;
		try {
			serverShellFileObj.setExecutable(true);
			
			serverShellFOS = new FileOutputStream(serverShellFileObj);
			serverShellOSW = new OutputStreamWriter(serverShellFOS, "UTF-8");
			
			serverShellOSW.write(serverShellText);
		} catch (Exception e) {
			e.printStackTrace();
			
			String errorMessage = String.format("프로젝트[%s] 서버 셀 파일[%s] unknown error", projectName, serverShellFileName);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		} finally {
			try {
				if (serverShellOSW != null)
					serverShellOSW.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				if (serverShellFOS != null)
					serverShellFOS.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * 지정된 프로젝트의 어플-클라이언트 셀의 내용을 저장한다.
	 * @param projectName 프로젝트 이름
	 * @param appClientShellText 어플-클라이언트 셀의 내용
	 * @return 저장 성공 여부
	 */
	private boolean saveAppClientShell(String projectName, String appClientShellText) {
		StringBuilder appClientShellFileNameBuilder = new StringBuilder(sinnoriInstallAbsPathName);
		appClientShellFileNameBuilder.append(File.separator);
		appClientShellFileNameBuilder.append("project");
		appClientShellFileNameBuilder.append(File.separator);
		appClientShellFileNameBuilder.append(projectName);
		appClientShellFileNameBuilder.append(File.separator);
		appClientShellFileNameBuilder.append("client_build");
		appClientShellFileNameBuilder.append(File.separator);
		appClientShellFileNameBuilder.append("app_build");
		appClientShellFileNameBuilder.append(File.separator);
		
		appClientShellFileNameBuilder.append(projectName);
		appClientShellFileNameBuilder.append(MainControllerIF.SINNORI_CLIENT_SHELL_NAME);
		if (isUnix) {
			appClientShellFileNameBuilder.append(".sh");
		} else {
			appClientShellFileNameBuilder.append(".bat");
		}
		
		String appClientShellFileName = appClientShellFileNameBuilder.toString();
		File appClientShellFileObj = new File(appClientShellFileName);
		
		FileOutputStream appClientShellFOS = null;
		OutputStreamWriter appClinetShellOSW = null;
		try {
			appClientShellFileObj.setExecutable(true);
			
			appClientShellFOS = new FileOutputStream(appClientShellFileObj);
			appClinetShellOSW = new OutputStreamWriter(appClientShellFOS, "UTF-8");
			
			appClinetShellOSW.write(appClientShellText);
		} catch (Exception e) {
			e.printStackTrace();
			
			String errorMessage = String.format("프로젝트[%s] 일반 어플리케이션 클라리언트 셀 파일[%s] unknown error", projectName, appClientShellFileName);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		} finally {
			try {
				if (appClinetShellOSW != null)
					appClinetShellOSW.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				if (appClientShellFOS != null)
					appClientShellFOS.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * 톰켓 미설치 라디오 버튼 이벤트 처리 클래스
	 * @author Won Jonghoon
	 *
	 */
	private class TomcatOffSwingAction extends AbstractAction {
		private JTextField targetTextField = null;
		private JButton targetJButton = null;
		
		/**
		 * 생성자
		 * @param targetTextField 톰켓 라이브러리 설치 경로 입력 콤포넌트 
		 * @param targetJButton 톰캣 라이브러리 설치 경로 선택 버튼 콤포넌트
		 */
		public TomcatOffSwingAction(JTextField targetTextField, JButton targetJButton) {
			this.targetTextField = targetTextField;
			this.targetJButton = targetJButton;
			
			putValue(NAME, "미설치");
			putValue(SHORT_DESCRIPTION, "자체적으로 공개키/개인키 생성 방법 선택");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			targetTextField.setEditable(false);
			targetJButton.setEnabled(false);
			
		}
	}
	
	
	/**
	 * 톰켓 설치 라디오 버튼 이벤트 처리 클래스
	 * @author Won Jonghoon
	 *
	 */
	private class TomcatOnSwingAction extends AbstractAction {
		private JTextField targetTextField = null;
		private JButton targetJButton = null;
		
		/**
		 * 생성자
		 * @param targetTextField 톰켓 라이브러리 설치 경로 입력 콤포넌트 
		 * @param targetJButton 톰캣 라이브러리 설치 경로 선택 버튼 콤포넌트
		 */
		public TomcatOnSwingAction(JTextField targetTextField, JButton targetJButton) {
			this.targetTextField = targetTextField;
			this.targetJButton = targetJButton;
			
			putValue(NAME, "설치");
			putValue(SHORT_DESCRIPTION, "외부 파일을 이용한 공개키/개인키 생성 방법 선택");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			targetTextField.setEditable(true);
			targetJButton.setEnabled(true);
		}
	}
}
