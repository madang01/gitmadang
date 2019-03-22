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
 *  공통적인 전 작업과 개별 프로그래머가 작성할 작업을 분리해 주는 추상화 클래스.
 *  
 * 복사자&수정자 : Won Jonghoon
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends JDFBaseServlet {
	
	@Override
	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
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
		
		performTask(req,res);	
	}

	/**
	 * 개발자는 이 method를 implementation하여 개발하시면 됩니다.
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param res javax.servlet.http.HttpServletResponse
	 */
	protected abstract void performTask (HttpServletRequest req, HttpServletResponse res) throws Exception;	
	
	@Override
	protected void printErrorMessagePage (HttpServletRequest req, HttpServletResponse res, 
			String userMessage, String debugMessage) {		
				
		if (null == userMessage) {
			userMessage = "user messsage is null";
		}

		if (null == debugMessage) {
			debugMessage = "";
		}
		
		req.setAttribute("debugMessage", debugMessage);
		req.setAttribute("userMessage", userMessage);
		
		printJspPage(req, res, JDF_ERROR_MESSAGE_PAGE);
	}
}
