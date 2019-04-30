<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="isSame" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="erraseTime" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="echoRes" class="kr.pe.codda.impl.message.Echo.Echo" scope="request" /><%
	// kr.pe.codda.impl.message.Echo.Echo echoRes = (kr.pe.codda.impl.message.Echo.Echo)request.getAttribute("echoRes");
		
	// String erraseTime = (String)request.getAttribute("erraseTime");

%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function init() {	
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>에코 테스트</h4></div>
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
					</div>
					<div class="row">
						<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
					일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
					일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
					이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 EchoTestSvl.java + EchoTest.jsp 로 구성되어있습니다.</div>
					</div>
					
					<div class="row">
						<div class="col-sm-12">&nbsp;</div>
					</div>
					
					<div class="row">
						<div class="col-sm-12">클라이언트에서 생성한 랜덤수를 갖는 에코 입력 메세지를
									그대로 출력 메세지로 받게 되는지를 검사한다.<br/>
									서버에서는 에코 입력 메세지를 그대로 출력 메세지로 복사한다.<br/>
									이 테스트를 얻게 되는 효과는 쓰레드 세이프 검증이다.<br/>
									만약 서버에서 입력메세지 처리시 쓰레드 세이프하지 않다면
									클라이언트로 보내는 값은 원래 보낸 데이터와 다르게 된다.</div>
					</div>
					<div class="row">
						<div class="col-sm-12">&nbsp;</div>
					</div>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavender;">항목</div>
						<div class="col-sm-2" style="background-color:lavender;">값</div>
					</div>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavenderblush;">랜덤 32bit 부호화 정수</div>
						<div class="col-sm-2" style="background-color:lavenderblush;"><%= echoRes.getRandomInt() %></div>
					</div>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavender;">the number of milliseconds since January 1, 1970, 00:00:00<br/>GMT represented</div>
						<div class="col-sm-2" style="background-color:lavender;"><%= echoRes.getStartTime() %></div>
					</div>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavenderblush;">경과시간(microseconds)</div>
						<div class="col-sm-2" style="background-color:lavenderblush;"><%= erraseTime %></div>
					</div>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavender;">출력 비교 결과</div><%						
	if (Boolean.valueOf(isSame)) {
		out.print("<div class=\"col-sm-2\" style=\"background-color:blue;color:white\">성공<div>");

	} else {
		out.print("<div class=\"col-sm-2\" style=\"background-color:red;color:white\">실패<div>");
	}
%>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>