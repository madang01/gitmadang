<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="orignalPlainText" class="java.lang.String" scope="request" />
<jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" />
<jsp:useBean id="resultMessage" class="java.lang.String" scope="request" />
<table>
	<tr><td colspan="2"><h3>RSA 암/복호화 테스트 결과 페이지</h3></td></tr>
	<tr>
	    <td>원문</td>
	    <td><%=StringEscapeUtils.escapeHtml(orignalPlainText)%></td> 
	</tr>
	<tr>
	    <td>복호문</td>
	    <td><%=StringEscapeUtils.escapeHtml(decryptedPlainText)%></td> 
	</tr>
	<tr>
	    <td>비교결과</td>
	    <td><%=StringEscapeUtils.escapeHtml(resultMessage)%></td> 
	</tr>
</table>
