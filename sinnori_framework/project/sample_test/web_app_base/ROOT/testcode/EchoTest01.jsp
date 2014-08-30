<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %><%

	kr.pe.sinnori.common.message.InputMessage echoInObj = 
		(kr.pe.sinnori.common.message.InputMessage)request.getAttribute("echoInObj");
		
	String erraseTime = (String)request.getAttribute("erraseTime");
	Boolean isSame = (Boolean)request.getAttribute("isSame");
	
	
	Integer randomInt = (Integer)echoInObj.getAttribute("randomInt");
	Long startTime = (Long)echoInObj.getAttribute("startTime");
	
	String errorMessage = (String) request.getAttribute("errorMessage");
	
	// errorMessage = "test error message";
	

%>
<table border="1">
<tr>
	<td colspan=2 style="text-align:center">Echo 검사</td>
</tr>
<tr>
	<td colspan=2 style="text-align:left">
	클라이언트에서 생성한 랜덤수를 갖는 에코 입력 메세지를<br/>
	그대로 출력 메세지로 받게 되는지를 검사한다.<br/>
	서버에서는 에코 입력 메세지를 그대로 출력 메세지로 복사한다.<br/>
	이 테스트를 얻게 되는 효과는 쓰레드 세이프 검증이다.<br/>
	만약 서버에서 입력메세지 처리시 쓰레드 세이프하지 않다면<br/>
	클라이언트로 보내는 값은 원래 보낸 데이터와 다르게 된다.
	</td>
</tr>
<tr>
	<td colspan=2 style="text-align:center">Echo 입력메세지</td>
</tr>
<tr>
	<td style="text-align:center">항목</td>
	<td style="text-align:center">값</td>
</tr>
<tr>
	<td style="text-align:left">랜덤 32bit 부호화 정수</td>
	<td style="text-align:right"><%= randomInt %></td>
</tr>
<tr>
	<td style="text-align:left">the number of milliseconds<br/>since January 1, 1970, 00:00:00<br/>GMT represented</td>
	<td style="text-align:right"><%= startTime %></td>
</tr><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
<tr>
	<td colspan=2><%= errorMessage %></td>
</tr><%	
	} else {
%>
<tr>
	<td style="text-align:left">경과시간(milliseconds)</td>
	<td style="text-align:right"><%=erraseTime%> ms</td>
</tr>
<tr>
	<td style="text-align:left">출력 비교 결과</td><%
		if (isSame) {
%><td style="color:blue;text-align:center">성공</td><%
		} else {
%><td style="color:red;text-align:center">실패</td><%
		}
%>
</tr><%
	}
%>
</table>

