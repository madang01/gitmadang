package kr.pe.codda.weblib.jdf;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractAdminLoginServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		if (! isAdminLogin(req)) {
			String requestURI = req.getRequestURI();
			
			ArrayList<Map.Entry<String, String>> parameterEntryList = new ArrayList<Map.Entry<String, String>>(); 
			
			Enumeration<String> parmeterKeyEnumeration = req.getParameterNames();
			
			while (parmeterKeyEnumeration.hasMoreElements()) {
				String parameterKey = parmeterKeyEnumeration.nextElement();				
				String parameterValue = req.getParameter(parameterKey);
				
				Map.Entry<String, String> parameterEntry = new AbstractMap.SimpleEntry<String, String>(parameterKey, parameterValue); 
				
				parameterEntryList.add(parameterEntry);
			}
			
			LoginRequestPageInformation loginRequestPageInformation = new LoginRequestPageInformation(requestURI, parameterEntryList);
			
			HttpSession httpSession = req.getSession();			
			httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_REQUEST_PAGE_INFORMATION, loginRequestPageInformation);
						
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
			
			
			// req.setAttribute("queryString", URLEncoder.encode(parameterStringBuilder.toString(), "UTF-8"));
			
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
					webServerSessionkey.getModulusHexStrForWeb());
			printJspPage(req, res, JDF_LOGIN_PAGE);
			return;
		} else {
			super.performPreTask(req, res);
		}
	}
}
