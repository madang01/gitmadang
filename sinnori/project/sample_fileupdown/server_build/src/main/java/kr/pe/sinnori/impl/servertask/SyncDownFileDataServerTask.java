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
package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.impl.message.DownFileDataResult.DownFileDataResult;
import kr.pe.sinnori.impl.message.SyncDownFileData.SyncDownFileData;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 동기 메시지 방식의 파일 다운로드 서버 비지니스 로직 클래스
 * @author "Won Jonghoon"
 *
 */
public class SyncDownFileDataServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage inObj)
			throws Exception {
		doWork(projectName, letterSender, (SyncDownFileData)inObj);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, SyncDownFileData inObj)
			throws Exception {
		// FIXME!
		log.info(inObj.toString());
				
		
		
		int serverSourceFileID = inObj.getServerSourceFileID();
		int fileBlockNo = inObj.getFileBlockNo();
		
		LocalSourceFileResourceManager localTargetFileResourceManager = LocalSourceFileResourceManager.getInstance();
		DownFileDataResult outObj = new DownFileDataResult();
		
		/*OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");

		int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");
		int fileBlockNo = (Integer)inObj.getAttribute("fileBlockNo");*/
		// byte[] fileData = (byte[])inObj.getAttribute("fileData");
		
		// FIXME!
		// log.info(inObj.toString());
		
		LocalSourceFileResource localSourceFileResource = localTargetFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			log.warn(String.format("serverSourceFileID[%d] 다운로드 파일을 받을 자원이 준비되지 않았습니다.", serverSourceFileID));
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage("서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setFileBlockNo(fileBlockNo);
			outObj.setFileData(new byte[0]);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		boolean isCompletedReadingFile = false; 
		
		byte[] fileData = null;
		try {
			fileData = localSourceFileResource.getByteArrayOfFileBlockNo(fileBlockNo);
		} catch (OutOfMemoryError e) {
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("파일 블락 번호[%d]에 해당하는 파일 블락 메모리 확보 실패", fileBlockNo));
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setFileBlockNo(fileBlockNo);
			outObj.setFileData(new byte[0]);
			letterSender.addSyncMessage(outObj);
			return;
		}
		try {
			isCompletedReadingFile = localSourceFileResource.readSourceFileData(fileBlockNo, fileData, true);
		} catch (IllegalArgumentException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			ClientResource clientResource = letterSender.getClientResource();
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			/*outObj.setAttribute("taskResult", "N");
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setFileBlockNo(fileBlockNo);
			outObj.setFileData(new byte[0]);
			letterSender.addSyncMessage(outObj);
			return;
		} catch (UpDownFileException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			ClientResource clientResource = letterSender.getClientResource();
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);*/
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			// letterSender.sendSync(outObj);
			
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setFileBlockNo(fileBlockNo);
			outObj.setFileData(new byte[0]);
			letterSender.addSyncMessage(outObj);
			return;
		}
			
		// FIXME!
		// log.info(String.format("파일 읽기 결과[%s]", isCompletedReadingFile));
		
		/*outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
		outObj.setAttribute("serverSourceFileID", serverSourceFileID);
		outObj.setAttribute("fileBlockNo", fileBlockNo);
		outObj.setAttribute("fileData", fileData);*/
		
		outObj.setTaskResult("Y");
		outObj.setResultMessage("서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
		outObj.setServerSourceFileID(serverSourceFileID);
		outObj.setFileBlockNo(fileBlockNo);
		outObj.setFileData(fileData);
		letterSender.addSyncMessage(outObj);		
		
		if (isCompletedReadingFile) {
			ClientResource clientResource = letterSender.getClientResource();
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			// localTargetFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
		}
	}
}
