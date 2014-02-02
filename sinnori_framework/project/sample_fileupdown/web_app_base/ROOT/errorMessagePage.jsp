<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%!
    public String translate(String s) {
	if ( s == null ) return null;
	StringBuffer buf = new StringBuffer();

	char[] c = s.toCharArray();

	int len = c.length;

	for ( int i=0; i < len; i++) {
	    if      ( c[i] == '&' ) buf.append("&amp;");
	    else if ( c[i] == '<' ) buf.append("&lt;");
	    else if ( c[i] == '>' ) buf.append("&gt;");
	    else if ( c[i] == '"' ) buf.append("&quot;");
	    else if ( c[i] == '\'') buf.append("&#039;");
	    else buf.append(c[i]);

	}

	return buf.toString();

    }
%><%

	String user_msg = (String)request.getAttribute("user_msg");
	if (null == user_msg) user_msg="null";
	String debug_msg = (String)request.getAttribute("debug_msg");
	if (null == debug_msg) debug_msg="null";
%>
user_msg=[<%=translate(user_msg).replaceAll("(\r\n|\n)","<br/>\n")%>]<br/>
debug_msg=[<%=translate(debug_msg)%>]<br/>

call errorMessage.jsp<br/><br/>

<a href="/">HOME</a>
