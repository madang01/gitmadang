package kr.pe.codda.servlet.user;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;


@SuppressWarnings("serial")
public class JDFSessionKeySvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String goPage = "/jsp/util/JDFSessionKey.jsp";
		printJspPage(req, res, goPage);	
	}
}
