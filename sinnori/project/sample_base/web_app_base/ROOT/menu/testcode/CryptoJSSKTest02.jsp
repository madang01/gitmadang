<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="plainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="algorithm" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="privateKey" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="iv" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="encryptedBytesHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="plainTextHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="decryptedBytesHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="resultMessage" class="java.lang.String" scope="request" />
<table>
	<tr><td colspan="2"><h3>CryptoJS 대칭키 테스트 결과 페이지</h3></td></tr>
	<tr>
	    <td>원문</td>
	    <td><%= escapeHtml(plainText, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
	<tr>
	    <td>선택한 대칭키 알고리즘</td>
	    <td><%= escapeHtml(algorithm, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
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
	    <td><%=encryptedBytesHex%></td> 
	</tr>
	<tr>
	    <td>원문 Hex</td>
	    <td><%=plainTextHex%></td> 
	</tr>
	<tr>
	    <td>server 복호문 Hex</td>
	    <td><%=decryptedBytesHex%></td> 
	</tr>
	<tr>
	    <td>server 복호문</td>
	    <td><%=escapeHtml(decryptedPlainText, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
	<tr>
	    <td>비교결과</td>
	    <td><%= escapeHtml(resultMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td> 
	</tr>
</table>
