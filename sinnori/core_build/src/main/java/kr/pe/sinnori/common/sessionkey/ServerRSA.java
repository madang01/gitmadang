package kr.pe.sinnori.common.sessionkey;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.util.HexUtil;

public class ServerRSA implements ServerRSAIF {
	private Logger log = LoggerFactory.getLogger(ServerRSA.class);

	private KeyPair rsaKeypair = null;

	private Cipher rsaDecryptModeCipher = null;
	private RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;

	public ServerRSA() throws SymmetricException {		
		rsaKeypair = ServerRSAKeypairGetter.getRSAKeyPair();
		
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		try {
			rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(rsaKeypair.getPrivate(), RSAPrivateCrtKeySpec.class);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		try {
			rsaDecryptModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
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
			rsaDecryptModeCipher.init(Cipher.DECRYPT_MODE, rsaKeypair.getPrivate());
		} catch (InvalidKeyException e) {
			String errorMessage = String.format("InvalidKeyException, errormessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(errorMessage);
		}
	}



	public final byte[] getDupPublicKeyBytes() {
		byte[] publickKeyBytes = rsaKeypair.getPublic().getEncoded();
		return Arrays.copyOf(publickKeyBytes, publickKeyBytes.length);
	}

	public byte[] getClientSymmetricKeyBytes(byte[] sessionKeyBytes) throws SymmetricException {

		byte[] clientSymmetricKeyBytes = null;
		try {
			clientSymmetricKeyBytes = rsaDecryptModeCipher.doFinal(sessionKeyBytes);
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
		return clientSymmetricKeyBytes;
	}

	public String getModulusHexStrForWeb() {
		return HexUtil.getHexStringFromByteArray(rsaPrivateCrtKeySpec.getModulus().toByteArray());
	}
}
