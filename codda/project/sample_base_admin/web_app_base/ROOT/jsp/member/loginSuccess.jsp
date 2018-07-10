<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="java.util.ArrayList" %><%
%><%@ page import="java.util.Map" %><%
%><%@ page import="org.apache.commons.text.StringEscapeUtils" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
%><%@ page import="kr.pe.codda.weblib.jdf.LoginRequestPageInformation" %><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
	LoginRequestPageInformation loginRequestPageInformation = (LoginRequestPageInformation)request.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_REQUEST_PAGE_INFORMATION);
%><!DOCTYPE html>
<html>
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
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function init() {
		<!-- 보안을 위해서 로그인시 생성한 비밀키와 세션키 덮어쓰기 -->
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', CryptoJS.enc.Base64.stringify(privateKey));
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY %>', sessionkeyBase64);

		document.gofrm.submit();
	}
	
	window.onload = init;
//-->
</script>
</head>
<body>
<%=adminSiteMenuManger.getSiteNavbarString(isAdminLogin(request))%>
	
	<div class="container-fluid">
		<h3>Admin Login Result</h3>
		<form name=gofrm method=get action="<%= loginRequestPageInformation.getRequestURI() %>"><%
	ArrayList<Map.Entry<String, String>> parameterEntryList = loginRequestPageInformation.getParameterEntryList();
		
	for (Map.Entry<String, String> parameterEntry : parameterEntryList) {
		String parameterKey = parameterEntry.getKey();
		String parameterValue = parameterEntry.getValue();
%>
		<textarea name="<%= StringEscapeUtils.escapeHtml4(parameterKey) %>" style="display:none;"><%= StringEscapeUtils.escapeHtml4(parameterValue) %></textarea><%
	}
%>
		</form>

		<div class="alert alert-success">
			<strong>Success!</strong> moving to the requested page.
		</div>	
	</div>
</body>
</html>