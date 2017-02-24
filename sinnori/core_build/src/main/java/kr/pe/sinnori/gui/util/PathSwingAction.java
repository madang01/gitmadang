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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 경로 선택 이벤트 처리 클래스
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class PathSwingAction extends AbstractAction {
	private Logger log = LoggerFactory.getLogger(PathSwingAction.class);
	
	private Component parentComponent = null;
	private JTextField pathTextField = null;
	private JFileChooser pathChooser = null;
	
	
	/**
	 * 
	 * @param sourcePathTextField the parameter sourcePathTextField is TextField component whose value is path
	 * @param sourcePathTextFieldName parameter sourcePathTextField's name
	 * @return the writable and readable path
	 * @throws RuntimeException but if parameter  sourcePathTextField value is not a valid path then throw RuntimeException.
	 */
	private File getWitableAndReadablePathFromTextField(JTextField sourcePathTextField,
			String sourcePathTextFieldName) throws RuntimeException {
		String sourcePathString = sourcePathTextField.getText();
		if (null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is nul",
					sourcePathTextFieldName);
			throw new RuntimeException(errorMessage);
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);

		if (sourcePathString.equals("")) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is empty",
					sourcePathTextFieldName);
			throw new RuntimeException(errorMessage);
		}

		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s][%s] doesn't exist", sourcePathTextFieldName,
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s][%s] is not a directory", sourcePathTextFieldName,
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.canRead()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to read", sourcePathTextFieldName,
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.canWrite()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to write", sourcePathTextFieldName,
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		return sourcePath;
	}
	
	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param pathChooser 파일 상자 콤포넌트
	 * @param pathTextField 경로 선택시 그 값이 들어갈 입력 박스 컴포넌트
	 */
	public PathSwingAction(Component parentComponent, JFileChooser pathChooser, JTextField pathTextField) {
		this.parentComponent= parentComponent;
		this.pathChooser = pathChooser;
		this.pathTextField = pathTextField;
		
		putValue(NAME, "Path");
		putValue(SHORT_DESCRIPTION, "select path dialog");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		File currrentWorkPath = null;
		try {
			currrentWorkPath = getWitableAndReadablePathFromTextField(pathTextField,  "the message information path");
		} catch(RuntimeException e1) {
			String errorMessage = e1.toString();
			log.info(errorMessage);
			
			currrentWorkPath = new File(".");
		}
		
		pathChooser.setCurrentDirectory(currrentWorkPath);
		
		int returnVal = pathChooser.showOpenDialog(parentComponent);
		if (JFileChooser.APPROVE_OPTION == returnVal) {
			File selectedFile = pathChooser.getSelectedFile();
			pathTextField.setText(selectedFile.getAbsolutePath());
		}
	}
}
