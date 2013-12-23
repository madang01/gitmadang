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

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;

/**
 * @author Jonghoon Won
 *
 */
public class DownFileDataAllSExtor extends AbstractAuthServerExecutor {
	
	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		
		log.info(inObj.toString());
		
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		
		LocalSourceFileResourceManager localTargetFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");
		
		// byte[] fileData = (byte[])inObj.getAttribute("fileData");
		
		// FIXME!
		// log.info(inObj.toString());
		
		LocalSourceFileResource localSourceFileResource = localTargetFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			log.warn(String.format("serverSourceFileID[%d] 다운로드 파일을 받을 자원이 준비되지 않았습니다.", serverSourceFileID));
			
			OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");
			// outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;		
			// outObj.messageHeaderInfo.mailID = clientResource.getServerMailID();
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("fileBlockNo", 0);
			outObj.setAttribute("fileData", new byte[0]);
			
			// letterToClientList.addLetterToClient(fromSC, outObj);
			sendAnonymous(fromSC, outObj);
			return;
		}
		
		// boolean isCompletedReadingFile = false; 
		int fileBlockNo = 0;
		int fileBlockMaxNo = localSourceFileResource.getFileBlockMaxNo();
		try {
			for (; fileBlockNo <= fileBlockMaxNo; fileBlockNo++) {
				if (localSourceFileResource.isCanceled()) {
					log.info(String.format("serverSourceFileID[%s] to clientTargetFileID[%d] 파일 다운로드 취소", serverSourceFileID, localSourceFileResource.getTargetFileID()));
					
					clientResource.removeLocalSourceFileID(serverSourceFileID);
					
					OutputMessage outObj = messageManger.createOutputMessage("CancelDownloadFileResult");
					// outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;		
					// outObj.messageHeaderInfo.mailID = clientResource.getServerMailID();
					outObj.setAttribute("taskResult", "Y");
					outObj.setAttribute("resultMessage", String.format("서버 다운로드용 원본 파일[%d] 자원을 성공적으로 해제하였습니다.", serverSourceFileID));
					outObj.setAttribute("serverSourceFileID", serverSourceFileID);
					outObj.setAttribute("clientTargetFileID", clientTargetFileID);
					sendAnonymous(fromSC, outObj);
					
					clientResource.removeLocalSourceFileID(serverSourceFileID);
					return;
				}
				
				byte[] fileData = null;
				fileData = localSourceFileResource.getByteArrayOfFileBlockNo(fileBlockNo);
				localSourceFileResource.readSourceFileData(fileBlockNo, fileData, true);
				
				OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");
				// outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;		
				// outObj.messageHeaderInfo.mailID = clientResource.getServerMailID();
				outObj.setAttribute("taskResult", "Y");
				
				outObj.setAttribute("resultMessage", "서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
				outObj.setAttribute("serverSourceFileID", serverSourceFileID);
				outObj.setAttribute("clientTargetFileID", clientTargetFileID);
				outObj.setAttribute("fileBlockNo", fileBlockNo);
				outObj.setAttribute("fileData", fileData);
				
				sendAnonymous(fromSC, outObj);
			}

			// FIXME!
			log.info(String.format("serverSourceFileID[%s] to clientTargetFileID[%d] 파일 다운로드 전체 완료", serverSourceFileID, localSourceFileResource.getTargetFileID()));
			
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			
		} catch (IllegalArgumentException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");
			// outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;		
			// outObj.messageHeaderInfo.mailID = clientResource.getServerMailID();
			outObj.setAttribute("taskResult", "N");
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			sendAnonymous(fromSC, outObj);
			return;
		} catch (UpDownFileException e) {
			log.warn(String.format("serverSourceFileID[%d] lock free::%s", serverSourceFileID, e.getMessage()), e);
			
			clientResource.removeLocalSourceFileID(serverSourceFileID);
			
			OutputMessage outObj = messageManger.createOutputMessage("DownFileDataResult");
			// outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;		
			// outObj.messageHeaderInfo.mailID = clientResource.getServerMailID();
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder("서버 에러 메시지\n").append(e.getMessage()).toString());
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			outObj.setAttribute("fileBlockNo", fileBlockNo);
			outObj.setAttribute("fileData", new byte[0]);
			
			sendAnonymous(fromSC, outObj);
			return;
		}
	}
}
