<%@page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%>
<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="allDataTypeInObj" class="kr.pe.sinnori.impl.message.AllDataType.AllDataType" scope="request" /><%
%><jsp:useBean id="allDataTypeOutObj" class="kr.pe.sinnori.impl.message.AllDataType.AllDataType" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%	
	Boolean isSame = (Boolean) request.getAttribute("isSame");
	
	// errorMessage = "test error message";
	
%>
<table border="1">
<tr>
	<td colspan="2" style="text-align:center">네트워크 통신상 모든 데이타 타입 검사</td>
</tr>
<tr>
	<td colspan="2" style="text-align:center">
	서버와 클라이 언트 사이에 모든 데이터 타입별 최대/최소/중간값이 잘 전달되는지를 검사한다.<br/>
	서버에서는 입력 메세지를 그대로 복사하여 전달한다.  
	</td>
</tr>
<tr>
	<td colspan=2 style="text-align:center">AllDataType 입력메세지</td>
</tr>
<tr>
	<td colspan=2 style="text-align:left"><%=HtmlStringUtil.toHtml4BRString(allDataTypeInObj.toString())%></td>
</tr><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
<tr>
	<td colspan="2"><%=errorMessage%></td>
</tr><%
	} else {
%>
<tr>
	<td style="text-align:center">출력 비교 결과</td><%
		if (isSame) {
	%><td style="color:blue;text-align:center">성공</td><%
		} else {
	%><td style="color:red;text-align:center">실패</td><%
		}
	%>
</tr>

<tr>
	<td colspan=2 style="text-align:center">AllDataType 출력메세지</td>
</tr>
<tr>
	<td colspan=2 style="text-align:left"><%=HtmlStringUtil.toHtml4BRString(allDataTypeOutObj.toString())%></td>
</tr><%
	}
%>
</table>

