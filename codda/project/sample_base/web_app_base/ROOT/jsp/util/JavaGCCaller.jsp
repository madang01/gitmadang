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
<title><%= WebCommonStaticFinalVars.WEBSITE_TITLE %></title>
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
<%= getSiteNavbarString(request) %>
	
	<div class="container-fluid">
		<h2>Java Garbage Collection Caller</h2>
 
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JavaGCCallerSvl.java + JavaGCCaller.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>

<div class="row">
	<div class="col-sm-12">자바 서버에서 System.gc() 를 호출시킨다.<br>
	서버에서 동적 클래스가 변경되어 동적 클래스 로더에 의해 구 클래스를 신규로 교체할때 구 클래스는 더 이상 쓰이지 않으므로 확실하게 GC 대상이 되어야 한다.<br>
	하여 강제적으로 System.gc() 를 호출시켜 구 클래스가 GC 대상인지 확인한다.
		<div class="alert alert-danger">
			<strong>WARNING!</strong> System.gc() 호출시 java VM 이 멈추므로 절대로 개발에서만 사용해야 하며 운영에서는 사용해서는 안된다.
		</div>
	</div>
</div>
	</div>
</body>
</html>



