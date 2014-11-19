package kr.pe.sinnori.common.weblib;

public class Line2EmptyStringReplacer implements AbstractStringReplacer {

	@Override
	public String replace(String str) {
		String ret = str.replaceAll("\r\n|\n|\r|\u0085|\u2028|\u2029", "");
		return ret;
	}
}
