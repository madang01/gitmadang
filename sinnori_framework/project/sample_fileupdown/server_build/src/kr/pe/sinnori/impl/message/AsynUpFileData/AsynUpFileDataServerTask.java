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
package kr.pe.sinnori.impl.message.AsynUpFileData;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.impl.message.UpFileDataResult.UpFileDataResult;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 동기 메시지 방식의 파일 업로드 서버 비지니스 로직
 * @author "Won Jonghoon"
 *
 */
public class AsynUpFileDataServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, 
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		// FIXME!
		// log.info(messageFromClient.toString());
		
		AsynUpFileData inObj = (AsynUpFileData) messageFromClient;
		
		int clientSourceFileID = inObj.getClientSourceFileID();
		int serverTargetFileID = inObj.getServerTargetFileID();
		int fileBlockNo = inObj.getFileBlockNo();
		byte[] fileData = inObj.getFileData();
		
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		UpFileDataResult outObj = new UpFileDataResult();
		// OutputMessage outObj = messageManger.createOutputMessage("UpFileDataResult");
		
		/*outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);
		outObj.setAttribute("fileBlockNo", fileBlockNo);*/
		outObj.setServerTargetFileID(serverTargetFileID);
		outObj.setClientSourceFileID(clientSourceFileID);
		outObj.setFileBlockNo(fileBlockNo);
		
		LocalTargetFileResource localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			log.info(String.format("serverTargetFileID[%d] 업로드 파일을 받을 자원이 준비되지 않았습니다.", serverTargetFileID));
			
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버에서 업로드 파일을 받을 자원이 준비되지 않았습니다.");
			
			
			letterSender.sendAsyn(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage("서버에서 업로드 파일을 받을 자원이 준비되지 않았습니다.");
			letterSender.addAsynMessage(outObj);
			return;
		}
		
		
		
		boolean isCompletedWritingFile = false; 
		try {
			isCompletedWritingFile = localTargetFileResource.writeTargetFileData(fileBlockNo, fileData, true);
			
			outObj.setTaskResult("Y");
			outObj.setResultMessage(new StringBuilder("서버에서 수신한 업로드 파일 조각[").append(fileBlockNo).append("] 저장이 완료되었습니다.").toString());
			letterSender.addAsynMessage(outObj);
			
			if (isCompletedWritingFile) {
				ClientResource clientResource = letterSender.getClientResource();
				clientResource.removeLocalTargetFileID(serverTargetFileID);
			}
			return;
		} catch (IllegalArgumentException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);
			
			/*ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.removeLocalTargetFileID(serverTargetFileID);
			
			
			outObj.setAttribute("taskResult", "N");
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버 IllegalArgumentException::").append(e.getMessage()).toString());
			
			
			letterSender.sendAsyn(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버 IllegalArgumentException::").append(e.getMessage()).toString());
			letterSender.addAsynMessage(outObj);
			return;
		} catch (UpDownFileException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);
			
			/*ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.removeLocalTargetFileID(serverTargetFileID);
						
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder("서버::").append(e.getMessage()).toString());
			
			
			letterSender.sendAsyn(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버 IllegalArgumentException::").append(e.getMessage()).toString());
			letterSender.addAsynMessage(outObj);
			return;
		}		
	}
}
