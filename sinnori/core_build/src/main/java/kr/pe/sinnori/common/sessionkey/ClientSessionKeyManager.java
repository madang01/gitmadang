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

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 클라이언트 관점의 세션키 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ClientSessionKeyManager {
	private Logger log = LoggerFactory.getLogger(ClientSessionKeyManager.class);
	
	private byte[] publicKeyBytes = null;
	private byte[] symmetricKeyBytes = null;
	private byte[] sessionKeyBytes = null;
	private Random random = new Random();
	
	/** Warning! the variable symmetricIVSize must not create getXXX method because it is Sinnori configuration variable */
	private int symmetricIVSize;
	/** Warning! the variable symmetricKeyAlgorithm must not create getXXX method because it is Sinnori configuration variable */
	private String symmetricKeyAlgorithm = null;
	/** Warning! the variable symmetricKeyEncoding must not create getXXX method because it is Sinnori configuration variable */
	private CommonType.SYMMETRIC_KEY_ENCODING_TYPE symmetricKeyEncodingType = null;
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자<br/>
	 * <pre>
	 * 공개 키를 받아 세션키를 만든다. 
	 * 공개키는 2 군데에서 받을 수 있다. 
	 * 첫번째 내부에서 조달받는 경우로, 공개키쌍을 생성하는 서버용 세션키 관리자를 통해서 공개키를 받는다. 주로 TCP/IP 서버쪽에서 사용한다.
	 * 두번째 외부에서 조달받는 경우로 메시지 송수신등의 방법을 통해 받는다. 주로 TCP/IP 클라이언트쪽에서 사용한다.
	 * </pre>
	 * 
	 * @param publicKeyBytes
	 * @throws IllegalArgumentException
	 * @throws SymmetricException
	 */
	public ClientSessionKeyManager(byte[] publicKeyBytes) throws IllegalArgumentException, SymmetricException {		
		if (null == publicKeyBytes) {
			throw new IllegalArgumentException("the parameter publicKeyBytes is null");
		}
		
		if (publicKeyBytes.length == 0) {
			throw new IllegalArgumentException("the parameter publicKeyBytes's length is zero");
		}
			
		
		this.publicKeyBytes = publicKeyBytes;
		
		log.info(String.format("publicKeyBytes=[%s]",
				HexUtil.getHexStringFromByteArray(publicKeyBytes)));
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();		
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		symmetricIVSize = commonPart.getSymmetricIVSizeOfSessionKey();		
		symmetricKeyAlgorithm = commonPart.getSymmetricKeyAlgorithmOfSessionKey();
		symmetricKeyEncodingType = commonPart.getSymmetricKeyEncodingOfSessionKey();
		int symmetricKeySize = commonPart.getSymmetricKeySizeOfSessionKey();
		
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyBytes);

		PublicKey publicKey = null;
		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String
					.format("RSA Public Key InvalidKeySpecException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		Cipher rsaEncModeCipher = null;

		try {
			rsaEncModeCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// rsaDecModeCipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String
					.format("RSA Cipher.getInstance NoSuchAlgorithmException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (NoSuchPaddingException e) {
			String errorMessage = String
					.format("RSA Cipher NoSuchPaddingException, errormessage=%s", e.getMessage());
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
		
		
		
		symmetricKeyBytes = new byte[symmetricKeySize];		
		random.nextBytes(symmetricKeyBytes);

		

		byte[] symmetricKeyBytesWithEncodingTypeApplied = null;
		switch (symmetricKeyEncodingType) {
			case NONE: {
				symmetricKeyBytesWithEncodingTypeApplied = symmetricKeyBytes;
				break;
			}
			case BASE64: {
				log.info("개인키를 Base64로 인코딩한 값으로 바꾼다.");
				symmetricKeyBytesWithEncodingTypeApplied = Base64.encodeBase64(symmetricKeyBytes);
			}
			default : {
				throw new SymmetricException("unknown symmetirc key encoding type");
			}
		}

		// byte[] encryptedBytesUsingPublickKey = null;

		try {
			sessionKeyBytes = rsaEncModeCipher
					.doFinal(symmetricKeyBytesWithEncodingTypeApplied);
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

		// sessionKeyBytes = encryptedBytesUsingPublickKey;
		
		
	}

	/**
	 * 세션키를 반환한다. 참고) 개인키는 싱글턴 패턴 생성자에서 환경 변수에서 지정하는 크기를 가지며 랜덤 값을 갖는다. 
	 * 
	 * @return 세션키
	 * @throws SymmetricException
	 *             공개키를 받지 못했을 경우 발생
	 */
	public byte[] getSessionKey() throws SymmetricException {
		if (null == sessionKeyBytes)
			throw new SymmetricException("ClientSessionKeyManager not ready");

		return sessionKeyBytes;
	}

	/**
	 * 세션키 만들때 사용된 대칭키와 랜덤하게 생성한 씨앗값을 갖는 대칭키 편의 클래스를 반환한다. 참고) 씨앗값은 호출될때 마다 새롭게 바뀐다.
	 * 
	 * @return 랜덤하게 생성한 대칭키와 씨앗값을 갖는 대칭키 편의 클래스
	 * @throws SymmetricException
	 *             세션키 만들때 사용된 대칭키가 준비되지 않았을때 발생, 먼저 공개키로 세션키를 만드는 메소드({@link #makeSessionKey(byte[])} 를 먼저 호출되어야한다.
	 */
	public SymmetricKey getSymmetricKey() throws SymmetricException {
		if (null == sessionKeyBytes)
			throw new SymmetricException("not ready");
		
		/** Warning! the variable ivByte must be local variable for Thread Safe whenever request */
		byte[] ivBytes = new byte[symmetricIVSize];
		random.nextBytes(ivBytes);

		// log.info("the member variable ivBytes was initialized randomly");		

		SymmetricKey symmetricKey = new SymmetricKey(symmetricKeyAlgorithm, symmetricKeyBytes, ivBytes);

		return symmetricKey;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSessionKeyManager [publicKeyBytes=");
		builder.append(Arrays.toString(publicKeyBytes));
		builder.append(", symmetricKeyBytes=");
		builder.append(Arrays.toString(symmetricKeyBytes));
		builder.append(", sessionKeyBytes=");
		builder.append(Arrays.toString(sessionKeyBytes));
		builder.append(", symmetricIVSize=");
		builder.append(symmetricIVSize);
		builder.append(", symmetricKeyAlgorithm=");
		builder.append(symmetricKeyAlgorithm);
		builder.append(", symmetricKeyEncodingType=");
		builder.append(symmetricKeyEncodingType);
		builder.append("]");
		return builder.toString();
	}
}
