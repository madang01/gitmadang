<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars" %><%
%><%@ page import="org.apache.commons.lang.StringEscapeUtils" %><%
%><jsp:useBean id="boardListResponseOutObj" class="kr.pe.sinnori.impl.message.BoardListResponse.BoardListResponse" scope="request" /><%
%>
<%= boardListResponseOutObj.toString() %>
