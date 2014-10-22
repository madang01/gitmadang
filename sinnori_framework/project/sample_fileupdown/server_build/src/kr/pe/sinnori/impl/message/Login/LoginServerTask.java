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
package kr.pe.sinnori.impl.message.Login;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.SinnoriUnsupportedEncodingException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 로그인 서버 비지니스 로직
 * @author "Jonghoon Won"
 *
 */
public class LoginServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		Login inObj = (Login) messageFromClient;
		String idCipherBase64 = inObj.getIdCipherBase64();
		String pwdCipherBase64 = inObj.getPwdCipherBase64();
		String sessionKeyBase64 = inObj.getSessionKeyBase64();
		String ivBase64 = inObj.getIvBase64();
		
		
		MessageResult outObj = new MessageResult();
		outObj.setTaskMessageID(inObj.getMessageID());
		/*OutputMessage outObj = messageManger.createOutputMessage("MessageResult");
		outObj.setAttribute("taskMessageID", inObj.getMessageID());*/
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = ServerSessionKeyManager.getInstance()
					.getSymmetricKey(sessionKeyBase64, ivBase64);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::getSymmetricKey::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		
		
// 		ClientResourceManagerIF clientResourceManager = (ClientResourceManagerIF)clientResource;
		String clientCharsetName = serverProjectConfig.getCharset().name();
		
		String mID =  null;
		String mPWD =  null;
		
		try {
			mID = symmetricKey.decryptStringBase64(idCipherBase64, clientCharsetName);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			/*outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			
			/*outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("SinnoriUnsupportedEncodingException", e);
			
			/*outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::id::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		try {
			mPWD =  symmetricKey.decryptStringBase64(pwdCipherBase64, clientCharsetName);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			/*outObj.setAttribute("resultMessage",  new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			
			/*outObj.setAttribute("resultMessage", new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("SinnoriUnsupportedEncodingException", e);
			/*outObj.setAttribute("resultMessage", new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder("서버").append("::password::").append(e.toString()).toString());
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		
		log.info(String.format("mID=[%s], mPWD=[%s]", mID, mPWD));
		
		if (!mID.equals("test01")) {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", new StringBuilder(mID).append(" 아이디가 존재하지 않습니다.").toString());*/
			//letterSender.sendSync(outObj);
			outObj.setTaskResult("N");
			outObj.setResultMessage(new StringBuilder(mID).append(" 아이디가 존재하지 않습니다.").toString());
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		if (!mPWD.equals("1234")) {
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "비밀번호가 잘못 되었습니다.");

			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage("비밀번호가 잘못 되었습니다.");
			letterSender.addSyncMessage(outObj);
			return;
		}
		
		if (loginManager.isLogin(mID)) {;
			/*outObj.setAttribute("taskResult", "N");
			outObj.setAttribute("resultMessage", "이미 로그인한 상태입니다.");

			letterSender.sendSync(outObj);*/
			outObj.setTaskResult("N");
			outObj.setResultMessage("이미 로그인한 상태입니다.");
			letterSender.addSyncMessage(outObj);
			return;
		}

		ClientResource clientResource = letterSender.getClientResource();
		loginManager.login(mID, clientResource);
		
		/*outObj.setAttribute("taskResult", "Y");
		outObj.setAttribute("resultMessage", "회원 가입을 축하드립니다.");
		letterSender.sendSync(outObj);*/
		
		outObj.setTaskResult("Y");
		outObj.setResultMessage("로그인 성공하셨습니다.");
		letterSender.addSyncMessage(outObj);
		
	}
}
