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

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.LoginWithSessionKey.LoginWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;

import org.apache.commons.codec.binary.Base64;

/**
 * 로그인
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class LoginSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String pageGubun = req.getParameter("pageGubun");
		
		
		if (null == pageGubun || !pageGubun.equals("step2")) {
			kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager sessionKeyServerManger = kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager.getInstance();
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb();
			req.setAttribute("modulusHex", modulusHex);
			printJspPage(req, res, "/member/login.jsp");
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
			
			SymmetricKey  webUserSymmetricKey = null;
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
			try {
				
				webUserSymmetricKey = sessionKeyServerManger.getSymmetricKey(WebCommonStaticFinalVars.WEBSITE_JAVA_SYMMETRIC_KEY_ALGORITHM_NAME, CommonType.SymmetricKeyEncoding.BASE64, parmSessionKeyBase64, parmIVBase64);
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			} catch(SymmetricException e) {
				String errorMessage = e.getMessage();
				log.warn(errorMessage);
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			String userId = webUserSymmetricKey.decryptStringBase64(parmId);
			String password = webUserSymmetricKey.decryptStringBase64(parmPwd);
			
			log.info("id=[{}], password=[{}]", userId, password);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID("");
			messageResultOutObj.setTaskResult("N");
			messageResultOutObj.setResultMessage("로그인 실패하였습니다.");
			
			String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			
			
			BinaryPublicKey binaryPublicKeyInObj = new BinaryPublicKey();			
			binaryPublicKeyInObj.setPublicKeyBytes(sessionKeyServerManger.getPublicKeyBytes());
			
			AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(binaryPublicKeyInObj);					
			if (messageFromServer instanceof BinaryPublicKey) {
				BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) messageFromServer;
				byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
				ClientSessionKeyManager clientSessionKeyManage = new ClientSessionKeyManager(binaryPublicKeyBytes);
				
				byte sessionKeyBytes[] = clientSessionKeyManage.getSessionKey();
				SymmetricKey serverSymmetricKey = clientSessionKeyManage.getSymmetricKey();				
				byte ivBytes[] = serverSymmetricKey.getIV();

				LoginWithSessionKey loginInObj = new LoginWithSessionKey();
				
				loginInObj.setIdCipherBase64(serverSymmetricKey.encryptStringBase64(userId));
				loginInObj.setPwdCipherBase64(serverSymmetricKey.encryptStringBase64(password));
				loginInObj.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytes));
				loginInObj.setIvBase64(Base64.encodeBase64String(ivBytes));				

				messageFromServer = clientProject.sendSyncInputMessage(loginInObj);					
				if (messageFromServer instanceof MessageResult) {
					messageResultOutObj = (MessageResult)messageFromServer;
					if (messageResultOutObj.getTaskResult().equals("Y")){
						HttpSession httpSession = req.getSession();
						httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME, userId);
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
			req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
			req.setAttribute("parmIVBase64", parmIVBase64);
			
			printJspPage(req, res, "/member/loginResult.jsp");
		}
		
		
	}


}
