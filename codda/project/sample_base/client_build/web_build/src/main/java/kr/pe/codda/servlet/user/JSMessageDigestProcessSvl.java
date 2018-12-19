package kr.pe.codda.servlet.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class JSMessageDigestProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = 7602060507367972223L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramAlgorithm = req.getParameter("algorithm");		
		String paramJavascriptMDHex = req.getParameter("javascriptMD");
		String paramPlainText = req.getParameter("plainText");
		/**************** 파라미터 종료 *******************/
		
		log.info("paramAlgorithm[{}]", paramAlgorithm);
		log.info("paramJavascriptMDHex[{}]", paramJavascriptMDHex);
		log.info("paramPlainText[{}]", paramPlainText);
		
		if (null == paramAlgorithm) {
			String errorMessage = "알고리즘을 입력해 주세요";
			String debugMessage = "the web parameter 'algorithm' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramJavascriptMDHex) {
			String errorMessage = "알고리즘을 입력해 주세요";
			String debugMessage = "the web parameter 'javascriptMD' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPlainText) {
			String errorMessage = "평문을 입력해 주세요";
			String debugMessage = "the web parameter 'plainText' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		
		
		byte[] javascriptMD = HexUtil.getByteArrayFromHexString(paramJavascriptMDHex);
		
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(paramAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a MessageDigest class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("the web parameter 'algorithm'[")
					.append(paramAlgorithm)
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		md.update(paramPlainText.replaceAll("\r\n", "\n").getBytes());
		
		byte serverMD[] =  md.digest();
		
		// log.info(String.format("server digestMessage[%s]", HexUtil.getHexStringFromByteArray(serverMD)));
		
		String isSame = String.valueOf(Arrays.equals(javascriptMD, serverMD));
		
		req.setAttribute("plainText", paramPlainText);
		req.setAttribute("algorithm", paramAlgorithm);
		req.setAttribute("javascriptMDHex", paramJavascriptMDHex);
		req.setAttribute("serverMDHex", HexUtil.getHexStringFromByteArray(serverMD));
		req.setAttribute("isSame", isSame);
		
		
		printJspPage(req, res, "/jsp/util/JSMessageDigestProcess.jsp");
		
	}

}
