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
import kr.pe.codda.impl.message.LoginReq.LoginReq;
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
public class LoginSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		

		String paramRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == paramRequestType) {
			firstPage(req, res);
			return;
		}

		if (paramRequestType.equals("view")) {
			firstPage(req, res);
			return;
		} else if (paramRequestType.equals("proc")) {
			processPage(req, res);
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

	private void firstPage(HttpServletRequest req, HttpServletResponse res)
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

		doFirstPage(req, res, webServerSessionkey);
	}

	private void processPage(HttpServletRequest req, HttpServletResponse res)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException,
			IllegalArgumentException, SymmetricException {

		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramId = req.getParameter("id");
		String paramPwd = req.getParameter("pwd");

		String successURL = req.getParameter("successURL");

		log.info("param sessionkeyBase64=[{}]", paramSessionKeyBase64);
		log.info("param ivBase64=[{}]", paramIVBase64);
		log.info("param id=[{}]", paramId);
		log.info("param pwd=[{}}]", paramPwd);
		log.info("param successURL=[{}}]", successURL);

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

		byte[] idBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramId));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwd));

		String userId = new String(idBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,
		// CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);

		// log.info("id=[{}], password=[{}]", userId, password);

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(binaryPublicKeyReq);
		if (binaryPublicKeyOutputMessage instanceof BinaryPublicKey) {
			BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
			byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

			ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
					.getNewClientSessionKey(binaryPublicKeyBytes);

			byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
			byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
			ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
			LoginReq loginReq = new LoginReq();

			loginReq.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(idBytes)));
			loginReq.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
			loginReq.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytesOfServer));
			loginReq.setIvBase64(Base64.encodeBase64String(ivBytesOfServer));

			AbstractMessage loginOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(loginReq);
			if (loginOutputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes) loginOutputMessage;
				if (messageResultRes.getIsSuccess()) {
					HttpSession httpSession = req.getSession();
					httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID, userId);
				}

				doProcessPage(req, res, successURL, webServerSymmetricKey, webServerSessionkey,
						messageResultRes);
				return;
			} else {
				String errorMessage = "로그인 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(loginReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(loginOutputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} else {			
			String errorMessage = "로그인 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}

	private void doFirstPage(HttpServletRequest req, HttpServletResponse res, 
			ServerSessionkeyIF webServerSessionkey) {
		String successURL = req.getParameter("successURL");
		
		if (null == successURL) {
			successURL = "/";
		}
		
		req.setAttribute("successURL", successURL);		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		printJspPage(req, res, "/menu/member/login.jsp");
	}

	private void doProcessPage(HttpServletRequest req, HttpServletResponse res,
			String successURL, ServerSymmetricKeyIF webServerSymmetricKey, ServerSessionkeyIF webServerSessionkey,
			MessageResultRes messageResultRes) {

		req.setAttribute("successURL", successURL);
		req.setAttribute("messageResultRes", messageResultRes);

		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());

		printJspPage(req, res, "/menu/member/loginResult.jsp");
	}
}
