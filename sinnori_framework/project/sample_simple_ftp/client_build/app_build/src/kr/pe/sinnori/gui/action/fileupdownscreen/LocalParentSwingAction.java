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

package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.lib.FileUpDownScreenIF;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;

/**
 * 로컬 부모 경로로 이동 이벤트 처리 클래스
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class LocalParentSwingAction extends AbstractAction implements CommonRootIF {
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
	public LocalParentSwingAction(JFrame mainFrame, 
			FileUpDownScreenIF fileUpDownScreen, 
			JTree localTree,
			LocalFileTreeNode localRootNode) {
		this.mainFrame = mainFrame;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
		
		putValue(NAME, "..");
		putValue(SHORT_DESCRIPTION, "로컬 작업 경로를 부모 경로로 변경하는 이벤트");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.printf(
				"localParentSwingAction::call actionPerformed [%d]",
				e.getID());
		System.out.println("");

		File localParntePathFile = localRootNode.getFileObj().getParentFile();

		if (null == localParntePathFile) {
			// log.debug("localParntePathFile is null");

			JOptionPane.showMessageDialog(mainFrame,
					"로컬 루트 디렉토리로 상위 디렉토리가 없습니다.");
			return;
		}

		localRootNode.changeFileObj(localParntePathFile);
		localRootNode.removeAllChildren();

		fileUpDownScreen.makeLocalTreeNode(localRootNode);
		fileUpDownScreen.repaintTree(localTree);
	}
}
