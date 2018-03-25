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
import kr.pe.sinnori.impl.message.MemberRegisterWithSessionKey.MemberRegisterWithSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;
import nl.captcha.Captcha;

/**
 * 회원 가입<br/>
 * 회원 가입은 총 2개 페이지로 구성된다.<br/> 
 * 첫번째페이지는 입력 화면 페이지, 마지막 두번째 페이지는 회원 가입 결과 페이지이다.
 * @author Won Jonghoon
 *
 */

@SuppressWarnings("serial")
public class MemberSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String goPage = null;
		
		String parmPageMode = req.getParameter("pageMode");
		if (null == parmPageMode) {
			parmPageMode = "view";
		}
		
		if (!parmPageMode.equals("view") && !parmPageMode.equals("proc")) {
			goPage = "/menu/member/Member01.jsp";
			String errorMessage = new StringBuilder("페이지 모드는 2가지(view, proc) 입니다.")
			.append(CommonStaticFinalVars.NEWLINE)
			.append("페이지 모드 값[").append(parmPageMode).append("]이 잘못 되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}		

		ServerSessionkeyIF webServerSessionkey = null;
		// ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
			// serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes, ivBytes);
		} catch (SymmetricException e) {
			log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
			
			String errorMessage = "ServerSessionkeyManger instance init error";
			String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
			printMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String modulusHex = webServerSessionkey.getModulusHexStrForWeb();
		req.setAttribute("modulusHex", modulusHex);
		
		if (parmPageMode.equals("view")) {
			goPage = "/menu/member/Member01.jsp";			
		} else {
			goPage = "/menu/member/Member02.jsp";
			
			String parmSessionKeyBase64 = req.getParameter("sessionkeyBase64");
			String parmIVBase64 = req.getParameter("ivBase64");

			String parmId = req.getParameter("id");
			String parmPwd = req.getParameter("pwd");
			String parmNickname = req.getParameter("nickname");
			String parmPwdHint = req.getParameter("pwdHint");
			String parmPwdAnswer = req.getParameter("pwdAnswer");
			String parmCaptchaAnswer = req.getParameter("answer");
			
			log.info("parm sessionkeyBase64=[{}], parm ivBase64=[{}], " +
					"parm id=[{}], parm pwd=[{}], parm nickname=[{}], " +
					"parm pwdHint=[{}], parm pwdAnswer=[{}], parm answer=[{}]", 
					parmSessionKeyBase64, parmIVBase64, 
					parmId, parmPwd, parmNickname, 
					parmPwdHint, parmPwdAnswer, parmCaptchaAnswer);
			
			if (null == parmSessionKeyBase64) {
				String errorMessage = "세션키 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
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
			
			if (null == parmCaptchaAnswer) {
				String errorMessage = "Captcha 값을 입력해 주세요.";
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}
			
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
			
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID("");
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("회원 가입이 실패하였습니다.");

			ServerSymmetricKeyIF webServerSymmetricKey = null;
			try {
				webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes, ivBytes);
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
			byte[] userIdBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmId));
			byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmPwd));
			byte[] nicknameBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmNickname));
			byte[] pwdHintBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmPwdHint));
			byte[] pwdAnswerBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmPwdAnswer));
			byte[] answerBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmCaptchaAnswer));
			
			String userId = new String(userIdBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			String answer = new String(answerBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			
			/*
			String userId = webUserSymmetricKey.decryptStringBase64(parmId);
			String password = webUserSymmetricKey.decryptStringBase64(parmPwd);
			String nickname = webUserSymmetricKey
					.decryptStringBase64(parmNickname);
			String pwdHint = webUserSymmetricKey
					.decryptStringBase64(parmPwdHint);
			String pwdAnswer = webUserSymmetricKey.decryptStringBase64(parmPwdAnswer);
			
			String answer = webUserSymmetricKey.decryptStringBase64(parmCaptchaAnswer);
			
			
			
			
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
			*/
			HttpSession httpSession = req.getSession();
			Captcha captcha = (Captcha) httpSession.getAttribute(Captcha.NAME);
			if (!captcha.isCorrect(answer)) {
				
				String errorMessage = String.format("사용자가 입력한 Captcha 값[%s]과 내부 Captcha 값[%s]이 다릅니다.", answer, captcha.getAnswer());
				log.warn(errorMessage);
				
				printMessagePage(req, res, "입력한 Captcha 값이 틀렸습니다.", errorMessage);
				return;
			}
			
			httpSession.removeAttribute(Captcha.NAME);

			log.info("userId=[{}]", userId);

			// HttpSession session = req.getSession();

			// ClientSessionKeyManager sessionKeyClientManager =
			// ClientSessionKeyManager.getInstance();		

			// String defaultServerName =
			// (String)conf.getResource("default_server_name");
			// getConnectionPool
			// ServerResource defaultServerResource =
			// SinnoriClientManager.getInstance().getServerResource(defaultServerName);

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
				
				

				MemberRegisterWithSessionKey memberRegisterWithSessionKeyInObj = new MemberRegisterWithSessionKey();
				
				memberRegisterWithSessionKeyInObj.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(userIdBytes)));
				memberRegisterWithSessionKeyInObj.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
				memberRegisterWithSessionKeyInObj.setNicknameCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(nicknameBytes)));
				memberRegisterWithSessionKeyInObj.setHintCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(pwdHintBytes)));
				memberRegisterWithSessionKeyInObj.setAnswerCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(pwdAnswerBytes)));
				memberRegisterWithSessionKeyInObj.setSessionKeyBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(sessionKeyBytesOfServer)));
				memberRegisterWithSessionKeyInObj.setIvBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(ivBytesOfServer)));				

				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(memberRegisterWithSessionKeyInObj);					
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
			req.setAttribute(WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
			// req.setAttribute("errorMessage", errorMessage);
			req.setAttribute("parmIVBase64", parmIVBase64);
		}		
		
		printJspPage(req, res, goPage);
	}
}
