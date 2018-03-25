<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
	request.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, SITE_TOPMENU_TYPE.TEST_EXAMPLE);

	String orignalMessage = "원문에 있는 이 문구가 복호문에서 잘 보시이면 AbstractSessionKeyServlet 모듈 테스트 통과 안보이면 실패";
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
		<h1>JDF 테스트 - 로그인</h1>
		<ul>
			<li>이 페이지는 AbstractAuthServlet 를 상속 받은 페이지입니다.<br/>
		AbstractAuthServlet 는 AbstractSessionKeyServlet 를 상속 받으며 로그인을 요구합니다.<br/>
		로그인 안했을 경우에 이 페이지 내용이 보여서는 안되며,<br/>
		로그인 했을 경우에만 이 페이지 내용이 보여야 합니다.<br/>
		AbstractSessionKeyServlet 는  세션키 운영에 필요한 파라미터를 요구하며<br/>
		없다면 파라미터 값들을 보존하며 세션키에 해당하는 파라미터 값들을 자동적으로 가져오는 페이지를 통해 가져옵니다.<br/>
		자동으로 가져올때 만약 HTML sessionStorage 에 세션키 관련 값들이 없다면 역시 자동생성한다.<br/>
		주) 파라미터 값들을 보존할때 암호화를 하지 않습니다.
			</li>
			<li><h2>AbstractSessionKeyServlet 모듈 테스트</h2>
				<table border="1">
				<tr>
					<td>원문</td><td>복호문</td>
				</tr>
				<tr>
					<td><%=orignalMessage%></td><td id="idTxtResultMessage"></td>
				</tr>
				</table>
			</li>
		</ul>
		<script type="text/javascript">
		<!--
			function init() {
				var pageIV = CryptoJS.enc.Base64.parse("<%=parmIVBase64%>");
				var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>'));

				var resultMessage = CryptoJS.AES.decrypt("<%=getCipheredBase64String(request, orignalMessage)%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });

				document.getElementById('idTxtResultMessage').innerHTML = resultMessage.toString(CryptoJS.enc.Utf8);
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
