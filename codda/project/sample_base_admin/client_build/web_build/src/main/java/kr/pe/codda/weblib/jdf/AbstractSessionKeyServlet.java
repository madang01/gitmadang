/**
 * 
 * @(#) .java
 * Copyright 1999-2000 by  LG-EDS Systems, Inc.,
 * Information Technology Group, Application Architecture Team, 
 * Application Intrastructure Part.
 * 236-1, Hyosung-2dong, Kyeyang-gu, Inchun, 407-042, KOREA.
 * All rights reserved.
 *  
 * NOTICE !      You can copy or redistribute this code freely, 
 * but you should not remove the information about the copyright notice 
 * and the author.
 *  
 * @author  WonYoung Lee, wyounglee@lgeds.lg.co.kr.
 */

package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

/**
 * <pre>
 * 암호화 페이지를 보여주고자 할때 상속 받아야할 추상화 클래스로 암호화에 필요한 사용자 session key 와 iv 를 요구한다.
 * iv 값은 대칭키 특성상 동일 원문에 대한 암호문이 같지 않도록 도와주는 랜덤 값이다.
 * 
 * 복사자&수정자 : Won Jonghoon
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractSessionKeyServlet extends AbstractServlet {	

	/*protected String getRedirectPageStringGettingSessionkey(HttpServletRequest req, String modulusHexString) {
		String requestURI = req.getRequestURI();
		
		StringBuilder pageStrBuilder = new StringBuilder("<!DOCTYPE html><html><head>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<title>");
		pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_TITLE);
		pageStrBuilder.append("</title>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/jsbn.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/jsbn2.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/prng4.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rng.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rsa.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rsa2.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/rollups/sha256.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/rollups/aes.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/components/core-min.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/components/cipher-core-min.js\"></script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("<script type=\"text/javascript\">");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\tfunction goURL(targeturl) {");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tif (typeof(sessionStorage) == 'undefined' ) {");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\talert('Your browser does not support HTML5 sessionStorage. Please Upgrade your browser.');");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\treturn;");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t}");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tvar webUserPrivateKeyBase64 = sessionStorage.getItem('");
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY);
		pageStrBuilder.append("');");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// FIXME! 웹 유저 비밀키(=대칭키)
		// pageStrBuilder.append("\t\talert(sinnoriPrivateKey);");
		// pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tif (null == webUserPrivateKeyBase64 || '' == webUserPrivateKeyBase64) {");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\ttry {");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\tvar webUserPrivateKey = CryptoJS.lib.WordArray.random(");
		pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE);
		pageStrBuilder.append(");");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		
		pageStrBuilder.append("\t\t\t\tsessionStorage.setItem('");
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY);
		pageStrBuilder.append("', CryptoJS.enc.Base64.stringify(webUserPrivateKey)); // key-value 형식으로 저장");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
				
		pageStrBuilder.append("\t\t\t\tvar rsa = new RSAKey();");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\trsa.setPublic(\"");
		pageStrBuilder.append(modulusHexString);
		pageStrBuilder.append("\", \"10001\");");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\tvar sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(webUserPrivateKey));");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\tsessionStorage.setItem('");
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY);
		pageStrBuilder.append("', CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex))); // key-value 형식으로 저장");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t} catch (e) {");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\t\tsessionStorage.removeItem('");
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY);
		pageStrBuilder.append("');");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\t\tsessionStorage.removeItem('");
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY);
		pageStrBuilder.append("');");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\t\talert(e);");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t\t\treturn;");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t\t}");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\t}");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tvar g = document.gofrm;");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("\t\tg.action = targeturl;");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tg.");
		pageStrBuilder.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		pageStrBuilder.append(".value = sessionStorage.getItem('");		
		
		
		pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY);
		pageStrBuilder.append("');");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tvar iv = CryptoJS.lib.WordArray.random(");
		pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_IV_SIZE);
		pageStrBuilder.append(");");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("\t\tg.");
		pageStrBuilder.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		pageStrBuilder.append(".value = CryptoJS.enc.Base64.stringify(iv);");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
		
		pageStrBuilder.append("\t\tg.submit();");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("\t}");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("</script>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		pageStrBuilder.append("</head>");
		
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("<body onload=\"goURL('");
		pageStrBuilder.append(requestURI);
		pageStrBuilder.append("')\">");
		
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		pageStrBuilder.append("<form name=gofrm method='post'>");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		
		pageStrBuilder.append("<input type=hidden name=\"");
		pageStrBuilder.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		pageStrBuilder.append("\" />");
				
		
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		 
		
		pageStrBuilder.append("<input type=hidden name=\"");
		pageStrBuilder.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		pageStrBuilder.append("\" />");
		pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		
		Enumeration<String> parmEnum = req.getParameterNames();
		while(parmEnum.hasMoreElements()) {
			String parmName = parmEnum.nextElement();
			String parmValue = req.getParameter(parmName);
			
			pageStrBuilder.append("<input type=hidden name=\"");
			
			
			pageStrBuilder.append(StringEscapeUtils.escapeHtml4(parmName));
			
			
			pageStrBuilder.append("\" value=\"");
			pageStrBuilder.append(StringEscapeUtils.escapeHtml4(parmValue));
			pageStrBuilder.append("\" />");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
		}
		
		pageStrBuilder.append("</body>");
		pageStrBuilder.append("</html>");
		
		// log.info(pageStrBuilder.toString());
		
		return pageStrBuilder.toString();
	}*/
	
	
	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {		
		String parmSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String parmIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		
		//log.info("parmSessionKeyBase64=[{}]", parmSessionKeyBase64);
		//log.info("parmIVBase64=[{}]", parmIVBase64);
		//System.out.println("parmSessionKeyBase64="+parmSessionKeyBase64);
		// System.out.println("parmIVBase64="+parmIVBase64);
		
		// log.info("req.getRequestURI=[{}]", req.getRequestURI());
		
		
		ServerSessionkeyIF webServerSessionkey  = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
		} catch (SymmetricException e) {
			log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
			
			String errorMessage = "ServerSessionkeyManger instance init error";
			String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		
		if (null == parmSessionKeyBase64 || null == parmIVBase64) {
			String modulusHexString = webServerSessionkey.getModulusHexStrForWeb();
			
			/*java.io.PrintWriter out = res.getWriter();
			out.write(getRedirectPageStringGettingSessionkey(req, modulusHexString));
			out.flush();
			out.close();*/
			log.info("req.getRequestURI=[{}]", req.getRequestURI());			
			req.setAttribute("requestURI", req.getRequestURI());
			
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING, modulusHexString);
			printJspPage(req, res, JDF_SESSION_KEY_PAGE);
			return;
		}
		
		//log.info("modulusHexString=[{}]", modulusHexString);
		
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmSessionKeyBase64);
		} catch(Exception e) {
			log.warn("parmSessionKeyBase64[{}] base64 decode error, errormessage=[{}]", parmSessionKeyBase64, e.getMessage());
			
			String errorMessage = "the parameter parmSessionKeyBase64 is not a base64 string";
			String debugMessage = String.format("parmSessionKeyBase64[%s] base64 decode error, errormessage=[%s]", parmSessionKeyBase64, e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(parmIVBase64);
		} catch(Exception e) {
			log.warn("parmIVBase64[{}] base64 decode error, errormessage=[{}]", parmIVBase64, e.getMessage());
			
			String errorMessage = "the parameter parmIVBase64 is not a base64 string";
			String debugMessage = String.format("parmIVBase64[%s] base64 decode error, errormessage=[%s]", parmIVBase64, e.getMessage());
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		//log.info("sessionkeyBytes=[{}]", HexUtil.getHexStringFromByteArray(sessionkeyBytes));
		//log.info("ivBytes=[{}]", HexUtil.getHexStringFromByteArray(ivBytes));
		
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING, 
				webServerSessionkey.getModulusHexStrForWeb());		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes));
		performTask(req,res);
	}
}
