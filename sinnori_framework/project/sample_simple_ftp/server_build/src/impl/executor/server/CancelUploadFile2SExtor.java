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

import java.io.File;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
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
 * 파일 업로드 취소 버전2 서버 비지니스 로직 클래스
 * @author Jonghoon Won
 *
 */
public class CancelUploadFile2SExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		OutputMessage outObj = messageManger.createOutputMessage("CancelUploadFileResult");
		
		int clientSourceFileID = (Integer)inObj.getAttribute("clientSourceFileID");
		int serverTargetFileID = (Integer)inObj.getAttribute("serverTargetFileID");
		
		// FIXME!
		log.info(inObj.toString());
		
		LocalTargetFileResource  localTargetFileResource = null;
		
		localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(serverTargetFileID);
		
		if (null == localTargetFileResource) {
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 목적지 파일[%d] 식별자입니다.", serverTargetFileID));
			outObj.setAttribute("clientSourceFileID", clientSourceFileID);
			outObj.setAttribute("serverTargetFileID", serverTargetFileID);
			
			//letterToClientList.addLetterToClient(fromSC, outObj);
			letterSender.sendAsyn(outObj);
			return;
		}
		
		ClientResource clientResource = letterSender.getInObjClientResource();
		clientResource.removeLocalTargetFileID(serverTargetFileID);
		
		
		// localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", 
				String.format("서버 업로드용 목적지 파일[%d][%s%s%s] 자원을 성공적으로 해제하였습니다.", 
						serverTargetFileID, 
						localTargetFileResource.getTargetFilePathName(),
						File.pathSeparator,
						localTargetFileResource.getTargetFileName()));
		outObj.setAttribute("clientSourceFileID", clientSourceFileID);
		outObj.setAttribute("serverTargetFileID", serverTargetFileID);
		
		// FIXME!
		log.info(outObj.toString());
		
		
		//letterToClientList.addLetterToClient(fromSC, outObj);
		letterSender.sendAsyn(outObj);
	}
}
