package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.weblib.AbstractServlet;

@SuppressWarnings("serial")
public class FreeBoardWriteSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/board/FreeBoardWrite01.jsp";
		printJspPage(req, res, goPage);	
		
	}

}
