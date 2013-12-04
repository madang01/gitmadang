package kr.pe.sinnori.gui.action.fileupdownscreen;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.gui.FileUpDownScreenIF;
import kr.pe.sinnori.gui.LocalFileTreeNode;
import kr.pe.sinnori.gui.MainControllerIF;
import kr.pe.sinnori.gui.RemoteFileTreeNode;
import kr.pe.sinnori.gui.UploadFileTransferTask;

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
		long localFileSize = -1;
		String remoteFilePathName = (String)remoteRootNode.getUserObject();
		String remoteFileName = "";
		int fileBlockSize = 1024 * 32;

		TreePath remoteSelectedPath = remoteTree.getSelectionPath();
		if (null != remoteSelectedPath) {
			RemoteFileTreeNode remoteSelectedNode = (RemoteFileTreeNode) remoteSelectedPath
					.getLastPathComponent();

			if (RemoteFileTreeNode.FileType.File == remoteSelectedNode
					.getFileType()) {
				int yesOption = JOptionPane.showConfirmDialog(mainFrame,
						String.format("로컬 파일[%s]을 원격지 파일[%s]에 덮어 쓰시겠습니까?",
								localFileName,
								remoteSelectedNode.getFileName()),
						"덮어쓰기 확인창", JOptionPane.YES_NO_OPTION);

				if (JOptionPane.NO_OPTION == yesOption)
					return;

				remoteFileName = remoteSelectedNode.getFileName();
			} else if (!remoteSelectedNode.isRoot()) {
				/** 업로드한 파일의 위치로 원격지 자식 디렉토리를 선택한 경우 */
				StringBuilder targetPathBuilder = new StringBuilder(remoteFilePathName);
				targetPathBuilder.append(remotePathSeperator);
				targetPathBuilder.append(remoteSelectedNode.getFileName());
				remoteFilePathName = targetPathBuilder.toString();
			}
		} else {
			int cntOfChild = remoteRootNode.getChildCount();
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
			}
		}

		// FIXME!
		log.info(String.format("copy localFilePathName[%s] localFileName[%s] to remoteFilePathName[%s] remoteFileName[%s]",
				localFilePathName,  localFileName, remoteFilePathName, remoteFileName));
		

		StringBuilder sourceFileBuilder = new StringBuilder(localFilePathName);
		sourceFileBuilder.append(File.separator);
		sourceFileBuilder.append(localFileName);
		String sourceFileFullName = sourceFileBuilder.toString();

		File localSourceFile = new File(sourceFileFullName);

		localFileSize = localSourceFile.length();
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		LocalSourceFileResource localSourceFileResource = null;

		try {
			OutputMessage upFileInfoResulOutObj = mainController
					.readyUploadFile(localFilePathName, localFileName,
							localFileSize, remoteFilePathName,
							remoteFileName, fileBlockSize);

			/** 정상적인 업로드 파일 준비 출력 메시지를 받지 못했을 경우 처리 종료 */
			if (null == upFileInfoResulOutObj) return;
				

			int serverTargetFileID = -1;
			try {
				serverTargetFileID = (Integer) upFileInfoResulOutObj.getAttribute("serverTargetFileID");
			} catch (MessageItemException e1) {
				log.warn("MessageItemException", e1);
				JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
				return;
			}
			
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(localFilePathName, localFileName, localFileSize, remoteFilePathName, remoteFileName, fileBlockSize);
			
			if (null == localSourceFileResource) {
				JOptionPane.showMessageDialog(mainFrame, "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
				return;
			}
			
			UploadFileTransferTask uploadFileTransferTask = new UploadFileTransferTask(mainFrame, mainController, fileUpDownScreen, serverTargetFileID, localSourceFileResource);
			mainController.openFileTransferProcessDialog(new StringBuilder(localFileName).append(" 업로드 중...").toString(), localFileSize, uploadFileTransferTask);
			
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
			
		} catch (UpDownFileException e1) {
			// String errorMessage = String.format("로컬 원본 파일[%s]을 읽는데 실패하였습니다.", sourceFileFullName);
			String errorMessage = e1.toString();
			//log.warn(errorMessage, e1);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		} finally {
			if (null != localSourceFileResource) localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			
			// mainController.closeFileTransferProcessDialog();
		}		
	}
}
