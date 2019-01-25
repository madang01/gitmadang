<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
	
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	String orignalMessage = "원문에 있는 이 문구가 복호문에서 잘 보시이면 " 
+ "AbstractSessionKeyServlet 모듈 테스트 통과 안보이면 실패\n<script type=\"text/javascript\">alert(\"hello\");</script> 또한 스크립트는 코드 인젝션 방어 즉 실행되지 않고 단순 문자로 출력되면 통과";
%>
<!DOCTYPE html>
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
		var pageIV = CryptoJS.enc.Base64.parse("<%= getParameterIVBase64Value(request) %>");	
		
		var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>'));
		
		var messageTxt = CryptoJS.AES.decrypt("<%= getCipheredBase64String(request, 
				StringEscapeActorUtil.replace(orignalMessage, 
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
				STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)) %>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
			
		document.getElementById('idTxtResultMessage').innerHTML = messageTxt.toString(CryptoJS.enc.Utf8);
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
<%= getWebsiteMenuString(request) %>
	
	<div class="container-fluid">
		<h2>JDF 테스트 - 세션키</h2>
 
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 세션키가 필요한 서블릿은 AbstractSessionKeyServlet 를 상속 받습니다.<br>
AbstractSessionKeyServlet 는  세션키 운영에 필요한 파라미터를 요구하며<br/>
세션키 운영에 필요한 파라미터가 없다면 파라미터 값들을 보존하며 세션키에 해당하는 파라미터 값들을 자동적으로 가져오는 페이지를 통해 가져옵니다.<br/>
자동으로 가져올때 만약 HTML5 sessionStorage 에 세션키 운영에 필요한 값이 없다면 생성합니다.<br/>
주) 파라미터 값들을 보존할때 암호화를 하지 않습니다.<br/>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 세션키가 필요한 페이지로써 JDFSessionKeyTestSvl.java + JDFSessionKeyTest.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>

<div class="row">
	<div class="col-sm-6" style="background-color:lavender;"><h4>원문</h4></div>
	<div class="col-sm-6" style="background-color:lavenderblush;"><h4>복호문</h4></div>
</div>
<div class="row">
	<div class="col-sm-6" style="background-color:lavender;"><%=StringEscapeActorUtil.replace(orignalMessage, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
	<div class="col-sm-6" style="background-color:lavenderblush;" id="idTxtResultMessage"></div>
</div>
	</div>
</body>
</html>
