<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="kr.pe.sinnori.client.ClientProject" %>
<%@page import="kr.pe.sinnori.client.ClientProjectManager" %>
<%@page import="kr.pe.sinnori.client.io.LetterListFromServer" %>
<%@page import="kr.pe.sinnori.common.lib.CommonRootIF" %>
<%@page import="kr.pe.sinnori.common.message.InputMessage" %>
<%@page import="kr.pe.sinnori.common.message.OutputMessage" %><%
	String errorMessage = null;
	long erraseTime = 0;
	boolean isSuccess=false;

	java.util.Random random = new java.util.Random();
	
	kr.pe.sinnori.common.util.LogManager log = kr.pe.sinnori.common.util.LogManager.getLogger();
	
	System.out.printf("11111111111\n");
	
	log.info("2222222222");
	
	String projectName = "sample_simple_chat";
	ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
	
	InputMessage echoInObj = null;
	echoInObj = clientProject.createInputMessage("Echo");
	
	
	echoInObj.setAttribute("mRandomInt", random.nextInt());
	echoInObj.setAttribute("mStartTime", new java.util.Date().getTime());

	LetterListFromServer letterList = null;
	
	
	
	letterList = clientProject
			.sendInputMessage(echoInObj);
	
	
	if (null == letterList) {
		errorMessage = String.format("input message[%s] letterList is null", echoInObj.getMessageID());
	} else {
		if (!letterList.hasMoreElements()) {
			errorMessage = String.format("%s 출력 메시지를 얻는 데 실패하였습니다.", echoInObj.getMessageID());
		} else {
			OutputMessage echoOutObj = letterList.nextElement();

			log.info("echoInObj=[%s]", echoInObj.toString());
			log.info("echoOutObj=[%s]", echoOutObj.toString());
			
			Integer out_mRandomInt = (Integer)echoOutObj.getAttribute("mRandomInt");
			Long out_mStartTime = (Long)echoOutObj.getAttribute("mStartTime");

			if (out_mRandomInt.equals(echoInObj
					.getAttribute("mRandomInt"))
					&& out_mStartTime.equals(echoInObj
							.getAttribute("mStartTime"))) {
				isSuccess = true;
				log.info("성공::echo 메시지 입력/출력 동일함");
			} else {
				log.info("실패::echo 메시지 입력/출력 다름");
				isSuccess = false;
			}
			
			erraseTime = new java.util.Date().getTime() - (Long) echoOutObj.getAttribute("mStartTime");
		}
	}	
	
%><!DOCTYPE html>
<html>
<head>
<title><jsp:include page="/title.html" /></title>
<script>
	function init() {	
		var div_leftmenu = document.getElementById("leftmenu");
		var div_body = document.getElementById("body");		
	}
</script>
</head>
<body style="width:800px;" onLoad="init();">
<header style="height:30px; width:800px;"></header>
<div id="main" style="height:auto; width:800px;">
<!-- main start --><%
	if (null != errorMessage) {
%><%=errorMessage%><%		
	} else {
%>
<table border="1">
<tr>
	<td colspan=2 style="text-align:center">Echo 입력메세지</td>
</tr>
<tr>
	<td style="text-align:center">항목</td><td style="text-align:center">값</td>
</tr>
<tr>
	<td style="text-align:left">랜덤 32bit 부호화 정수</td><td style="text-align:right"><%=(Integer)echoInObj.getAttribute("mRandomInt") %></td>
</tr>
<tr>
	<td style="text-align:left">the number of milliseconds<br/>since January 1, 1970, 00:00:00<br/>GMT represented</td><td style="text-align:right"><%=(Long)echoInObj.getAttribute("mStartTime")%></td>
</tr>
<tr>
	<td style="text-align:left">경과시간(milliseconds)</td><td style="text-align:right"><%=erraseTime%> ms</td>
</tr>
<tr>
	<td style="text-align:left">출력 비교 결과</td><%
		if (isSuccess) {
%><td style="color:blue;text-align:center">성공</td><%
		} else {
%><td style="color:red;text-align:center">실패</td><%
		}
%>
</tr>
</table><%		
	}
%>
<!-- main end -->
</div>
<footer style="height:60px; width:800px; float:right;"><jsp:include page="/footer.html" /></footer>
</body>
</html>

