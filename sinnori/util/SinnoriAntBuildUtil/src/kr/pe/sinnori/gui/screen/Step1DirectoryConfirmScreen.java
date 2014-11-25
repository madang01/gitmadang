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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import kr.pe.sinnori.gui.util.PathSwingAction;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * 1단계 신놀이 설치 경로를 지정하는 화면 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class Step1DirectoryConfirmScreen extends JPanel {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	
	private JTextField textField;
	private final Action nextAction = new NextSwingAction();
	
	 
	private JFileChooser chooser = null;
	
	
	/**
	 * Create the panel.
	 */
	public Step1DirectoryConfirmScreen(final JFrame mainFrame, MainControllerIF mainController) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		
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
		
		JLabel titelLabel = new JLabel("1단계 신놀이 설치 경로 확인");
		add(titelLabel, "2, 2");
		
		JPanel bodyPanel = new JPanel();
		add(bodyPanel, "2, 4, fill, fill");
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.X_AXIS));
		
		textField = new JTextField();
		try {
			textField.setText(new File(".").getCanonicalFile().getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		bodyPanel.add(textField);
		textField.setColumns(30);
		
		JButton btnNewButton = new JButton("경로선택");
		PathSwingAction pathAction = new PathSwingAction(this.mainFrame, chooser, textField);
		btnNewButton.setAction(pathAction);
		bodyPanel.add(btnNewButton);
		
		JPanel bottomPanel = new JPanel();
		add(bottomPanel, "2, 6, center, center");
		
		JButton nextButton = new JButton("다음");
		nextButton.setAction(nextAction);
		bottomPanel.add(nextButton);
		// chooser.set
		// chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("폴더", "."));

	}

	/**
	 * 2단계 화면 이동 이벤트 처리 클래스
	 * @author Won Jonghoon
	 *
	 */
	private class NextSwingAction extends AbstractAction {
		/**
		 * 생성자
		 */
		public NextSwingAction() {
			putValue(NAME, "다음");
			putValue(SHORT_DESCRIPTION, "다음 이동");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String sinnoriInstallPathName = textField.getText();
			mainController.nextStep2Screen(sinnoriInstallPathName);
		}
	}
}
