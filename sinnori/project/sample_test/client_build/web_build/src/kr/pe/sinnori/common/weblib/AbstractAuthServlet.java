package kr.pe.sinnori.common.weblib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class AbstractAuthServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {		
		if (!isLogin(req)) {
			String errorMessage = "로그인 서비스 입니다. 로그인후 이용 하시기바랍니다.";
			log.warn(errorMessage);
			
			printMessagePage(req, res, errorMessage, null);
			return;
		}
		
		super.performPreTask(req, res);
	}
}
