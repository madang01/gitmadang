<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%>
<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%>
<%@page import="kr.pe.codda.client.ConnectionPoolManager"%>
<%@page import="kr.pe.codda.client.AnyProjectConnectionPoolIF"%>
<%@page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%>
<%@page import="kr.pe.codda.common.message.AbstractMessage"%>
<%@page import="kr.pe.codda.impl.message.Echo.Echo"%>
<%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	String errorMessage = null;
	long erraseTime = 0;
	boolean isSuccess=false;

	java.util.Random random = new java.util.Random();
	
	
	
	AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
	
	Echo echoInObj = new Echo();
	echoInObj.setRandomInt(random.nextInt());
	echoInObj.setStartTime(new java.util.Date().getTime());
	
	AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);
	
	boolean isSame = false;
	
	Echo echoRes = null;
	
	if (messageFromServer instanceof Echo) {
		echoRes = (Echo)messageFromServer;
		
		erraseTime = new java.util.Date().getTime() - echoInObj.getStartTime();
		
		if ((echoRes.getRandomInt() == echoInObj
		.getRandomInt())
		&& (echoRes.getStartTime() == echoInObj.getStartTime())) {
	isSame = true;
	//log.info("성공::echo 메시지 입력/출력 동일함");
		} else {
	isSame = false;
	// log.info("실패::echo 메시지 입력/출력 다름");
		}
	} else {
		errorMessage = messageFromServer.toString();
	}
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type="text/javascript">
<!--
	function init() {	
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
<%=getSiteNavbarString(request)%>
<div class="container-fluid">
		<h2>에코 테스트</h2><%
			if (null != errorMessage) {
		%><%=StringEscapeActorUtil.replace(errorMessage, 
		STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
		STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%><%
	} else {
%>
 
 <div class="row">
	<div class="col-sm-12">클라이언트에서 생성한 랜덤수를 갖는 에코 입력 메세지를
				그대로 출력 메세지로 받게 되는지를 검사한다.<br/>
				서버에서는 에코 입력 메세지를 그대로 출력 메세지로 복사한다.<br/>
				이 테스트를 얻게 되는 효과는 쓰레드 세이프 검증이다.<br/>
				만약 서버에서 입력메세지 처리시 쓰레드 세이프하지 않다면
				클라이언트로 보내는 값은 원래 보낸 데이터와 다르게 된다.
	</div>
</div>
<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">항목</div>
	<div class="col-sm-1" style="background-color:lavender;">값</div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">랜덤 32bit 부호화 정수</div>
	<div class="col-sm-1" style="background-color:lavenderblush;"><%= echoRes.getRandomInt() %></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">the number of milliseconds since January 1, 1970, 00:00:00<br/>GMT represented</div>
	<div class="col-sm-1" style="background-color:lavender;"><%= echoRes.getStartTime() %></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">경과시간(milliseconds)</div>
	<div class="col-sm-1" style="background-color:lavenderblush;"><%= erraseTime %></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">출력 비교 결과</div>
	<div class="col-sm-1" style="background-color:lavender;"><%= isSame %></div>
</div><%
	}
%>
	</div>
</body>
</html>

