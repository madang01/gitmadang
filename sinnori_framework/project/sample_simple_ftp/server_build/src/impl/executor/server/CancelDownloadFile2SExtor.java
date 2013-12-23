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
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;

/**
 * @author Jonghoon Won
 *
 */
public class CancelDownloadFile2SExtor extends AbstractAuthServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		
		int serverSourceFileID = (Integer)inObj.getAttribute("serverSourceFileID");
		int clientTargetFileID = (Integer)inObj.getAttribute("clientTargetFileID");

		// FIXME!
		log.info(inObj.toString());
		
		LocalSourceFileResource  localSourceFileResource = null;
		
		localSourceFileResource = localSourceFileResourceManager.getLocalSourceFileResource(serverSourceFileID);
		
		if (null == localSourceFileResource) {
			OutputMessage outObj = messageManger.createOutputMessage("CancelDownloadFileResult");
			outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
			outObj.messageHeaderInfo.mailID = clientResourceManager.getClientResource(fromSC).getServerMailID();
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", String.format("존재하지 않는 서버 원본 파일[%d] 식별자입니다.", serverSourceFileID));
			outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			
			sendAnonymous(fromSC, outObj);
			return;
		}
		
		localSourceFileResource.cancel();
		/*
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		clientResource.removeLocalSourceFileID(serverSourceFileID);
		
		// localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
		
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", String.format("서버 다운로드용 원본 파일[%d] 자원을 성공적으로 해제하였습니다.", serverSourceFileID));
		outObj.setAttribute("serverSourceFileID", serverSourceFileID);
		outObj.setAttribute("clientTargetFileID", clientTargetFileID);
		letterToClientList.addLetterToClient(fromSC, outObj);
		*/
	}
}