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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.util.HexUtil;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서버 관점의 세션키 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ServerSessionKeyManager {
	private Logger log = LoggerFactory.getLogger(ServerSessionKeyManager.class);
	
	// private final Object monitor = new Object();
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;
	private RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;
	
	
	/** Warning! the variable symmetricKeyAlgorithm must not create getXXX method because it is Sinnori configuration variable */
	private String symmetricKeyAlgorithm = null;
	/** Warning! the variable symmetricKeyEncoding must not create getXXX method because it is Sinnori configuration variable */
	private CommonType.SYMMETRIC_KEY_ENCODING_TYPE symmetricKeyEncoding = null;
	/** Warning! the variable rsaKeySize must not create getXXX method because it is Sinnori configuration variable */
	private int rsaKeySize;

	// private SymmetricKeyAlgorithmType symmetricKeyAlgorithmType;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SessionKeyManagerHolder {
		static final ServerSessionKeyManager singleton = new ServerSessionKeyManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ServerSessionKeyManager getInstance() {
		return SessionKeyManagerHolder.singleton;
	}

	/**
	 * <pre>
	 * 공개키 쌍을 환경 변수에서 지정하는 경로에 있는 특정 파일을 읽어 만든다.
	 * 외부 전역 변수 개인키와 공개키를 초기화 한다.
	 * (1) private PrivateKey privateKey : 개인키
	 * (2) private PublicKey publicKey : 공개키
	 * </pre>
	 */
	private void makeRSAKeyUsingFile(File rsaKeyPairPath) {
		File rsaPrivateKeyFile = new File(rsaKeyPairPath.getAbsolutePath()
				+ java.io.File.separator + CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		if (!rsaPrivateKeyFile.exists()) {
			String errorMessage = String.format(
					"sinnori.privatekey file not exist. fullpath=[%s]\n",
					rsaPrivateKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		if (!rsaPrivateKeyFile.canRead()) {
			String errorMessage = String.format(
					"can not read sinnori.privatekey file. fullpath=[%s]\n",
					rsaPrivateKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		FileInputStream rsaPrivateKeyFIS = null;
		byte privateKeyBytes[] = null;

		try {
			rsaPrivateKeyFIS = new FileInputStream(rsaPrivateKeyFile);

			long size = rsaPrivateKeyFile.length();
			if (size > CommonStaticFinalVars.MAX_KEY_FILE_SIZE
					|| size > Integer.MAX_VALUE) {
				String errorMessage = String.format(
						"check rsa private key file size[%d]", size);
				log.error(errorMessage);
				System.exit(1);
			}
			privateKeyBytes = new byte[(int) size];
			rsaPrivateKeyFIS.read(privateKeyBytes);
		} catch (FileNotFoundException e) {
			String errorMessage = String
					.format("the RSA private key File[%s] is not found", rsaPrivateKeyFile.getAbsolutePath());
			log.error(errorMessage, e);
			System.exit(1);
		} catch (IOException e) {
			String errorMessage = String.format("the RSA private key File[%s] IOException, errmessage=%s", 
					rsaPrivateKeyFile.getAbsolutePath(), e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		} finally {
			try {
				if (null != rsaPrivateKeyFIS)
					rsaPrivateKeyFIS.close();
			} catch (Exception e1) {
			}
		}
		
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				privateKeyBytes);
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		try {
			privateKey = rsaKeyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String
					.format("fail to get the RSA private key(=PKCS8EncodedKeySpec)[%s]::errmessage=%s", 
							HexUtil.getHexStringFromByteArray(privateKeyBytes), 
							e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		File rsaPublicKeyFile = new File(rsaKeyPairPath.getAbsolutePath()
				+ java.io.File.separator + CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		if (!rsaPublicKeyFile.exists()) {
			String errorMessage = String.format(
					"sinnori.publickey file not exist. fullpath=[%s]\n",
					rsaPublicKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		if (!rsaPublicKeyFile.canRead()) {
			String errorMessage = String.format(
					"can not read sinnori.publickey file. fullpath=[%s]\n",
					rsaPublicKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		FileInputStream rsaPublicKeyFIS = null;
		byte publicKeyBytes[] = null;
		try {
			rsaPublicKeyFIS = new FileInputStream(rsaPublicKeyFile);

			long size = rsaPublicKeyFile.length();
			if (size > CommonStaticFinalVars.MAX_KEY_FILE_SIZE
					|| size > Integer.MAX_VALUE) {
				String errorMessage = String.format(
						"check rsa public key file size[%d]", size);
				log.error(errorMessage);
				System.exit(1);
			}
			publicKeyBytes = new byte[(int) size];
			rsaPublicKeyFIS.read(publicKeyBytes);
		} catch (FileNotFoundException e) {
			String errorMessage = String
					.format("the RSA public key file[%s] is not found", 
							rsaPublicKeyFile.getAbsolutePath());
			log.error(errorMessage, e);
			System.exit(1);
		} catch (IOException e) {
			String errorMessage = String.format("the RSA public key file[%s] IOException, errormessage=%s",
					rsaPublicKeyFile.getAbsolutePath(), e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		} finally {
			try {
				if (null != rsaPublicKeyFIS)
					rsaPublicKeyFIS.close();
			} catch (Exception e1) {
			}
		}

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyBytes);

		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String
					.format("fail to get the RSA public key(=X509EncodedKeySpec)[%s], errormessage=%s",
							HexUtil.getHexStringFromByteArray(publicKeyBytes), 
							e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
		} catch (InvalidKeySpecException e) {
			String errorMessage = 
					String.format("fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", 
					e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
	}
	
	/**
	 * <pre>
	 * 공개키 쌍을 파일 의존하지 않고 자체 라이브러리를 이용하여 만든다.
	 * 외부 전역 변수 개인키와 공개키를 초기화 한다.
	 * (1) private PrivateKey privateKey : 개인키
	 * (2) private PublicKey publicKey : 공개키
	 * </pre>
	 * 
	 */
	private void makeRSAKeyUsingAPI() {
		KeyPairGenerator rsaKeyPairGenerator = null;		
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("fail to get the RSA KeyPairGenerator, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}	
		
		
		rsaKeyPairGenerator.initialize(rsaKeySize);
		KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
		publicKey =  keyPair.getPublic();
		privateKey =  keyPair.getPrivate();
		
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
		} catch (InvalidKeySpecException e) {
			String errorMessage = 
					String.format("fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", 
					e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private ServerSessionKeyManager() {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();	
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeyEncoding = commonPart.getSymmetricKeyEncodingOfSessionKey();
		rsaKeySize = commonPart.getRsaKeySizeOfSessionKey();;
		
		
		CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY rsaKeyPairSource = commonPart.getRsaKeypairSourceOfSessionKey();
		if (rsaKeyPairSource.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.File)) {
			File rsaKeyPairPath = null;
			try {
				rsaKeyPairPath = commonPart.getRsaKeyPairPathOfSessionKey();
				makeRSAKeyUsingFile(rsaKeyPairPath);
			} catch (SinnoriConfigurationException e) {
				log.warn("fail to get RSAKeypairPath", e);
				log.info("The rsa key pair path[{}] doesn't exist so the rsa key pair will be created using API");
				makeRSAKeyUsingAPI();
			}
			
		} else {
			makeRSAKeyUsingAPI();
		}
	}

	/**
	 * 자바스크립트 공개키 모듈 jsbn 에서는 modulus 값이 공개키값으로 사용된다. 참고) 자바에서는 공개키 교환은 X509 이다.
	 * 
	 * @return 자바스크립트 공개키 모듈 jsbn 에서 사용할 modulus 값
	 */
	public String getModulusHexStrForWeb() {

		
		return HexUtil.getHexStringFromByteArray(rsaPrivateCrtKeySpec.getModulus().toByteArray());
	}

	/**
	 * 자바쪽에서의 공개키는 X509로 교환된다. 참고) 자바스크립트 공개키 모듈 jsbn에서는 modulus 값이 사용된다.
	 * 
	 * @return X509로 인코딩된 공개키
	 */
	public byte[] getPublicKeyBytes() {

		return publicKey.getEncoded();
	}

	/**
	 * 서버 관점의 섹셕키의 대칭키를 다루는 편의 클래스를 반환한다.
	 * 
	 * @param sessionKeyBase64
	 *            base64로 인코딩된 세션키
	 * @param ivBase64
	 *            base64로 인코딩된 IV
	 * @return 서버 관점의 섹셕키의 대칭키를 다루는 편의 클래스
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SymmetricException 암호화 관련 에러가 있다면 발생
	 */
	public SymmetricKey getSymmetricKey(String sessionKeyBase64, String ivBase64)
			throws IllegalArgumentException, SymmetricException {
		return getSymmetricKey(this.symmetricKeyAlgorithm, symmetricKeyEncoding,
				sessionKeyBase64, ivBase64);
	}

	/**
	 * 세션키로 부터 추출한 대칭키를 갖는 대칭키 클래스를 반환한다.
	 * 
	 * @param symmetricKeyAlgorithm
	 *            대칭키 알고리즘
	 * @param symmetricKeyEncoding
	 *            대칭키 인코딩 방법
	 * @param sessionKeyBase64
	 *            base64 로 인코딩된 세션키
	 * @param ivBase64
	 *            base64로 인코딩된 IV
	 * @return 세션키로 부터 추출된 대칭키 클래스
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SymmetricException 암호화 관련 에러가 있다면 발생
	 */
	public SymmetricKey getSymmetricKey(String symmetricKeyAlgorithm,
			CommonType.SYMMETRIC_KEY_ENCODING_TYPE symmetricKeyEncoding,
			String sessionKeyBase64, String ivBase64)
			throws IllegalArgumentException, SymmetricException {

		if (null == symmetricKeyAlgorithm) {
			throw new IllegalArgumentException("parm symmetricKeyAlgorithm is null");
		}

		if (null == sessionKeyBase64) {
			throw new IllegalArgumentException("parm sessionKeyBase64 is null");
		}

		if (null == ivBase64) {
			String errorMessage = "parm ivBase64 is null";
			
			throw new IllegalArgumentException(errorMessage);
		}

		if (!Base64.isBase64(sessionKeyBase64)) {
			String errorMessage = String.format(
					"parm sessionKeyBase64[%s] not Base64 string", sessionKeyBase64);
			throw new IllegalArgumentException(errorMessage);
		}

		if (!Base64.isBase64(ivBase64)) {
			String errorMessage = String.format("parm ivBase64[%s] not Base64 string",
					ivBase64);
			throw new IllegalArgumentException(errorMessage);
		}

		byte[] sessionKeyBytes = Base64.decodeBase64(sessionKeyBase64);
		
		if (null == sessionKeyBytes) {
			String errorMessage = "파라미터 Base64 인코딩된 세션키를 Base64 디코딩한 결과 값이 null 입니다.";
			throw new IllegalArgumentException(errorMessage);
		}

		byte[] ivBytes = Base64.decodeBase64(ivBase64);
		
		if (null == ivBytes) {
			String errorMessage = "파라미터 Base64 인코딩된 IV 값을 Base64 디코딩한 결과 값이 null 입니다.";
			throw new IllegalArgumentException(errorMessage);
		}

		byte[] symmetricKeyBytes = null;

		byte[] drcryptedBytes = decryptUsingPrivateKey(sessionKeyBytes);
		
		if (null == drcryptedBytes) {
			String errorMessage = "파라미터 Base64 인코딩된 세션키를 서버 비밀키로 풀어 얻은 값이 null 입니다.";
			throw new SymmetricException(errorMessage);
		}

		switch (symmetricKeyEncoding) {
			case NONE: {
				symmetricKeyBytes = drcryptedBytes;
				break;
			}
			case BASE64: {
				// log.debug(String.format("isBase64[%s]", Base64.isBase64(drcryptedBytes)));
				
				if (!Base64.isBase64(drcryptedBytes)) {
					String errorMessage = String.format(
							"In BASE64 mode, 파라미터 Base64 인코딩된 세션키를 서버 비밀키로 풀어 얻은 값[%s] not base64", HexUtil.getHexStringFromByteArray(drcryptedBytes));
					
					throw new SymmetricException(errorMessage);
				}

				/**
				 * NONE   mode : 파라미터 세션키 = binary private key -> binary session key -> base64 session key
				 * BASE64 mode : 파라미터 세션키 = binary private key -> base64 private key -> binary session key -> base64 session key
				 */
				symmetricKeyBytes = Base64.decodeBase64(drcryptedBytes);
				break;
			}
		}

		SymmetricKey symmetricKey = new SymmetricKey(symmetricKeyAlgorithm,
				symmetricKeyBytes, ivBytes);

		return symmetricKey;
	}

	/**
	 * 공개키로 평문을 암호하여 암호문을 반환한다. 세션키 만들때 필요함 기능이다.
	 * 
	 * @param plainTextBytes
	 *            평문
	 * @return 공개키로 암호화된 암호문
	 * @throws SymmetricException
	 *             암호화 관련 에러가 있다면 발생
	 */
	public byte[] encryptUsingPublicKey(byte[] plainTextBytes)
			throws SymmetricException {
		Cipher rsaEncModeCipher = null;

		try {
			rsaEncModeCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// rsaDecModeCipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("Cipher.getInstance NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String
					.format("Cipher.getInstance NoSuchPaddingException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaEncModeCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
			String errorMessage = String
					.format("RSA Cipher InvalidKeyException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		byte[] encryptedBytesUsingPublickKey = null;
		try {
			encryptedBytesUsingPublickKey = rsaEncModeCipher
					.doFinal(plainTextBytes);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String
					.format("RSA Cipher IllegalBlockSizeException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher BadPaddingException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		return encryptedBytesUsingPublickKey;
	}

	/**
	 * 공개키로 암호화한 데이터를 개인키로 풀어 넘겨준다. 이때 공개키로 암호화한 데이터는 세션키이며, 넘겨주게되는 데이터는 바로 대칭키가
	 * 된다. FIXME!, 보안상 대칭키는 절대로 외부로 노출되어서는 안된다. 따라서 이 메소드는 외부로 공개되어서는 안되지만,
	 * 자바스크립트 공개키 암호화 모듈 JSBN 를 테스트 하기위해서 잠시 public 으로 임시 조치하도록한다.
	 * 
	 * @param encryptedBytesWithPublicKey
	 * @return 복호문
	 * @throws SymmetricException 암호화 관련 에러가 있다면 발생
	 */
	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey)
			throws SymmetricException {	

		Cipher rsaDecModeCipher = null;

		try {
			rsaDecModeCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// rsaDecModeCipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("Cipher.getInstance NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String.format("NoSuchPaddingException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		try {
			rsaDecModeCipher.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			String errorMessage = String.format("InvalidKeyException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		byte[] decryptedBytesUsingPrivateKey = null;
		try {
			decryptedBytesUsingPrivateKey = rsaDecModeCipher
					.doFinal(encryptedBytesWithPublicKey);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String
					.format("RSA Cipher IllegalBlockSizeException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher BadPaddingException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("공개키로 암호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(encryptedBytesWithPublicKey));
		// log.info("비밀키로 복호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytesUsingPrivateKey));
		return decryptedBytesUsingPrivateKey;
	}
}
