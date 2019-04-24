package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;


public class JDFLoginSvl extends AbstractUserLoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6405661363137104276L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		String goPage="/jsp/util/JDFLogin.jsp";
		
		printJspPage(req, res, goPage);			
	}

}
