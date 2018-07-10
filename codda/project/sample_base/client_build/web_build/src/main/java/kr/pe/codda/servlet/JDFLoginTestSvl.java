package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractLoginServlet;

@SuppressWarnings("serial")
public class JDFLoginTestSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		String goPage="/menu/testcode/JDFLoginTest01.jsp";
		
		printJspPage(req, res, goPage);			
	}

}
