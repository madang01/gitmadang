package kr.pe.sinnori.servlet;
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

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.util.HexUtil;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;



@SuppressWarnings("serial")
public class JSBNTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.TEST_EXAMPLE);
		
		
		String parmRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == parmRequestType) {		
			firstPage(req, res);			
			return;
		}
		
		if (parmRequestType.equals("view")) {
			firstPage(req, res);
			return;
		} else if (parmRequestType.equals("proc")) {		
			processPage(req, res);
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
		
	private void firstPage(HttpServletRequest req, HttpServletResponse res) {
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
		
		printJspPage(req, res, "/menu/testcode/JSBNTest01.jsp");
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) {
		
		String parmEncryptedBytesWithPublicKeyHex = req.getParameter("encryptedBytesWithPublicKey");
		String parmPlainText = req.getParameter("plainText");
		
		log.info("parmEncryptedBytesWithPublicKeyHex[{}]", parmEncryptedBytesWithPublicKeyHex);
		log.info("parmPlainText[{}]", parmPlainText);
		
		if (null == parmEncryptedBytesWithPublicKeyHex) {
			String errorMessage = "공개키로 암호화한 암호문을 입력해 주세요";
			String debugMessage = "the web parameter 'encryptedBytesWithPublicKey' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == parmPlainText) {
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
		
		
		String plainTextHex = HexUtil
				.getHexStringFromByteArray(parmPlainText.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));		
		//log.info("plainText hex[%s]", plainTextHex);
		// String sessionKeyHex =  new String(HexUtil.hexToByteArray(sessionKeyDoubleHex));
		//log.info("sessionKeyHex=[%s]", sessionKeyHex);
		byte encryptedBytesWithPublicKey[] = HexUtil.getByteArrayFromHexString(parmEncryptedBytesWithPublicKeyHex);
		
		byte decryptUsingPrivateKey[] = null;
		try {
			decryptUsingPrivateKey = webServerSessionkey.decryptUsingPrivateKey(encryptedBytesWithPublicKey);
		} catch (SymmetricException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("parmEncryptedBytesWithPublicKeyHex=[")
					.append(parmEncryptedBytesWithPublicKeyHex)					
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
		}			
		String decryptUsingPrivateKeyHex = HexUtil.getHexStringFromByteArray(decryptUsingPrivateKey);
		//log.info(String.format("decryptUsingPrivateKey=[%s]", decryptUsingPrivateKeyHex));
		
		boolean isSame = plainTextHex.equals(decryptUsingPrivateKeyHex);
		//log.info(String.format("resultMessage=[%s]", resultMessage));
		
		
		String decryptedPlainText = new String(decryptUsingPrivateKey);
		
		req.setAttribute("orignalPlainText", parmPlainText);
		req.setAttribute("decryptedPlainText", decryptedPlainText);
		req.setAttribute("isSame", String.valueOf(isSame));		
		printJspPage(req, res, "/menu/testcode/JSBNTest02.jsp");	
	}
	
}
