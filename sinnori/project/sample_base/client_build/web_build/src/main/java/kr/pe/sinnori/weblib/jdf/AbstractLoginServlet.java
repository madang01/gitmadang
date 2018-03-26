package kr.pe.sinnori.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType;

@SuppressWarnings("serial")
public abstract class AbstractLoginServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		if (! isLogin(req)) {			
			setSiteTopMenuRequestAtrributeMatchingTopMenuParameter(req, SiteTopMenuType.INTRODUCE);
			
			String requestURI = req.getRequestURI();			
			// log.info("requestURI={}", requestURI);
			
			
			ServerSessionkeyIF webServerSessionkey  = null;
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
				printMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
					
			String modulusHexString = webServerSessionkey.getModulusHexStrForWeb();

			req.setAttribute("successURL", requestURI);
			req.setAttribute("modulusHexString", modulusHexString);
			printJspPage(req, res, JDF_LOGIN_PAGE);
			return;
		} else {
			super.performPreTask(req, res);
		}
	}
}
