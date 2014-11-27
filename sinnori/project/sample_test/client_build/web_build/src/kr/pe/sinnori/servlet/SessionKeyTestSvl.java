package kr.pe.sinnori.servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.weblib.AbstractSessionKeyServlet;


@SuppressWarnings("serial")
public class SessionKeyTestSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/testcode/SessionKeyTest01.jsp";
		
		
		printWebLayoutControlJspPage(req, res, goPage);	
	}

}
