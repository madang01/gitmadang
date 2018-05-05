package kr.pe.codda.common.sessionkey;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.io.FileUtils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.Configuration;
import kr.pe.codda.common.config.ConfigurationManager;
import kr.pe.codda.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.codda.common.exception.ConfigurationException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.HexUtil;

public abstract class ServerRSAKeypairGetter {
	
	public static KeyPair getRSAKeyPair() throws SymmetricException {
		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();

		SessionKey.RSAKeypairSourceType rsaKeyPairSoureOfSessionkey = commonPart
				.getRsaKeypairSourceOfSessionKey();
		
		KeyPair rsaKeypair = null;
		
		if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.SERVER)) {
			rsaKeypair = getRSAKeyPairFromServer();
		} else if (rsaKeyPairSoureOfSessionkey.equals(SessionKey.RSAKeypairSourceType.FILE)) {
			rsaKeypair = getRSAKeyPairFromFile();
		} else {
			throw new SymmetricException(new StringBuilder("unknown rsa keypair source[")
					.append(rsaKeyPairSoureOfSessionkey.toString()).append("]").toString());
		}
		
		return rsaKeypair;
	}
	
	private static KeyPair getRSAKeyPairFromServer() throws SymmetricException {
		InternalLogger log = InternalLoggerFactory.getInstance(ServerRSAKeypairGetter.class);
		
		
		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();		
		int rsaKeySize = commonPart.getRsaKeySizeOfSessionKey();
		
		KeyPairGenerator rsaKeyPairGenerator = null;
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyPairGenerator, errmessage=%s", e.getMessage());
			log.error(errorMessage, e);
			System.exit(1);
		}

		rsaKeyPairGenerator.initialize(rsaKeySize);
		KeyPair rsaKeypair = rsaKeyPairGenerator.generateKeyPair();
			
		
		return rsaKeypair;
	}
	
	private static KeyPair getRSAKeyPairFromFile() throws SymmetricException {
		InternalLogger log = InternalLoggerFactory.getInstance(ServerRSAKeypairGetter.class);
		
		PrivateKey privateKey = null;
		PublicKey publicKey = null;		
		
		Configuration sinnoriRunningProjectConfiguration = ConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		File rsaPrivateKeyFile = null;
		File rsaPublicKeyFile = null;
		try {
			rsaPrivateKeyFile = commonPart.getRSAPrivatekeyFileOfSessionKey();
			rsaPublicKeyFile = commonPart.getRSAPublickeyFileOfSessionKey();
		} catch (ConfigurationException e) {
			log.warn(e.getMessage(), e);
			throw new SymmetricException(e.getMessage());
		}
		
		
		byte privateKeyBytes[] = null;
		try {
			privateKeyBytes = FileUtils.readFileToByteArray(rsaPrivateKeyFile);
		} catch (IOException e) {
			String errorMessage = String.format("the RSA private key File[%s] IOException, errmessage=%s",
					rsaPrivateKeyFile.getAbsolutePath(), e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = String.format("fail to get the RSA KeyFactory, errmessage=%s", e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		try {
			privateKey = rsaKeyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA private key(=PKCS8EncodedKeySpec)[%s]::errmessage=%s",
					HexUtil.getHexStringFromByteArray(privateKeyBytes), e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}

		byte publicKeyBytes[] = null;
		try {
			publicKeyBytes = FileUtils.readFileToByteArray(rsaPublicKeyFile);
		} catch (IOException e) {
			String errorMessage = String.format("the RSA public key file[%s] IOException, errormessage=%s",
					rsaPublicKeyFile.getAbsolutePath(), e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}
		
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			String errorMessage = String.format(
					"fail to get the RSA public key(=X509EncodedKeySpec)[%s], errormessage=%s",
					HexUtil.getHexStringFromByteArray(publicKeyBytes), e.getMessage());
			log.warn(errorMessage, e);
			throw new SymmetricException(e.getMessage());
		}
		
		return new KeyPair(publicKey, privateKey);
	}
	
}
