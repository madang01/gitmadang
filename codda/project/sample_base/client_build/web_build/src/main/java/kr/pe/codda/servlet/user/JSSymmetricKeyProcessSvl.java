package kr.pe.codda.servlet.user;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class JSSymmetricKeyProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = 852145092905856928L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramAlgorithm = req.getParameter("algorithm");		
		String paramPrivateKeyHex = req.getParameter("privateKey");
		String paramIVHex = req.getParameter("iv");
		String paramPlainText = req.getParameter("plainText");
		String paramEncryptedHexText = req.getParameter("encryptedHexText");
		/**************** 파라미터 종료 *******************/
		
		log.info("paramAlgorithm=[{}]", paramAlgorithm);
		log.info("paramPrivateKeyHex=[{}]", paramPrivateKeyHex);
		log.info("paramIVHex=[{}]", paramIVHex);
		log.info("paramPlainText=[{}]", paramPlainText);
		log.info("paramEncryptedHexText=[{}]", paramEncryptedHexText);
		
		Hashtable<String,String> symmetricKeyTransformationHash = null;
		symmetricKeyTransformationHash = new Hashtable<String,String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");		
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
		
		String transformation = symmetricKeyTransformationHash.get(paramAlgorithm);
		
		if (null == transformation) {
			throw new RuntimeException(String.format("don't support the algorithm[%s]", paramAlgorithm));
		}
		
		byte[] privateKeyBytes = HexUtil.getByteArrayFromHexString(paramPrivateKeyHex);
		byte[] ivBytes = HexUtil.getByteArrayFromHexString(paramIVHex);
		byte[] encryptedBytes = HexUtil.getByteArrayFromHexString(paramEncryptedHexText);
		
		Cipher symmetricKeyCipher = null;		
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (NoSuchPaddingException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		SecretKeySpec symmetricKey = new SecretKeySpec(privateKeyBytes, paramAlgorithm);
		
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
		} catch (InvalidKeyException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramAlgorithm=[")
					.append(paramAlgorithm)
					.append("], paramPrivateKeyHex=")
					.append(paramPrivateKeyHex)
					.append("], paramIVHex=[")
					.append(paramIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramAlgorithm=[")
					.append(paramAlgorithm)
					.append("], paramPrivateKeyHex=")
					.append(paramPrivateKeyHex)
					.append("], paramIVHex=[")
					.append(paramIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		log.info("Cipher.init with IV");
		
		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			log.warn("IllegalBlockSizeException", e);
			throw new RuntimeException("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			log.warn("BadPaddingException", e);
			throw new RuntimeException("BadPaddingException");
		}
		
		String plainHexText = HexUtil.getHexStringFromByteArray(paramPlainText.getBytes());
		String decryptedHexText = HexUtil.getHexStringFromByteArray(decryptedBytes);
		log.info("plainHexText[{}], decryptedBytes[{}]", plainHexText, decryptedHexText);
		
		
		String decryptedPlainText = new String(decryptedBytes);
		String isSame = String.valueOf(decryptedPlainText.equals(paramPlainText.replaceAll("\r\n", "\n")));			
		
		
		req.setAttribute("plainText", paramPlainText);
		req.setAttribute("algorithm", paramAlgorithm);
		req.setAttribute("privateKey", paramPrivateKeyHex);
		req.setAttribute("iv", paramIVHex);
		req.setAttribute("encryptedHexText", paramEncryptedHexText);
		req.setAttribute("plainHexText", plainHexText);
		req.setAttribute("decryptedHexText", decryptedHexText);
		req.setAttribute("decryptedPlainText", decryptedPlainText);
		req.setAttribute("isSame", isSame);
		printJspPage(req, res, "/jsp/util/JSSymmetricKeyProcess.jsp");		
	}
}
