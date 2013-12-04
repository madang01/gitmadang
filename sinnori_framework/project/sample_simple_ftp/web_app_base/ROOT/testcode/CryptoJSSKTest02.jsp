<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<jsp:useBean id="plainText" class="java.lang.String" scope="request" />
<jsp:useBean id="algorithm" class="java.lang.String" scope="request" />
<jsp:useBean id="privateKey" class="java.lang.String" scope="request" />
<jsp:useBean id="iv" class="java.lang.String" scope="request" />
<jsp:useBean id="encryptedBytes" class="java.lang.String" scope="request" />
<jsp:useBean id="decryptedBytes" class="java.lang.String" scope="request" />
<jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" />
<jsp:useBean id="resultMessage" class="java.lang.String" scope="request" />
<table>
	<tr><td colspan="2"><h3>CryptoJS 대칭키 테스트 결과 페이지</h3></td></tr>
	<tr>
	    <td>원문</td>
	    <td><%=StringEscapeUtils.escapeHtml(plainText)%></td> 
	</tr>
	<tr>
	    <td>선택한 대칭키 알고리즘</td>
	    <td><%=StringEscapeUtils.escapeHtml(algorithm)%></td> 
	</tr>
	<tr>
	    <td>개인키</td>
	    <td><%=privateKey%></td> 
	</tr>
	<tr>
	    <td>iv</td>
	    <td><%=iv%></td> 
	</tr>
	<tr>
	    <td>javascirpt 암호문</td>
	    <td><%=encryptedBytes%></td> 
	</tr>
	<tr>
	    <td>server 복호문 Hex</td>
	    <td><%=decryptedBytes%></td> 
	</tr>
	<tr>
	    <td>server 복호문</td>
	    <td><%=decryptedPlainText%></td> 
	</tr>
	<tr>
	    <td>비교결과</td>
	    <td><%=StringEscapeUtils.escapeHtml(resultMessage)%></td> 
	</tr>
</table>
