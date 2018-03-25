<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="modulusHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultOutObj" class="kr.pe.sinnori.impl.message.MessageResult.MessageResult" scope="request" /><%
	String parmTopmenu = request.getParameter("topmenu");
	if (null == parmTopmenu) {
		parmTopmenu = String.valueOf(SITE_TOPMENU_TYPE.MEMBER.getTopMenuIndex());
	}
	parmTopmenu = parmTopmenu.trim();	
	if (parmTopmenu.equals("")) parmTopmenu=String.valueOf(SITE_TOPMENU_TYPE.MEMBER.getTopMenuIndex());
	
	System.out.println("parmTopmenu="+parmTopmenu);
	
	int nTopMenu = SITE_TOPMENU_TYPE.MEMBER.getTopMenuIndex();
	
	try {
		nTopMenu = Integer.parseInt(parmTopmenu);
	} catch (NumberFormatException num_e) {
	}
	
	SITE_TOPMENU_TYPE targetSiteTopMenuType = SITE_TOPMENU_TYPE.matchIndex(nTopMenu);

	request.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, targetSiteTopMenuType);

	// String resultMessage = messageResultOutObj.getResultMessage();
	boolean isSuccess = messageResultOutObj.getIsSuccess();

	
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
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
		/*
		var inx = bodyurl.indexOf("/servlet/");	
		if (0 == inx) {
			var f = document.directgofrm;
			f.action = bodyurl;
			f.submit();		
		} else {
			top.document.location.href = bodyurl;
		}
		*/
		top.document.location.href = bodyurl;		
    }
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
	if (! isLogin(request)) {
%><a href="/servlet/Login?topmenu=<%=getCurrentTopMenuIndex(request)%>">login</a><%		
	} else {
%><a href="/menu/member/logout.jsp?topmenu=<%=SITE_TOPMENU_TYPE.MEMBER.getTopMenuIndex()%>">logout</a><%
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
		<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
		<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
		<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
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

		<script type="text/javascript">
		<!--
			function init() {
				var pageIV = CryptoJS.enc.Base64.parse("<%=parmIVBase64%>");
				var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>'));

				var resultMessage = CryptoJS.AES.decrypt("<%=getCipheredBase64String(request, messageResultOutObj.toString())%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
				document.getElementById('idTxtResultMessage').innerHTML = resultMessage.toString(CryptoJS.enc.Utf8);

				<!-- 보안을 위해서 로그인시 생성한 비밀키와 세션키 덮어쓰기 -->
				var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
				
				var rsa = new RSAKey();
				rsa.setPublic("<%=modulusHex%>", "10001");
					
				var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
				var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

				sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>', CryptoJS.enc.Base64.stringify(privateKey));
				sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_SESSIONKEY_NAME%>', sessionkeyBase64);
			}

			window.onload = init;
			
		//-->
		</script>
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
