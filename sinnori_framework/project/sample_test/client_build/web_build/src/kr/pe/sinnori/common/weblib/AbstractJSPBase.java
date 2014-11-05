package kr.pe.sinnori.common.weblib;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.HttpJspPage;

import org.apache.commons.lang3.StringEscapeUtils;


@SuppressWarnings("serial")
public abstract class AbstractJSPBase extends HttpServlet implements HttpJspPage {
	private ServletConfig config;

	// Initialise global variables
	@Override
	final public void init(ServletConfig config) throws ServletException {

		this.config = config;
		jspInit();

	}

	// provide accessor to the ServletConfig object
	@Override
	final public ServletConfig getServletConfig() {

		return config;

	}

	// Provide simple service method that calls the generated _jspService method
	@Override
	final public void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		_jspService(request, response);
	}

	// Create an abstract method that will be implemented by the JSP processor
	// in the subclass
	abstract public void _jspService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	// Provide a destroy method
	@Override
	final public void destroy() {
		jspDestroy();
	}

	// provide some utility methods
	public String getUser(HttpServletRequest request) {
		// get the user name from the request
		return (String) request.getParameter("user");
	}

	public String getCompany(HttpServletRequest request) {
		// get the user name from the request
		return (String) request.getParameter("company");
	}

	@Override
	public String getServletInfo() {

		return new String("PureJSPBase");
	}

	@Override
	public void jspDestroy() {		
		
	}

	@Override
	public void jspInit() {		
	}
	
	public String escapeHtml(String str, boolean isLine2Br) {
		if (null == str)
			return "";
		String ret = StringEscapeUtils.escapeHtml4(str);
		if (isLine2Br)
			ret.replaceAll("\r\n|\n", "<br/>");
		return ret;
	}

	public boolean isLogin(HttpSession session) {
		String userId = (String) session
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}

}
