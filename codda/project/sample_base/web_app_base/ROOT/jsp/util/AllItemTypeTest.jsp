<%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil"%>
<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="isSame" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="allDataTypeReq" class="kr.pe.codda.impl.message.AllItemType.AllItemType" scope="request" /><%
%><jsp:useBean id="allItemTypeRes" class="kr.pe.codda.impl.message.AllItemType.AllItemType" scope="request" /><%
	// kr.pe.codda.impl.message.Echo.Echo echoRes = (kr.pe.codda.impl.message.Echo.Echo)request.getAttribute("echoRes");
		
	// String erraseTime = (String)request.getAttribute("erraseTime");

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
		<h2>모든 데이터 타입 검사</h2>
 
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 AllItemTypeTestSvl.java + AllItemTypeTest.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>

<div class="row">
	<div class="col-sm-12">서버와 클라이 언트 사이에 모든 데이터 타입별 최대/최소/중간값이 잘 전달되는지를 검사한다.<br/>
				서버에서는 입력 메세지를 그대로 복사하여 전달한다.</div>
</div>
<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>
<div class="row">
	<div class="col-sm-2" style="background-color:lavender;">AllDataType 입력메세지</div>
	<div class="col-sm-10" style="background-color:lavender;"><%= StringReplacementActorUtil.replace(allDataTypeReq.toString(), 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR) %></div>
</div>
<div class="row">
	<div class="col-sm-2" style="background-color:lavenderblush;">AllDataType 출력메세지</div>
	<div class="col-sm-10" style="background-color:lavenderblush;"><%= StringReplacementActorUtil.replace(allItemTypeRes.toString(), 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR) %></div>
</div>
<div class="row">
	<div class="col-sm-2" style="background-color:lavender;">출력 비교 결과</div>
	<div class="col-sm-10" style="background-color:lavender;"><%= isSame %></div>
</div>
	</div>
</body>
</html>



