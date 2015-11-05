package kr.pe.sinnori.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class AbstractAuthServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {		
		if (!isLogin(req)) {			
			printJspPage(req, res, JDF_LOGIN_PAGE);
			return;
		}
		
		super.performPreTask(req, res);
	}
}
