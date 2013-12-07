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

package kr.pe.sinnori.gui.lib;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;

/**
 * <pre>
 * 파일 다운로드 수신 상태 모달 윈도우에서 기동하는 
 * 쓰레드에서 호출되는 사용자 정의 비지니스 로직 클래스. 
 * 
 * 서버에 순차적으로 다운로드할 파일 조각을 요청하여 받아 저장하고 
 * 파일 다운로드 수신 상태 모달 윈도우에 이를 알려 파일 전송 진행 상태를 갱신한다.
 * 파일 다운로드 수신 상태 모달 윈도우에서 사용자가 취소를 누르면 다운로드 동작은 중지된다.
 * 다운로드 파일을 모두 전송 받았거나 중간에 취소시 로컬 파일 목록을 재 갱신한다. 
 * </pre> 
 * @author Jonghoon Won
 *
 */
public class DownloadFileTransferTask implements CommonRootIF, FileTransferTaskIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private FileTranferProcessDialog fileTranferProcessDialog = null;
	
	private int serverSourceFileID = -1;
	private LocalTargetFileResource localTargetFileResource = null;

	private boolean isCanceled = false;

	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mainController 메인 제어자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 * @param serverSourceFileID 서버 소스 파일 식별자. 참고) 서버는 다운로드 할 서버 파일에 걸린 락을 거는데 이 식별자를 통해서 이를 관리한다.
	 * @param localTargetFileResource 로컬 목적지 파일 자원
	 */
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

	@Override
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

	@Override
	public void cancelTask() {
		isCanceled = true;
	}

}