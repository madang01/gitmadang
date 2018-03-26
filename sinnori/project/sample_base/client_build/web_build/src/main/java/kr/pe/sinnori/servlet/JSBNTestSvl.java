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

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.util.HexUtil;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;



@SuppressWarnings("serial")
public class JSBNTestSvl extends AbstractServlet {
	
	final String arryPageURL[] = {
			"/menu/testcode/JSBNTest01.jsp", "/menu/testcode/JSBNTest02.jsp"
	};

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.TEST_EXAMPLE);
		
		String pageGubun = req.getParameter("pagegubun");
		
		
		log.info(String.format("pageGubun[%s]", pageGubun));
		
		
		
		String goPage=null;
		
		if (null == pageGubun || pageGubun.equals("step1")) {
			ServerSessionkeyIF serverSessionkey  = null;
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				serverSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			String modulusHex = serverSessionkey.getModulusHexStrForWeb(); 
			req.setAttribute("modulusHex", modulusHex);
			
			goPage = arryPageURL[0];
		} else {
			String encryptedBytesWithPublicKeyHex = req.getParameter("encryptedBytesWithPublicKey");
			String plainText = req.getParameter("plainText");
			
			
			String plainTextHex = HexUtil.getHexStringFromByteArray(plainText.getBytes());
			
			log.info(String.format("encryptedBytesWithPublicKeyHex[%s]", encryptedBytesWithPublicKeyHex));
			log.info(String.format("plainText[%s]", plainText));
			log.info(String.format("plainText hex[%s]", plainTextHex));
			
			
			// String sessionKeyHex =  new String(HexUtil.hexToByteArray(sessionKeyDoubleHex));
			//log.info("sessionKeyHex=[%s]", sessionKeyHex);
			byte encryptedBytesWithPublicKey[] = HexUtil.getByteArrayFromHexString(encryptedBytesWithPublicKeyHex);
						
			ServerSessionkeyIF serverSessionkey  = null;
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				serverSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
				
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			byte decryptUsingPrivateKey[] = serverSessionkey.decryptUsingPrivateKey(encryptedBytesWithPublicKey);			
			String decryptUsingPrivateKeyHex = HexUtil.getHexStringFromByteArray(decryptUsingPrivateKey);
			log.info(String.format("decryptUsingPrivateKey=[%s]", decryptUsingPrivateKeyHex));
			
			String resultMessage = String.format("%s", plainTextHex.equals(decryptUsingPrivateKeyHex));
			log.info(String.format("resultMessage=[%s]", resultMessage));
			
			goPage = arryPageURL[1];
			
			String decryptedPlainText = new String(decryptUsingPrivateKey);
			
			req.setAttribute("orignalPlainText", plainText);
			req.setAttribute("decryptedPlainText", decryptedPlainText);
			req.setAttribute("resultMessage", resultMessage);
		}
		
		log.info(String.format("goPage[%s]", goPage));
		
		printJspPage(req, res, goPage);	
	}
	
}
