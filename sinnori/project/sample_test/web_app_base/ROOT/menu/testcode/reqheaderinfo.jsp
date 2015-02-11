<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="reqHeaderInfo" class="kr.pe.sinnori.impl.javabeans.ReqHeaderInfoBean" scope="request" /><%
	// ReqHeaderInfoOutputMessage reqHeaderInfo = (ReqHeaderInfoOutputMessage)session.getAttribute("ReqHeaderInfoOutputMessage");
	if (null == reqHeaderInfo) {
%>reqHeaderInfo is null<%		
	} else {
%>
title=[<%=reqHeaderInfo.title%>]<br/>
headerInfoSize=[<%=reqHeaderInfo.headerInfoSize%>]<br/>
<%
		for (int i=0; i < reqHeaderInfo.headerInfoSize; i++) {
			String headerKey = reqHeaderInfo.headerInfoList[i].headerKey;
			String headerValue = reqHeaderInfo.headerInfoList[i].headerValue;
%>
index[<%=i%>].headerKey=[<%=escapeHtml(headerKey, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/>
index[<%=i%>].headerValue=[<%=escapeHtml(headerValue, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/><br/><%
		}
	}
%>
