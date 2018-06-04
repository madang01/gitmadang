package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractAdminServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_USERID);
		if (null == userId || userId.equals("")) {
			printJspPage(req, res, JDF_LOGIN_PAGE);
			return;
		}
		
		super.performPreTask(req, res);
	}
}