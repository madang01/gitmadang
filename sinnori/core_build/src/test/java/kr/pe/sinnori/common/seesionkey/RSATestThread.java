package kr.pe.sinnori.common.seesionkey;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.sessionkey.ClientRSA;
import kr.pe.sinnori.common.sessionkey.ServerRSA;

public class RSATestThread extends Thread {
	private Logger log = LoggerFactory.getLogger(SymmetricKeyTestThread.class);
	private int threadID = -1;
	private ServerRSA serverRSA = null;
	private ClientRSA clientRSA = null;
	
	private boolean isTerminated=false;
	private String errorMessage = null;
	
	public RSATestThread(int threadID, ServerRSA serverRSA, ClientRSA clientRSA) {
		this.threadID = threadID;
		this.serverRSA = serverRSA;
		this.clientRSA = clientRSA;
	}
	public void run() {
		Random random = new Random();
		random.setSeed(new Date().getTime());
		
		// log.info("threadID[{}] start", threadID);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				// log.info("threadID[{}] running 111", threadID);
				
				String plainText = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);				
				
				byte encryptedBytes[] = clientRSA.encrypt(plainTextBytes);
				byte decryptedBytes[] = serverRSA.decrypt(encryptedBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				
				if (!decryptedText.equals(plainText)) {
					errorMessage = String.format("In the RSATestThread[%d] the plain text[%s] is not same to the decrypted text[%s]", 
							threadID, plainText, decryptedText);
					log.warn(errorMessage);
					break;
				}
				
				
				Thread.sleep(random.nextInt(5)+5);
			}		
			
			log.info("the RSATestThread[{}] loop exist", threadID);
		} catch (InterruptedException e) {
			log.info("the RSATestThread[{}] was interrupted", threadID);
		} catch (Exception e) {
			errorMessage = String.format("the RSATestThread[%d]]'s error message=%s", threadID, e.getMessage());
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
