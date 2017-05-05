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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.util.HexUtil;

public class ServerRSA {
	private Logger log = LoggerFactory.getLogger(ServerRSA.class);

	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;
	private RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;
	private int rsaKeySize = -1;

	private Cipher rsaDecModeCipher = null;

	public ServerRSA() throws SymmetricException {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY rsaKeyPairSoureOfSessionkey = commonPart
				.getRsaKeypairSourceOfSessionKey();
		rsaKeySize = commonPart.getRsaKeySizeOfSessionKey();
		

		if (rsaKeyPairSoureOfSessionkey.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER)) {
			makeRSAKeyForServer();
		} else if (rsaKeyPairSoureOfSessionkey.equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.FILE)) {
			try {
				makeRSAKeyForFile(commonPart.getRSAPrivatekeyFileOfSessionKey(), commonPart.getRSAPublickeyFileOfSessionKey());
			} catch (SinnoriConfigurationException e) {
				log.warn(e.getMessage(), e);
				new SymmetricException(e.getMessage());
			}
		} else {
			new SymmetricException(new StringBuilder("unknown rsa keypair source[")
					.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
		}

		try {
			rsaDecModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
			// rsaDecModeCipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("Cipher.getInstance NoSuchAlgorithmException, errormessage=%s",
					e.getMessage());
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
	}

	/**
	 * <pre>
	 * 공개키 쌍을 환경 변수에서 지정하는 경로에 있는 특정 파일을 읽어 만든다.
	 * 외부 전역 변수 개인키와 공개키를 초기화 한다.
	 * (1) private PrivateKey privateKey : 개인키
	 * (2) private PublicKey publicKey : 공개키
	 * </pre>
	 */
	private void makeRSAKeyForFile(File rsaPrivateKeyFile, File rsaPublicKeyFile) {
		FileInputStream rsaPrivateKeyFIS = null;
		byte privateKeyBytes[] = null;

		try {
			rsaPrivateKeyFIS = new FileInputStream(rsaPrivateKeyFile);

			long size = rsaPrivateKeyFile.length();
			if (size > CommonStaticFinalVars.MAX_KEY_FILE_SIZE || size > Integer.MAX_VALUE) {
				String errorMessage = String.format("check rsa private key file size[%d]", size);
				log.error(errorMessage);
				System.exit(1);
			}
			privateKeyBytes = new byte[(int) size];
			rsaPrivateKeyFIS.read(privateKeyBytes);
		} catch (FileNotFoundException e) {
			String errorMessage = String.format("the RSA private key File[%s] is not found",
					rsaPrivateKeyFile.getAbsolutePath());
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

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		try {
			privateKey = rsaKeyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key(=PKCS8EncodedKeySpec)[%s]::errmessage=%s",
					HexUtil.getHexStringFromByteArray(privateKeyBytes), e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		/*File rsaPublicKeyFile = new File(
				rsaKeyPairPath.getAbsolutePath() + java.io.File.separator + CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);*/
		if (!rsaPublicKeyFile.exists()) {
			String errorMessage = String.format("sinnori.publickey file not exist. fullpath=[%s]\n",
					rsaPublicKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		if (!rsaPublicKeyFile.canRead()) {
			String errorMessage = String.format("can not read sinnori.publickey file. fullpath=[%s]\n",
					rsaPublicKeyFile.getAbsolutePath());
			log.error(errorMessage);
			System.exit(1);
		}

		FileInputStream rsaPublicKeyFIS = null;
		byte publicKeyBytes[] = null;
		try {
			rsaPublicKeyFIS = new FileInputStream(rsaPublicKeyFile);

			long size = rsaPublicKeyFile.length();
			if (size > CommonStaticFinalVars.MAX_KEY_FILE_SIZE || size > Integer.MAX_VALUE) {
				String errorMessage = String.format("check rsa public key file size[%d]", size);
				log.error(errorMessage);
				System.exit(1);
			}
			publicKeyBytes = new byte[(int) size];
			rsaPublicKeyFIS.read(publicKeyBytes);
		} catch (FileNotFoundException e) {
			String errorMessage = String.format("the RSA public key file[%s] is not found",
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

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA public key(=X509EncodedKeySpec)[%s], errormessage=%s",
					HexUtil.getHexStringFromByteArray(publicKeyBytes), e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		// FIXME!
		log.info("rsa key size=[{}], publicKey.getEncoded().length=[{}]", rsaKeySize, publicKey.getEncoded().length);
		
	}

	/**
	 * <pre>
	 * 공개키 쌍을 파일 의존하지 않고 자체적으로 만든다.
	 * 외부 전역 변수 개인키와 공개키를 초기화 한다.
	 * (1) private PrivateKey privateKey : 개인키
	 * (2) private PublicKey publicKey : 공개키
	 * </pre>
	 * 
	 */
	private void makeRSAKeyForServer() {
		KeyPairGenerator rsaKeyPairGenerator = null;
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyPairGenerator, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		rsaKeyPairGenerator.initialize(rsaKeySize);
		KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
	}

	public byte[] getPublicKeyBytes() {

		return publicKey.getEncoded();
	}

	public byte[] decryptUsingPrivateKey(byte[] encryptedBytesWithPublicKey) throws SymmetricException {

		byte[] decryptedBytesUsingPrivateKey = null;
		try {
			decryptedBytesUsingPrivateKey = rsaDecModeCipher.doFinal(encryptedBytesWithPublicKey);
		} catch (IllegalBlockSizeException e) {
			String errorMessage = String.format("RSA Cipher IllegalBlockSizeException, errormessage=%s",
					e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		} catch (BadPaddingException e) {
			String errorMessage = String.format("RSA Cipher BadPaddingException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}

		// log.info("공개키로 암호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(encryptedBytesWithPublicKey));
		// log.info("비밀키로 복호화한 이진 데이터를 16진수로 표현한 문자열[%s]",
		// HexUtil.byteArrayAllToHex(decryptedBytesUsingPrivateKey));
		return decryptedBytesUsingPrivateKey;
	}

	public String getModulusHexStrForWeb() {

		return HexUtil.getHexStringFromByteArray(rsaPrivateCrtKeySpec.getModulus().toByteArray());
	}
}
