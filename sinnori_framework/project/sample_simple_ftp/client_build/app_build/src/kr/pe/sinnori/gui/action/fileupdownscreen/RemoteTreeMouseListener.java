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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.lib.FileUpDownScreenIF;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;

/**
 * 원격지 트리 노드 마우스 이벤트 처리 클래스
 * @author Jonghoon Won
 *
 */
public class RemoteTreeMouseListener extends MouseAdapter implements CommonRootIF {
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
	public RemoteTreeMouseListener(
			JFrame mainFrame,
			MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen, 
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode,
			String remotePathSeperator) {
		super();
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
	}
	
	public void mousePressed(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			RemoteFileTreeNode selNode = (RemoteFileTreeNode) selPath
					.getLastPathComponent();
			if (selNode.isDirectory() && e.getClickCount() == 2
					&& !selNode.isRoot()) {
				// myDoubleClick(selRow, selPath);
				log.debug(String.format("selNode.getFileName=[%s]", selNode.getFileName()));

				if (null == remotePathSeperator) {
					remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
				}
				
				StringBuilder newWorkPathBuilder = new StringBuilder(
						remoteRootNode.getFileName());
				newWorkPathBuilder.append(remotePathSeperator);
				newWorkPathBuilder.append(selNode.getFileName());
				String newWorkPath = newWorkPathBuilder.toString();

				log.debug(String.format("newWorkPath=[%s]", newWorkPath));
				

				OutputMessage fileListOutObj = mainController
						.getRemoteFileList(newWorkPath);

				if (null != fileListOutObj) {
					log.debug(fileListOutObj.toString());

					remoteRootNode.removeAllChildren();
					try {
						fileUpDownScreen.makeRemoteTreeNode(fileListOutObj,
								remoteRootNode);
						
						fileUpDownScreen.repaintTree(remoteTree);
					} catch (MessageItemException e1) {
						log.warn("MessageItemException", e1);
						JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
						return;
					}
				}
			}
		}
	}
}
