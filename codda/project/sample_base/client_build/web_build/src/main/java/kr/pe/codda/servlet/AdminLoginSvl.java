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
		String paramRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);

		if (null == paramRequestType || paramRequestType.equals("input")) {
			inputPage(req, res);
			return;
		} else if (paramRequestType.equals("proc")) {
			resultPage(req, res);
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

	
	private void inputPage(HttpServletRequest req, HttpServletResponse res)
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
		
		req.setAttribute("requestURI", "/");
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		
		/** /jsp/member/AdminLoginInput.jsp */
		printJspPage(req, res, JDF_ADMIN_LOGIN_INPUT_PAGE);
	}

	private void resultPage(HttpServletRequest req, HttpServletResponse res)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException,
			IllegalArgumentException, SymmetricException {

		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramUserIDCipherBase64 = req.getParameter("userID");
		String paramPwdCipherBase64 = req.getParameter("pwd");
		/**************** 파라미터 종료 *******************/

		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramSessionKeyBase64)) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramIVBase64) {
			String errorMessage = "the request parameter paramIVBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramIVBase64)) {
			String errorMessage = "the request parameter paramIVBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramUserIDCipherBase64)) {
			String errorMessage = "the request parameter userID is not a base64 cipher text";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramPwdCipherBase64)) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is not a base64 cipher string";
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

		log.info("param sessionkeyBase64=[{}]", paramSessionKeyBase64);
		log.info("param ivBase64=[{}]", paramIVBase64);
		log.info("param userID=[{}]", paramUserIDCipherBase64);
		log.info("param pwd=[{}}]", paramPwdCipherBase64);

		// req.setAttribute("isSuccess", Boolean.FALSE);

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramSessionKeyBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]", paramSessionKeyBase64, e.getMessage());
			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter paramSessionKeyBase64[%s] is a base64 encoding string, errormessage=[%s]", paramSessionKeyBase64, e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramIVBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]", paramIVBase64, e.getMessage());
			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter paramIVBase64[%s] is a base64 encoding string, errormessage=[%s]", paramIVBase64, e.getMessage());
			
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

		byte[] userIDBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwdCipherBase64));

		String userId = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,
		// CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);

		// log.info("id=[{}], password=[{}]", userId, password);
		
		// FIXME!
		// log.info("userID=[{}]", new String(userIDBytes, "UTF8"));

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		ClientSessionKeyIF clientSessionKey = null;
		
		synchronized (req) {
			clientSessionKey = (ClientSessionKeyIF)req.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY);
			
			if (null == clientSessionKey) {
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

					printAdminLoginFailureCallBackPage(req, res, 
							webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
							errorMessage);
					return;
				}		
				
				
				BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
				byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

				clientSessionKey = ClientSessionKeyManager.getInstance()
						.getNewClientSessionKey(binaryPublicKeyBytes);
				
				req.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY, clientSessionKey);			
			}
		}

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

			printAdminLoginFailureCallBackPage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
					errorMessage);
			return;
		}		
		
		MessageResultRes messageResultRes = (MessageResultRes) loginOutputMessage;
		if (messageResultRes.getIsSuccess()) {
			HttpSession httpSession = req.getSession();
			httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMIN_ID, userId);
			
			printAdminLoginOKCallBackPage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb());
			return;
		} else {
			printAdminLoginFailureCallBackPage(req, res, 
					webServerSymmetricKey, webServerSessionkey.getModulusHexStrForWeb(), 
					messageResultRes.getResultMessage());
			return;
		}
	}

	private void printAdminLoginFailureCallBackPage(HttpServletRequest req, HttpServletResponse res,
			ServerSymmetricKeyIF webServerSymmetricKey,
			String modulusHexString, 
			String errorMessage) {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);
		req.setAttribute("errorMessage", errorMessage);
		
		printJspPage(req, res, "/jsp/member/AdminLoginFailureCallBack.jsp");
	}

	private void printAdminLoginOKCallBackPage(HttpServletRequest req, HttpServletResponse res,
			ServerSymmetricKeyIF webServerSymmetricKey,
			String modulusHexString) {		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);

		printJspPage(req, res, "/jsp/member/AdminLoginOKCallBack.jsp");
	}
}
