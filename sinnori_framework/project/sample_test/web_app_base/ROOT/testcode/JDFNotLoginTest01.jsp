<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:useBean id="reqHeaderInfo" class="kr.pe.sinnori.impl.javabeans.ReqHeaderInfoBean" scope="request" />
<h3>JDF 테스트 - 비 로그인</h3>
<%
	
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
index[<%=i%>].headerKey=[<%=headerKey%>]<br/>
index[<%=i%>].headerValue=[<%=headerValue%>]<br/><br/><%
		}
	}
%>
