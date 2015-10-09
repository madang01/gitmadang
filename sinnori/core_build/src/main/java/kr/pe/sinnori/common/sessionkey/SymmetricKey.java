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

package kr.pe.sinnori.common.sessionkey;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import kr.pe.sinnori.common.exception.SinnoriUnsupportedEncodingException;
import kr.pe.sinnori.common.exception.SymmetricException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 대칭키와 씨앗값을 입력 파라미터로 받는 대칭키 편의 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SymmetricKey implements Serializable {
	private Logger log = LoggerFactory.getLogger(SymmetricKey.class);
	
	private static final long serialVersionUID = 5399674252065365470L;
	private String symmetricKeyAlgorithm = null;
	private byte[] symmetricKeyBytes = null;
	private byte[] ivBytes = null;
	private String errorMessage = null;
	private SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager
			.getInstance();
	/**
	 * 생성자
	 * 
	 * @param symmetricKeyAlgorithm
	 *            대칭키 알고리즘
	 * @param symmetricKeyBytes
	 *            대칭키
	 * @param ivBytes
	 *            IV
	 */
	public SymmetricKey(String symmetricKeyAlgorithm, byte[] symmetricKeyBytes,
			byte[] ivBytes) {
		this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
		this.symmetricKeyBytes = symmetricKeyBytes;
		this.ivBytes = ivBytes;

		// log.info(String.format("symmetricKeyAlgorithm=[%s], symmetricKeyBytes=[%s], ivBytes=[%s]", symmetricKeyAlgorithm, HexUtil.byteArrayAllToHex(symmetricKeyBytes), HexUtil.byteArrayAllToHex(ivBytes)));
	}

	/**
	 * 복사된 IV 를 반환한다.
	 * 
	 * @return IV
	 */
	public byte[] getIV() {
		return Arrays.copyOf(ivBytes, ivBytes.length);
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 평문에서 암호문을 만들어 반환한다.
	 * 
	 * @param plainTextBytes
	 *            평문
	 * @return 암호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] encryptBinary(byte[] plainTextBytes)
			throws IllegalArgumentException, SymmetricException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);
		byte[] retBytes = symmetricKeyManager.encryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes,
				ivBytes);
		// return Base64.encodeBytes(retBytes);
		return retBytes;
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 평문에서 암호문을 만들어 주어진 문자셋에 맞춘 문자열로 반환한다.
	 * 
	 * @param plainText
	 *            평문
	 * @param charsetName
	 *            암호문의 문자열의 문자셋
	 * @return 지정된 문자셋을 가지는 문자열로 변환된 암호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 * @throws SinnoriUnsupportedEncodingException 지원하지 않는 문자셋
	 */
	public byte[] encryptString(String plainText, String charsetName)
			throws IllegalArgumentException, SymmetricException, SinnoriUnsupportedEncodingException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);

		byte plainBytes[] = null;
		try {
			plainBytes = plainText.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			String errorMessage = String.format("bad charset name, check parameter charsetName[%s]",charsetName);
			log.warn(errorMessage);
			throw new SinnoriUnsupportedEncodingException(errorMessage);
		}

		byte[] retBytes = symmetricKeyManager.encryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, plainBytes, ivBytes);
		// return Base64.encodeBytes(retBytes);
		return retBytes;
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 평문에서 암호문을 만들어 반환한다.
	 * 
	 * @param plainText
	 *            평문
	 * @return 암호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] encryptString(String plainText)
			throws IllegalArgumentException, SymmetricException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);
		byte[] retBytes = symmetricKeyManager.encryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, plainText.getBytes(),
				ivBytes);
		// return Base64.encodeBytes(retBytes);

		// log.info(String.format("plainText=[%s], retBytes=[%s]", plainText, HexUtil.byteArrayAllToHex(retBytes)));

		return retBytes;
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 암호문을 복호화하여 복호문을 반환한다.
	 * 
	 * @param encryptedBytes
	 *            암호문
	 * @return 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] decryptBinary(byte[] encryptedBytes)
			throws IllegalArgumentException, SymmetricException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);
		byte[] retBytes = symmetricKeyManager.decryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes,
				ivBytes);
		return retBytes;
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 암호문을 복호화하여 복호문을 지정된 문자셋을 가지는 문자열로
	 * 반환한다.
	 * 
	 * @param encryptedBytes
	 *            암호문
	 * @param charsetName
	 *            복호문의 문자열이 가지는 문자셋
	 * @return 지정된 문자셋을 가지는 문자열로 변환된 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public String decryptString(byte[] encryptedBytes, String charsetName)
			throws IllegalArgumentException, SymmetricException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);

		byte[] retBytes = symmetricKeyManager.decryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes,
				ivBytes);

		String retStr = null;
		try {
			retStr = new String(retBytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(
					String.format(
							"charset name invalid, check charset name[%s]",
							charsetName));
		}

		return retStr;
	}

	/**
	 * 생성자에서 주어진 대칭키 알고리즘, 대칭키, 그리고 IV를 가지고 암호문을 복호화하여 문자열로 변환후 반환한다.
	 * 
	 * @param encryptedBytes
	 *            암호문
	 * @return 문자열인 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public String decryptString(byte[] encryptedBytes)
			throws IllegalArgumentException, SymmetricException {
		if (null != errorMessage)
			throw new RuntimeException(errorMessage);
		byte[] retBytes = symmetricKeyManager.decryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes,
				ivBytes);
		String retValue = new String(retBytes);

		// log.info(String.format("encryptedBytes=[%s], retValue=[%s]", HexUtil.byteArrayAllToHex(encryptedBytes), retValue));
		return retValue;
	}

	/**
	 * 평문을 대칭키로 암호화 하여 base64 인코딩한 문자열 반환한다.
	 * 
	 * @param plainText
	 *            평문
	 * @return base64 인코딩한 문자열
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public String encryptStringBase64(String plainText)
			throws IllegalArgumentException, SymmetricException {
		String retStr = null;
		try {
			retStr = encryptStringBase64(plainText, "UTF-8");
		} catch (SinnoriUnsupportedEncodingException e) {
			/** 문자셋 이름의 경우 사용자 지정이 아닌 값(UTF-8)으로 지정되어 있기에 이곳 로직으로 올 수 없다. */
			log.error(e.getMessage(), e);
			System.exit(1);
		}
		return retStr;
	}
	
	/**
	 * 평문을 대칭키로 암호화 하여 base64 인코딩한 문자열 반환한다.
	 * @param plainText 평문
	 * @param charsetName 문자셋
	 * @return 인코딩한 문자열
	 * @throws IllegalArgumentException  잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException 암호화 관련 에러가 있다면 발생
	 * @throws SinnoriUnsupportedEncodingException 파라미터 문자셋명이 잘못되었을 경우 던지는 예외
	 */
	public String encryptStringBase64(String plainText, String charsetName)
			throws IllegalArgumentException, SymmetricException, SinnoriUnsupportedEncodingException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);

		if (null == plainText)
			throw new IllegalArgumentException(
					"'PlainText' input variable value is null.");

		byte[] retBytes = null;
		try {
			retBytes = symmetricKeyManager.encryptDirect(
					symmetricKeyAlgorithm, symmetricKeyBytes, plainText.getBytes(charsetName),
					ivBytes);
		} catch (UnsupportedEncodingException e) {
			String errorMessage = String.format("bad charset name, check parameter charsetName[%s]",charsetName);
			log.warn(errorMessage);
			throw new SinnoriUnsupportedEncodingException(errorMessage);
		}
		// return Base64.encodeBytes(retBytes);

		if (null == retBytes)
			throw new RuntimeException(
					"SessionKeyManager::encryptDirect return value is null");

		String retValue = Base64.encodeBase64String(retBytes);

		// log.info(String.format("plainText=[%s], retBytes=[%s], retValue=[%s]", plainText, HexUtil.byteArrayAllToHex(retBytes), retValue));

		return retValue;
	}

	/**
	 * base64 로 인코딩된 대칭키 암호문을 입력받아 복화화하여 문자열로 반환한다. <br/>
	 * 주의점) 암호하기전 문자셋은 반듯이 현재 자바의 디폴트 문자셋과 일치해야한다.<br/>
	 * 만약 문자셋이 다를경우 오동작의 원인이된다.<br/>
	 * 참고) 신놀이 공식 문자셋은 UTF-8 이다.
	 * 
	 * @param cipherTextBase64
	 *            base64 로 인코딩된 대칭키 암호문
	 * @return 문자열로 변환된 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생 
	 */
	public String decryptStringBase64(String cipherTextBase64)
			throws IllegalArgumentException, SymmetricException {
		String retStr = null;
		try {
			retStr = decryptStringBase64(cipherTextBase64, "UTF-8");
		} catch (SinnoriUnsupportedEncodingException e) {
			/** 문자셋 이름의 경우 사용자 지정이 아닌 값(UTF-8)으로 지정되어 있기에 이곳 로직으로 올 수 없다. */
			log.error(e.getMessage(), e);
			System.exit(1);
		}
		return retStr;
	}
	
	/**
	 * base64 로 인코딩된 대칭키 암호문을 입력받아 복화화하여 문자열로 반환한다. <br/>
	 * 주의점) 암호하기전 문자셋은 반듯이 현재 자바의 디폴트 문자셋과 일치해야한다.<br/>
	 * 만약 문자셋이 다를경우 오동작의 원인이된다.<br/>
	 * 참고) 신놀이 공식 문자셋은 UTF-8 이다.
	 * 
	 * @param cipherTextBase64
	 *            base64 로 인코딩된 대칭키 암호문
	 * @return 문자열로 변환된 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 * @throws  SinnoriUnsupportedEncodingException 파라미터 문자셋명이 잘못되었을 경우 던지는 예외
	 */
	public String decryptStringBase64(String cipherTextBase64, String charsetName)
			throws IllegalArgumentException, SymmetricException, SinnoriUnsupportedEncodingException {
		if (null != errorMessage)
			throw new IllegalArgumentException(errorMessage);

		if (null == cipherTextBase64)
			throw new IllegalArgumentException(
					"'cipherTextBase64' input variable value is null.");

		if (!Base64.isBase64(cipherTextBase64)) {
			String errorMessage = String.format("not Base64 string[%s]",
					cipherTextBase64);
			throw new IllegalArgumentException(errorMessage);
		}
		byte cipherTextBytes[] = Base64.decodeBase64(cipherTextBase64);

		byte[] retBytes = symmetricKeyManager.decryptDirect(
				symmetricKeyAlgorithm, symmetricKeyBytes, cipherTextBytes,
				ivBytes);
		String retValue = null;
		try {
			retValue = new String(retBytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			String errorMessage = String.format("bad charset name, check parameter charsetName[%s]",charsetName);
			log.warn(errorMessage);
			throw new SinnoriUnsupportedEncodingException(errorMessage);
		}
		/**
		log.info(String.format(
				"cipherTextBase64=[%s], retValue=[%s], cipherTextBytes=[%s], retBytes=[%s]",
				cipherTextBase64, retValue,
				HexUtil.byteArrayAllToHex(cipherTextBytes),
				HexUtil.byteArrayAllToHex(retBytes)));
				*/

		return retValue;
	}

}
