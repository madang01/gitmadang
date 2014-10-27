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

package kr.pe.sinnori.common.weblib;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *  공통적인 전 작업과 개별 프로그래머가 작성할 작업을 분리해 주는 추상화 클래스.
 *  
 * 복사자&수정자 : Jonghoon Won
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends JDFBaseServlet {	

	
	@Override
	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {	
		
		performTask(req,res);	
	}

	/**
	 * 개발자는 이 method를 implementation하여 개발하시면 됩니다.
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param res javax.servlet.http.HttpServletResponse
	 */
	protected abstract void performTask (HttpServletRequest req, HttpServletResponse res) throws Exception;	
	
	@Override
	protected void printMessagePage (HttpServletRequest req, HttpServletResponse res, String user_msg, String debug_msg) {		
		
		
		if (null == user_msg) {
			user_msg = "user messsage is null";
		}
		
		
		if (null == debug_msg) {
			debug_msg = "debug messsage is null";
		}
		
		req.setAttribute("debug_msg", debug_msg);
		req.setAttribute("user_msg", user_msg);
		req.setAttribute("targeturl", jdf_error_message_page);
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(web_layout_control_page);
		
		try {
			dispatcher.forward(req, res);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
