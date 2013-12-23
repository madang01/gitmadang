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
import kr.pe.sinnori.common.exception.SinnoriUnsupportedEncodingException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;

/**
 * 메세지 식별자 Login 비지니스 로직
 * 
 * @author Jonghoon Won
 * 
 */
public final class LoginSExtor extends AbstractServerExecutor {

	@Override
	protected void doTask(SocketChannel fromSC, InputMessage inObj,
			MessageMangerIF messageManger,			
			ClientResourceManagerIF clientResourceManager)
			throws MessageInfoNotFoundException, MessageItemException {

		String idCipherBase64 = (String)inObj.getAttribute("idCipherBase64");
		String pwdCipherBase64 = (String)inObj.getAttribute("pwdCipherBase64");
		String sessionKeyBase64 = (String)inObj.getAttribute("sessionKeyBase64");
		String ivBase64 = (String)inObj.getAttribute("ivBase64");
		
		OutputMessage outObj = messageManger.createOutputMessage("MessageResult");
		outObj.setAttribute("taskMessageID", inObj.getMessageID());
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = ServerSessionKeyManager.getInstance()
					.getSymmetricKey(sessionKeyBase64, ivBase64);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		}
		
		ClientResource clientResource = clientResourceManager.getClientResource(fromSC);
		String clientCharsetName = clientResource.getCharset().name();
		
		String mID =  null;
		String mPWD =  null;
		
		try {
			mID = symmetricKey.decryptStringBase64(idCipherBase64, clientCharsetName);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("SinnoriUnsupportedEncodingException", e);
			
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		}
		
		try {
			mPWD =  symmetricKey.decryptStringBase64(pwdCipherBase64, clientCharsetName);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			
			outObj.setAttribute("resultMessage", new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("SinnoriUnsupportedEncodingException", e);
			outObj.setAttribute("resultMessage", new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			sendSelf(outObj);
			return;
		}
		
		
		log.info(String.format("mID=[%s], mPWD=[%s]", mID, mPWD));
		
		if (!mID.equals("test01")) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder(mID).append(" 아이디가 존재하지 않습니다.").toString());

			sendSelf(outObj);
			return;
		}
		
		if (!mPWD.equals("1234")) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "비밀번호가 잘못 되었습니다.");

			sendSelf(outObj);
			return;
		}
		
		if (clientResourceManager.isLogin(mID)) {
			outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "이미 로그인한 상태입니다.");

			sendSelf(outObj);
			return;
		}
		
		
		clientResourceManager.loginOK(mID, fromSC);
		// clientResource.setLoginID(mID);
		
		
		outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "회원 가입을 축하드립니다.");

		sendSelf(outObj);
	}
}
