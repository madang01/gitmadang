package kr.pe.codda.weblib.htmlstring;


public class Line2BrStringReplacementActor implements AbstractStringReplacementActor {

	@Override
	public String replace(String str) {
		String ret = str.replaceAll("\r\n|\n|\r|\u0085|\u2028|\u2029", "<br/>");
		return ret;
	}
}
