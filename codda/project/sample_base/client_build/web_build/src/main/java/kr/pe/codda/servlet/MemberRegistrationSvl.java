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
package kr.pe.codda.servlet;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.SiteType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;
import nl.captcha.Captcha;

/**
 * 회원 가입<br/>
 * 회원 가입은 총 2개 페이지로 구성된다.<br/> 
 * 첫번째페이지는 입력 화면 페이지, 마지막 두번째 페이지는 회원 가입 결과 페이지이다.
 * @author Won Jonghoon
 *
 */

@SuppressWarnings("serial")
public class MemberRegistrationSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		
		String paramRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == paramRequestType || paramRequestType.equals("input")) {
			memberInputPage(req, res);
			return;
		} else if (paramRequestType.equals("proc")) {		
			memberResultPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(paramRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
		
	private void memberInputPage(HttpServletRequest req, HttpServletResponse res) {
		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		
		SiteType siteType = SiteType.USER;
		String nativeSiteType = getServletConfig().getInitParameter("siteType");
		
		if (null == nativeSiteType) {
			String errorMessage = "web.xml 에서 init-param 'siteType' 를  지정하지 않았습니다. 참고) siteType 값은 {admin, user} 로 구성되어 있습니다";
			
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}
		
		nativeSiteType = nativeSiteType.toUpperCase();
			
		try {
			siteType = SiteType.valueOf(nativeSiteType);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder().append("web.xml 에서 init-param 'siteType' 값[")
					.append(nativeSiteType)
					.append("]이 잘못되었습니다.  참고) siteType 값은 {admin, user} 로 구성되어 있습니다").toString();			
			printErrorMessagePage(req, res, errorMessage, null);
			return;	
		}		
		
		
		if (siteType.equals(SiteType.ADMIN)) {
			printJspPage(req, res, "/jsp/member/MemberRegistrationInputForAdminSite.jsp");
		} else {
			printJspPage(req, res, "/jsp/member/MemberRegistrationInputForUserSite.jsp");
		}
		
		
	}
		
	private void memberResultPage(HttpServletRequest req, HttpServletResponse res) throws IllegalArgumentException, SymmetricException, IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {
		
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
	
		String paramUserID = req.getParameter("userID");
		String paramPwd = req.getParameter("pwd");
		String paramNickname = req.getParameter("nickname");
		String paramPwdHint = req.getParameter("pwdHint");
		String paramPwdAnswer = req.getParameter("pwdAnswer");
		String paramCaptchaAnswer = req.getParameter("answer");
		/**************** 파라미터 종료 *******************/
		
		log.info("param sessionkeyBase64=[{}], param ivBase64=[{}], " +
				"param userID=[{}], param pwd=[{}], param nickname=[{}], " +
				"param pwdHint=[{}], param pwdAnswer=[{}], param answer=[{}]", 
				paramSessionKeyBase64, paramIVBase64, 
				paramUserID, paramPwd, paramNickname, 
				paramPwdHint, paramPwdAnswer, paramCaptchaAnswer);
		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "세션키 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramIVBase64) {
			String errorMessage = "IV 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramUserID) {
			String errorMessage = "아이디 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramPwd) {
			String errorMessage = "비밀번호 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramNickname) {
			String errorMessage = "별명 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramPwdHint) {
			String errorMessage = "비밀번호 분실시 힌트 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramPwdAnswer) {
			String errorMessage = "비밀번호 분실시 답변 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (null == paramCaptchaAnswer) {
			String errorMessage = "Captcha 값을 입력해 주세요";
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramSessionKeyBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]", paramSessionKeyBase64, e.getMessage());
			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			// String debugMessage = String.format("check whether the parameter paramSessionKeyBase64[%s] is a base64 encoding string, errormessage=[%s]", paramSessionKeyBase64, e.getMessage());
			
			
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramIVBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]", paramIVBase64, e.getMessage());
			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			// String debugMessage = String.format("check whether the parameter paramIVBase64[%s] is a base64 encoding string, errormessage=[%s]", paramIVBase64, e.getMessage());
			
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}	
		
		ServerSessionkeyIF webServerSessionkey = null;
		// ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
			// serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes, ivBytes);
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);			
			
			// String debugMessage = e.getMessage();
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
		
		ServerSymmetricKeyIF webServerSymmetricKey = null;
		try {
			webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			log.warn(debugMessage, e);
			
			
			
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		} catch(SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			log.warn(debugMessage, e);
			
			
			
			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
		}
	
		byte[] userIdBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramUserID));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwd));
		byte[] nicknameBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramNickname));
		byte[] pwdHintBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwdHint));
		byte[] pwdAnswerBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwdAnswer));
		byte[] answerBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramCaptchaAnswer));
		
		String answer = new String(answerBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		
		HttpSession httpSession = req.getSession();
		Captcha captcha = (Captcha) httpSession.getAttribute(Captcha.NAME);
		if (!captcha.isCorrect(answer)) {
			
			String debugMessage = String.format("사용자가 입력한 Captcha 값[%s]과 내부 Captcha 값[%s]이 다릅니다.", answer, captcha.getAnswer());
			log.warn(debugMessage);
			
			printMemberRegistrationFailureCallBackPage(req, res, "입력한 Captcha 값이 틀렸습니다.");
			return;
		}
		
		httpSession.removeAttribute(Captcha.NAME);
	
		// log.info("userId=[{}]", userId);
	
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		
		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();			
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());
		
		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(binaryPublicKeyReq);					
		if (binaryPublicKeyOutputMessage instanceof BinaryPublicKey) {
			BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) binaryPublicKeyOutputMessage;
			byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
			ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance().getNewClientSessionKey(binaryPublicKeyBytes);
			
			
			byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();								
			byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
			ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
	
			MemberRegisterReq memberRegisterReq = new MemberRegisterReq();
			
			memberRegisterReq.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(userIdBytes)));
			memberRegisterReq.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
			memberRegisterReq.setNicknameCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(nicknameBytes)));
			memberRegisterReq.setHintCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(pwdHintBytes)));
			memberRegisterReq.setAnswerCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(pwdAnswerBytes)));
			memberRegisterReq.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytesOfServer));
			memberRegisterReq.setIvBase64(Base64.encodeBase64String(ivBytesOfServer));
			memberRegisterReq.setIp(req.getRemoteAddr());
	
			AbstractMessage memberRegisterOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(memberRegisterReq);					
			if (memberRegisterOutputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)memberRegisterOutputMessage;
				
				if (messageResultRes.getIsSuccess()) {
					printMemberRegistrationOKCallBackPage(req, res);
				} else {
					printMemberRegistrationFailureCallBackPage(req, res, messageResultRes.getResultMessage());
				}
				
				return;
			} else {
				String errorMessage = "회원 가입이 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(memberRegisterReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(memberRegisterOutputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
				return;
			}	
		} else {
			String errorMessage = "회원 가입이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printMemberRegistrationFailureCallBackPage(req, res, errorMessage);
			return;
			
		}	
	}
	
	private void printMemberRegistrationOKCallBackPage(HttpServletRequest req, HttpServletResponse res) {		
		printJspPage(req, res, "/jsp/member/MemberRegistrationOKCallBack.jsp");
	}
	
	private void printMemberRegistrationFailureCallBackPage(HttpServletRequest req, HttpServletResponse res,
			String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);		
		printJspPage(req, res, "/jsp/member/MemberRegistrationFailureCallBack.jsp");
	}
}
