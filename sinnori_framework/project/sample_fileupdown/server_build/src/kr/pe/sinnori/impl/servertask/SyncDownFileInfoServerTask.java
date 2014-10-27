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

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.impl.message.DownFileInfoResult.DownFileInfoResult;
import kr.pe.sinnori.impl.message.SyncDownFileInfo.SyncDownFileInfo;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 다운 로드 파일 정보 서버 비지니스 로직
 * @author "Jonghoon Won"
 *
 */
public class SyncDownFileInfoServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		SyncDownFileInfo inObj = (SyncDownFileInfo) messageFromClient;
		
		/*byte appendByte = (Byte)inObj.getAttribute("append");
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		long localFileSize = (Long)inObj.getAttribute("localFileSize");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		long remoteFileSize = (Long)inObj.getAttribute("remoteFileSize");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");*/
		byte appendByte = inObj.getAppend();
		String localFilePathName = inObj.getLocalFilePathName();
		String localFileName = inObj.getLocalFileName();
		long localFileSize = inObj.getLocalFileSize();
		String remoteFilePathName = inObj.getRemoteFilePathName();
		String remoteFileName = inObj.getRemoteFileName();
		long remoteFileSize = inObj.getRemoteFileSize();
		int clientTargetFileID = inObj.getClientTargetFileID();
		int fileBlockSize = inObj.getFileBlockSize();
		
		
		
		boolean append;
		if (0 == appendByte) {
			append = false;
		} else if (1 == appendByte) {
			append = true;
		} else {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("서버::파라미터 append 값[%x]에 알수없는 값이 들어왔습니다.", append));
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			letterSender.sendSync(outObj);*/
			/*sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverSourceFileID, 
					clientTargetFileID, letterSender, messageManger);*/
			
			DownFileInfoResult outObj = new DownFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("서버::파라미터 append 값[%x]에 알수없는 값이 들어왔습니다.", appendByte));
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setServerSourceFileID(-1);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		LocalSourceFileResource localSourceFileResource = null;
		try {
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(
					append,
					remoteFilePathName, remoteFileName, remoteFileSize, 
					localFilePathName, localFileName, localFileSize,
					fileBlockSize);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			if (null != localSourceFileResource) {
				localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			}
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			/*sendSync("N", String.format("IllegalArgumentException::%s", e.getMessage()), serverSourceFileID,
					clientTargetFileID, letterSender, messageManger);*/
			DownFileInfoResult outObj = new DownFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage("업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setServerSourceFileID(-1);
			letterSender.addSyncMessage(outObj);
			return;
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			/*sendSync("N", String.format("UpDownFileException::%s", e.getMessage()), serverSourceFileID,
					clientTargetFileID, letterSender, messageManger);*/
			
			DownFileInfoResult outObj = new DownFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage("서버::"+e.getMessage());
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setServerSourceFileID(-1);
			letterSender.addSyncMessage(outObj);
			return;
		}
			
		if (null == localSourceFileResource) {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("serverSourceFileID", -1);
			// letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendSync(outObj);*/
			/*sendSync("N", "큐로부터 원본 파일 자원 할당에 실패하였습니다.", serverSourceFileID, 
					clientTargetFileID, letterSender, messageManger);*/
			
			DownFileInfoResult outObj = new DownFileInfoResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage("큐로부터 원본 파일 자원 할당에 실패하였습니다.");
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setServerSourceFileID(-1);
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		log.info("localSourceFileResource 할당 성공");
		
		localSourceFileResource.setTargetFileID(clientTargetFileID);
		
		/** 서버 원본 파일 식별자, 디폴트 값은 에러를 나타내는 -1 */
		int serverSourceFileID = -1;
		serverSourceFileID = localSourceFileResource.getSourceFileID(); 

		ClientResource clientResource = letterSender.getClientResource();
		clientResource.addLocalSourceFileID(serverSourceFileID);
		
		/*outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
		outObj.setAttribute("clientTargetFileID", clientTargetFileID);
		outObj.setAttribute("serverSourceFileID", serverSourceFileID);
		letterSender.sendSync(outObj);*/
		/*sendSync("Y", "파일 다운로드 준비가 되었습니다.", serverSourceFileID, 
				clientTargetFileID, letterSender, messageManger);*/
		
		DownFileInfoResult outObj = new DownFileInfoResult();
		outObj.setTaskResult("Y");
		outObj.setResultMessage("업로드할 파일을 받아줄 준비가 되었습니다.");
		outObj.setClientTargetFileID(clientTargetFileID);
		outObj.setServerSourceFileID(serverSourceFileID);
		letterSender.addSyncMessage(outObj);
	}
}
