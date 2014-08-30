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

import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 로그인 테스트, 아직 구현 못 했음
 * @author Jonghoon Won
 *
 */

@SuppressWarnings("serial")
public class JSBNTestSvl extends AbstractServlet {
	final String arryPageURL[] = {
			"/testcode/JSBNTest01.jsp", "/testcode/JSBNTest02.jsp"
	};

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String pageGubun = req.getParameter("pagegubun");
		
		
		log.info(String.format("pageGubun[%s]", pageGubun));
		
		
		
		String goPage=null;
		
		if (null == pageGubun || pageGubun.equals("step1")) {
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
			
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb(); 
			req.setAttribute("modulusHex", modulusHex);
			
			goPage = arryPageURL[0];
		} else {
			String encryptedBytesWithPublicKeyHex = req.getParameter("encryptedBytesWithPublicKey");
			String plainText = req.getParameter("plainText");
			
			
			String plainTextHex = HexUtil.byteArrayAllToHex(plainText.getBytes());
			
			log.info(String.format("encryptedBytesWithPublicKeyHex[%s]", encryptedBytesWithPublicKeyHex));
			log.info(String.format("plainText[%s]", plainText));
			log.info(String.format("plainText hex[%s]", plainTextHex));
			
			
			// String sessionKeyHex =  new String(HexUtil.hexToByteArray(sessionKeyDoubleHex));
			//log.info("sessionKeyHex=[%s]", sessionKeyHex);
			byte encryptedBytesWithPublicKey[] = HexUtil.hexToByteArray(encryptedBytesWithPublicKeyHex);
						
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();			
			byte decryptUsingPrivateKey[] = sessionKeyServerManger.decryptUsingPrivateKey(encryptedBytesWithPublicKey);			
			String decryptUsingPrivateKeyHex = HexUtil.byteArrayAllToHex(decryptUsingPrivateKey);
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
