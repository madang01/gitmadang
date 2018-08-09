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


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 해쉬(=메시지 다이제스트) 함수와 자바 결과 일치 테스트<br/>
 * 해쉬 함수 목록 (1) MD5 (2) SHA1 (3) SHA-256 (4) SHA-512 가 있다.
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class CryptoJSMDTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {		
		
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
	
	private void firstPage(HttpServletRequest req, HttpServletResponse res) {
		printJspPage(req, res, "/menu/testcode/CryptoJSMDTest01.jsp");	
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) {
		String paramAlgorithm = req.getParameter("algorithm");		
		String paramJavascriptMDHex = req.getParameter("javascriptMD");
		String paramPlainText = req.getParameter("plainText");
		
		log.info("paramAlgorithm[{}]", paramAlgorithm);
		log.info("paramJavascriptMDHex[{}]", paramJavascriptMDHex);
		log.info("paramPlainText[{}]", paramPlainText);
		
		if (null == paramAlgorithm) {
			String errorMessage = "알고리즘을 입력해 주세요";
			String debugMessage = "the web parameter 'algorithm' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramJavascriptMDHex) {
			String errorMessage = "알고리즘을 입력해 주세요";
			String debugMessage = "the web parameter 'javascriptMD' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPlainText) {
			String errorMessage = "평문을 입력해 주세요";
			String debugMessage = "the web parameter 'plainText' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		byte[] javascriptMD = HexUtil.getByteArrayFromHexString(paramJavascriptMDHex);
		
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(paramAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a MessageDigest class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("the web parameter 'algorithm'[")
					.append(paramAlgorithm)
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		md.update(paramPlainText.replaceAll("\r\n", "\n").getBytes());
		
		byte serverMD[] =  md.digest();
		
		// log.info(String.format("server digestMessage[%s]", HexUtil.getHexStringFromByteArray(serverMD)));
		
		String isSame = String.valueOf(Arrays.equals(javascriptMD, serverMD));
		
		req.setAttribute("plainText", paramPlainText);
		req.setAttribute("javascriptMDHex", paramJavascriptMDHex);
		req.setAttribute("serverMDHex", HexUtil.getHexStringFromByteArray(serverMD));
		req.setAttribute("isSame", isSame);
		
		
		printJspPage(req, res, "/menu/testcode/CryptoJSMDTest02.jsp");
	}
}
