package kr.pe.sinnori.common.protocol.djson.header;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.lib.CharsetUtil;


public class DJSONHeader { 
	public static final String JSON_STRING_CHARSET_NAME = "UTF-8";
	public static final Charset JSON_STRING_CHARSET = CharsetUtil.getCharset(JSON_STRING_CHARSET_NAME);
	
	public int lenOfJSONStr;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DJSONHeader [lenOfJSONStr=");
		builder.append(lenOfJSONStr);
		builder.append("]");
		return builder.toString();
	}
}
