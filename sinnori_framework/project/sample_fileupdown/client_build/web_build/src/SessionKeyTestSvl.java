import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.servlet.AbstractSessionKeyServlet;

@SuppressWarnings("serial")
public class SessionKeyTestSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/testcode/SessionKeyTest01.jsp";
		
		
		printJspPage(req, res, goPage);	
	}

}
