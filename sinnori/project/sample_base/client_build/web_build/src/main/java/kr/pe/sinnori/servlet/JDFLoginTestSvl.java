package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractLoginServlet;

@SuppressWarnings("serial")
public class JDFLoginTestSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.TEST_EXAMPLE);
		
		String goPage="/menu/testcode/JDFLoginTest01.jsp";
		
		printJspPage(req, res, goPage);			
	}

}
