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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 대칭키 관리자. 신놀이가 지원하는 대칭키에 대한 관리를 위한 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public final class SymmetricKeyManager implements CommonRootIF {
	private Map<String, String> symmetricKeyTransformationHash = null;
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SymmetricKeyManagerHolder {
		static final SymmetricKeyManager singleton = new SymmetricKeyManager();
	}
	
	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SymmetricKeyManager getInstance() {
		return SymmetricKeyManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private SymmetricKeyManager() {
		/**
		 * 자바스크립트 공개키 암호화 모듈 JSBN 와 자바 JCA 공통 지원 알고리즘만으로 구성된다. 웹의 특성상 대칭키 생성은 매
		 * 페이지 요청시 마다 하는것이 좋겠지만, hisotry back 기능이 제대로 동작 못하게 되는 문제가 있다. 따라서 이
		 * 문제를 해결하고자 CBC 로 고정한다. 각각의 페이지는 고유 iv를 갖게해서 history back에서도 안전하게 화면을
		 * 볼수있도록한다.
		 * 
		 * 자바 JCA 참고 url :
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/
		 * security/StandardNames.html#alg 인용 : KeyGenerator Algorithms
		 * 
		 * The following algorithm names can be specified when requesting an
		 * instance of KeyGenerator.
		 * 
		 * Alg. Name Description AES Key generator for use with the AES
		 * algorithm. ARCFOUR Key generator for use with the ARCFOUR (RC4)
		 * algorithm. Blowfish Key generator for use with the Blowfish
		 * algorithm. DES Key generator for use with the DES algorithm. DESede
		 * Key generator for use with the DESede (triple-DES) algorithm. HmacMD5
		 * Key generator for use with the HmacMD5 algorithm. HmacSHA1 HmacSHA256
		 * HmacSHA384 HmacSHA512 Keys generator for use with the various flavors
		 * of the HmacSHA algorithms. RC2 Key generator for use with the RC2
		 * algorithm.
		 */

		symmetricKeyTransformationHash = new HashMap<String, String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
	}

	

	

	/**
	 * 대칭키 알고리즘과 키를 받아 암호문에서 복호문을 만들어 반환한다.
	 * 
	 * @param symmetricKeyAlgorithm
	 *            대칭키 알고리즘명
	 * @param symmetricKeyBytes
	 *            대칭키 알고리즘 대칭키
	 * @param encryptedBytes
	 *            대칭키 알고리즘으로 만든 암호문
	 * @param ivBytes
	 *            IV
	 * @return 복호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] decryptDirect(String symmetricKeyAlgorithm,
			byte[] symmetricKeyBytes, byte[] encryptedBytes, byte[] ivBytes)
			throws IllegalArgumentException, SymmetricException {

		if (null == symmetricKeyAlgorithm)
			log.info("symmetricKeyAlgorithm is null");
		
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("symmetricKeyBytes is null");
		}

		if (null == encryptedBytes) {
			throw new IllegalArgumentException("encryptedBytes is null");
		}

		if (null == ivBytes)
			log.info("ivBytes is null");

		String transformation = symmetricKeyTransformationHash
				.get(symmetricKeyAlgorithm);
		
		if (null == transformation) {
			String errorMessage = String.format("지원하지 않는 파라미터 symmetricKeyAlgorithm 값[%s]", symmetricKeyAlgorithm);
			log.warn(errorMessage, new Throwable("잘못된 파라미터 값을 넣은 소스 추적용 예외"));
			throw new IllegalArgumentException(errorMessage);			
		}
		

		Cipher symmetricKeyCipher = null;
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("%s Cipher NoSuchAlgorithmException",
							symmetricKeyAlgorithm);
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String.format(
					"%s Cipher NoSuchPaddingException, errormessage=%s", 
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
		log.info("Successful symmetric key cipher class creation");

		SecretKeySpec symmetricKey = new SecretKeySpec(symmetricKeyBytes,
				symmetricKeyAlgorithm);
		if (null == ivBytes) {
			try {
				symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey);
			} catch (InvalidKeyException e) {
				String errorMessage = String.format(
						"%s Cipher InvalidKeyException, errormessage=%s", 
						symmetricKeyAlgorithm, e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			}

			// log.info("Cipher.init");
		} else {
			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			try {
				symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
			} catch (InvalidKeyException e) {
				String errorMessage = String.format(
						"%s Cipher with IV InvalidKeyException, errormessage=%s",
						symmetricKeyAlgorithm, e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			} catch (InvalidAlgorithmParameterException e) {
				String errorMessage = String
						.format("%s Cipher with IV[%s] InvalidAlgorithmParameterException, errormessage=%s",
								symmetricKeyAlgorithm,
								HexUtil.getHexStringFromByteArray(ivBytes),
								e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			}

			// log.info("Cipher.init with IV");
		}

		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String.format(
					"%s Cipher IllegalBlockSizeException, errormessage=%s",
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String.format(
					"%s Cipher BadPaddingException, errormessage=%s", 
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("decryptedBytes[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytes));

		return decryptedBytes;
	}

	/**
	 * 대칭키 알고리즘, 대칭키를 가지고 평문을 암호화하여 암호문을 반환한다.
	 * 
	 * @param symmetricKeyAlgorithm
	 *            대칭키 알고리즘명
	 * @param symmetricKeyBytes
	 *            대칭키
	 * @param plainTextBytes
	 *            평문
	 * @param ivBytes
	 *            IV
	 * @return 암호문
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터가 있을때 발생
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] encryptDirect(String symmetricKeyAlgorithm,
			byte[] symmetricKeyBytes, byte[] plainTextBytes, byte[] ivBytes)
			throws IllegalArgumentException, SymmetricException {

		if (null == symmetricKeyAlgorithm)
			log.info("symmetricKeyAlgorithm is null");
		
		if (null == symmetricKeyBytes) {
			throw new IllegalArgumentException("symmetricKeyBytes is null");
		}

		if (null == plainTextBytes) {
			throw new IllegalArgumentException("plainTextBytes is null");
		}

		if (null == ivBytes)
			log.info("ivBytes is null");

		String transformation = symmetricKeyTransformationHash
				.get(symmetricKeyAlgorithm);
		
		if (null == transformation) {
			String errorMessage = String.format("지원하지 않는 파라미터 symmetricKeyAlgorithm 값[%s]", symmetricKeyAlgorithm);
			log.warn(errorMessage, new Throwable("잘못된 파라미터 값을 넣은 소스 추적용 예외"));
			throw new IllegalArgumentException(errorMessage);			
		}

		Cipher symmetricKeyCipher = null;
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("%s Cipher NoSuchAlgorithmException",
							symmetricKeyAlgorithm);
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String.format(
					"%s Cipher NoSuchPaddingException, errormessage=%s", 
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// FIXME!
		log.info("Successful symmetric key cipher class creation");

		SecretKeySpec symmetricKey = new SecretKeySpec(symmetricKeyBytes,
				symmetricKeyAlgorithm);

		if (null == ivBytes) {
			try {
				symmetricKeyCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
			} catch (InvalidKeyException e) {
				String errorMessage = String.format(
						"%s Cipher InvalidKeyException, errormessage=%s", 
						symmetricKeyAlgorithm, e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			}

			// log.info("Cipher.init");
		} else {
			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			try {
				symmetricKeyCipher.init(Cipher.ENCRYPT_MODE, symmetricKey, iv);
			} catch (InvalidKeyException e) {
				String errorMessage = String.format(
						"%s Cipher with IV InvalidKeyException, errormessage=%s",
						symmetricKeyAlgorithm, e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			} catch (InvalidAlgorithmParameterException e) {
				String errorMessage = String
						.format("%s Cipher with IV[%s] InvalidAlgorithmParameterException, errormessage=%s",
								symmetricKeyAlgorithm,
								HexUtil.getHexStringFromByteArray(ivBytes),
								e.getMessage());
				log.warn(errorMessage, e);
				throw new SymmetricException(errorMessage);
			}
			// log.info("Cipher.init with IV");
		}

		byte[] encryptedBytes;
		try {
			encryptedBytes = symmetricKeyCipher.doFinal(plainTextBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String.format(
					"%s Cipher IllegalBlockSizeException, errormessage=%s",
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String.format(
					"%s Cipher BadPaddingException, errormessage=%s", 
					symmetricKeyAlgorithm, e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("encryptedBytes[%s]",
		// HexUtil.byteArrayAllToHex(encryptedBytes));

		return encryptedBytes;
	}
}
