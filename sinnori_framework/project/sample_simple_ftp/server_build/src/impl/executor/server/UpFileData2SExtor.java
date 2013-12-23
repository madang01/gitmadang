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
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;

/**
 * @author Jonghoon Won
 *
 */
public class UpFileData2SExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		
		OutputMessage outObj = messageManger.createOutputMessage("UpFileDataResult");
		outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		outObj.messageHeaderInfo.mailID = clientResourceManager.getClientResource(fromSC).getServerMailID();
		
		int clientSourceFileID = (Integer)inObj.getAttribute("clientSourceFileID");
		int serverTargetFileID = (Integer)inObj.getAttribute("serverTargetFileID");
		int fileBlockNo = (Integer)inObj.getAttribute("fileBlockNo");
		byte[] fileData = (byte[])inObj.getAttribute("fileData");
		
		// FIXME!
		// log.info(inObj.toString());
		
		outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);
		outObj.setAttribute("fileBlockNo", fileBlockNo);
		
		LocalTargetFileResource localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			log.info(String.format("serverTargetFileID[%d] 업로드 파일을 받을 자원이 준비되지 않았습니다.", serverTargetFileID));
			
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "서버에서 업로드 파일을 받을 자원이 준비되지 않았습니다.");
			
			
			sendAnonymous(fromSC, outObj);
			return;
		}
		
		
		
		boolean isCompletedWritingFile = false; 
		try {
			isCompletedWritingFile = localTargetFileResource.writeTargetFileData(fileBlockNo, fileData, true);
			if (isCompletedWritingFile) {
				ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
				clientResource.removeLocalTargetFileID(serverTargetFileID);
				// localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
			}
		} catch (IllegalArgumentException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);
			
			ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
			clientResource.removeLocalTargetFileID(serverTargetFileID);
			
			
			outObj.setAttribute("taskResult", "N");
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버 IllegalArgumentException::").append(e.getMessage()).toString());
			
			
			sendAnonymous(fromSC, outObj);
			return;
		} catch (UpDownFileException e) {
			log.info(String.format("serverTargetFileID[%d] lock free::%s", serverTargetFileID, e.getMessage()), e);
			
			ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
			clientResource.removeLocalTargetFileID(serverTargetFileID);
						
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder("서버::").append(e.getMessage()).toString());
			
			
			sendAnonymous(fromSC, outObj);
			return;
		}
	}
}
