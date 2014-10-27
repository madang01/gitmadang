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


import java.security.MessageDigest;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.util.HexUtil;
import kr.pe.sinnori.common.weblib.AbstractServlet;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 해쉬(=메시지 다이제스트) 함수와 자바 결과 일치 테스트<br/>
 * 해쉬 함수 목록 (1) MD5 (2) SHA1 (3) SHA-256 (4) SHA-512 가 있다.
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class CryptoJSMDTestSvl extends AbstractServlet {
	final String arryPageURL[] = {
			"/testcode/CryptoJSMDTest01.jsp", "/testcode/CryptoJSMDTest02.jsp"
	};

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String pageGubun = req.getParameter("pagegubun");
		log.info(String.format("pageGubun[%s]", pageGubun));		
		
		
		String goPage=null;
		
		if (null == pageGubun || pageGubun.equals("step1")) {
			goPage = arryPageURL[0];
		} else {
			String algorithm = req.getParameter("algorithm");
			
			String javascriptMDHex = req.getParameter("javascriptMD");
			
			String plainText = req.getParameter("plainText");
			
			
			log.info(String.format("algorithm[%s]", algorithm));
			log.info(String.format("javascrpt digestMessage[%s]", javascriptMDHex));
			log.info(String.format("plainText[%s]", plainText));
			
			byte[] javascriptMD = HexUtil.getByteArrayFromHexString(javascriptMDHex);
			
			
			MessageDigest md = MessageDigest.getInstance(algorithm);
			
			md.update(plainText.getBytes());
			
			byte serverMD[] =  md.digest();
			
			log.info(String.format("server digestMessage[%s]", HexUtil.getHexStringFromByteArray(serverMD)));
			
			String resultMessage = String.format("%s", Arrays.equals(javascriptMD, serverMD));
			
			goPage = arryPageURL[1];
			
			req.setAttribute("plainText", plainText);
			req.setAttribute("javascriptMDHex", javascriptMDHex);
			req.setAttribute("serverMDHex", HexUtil.getHexStringFromByteArray(serverMD));
			req.setAttribute("resultMessage", resultMessage);
		}
		
		log.info(String.format("goPage[%s]", goPage));
		
		printJspPage(req, res, goPage);	
	}
}
