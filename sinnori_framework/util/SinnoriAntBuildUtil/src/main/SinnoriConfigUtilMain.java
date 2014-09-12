package main;
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

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.screen.MainControllerIF;
import kr.pe.sinnori.gui.screen.Step1DirectoryConfirmScreen;
import kr.pe.sinnori.gui.screen.Step2SinnoriConfigScreen;
import kr.pe.sinnori.gui.screen.Step3AntAndShellConfigScreen;

/**
 * 신놀이 설치 경로 기준 Ant 개발 환경 제공 도우미 메인
 * @author Jonghoon Won
 *
 */
public class SinnoriConfigUtilMain implements MainControllerIF {

	private JFrame mainFrame = null;
	private Step1DirectoryConfirmScreen step1DirectoryConfirmScreen = null;
	private Step2SinnoriConfigScreen step2SinnoriConfigScreen = null;
	private Step3AntAndShellConfigScreen step3AntAndShellConfigScreen = null;
	// private FileProcessDialog fileProcessDialog = null;
	
	private File sinnoriInstallPathFileObj = null;
	private String sinnoriInstallAbsPathName = null;
	/**
	 * 메인 프로젝트는 설정 파일을 갖는 프로젝트이다.
	 */
	private ArrayList<String> mainProjectList = new ArrayList<String>();
	private HashMap<String, SequencedProperties> project2ConfigHash = new HashMap<String, SequencedProperties>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SinnoriConfigUtilMain window = new SinnoriConfigUtilMain();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the application.
	 */
	public SinnoriConfigUtilMain() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		step1DirectoryConfirmScreen = new Step1DirectoryConfirmScreen(mainFrame, this);
		
		mainFrame.add(step1DirectoryConfirmScreen);
		
		// mainFrame.setSize(520, 130);
		mainFrame.pack();
		
		step1DirectoryConfirmScreen.setVisible(true);
	}
	
	@Override
	public void nextStep2Screen(String sinnoriInstalledPathName) {
		sinnoriInstallPathFileObj = new File(sinnoriInstalledPathName);
		if (!sinnoriInstallPathFileObj.exists()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로가 존재하지 않습니다.");
			return;
		}
		
		try {
			sinnoriInstallPathFileObj = sinnoriInstallPathFileObj.getCanonicalFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로 파일 객체화 하는 과정에서 입출력 에러 발생");
			e1.printStackTrace();
			return;
		}
		
		sinnoriInstallAbsPathName = sinnoriInstallPathFileObj.getAbsolutePath();
		
		StringBuilder projectPathNameBuilder = new  StringBuilder(sinnoriInstallAbsPathName);
		projectPathNameBuilder.append(File.separator);
		projectPathNameBuilder.append("project");
		
		File projectPathFileObj = new File(projectPathNameBuilder.toString());
		
		if (!projectPathFileObj.exists()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로에 project 디렉토리가 존재하지 않습니다.");
			return;
		}
		
		if (!projectPathFileObj.isDirectory()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로의 project 는 디렉토리가 아닙니다.");
			return;
		}
		
		File[] projectFileObjList = projectPathFileObj.listFiles();
		
		// ArrayList<String> projectList  = new ArrayList<String>();
		mainProjectList.clear();
		for (int i=0; i < projectFileObjList.length; i ++) {
			File f = projectFileObjList[i];
			if (f.isDirectory()) {
				StringBuilder configFileBuilder = new StringBuilder(f.getAbsolutePath());
				configFileBuilder.append(File.separator);
				configFileBuilder.append("config");
				configFileBuilder.append(File.separator);
				configFileBuilder.append(MainControllerIF.SINNORI_CONFIG_FILE_NAME);
				
				File configFileObjOfProject = new File(configFileBuilder.toString());
				if (configFileObjOfProject.exists() && configFileObjOfProject.isFile()) {
					if (!configFileObjOfProject.canRead()) {
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]은 읽기 권한이 없습니다.", f.getName());
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
					
					if (!configFileObjOfProject.canWrite()) {
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]은 쓰기 권한이 없습니다.", f.getName());
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
					
					String projectName = f.getName();
					
					FileInputStream fis_sinnoriConfig_file = null;
					InputStreamReader isr_sinnoriConfig_file = null;
					try {

						fis_sinnoriConfig_file = new FileInputStream(configFileObjOfProject);

						isr_sinnoriConfig_file = new InputStreamReader(
								fis_sinnoriConfig_file, "UTF-8");

						SequencedProperties configFileProperties = new SequencedProperties();
						
						configFileProperties.load(isr_sinnoriConfig_file);
						
						/**
						 * 메인 프로젝트는 설정 파일을 갖는 프로젝트이다.
						 */
						mainProjectList.add(projectName);
						project2ConfigHash.put(projectName, configFileProperties);

					} catch (Exception e) {
						e.printStackTrace();
						
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]을 읽어 Properties 객채 생성하는 과정에서 입출력 에러가 발생하여 실패하였습니다.", projectName);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					} finally {
						try {
							if (isr_sinnoriConfig_file != null)
								isr_sinnoriConfig_file.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						try {
							if (fis_sinnoriConfig_file != null)
								fis_sinnoriConfig_file.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} 
				}
			}
		}
		
		step2SinnoriConfigScreen = new Step2SinnoriConfigScreen(mainFrame, this, sinnoriInstallAbsPathName, mainProjectList, project2ConfigHash);
		mainFrame.add(step2SinnoriConfigScreen);
		
		step1DirectoryConfirmScreen.setVisible(false);
		mainFrame.remove(step1DirectoryConfirmScreen);
		
		// mainFrame.setSize(800, 320);
		mainFrame.pack();
		step2SinnoriConfigScreen.setVisible(true);
		
	}
	
	@Override
	public void nextStep3Screen() {
		step3AntAndShellConfigScreen = new Step3AntAndShellConfigScreen(mainFrame, this, sinnoriInstallAbsPathName, mainProjectList);
		mainFrame.add(step3AntAndShellConfigScreen);
		
		step2SinnoriConfigScreen.setVisible(false);
		mainFrame.remove(step2SinnoriConfigScreen);

		// mainFrame.setSize(690, 380);
		mainFrame.pack();
		
		step3AntAndShellConfigScreen.setVisible(true);
		
	}
	
	@Override
	public void finish() {
		mainFrame.dispose();
		System.out.println("작업 완료");
	}
}
