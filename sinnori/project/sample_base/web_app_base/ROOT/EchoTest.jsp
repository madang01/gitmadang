<%@page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%>
<%@page import="kr.pe.sinnori.common.message.AbstractMessage"%>
<%@page import="kr.pe.sinnori.impl.message.Echo.Echo"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="kr.pe.sinnori.client.ClientProject" %>
<%@page import="kr.pe.sinnori.client.ClientProjectManager" %>
<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	String errorMessage = null;
	long erraseTime = 0;
	boolean isSuccess=false;

	java.util.Random random = new java.util.Random();
	
	Logger log = LoggerFactory
			.getLogger("kr.pe.sinnori.test");
	
	// System.out.printf("11111111111\n");
	
	log.info("2222222222");
	
	String projectName = "sample_simple_chat";
	ClientProject clientProject = ClientProjectManager.getInstance().getMainClientProject();
	
	Echo echoInObj = new Echo();
	echoInObj.setRandomInt(random.nextInt());
	echoInObj.setStartTime(new java.util.Date().getTime());
	
	AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(echoInObj);
	
	boolean isSame = false;
	
	if (messageFromServer instanceof Echo) {
		Echo echoOutObj = (Echo)messageFromServer;
		
		erraseTime = new java.util.Date().getTime() - echoInObj.getStartTime();
		
		if ((echoOutObj.getRandomInt() == echoInObj
				.getRandomInt())
				&& (echoOutObj.getStartTime() == echoInObj.getStartTime())) {
			isSame = true;
			//log.info("성공::echo 메시지 입력/출력 동일함");
		} else {
			isSame = false;
			// log.info("실패::echo 메시지 입력/출력 다름");
		}
	} else {
		errorMessage = messageFromServer.toString();
		log.warn(errorMessage);
	}
	
%><!DOCTYPE html>
<html>
<head>
<title><jsp:include page="/title.html" /></title>
</head>
<body style="width:800px;">
<header style="height:30px; width:800px;"></header>
<table border="1">
<tr>
	<td colspan=2 style="text-align:center">Echo 검사</td>
</tr>
<tr>
	<td colspan=2 style="text-align:left">
	클라이언트에서 생성한 랜덤수를 갖는 에코 입력 메세지를
	그대로 출력 메세지로 받게 되는지를 검사한다.<br/>
	서버에서는 에코 입력 메세지를 그대로 출력 메세지로 복사한다.<br/>
	이 테스트를 얻게 되는 효과는 쓰레드 세이프 검증이다.<br/>
	만약 서버에서 입력메세지 처리시 쓰레드 세이프하지 않다면
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
	<td style="text-align:right"><%= echoInObj.getRandomInt() %></td>
</tr>
<tr>
	<td style="text-align:left">the number of milliseconds since January 1, 1970, 00:00:00<br/>GMT represented</td>
	<td style="text-align:right"><%= echoInObj.getStartTime() %></td>
</tr><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
<tr>
	<td colspan=2><%=HtmlStringUtil.toHtml4BRString(errorMessage)%></td>
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
</body>
</html>

