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

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.screen.commonfileupdown.FileUpDownScreenIF;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;

/**
 * MS사 윈도우 OS 류에서 사용하는 원격지 드라이브 목록 변경 이벤트 처리 클래스.
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class RemoteDriverChangeAction extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;
	

	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mainController 메인 제어자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 * @param remoteTree 원격 트리
	 * @param remoteRootNode 원격 루트 노드
	 * @param remotePathSeperator 원격지 파일 구분자
	 */
	public RemoteDriverChangeAction(JFrame mainFrame, MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen, 
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode,
			String remotePathSeperator) {
		this.mainFrame= mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
		
		
		putValue(NAME, "remoteDriverChange");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>)e.getSource();
		int selectedInx = cb.getSelectedIndex();
		if (selectedInx > 0) {
			if (null == remotePathSeperator) {
				remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
			}
			
			String driverName = (String)cb.getSelectedItem();
			
			StringBuilder newWorkPathBuilder = new StringBuilder(driverName);
			
			// newWorkPathBuilder.append(remotePathSeperator);
			// newWorkPathBuilder.append(selNode.getFileName());
			String newWorkPath = newWorkPathBuilder.toString();

			log.debug(String.format("newWorkPath=[%s]", newWorkPath));
			

			FileListResult fileListOutObj = mainController
					.getRemoteFileList(newWorkPath);

			if (null != fileListOutObj) {
				log.debug(fileListOutObj.toString());

				remoteRootNode.removeAllChildren();
				try {
					fileUpDownScreen.makeRemoteTreeNode(fileListOutObj, remoteRootNode);
					fileUpDownScreen.repaintTree(remoteTree);
				} catch (Exception e1) {
					log.warn("Exception", e1);
					JOptionPane.showMessageDialog(mainFrame, "unknwon error::"+e1.getMessage());
					return;
				}
				
			}
		} 
	}

}
