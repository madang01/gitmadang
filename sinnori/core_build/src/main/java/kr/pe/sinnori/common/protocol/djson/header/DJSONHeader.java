package kr.pe.sinnori.common.protocol.djson.header;

import java.nio.charset.Charset;

import kr.pe.sinnori.common.etc.CharsetUtil;


public class DJSONHeader { 
	public static final String JSON_STRING_CHARSET_NAME = "UTF-8";
	public static final Charset JSON_STRING_CHARSET = CharsetUtil.getCharset(JSON_STRING_CHARSET_NAME);
	
	public static final int MESSAGE_HEADER_SIZE = 4;
	
	private int bodySize;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DJSONHeader [bodySize=");
		builder.append(bodySize);
		builder.append("]");
		return builder.toString();
	}
	
	public int getBodySize() {
		return bodySize;
	}
	
	public void setBodySize(int bodySize) {
		this.bodySize = bodySize;
	}
}
