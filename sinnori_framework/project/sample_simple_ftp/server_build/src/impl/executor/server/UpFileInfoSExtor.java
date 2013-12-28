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

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
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

	@Override
	protected void doTask(CommonProjectInfo commonProjectInfo,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		OutputMessage outObj = messageManger.createOutputMessage("UpFileInfoResult");
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		
		int clientSourceFileID = (Integer)inObj.getAttribute("clientSourceFileID");
		String localFilePathName = (String)inObj.getAttribute("localFilePathName");
		String localFileName = (String)inObj.getAttribute("localFileName");
		Long localFileSize = (Long)inObj.getAttribute("localFileSize");
		String remoteFilePathName = (String)inObj.getAttribute("remoteFilePathName");
		String remoteFileName = (String)inObj.getAttribute("remoteFileName");
		int fileBlockSize = (Integer)inObj.getAttribute("fileBlockSize");
		
		
		outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		
		// FIXME!
		log.info(inObj.toString());
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		
		try {
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(localFilePathName, localFileName, localFileSize, remoteFilePathName, remoteFileName, fileBlockSize);
			
			if (null == localTargetFileResource) {
				outObj.setAttribute("taskResult", "N");
				outObj.setAttribute("resultMessage", "큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
				outObj.setAttribute("serverTargetFileID", -1);
				
				letterSender.sendSelf(outObj);
				return;
			}
			
			localTargetFileResource.setSourceFileID(clientSourceFileID);
			int serverTargetFileID = localTargetFileResource.getTargetFileID();
			
			ClientResource clientResource = letterSender.getInObjClientResource();
			clientResource.addLocalTargetFileID(serverTargetFileID);
			
			
			outObj.setAttribute("taskResult", "Y");
			outObj.setAttribute("resultMessage", "업로드할 파일을 받아줄 준비가 되었습니다.");
			outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			
			// FIXME!
			// log.info(outObj.toString());
			
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				// 출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
				return;
			}
		*/
			letterSender.sendSelf(outObj);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", e.getMessage());
			outObj.setAttribute("serverTargetFileID", -1);
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				//  출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시 
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
			}
			return;
			*/
			letterSender.sendSelf(outObj);
		} catch (UpDownFileException e) {
			log.info("UpDownFileException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버::"+e.getMessage());
			outObj.setAttribute("serverTargetFileID", -1);
			/*
			LetterToClient letterToClient = new LetterToClient(fromSC, outObj);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				// 출력 메시지 큐 담는 과정에서 인터럽트 발생시 로그만 남기고 무시 
				log.warn("업로드 파일 준비 실패했다는 내용을 담은 출력 메시지[UpFileInfoResult]를 출력 메시지 큐 담는 과정에서 인터럽트 발생", e1);
			}
			return;
			*/
			letterSender.sendSelf(outObj);
		}
	}
}
