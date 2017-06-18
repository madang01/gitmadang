package kr.pe.sinnori.common.seesionkey;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.sessionkey.SymmetricKeyManager;

public class SymmetricKeyTestThread extends Thread {
	private Logger log = LoggerFactory.getLogger(SymmetricKeyTestThread.class);
	
	private int threadID = -1;
	private SymmetricKeyInfo symmetricKeyInfo = null;
	
	private boolean isTerminated=false;
	private String errorMessage = null;
	
	public SymmetricKeyTestThread(int threadID, SymmetricKeyInfo symmetricKeyInfo) {
		this.threadID = threadID;
		this.symmetricKeyInfo = symmetricKeyInfo;
		
	}
	public void run() {
		Random random = new Random();
		random.setSeed(new Date().getTime());	
		
		SymmetricKeyManager symmetricKeyManager = SymmetricKeyManager.getInstance();
		
		try {
			while (!Thread.currentThread().isInterrupted()) {				
				String plainText = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				
				String symmetricKeyAlgorithm = symmetricKeyInfo.getSymmetricKeyAlgorithm();
				
				byte symmetricKeyBytes[] = new byte[symmetricKeyInfo.getSymmetricKeySize()];
				random.nextBytes(symmetricKeyBytes);
						
				byte ivBytes[] = new byte[symmetricKeyInfo.getIvSize()];					
				random.nextBytes(ivBytes);
				
				
				byte encryptedBytes[] = symmetricKeyManager.encrypt(symmetricKeyAlgorithm, symmetricKeyBytes, plainTextBytes, ivBytes);
				byte decryptedBytes[] = symmetricKeyManager.decrypt(symmetricKeyAlgorithm, symmetricKeyBytes, encryptedBytes, ivBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				
				if (!decryptedText.equals(plainText)) {
					errorMessage = String.format("In the SymmetricKeyTestThread[%d] the plain text[%s] is not same to the decrypted text[%s]", 
							threadID, plainText, decryptedText);
					log.warn(errorMessage);
					break;
				}
				
				Thread.sleep(random.nextInt(5)+5);
			}
			
			log.info("the SymmetricKeyTestThread[{}] loop exist", threadID);
			
		} catch (InterruptedException e) {
			log.info("the SymmetricKeyTestThread[{}] was interrupted", threadID);
		} catch (Exception e) {
			errorMessage = String.format("the SymmetricKeyTestThread[%d]]'s error message=%s", threadID, e.getMessage());
			log.warn(errorMessage, e);
		} finally {
			isTerminated = true;
		}	
	}
	
	public boolean isTerminated() {
		return isTerminated;
	}
	
	public boolean isError() {
		return (errorMessage != null) ? true : false;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
