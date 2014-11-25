package kr.pe.sinnori.gui.util;
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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * 경로 선택 이벤트 처리 클래스
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class PathSwingAction extends AbstractAction {
	private JFrame mainFrame = null;
	private JTextField targetTextField = null;
	private JFileChooser chooser = null;
	
	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param chooser 파일 상자 콤포넌트
	 * @param targetTextField 경로 선택시 그 값이 들어갈 입력 박스 컴포넌트
	 */
	public PathSwingAction(JFrame mainFrame, JFileChooser chooser, JTextField targetTextField) {
		this.mainFrame= mainFrame;
		this.chooser = chooser;
		this.targetTextField = targetTextField;
		putValue(NAME, "경로선택");
		putValue(SHORT_DESCRIPTION, "경로 선택 박스");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int returnVal = chooser.showOpenDialog(mainFrame);
		if (JFileChooser.APPROVE_OPTION == returnVal) {
			File selectedFile = chooser.getSelectedFile();
			targetTextField.setText(selectedFile.getAbsolutePath());
		}
	}
}
