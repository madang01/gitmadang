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

import kr.pe.sinnori.common.etc.CommonType.READ_WRITE_MODE;
import kr.pe.sinnori.common.util.CommonStaticUtil;

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
	 * @param readWriteMode read/write mode
	 * @return the valid path
	 * @throws RuntimeException if the file is not a valid path. then throw it
	 */
	private File getValidPathFromTextField(JTextField sourcePathTextField, READ_WRITE_MODE	readWriteMode) throws RuntimeException {
		String sourcePathString = sourcePathTextField.getText();
		if (null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is null",
					sourcePathTextField.getName());
			throw new RuntimeException(errorMessage);
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);

		File sourcePath = null;
		try {
			sourcePath =CommonStaticUtil.getValidPath(sourcePathString, readWriteMode);
		} catch(RuntimeException e) {
			String errorMessage = e.toString();
			throw new RuntimeException(String.format("parameter sourcePathTextField[%s]'s value is not a valid path::%s", sourcePathTextField.getName(), errorMessage));
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
			currrentWorkPath = getValidPathFromTextField(pathTextField, READ_WRITE_MODE.ONLY_READ);
		} catch(RuntimeException e1) {
			String errorMessage = e1.getMessage();
			log.info("this JFileChooser sets the current directory to current working directory because {}", errorMessage);
			
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
