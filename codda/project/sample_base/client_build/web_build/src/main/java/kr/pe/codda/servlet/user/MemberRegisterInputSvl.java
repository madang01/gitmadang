package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;

public class MemberRegisterInputSvl extends AbstractServlet {

	private static final long serialVersionUID = -3208601159406678519L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		printJspPage(req, res, "/jsp/member/MemberRegisterInput.jsp");
	}
}