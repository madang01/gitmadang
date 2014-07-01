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


package impl.executor.server;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * <pre>
 * 로그인 요구 서비스로 업로드할 준비를 수행하는 비지니스 로직.
 * 업로드할 파일에 락을 걸고 락 정보를 클라이언트로 보낸다.
 * </pre>
 * 
 * @author Jonghoon Won
 *
 */
public final class UpFileInfoSExtor extends AbstractAuthServerExecutor {

	private void sendSync(
			String taskResult, String resultMessage, int serverTargetFileID, int clientSourceFileID,
			LetterSender letterSender, MessageMangerIF messageManger) throws IllegalArgumentException, MessageInfoNotFoundException, MessageItemException {
		OutputMessage outObj = messageManger.createOutputMessage("UpFileInfoResult");
		outObj.setAttribute("taskResult", taskResult);
		outObj.setAttribute("resultMessage", resultMessage);
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);
		outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		letterSender.sendSync(outObj);
	}
	
	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		// OutputMessage outObj = messageManger.createOutputMessage("UpFileInfoResult");
		// outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		byte appendByte = (Byte)inObj.getAttribute("append");
		int clientSourceFileID = (Integer)inObj.getAttribute("clientSourceFileID");
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		long localFileSize = (Long)inObj.getAttribute("localFileSize");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		long remoteFileSize = (Long)inObj.getAttribute("remoteFileSize");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");
		
		
		// outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		
		// FIXME!
		log.info(inObj.toString());
		
		/** 서버 목적지 파일 식별자, 디폴트 값은 에러를 나타내는 -1 */
		int serverTargetFileID = -1;
		
		boolean append;
		if (0 == appendByte) {
			append = false;
		} else if (1 == appendByte) {
			append = true;
		} else {
			sendSync("N", 
					String.format("파라미터 append 값[%x]에 알수 없는 값이 들어왔습니다.", appendByte), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);
			return;
		}
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		
		try {
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(append, 
					localFilePathName, localFileName, localFileSize, 
					remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
			
			if (null == localTargetFileResource) {
				/*outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("serverTargetFileID", -1);
				
				letterSender.sendSync(outObj);*/
				sendSync("N", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.", serverTargetFileID, 
						clientSourceFileID, letterSender, messageManger);
				return;
			}
			
			localTargetFileResource.makeZeroSizeFile();
			localTargetFileResource.setSourceFileID(clientSourceFileID);
			serverTargetFileID = localTargetFileResource.getTargetFileID();
			
			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.addLocalTargetFileID(serverTargetFileID);
			
			sendSync("Y", "파일 업로드 준비가 되었습니다.", serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);
			
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			if (null != localTargetFileResource) {
				localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
			}
			
			sendSync("N", String.format("IllegalArgumentException::%s", e.getMessage()), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			if (null != localTargetFileResource) {
				localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
			}
			sendSync("N", String.format("UpDownFileException::%s", e.getMessage()), serverTargetFileID, 
					clientSourceFileID, letterSender, messageManger);
		}
	}
}
