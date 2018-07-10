package kr.pe.codda.servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;


@SuppressWarnings("serial")
public class SessionKeyTestSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String goPage = "/menu/testcode/SessionKeyTest01.jsp";
		
		
		printJspPage(req, res, goPage);	
	}

}
