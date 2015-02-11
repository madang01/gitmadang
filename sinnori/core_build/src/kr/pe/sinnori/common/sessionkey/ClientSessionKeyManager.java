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

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.util.HexUtil;

import org.apache.commons.codec.binary.Base64;

/**
 * 클라이언트 관점의 세션키 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ClientSessionKeyManager implements CommonRootIF {
	private byte[] publicKeyBytes = null;
	// private PublicKey publicKey = null;
	private byte[] symmetricKeyBytes = null;
	private String symmetricKeyAlgorithm = null;
	private CommonType.SymmetricKeyEncoding symmetricKeyEncoding = null;
	private byte[] sessionKeyBytes = null;
	// private String errorMessage = null;
	private Random random = new Random();

	

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws SymmetricException 
	 * @throws IllegalArgumentException 
	 */
	public ClientSessionKeyManager(byte[] publicKeyBytes) throws IllegalArgumentException, SymmetricException {
		this.publicKeyBytes = publicKeyBytes;
		symmetricKeyAlgorithm = (String) conf
				.getResource("sessionkey.symmetric_key_algorithm.value");
		symmetricKeyEncoding = (CommonType.SymmetricKeyEncoding) conf
				.getResource("sessionkey.private_key.encoding.value");
		
		makeSessionKey(publicKeyBytes);
	}

	/**
	 * <pre>
	 * 공개 키를 받아 세션키를 만든다. 
	 * 공개키는 2 군데에서 받을 수 있다. 
	 * 첫번째 내부에서 조달받는 경우로, 공개키쌍을 생성하는 서버용 세션키 관리자를 통해서 공개키를 받는다. 주로 TCP/IP 서버쪽에서 사용한다.
	 * 두번째 외부에서 조달받는 경우로 메시지 송수신등의 방법을 통해 받는다. 주로 TCP/IP 클라이언트쪽에서 사용한다.
	 * 
	 * </pre> 
	 * 
	 * @param publicKeyBytes
	 *            공개 키
	 * @throws IllegalArgumentException
	 * @throws SymmetricException
	 */
	private void makeSessionKey(byte[] publicKeyBytes)
			throws IllegalArgumentException, SymmetricException {
		if (null == publicKeyBytes)
			throw new IllegalArgumentException(
					"parameter public key bytes is null");

		log.info(String.format("publicKeyBytes=[%s]",
				HexUtil.getHexStringFromByteArray(publicKeyBytes)));

		
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

		symmetricKeyBytes = new byte[(Integer) conf
				.getResource("sessionkey.symmetric_key_size.value")];
		random.nextBytes(symmetricKeyBytes);

		// FIXME!
		log.info(String.format("symmetricKeyBytes length=[%d]", symmetricKeyBytes.length));

		byte[] rsaPrivateKeyBytes = null;
		switch (this.symmetricKeyEncoding) {
			case NONE: {
				rsaPrivateKeyBytes = symmetricKeyBytes;
				break;
			}
			case BASE64: {
				log.info("개인키를 Base64로 인코딩한 값으로 바꾼다.");
				rsaPrivateKeyBytes = Base64.encodeBase64(symmetricKeyBytes);
			}
		}

		byte[] encryptedBytesUsingPublickKey = null;

		try {
			encryptedBytesUsingPublickKey = rsaEncModeCipher
					.doFinal(rsaPrivateKeyBytes);
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

		sessionKeyBytes = encryptedBytesUsingPublickKey;
	}

	/*
	 * public boolean isSessionKey() throws RuntimeException { if (null !=
	 * errorMessage) throw new RuntimeException(errorMessage);
	 * 
	 * return (null != sessionKeyBytes); }
	 * 
	 * 
	 * 
	 * public String getSymmetricKeyAlgorithm() throws RuntimeException { if
	 * (null != errorMessage) throw new RuntimeException(errorMessage); return
	 * symmetricKeyAlgorithm; }
	 * 
	 * public PrivakeKeyEncoding getPrivateKeyEncoding() throws RuntimeException
	 * { if (null != errorMessage) throw new RuntimeException(errorMessage);
	 * return symmetricKeyEncoding; }
	 */

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
			throw new SymmetricException("ClientSessionKeyManager not ready");

		byte[] ivBytes = new byte[(Integer) conf
				.getResource("sessionkey.iv_size.value")];
		random.nextBytes(ivBytes);

		// FIXME!
		log.info(String.format("ivBytes length=[%d]", ivBytes.length));

		SymmetricKey symmetricKey = new SymmetricKey(symmetricKeyAlgorithm,
				symmetricKeyBytes, ivBytes);

		return symmetricKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientSessionKeyManager [publicKeyBytes=");
		builder.append(HexUtil.getHexStringFromByteArray(publicKeyBytes));
		builder.append(", symmetricKeyBytes=");
		builder.append(HexUtil.getHexStringFromByteArray(symmetricKeyBytes));
		builder.append(", symmetricKeyAlgorithm=");
		builder.append(symmetricKeyAlgorithm);
		builder.append(", symmetricKeyEncoding=");
		builder.append(symmetricKeyEncoding);
		builder.append(", sessionKeyBytes=");
		builder.append(Arrays.toString(sessionKeyBytes));
		builder.append("]");
		return builder.toString();
	}

	
}
