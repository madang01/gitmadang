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
package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.LoginWithSessionKey.LoginWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;

/**
 * 로그인
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class LoginSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String pageGubun = req.getParameter("pageGubun");
		
		if (null == pageGubun || !pageGubun.equals("step2")) {
			ServerSessionkeyIF webServerSessionkey = null;
			
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			String modulusHexString = webServerSessionkey.getModulusHexStrForWeb();
			
			req.setAttribute("modulusHexString", modulusHexString);
			printJspPage(req, res, "/menu/member/login.jsp");
			return;
		} else {
			String parmSessionKeyBase64 = req.getParameter("sessionkeyBase64");
			String parmIVBase64 = req.getParameter("ivBase64");

			String parmId = req.getParameter("id");
			String parmPwd = req.getParameter("pwd");
			
			log.info(String.format("parm sessionkeyBase64=[%s]", parmSessionKeyBase64));
			log.info(String.format("parm ivBase64=[%s]", parmIVBase64));

			log.info(String.format("parm id=[%s]", parmId));
			log.info(String.format("parm pwd=[%s]", parmPwd));
			
			// req.setAttribute("isSuccess", Boolean.FALSE);
			
			
			byte[] sessionkeyBytes = null;
			try {
				sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmSessionKeyBase64);
			} catch(Exception e) {
				log.warn("parmSessionKeyBase64[{}] base64 decode error, errormessage=[{}]", parmSessionKeyBase64, e.getMessage());
				
				String errorMessage = "the parameter parmSessionKeyBase64 is not a base64 string";
				String debugMessage = String.format("parmSessionKeyBase64[%s] base64 decode error, errormessage=[%s]", parmSessionKeyBase64, e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			byte[] ivBytes = null;
			try {
				ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmIVBase64);
			} catch(Exception e) {
				log.warn("parmIVBase64[{}] base64 decode error, errormessage=[{}]", parmIVBase64, e.getMessage());
				
				String errorMessage = "the parameter parmIVBase64 is not a base64 string";
				String debugMessage = String.format("parmIVBase64[%s] base64 decode error, errormessage=[%s]", parmIVBase64, e.getMessage());
				
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}		
			
			
			ServerSessionkeyIF webServerSessionkey = null;
			ServerSymmetricKeyIF webServerSymmetricKey = null;
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
				webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes);
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			
			byte[] idBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmId));
			byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmPwd));
			
			String userId = new String(idBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			// String password =  new String(passwordBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			
			// log.info("id=[{}], password=[{}]", userId, password);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID("");
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("로그인 실패하였습니다.");
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
			
			BinaryPublicKey binaryPublicKeyInObj = new BinaryPublicKey();			
			binaryPublicKeyInObj.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());
			
			AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(binaryPublicKeyInObj);					
			if (messageFromServer instanceof BinaryPublicKey) {
				BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) messageFromServer;
				byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
				
				ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance().getNewClientSessionKey(binaryPublicKeyBytes);
				
				
				byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();								
				byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
				ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
				LoginWithSessionKey loginInObj = new LoginWithSessionKey();
				
				
				loginInObj.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(idBytes)));
				loginInObj.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
				loginInObj.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytesOfServer));
				loginInObj.setIvBase64(Base64.encodeBase64String(ivBytesOfServer));				

				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(loginInObj);					
				if (messageFromServer instanceof MessageResult) {
					messageResultOutObj = (MessageResult)messageFromServer;
					if (messageResultOutObj.getIsSuccess()){
						HttpSession httpSession = req.getSession();
						httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME, userId);
					}
					
				} else {
					// errorMessage = messageFromServer.toString();
					// FIXME!
					log.warn(messageFromServer.toString());
					
					messageResultOutObj.setResultMessage("서버에서 회원 가입 처리가 실패하였습니다.");
				}	
			} else {
				// FIXME!
				log.warn(messageFromServer.toString());
				
				messageResultOutObj.setResultMessage("서버로 부터 공개키 바이트 배열을 얻는데 실패하였습니다.");
				
			}
			
			req.setAttribute("messageResultOutObj", messageResultOutObj);
			req.setAttribute(WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
			req.setAttribute("parmIVBase64", parmIVBase64);
			
			printJspPage(req, res, "/menu/member/loginResult.jsp");
		}
		
		
	}


}
