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
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 
 * @author Jonghoon Won
 *
 */
public final class BinaryPublicKeySExtor extends AbstractServerExecutor {
	
	@Override
	protected void doTask(ServerProjectConfigIF serverProjectConfig,
			LetterSender letterSender, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {
		
		// FIXME! 클라이언트로 받은 공개키는 현재 버려지지만 나중에 세션키를 이용한 데이터 교환시 반듯이 다시 쓰일것이다. 
		// byte[] clientPublicKeyBytes = (byte [])inObj.getAttribute("publicKeyBytes");
				
		
		OutputMessage outObj = messageManger.createOutputMessage("BinaryPublicKey");
		

		outObj.setAttribute("publicKeyBytes", ServerSessionKeyManager.getInstance().getPublicKeyBytes());
		

		letterSender.sendSelf(outObj);
	}
}
