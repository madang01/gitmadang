package kr.pe.codda.servlet;
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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;



@SuppressWarnings("serial")
public class JSRSATestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		
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
		
	private void inputPage(HttpServletRequest req, HttpServletResponse res) {
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
		
		printJspPage(req, res, "/jsp/util/JSRSATestInput.jsp");
	}
	
	private void resultPage(HttpServletRequest req, HttpServletResponse res) {
		
		String paramEncryptedHexTextWithPublicKey = req.getParameter("encryptedHexTextWithPublicKey");
		String paramPlainText = req.getParameter("plainText");
		
		log.info("paramEncryptedHexTextWithPublicKey[{}]", paramEncryptedHexTextWithPublicKey);
		log.info("paramPlainText[{}]", paramPlainText);
		
		if (null == paramEncryptedHexTextWithPublicKey) {
			String errorMessage = "헥사로 표현된 공개키로 암호화한 암호문을 입력해 주세요";
			String debugMessage = "the web parameter 'encryptedHexTextWithPublicKey' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPlainText) {
			String errorMessage = "평문을 입력해 주세요";
			String debugMessage = "the web parameter 'plainText' is null";
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
		
		/**
		 * 자바 스크립에서 RSA 로 암호문을 만들면 \r\n 을 보존하지 않고 강제적으로 \n 으로 변경한다. 
		 * 따라서 원문과 복혹문 정상적인 비교를 위해서 원문도 똑같이 \n 으로 변경해 주어야 한다. 
		 */
		String plainHexText = HexUtil
				.getHexStringFromByteArray(paramPlainText.replaceAll("\r\n", "\n").getBytes(CommonStaticFinalVars.CIPHER_CHARSET));		
		//log.info("plainText hex[%s]", plainTextHex);
		// String sessionKeyHex =  new String(HexUtil.hexToByteArray(sessionKeyDoubleHex));
		//log.info("sessionKeyHex=[%s]", sessionKeyHex);
		byte encryptedBytesWithPublicKey[] = HexUtil.getByteArrayFromHexString(paramEncryptedHexTextWithPublicKey);
		
		byte decryptedBytesUsingPrivateKey[] = null;
		try {
			decryptedBytesUsingPrivateKey = webServerSessionkey.decryptUsingPrivateKey(encryptedBytesWithPublicKey);
		} catch (SymmetricException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramEncryptedHexTextWithPublicKey=[")
					.append(paramEncryptedHexTextWithPublicKey)					
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
		}			
		String decryptedHexTextUsingPrivateKey = HexUtil.getHexStringFromByteArray(decryptedBytesUsingPrivateKey);
		//log.info(String.format("decryptUsingPrivateKey=[%s]", decryptUsingPrivateKeyHex));
		
		log.info("plainHexText={}", plainHexText);
		log.info("decryptedHexTextUsingPrivateKey={}", decryptedHexTextUsingPrivateKey);
		
		boolean isSame = plainHexText.equals(decryptedHexTextUsingPrivateKey);
		//log.info(String.format("resultMessage=[%s]", resultMessage));
		
		
		String decryptedText = new String(decryptedBytesUsingPrivateKey);
		
		req.setAttribute("orignalPlainText", paramPlainText);
		req.setAttribute("decryptedText", decryptedText);
		req.setAttribute("isSame", String.valueOf(isSame));		
		printJspPage(req, res, "/jsp/util/JSRSATestResult.jsp");	
	}
	
}
