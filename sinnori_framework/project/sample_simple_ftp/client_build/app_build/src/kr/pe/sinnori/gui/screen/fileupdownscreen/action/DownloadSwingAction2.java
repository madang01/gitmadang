
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

package kr.pe.sinnori.gui.screen.fileupdownscreen.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.lib.AbstractFileTreeNode;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.screen.fileupdownscreen.FileUpDownScreenIF;

/**
 * 다운로드 이벤트 처리 버전2 클래스
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class DownloadSwingAction2 extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree localTree = null;
	private LocalFileTreeNode localRootNode = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;

	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mainController 메인 제어자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 * @param localTree 로컬 트리
	 * @param localRootNode 로컬 루트 노드
	 * @param remoteTree 원격지 트리
	 * @param remoteRootNode 원격지 루트 노드
	 * @param remotePathSeperator 원격지 파일 구분자. 참고) 원격지 파일 목록을 요청하기전에 생성시에는 null 값이다.
	 */
	public DownloadSwingAction2(JFrame mainFrame,
			MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen,
			JTree localTree,
			LocalFileTreeNode localRootNode,
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode, 
			String remotePathSeperator) {
		super();

		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
		
		
		putValue(NAME, "downlaod");
		putValue(SHORT_DESCRIPTION, "download remote file to client");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));

		if (null == remotePathSeperator) {
			remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
		}
		
		
		TreePath remoteSelectedPath = remoteTree.getSelectionPath();
		if (null == remoteSelectedPath) {
			JOptionPane.showMessageDialog(mainFrame, "원격지 파일을 선택해 주세요.");
			return;
		}

		RemoteFileTreeNode remoteSelectedNode = (RemoteFileTreeNode) remoteSelectedPath
				.getLastPathComponent();

		if (remoteSelectedNode.isDirectory()) {
			JOptionPane.showMessageDialog(mainFrame,
					"원격지 디렉토리를 선택하였습니다. 원격지 파일을 선택해 주세요.");
			return;
		}
				
		String localFilePathName = (String)localRootNode.getUserObject();
		String localFileName = "";
		String remoteFilePathName = remoteRootNode.getFileName();
		String remoteFileName = remoteSelectedNode.getFileName();
		long remoteFileSize = remoteSelectedNode.getFileSize();
		if (0 == remoteFileSize) {
			String errorMessage = "다운 로드할 파일 크기가 0 입니다.";
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		
		
		int fileBlockSize = mainController.getFileBlockSize();

		TreePath localSelectedPath = localTree.getSelectionPath();
		if (null != localSelectedPath) {
			LocalFileTreeNode localSelectedNode = (LocalFileTreeNode) localSelectedPath
					.getLastPathComponent();
			if (AbstractFileTreeNode.FileType.File == localSelectedNode
					.getFileType()) {
				int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
						.format("원격지 파일[%s]을 로컬 파일[%s]에 덮어 쓰시겠습니까?",
								remoteFileName,
								localSelectedNode.getFileName()), "덮어쓰기 확인창",
						JOptionPane.YES_NO_OPTION);
				if (JOptionPane.NO_OPTION == yesOption)
					return;

				localFileName = localSelectedNode.getFileName();
			} else {
				StringBuilder targetPathBuilder = new StringBuilder(localFilePathName);
				targetPathBuilder.append(File.separator);
				targetPathBuilder.append(localSelectedNode.getFileName());
				localFilePathName = targetPathBuilder.toString();
				// targetPath = targetPath + File.separator +
				// localSelectedNode.getFileName();
			}
		} else {
			int cntOfChild = localRootNode.getChildCount();
			for (int i=0;i < cntOfChild; i++) {
				LocalFileTreeNode localFileTreeNode = (LocalFileTreeNode)localRootNode.getChildAt(i);
				String localTempFileName = localFileTreeNode.getFileName();
				if (localTempFileName.equals(remoteFileName)) {
					int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
							.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
									remoteFileName, localFilePathName), "덮어쓰기 확인창",
							JOptionPane.YES_NO_OPTION);
					if (JOptionPane.NO_OPTION == yesOption) return;
					break;
				}
			}
		}
		
		
		// FIXME!
		log.info(String.format("copy remoteFilePathName[%s] remoteFileName[%s] to localFilePathName[%s] localFileName[%s]",
				remoteFilePathName, remoteFileName, localFilePathName,  localFileName));
		
		
		OutputMessage downFileInfoResulOutObj = mainController
				.readyDownloadFile(localFilePathName, localFileName,
						remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
		
		if (null == downFileInfoResulOutObj) {
			mainController.freeLocalTargetFileResource();
			return;
		}
		
		int serverSourceFileID = -1;
		try {
			serverSourceFileID = (Integer)downFileInfoResulOutObj.getAttribute("serverSourceFileID");
		} catch (MessageItemException e1) {
			log.warn("MessageItemException", e1);
			
			JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
			return;
		}
		
		mainController.openDownloadProcessDialog(serverSourceFileID, new StringBuilder(remoteFileName).append(" 다운로드 중...").toString(), remoteFileSize);
	}
}
