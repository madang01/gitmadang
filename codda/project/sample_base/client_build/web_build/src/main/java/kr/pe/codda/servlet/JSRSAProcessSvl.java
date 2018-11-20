package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class JSRSAProcessSvl extends AbstractServlet {
	
	private static final long serialVersionUID = 5047248659153700972L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String paramEncryptedHexTextWithPublicKey = req.getParameter("encryptedHexTextWithPublicKey");
		String paramPlainText = req.getParameter("plainText");
		
		log.info("paramEncryptedHexTextWithPublicKey[{}]", paramEncryptedHexTextWithPublicKey);
		log.info("paramPlainText[{}]", paramPlainText);
		
		
		if (null == paramEncryptedHexTextWithPublicKey) {
			String errorMessage = "헥사로 표현된 공개키로 암호화한 암호문을 입력해 주세요";
			String debugMessage = "the web parameter 'encryptedHexTextWithPublicKey' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPlainText) {
			String errorMessage = "평문을 입력해 주세요";
			String debugMessage = "the web parameter 'plainText' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		/**
		 * 자바 스크립에서 RSA 로 암호문을 만들면 \r\n 을 보존하지 않고 강제적으로 \n 으로 변경한다. 
		 * 따라서 원문과 복혹문 정상적인 비교를 위해서 원문도 똑같이 \n 으로 변경해 주어야 한다. 
		 */
		String plainHexText = HexUtil
				.getHexStringFromByteArray(paramPlainText.replaceAll("\r\n", "\n").getBytes(CommonStaticFinalVars.CIPHER_CHARSET));		
		//log.info("plainText hex[%s]", plainTextHex);
		// String sessionKeyHex =  new String(HexUtil.hexToByteArray(sessionKeyDoubleHex));
		//log.info("sessionKeyHex=[%s]", sessionKeyHex);
		byte encryptedBytesWithPublicKey[] = HexUtil.getByteArrayFromHexString(paramEncryptedHexTextWithPublicKey);
		
		byte decryptedBytesUsingPrivateKey[] = null;
		try {
			decryptedBytesUsingPrivateKey = webServerSessionkey.decryptUsingPrivateKey(encryptedBytesWithPublicKey);
		} catch (SymmetricException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramEncryptedHexTextWithPublicKey=[")
					.append(paramEncryptedHexTextWithPublicKey)					
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
		}			
		String decryptedHexTextUsingPrivateKey = HexUtil.getHexStringFromByteArray(decryptedBytesUsingPrivateKey);
		//log.info(String.format("decryptUsingPrivateKey=[%s]", decryptUsingPrivateKeyHex));
		
		log.info("plainHexText={}", plainHexText);
		log.info("decryptedHexTextUsingPrivateKey={}", decryptedHexTextUsingPrivateKey);
		
		boolean isSame = plainHexText.equals(decryptedHexTextUsingPrivateKey);
		//log.info(String.format("resultMessage=[%s]", resultMessage));
		
		
		String decryptedText = new String(decryptedBytesUsingPrivateKey);
		
		req.setAttribute("orignalPlainText", paramPlainText);
		req.setAttribute("decryptedText", decryptedText);
		req.setAttribute("isSame", String.valueOf(isSame));		
		printJspPage(req, res, "/jsp/util/JSRSAProcess.jsp");
	}

}
