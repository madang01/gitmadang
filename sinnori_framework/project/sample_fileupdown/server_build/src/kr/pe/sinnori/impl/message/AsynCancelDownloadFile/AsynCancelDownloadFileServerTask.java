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
package kr.pe.sinnori.impl.message.AsynCancelDownloadFile;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.impl.message.CancelDownloadFileResult.CancelDownloadFileResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 비동기 메시지 방식의 다운로드 취소 서버 비지니스 로직.
 * @author "Jonghoon Won"
 *
 */
public class AsynCancelDownloadFileServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		AsynCancelDownloadFile inObj = (AsynCancelDownloadFile) messageFromClient;
		int serverSourceFileID = inObj.getServerSourceFileID();
		int clientTargetFileID = inObj.getClientTargetFileID();
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		CancelDownloadFileResult outObj = new CancelDownloadFileResult();
		
		/*int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");*/
		
		LocalSourceFileResource  localSourceFileResource = null;
		
		localSourceFileResource = localSourceFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			/*OutputMessage outObj = messageManger.createOutputMessage("CancelDownloadFileResult");			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 원본 파일[%d] 식별자입니다.", serverSourceFileID));
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			
			letterSender.sendAsyn(outObj);*/
			
			outObj.setTaskResult("N");
			outObj.setResultMessage(String.format("존재하지 않는 서버 원본 파일[%d] 식별자입니다.", serverSourceFileID));
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setClientTargetFileID(clientTargetFileID);
			letterSender.addAsynMessage(outObj);
			return;
		}
		
		localSourceFileResource.cancel();
	}
}
