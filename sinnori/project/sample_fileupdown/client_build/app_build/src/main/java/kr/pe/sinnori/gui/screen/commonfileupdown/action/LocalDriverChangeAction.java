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

package kr.pe.sinnori.gui.screen.commonfileupdown.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.screen.commonfileupdown.FileUpDownScreenIF;

/**
 * MS사 윈도우 OS 류에서 사용하는 로컬 드라이브 목록 변경 이벤트 처리 클래스.
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class LocalDriverChangeAction extends AbstractAction {
	private Logger log = LoggerFactory.getLogger(LocalDriverChangeAction.class);
	
	
	private JFrame mainFrame = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree localTree = null;
	private LocalFileTreeNode localRootNode = null;
	
	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 * @param localTree 로컬 트리
	 * @param localRootNode 로컬 루트 노드
	 */
	public LocalDriverChangeAction(JFrame mainFrame, 
			FileUpDownScreenIF fileUpDownScreen, 
			JTree localTree,
			LocalFileTreeNode localRootNode) {
		this.mainFrame = mainFrame;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>)e.getSource();

		int selectedInx = cb.getSelectedIndex();
		if (selectedInx > 0) {
			String driverName = (String)cb.getSelectedItem();
			
			StringBuilder newWorkPathBuilder = new StringBuilder(driverName);
			// newWorkPathBuilder.append(File.separator);
			// newWorkPathBuilder.append(selNode.getFileName());
			String newWorkPath = newWorkPathBuilder.toString();

			log.debug(String.format("newWorkPath=[%s]", newWorkPath));
			

			File localSelectedPathFile = new File(newWorkPath);

			if (!localSelectedPathFile.exists()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 존재하지 않습니다.", newWorkPath));

				JOptionPane.showMessageDialog(mainFrame, "선택된 디렉토리가 존재하지 않습니다");
				return;
			}

			if (!localSelectedPathFile.isDirectory()) {
				// log.debug(String.format("선택된 디렉토리[%s]가 디렉토리가 아닙니다.", newWorkPath));
				
				JOptionPane.showMessageDialog(mainFrame,
						"선택된 디렉토리가 디렉토리가 아닙니다.");
				return;
			}

			localRootNode.changeFileObj(localSelectedPathFile);
			localRootNode.removeAllChildren();
			fileUpDownScreen.makeLocalTreeNode(localRootNode);
			fileUpDownScreen.repaintTree(localTree);
		}
	}
}
