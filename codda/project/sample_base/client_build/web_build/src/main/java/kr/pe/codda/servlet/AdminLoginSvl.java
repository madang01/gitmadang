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
import kr.pe.codda.impl.message.AdminLoginReq.AdminLoginReq;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 로그인
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class AdminLoginSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String parmRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == parmRequestType) {
			parmRequestType= "input";
		}

		if (parmRequestType.equals("input")) {
			loginInputPage(req, res);
			return;
		} else if (parmRequestType.equals("proc")) {
			loginResultPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(parmRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}

	private void loginInputPage(HttpServletRequest req, HttpServletResponse res)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {
		ServerSessionkeyIF webServerSessionkey = null;

		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());

			String errorMessage = "ServerSessionkeyManger instance init error";
			String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]",
					e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		doLoginInputPage(req, res, webServerSessionkey.getModulusHexStrForWeb());
	}

	private void loginResultPage(HttpServletRequest req, HttpServletResponse res)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException,
			IllegalArgumentException, SymmetricException {

		String parmSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String parmIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String parmUserIDCipherBase64 = req.getParameter("userID");
		String parmPwdCipherBase64 = req.getParameter("pwd");

		// String successURL = req.getParameter("successURL");
		
		if (null == parmSessionKeyBase64) {
			String errorMessage = "the request parameter parmSessionKeyBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(parmSessionKeyBase64)) {
			String errorMessage = "the request parameter parmSessionKeyBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == parmIVBase64) {
			String errorMessage = "the request parameter parmIVBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(parmIVBase64)) {
			String errorMessage = "the request parameter parmIVBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == parmUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(parmUserIDCipherBase64)) {
			String errorMessage = "the request parameter userID is not a base64 cipher text";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == parmPwdCipherBase64) {
			String errorMessage = "the request parameter parmPwdCipherBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(parmPwdCipherBase64)) {
			String errorMessage = "the request parameter parmPwdCipherBase64 is not a base64 cipher string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} 		
		
		
		/*if (successURL.indexOf('/') != 0) {
			String errorMessage = "the request parameter successURL doesn't begin a char '/'";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}*/

		log.info("parm sessionkeyBase64=[{}]", parmSessionKeyBase64);
		log.info("parm ivBase64=[{}]", parmIVBase64);
		log.info("parm userID=[{}]", parmUserIDCipherBase64);
		log.info("parm pwd=[{}}]", parmPwdCipherBase64);

		// req.setAttribute("isSuccess", Boolean.FALSE);

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmSessionKeyBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter parmSessionKeyBase64[{}], errormessage=[{}]", parmSessionKeyBase64, e.getMessage());
			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter parmSessionKeyBase64[%s] is a base64 encoding string, errormessage=[%s]", parmSessionKeyBase64, e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmIVBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter parmIVBase64[{}], errormessage=[{}]", parmIVBase64, e.getMessage());
			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter parmIVBase64[%s] is a base64 encoding string, errormessage=[%s]", parmIVBase64, e.getMessage());
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

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
		
		ServerSymmetricKeyIF webServerSymmetricKey = null;
		try {
			webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch(SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		// FIXME!
		log.info("한글 대칭키 암호문 base64={}", Base64.encodeBase64String(webServerSymmetricKey.encrypt("한글".getBytes("UTF8"))));

		byte[] userIDBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(parmPwdCipherBase64));

		String userId = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,
		// CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);

		// log.info("id=[{}], password=[{}]", userId, password);
		
		// FIXME!
		log.info("userID=[{}]", new String(userIDBytes, "UTF8"));

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(binaryPublicKeyReq);
		
		if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			doLoginFailurePage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
					errorMessage);
			return;
		}
		
		
		
		BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
				.getNewClientSessionKey(binaryPublicKeyBytes);

		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		AdminLoginReq adminLoginReq = new AdminLoginReq();

		adminLoginReq.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(userIDBytes)));
		adminLoginReq.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
		adminLoginReq.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytesOfServer));
		adminLoginReq.setIvBase64(Base64.encodeBase64String(ivBytesOfServer));			

		AbstractMessage loginOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(adminLoginReq);
		if (!(loginOutputMessage instanceof MessageResultRes)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(adminLoginReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(loginOutputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			doLoginFailurePage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
					errorMessage);
			return;
		}		
		
		MessageResultRes messageResultRes = (MessageResultRes) loginOutputMessage;
		if (messageResultRes.getIsSuccess()) {
			HttpSession httpSession = req.getSession();
			httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID, userId);
			
			doLoginSuccessPage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb());
			return;
		} else {
			doLoginFailurePage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
					messageResultRes.getResultMessage());
			return;
		}
	}

	private void doLoginFailurePage(HttpServletRequest req, HttpServletResponse res,
			ServerSymmetricKeyIF webServerSymmetricKey,
			String modulusHexString, 
			String errorMessage) {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);
		req.setAttribute("errorMessage", errorMessage);
		
		printJspPage(req, res, "/jsp/member/adminLoginFailureCallBack.jsp");
	}

	private void doLoginSuccessPage(HttpServletRequest req, HttpServletResponse res,
			ServerSymmetricKeyIF webServerSymmetricKey,
			String modulusHexString) {		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);

		printJspPage(req, res, "/jsp/member/adminLoginOKCallBack.jsp");
	}
	

	private void doLoginInputPage(HttpServletRequest req, HttpServletResponse res, 
			String modulusHexString) {		
		
		req.setAttribute("requestURI", "/");
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);
		printJspPage(req, res, JDF_ADMIN_LOGIN_INPUT_PAGE);
	}

}
