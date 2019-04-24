package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;

public class MemberWithdrawRequestSvl extends AbstractUserLoginServlet {
	
	private static final long serialVersionUID = 1773282625699468262L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		printJspPage(req, res, "/jsp/my/MemberWithdrawRequest.jsp");
	}

}
