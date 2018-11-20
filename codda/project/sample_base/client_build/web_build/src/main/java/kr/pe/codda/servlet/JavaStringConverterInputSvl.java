package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;

@SuppressWarnings("serial")
public class JavaStringConverterInputSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		printJspPage(req, res, "/jsp/util/JavaStringConverterInput.jsp");
	}
}
