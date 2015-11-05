<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%

	String userMessage = (String)request.getAttribute("userMessage");
	if (null == userMessage) userMessage="";
	String debugMessage = (String)request.getAttribute("debugMessage");
	
%>
userMessage=[<%=HtmlStringUtil.toHtml4BRString(userMessage)%>]<br/><%if (null != debugMessage) {%>
debugMessage=[<%=HtmlStringUtil.toHtml4BRString(debugMessage)%>]<br/><%
	}
%>
<a href="/">HOME</a>
