package kr.pe.sinnori.common.sessionkey;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;

public class ClientRSA implements ClientRSAIF {
	private Logger log = LoggerFactory.getLogger(ClientRSA.class);	
	
	private byte[] publicKeyBytes = null;	
	
	public ClientRSA(byte[] publicKeyBytes ) throws SymmetricException {
		this.publicKeyBytes =publicKeyBytes;		
	}
	
	public final byte[] getDupPublicKeyBytes() {		
		return Arrays.copyOf(publicKeyBytes, publicKeyBytes.length);
	}
	
	public byte[] encrypt(byte plainTextBytes[]) throws SymmetricException {
		if (null == plainTextBytes) {
			throw new IllegalArgumentException("the paramter plainTextBytes is null");
		}
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
			rsaEncModeCipher = Cipher.getInstance(CommonStaticFinalVars.RSA_TRANSFORMATION);
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
		
		byte encryptedBytes[] = null;
		try {
			encryptedBytes = rsaEncModeCipher
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
		return encryptedBytes;
	}
	
}
