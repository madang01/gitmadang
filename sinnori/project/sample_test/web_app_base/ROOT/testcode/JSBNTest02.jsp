<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="orignalPlainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="resultMessage" class="java.lang.String" scope="request" />
<table>
	<tr><td colspan="2"><h3>RSA 암/복호화 테스트 결과 페이지</h3></td></tr>
	<tr>
	    <td>원문</td>
	    <td><%= escapeHtml(orignalPlainText, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
	<tr>
	    <td>복호문</td>
	    <td><%= escapeHtml(decryptedPlainText, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
	<tr>
	    <td>비교결과</td>
	    <td><%=escapeHtml(resultMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
</table>
