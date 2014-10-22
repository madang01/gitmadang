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
package kr.pe.sinnori.gui.screen;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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
import javax.swing.JTabbedPane;
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
 * 2단계 프로젝트 귀속 설정 파일 화면
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class Step2SinnoriConfigScreen extends JPanel {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;

	private String sinnoriInstallAbsPathName = null;
	
	private ArrayList<String> mainProjectList = null;
	private HashMap<String, SequencedProperties> project2ConfigHash = null;
	
	// private File projectConfigFileList[] = null;
	
	private JRadioButton apiRadioButton[] = null;
	private JRadioButton fileButton[] = null;
	private JTextField rsaKeyPairPathTextField[] = null;
	// private JTextField workerBinaryPathTextField[] = null;
	// private JTextField workerSourcePathTextField[] = null;
	
	private String innerProjectList[][] = null;
	private JTextField innerProjectMessagePathTextField[][] = null;
	private JTextField innerProjectClassLoaderAPPINFPathTextField[][] = null;
	private JTextField innerProjectClassLoaderSourcePathTextField[][] = null;

	// private ButtonGroup rsaKeypairSourceGroup = null;
	
	private JFileChooser chooser = null;
	
	/**
	 * Create the panel.
	 */
	public Step2SinnoriConfigScreen(final JFrame mainFrame, MainControllerIF mainController,
			String sinnoriInstallAbsPathName, 
			ArrayList<String> mainProjectList, HashMap<String, SequencedProperties> project2ConfigHash) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		
		this.sinnoriInstallAbsPathName = sinnoriInstallAbsPathName;
		this.mainProjectList = mainProjectList;
		this.project2ConfigHash = project2ConfigHash;
		
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("center:max(262dlu;default):grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,}));
		
		JLabel label = new JLabel("단계2 신놀이 환경 파일");
		add(label, "2, 2");
		
		JTabbedPane projectListTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(projectListTabbedPane, "2, 4, fill, fill");
		
		int projectCnt = this.mainProjectList.size();
		
		apiRadioButton = new JRadioButton[projectCnt];
		fileButton = new JRadioButton[projectCnt];
		rsaKeyPairPathTextField = new JTextField[projectCnt];
		// workerBinaryPathTextField = new JTextField[projectCnt];
		// workerSourcePathTextField = new JTextField[projectCnt];
		
		
		innerProjectList = new String[projectCnt][];
		innerProjectMessagePathTextField = new JTextField[projectCnt][];
		innerProjectClassLoaderAPPINFPathTextField = new JTextField[projectCnt][];
		innerProjectClassLoaderSourcePathTextField = new JTextField[projectCnt][];
		
		for (int i=0; i < projectCnt; i++) {
			String projectName = this.mainProjectList.get(i);
			SequencedProperties configOfProject = this.project2ConfigHash.get(projectName);
			String propKey = "common.projectlist.value";
			
			String propProjectList = configOfProject.getProperty(propKey);
			if (null == propProjectList) {
				System.out.printf("필수 항목 '프로젝트 목록'[%s] 변수및 값이 설정되지 않았습니다.", propKey);
				System.out.println();
				System.exit(1);
			}
			
			StringTokenizer projectListTokens = new StringTokenizer(propProjectList, ",");
			
			int projectCntOfConfig = projectListTokens.countTokens();
			
			if (projectCntOfConfig < 1) {
				System.out.printf("필수 항목 '프로젝트 목록'[%s] 값이 없습니다.", propKey);
				System.out.println();
				System.exit(1);
			}
			// String projectOfConfing[] = new String[cntOfProject];
			// ArrayList<String> projectListOfConfig = new ArrayList<String>(); 
			
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
			
			JPanel commonItemsPanel = new JPanel();
			projectTabbedPane.addTab("공통 항목", null, commonItemsPanel, null);
			
			
			commonItemsPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.MIN_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.GROWING_BUTTON_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.PREF_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.MIN_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.MIN_ROWSPEC,					
					FormFactory.LINE_GAP_ROWSPEC,}));
			
			JLabel keyLabel = new JLabel("변수명");
			commonItemsPanel.add(keyLabel, "2, 2, center, default");
			
			JLabel valueLabel = new JLabel("값");
			commonItemsPanel.add(valueLabel, "4, 2, center, default");
			
			propKey = "sessionkey.rsa_keypair_source.value";
			String rsaKeypairSource = configOfProject.getProperty(propKey);
			JLabel sessionKeySourceLabel = new JLabel(propKey);
			commonItemsPanel.add(sessionKeySourceLabel, "2, 4");
			
			JPanel sessionKeySourceValuePanel = new JPanel();
			sessionKeySourceValuePanel.setLayout(new BoxLayout(sessionKeySourceValuePanel, BoxLayout.X_AXIS));
			apiRadioButton[i] = new JRadioButton("API");
			apiRadioButton[i].setMnemonic(KeyEvent.VK_C);
			apiRadioButton[i].setActionCommand("api::"+i);
			fileButton[i] = new JRadioButton("File");
			fileButton[i].setMnemonic(KeyEvent.VK_C);
			fileButton[i].setActionCommand("file::"+i);
			ButtonGroup rsaKeypairSourceGroup = new ButtonGroup();
			rsaKeypairSourceGroup.add(apiRadioButton[i]);
			rsaKeypairSourceGroup.add(fileButton[i]);
			sessionKeySourceValuePanel.add(apiRadioButton[i]);
			sessionKeySourceValuePanel.add(fileButton[i]);			
			commonItemsPanel.add(sessionKeySourceValuePanel, "4, 4, left, default");
			
			propKey = "sessionkey.rsa_keypair_path.value";
			String rsaKeyPairPath = configOfProject.getProperty(propKey);
			JLabel rsaKeyPairPathLabel = new JLabel(propKey);
			commonItemsPanel.add(rsaKeyPairPathLabel, "2, 6");
			
			JPanel rsaKeyPairPathValuePanel = new JPanel();
			rsaKeyPairPathValuePanel.setLayout(new BoxLayout(rsaKeyPairPathValuePanel, BoxLayout.X_AXIS));			
			rsaKeyPairPathTextField[i] = new JTextField();
			rsaKeyPairPathTextField[i].setColumns(30);			
			File rsaKeyPairPathFileObj = new File(rsaKeyPairPath);			
			if (!rsaKeyPairPathFileObj.exists() || !rsaKeyPairPathFileObj.isDirectory()) {
				StringBuilder rsaKeyPairPathBuilder = new StringBuilder(this.sinnoriInstallAbsPathName);
				rsaKeyPairPathBuilder.append(File.separator);
				rsaKeyPairPathBuilder.append("project");
				rsaKeyPairPathBuilder.append(File.separator);
				rsaKeyPairPathBuilder.append(projectName);
				rsaKeyPairPathBuilder.append(File.separator);
				rsaKeyPairPathBuilder.append("rsa_keypair");
				
				rsaKeyPairPath = rsaKeyPairPathBuilder.toString();
				
				rsaKeyPairPathFileObj = new File(rsaKeyPairPath);
				if (!rsaKeyPairPathFileObj.exists()) {
					rsaKeyPairPathFileObj.mkdirs();
				}
				
				if (!rsaKeyPairPathFileObj.isDirectory()) {
					System.out.printf("프로젝트[%s]의 환경 변수[%s] 새롭게 설정한 RSA 키 경로 값[%s]은 디렉토리가 아닙니다.", projectName, propKey, rsaKeyPairPath);
					System.out.println();
					System.exit(1);
				}
			}
			rsaKeyPairPathTextField[i].setText(rsaKeyPairPath);
			rsaKeyPairPathValuePanel.add(rsaKeyPairPathTextField[i]);
			JButton rsaKeyPairPathButton = new JButton("경로선택");
			PathSwingAction rsaKeyPairPathAction = new PathSwingAction(this.mainFrame, chooser, rsaKeyPairPathTextField[i]);
			rsaKeyPairPathButton.setAction(rsaKeyPairPathAction);
			rsaKeyPairPathValuePanel.add(rsaKeyPairPathButton);
			commonItemsPanel.add(rsaKeyPairPathValuePanel, "4, 6, default, center");
			
			
			FileOnSwingAction fileOnSwingAction = new FileOnSwingAction(rsaKeyPairPathTextField[i], rsaKeyPairPathButton);
			fileButton[i].setAction(fileOnSwingAction);
			
			APIOnSwingAction apiOnSwingAction = new APIOnSwingAction(rsaKeyPairPathTextField[i], rsaKeyPairPathButton);
			apiRadioButton[i].setAction(apiOnSwingAction);
						
			if (rsaKeypairSource.equals("File")) {
				apiRadioButton[i].setSelected(false);
				fileButton[i].setSelected(true);
				
				rsaKeyPairPathTextField[i].setEditable(true);
				rsaKeyPairPathButton.setEnabled(true);
			} else {
				apiRadioButton[i].setSelected(true);
				fileButton[i].setSelected(false);
				rsaKeyPairPathTextField[i].setEditable(false);
				rsaKeyPairPathButton.setEnabled(false);
			}
			
			innerProjectList[i] = new String[projectCntOfConfig];
			innerProjectMessagePathTextField[i] = new JTextField[projectCntOfConfig];
			innerProjectClassLoaderAPPINFPathTextField[i] = new JTextField[projectCntOfConfig];
			innerProjectClassLoaderSourcePathTextField[i] = new JTextField[projectCntOfConfig];
			
			
			for (int j=0; j < projectCntOfConfig; j++) {
				String projectNameOfConfig = projectListTokens.nextToken();
				innerProjectList[i][j] = projectNameOfConfig;
				
				// projectListOfConfig.add(projectListTokens.nextToken());
				
				JPanel innerProjectPanel = new JPanel();
				innerProjectPanel.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.MIN_COLSPEC,
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
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.MIN_ROWSPEC,}));
				
				JLabel innerKeyLabel = new JLabel("변수명");
				innerKeyLabel.setHorizontalAlignment(SwingConstants.CENTER);
				keyLabel.setHorizontalAlignment(SwingConstants.CENTER);
				innerProjectPanel.add(innerKeyLabel, "2, 2");
				
				JLabel innerValueLabel = new JLabel("값");
				innerValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
				innerProjectPanel.add(innerValueLabel, "4, 2");
				
				
				// new StringBuilder(innerProjectList[i][j]).append(".common.message_info.xmlpath.value").toString();
				JLabel innerProjectMessagePathLabel = new JLabel(new StringBuilder(innerProjectList[i][j]).append(".common.message_info.xmlpath.value").toString());
				innerProjectPanel.add(innerProjectMessagePathLabel, "2, 4");
				
				innerProjectMessagePathTextField[i][j] = new JTextField();
				innerProjectMessagePathTextField[i][j].setColumns(30);
				
				JPanel innerProjectMessagePathValuePanel = new JPanel();
				innerProjectMessagePathValuePanel.setLayout(new BoxLayout(innerProjectMessagePathValuePanel, BoxLayout.X_AXIS));
				innerProjectMessagePathValuePanel.add(innerProjectMessagePathTextField[i][j]);
				
				innerProjectPanel.add(innerProjectMessagePathValuePanel, "4, 4");
				// "sample_simple_ftp.server.executor.impl.binary.path.value"
				JLabel innerProjectDynamicClassBinaryBasePathLabel = new JLabel(new StringBuilder(innerProjectList[i][j]).append(".server.classloader.appinf.path.value").toString());
				innerProjectDynamicClassBinaryBasePathLabel.setHorizontalAlignment(SwingConstants.LEFT);
				innerProjectPanel.add(innerProjectDynamicClassBinaryBasePathLabel, "2, 6");
				
				innerProjectClassLoaderAPPINFPathTextField[i][j] = new JTextField();
				innerProjectClassLoaderAPPINFPathTextField[i][j].setColumns(30);
				
				JPanel innerProjectExecutorBinaryPathValuePanel = new JPanel();
				innerProjectExecutorBinaryPathValuePanel.setLayout(new BoxLayout(innerProjectExecutorBinaryPathValuePanel, BoxLayout.X_AXIS));
				innerProjectExecutorBinaryPathValuePanel.add(innerProjectClassLoaderAPPINFPathTextField[i][j]);
				
				innerProjectPanel.add(innerProjectExecutorBinaryPathValuePanel, "4, 6, fill, default");
				// "sample_simple_ftp.server.executor.impl.source.path.value"
				JLabel innerProjectDynamicClassSourceBasePathLabel = new JLabel(new StringBuilder(innerProjectList[i][j]).append(".server.classloader.class.source.path.value").toString());
				innerProjectDynamicClassSourceBasePathLabel.setHorizontalAlignment(SwingConstants.LEFT);
				innerProjectPanel.add(innerProjectDynamicClassSourceBasePathLabel, "2, 8");
				
				innerProjectClassLoaderSourcePathTextField[i][j] = new JTextField();
				innerProjectClassLoaderSourcePathTextField[i][j].setColumns(30);
				
				JPanel innerProjectExecutorSourcePathValuePanel = new JPanel();
				innerProjectExecutorSourcePathValuePanel.setLayout(new BoxLayout(innerProjectExecutorSourcePathValuePanel, BoxLayout.X_AXIS));
				innerProjectExecutorSourcePathValuePanel.add(innerProjectClassLoaderSourcePathTextField[i][j]);
				
				innerProjectPanel.add(innerProjectExecutorSourcePathValuePanel, "4, 8, fill, default");
				
				
				if (projectNameOfConfig.equals(projectName)) {
					/** <신놀이설치경로>/project/<프로젝트명> 에서 사용된 프로젝트명과 설정파일에서의 프로젝트명이 일치될 경우 */
					projectTabbedPane.addTab("주 프로젝트", null, innerProjectPanel, null);
					
					StringBuilder innerProjectMessagePathBuilder = new StringBuilder(sinnoriInstallAbsPathName);
					innerProjectMessagePathBuilder.append(File.separator);
					innerProjectMessagePathBuilder.append("project");
					innerProjectMessagePathBuilder.append(File.separator);
					innerProjectMessagePathBuilder.append(projectName);
					innerProjectMessagePathBuilder.append(File.separator);
					innerProjectMessagePathBuilder.append("impl");
					innerProjectMessagePathBuilder.append(File.separator);
					innerProjectMessagePathBuilder.append("message");
					innerProjectMessagePathBuilder.append(File.separator);
					innerProjectMessagePathBuilder.append("info");
					
					innerProjectMessagePathTextField[i][j].setText(innerProjectMessagePathBuilder.toString());
					innerProjectMessagePathTextField[i][j].setEditable(false);
					
					StringBuilder innerProjectClassLoaderAPPINFPathBuilder = new StringBuilder(sinnoriInstallAbsPathName);
					innerProjectClassLoaderAPPINFPathBuilder.append(File.separator);
					innerProjectClassLoaderAPPINFPathBuilder.append("project");
					innerProjectClassLoaderAPPINFPathBuilder.append(File.separator);
					innerProjectClassLoaderAPPINFPathBuilder.append(projectName);
					innerProjectClassLoaderAPPINFPathBuilder.append(File.separator);
					innerProjectClassLoaderAPPINFPathBuilder.append("server_build");
					innerProjectClassLoaderAPPINFPathBuilder.append(File.separator);
					innerProjectClassLoaderAPPINFPathBuilder.append("APP-INF");
					
					innerProjectClassLoaderAPPINFPathTextField[i][j].setText(innerProjectClassLoaderAPPINFPathBuilder.toString());
					innerProjectClassLoaderAPPINFPathTextField[i][j].setEditable(false);
					
					StringBuilder innerProjectClassLoaderSourcePathBuilder = new StringBuilder(sinnoriInstallAbsPathName);
					innerProjectClassLoaderSourcePathBuilder.append(File.separator);
					innerProjectClassLoaderSourcePathBuilder.append("project");
					innerProjectClassLoaderSourcePathBuilder.append(File.separator);
					innerProjectClassLoaderSourcePathBuilder.append(projectName);
					innerProjectClassLoaderSourcePathBuilder.append(File.separator);
					innerProjectClassLoaderSourcePathBuilder.append("server_build");
					innerProjectClassLoaderSourcePathBuilder.append(File.separator);
					innerProjectClassLoaderSourcePathBuilder.append("src");
					
					innerProjectClassLoaderSourcePathTextField[i][j].setText(innerProjectClassLoaderSourcePathBuilder.toString());
					innerProjectClassLoaderSourcePathTextField[i][j].setEditable(false);
				} else {
					/** <신놀이설치경로>/project/<프로젝트명> 에서 사용된 프로젝트명과 설정파일에서의 프로젝트명이 일치될 되지 않았을 경우 */
					projectTabbedPane.addTab("서브 프로젝트", null, innerProjectPanel, null);
					
					// sample_simple_ftp.common.message_info.xmlpath.value
					StringBuilder propKeyBuilder = null;
					
					propKeyBuilder = new StringBuilder(projectNameOfConfig);
					propKeyBuilder.append(".common.message_info.xmlpath.value");
					String innerProjectMessagePathText = configOfProject.getProperty(propKeyBuilder.toString());
					if (null == innerProjectMessagePathText) innerProjectMessagePathText= "'";
					// else innerProjectMessagePathText = innerProjectMessagePathText.trim();
					
					innerProjectMessagePathTextField[i][j].setText(innerProjectMessagePathText);
					innerProjectMessagePathTextField[i][j].setEditable(true);
					JButton innerProjectMessageButton = new JButton("경로선택");
					PathSwingAction innerProjectMessagePathAction = new PathSwingAction(this.mainFrame, chooser, innerProjectMessagePathTextField[i][j]);
					innerProjectMessageButton.setAction(innerProjectMessagePathAction);
					
					innerProjectMessagePathValuePanel.add(innerProjectMessageButton);
					
					propKeyBuilder = new StringBuilder(projectNameOfConfig);
					propKeyBuilder.append(".server.classloader.appinf.path.value");
					String innerProjectExecutorBinaryPath = configOfProject.getProperty(propKeyBuilder.toString());
					if (null == innerProjectExecutorBinaryPath) innerProjectExecutorBinaryPath= "'";
					
					innerProjectClassLoaderAPPINFPathTextField[i][j].setText(innerProjectExecutorBinaryPath);
					innerProjectClassLoaderAPPINFPathTextField[i][j].setEditable(true);
					JButton innerProjectExecutorBinaryPathButton = new JButton("경로선택");
					PathSwingAction innerProjectDynamicClassBinaryBasePathAction = new PathSwingAction(this.mainFrame, chooser, innerProjectClassLoaderAPPINFPathTextField[i][j]);
					innerProjectExecutorBinaryPathButton.setAction(innerProjectDynamicClassBinaryBasePathAction);
					
					innerProjectExecutorBinaryPathValuePanel.add(innerProjectExecutorBinaryPathButton);
					
					propKeyBuilder = new StringBuilder(projectNameOfConfig);
					propKeyBuilder.append(".server.classloader.class.source.path.value");
					String innerProjectExecutorSourcePath = configOfProject.getProperty(propKeyBuilder.toString());
					if (null == innerProjectExecutorSourcePath) innerProjectExecutorSourcePath= "'";
					
					innerProjectClassLoaderSourcePathTextField[i][j].setText(innerProjectExecutorSourcePath);
					innerProjectClassLoaderSourcePathTextField[i][j].setEditable(true);
					JButton innerProjectExecutorSourcePathButton = new JButton("경로선택");
					PathSwingAction innerProjectDynamicClassSourceBasePathAction = new PathSwingAction(this.mainFrame, chooser, innerProjectClassLoaderSourcePathTextField[i][j]);
					innerProjectExecutorSourcePathButton.setAction(innerProjectDynamicClassSourceBasePathAction);
					
					innerProjectExecutorSourcePathValuePanel.add(innerProjectExecutorSourcePathButton);
				}
			}
		}
		
		
		JPanel bottomPanel = new JPanel();
		FlowLayout fl_bottomPanel = (FlowLayout) bottomPanel.getLayout();
		fl_bottomPanel.setAlignOnBaseline(true);
		add(bottomPanel, "2, 6, fill, center");
		
		JButton nextButton = new JButton("다음");
		NextSwingAction nextSwingAction = new NextSwingAction(this);
		nextButton.setAction(nextSwingAction);
		
		bottomPanel.add(nextButton);

	}
	
	/**
	 * 프로젝트 수 만큼 프로젝트 귀속 설정파일 저장
	 * @return
	 */
	public boolean saveConfig() {
		System.out.printf("innerProjectList.length=[%d]", innerProjectList.length);
		System.out.println();
		System.out.printf("apiRadioButton.length=[%d]", apiRadioButton.length);
		System.out.println();
		
		for (int i=0; i < innerProjectList.length; i++) {
			
			// sinnoriInstallAbsPathName
			String projectName = mainProjectList.get(i);
			SequencedProperties configOfProject = project2ConfigHash.get(projectName);
			
			String propKey = null;
						
			propKey = "sessionkey.rsa_keypair_path.value";
			String rsaKeyPairPath = rsaKeyPairPathTextField[i].getText();
			
			File fileObj = new File(rsaKeyPairPath);
			if (!fileObj.exists()) {
				String errorMessage = String.format("프로젝트[%s]의 환경 변수[%s] RSA 키 경로[%s]가 존재하지 않습니다.", projectName, propKey, rsaKeyPairPath);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				
				rsaKeyPairPathTextField[i].requestFocus();
				rsaKeyPairPathTextField[i].grabFocus();
				return false;
			}
			
			if (!fileObj.isDirectory()) {
				String errorMessage = String.format("프로젝트[%s]의 환경 변수[%s] RSA 키 경로 값[%s]은 디렉토리가 아닙니다.", projectName, propKey, rsaKeyPairPath);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				
				rsaKeyPairPathTextField[i].requestFocus();
				rsaKeyPairPathTextField[i].grabFocus();
				return false;
			}
			
			if (fileButton[i].isSelected()) {
				configOfProject.setProperty("sessionkey.rsa_keypair_source.value", "File");
				configOfProject.setProperty(propKey, rsaKeyPairPath);
			} else {
				configOfProject.setProperty("sessionkey.rsa_keypair_source.value", "API");
				configOfProject.setProperty(propKey, rsaKeyPairPath);
			}
			
			/*propKey = "sinnori_worker.client.executor.impl.binary.path.value";
			String workerBinaryPath = workerBinaryPathTextField[i].getText();
			configOfProject.setProperty(propKey, workerBinaryPath);
			
			fileObj = new File(workerBinaryPath);
			if (!fileObj.exists()) {
				
				boolean isCreatedDir = fileObj.mkdirs();
				System.out.printf("환경변수[%s] 경로 생성 여부[%s] 경로[%s]", propKey, isCreatedDir, workerBinaryPath);
				System.out.println();
				
				if (! isCreatedDir) System.exit(1);
			}
			
			propKey = "sinnori_worker.client.executor.impl.source.path.value";
			String workerSourcePath = workerSourcePathTextField[i].getText();
			configOfProject.setProperty(propKey, workerSourcePath);
			
			fileObj = new File(workerSourcePath);
			if (!fileObj.exists()) {
				boolean isCreatedDir = fileObj.mkdirs();
				
				System.out.printf("환경변수[%s] 경로 생성 여부[%s] 경로[%s]", propKey, isCreatedDir, workerBinaryPath);
				System.out.println();
				
				if (! isCreatedDir) System.exit(1);
			}
			*/
			
			System.out.printf("innerProjectList[%d].length=[%d]", i, innerProjectList[i].length);
			System.out.println();
			
			for (int j=0; j < innerProjectList[i].length; j++) {

				propKey = new StringBuilder(innerProjectList[i][j]).append(".common.message_info.xmlpath.value").toString();
				String innerProjectMessagePath = innerProjectMessagePathTextField[i][j].getText();
				
				fileObj = new File(innerProjectMessagePath);
				if (!fileObj.exists()) {
					String errorMessage = String.format("프로젝트[%s]의 환경변수[%s]의  경로[%s] 가 존재하지 않습니다.", projectName, propKey, innerProjectMessagePath);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					if (innerProjectMessagePathTextField[i][j].isEditable()) {
						innerProjectMessagePathTextField[i][j].requestFocus();
						innerProjectMessagePathTextField[i][j].grabFocus();
					}
					return false;
				}
				
				if (!fileObj.isDirectory()) {
					String errorMessage = String.format("프로젝트[%s]의 환경변수[%s]에 입력한 경로 값[%s]은 디렉토리가 아닙니다.", projectName, propKey, innerProjectMessagePath);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					
					if (innerProjectMessagePathTextField[i][j].isEditable()) {
						innerProjectMessagePathTextField[i][j].requestFocus();
						innerProjectMessagePathTextField[i][j].grabFocus();
					}
					return false;
				}
				
				configOfProject.setProperty(propKey, innerProjectMessagePath);
				
				propKey = new StringBuilder(innerProjectList[i][j]).append(".server.classloader.appinf.path.value").toString();
				String innerProjectDynamicClassBinaryBasePath = innerProjectClassLoaderAPPINFPathTextField[i][j].getText();
				
				fileObj = new File(innerProjectDynamicClassBinaryBasePath);
				if (!fileObj.exists()) {
					boolean isCreatedDir = fileObj.mkdirs();
					System.out.printf("환경변수[%s] 경로 생성 여부[%s] 경로[%s]", propKey, isCreatedDir, innerProjectDynamicClassBinaryBasePath);
					System.out.println();
					
					if (! isCreatedDir) System.exit(1);
				}
				
				if (!fileObj.isDirectory()) {
					String errorMessage = String.format("환경변수[%s]에 입력한 경로 값[%s]은 디렉토리가 아닙니다.", propKey, innerProjectDynamicClassBinaryBasePath);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					
					if (innerProjectClassLoaderAPPINFPathTextField[i][j].isEditable()) {
						innerProjectClassLoaderAPPINFPathTextField[i][j].requestFocus();
						innerProjectClassLoaderAPPINFPathTextField[i][j].grabFocus();
					}
					return false;
				}
				
				configOfProject.setProperty(propKey, innerProjectDynamicClassBinaryBasePath);
				
				
				propKey = new StringBuilder(innerProjectList[i][j]).append(".server.classloader.class.source.path.value").toString();
				String innerProjectDynamicClassSourceBasePath = innerProjectClassLoaderSourcePathTextField[i][j].getText();
				
				fileObj = new File(innerProjectDynamicClassSourceBasePath);
				if (!fileObj.exists()) {
					boolean isCreatedDir = fileObj.mkdirs();
					System.out.printf("환경변수[%s] 경로 생성 여부[%s] 경로[%s]", propKey, isCreatedDir, innerProjectDynamicClassSourceBasePath);
					System.out.println();
					
					if (! isCreatedDir) System.exit(1);
				}
				
				
				if (!fileObj.isDirectory()) {
					String errorMessage = String.format("환경변수[%s]에 입력한 경로 값[%s]은 디렉토리가 아닙니다.", propKey, innerProjectDynamicClassSourceBasePath);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					if (innerProjectClassLoaderSourcePathTextField[i][j].isEditable()) {
						innerProjectClassLoaderSourcePathTextField[i][j].requestFocus();
						innerProjectClassLoaderSourcePathTextField[i][j].grabFocus();
					}
					return false;
				}
				
				configOfProject.setProperty(propKey, innerProjectDynamicClassSourceBasePath);
			}
			
			StringBuilder configFileBuilder = new StringBuilder(sinnoriInstallAbsPathName);
			configFileBuilder.append(File.separator);
			configFileBuilder.append("project");
			configFileBuilder.append(File.separator);
			configFileBuilder.append(projectName);
			configFileBuilder.append(File.separator);
			configFileBuilder.append("config");
			configFileBuilder.append(File.separator);
			configFileBuilder.append(MainControllerIF.SINNORI_CONFIG_FILE_NAME);
			
			File configFileObjOfProject = new File(configFileBuilder.toString());
			
			FileOutputStream sinnoriConfigFOS = null;
			OutputStreamWriter sinnoriConfigOSW = null;
			try {
				sinnoriConfigFOS = new FileOutputStream(configFileObjOfProject);
				sinnoriConfigOSW = new OutputStreamWriter(sinnoriConfigFOS, "UTF-8");
				configOfProject.store(sinnoriConfigOSW, String.format("Project[%s]'s Config File", projectName));
			} catch (Exception e) {
				e.printStackTrace();
				String errorMessage = String.format("Project[%s] sinnori config file[%s] unknown error", projectName, configFileObjOfProject.getAbsolutePath());
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return false;
			} finally {
				try {
					if (sinnoriConfigOSW != null)
						sinnoriConfigOSW.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				try {
					if (sinnoriConfigFOS != null)
						sinnoriConfigFOS.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 다음 화면 이동 이벤트 처리 클래스
	 * @author Jonghoon Won
	 *
	 */
	private class NextSwingAction extends AbstractAction {
		Step2SinnoriConfigScreen step2SinnoriConfigScreen = null;
		/**
		 * 생성자
		 * @param step2SinnoriConfigScreen 2단계 화면
		 */
		public NextSwingAction(Step2SinnoriConfigScreen step2SinnoriConfigScreen) {
			this.step2SinnoriConfigScreen = step2SinnoriConfigScreen;
			putValue(NAME, "다음");
			putValue(SHORT_DESCRIPTION, "다음 이동");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (step2SinnoriConfigScreen.saveConfig()) {
				mainController.nextStep3Screen();
			}
		}
	}

	/**
	 * 공개키 생성 주체가 API(=프로그램)인 라디오 버튼 이벤트 처리 클래스
	 * @author Jonghoon Won
	 *
	 */
	private class APIOnSwingAction extends AbstractAction {
		private JTextField targetTextField = null;
		private JButton targetJButton = null;
		
		/**
		 * 생성자
		 * @param targetTextField 공개키 경로 입력 콤포넌트 
		 * @param targetJButton 공개키 경로 선택 버튼 콤포넌트
		 */
		public APIOnSwingAction(JTextField targetTextField, JButton targetJButton) {
			this.targetTextField = targetTextField;
			this.targetJButton = targetJButton;
			
			putValue(NAME, "API");
			putValue(SHORT_DESCRIPTION, "자체적으로 공개키/개인키 생성 방법 선택");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			targetTextField.setEditable(false);
			targetJButton.setEnabled(false);
			
		}
	}
	
	/**
	 * 공개키 생성 주체가 파일인 라디오 버튼 이벤트 처리 클래스
	 * @author Jonghoon Won
	 *
	 */
	private class FileOnSwingAction extends AbstractAction {
		private JTextField targetTextField = null;
		private JButton targetJButton = null;
		
		/**
		 * 생성자
		 * @param targetTextField 공개키 경로 입력 콤포넌트 
		 * @param targetJButton 공개키 경로 선택 버튼 콤포넌트
		 */
		public FileOnSwingAction(JTextField targetTextField, JButton targetJButton) {
			this.targetTextField = targetTextField;
			this.targetJButton = targetJButton;
			
			putValue(NAME, "File");
			putValue(SHORT_DESCRIPTION, "외부 파일을 이용한 공개키/개인키 생성 방법 선택");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			targetTextField.setEditable(true);
			targetJButton.setEnabled(true);
		}
	}
}
