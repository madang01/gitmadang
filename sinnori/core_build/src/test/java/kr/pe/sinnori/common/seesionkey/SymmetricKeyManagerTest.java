package kr.pe.sinnori.common.seesionkey;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.SymmetricKeyManager;

public class SymmetricKeyManagerTest {
	private Logger log = LoggerFactory.getLogger(SymmetricKeyManagerTest.class);
	
	
	@Test
	public void testEncrypt_paramter_symmetricKeyAlgorithm_null() {
		
		try {
			SymmetricKeyManager.getInstance().encrypt(null, null, null, null);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyAlgorithm is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_paramter_symmetricKeyBytes_null() {
		
		try {
			SymmetricKeyManager.getInstance().encrypt("AES", null, null, null);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_paramter_plainTextBytes_null() {
		
		String symmetricKeyAlgorithm = "AES";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = null;
		byte ivBytes[] = null;
		
		Random random = new Random();
		random.setSeed(new Date().getTime());
		
		random.nextBytes(symmetricKeyBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			if (!e.getMessage().equals("the parameter plainTextBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testEncrypt_paramter_ivBytes_null() {
		String symmetricKeyAlgorithm = "AES";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = "hello한글".getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		byte ivBytes[] = null;
		
		Random random = new Random();
		
		random.nextBytes(symmetricKeyBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			if (!e.getMessage().equals("the parameter ivBytes is null")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncrypt_paramter_symmetricKeyAlgorithm_unknownSymmetricKeyAlgorithm() {
		String symmetricKeyAlgorithm = "AAA";
		byte symmetricKeyBytes[] = new byte[128]; 
		byte [] plainTextBytes  = "hello한글".getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		byte ivBytes[] = new byte[24];
		
		Random random = new Random();
		
		random.nextBytes(symmetricKeyBytes);
		random.nextBytes(ivBytes);
		
		try {
			SymmetricKeyManager.getInstance().encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			if (!e.getMessage().equals("the parameter symmetricKeyAlgorithm["+symmetricKeyAlgorithm+"] is not a element of set[DES, DESede, AES]")) {
				fail("fail to get the expected error message");
			}
			
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	/**
Java API : https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
Every implementation of the Java platform is required to support the following standard Cipher transformations with the keysizes in parentheses: 
AES/CBC/NoPadding (128)
AES/CBC/PKCS5Padding (128)
AES/ECB/NoPadding (128)
AES/ECB/PKCS5Padding (128)
DES/CBC/NoPadding (56)
DES/CBC/PKCS5Padding (56)
DES/ECB/NoPadding (56)
DES/ECB/PKCS5Padding (56)
DESede/CBC/NoPadding (168)
DESede/CBC/PKCS5Padding (168)
DESede/ECB/NoPadding (168)
DESede/ECB/PKCS5Padding (168)
RSA/ECB/PKCS1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
	 */
	@Test
	public void testSymmetricKeyManagerBySymmetricKeyAlgorithm() {
		
		SymmetricKeyInfo[] SymmetricKeyInfoList = { 
				new SymmetricKeyInfo("AES", 16, 16), 
				new SymmetricKeyInfo("DES", 8, 8), 
				new SymmetricKeyInfo("DESede", 24, 8)}; 
		
		String plainText = "hello한글";
		byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		
		
		Random random = new Random();
		random.setSeed(new Date().getTime());		
		
		SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
		
		for (SymmetricKeyInfo symmetricKeyInfo : SymmetricKeyInfoList) {
			String symmetricKeyAlgorithm = symmetricKeyInfo.getSymmetricKeyAlgorithm();
			
			byte symmetricKeyBytes[] = new byte[symmetricKeyInfo.getSymmetricKeySize()];
			random.nextBytes(symmetricKeyBytes);
					
			byte ivBytes[] = new byte[symmetricKeyInfo.getIvSize()];					
			random.nextBytes(ivBytes);
					
			try {
				byte encryptedBytes[] = symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
				byte decryptedBytes[] = symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				
				if (!decryptedText.equals(plainText)) {
					fail(String.format("the plain text[%s] is not same to the decrypted text[%s]", plainText, decryptedText));
				}
			} catch (Exception e) {
				log.warn(symmetricKeyInfo.toString(), e);
				fail(e.getMessage());
			}
		}		
	}
	
	@Test
	public void testSymmetricKeyManagerThreadSafe() {
		SymmetricKeyInfo aesSymmetricKeyInfo = new SymmetricKeyInfo("AES", 16, 16);
		SymmetricKeyInfo desSymmetricKeyInfo = new SymmetricKeyInfo("DES", 8, 8);
		SymmetricKeyInfo desedeSymmetricKeyInfo = new SymmetricKeyInfo("DESede", 24, 8);
		
		int threadID = 0;
		SymmetricKeyTestThread symmetricKeyTestThreadList[]  = {
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, aesSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desedeSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desedeSymmetricKeyInfo),
				new SymmetricKeyTestThread(threadID++, desSymmetricKeyInfo)
		};
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			symmetricKeyTestThread.start();
		}
		
		try {
			Thread.sleep(1000L*60*10);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			symmetricKeyTestThread.interrupt();
		}
		
		while (! isAllTerminated(symmetricKeyTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			if (symmetricKeyTestThread.isError()) {
				fail(symmetricKeyTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(SymmetricKeyTestThread symmetricKeyTestThreadList[]) {
		for (SymmetricKeyTestThread symmetricKeyTestThread : symmetricKeyTestThreadList) {
			if (!symmetricKeyTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}
