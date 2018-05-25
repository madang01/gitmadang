<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="successURL" class="java.lang.String" scope="request" /><%
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
		<script type="text/javascript">
		<!--
			function init() {
				var f = document.frm;
				f.id.value =  "test00";
				f.pwd.value =  "test1234$";
				// f.pwd.value =  "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~$";
			}

			window.onload = init;

			function chkform() {				
				var f = document.frm;
				var g = document.gofrm;
				
				var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
				var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,50}$/;
				var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
				var regexPwdDigit = /.*[0-9]{1,}.*/;
				//var regexPwdPunct = /.*[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}.*/;
				var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;

							
				if(typeof(sessionStorage) == "undefined") {
					alert("Sorry! No HTML5 sessionStorage support..");
					return;
				}	
				
				if (f.id.value == '') {
					alert("아이디를 넣어주세요.");
					f.id.focus();
					return;
				}
				
				if (!regexID.test(f.id.value)) {
					alert("아이디는 첫 문자가 영문자 그리고 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
					f.id.value = '';
					f.id.focus();
					return;
				}
				
				if (f.pwd.value == '') {
					alert("비밀번호를 넣어주세요.");
					f.pwd.focus();
					return;
				}
				
				
				/*
				for( var i=0; i< f.pwd.value.length; i++){
					if(regexp_pwd.test(f.pwd.value.charAt(i)) == false ){
						alert(f.pwd.value.charAt(i) + "는 입력불가능한 문자입니다");
						return;
					}
				}
				*/	

				if (!regexPwd.test(f.pwd.value)) {
					alert("비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
					f.pwd.value = '';
					f.pwd.focus();
					return;
				}
				
				if (!regexPwdAlpha.test(f.pwd.value)) {
					alert("비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요.");
					f.pwd.value = '';
					f.pwd.focus();
					return;
				}

				if (!regexPwdDigit.test(f.pwd.value)) {
					alert("비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
					f.pwd.value = '';
					f.pwd.focus();
					return;
				}

				if (!regexPwdPunct.test(f.pwd.value)) {
					alert("비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
					f.pwd.value = '';
					f.pwd.focus();
					return;
				}		
						
				
				var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
				
				var rsa = new RSAKey();
				rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
					
				var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
				g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
					= CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

				sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY %>', CryptoJS.enc.Base64.stringify(privateKey));
				sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', g.sessionkeyBase64.value);

				var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
				g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
					= CryptoJS.enc.Base64.stringify(iv);

				
				var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
			
				g.id.value = symmetricKeyObj.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				
				f.pwd.value = '';
				
				// alert("성공");

				g.submit();
				
				return;
			}
		//-->
		</script>
		<form method="post" name="gofrm" action="/servlet/Login">
		<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
		<input type="hidden" name="id" />
		<input type="hidden" name="pwd" />
		<input type="hidden" name="successURL" value="<%=successURL%>" /><%
		java.util.Enumeration<String> parmEnum = request.getParameterNames();
		while(parmEnum.hasMoreElements()) {
			String parmName = parmEnum.nextElement();
			
			if (WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY.equals(parmName) 
					|| WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV.equals(parmName)
				|| "id".equals(parmName) || "pwd".equals(parmName)
				|| "successURL".equals(parmName) 
				|| WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE.equals(parmName) 
				|| "topmenu".equals(parmName)) {
				continue;
			}
			
			String parmValue = request.getParameter(parmName);			
		%><input type=hidden name="<%= HtmlStringUtil.toHtml4String(parmName) %>" value="<%= HtmlStringUtil.toHtml4String(parmValue)%>" />

<%
		
	}
%>
		</form>
		<form method="post" name="frm" onsubmit="return false;">
		<table style="board:0">
			<tr>
				<td colspan="2" align="center" style="font-size:24px">로그인</td></tr>
			<tr>
				<td>아이디</td>
				<td><input type="text" name="id" size="15" maxlength="15" /></td> 
			</tr>
			<tr> 
				<td>비빌번호</td>
				<td><input type="password" name="pwd" size="15" maxlength="15" /></td>
			</tr>
			<tr> 
				<td colspan="2" align="center"><input type="button" value="확인" onclick="chkform()" /></td>
			</tr>
			</table>
		</form>
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
