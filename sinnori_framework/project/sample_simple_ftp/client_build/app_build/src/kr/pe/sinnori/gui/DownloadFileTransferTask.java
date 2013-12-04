package kr.pe.sinnori.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;

public class DownloadFileTransferTask implements CommonRootIF, FileTransferTaskIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private FileTranferProcessDialog fileTranferProcessDialog = null;
	
	private int serverSourceFileID = -1;
	private LocalTargetFileResource localTargetFileResource = null;

	private boolean isCanceled = false;

	public DownloadFileTransferTask(JFrame mainFrame,
			MainControllerIF mainController, FileUpDownScreenIF fileUpDownScreen,
			int serverSourceFileID,
			LocalTargetFileResource localTargetFileResource) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.serverSourceFileID = serverSourceFileID;
		this.localTargetFileResource = localTargetFileResource;
	}

	public void setFileTranferProcessDialog(FileTranferProcessDialog fileTranferProcessDialog) {
		this.fileTranferProcessDialog = fileTranferProcessDialog;
	}
	
	@Override
	public void doTask() {
		int fileBlockMaxNo =  localTargetFileResource.getFileBlockMaxNo();
		int fileBlockNo = 0;
		try {

			for (; fileBlockNo <= fileBlockMaxNo; fileBlockNo++) {
				// boolean isCanceled = fileUpDownScreen.getIsCancelFileTransfer();
				if (isCanceled) {
					isCanceled = false;
					// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
					
					OutputMessage cancelDownloadFileResultOutObj = mainController.cancelDownloadFile(serverSourceFileID);
					/** 서버 다운로드 취소 성공시 루프 종료 */
					if (null != cancelDownloadFileResultOutObj) break;
				}
				
				OutputMessage downFileDataResulOutObj = mainController.doDownloadFile(serverSourceFileID, fileBlockNo);
				
				if (null == downFileDataResulOutObj) break;
				
				byte[] fileData = null;
				try {
					fileData = (byte[]) downFileDataResulOutObj.getAttribute("fileData");
				} catch (MessageItemException e) {
					log.warn(String.format("서버 소스 파일 식별자[%d] 의 [%d] 번째 다운 로드 시도중 메시지 항목 에러 발생", serverSourceFileID, fileBlockNo), e);
					JOptionPane.showMessageDialog(mainFrame, e.toString());
					break;
				}
				
				localTargetFileResource.writeTargetFileData(fileBlockNo, fileData, true);
					
				fileTranferProcessDialog.noticeAddingFileData(fileData.length);
			}

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return;
		} catch (UpDownFileException e) {
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return;
		} finally {
			fileUpDownScreen.reloadLocalFileList();
		}

		// fileTranferProcessDialog.updateInfoMesg();
		
		// Task 종료후 파일 전송 창 자동 종료
		// fileTranferProcessDialog.closeFileTransferProcessDialog();
	}

	public void cancelTask() {
		isCanceled = true;
	}

}