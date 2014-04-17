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

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.lib.AbstractFileTreeNode.FileType;
import kr.pe.sinnori.gui.screen.fileupdownscreen.FileUpDownScreenIF;

/**
 * 업로드 이벤트 처리 클래스
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class UploadSwingAction extends AbstractAction implements CommonRootIF {
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
	public UploadSwingAction(JFrame mainFrame,
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

		putValue(NAME, "upload");
		putValue(SHORT_DESCRIPTION, "upload local file to server");
	}
	
	/**
	 * 업로드 이어받기/덮어쓰기/취소 여부를 묻는 창
	 * @param localFileName 사용자가 업로드 하겠다고 선택한 로컬 파일 이름
	 * @param remoteFilePathName 원격지 파일 작업 경로
	 * @return 사용자의 이어받기/덮어쓰기/취소 선택값, 디폴트 이어받기, 단 원격지에 로컬에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION,  
	 */
	private int getYesNoCancel(String localFileName,  String remoteFilePathName) {
		Object[] options = {"이어받기",
		"덮어쓰기",
		"취소"};
		int yesNoCancelOption = JOptionPane.showOptionDialog(mainFrame,
				String
				.format("로컬 파일[%s]과 동일한 파일이 원격지 작업 경로[%s]에 존재합니다. 이어받기/덮어쓰기/취소를 선택하세요",
						localFileName, remoteFilePathName),
		"이어받기 확인창",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
		
		return yesNoCancelOption;
	}
	
	/**
	 * 원격지에 로컬에서 선택한 파일과 같은 파일 이름이 있고 파일 크기가 0 보다 크다면,
	 * 사용자에게 업로드 이어받기/덮어쓰기/취소 여부를 물어 그 결과 값을 반환한다.
	 * 단 원격지에 로컬에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기(JOptionPane.NO_OPTION)로 반환한다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION,
	 *   
	 * @param localFileName 사용자가 업로드 하겠다고 선택한 로컬 파일 이름
	 * @param remoteWorkPathName 원격지 파일 작업 경로
	 * @return 사용자의 이어받기/덮어쓰기/취소 선택값, 단 원격지에 로컬에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION
	 */
	private int getYesNoCancelOfRemoteRootNode(String localFileName,  String remoteWorkPathName) {
		int cntOfChild = remoteRootNode.getChildCount();
		for (int i=0;i < cntOfChild; i++) {
			RemoteFileTreeNode remoteFileTreeNode = (RemoteFileTreeNode)remoteRootNode.getChildAt(i);
			if (remoteFileTreeNode.getFileType() == FileType.Directory) continue;
			
			String remoteTempFileName = remoteFileTreeNode.getFileName();
			long remoteTempFileSize = remoteFileTreeNode.getFileSize();
			if (remoteTempFileName.equals(localFileName)) {
				if (remoteTempFileSize > 0) {
					int yesNoCancelOption = getYesNoCancel(localFileName, remoteWorkPathName);
					return yesNoCancelOption;
				}
				break;
			}
		}
		return JOptionPane.NO_OPTION;
	}

	public void actionPerformed(ActionEvent e) {
		log.info(String.format("e.getID=[%d]", e.getID()));
		
		
		if (null == remotePathSeperator) {
			remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
		}

		TreePath localSelectedPath = localTree.getSelectionPath();
		if (null == localSelectedPath) {
			JOptionPane.showMessageDialog(mainFrame, "로컬 파일을 선택해 주세요.");
			return;
		}

		LocalFileTreeNode localSelectedNode = (LocalFileTreeNode) localSelectedPath
				.getLastPathComponent();

		if (localSelectedNode.isDirectory()) {
			JOptionPane.showMessageDialog(mainFrame,
					"로컬 디렉토리를 선택하였습니다. 로컬 파일을 선택해 주세요.");
			return;
		}

		String localFilePathName = (String)localRootNode.getUserObject();
		String localFileName = localSelectedNode.getFileName();
		long localFileSize = localSelectedNode.getFileSize();
		if (0 == localFileSize) {
			String errorMessage = "업 로드할 파일 크기가 0 입니다.";
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		
		String remoteFilePathName = (String)remoteRootNode.getUserObject();
		String remoteFileName = "";
		long remoteFileSize = 0L;
		int fileBlockSize = mainController.getFileBlockSize();
		boolean append = false;

		TreePath remoteSelectedPath = remoteTree.getSelectionPath();
		if (null != remoteSelectedPath) {
			RemoteFileTreeNode remoteSelectedNode = (RemoteFileTreeNode) remoteSelectedPath
					.getLastPathComponent();
			
			if (remoteSelectedNode.isRoot()) {
				/*int cntOfChild = remoteRootNode.getChildCount();
				for (int i=0;i < cntOfChild; i++) {
					RemoteFileTreeNode remoteFileTreeNode = (RemoteFileTreeNode)remoteRootNode.getChildAt(i);
					String remoteTempFileName = remoteFileTreeNode.getFileName();
					if (remoteTempFileName.equals(localFileName)) {
						int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
								.format("로컬 파일[%s]과 동일한 파일이 원격지 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
										localFileName, remoteFilePathName), "덮어쓰기 확인창",
								JOptionPane.YES_NO_OPTION);
						if (JOptionPane.NO_OPTION == yesOption) return;
						break;
					}
				}*/
				int yesNoCancelOption = getYesNoCancelOfRemoteRootNode(localFileName, remoteFilePathName);
				/** 취소 */
				if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
				
				if (JOptionPane.NO_OPTION == yesNoCancelOption) {
					/** 덮어쓰기 */
					append = false;
				} else {
					/** 이어 받기 */
					append = true;
				}
			} else {
				if (RemoteFileTreeNode.FileType.File == remoteSelectedNode
						.getFileType()) {
					/*int yesOption = JOptionPane.showConfirmDialog(mainFrame,
							String.format("로컬 파일[%s]을 원격지 파일[%s]에 덮어 쓰시겠습니까?",
									localFileName,
									remoteSelectedNode.getFileName()),
							"덮어쓰기 확인창", JOptionPane.YES_NO_OPTION);
	
					if (JOptionPane.NO_OPTION == yesOption)
						return;
						
					remoteFileName = remoteSelectedNode.getFileName();
					*/
					
					remoteFileSize = remoteSelectedNode.getFileSize();
					remoteFileName = remoteSelectedNode.getFileName();
					
					if (0 == remoteFileSize) {
						/** 덮어쓰기 */
						append = false;
					} else {
						int yesNoCancelOption = getYesNoCancel(localFileName, remoteFilePathName);
						
						/** 취소 */
						if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
						
						if (JOptionPane.NO_OPTION == yesNoCancelOption) {
							/** 덮어쓰기 */
							append = false;
						} else {
							/** 이어 받기 */
							append = true;
						}
					}				
					
				} else {
					/** 업로드한 파일의 위치로 원격지 자식 디렉토리를 선택한 경우 */
					StringBuilder targetPathBuilder = new StringBuilder(remoteFilePathName);
					targetPathBuilder.append(remotePathSeperator);
					targetPathBuilder.append(remoteSelectedNode.getFileName());
					remoteFilePathName = targetPathBuilder.toString();
					
					/** 덮어쓰기 */
					append = false;
				}
			}
		} else {
			/*int cntOfChild = remoteRootNode.getChildCount();
			for (int i=0;i < cntOfChild; i++) {
				RemoteFileTreeNode remoteFileTreeNode = (RemoteFileTreeNode)remoteRootNode.getChildAt(i);
				String remoteTempFileName = remoteFileTreeNode.getFileName();
				if (remoteTempFileName.equals(localFileName)) {
					int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
							.format("로컬 파일[%s]과 동일한 파일이 원격지 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
									localFileName, remoteFilePathName), "덮어쓰기 확인창",
							JOptionPane.YES_NO_OPTION);
					if (JOptionPane.NO_OPTION == yesOption) return;
					break;
				}
			}*/
			int yesNoCancelOption = getYesNoCancelOfRemoteRootNode(localFileName, remoteFilePathName);
			/** 취소 */
			if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
			
			if (JOptionPane.NO_OPTION == yesNoCancelOption) {
				/** 덮어쓰기 */
				append = false;
			} else {
				/** 이어 받기 */
				append = true;
			}
		}

		// FIXME!
		log.info(String.format("copy localFilePathName[%s] localFileName[%s] to remoteFilePathName[%s] remoteFileName[%s]",
				localFilePathName,  localFileName, remoteFilePathName, remoteFileName));

		try {
			OutputMessage upFileInfoResulOutObj = mainController
					.readyUploadFile(append, localFilePathName, localFileName, localFileSize, 
							remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);

			/** 정상적인 업로드 파일 준비 출력 메시지를 받지 못했을 경우 처리 종료 */
			if (null == upFileInfoResulOutObj) {
				mainController.freeLocalSourceFileResource();
				return;
			}
			
			// FIXME!
			log.info(upFileInfoResulOutObj.toString());
				

			int serverTargetFileID = -1;
			try {
				serverTargetFileID = (Integer) upFileInfoResulOutObj.getAttribute("serverTargetFileID");
			} catch (MessageItemException e1) {
				log.warn("MessageItemException", e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				return;
			}
			
			
			mainController.openUploadProcessDialog(serverTargetFileID, new StringBuilder(localFileName).append(" 업로드 중...").toString(), localFileSize);
			
			/*
			int localFileBlockMaxNo = localSourceFileResource.getFileBlockMaxNo();
			int fileBlockNo = 0;
			for (; fileBlockNo < localFileBlockMaxNo; fileBlockNo++) {
				boolean isCanceled = fileUpDownScreen.getIsCancelFileTransfer();
				if (isCanceled) {
					fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
					
					OutputMessage cancelUploadFileResultOutObj = mainController.cancelUploadFile(serverTargetFileID);
					// 서버 업로드 취소 성공시 루프 종료 
					if (null != cancelUploadFileResultOutObj) break;
				}
				
				
				byte fileData[] =  localSourceFileResource.getByteArrayOfFileBlockNo(fileBlockNo);
				localSourceFileResource.readSourceFileData(fileBlockNo, fileData, true);
				OutputMessage upFileDataResultOutObj = mainController
						.doUploadFile(serverTargetFileID, fileBlockNo, fileData);
				if (null == upFileDataResultOutObj) break;
				
				mainController.noticeFileBlockSizeToFileTransferProcessDialog(fileData.length);
			}
			
			
			fileUpDownScreen.reloadRemoteFileList();
			*/
		} catch (IllegalArgumentException e1) {
			String errorMessage = e1.toString();
			// log.warn(errorMessage, e1);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
	}
}
