package kr.pe.sinnori.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;

public class UploadFileTransferTask implements FileTransferTaskIF, CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private FileTranferProcessDialog fileTranferProcessDialog = null;
	private int serverTargetFileID = -1;
	private LocalSourceFileResource localSourceFileResource = null;

	private boolean isCanceled = false;

	public UploadFileTransferTask(JFrame mainFrame,
			MainControllerIF mainController, FileUpDownScreenIF fileUpDownScreen,
			int serverTargetFileID,
			LocalSourceFileResource localSourceFileResource) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.serverTargetFileID = serverTargetFileID;
		this.localSourceFileResource = localSourceFileResource;
	}

	public void setFileTranferProcessDialog(FileTranferProcessDialog fileTranferProcessDialog) {
		this.fileTranferProcessDialog = fileTranferProcessDialog;
	}
	
	@Override
	public void doTask() {
		// FIXME!
		log.info("UploadFileTransferTask start");
					
		int localFileBlockMaxNo = localSourceFileResource.getFileBlockMaxNo();
		int fileBlockNo = 0;
		try {

			for (; fileBlockNo <= localFileBlockMaxNo; fileBlockNo++) {
				// boolean isCanceled =
				// fileUpDownScreen.getIsCancelFileTransfer();
				if (isCanceled) {
					isCanceled = false;
					// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
					// FIXME!
					log.info("do cancel");

					OutputMessage cancelUploadFileResultOutObj = mainController
							.cancelUploadFile(serverTargetFileID);
					/** 서버 업로드 취소 성공시 루프 종료 */
					if (null != cancelUploadFileResultOutObj) {
						// FIXME!
						log.info(cancelUploadFileResultOutObj.toString());
						
						break;
					}
						
					
					
				}

				byte fileData[] = localSourceFileResource
						.getByteArrayOfFileBlockNo(fileBlockNo);

				localSourceFileResource.readSourceFileData(fileBlockNo,
						fileData, true);

				OutputMessage upFileDataResultOutObj = mainController
						.doUploadFile(serverTargetFileID, fileBlockNo, fileData);
				if (null == upFileDataResultOutObj)
					break;

				// mainController.noticeFileBlockSizeToFileTransferProcessDialog(fileData.length);
				fileTranferProcessDialog.noticeAddingFileData(fileData.length);
			}
			
			// FIXME!
			log.info("UploadFileTransferTask end");

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return;
		} catch (UpDownFileException e) {
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return;
		} finally {
			fileUpDownScreen.reloadRemoteFileList();
		}

		
		// fileTranferProcessDialog.updateInfoMesg();
		
		// Task 종료후 파일 전송 창 자동 종료
		// fileTranferProcessDialog.closeFileTransferProcessDialog();
	}

	public void cancelTask() {
		isCanceled = true;
	}

}
