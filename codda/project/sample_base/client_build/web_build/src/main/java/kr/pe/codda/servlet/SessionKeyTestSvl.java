package kr.pe.codda.servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;


@SuppressWarnings("serial")
public class SessionKeyTestSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				SiteTopMenuType.TEST_EXAMPLE);
		
		String goPage = "/menu/testcode/SessionKeyTest01.jsp";
		
		
		printJspPage(req, res, goPage);	
	}

}
