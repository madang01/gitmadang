package kr.pe.sinnori.weblib.htmlstring;

import org.apache.commons.text.StringEscapeUtils;

public class EscapeEcmaScriptStringReplacementActor implements AbstractStringReplacementActor {

	@Override
	public String replace(String sourceString) {
		String ret = StringEscapeUtils.escapeEcmaScript(sourceString);
		return ret;
	}
}
