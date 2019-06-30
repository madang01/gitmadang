package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class BoardManagerSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -8949718293331700735L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		printJspPage(req, res, "/jsp/board/BoardManager.jsp");
	}

}
