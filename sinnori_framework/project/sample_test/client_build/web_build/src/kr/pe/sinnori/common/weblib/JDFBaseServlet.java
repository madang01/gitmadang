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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;

/**
 * <pre>
 * get 방식과 post 방식 추상화 시켜 하나로 바라보게 해 주는 JDF 프레임 최상의 추상 클래스. 
 * 복사자&수정자 : Jonghoon Won
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class JDFBaseServlet extends HttpServlet implements CommonRootIF {
	protected String web_layout_control_page = (String)conf.getResource("servlet_jsp.web_layout_control_page.value");
	protected String jdf_login_page = (String)conf.getResource("servlet_jsp.jdf_login_page.value");
	protected String jdf_error_message_page = (String)conf.getResource("servlet_jsp.jdf_error_message_page.value");
	protected boolean jdf_servlet_trace = (Boolean)conf.getResource("servlet_jsp.jdf_servlet_trace.value");
	
	
	/**
	 * BaseServlet constructor comment.
	 */
	public JDFBaseServlet() {
		super();
	}

	/**
	 * Performs the HTTP GET operation; the default implementation
	 * reports an HTTP BAD_REQUEST error.  Overriding this method to
	 * support the GET operation also automatically supports the HEAD
	 * operation.  (HEAD is a GET that returns no body in the response;
	 * it just returns the request HEADer fields.)
	 *
	 * <p>Servlet writers who override this method should read any data
	 * from the request, set entity headers in the response, access the
	 * writer or output stream, and, finally, write any response data.
	 * The headers that are set should include content type, and
	 * encoding.  If a writer is to be used to write response data, the
	 * content type must be set before the writer is accessed.  In
	 * general, the servlet implementor must write the headers before
	 * the response data because the headers can be flushed at any time
	 * after the data starts to be written.
	 * 
	 * <p>Setting content length allows the servlet to take advantage
	 * of HTTP "connection keep alive".  If content length can not be
	 * set in advance, the performance penalties associated with not
	 * using keep alives will sometimes be avoided if the response
	 * entity fits in an internal buffer.
	 *
	 * <p>Entity data written for a HEAD request is ignored.  Servlet
	 * writers can, as a simple performance optimization, omit writing
	 * response data for HEAD methods.  If no response data is to be
	 * written, then the content length field must be set explicitly.
	 *
	 * <P>The GET operation is expected to be safe: without any side
	 * effects for which users might be held responsible.  For example,
	 * most form queries have no side effects.  Requests intended to
	 * change stored data should use some other HTTP method.  (There
	 * have been cases of significant security breaches reported
	 * because web-based applications used GET inappropriately.)
	 *
	 * <P> The GET operation is also expected to be idempotent: it can
	 * safely be repeated.  This is not quite the same as being safe,
	 * but in some common examples the requirements have the same
	 * result.  For example, repeating queries is both safe and
	 * idempotent (unless payment is required!), but buying something
	 * or modifying data is neither safe nor idempotent.
	 *
	 * @param req HttpServletRequest that encapsulates the request to
	 * the servlet 
	 * @param resp HttpServletResponse that encapsulates the response
	 * from the servlet
	 * 
	 * @exception IOException if detected when handling the request
	 * @exception ServletException if the request could not be handled
	 * 
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException 
	{
		performBasePreTask(req, res);
	}

	/**
	 *
	 * Performs the HTTP POST operation; the default implementation
	 * reports an HTTP BAD_REQUEST error.  Servlet writers who override
	 * this method should read any data from the request (for example,
	 * form parameters), set entity headers in the response, access the
	 * writer or output stream and, finally, write any response data
	 * using the servlet output stream.  The headers that are set
	 * should include content type, and encoding.  If a writer is to be
	 * used to write response data, the content type must be set before
	 * the writer is accessed.  In general, the servlet implementor
	 * must write the headers before the response data because the
	 * headers can be flushed at any time after the data starts to be
	 * written.
	 *
	 * <p>If HTTP/1.1 chunked encoding is used (that is, if the
	 * transfer-encoding header is present), then the content-length
	 * header should not be set.  For HTTP/1.1 communications that do
	 * not use chunked encoding and HTTP 1.0 communications, setting
	 * content length allows the servlet to take advantage of HTTP
	 * "connection keep alive".  For just such communications, if
	 * content length can not be set, the performance penalties
	 * associated with not using keep alives will sometimes be avoided
	 * if the response entity fits in an internal buffer.
	 *
	 * <P> This method does not need to be either "safe" or
	 * "idempotent".  Operations requested through POST can have side
	 * effects for which the user can be held accountable.  Specific
	 * examples including updating stored data or buying things online.
	 *
	 * @param req HttpServletRequest that encapsulates the request to
	 * the servlet 
	 * @param resp HttpServletResponse that encapsulates the response
	 * from the servlet
	 * 
	 * @exception IOException if detected when handling the request
	 * @exception ServletException if the request could not be handled
	 *
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{	
		performBasePreTask(req, res);
	}

	/**
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	protected void performBasePreTask (HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{	
		
		// req.setCharacterEncoding(Charset.defaultCharset().toString());
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");
		
		String logMsg = null;
		long start = 0, end = 0;
		if ( jdf_servlet_trace ) {
			// logMsg = req.getRequestURI() + ":" + req.getRemoteHost() + "(" + req.getRemoteAddr() + ")";
			StringBuilder logMsgStrBuff = new StringBuilder(req.getRequestURI()).append(":").append(req.getRemoteHost()).append("(").append(req.getRemoteAddr()).append(")"); 
			
			
			String user = req.getRemoteUser();
			if ( user != null ) {
				// logMsg += ":" + user;
				logMsgStrBuff.append(":").append(user);
			}
			
			logMsg = logMsgStrBuff.toString();
			
			start = System.currentTimeMillis();
			log.info(String.format("%s:calling", logMsg));
		}
		
		String topmenu = req.getParameter("topmenu");
		//if (null ==  topmenu) topmenu="";
		
		String leftmenu = req.getRequestURI();
		//if (null ==  leftmenu) leftmenu = req.getRequestURI();
		
		req.setAttribute("topmenu", topmenu);
		req.setAttribute("leftmenu", leftmenu);
		
		try {		
			performPreTask(req, res);
		}
		catch(Exception e){
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.PrintWriter writer = new java.io.PrintWriter(bos);
			e.printStackTrace(writer);
			writer.flush();
			
			String userMessgae = bos.toString();
			
			try{
				writer.close();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			
			// String errString = "Programmer's Exception: " +logMsg +  CommonStaticFinal.NEWLINE + bos.toString();			
			// Logger.err.println(this, errString);
			String errString = new StringBuilder("Programmer's Exception: ").append(logMsg).append(CommonStaticFinalVars.NEWLINE).append(userMessgae).toString(); 
			
			log.warn(String.format("1. userMessgae=[%s]", userMessgae), e);
			
			printMessagePage(req,res, errString);
		}

		if ( jdf_servlet_trace ) {
			end = System.currentTimeMillis();
			// Logger.sys.println(this, logMsg + ":end(elapsed=" + (end-start) + ")" + CommonStaticFinal.NEWLINE);
			log.info(new StringBuilder(logMsg).append(":end(elapsed=").append((end-start)).append(")").append(CommonStaticFinalVars.NEWLINE).toString());
		}
	}

	/**
	 * get, post 모드에 상관없이 하나로 보게 해주는 메소드.
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	protected abstract void performPreTask (HttpServletRequest req, HttpServletResponse res) throws Exception;

	/**
	* Sends a temporary redirect response to the client using the
	* specified redirect location URL.  The URL must be absolute (for
	* example, <code><em>https://hostname/path/file.html</em></code>).
	* Relative URLs are not permitted here.
	*
	* @param req javax.servlet.http.HttpServletRequest
	* @param res javax.servlet.http.HttpServletResponse
	* @param location the redirect location URL
	* @exception IOException If an I/O error has occurred.
	*/
	protected void printHtmlPage (HttpServletRequest req, HttpServletResponse res, String location) {
		try {
			res.sendRedirect(location);
		}
		catch (IOException e) {
			StringBuilder errStrBuff = new StringBuilder("File Not Found : ").append(location);
			String err = "IOException";
			if ( err != null ) errStrBuff.append("(").append(err).append(")");			
			printMessagePage(req,res, errStrBuff.toString());
		}
	}

	/**
	* Sends a temporary redirect response to the client using the
	* specified redirect location of jsp file.  The URL must be absolute (for
	* example, <code><em>/example/result.jsp</em></code>).
	* Relative URLs are not permitted here.
	*
	* @param req javax.servlet.http.HttpServletRequest
	* @param res javax.servlet.http.HttpServletResponse
	* @param jspfile the redirect location URL of jsp file.
	*/
	protected void printJspPage (HttpServletRequest req, HttpServletResponse res, String jspfile) {
		try {
			
			req.setAttribute("targeturl", jspfile);

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(web_layout_control_page);

        
			dispatcher.forward(req, res);
		}
		catch (IllegalStateException e) {
			try{
				java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
				java.io.PrintWriter writer = new java.io.PrintWriter(bos);
				writer.print("JSP Call Error: ");
				writer.println(this.getClass().getName());
				
				writer.print("Request URI: ");
				writer.println(req.getRequestURI());
				
				String user = req.getRemoteUser();
				if ( user != null ) {
					writer.print("User : ");
					writer.println(user);
					
				}
				writer.print("User Location  : ");
				writer.print(req.getRemoteHost());
				writer.print("(");
				writer.print(req.getRemoteAddr());
				writer.println(")");
				
				e.printStackTrace(writer);
				writer.flush();
				
				String userMessgae = bos.toString();
				
				try{
					writer.close();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				
				log.warn(String.format("2. writer=[%s]", userMessgae), e);
				
				res.setContentType("text/html;charset=UTF-8");
				java.io.PrintWriter out = res.getWriter();
				out.println("<html><head><title>Errort</title></head><body bgcolor=white><xmp>");
				out.println(userMessgae);
				out.println("</xmp></body></html>");
				out.close();
			}
			catch(Exception ex){}
		}
		catch (Exception e) {
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.PrintWriter writer = new java.io.PrintWriter(bos);
			writer.print("JSP Call Error: ");
			writer.println(this.getClass().getName());
			
			writer.print("Request URI: ");
			writer.println(req.getRequestURI());
			
			String user = req.getRemoteUser();
			if ( user != null ) {
				writer.print("User : ");
				writer.println(user);
				
			}
			writer.print("User Location  : ");
			writer.print(req.getRemoteHost());
			writer.print("(");
			writer.print(req.getRemoteAddr());
			writer.println(")");
			
			e.printStackTrace(writer);
			writer.flush();
			
			String userMessgae = bos.toString();
			
			try{
				writer.close();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			
			log.warn(String.format("3. userMessgae=[%s]", userMessgae), e);
			
			printMessagePage(req,res, userMessgae);
			
			
		}
	}

	

	/**
	 * 디버깅 메시지 없는 메시지 출력
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param res javax.servlet.http.HttpServletResponse
	 * @param msg 최종사용자에게 보여질 메세지
	 */
	protected void printMessagePage (HttpServletRequest req, HttpServletResponse res, String msg) 
	{
		printMessagePage(req,res, msg, (String)null);
	}

	

	/**
	 * 이 Method는 반드시 프로젝트에서 구현해서 사용해야 함. 왜냐면, 프로젝트마다
	 * Message를 보여주는 화면이 다를 수 있기 때문.
	 *
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param res javax.servlet.http.HttpServletResponse
	 * @param user_msg 최종 사용자에게 보여질 메세지
	 * @param debug_msg 개발시점에서 개발자가 Debugging을 위해 보는 메세지, 
	 *                                  통상 운영시는 보이지 않도록 함.
	 */
	protected abstract void printMessagePage (HttpServletRequest req, HttpServletResponse res, String user_msg, String debug_msg);
	
}
