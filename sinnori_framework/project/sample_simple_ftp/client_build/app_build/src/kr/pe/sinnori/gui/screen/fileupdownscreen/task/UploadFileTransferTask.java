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

package kr.pe.sinnori.gui.screen.fileupdownscreen.task;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;

/**
 * 파일 업로드 수신 상태 모달 윈도우에서 기동하는 
 * 쓰레드에서 호출되는 사용자 정의 비지니스 로직 클래스.
 * 
 * 서버에 순차적으로 업로드할 파일 조작을 요청하여 받아 저장하고,
 * 파일 업로드 수신 상태 모달 윈도우에 이를 알려 파일 전송 진행 상태를 갱신한다.
 * 파일 업로드 수신 상태 모달 윈도우에서 사용자가 취소를 누르면 업로드 동작은 중지된다.
 * 업로드 파일을 모두 전송 받았거나 중간에 취소시 원격지 파일 목록을 재 갱신한다. 
 *  
 * @author Jonghoon Won
 *
 */
public class UploadFileTransferTask implements FileTransferTaskIF, CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileTranferProcessDialog fileTranferProcessDialog = null;
	private int serverTargetFileID = -1;
	private LocalSourceFileResource localSourceFileResource = null;

	// private boolean isCanceled = false;

	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mainController 메인 제어자
	 * @param serverTargetFileID 서버 목적지 파일 식별자. 참고) 서버는 업로드할 파일에 락을 거는데 이 식별자를 통해서 이를 관리한다.
	 * @param localSourceFileResource 로컬 원본 파일 자원
	 */
	public UploadFileTransferTask(JFrame mainFrame,
			MainControllerIF mainController,
			int serverTargetFileID,
			LocalSourceFileResource localSourceFileResource) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.serverTargetFileID = serverTargetFileID;
		this.localSourceFileResource = localSourceFileResource;
	}

	@Override
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
				if (localSourceFileResource.isCanceled()) {
					// FIXME!
					log.info("do cancel");
					
					OutputMessage cancelUploadFileResultOutObj = mainController.cancelUploadFile();
					
					/** 파일 업로드 취소 완료시 종료, 파일 업로드 취소 실패시 파일 업로드 작업 계속 진행 */
					if (null != cancelUploadFileResultOutObj) break;
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
		}
	}
	
	@Override
	public void cancelTask() {
		localSourceFileResource.cancel();
	}
	
	@Override
	public void endTask() {
		mainController.endUploadTask();
	}

}
