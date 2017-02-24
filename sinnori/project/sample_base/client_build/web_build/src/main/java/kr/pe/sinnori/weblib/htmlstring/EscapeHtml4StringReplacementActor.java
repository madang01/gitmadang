package kr.pe.sinnori.weblib.htmlstring;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscapeHtml4StringReplacementActor implements AbstractStringReplacementActor {

	@Override
	public String replace(String sourceString) {
		String ret = StringEscapeUtils.escapeHtml4(sourceString);
		return ret;
	}


}
