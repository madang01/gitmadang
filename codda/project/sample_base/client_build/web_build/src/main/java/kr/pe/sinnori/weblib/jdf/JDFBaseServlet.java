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

package kr.pe.sinnori.weblib.jdf;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.weblib.common.BoardType;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil;
import kr.pe.sinnori.weblib.sitemenu.CommunityLeftMenuInfo;

/**
 * <pre>
 * get 방식과 post 방식 추상화 시켜 하나로 바라보게 해 주는 JDF 프레임 최상의 추상 클래스. 
 * 복사자&수정자 : Won Jonghoon
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class JDFBaseServlet extends AbstractBaseServlet {
	// protected String WEB_LAYOUT_CONTROL_PAGE = null;
	protected String JDF_LOGIN_PAGE = null;
	protected String JDF_ERROR_MESSAGE_PAGE = null;
	protected boolean JDF_SERVLET_TRACE = true;

	/**
	 * BaseServlet constructor comment.
	 */
	public JDFBaseServlet() {
		super();
		
		SinnoriConfigurationManager sinnoriConfigurationManager = SinnoriConfigurationManager.getInstance();
		
		
		CommonPartConfiguration commonPart = sinnoriConfigurationManager
					.getSinnoriRunningProjectConfiguration()
					.getCommonPartConfiguration();
		
		// WEB_LAYOUT_CONTROL_PAGE = commonPart.getWebLayoutControlPage();
		JDF_LOGIN_PAGE = commonPart.getJdfLoginPage();
		JDF_ERROR_MESSAGE_PAGE = commonPart.getJdfErrorMessagePage();
		JDF_SERVLET_TRACE = commonPart.getJdfServletTrace();
	}

	/**
	 * Performs the HTTP GET operation; the default implementation reports an
	 * HTTP BAD_REQUEST error. Overriding this method to support the GET
	 * operation also automatically supports the HEAD operation. (HEAD is a GET
	 * that returns no body in the response; it just returns the request HEADer
	 * fields.)
	 * 
	 * <p>
	 * Servlet writers who override this method should read any data from the
	 * request, set entity headers in the response, access the writer or output
	 * stream, and, finally, write any response data. The headers that are set
	 * should include content type, and encoding. If a writer is to be used to
	 * write response data, the content type must be set before the writer is
	 * accessed. In general, the servlet implementor must write the headers
	 * before the response data because the headers can be flushed at any time
	 * after the data starts to be written.
	 * 
	 * <p>
	 * Setting content length allows the servlet to take advantage of HTTP
	 * "connection keep alive". If content length can not be set in advance, the
	 * performance penalties associated with not using keep alives will
	 * sometimes be avoided if the response entity fits in an internal buffer.
	 * 
	 * <p>
	 * Entity data written for a HEAD request is ignored. Servlet writers can,
	 * as a simple performance optimization, omit writing response data for HEAD
	 * methods. If no response data is to be written, then the content length
	 * field must be set explicitly.
	 * 
	 * <P>
	 * The GET operation is expected to be safe: without any side effects for
	 * which users might be held responsible. For example, most form queries
	 * have no side effects. Requests intended to change stored data should use
	 * some other HTTP method. (There have been cases of significant security
	 * breaches reported because web-based applications used GET
	 * inappropriately.)
	 * 
	 * <P>
	 * The GET operation is also expected to be idempotent: it can safely be
	 * repeated. This is not quite the same as being safe, but in some common
	 * examples the requirements have the same result. For example, repeating
	 * queries is both safe and idempotent (unless payment is required!), but
	 * buying something or modifying data is neither safe nor idempotent.
	 * 
	 * @param req
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param resp
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 * 
	 * @exception IOException
	 *                if detected when handling the request
	 * @exception ServletException
	 *                if the request could not be handled
	 * 
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		performBasePreTask(req, res);
	}

	/**
	 * 
	 * Performs the HTTP POST operation; the default implementation reports an
	 * HTTP BAD_REQUEST error. Servlet writers who override this method should
	 * read any data from the request (for example, form parameters), set entity
	 * headers in the response, access the writer or output stream and, finally,
	 * write any response data using the servlet output stream. The headers that
	 * are set should include content type, and encoding. If a writer is to be
	 * used to write response data, the content type must be set before the
	 * writer is accessed. In general, the servlet implementor must write the
	 * headers before the response data because the headers can be flushed at
	 * any time after the data starts to be written.
	 * 
	 * <p>
	 * If HTTP/1.1 chunked encoding is used (that is, if the transfer-encoding
	 * header is present), then the content-length header should not be set. For
	 * HTTP/1.1 communications that do not use chunked encoding and HTTP 1.0
	 * communications, setting content length allows the servlet to take
	 * advantage of HTTP "connection keep alive". For just such communications,
	 * if content length can not be set, the performance penalties associated
	 * with not using keep alives will sometimes be avoided if the response
	 * entity fits in an internal buffer.
	 * 
	 * <P>
	 * This method does not need to be either "safe" or "idempotent". Operations
	 * requested through POST can have side effects for which the user can be
	 * held accountable. Specific examples including updating stored data or
	 * buying things online.
	 * 
	 * @param req
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param resp
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 * 
	 * @exception IOException
	 *                if detected when handling the request
	 * @exception ServletException
	 *                if the request could not be handled
	 * 
	 * @see javax.servlet.ServletResponse#setContentType
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		performBasePreTask(req, res);
	}

	/**
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param res
	 *            HttpServletResponse
	 */
	protected void performBasePreTask(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {

		req.setCharacterEncoding(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET.name());
		
		String traceLogBaseMsg = null;
		long start = 0, end = 0;
		if (JDF_SERVLET_TRACE) {
			// logMsg = req.getRequestURI() + ":" + req.getRemoteHost() + "(" +
			// req.getRemoteAddr() + ")";
			StringBuilder traceLogBaseMsgBuilder = new StringBuilder(
					req.getRequestURI()).append(":")
					.append(req.getRemoteHost()).append("(")
					.append(req.getRemoteAddr()).append(")");

			String user = req.getRemoteUser();
			if (user != null) {
				// logMsg += ":" + user;
				traceLogBaseMsgBuilder.append(":").append(user);
			}

			traceLogBaseMsg = traceLogBaseMsgBuilder.toString();

			start = System.currentTimeMillis();
			log.info("{}:calling", traceLogBaseMsg);
		}
		
		
		String siteLeftMenuURL = req.getRequestURI();
		
		for (String boardBaseURL : CommunityLeftMenuInfo.BOARD_LEFTMENU_BASE_URL_LIST) {
			if (siteLeftMenuURL.equals(boardBaseURL)) {
				short boardID;
				String parmBoardId = req.getParameter("boardId");
				if (null == parmBoardId) {
					break;
				}
				try {
					boardID = Short.parseShort(parmBoardId);
				}catch (NumberFormatException nfe) {
					break;
				}
				
				try {
					BoardType.valueOf(boardID);
				} catch(IllegalArgumentException e) {
					break;
				}
				siteLeftMenuURL = new StringBuilder(siteLeftMenuURL)
						.append("?boardId=")
						.append(parmBoardId).toString();
			}
		}
			
		setSiteLeftMenu(req, siteLeftMenuURL);
			
			
		
		
		
		try {
			performPreTask(req, res);
		} catch (Exception | java.lang.Error e) {
			log.warn("unknown error", e);
			e.printStackTrace();

			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.PrintWriter writer = new java.io.PrintWriter(bos);
			e.printStackTrace(writer);
			writer.flush();

			String errorMessgae = bos.toString();

			try {
				writer.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// String errString = "Programmer's Exception: " +logMsg +
			// CommonStaticFinal.NEWLINE + bos.toString();
			// Logger.err.println(this, errString);
			String debugMessage = new StringBuilder("Programmer's Exception: ")
					.append(traceLogBaseMsg)
					.append(CommonStaticFinalVars.NEWLINE).append(errorMessgae)
					.toString();

			printErrorMessagePage(req, res, "에러가 발생하였습니다",
					debugMessage);
		}

		if (JDF_SERVLET_TRACE) {
			end = System.currentTimeMillis();
			// Logger.sys.println(this, logMsg + ":end(elapsed=" + (end-start) +
			// ")" + CommonStaticFinal.NEWLINE);
			log.info(new StringBuilder(traceLogBaseMsg).append(":end(elapsed=")
					.append((end - start)).append(")")
					.append(CommonStaticFinalVars.NEWLINE).toString());
		}
	}

	/**
	 * get, post 모드에 상관없이 하나로 보게 해주는 메소드.
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param res
	 *            HttpServletResponse
	 */
	protected abstract void performPreTask(HttpServletRequest req,
			HttpServletResponse res) throws Exception;

	/**
	 * Sends a temporary redirect response to the client using the specified
	 * redirect location URL. The URL must be absolute (for example,
	 * <code><em>https://hostname/path/file.html</em></code>). Relative URLs are
	 * not permitted here.
	 * 
	 * @param req
	 *            javax.servlet.http.HttpServletRequest
	 * @param res
	 *            javax.servlet.http.HttpServletResponse
	 * @param location
	 *            the redirect location URL
	 * @exception IOException
	 *                If an I/O error has occurred.
	 */
	protected void printHtmlPage(HttpServletRequest req,
			HttpServletResponse res, String location) {
		try {
			res.sendRedirect(location);
		} catch (IOException e) {
			log.warn("fail to call method sendRedirect", e);

			StringBuilder debugMessageBuilder = new StringBuilder(
					"IOException::File Not Found, location=").append(location);
			printErrorMessagePage(req, res,
					" 에러가 발생하여 서블릿 정적 페이지 이동이 실패하였습니다.",
					debugMessageBuilder.toString());
		}
	}

	/**
	 * Sends a temporary redirect response to the client using the specified
	 * redirect location of jsp file. The URL must be absolute (for example,
	 * <code><em>/example/result.jsp</em></code>). Relative URLs are not
	 * permitted here.
	 * 
	 * @param req
	 *            javax.servlet.http.HttpServletRequest
	 * @param res
	 *            javax.servlet.http.HttpServletResponse
	 * @param jspfile
	 *            the redirect location URL of jsp file.
	 */
	protected void printJspPage(HttpServletRequest req,
			HttpServletResponse res, String jspfile) {
		try {
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(jspfile);

			dispatcher.forward(req, res);
		} catch (Exception |  Error e) {
			log.warn("fail to call method forward", e);

			try {
				java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
				java.io.PrintWriter writer = new java.io.PrintWriter(bos);
				writer.print("JSP Call Error: ");
				writer.println(this.getClass().getName());

				writer.print("Request URI: ");
				writer.println(req.getRequestURI());

				String user = req.getRemoteUser();
				if (user != null) {
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

				String debugMessage = bos.toString();
				
				
				try {
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				res.setContentType("text/html;charset=UTF-8");
				java.io.PrintWriter out = null;
				try {
					out = res.getWriter();
					out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
					out.print("<title>");
					out.print(WebCommonStaticFinalVars.WEBSITE_TITLE);
					out.print("</title>");
					out.println("</head><body bgcolor=white>");
					out.println(HtmlStringUtil.toHtml4BRString(debugMessage));
					out.println("</body></html>");				
				} finally {
					try {
						if (null != out) out.close();
					} catch(Exception e1) {
						log.warn("서블릿 동적 페이지 이동 PrintWriter 닫기 실패", e1);
					}
				}
			} catch (Exception ex) {
				log.warn("unkwon error", ex);
			}
		}
	}
	
	/**
	 * 웹사이트 레이아웃을 가지며 지정한 본문 jsp 파일을 출력한다.
	 * 
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param res javax.servlet.http.HttpServletResponse
	 * @param bodyJspfile
	 *//*
	protected void printWebLayoutControlJspPage(HttpServletRequest req,
			HttpServletResponse res, String bodyJspfile) {
		try {

			req.setAttribute("bodyurl", bodyJspfile);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(WEB_LAYOUT_CONTROL_PAGE);

			dispatcher.forward(req, res);
		} catch (Exception |  Error e) {
			log.warn("서블릿 동적 페이지 이동 에러", e);

			try {
				java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
				java.io.PrintWriter writer = new java.io.PrintWriter(bos);
				writer.print("JSP Call Error: ");
				writer.println(this.getClass().getName());

				writer.print("Request URI: ");
				writer.println(req.getRequestURI());

				String user = req.getRemoteUser();
				if (user != null) {
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

				String debugMessage = bos.toString();
				
				
				try {
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				res.setContentType("text/html;charset=UTF-8");
				java.io.PrintWriter out = null;
				try {
					out = res.getWriter();
					out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
					out.print("<title>");
					out.print(WebCommonStaticFinalVars.WEBSITE_TITLE);
					out.print("</title>");
					out.println("</head><body bgcolor=white>");
					out.println(HtmlStringUtil.toHtml4BRString(debugMessage));
					out.println("</body></html>");				
				} finally {
					try {
						if (null != out) out.close();
					} catch(Exception e1) {
						log.warn("서블릿 동적 페이지 이동 PrintWriter 닫기 실패", e1);
					}
				}
			} catch (Exception ex) {
				log.warn("서블릿 동적 페이지 이동 에러 처리 실패", ex);
			}
		}
	}*/

	/**
	 * <pre>
	 * 이 Method는 반드시 프로젝트에서 구현해서 사용해야 함.
	 * 왜냐면, 프로젝트마다 Message를 보여주는 화면이 다를 수 있기 때문.
	 * 
	 * </pre>
	 * 
	 * @param req
	 *            javax.servlet.http.HttpServletRequest
	 * @param res
	 *            javax.servlet.http.HttpServletResponse
	 * @param userMessage
	 *            최종 사용자에게 보여질 메세지
	 * @param debugMessage
	 *            개발시점에서 개발자가 Debugging을 위해 보는 메세지, 통상 운영시는 보이지 않도록 함.
	 */
	protected abstract void printErrorMessagePage(HttpServletRequest req,
			HttpServletResponse res, String userMessage, String debugMessage);	
}
