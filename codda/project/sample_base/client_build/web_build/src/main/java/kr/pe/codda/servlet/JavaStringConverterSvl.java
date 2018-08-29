package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

@SuppressWarnings("serial")
public class JavaStringConverterSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String paramRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		
		
		if (null == paramRequestType || paramRequestType.equals("input")) {
			inputPage(req, res);
			return;
		} else if (paramRequestType.equals("proc")) {		
			resultPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(paramRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
	
	private void inputPage(HttpServletRequest req, HttpServletResponse res) {
		printJspPage(req, res, "/jsp/util/JavaStringConverterInput.jsp");
	}
	
	private void resultPage(HttpServletRequest req, HttpServletResponse res) {
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
		printJspPage(req, res, "/jsp/util/JavaStringConverterResult.jsp");
	}
}
