<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="plainText" class="java.lang.String" scope="request" />
<jsp:useBean id="javascriptMDHex" class="java.lang.String" scope="request" />
<jsp:useBean id="serverMDHex" class="java.lang.String" scope="request" />
<jsp:useBean id="resultMessage" class="java.lang.String" scope="request" />
<table>
	<tr><td colspan="2"><h3>CryptoJS 해시 알고리즘(=메세지 다이제스트) 테스트 결과 페이지</h3></td></tr>
	<tr>
	    <td>원문</td>
	    <td><%=StringEscapeUtils.escapeHtml(plainText)%></td> 
	</tr>
	<tr>
	    <td>javascript Diagst Message</td>
	    <td><%=StringEscapeUtils.escapeHtml(javascriptMDHex)%></td> 
	</tr>
	<tr>
	    <td>server Diagst Message</td>
	    <td><%=serverMDHex%></td> 
	</tr>
	<tr>
	    <td>비교결과</td>
	    <td><%=StringEscapeUtils.escapeHtml(resultMessage)%></td> 
	</tr>
</table>
