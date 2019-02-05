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
import kr.pe.codda.common.util.CommonStaticUtil;
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
	
	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {		
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		
		//log.info("paramSessionKeyBase64=[{}]", paramSessionKeyBase64);
		//log.info("paramIVBase64=[{}]", paramIVBase64);
		//System.out.println("paramSessionKeyBase64="+paramSessionKeyBase64);
		// System.out.println("paramIVBase64="+paramIVBase64);
		
		// log.info("req.getRequestURI=[{}]", req.getRequestURI());
		
		
		ServerSessionkeyIF webServerSessionkey  = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
			
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING, webServerSessionkey.getModulusHexStrForWeb());
		} catch (SymmetricException e) {
			log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
			
			String errorMessage = "ServerSessionkeyManger instance init error";
			String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramSessionKeyBase64 || null == paramIVBase64) {			
			log.info("req.getRequestURI=[{}]", req.getRequestURI());			
			req.setAttribute("requestURI", req.getRequestURI());
			printJspPage(req, res, JDF_SESSION_KEY_REDIRECT_PAGE);
			return;
		}		
		
		
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch(Exception e) {
			log.warn("paramSessionKeyBase64[{}] base64 decode error, errormessage=[{}]", paramSessionKeyBase64, e.getMessage());
			
			String errorMessage = "the parameter paramSessionKeyBase64 is not a base64 string";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
			.append("'[")
			.append(paramSessionKeyBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch(Exception e) {
			log.warn("paramIVBase64[{}] base64 decode error, errormessage=[{}]", paramIVBase64, e.getMessage());
			
			String errorMessage = "the parameter paramIVBase64 is not a base64 string";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
			.append("'[")
			.append(paramIVBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		//log.info("sessionkeyBytes=[{}]", HexUtil.getHexStringFromByteArray(sessionkeyBytes));
		//log.info("ivBytes=[{}]", HexUtil.getHexStringFromByteArray(ivBytes));
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes));
		performTask(req,res);
	}
}
