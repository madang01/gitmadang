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
import java.util.Arrays;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 대칭키 함수와 자바 결과 일치 테스트<br/>
 * 대칭키 함수 목록 (1) AES (2) DES (3)  DESede(=Triple DES)
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class CryptoJSSKTestSvl extends AbstractServlet {
	final String arryPageURL[] = {
			"/testcode/CryptoJSSKTest01.jsp", "/testcode/CryptoJSSKTest02.jsp"
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
			
			String privateKeyHex = req.getParameter("privateKey");
			String ivHex = req.getParameter("iv");
			String plainText = req.getParameter("plainText");
			String encryptedBytesHex = req.getParameter("encryptedBytes");
			
			
			
			log.info(String.format("privateKey[%s]", privateKeyHex));
			log.info(String.format("iv[%s]", ivHex));
			log.info(String.format("plainText[%s]", plainText));
			log.info(String.format("encryptedBytes[%s]", encryptedBytesHex));
			
			Hashtable<String,String> symmetricKeyTransformationHash = null;
			symmetricKeyTransformationHash = new Hashtable<String,String>();
			symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");		
			symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
			symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
			
			String transformation = symmetricKeyTransformationHash.get(algorithm);
			
			if (null == transformation) {
				throw new RuntimeException(String.format("don't support the algorithm[%s]", algorithm));
			}
			
			byte[] privateKeyBytes = HexUtil.hexToByteArray(privateKeyHex);
			byte[] ivBytes = HexUtil.hexToByteArray(ivHex);
			byte[] encryptedBytes = HexUtil.hexToByteArray(encryptedBytesHex);
			
			Cipher symmetricKeyCipher = null;		
			try {
				symmetricKeyCipher = Cipher.getInstance(transformation);
			} catch (NoSuchAlgorithmException e) {
				log.warn("NoSuchAlgorithmException", e);
				throw new RuntimeException("NoSuchAlgorithmException");
			} catch (NoSuchPaddingException e) {
				log.warn("NoSuchPaddingException", e);
				throw new RuntimeException("NoSuchPaddingException");
			}
			
			SecretKeySpec symmetricKey = new SecretKeySpec(privateKeyBytes, algorithm);
			
			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			try {
				symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
			} catch (InvalidKeyException e1) {
				log.warn("InvalidKeyException", e1);
				throw new RuntimeException("InvalidKeyException");
			} catch (InvalidAlgorithmParameterException e1) {
				log.warn("InvalidAlgorithmParameterException", e1);
				throw new RuntimeException("InvalidAlgorithmParameterException");
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
			
			String decryptedBytesHex = HexUtil.byteArrayAllToHex(decryptedBytes);
			log.info(String.format("decryptedBytes[%s]", decryptedBytesHex));
			
			String resultMessage = String.format("%s", Arrays.equals(plainText.getBytes(), decryptedBytes));			
			goPage = arryPageURL[1];
			
			req.setAttribute("plainText", plainText);
			req.setAttribute("algorithm", algorithm);
			req.setAttribute("privateKey", privateKeyHex);
			req.setAttribute("iv", ivHex);
			req.setAttribute("encryptedBytes", encryptedBytesHex);
			req.setAttribute("decryptedBytes", decryptedBytesHex);
			req.setAttribute("decryptedPlainText", new String(decryptedBytes));
			req.setAttribute("resultMessage", resultMessage);
		}
		
		log.info(String.format("goPage[%s]", goPage));
		
		printJspPage(req, res, goPage);	
		
	}

}
