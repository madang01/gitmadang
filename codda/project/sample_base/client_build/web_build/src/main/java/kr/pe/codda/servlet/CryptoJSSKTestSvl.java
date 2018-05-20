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


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 대칭키 함수와 자바 결과 일치 테스트<br/>
 * 대칭키 함수 목록 (1) AES (2) DES (3)  DESede(=Triple DES)
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class CryptoJSSKTestSvl extends AbstractServlet {
	final String arryPageURL[] = {
			"/menu/testcode/CryptoJSSKTest01.jsp", "/menu/testcode/CryptoJSSKTest02.jsp"
	};

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				SiteTopMenuType.TEST_EXAMPLE);
		
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
		printJspPage(req, res, "/menu/testcode/CryptoJSSKTest01.jsp");	
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) {
		String parmAlgorithm = req.getParameter("algorithm");		
		String parmPrivateKeyHex = req.getParameter("privateKey");
		String parmIVHex = req.getParameter("iv");
		String parmPlainText = req.getParameter("plainText");
		String parmEncryptedBytesHex = req.getParameter("encryptedBytes");
		
		log.info("parmAlgorithm=[{}]", parmAlgorithm);
		log.info("parmPrivateKeyHex=[{}]", parmPrivateKeyHex);
		log.info("parmIVHex=[{}]", parmIVHex);
		log.info("parmPlainText=[{}]", parmPlainText);
		log.info("parmEncryptedBytesHex=[{}]", parmEncryptedBytesHex);
		
		Hashtable<String,String> symmetricKeyTransformationHash = null;
		symmetricKeyTransformationHash = new Hashtable<String,String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");		
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
		
		String transformation = symmetricKeyTransformationHash.get(parmAlgorithm);
		
		if (null == transformation) {
			throw new RuntimeException(String.format("don't support the algorithm[%s]", parmAlgorithm));
		}
		
		byte[] privateKeyBytes = HexUtil.getByteArrayFromHexString(parmPrivateKeyHex);
		byte[] ivBytes = HexUtil.getByteArrayFromHexString(parmIVHex);
		byte[] encryptedBytes = HexUtil.getByteArrayFromHexString(parmEncryptedBytesHex);
		
		Cipher symmetricKeyCipher = null;		
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (NoSuchPaddingException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		SecretKeySpec symmetricKey = new SecretKeySpec(privateKeyBytes, parmAlgorithm);
		
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
		} catch (InvalidKeyException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("parmAlgorithm=[")
					.append(parmAlgorithm)
					.append("], parmPrivateKeyHex=")
					.append(parmPrivateKeyHex)
					.append("], parmIVHex=[")
					.append(parmIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("parmAlgorithm=[")
					.append(parmAlgorithm)
					.append("], parmPrivateKeyHex=")
					.append(parmPrivateKeyHex)
					.append("], parmIVHex=[")
					.append(parmIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		log.info("Cipher.init with IV");
		
		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			log.warn("IllegalBlockSizeException", e);
			throw new RuntimeException("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			log.warn("BadPaddingException", e);
			throw new RuntimeException("BadPaddingException");
		}
		
		String plainTextHex = HexUtil.getHexStringFromByteArray(parmPlainText.getBytes());
		String decryptedBytesHex = HexUtil.getHexStringFromByteArray(decryptedBytes);
		log.info("plainTextHex[{}], decryptedBytes[{}]", plainTextHex, decryptedBytesHex);
		
		
		String decryptedPlainText = new String(decryptedBytes);
		String isSame = String.valueOf(decryptedPlainText.equals(parmPlainText));			
		
		
		req.setAttribute("plainText", parmPlainText);
		req.setAttribute("algorithm", parmAlgorithm);
		req.setAttribute("privateKey", parmPrivateKeyHex);
		req.setAttribute("iv", parmIVHex);
		req.setAttribute("encryptedBytesHex", parmEncryptedBytesHex);
		req.setAttribute("plainTextHex", plainTextHex);
		req.setAttribute("decryptedBytesHex", decryptedBytesHex);
		req.setAttribute("decryptedPlainText", decryptedPlainText);
		req.setAttribute("isSame", isSame);
		printJspPage(req, res, "/menu/testcode/CryptoJSSKTest02.jsp");
	}
}
