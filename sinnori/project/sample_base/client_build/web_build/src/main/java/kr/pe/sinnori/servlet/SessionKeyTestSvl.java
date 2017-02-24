package kr.pe.sinnori.servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.weblib.jdf.AbstractSessionKeyServlet;


@SuppressWarnings("serial")
public class SessionKeyTestSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/menu/testcode/SessionKeyTest01.jsp";
		
		
		printWebLayoutControlJspPage(req, res, goPage);	
	}

}
