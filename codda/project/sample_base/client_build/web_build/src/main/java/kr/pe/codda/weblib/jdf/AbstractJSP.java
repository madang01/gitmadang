package kr.pe.codda.weblib.jdf;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;


import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;


@SuppressWarnings("serial")
public abstract class AbstractJSP extends AbstractBaseServlet implements HttpJspPage {	
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
	
	public String getCipheredBase64String(HttpServletRequest req, String painText) throws IllegalArgumentException, SymmetricException {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		
		if (null == painText) {
			throw new IllegalArgumentException("the parameter painText is null");
		}
		
		
		ServerSymmetricKeyIF webServerSymmetricKey = (ServerSymmetricKeyIF)req.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY);
		if (null == webServerSymmetricKey) {
			/*String errorMessage = new StringBuilder("the jsp request's attribute[")
					.append(WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY)
					.append("] doesn't exist").toString();*/
			log.warn("the jsp request's attribute[{}] doesn't exist", WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY);
			return "";
		}
		return Base64.getEncoder().encodeToString(webServerSymmetricKey.encrypt(painText.getBytes(CommonStaticFinalVars.CIPHER_CHARSET)));
	}
	
	protected String getParameterIVBase64Value(HttpServletRequest req) {
		String parmIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		if (null == parmIVBase64) {
			return "";
		}
		return parmIVBase64;
	}
	
	protected String getModulusHexString(HttpServletRequest req) {
		Object modulusHexStringValue = req.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING);
		if (null == modulusHexStringValue) {
			return "";
		}
		return (String)modulusHexStringValue;
	}
	
	protected String getGroupRequestURL(HttpServletRequest req) {		
		Object groupRequestURL = req.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL);
		
		if (null == groupRequestURL) {
			return "/";
		}
		return (String)groupRequestURL;
	}
	
	abstract public String getWebsiteMenuString(HttpServletRequest request);
}
