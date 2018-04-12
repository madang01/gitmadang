package kr.pe.sinnori.common.sessionkey;

import java.math.BigInteger;
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

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.util.HexUtil;

public class ServerRSA implements ServerRSAIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerRSA.class);

	private KeyPair rsaKeypair = null;
	private BigInteger modulusOfRSAPrivateCrtKeySpec = null;

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
			RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = rsaKeyFactory.getKeySpec(rsaKeypair.getPrivate(), RSAPrivateCrtKeySpec.class);
			
			modulusOfRSAPrivateCrtKeySpec = rsaPrivateCrtKeySpec.getModulus();
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key spec(=RSAPrivateCrtKeySpec), errormessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}		
	}



	public final byte[] getDupPublicKeyBytes() {
		byte[] publickKeyBytes = rsaKeypair.getPublic().getEncoded();
		return Arrays.copyOf(publickKeyBytes, publickKeyBytes.length);
	}

	public byte[] decrypt(byte[] encryptedBytes) throws SymmetricException {
		if (null == encryptedBytes) {
			throw new IllegalArgumentException("the paramter encryptedBytes is null");
		}
		byte[] decryptedBytes = null;		
		Cipher rsaDecryptModeCipher = null;
		
		try {
			rsaDecryptModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
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
		
		try {
			decryptedBytes = rsaDecryptModeCipher.doFinal(encryptedBytes);
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
		return decryptedBytes;
	}

	public String getModulusHexStrForWeb() {
		return HexUtil.getHexStringFromByteArray(modulusOfRSAPrivateCrtKeySpec.toByteArray());
	}
}
