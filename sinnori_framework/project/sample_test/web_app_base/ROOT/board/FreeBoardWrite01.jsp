<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%!
	public String escapeHtml(String str) {
		return StringEscapeUtils.escapeHtml4(str);		
	}
%>