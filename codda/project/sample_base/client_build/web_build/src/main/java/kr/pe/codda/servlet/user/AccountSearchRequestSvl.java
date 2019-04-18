package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;

public class AccountSearchRequestSvl extends AbstractServlet {
	
	private static final long serialVersionUID = 210519591910597864L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		printJspPage(req, res, "/jsp/member/AccountSearchRequest.jsp");
	}

}
