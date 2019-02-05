package kr.pe.codda.common.seesionkey;

import java.util.Date;
import java.util.Random;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.sessionkey.ClientRSA;
import kr.pe.codda.common.sessionkey.ServerRSA;

public class RSATestThread extends Thread {
	private final InternalLogger log = InternalLoggerFactory.getInstance(SymmetricKeyTestThread.class);
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
			while (! Thread.currentThread().isInterrupted()) {
				// log.info("threadID[{}] running 111", threadID);
				
				String plainText = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytes  = plainText.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET);				
				
				byte encryptedBytes[] = clientRSA.encrypt(plainTextBytes);
				byte decryptedBytes[] = serverRSA.decrypt(encryptedBytes);
				
				String decryptedText = new String(decryptedBytes, CommonStaticFinalVars.DEFUALT_CHARSET);
				
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
