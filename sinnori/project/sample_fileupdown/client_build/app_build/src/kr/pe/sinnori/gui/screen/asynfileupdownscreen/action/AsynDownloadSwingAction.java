
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

package kr.pe.sinnori.gui.screen.asynfileupdownscreen.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.gui.lib.AbstractFileTreeNode.FileType;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.screen.commonfileupdown.FileUpDownScreenIF;
import kr.pe.sinnori.impl.message.DownFileInfoResult.DownFileInfoResult;

/**
 * 다운로드 이벤트 처리 버전2 클래스
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class AsynDownloadSwingAction extends AbstractAction implements CommonRootIF {
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
	public AsynDownloadSwingAction(JFrame mainFrame,
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

	/**
	 * 다운로드 이어받기/덮어쓰기/취소 여부를 묻는 창
	 * @param remoteFileName 사용자가 다운로드 하겠다고 선택한 원격지 파일 이름
	 * @param localWorkPathName 로컬 파일 작업 경로
	 * @return 사용자의 이어받기/덮어쓰기/취소 선택값, 디폴트 이어받기, 단 로컬에 원격지에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION,  
	 *  
	 */
	private int getYesNoCancel(String remoteFileName,  String localWorkPathName) {
		Object[] options = {"이어받기",
		"덮어쓰기",
		"취소"};
		int yesNoCancelOption = JOptionPane.showOptionDialog(mainFrame,
				String
				.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
						remoteFileName, localWorkPathName),
		"이어받기 확인창",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
		
		return yesNoCancelOption;
	}
	
	
	
	/**
	 * <pre>
	 * 다운로드할 파일과 같은 이름을 갖는 로컬 트리 노드를 얻는다.
	 * 만약 없다면 null 를 반환한다.
	 * </pre>
	 *   
	 * @param remoteFileName 사용자가 다운로드 하겠다고 선택한 원격지 파일 이름
	 * @return 다운로드할 파일과 같은 이름을 갖는 로컬 트리 노드, 만약 다운로드할 파일과 같은 이름 같은 로컬 트리 노드가 없다면 null 를 리턴한다.
	 */
	private LocalFileTreeNode getLocalTreeNodeHavingSameFileName(String remoteFileName) {
		
		int cntOfChild = localRootNode.getChildCount();
		for (int i=0;i < cntOfChild; i++) {
			LocalFileTreeNode localFileTreeNode = (LocalFileTreeNode)localRootNode.getChildAt(i);
			String localTempFileName = localFileTreeNode.getFileName();
			if (localTempFileName.equals(remoteFileName)) {
				return localFileTreeNode;
			}			
		}
		return null;
	}
	
	private enum UserSelectableMode {NON_USER_SELECTABLE, USER_SELECTABLE};
	
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

		boolean append = false;
		String localFilePathName = (String)localRootNode.getUserObject();
		String localFileName = "";
		long localFileSize = 0L;
		String remoteFilePathName = remoteRootNode.getFileName();
		String remoteFileName = remoteSelectedNode.getFileName();
		long remoteFileSize = remoteSelectedNode.getFileSize();
		if (0 == remoteFileSize) {
			String errorMessage = "다운로드할 파일 크기가 0 입니다.";
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		
		
		int fileBlockSize = mainController.getFileBlockSize();

		LocalFileTreeNode localFileTreeNode = null;
		UserSelectableMode userSelectableMode = UserSelectableMode.NON_USER_SELECTABLE;
		long totalReceivedDataSize = 0L;
		
		TreePath localSelectedPath = localTree.getSelectionPath();
		if (null != localSelectedPath) {
			LocalFileTreeNode localSelectedNode = (LocalFileTreeNode) localSelectedPath
					.getLastPathComponent();
			
			if (localSelectedNode.isRoot()) {
				localFileTreeNode = getLocalTreeNodeHavingSameFileName(remoteFileName);				
			} else {
				userSelectableMode = UserSelectableMode.USER_SELECTABLE;
				localFileTreeNode = localSelectedNode;
			}
		} else {			
			localFileTreeNode = getLocalTreeNodeHavingSameFileName(remoteFileName);			
		}
		
		
		if (null == localFileTreeNode) {
			/** 중복된 이름을 갖는 로컬 트리 노드가 없다면 덮어쓰기로 설정 */
			append = false;
		} else {
			localFileName = localFileTreeNode.getFileName();
			localFileSize = localFileTreeNode.getFileSize();
			 
			if (localFileTreeNode.getFileType() == FileType.Directory) {
				if (userSelectableMode == UserSelectableMode.NON_USER_SELECTABLE) {				
					/**
					 * <pre> 
					 * 사용자가 직접 다운로드 파일이 위치할 경로를 지정 하지 않았을 경우
					 * 다운로드 하고자 하는 파일과 동일한 이름의 경로가 존재하므로
					 * 수행 불가 메시지를 보여주고 처리 종료.
					 * </pre>
					 */
					
					JOptionPane.showMessageDialog(mainFrame, "다운로드 하고자 하는 원격지 파일과 동일한 이름을 갖는 로컬 경로가 존재합니다.");
					localTree.setSelectionPath(new TreePath(localFileTreeNode.getPath()));
					return;			
				} else {				
					/** 사용자가 직접 다운로드 파일이 위치할 경로를 지정 했을 경우 경로명과 파일명 재 조정후 덮어쓰기로 설정 */
					StringBuilder targetPathBuilder = new StringBuilder(localFilePathName);
					targetPathBuilder.append(File.separator);
					targetPathBuilder.append(localFileName);
					localFilePathName = targetPathBuilder.toString();
					localFileName =  "";
					
					/** 덮어쓰기 */
					append = false;
				}
			} else {
				if (localFileSize > 0) {
					/** 다운로드 하고자 하는 파일과 동일한 이름의 파일의 크기가 0보다 큰 경우 이어붙이기/덮어쓰기/취소 여부 묻기 */
					int yesNoCancel = getYesNoCancel(remoteFileName, localFilePathName);
					/** 취소 */
					if (JOptionPane.CANCEL_OPTION == yesNoCancel) return;
					
					if (JOptionPane.NO_OPTION == yesNoCancel) {
						/** 덮어쓰기 */
						append = false;
						totalReceivedDataSize = localFileSize;
					} else {
						/** 이어 받기 */
						append = true;
					}
				} else {
					/** 다운로드 하고자 하는 파일과 동일한 이름의 파일의 크기가 0인 경우 덮어쓰기로 설정 */
					append = false;
				}
			}					
		}
		
		
		// FIXME!
		log.info(String.format("copy remoteFilePathName[%s] remoteFileName[%s] to localFilePathName[%s] localFileName[%s]",
				remoteFilePathName, remoteFileName, localFilePathName,  localFileName));
		
		
		DownFileInfoResult downFileInfoResultOutObj = mainController
				.readyDownloadFile(append, localFilePathName, localFileName, localFileSize,
						remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
		
		if (null == downFileInfoResultOutObj) {
			mainController.freeLocalTargetFileResource();
			return;
		}
		
		int serverSourceFileID = -1;
		/*try {
			serverSourceFileID = (Integer)downFileInfoResulOutObj.getAttribute("serverSourceFileID");
		} catch (MessageItemException e1) {
			log.warn("MessageItemException", e1);
			
			JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
			return;
		}*/
		serverSourceFileID = downFileInfoResultOutObj.getServerSourceFileID();
		
		/**
		 * 서버에서 다운로드할 준비가 되었다면 로컬 목적지 파일 크기를 0으로 재조정, 만약 중복 받기이면 크기 0 으로 이어받기이면 아무 동작 안한다.
		 */
		if (!mainController.makeZeroToDownloadFileSize()) return;
		
		mainController.openDownloadProcessDialog(serverSourceFileID, 
				new StringBuilder(remoteFileName).append(" 다운로드 중...").toString(), remoteFileSize, totalReceivedDataSize);
	}
}
