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
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.MemberRegisterWithSessionKey.MemberRegisterWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import nl.captcha.Captcha;

import org.apache.commons.codec.binary.Base64;

/**
 * 회원 가입<br/>
 * 회원 가입은 총 2개 페이지로 구성된다.<br/> 
 * 첫번째페이지는 입력 화면 페이지, 마지막 두번째 페이지는 회원 가입 결과 페이지이다.
 * @author Jonghoon Won
 *
 */

@SuppressWarnings("serial")
public class MemberSvl extends AbstractServlet {
	final String arryPageURL[] = { "/member/Member01.jsp",
			"/member/Member02.jsp" };
	
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		String pageGubun = req.getParameter("pagegubun");
		log.info(String.format("pageGubun[%s]", pageGubun));

		String goPage = null;

		if (null == pageGubun || !pageGubun.equals("step2")) {
			goPage = arryPageURL[0];

			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager
					.getInstance();
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb();
			req.setAttribute("modulusHex", modulusHex);

		} else {
			goPage = arryPageURL[1];
			

			String parmSessionKeyBase64 = req.getParameter("sessionkeyBase64");
			String parmIVBase64 = req.getParameter("ivBase64");

			String parmId = req.getParameter("id");
			String parmPwd = req.getParameter("pwd");
			String parmNickname = req.getParameter("nickname");
			String parmPwdHint = req.getParameter("pwdHint");
			String parmPwdAnswer = req.getParameter("pwdAnswer");
			String parmAnswer = req.getParameter("answer");
			
			log.info(String.format("parm sessionkeyBase64=[%s]", parmSessionKeyBase64));
			log.info(String.format("parm ivBase64=[%s]", parmIVBase64));
			log.info(String.format("parm id=[%s]", parmId));
			log.info(String.format("parm pwd=[%s]", parmPwd));
			log.info(String.format("parm nickname=[%s]", parmNickname));
			log.info(String.format("parm pwdHint=[%s]", parmPwdHint));
			log.info(String.format("parm pwdAnswer=[%s]", parmPwdAnswer));
			log.info(String.format("parm answer=[%s]", parmAnswer));
			
			if (null == parmSessionKeyBase64) {
				String errorMessage = "세션키 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmIVBase64) {
				String errorMessage = "IV 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmId) {
				String errorMessage = "아이디 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmPwd) {
				String errorMessage = "비밀번호 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmNickname) {
				String errorMessage = "별명 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmPwdHint) {
				String errorMessage = "비밀번호 분실시 힌트 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmPwdAnswer) {
				String errorMessage = "비밀번호 분실시 답변 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (null == parmAnswer) {
				String errorMessage = "Captcha 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID("");
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("회원 가입이 실패하였습니다.");

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

			// String errorMessage = "";
			
			String userId = webUserSymmetricKey.decryptStringBase64(parmId);
			String password = webUserSymmetricKey.decryptStringBase64(parmPwd);
			String nickname = webUserSymmetricKey
					.decryptStringBase64(parmNickname);
			String pwdHint = webUserSymmetricKey
					.decryptStringBase64(parmPwdHint);
			String pwdAnswer = webUserSymmetricKey.decryptStringBase64(parmPwdAnswer);
			
			String answer = webUserSymmetricKey.decryptStringBase64(parmAnswer);
			
			
			
			
			if (userId.equals("")) {
				String errorMessage = "아이디 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (password.equals("")) {
				String errorMessage = "비밀번호 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (nickname.equals("")) {
				String errorMessage = "별명 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (pwdHint.equals("")) {
				String errorMessage = "비밀번호 분실시 힌트 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (pwdAnswer.equals("")) {
				String errorMessage = "비밀번호 분실시 답변 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			if (answer.equals("")) {
				String errorMessage = "Captcha 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
			HttpSession httpSession = req.getSession();
			Captcha captcha = (Captcha) httpSession.getAttribute(Captcha.NAME);
			if (!captcha.isCorrect(answer)) {
				
				String errorMessage = String.format("사용자가 입력한 Captcha 값[%s]과 내부 Captcha 값[%s]이 다릅니다.", answer, captcha.getAnswer());
				log.warn(errorMessage);
				
				printMessagePage(req, res, "입력한 Captcha 값이 틀렸습니다.", errorMessage);
				return;
			}

			log.info(String.format("userId=[%s]", userId));

			// HttpSession session = req.getSession();

			// ClientSessionKeyManager sessionKeyClientManager =
			// ClientSessionKeyManager.getInstance();
			

			

			// String defaultServerName =
			// (String)conf.getResource("default_server_name");
			// getConnectionPool
			// ServerResource defaultServerResource =
			// SinnoriClientManager.getInstance().getServerResource(defaultServerName);

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

				MemberRegisterWithSessionKey memberRegisterWithSessionKeyInObj = new MemberRegisterWithSessionKey();
				
				memberRegisterWithSessionKeyInObj.setIdCipherBase64(serverSymmetricKey.encryptStringBase64(userId));
				memberRegisterWithSessionKeyInObj.setPwdCipherBase64(serverSymmetricKey.encryptStringBase64(password));
				memberRegisterWithSessionKeyInObj.setNicknameCipherBase64(serverSymmetricKey.encryptStringBase64(nickname));
				memberRegisterWithSessionKeyInObj.setHintCipherBase64(serverSymmetricKey.encryptStringBase64(pwdHint));
				memberRegisterWithSessionKeyInObj.setAnswerCipherBase64(serverSymmetricKey.encryptStringBase64(pwdAnswer));
				memberRegisterWithSessionKeyInObj.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytes));
				memberRegisterWithSessionKeyInObj.setIvBase64(Base64.encodeBase64String(ivBytes));				

				messageFromServer = clientProject.sendSyncInputMessage(memberRegisterWithSessionKeyInObj);					
				if (messageFromServer instanceof MessageResult) {
					messageResultOutObj = (MessageResult)messageFromServer;
					/*if (outObj.getTaskResult().equals("N")) {
						errorMessage = outObj.getResultMessage();
						log.warn(errorMessage);
					}*/			
					
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

			// error 처리르 위한 loop문 종료
			// log.info(String.format("9. errorMessage=[%s]", errorMessage));
			
			// FIXME!
			log.warn(messageResultOutObj.toString());

			req.setAttribute("messageResultOutObj", messageResultOutObj);
			req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
			// req.setAttribute("errorMessage", errorMessage);
			req.setAttribute("parmIVBase64", parmIVBase64);
		}

		log.info(String.format("goPage[%s]", goPage));

		printJspPage(req, res, goPage);
	}
}
