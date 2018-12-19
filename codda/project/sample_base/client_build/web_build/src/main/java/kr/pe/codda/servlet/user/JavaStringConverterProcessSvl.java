package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class JavaStringConverterProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = 4617349128846475309L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String paramSourceString = req.getParameter("sourceString");
		
		log.info("paramSourceString={}", paramSourceString);
		
		
		if (null == paramSourceString) {
			String errorMessage = "자바 문자열로 변환을 원하는 문자열을 넣어 주세요";
			String debugMessage = "the web parameter 'sourceString' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		String[] sourceLines = paramSourceString.split("(\r\n|\r|\n|\n\r)");
		
		StringBuilder targetStringBuilder = new StringBuilder();
		
		targetStringBuilder.append("StringBuilder stringBuilder = new StringBuilder();");
		targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		
		for (String sourceLine : sourceLines) {
			targetStringBuilder.append("stringBuilder.append(\"");
			targetStringBuilder.append(StringEscapeUtils.escapeJava(sourceLine));
			targetStringBuilder.append("\");");
			targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			targetStringBuilder.append("stringBuilder.append(");
			targetStringBuilder.append("System.getProperty(\"line.separator\")");
			targetStringBuilder.append(");");
			targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		}
		
		targetStringBuilder.append("return stringBuilder.toString();");		
		
		req.setAttribute("sourceString", paramSourceString);
		req.setAttribute("targetString", targetStringBuilder.toString());
		printJspPage(req, res, "/jsp/util/JavaStringConverterProcess.jsp");		
	}
}
