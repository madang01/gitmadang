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

import java.util.StringTokenizer;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

import org.apache.commons.codec.binary.Base64;

/**
 * 메세지 식별자 Command 비지니스 로직
 * 
 * @author Jonghoon Won
 * 
 */
public final class CommandSExtor extends AbstractServerExecutor {

	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {

		String commandString = (String) inObj.getAttribute("commandString");
		commandString = commandString.trim();

		StringTokenizer token = new StringTokenizer(commandString);

		StringBuffer commandBuffer = new StringBuffer();

		if (token.hasMoreTokens()) {
			// ClientResource clientResource = clientResourceManager.getClientResource(fromSC);

			String commandValue = token.nextToken().trim().toUpperCase();

			if (commandValue.equals("/공개키요청")) {
				commandBuffer.append("/공개키답변");
				commandBuffer.append(" ");

				String publicKeyBase64 = Base64
						.encodeBase64String(ServerSessionKeyManager
								.getInstance().getPublicKeyBytes());
				if (null == publicKeyBase64) {
					commandBuffer.append("FALSE");
					commandBuffer.append(" ");
					commandBuffer.append("Base64 인코딩 실패");
				} else {
					commandBuffer.append("TRUE");
					commandBuffer.append(" ");
					commandBuffer.append(publicKeyBase64);
				}

			} else {
				// 비명령어의 경우 nothing
				commandBuffer.append(commandString);
			}
		} else {
			commandBuffer.append(commandString);
		}

		OutputMessage outObj = messageManger.createOutputMessage("Command");
		outObj.setAttribute("command", commandBuffer.toString());
		// commandOutputMessage.commonBody.messageSeq;
		// commandOutputMessage.command = commandBuffer.toString();

		log.info(String.format("outObj=[%s]", outObj.toString()));

		// FIXME!, 비지니스 로직 추가로 toSC, toOut 생성
		// letterToClientList.addLetterToClient(fromSC, outObj);
		letterSender.sendSync(outObj);
	}
}
