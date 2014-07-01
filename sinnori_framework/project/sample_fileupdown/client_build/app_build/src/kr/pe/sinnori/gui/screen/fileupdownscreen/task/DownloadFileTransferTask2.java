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

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;

/**
 * @author Jonghoon Won
 *
 */
public class DownloadFileTransferTask2 implements CommonRootIF, FileTransferTaskIF {
	private MainControllerIF mainController = null;
	private FileTranferProcessDialog fileTranferProcessDialog = null;
	
	private LocalTargetFileResource localTargetFileResource = null;
	private long intervalOfTimeoutCheck = 5000;

	// private boolean isCanceled = false;

	/**
	 * 생성자
	 * @param mainController 메인 제어자
	 * @param serverSourceFileID 서버 소스 파일 식별자. 참고) 서버는 다운로드 할 서버 파일에 걸린 락을 거는데 이 식별자를 통해서 이를 관리한다.
	 * @param localTargetFileResource 로컬 목적지 파일 자원
	 */
	public DownloadFileTransferTask2(MainControllerIF mainController,
			LocalTargetFileResource localTargetFileResource,
			long intervalOfTimeoutCheck) {
		this.mainController = mainController;		
		this.localTargetFileResource = localTargetFileResource;
		this.intervalOfTimeoutCheck = intervalOfTimeoutCheck;
	}

	@Override
	public void setFileTranferProcessDialog(FileTranferProcessDialog fileTranferProcessDialog) {
		this.fileTranferProcessDialog = fileTranferProcessDialog;
	}
	
	@Override
	public void doTask() {
		OutputMessage downloadFileAllOutObj = mainController.doDownloadFileAll();
		
		if (null == downloadFileAllOutObj) return;
		
		try {
			while(!localTargetFileResource.isCanceled() && !fileTranferProcessDialog.isFinished()) {
				// FIXME!
				//log.info("AAAAAAAAAAAA");
				
				/**
				 * 변경전 : 로그인 에코 메시지를 보낸다.
				 * ====== 변경전 코드 시작 ====
				 * OutputMessage outObj = mainController.doLoginEcho()
				 * ====== 변경전 코드 종료 ====
				 * 변경후 : 로그인 에코 메시지를 보내지 않는다.
				 * 변경 사유 : 로그인 에코 메시지는 동기 방식으로 메시지를 보내고 받기때문에 파일 다운로드시 서버쪽 네트워크 용량 문제로 응답이 지연되어 결국 타임아웃 발생한다.
				 * 변경후 영향 : 서버가 살아 있는지 검사하는 루틴 제거되므로 서버가 죽어 있어도 자동 감지를 못하게 된다.
				 */
				/*OutputMessage outObj = mainController.doLoginEcho();
				if (null == outObj) break;*/
				/**
				 * this.wait(intervalOfTimeoutCheck); 는 잘 동작하지 않아 sleep 로 대체함
				 */
				Thread.sleep(intervalOfTimeoutCheck);
			}		
		} catch (InterruptedException e) {
			log.info("InterruptedException");
		}
	}
	
	@Override
	public void cancelTask() {
		localTargetFileResource.cancel();
	}
	
	
	@Override
	public void endTask() {
		// FIXME!
		// log.info("call");
		mainController.endDownloadTask();
	}
}
