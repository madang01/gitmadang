<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%%><jsp:useBean id="plainText" class="java.lang.String" scope="request" /><%%><jsp:useBean id="algorithm" class="java.lang.String" scope="request" /><%%><jsp:useBean id="privateKey" class="java.lang.String" scope="request" /><%%><jsp:useBean id="iv" class="java.lang.String" scope="request" /><%%><jsp:useBean id="encryptedHexText" class="java.lang.String" scope="request" /><%%><jsp:useBean id="plainHexText" class="java.lang.String" scope="request" /><%%><jsp:useBean id="decryptedHexText" class="java.lang.String" scope="request" /><%%><jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" /><%%><jsp:useBean id="isSame" class="java.lang.String" scope="request" /><%%>
<!DOCTYPE html>
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
<%=getMenuNavbarString(request)%>
	
	<div class="container-fluid">
		<h3>CryptoJS 대칭키 테스트 - 결과</h3>
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JSSymmetricKeyTestSvl.java + JSSymmetricKeyTestResult.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>

<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">원문</div>
	<div class="col-sm-9" style="background-color:lavender;"><%=StringEscapeActorUtil.replace(plainText, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">대칭키 알고리즘</div>
	<div class="col-sm-9" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(algorithm, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">개인키</div>
	<div class="col-sm-9" style="background-color:lavender;"><%=StringEscapeActorUtil.replace(privateKey, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">iv</div>
	<div class="col-sm-9" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(iv, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">javascirpt 암호문</div>
	<div class="col-sm-9" style="background-color:lavender;"><%=StringEscapeActorUtil.replace(encryptedHexText, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">원문 Hex</div>
	<div class="col-sm-9" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(plainHexText, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">server 복호문 Hex</div>
	<div class="col-sm-9" style="background-color:lavender;"><%=StringEscapeActorUtil.replace(decryptedHexText, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavenderblush;">server 복호문</div>
	<div class="col-sm-9" style="background-color:lavenderblush;"><%=StringEscapeActorUtil.replace(decryptedPlainText, 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
			STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
</div>
<div class="row">
	<div class="col-sm-3" style="background-color:lavender;">비교결과</div>
	<div class="col-sm-9" style="background-color:lavender;"><%= isSame %></div>
</div>
	</div>
</body>
</html>
