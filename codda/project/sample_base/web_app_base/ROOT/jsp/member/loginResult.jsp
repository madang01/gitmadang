<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="successURL" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultRes" class="kr.pe.codda.impl.message.MessageResultRes.MessageResultRes" scope="request" /><%
	// String resultMessage = messageResultRes.getResultMessage();
	boolean isSuccess = messageResultRes.getIsSuccess();	
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><%= WebCommonStaticFinalVars.WEBSITE_TITLE %></title>
<meta name="Author" content="SinnoriTeam - website / Design by Ian Smith - N-vent Design Services LLC - www.n-vent.com" />
<meta name="distribution" content="global" />
<meta name="rating" content="general" />
<meta name="Keywords" content="" />
<meta name="ICBM" content=""/> <!-- see geourl.org -->
<meta name="DC.title" content="Your Company"/>
<link rel="shortcut icon" href="favicon.ico"/> <!-- see favicon.com -->
<link rel="stylesheet" type="text/css" href="/css/style.css" />
<script type="text/javascript">
    function goURL(bodyurl) {		
		top.document.location.href = bodyurl;		
    }
    
    function init() {
		var pageIV = CryptoJS.enc.Base64.parse("<%= getParameterIVBase64Value(request) %>");
		var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>'));

		var resultMessage = CryptoJS.AES.decrypt("<%= getCipheredBase64String(request, messageResultRes.toString()) %>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
				
		document.getElementById('idTxtResultMessage').innerHTML = resultMessage.toString(CryptoJS.enc.Utf8);

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
</script>
</head>
<body>
<form name="directgofrm" method="post">
<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>"/>
</form>
<!-- The ultra77 template is designed and released by Ian Smith - N-vent Design Services LLC - www.n-vent.com. Feel free to use this, but please don't sell it and kindly leave the credits intact. Muchas Gracias! -->
<div id="wrapper">
<a name="top"></a>
<!-- header -->
<div id="header">
	<div id="pagedescription"><h1>Sinnori Framework::공사중</h1><br /><h2> Sinnori Framework is an open software<br/> that help to create a server/client application.</h2><%
		if (! isAdminLoginedIn(request)) {
	%><a href="/servlet/Login?topmenu=<%=getCurrentTopMenuIndex(request)%>">login</a><%		
	} else {
%><a href="/menu/member/logout.jsp?topmenu=<%=getCurrentTopMenuIndex(request)%>">logout</a><%
	}
%>
	
	</div>
	<div id="branding"><p><span class="templogo"><!-- your logo here -->Sinnori Framework</span><br />of the developer, by the developer, for the developer</p></div>
</div>

<!-- top menu -->
<div id="menu">
	<ul><%= buildTopMenuPartString(request) %></ul>
</div> <!-- end top menu -->
<!-- bodywrap -->
<div id="bodytop">&nbsp;</div>
<div id="bodywrap">
	<div id="contentbody">

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
		<form name=gofrm method=get action="<%=successURL%>"><%
		java.util.Enumeration<String> parmEnum = request.getParameterNames();
		while(parmEnum.hasMoreElements()) {
			String parmName = parmEnum.nextElement();
			
			if (WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY.equals(parmName) 
					|| WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV.equals(parmName)
				|| "id".equals(parmName) || "pwd".equals(parmName)
				|| "successURL".equals(parmName) 
				|| WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE.equals(parmName)) {
				continue;
			}
			
			
			String parmValue = request.getParameter(parmName);			
		%>
			<input type=hidden name="<%= HtmlStringUtil.toHtml4String(parmName) %>" value="<%= HtmlStringUtil.toHtml4String(parmValue) %>" />
<%		
	}
%>		</form>
		<table>
			<tr><td><h1>로그인 결과</h1></td></tr>
			<tr>
				<td id="idTxtResultMessage"></td>
			</tr>
			<tr>
				<td><%
		if (isSuccess) {
		%><a href="/">home</a><%
			} else {
		%><a href="#" onclick="window.history.back()">back</a><%
			}
		%></td>
			</tr>
		</table>
	</div>
</div> <!-- end bodywrap -->
<div id="bodybottom">&nbsp;</div>


<!-- footer -->
<div id="footer">
<p><jsp:include page="/footer.html"  flush="false" />. Design by <a href="http://www.n-vent.com" title="The ultra77 template is designed and released by N-vent Design Services LLC">N-vent</a></p>
<ul>
<li><a href="http://www.oswd.org" title="Open Source Web Design">Open Source Web Design</a></li>

</ul>
</div> <!-- end footer -->

<!-- side menu  --><%= buildLeftMenuPartString(request) %><!-- end side menu -->

</div> <!-- end wrapper -->
</body>
</html>
