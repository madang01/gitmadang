<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
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
				f.pwdconfirm.value =  f.pwd.value;
				f.nickname.value = "별명00";
				f.pwdHint.value = "질문";
				f.pwdAnswer.value = "답변";
			}

			window.onload = init;

			function chkform() {
			
				var f = document.frm;
				var g = document.gofrm;
				
				var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
				var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
				var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
				var regexPwdDigit = /.*[0-9]{1,}.*/;
				var regexPwdPunct = /.*[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}.*/;
			
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
		*/
				
				if (f.pwdconfirm.value == '') {
					alert("비밀번호 확인을 넣어주세요.");
					f.pwdconfirm.focus();
					return;
				}
				
				if (f.pwdconfirm.value != f.pwd.value) {
					alert("비밀번호가 일치하지 않습니다. 다시 넣어주세요.");
					f.pwd.value='';	    
					f.pwdconfirm.value='';
					f.pwd.focus();	    
					return;
				}
				
				

				if (f.nickname.value == '') {
					alert("별명을 넣어주세요.");
					f.nickname.focus();
					return;
				}
			
						
				if (f.pwdHint.value == '') {
					alert("비밀번호 분실시 질문을 넣어주세요.");
					f.pwdHint.focus();
					return;
				}		
				
				if (f.pwdAnswer.value == '') {
					alert("비밀번호 분실시 답변을 넣어주세요.");
					f.pwdAnswer.focus();
					return;
				}		
				
				if (f.answer.value == '') {
					alert("Captcha 답변을 넣어주세요.");
					f.answer.focus();
					return;
				}					
				
				// FIXME!
				var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);		
				// var privateKey = CryptoJS.enc.Hex.parse("cf5553bdbe3a6240a0a89fdd9be4e64c");
								
				// alert(privateKey.length);				
				
				var rsa = new RSAKey();
				rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
				
				// FIXME!
				var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
				var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));				

				sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', CryptoJS.enc.Base64.stringify(privateKey));
				sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY %>', sessionkeyBase64);
				
			
				var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
				// alert(g.ivBase64.value);				
				var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;

				g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
					= sessionkeyBase64;
				g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
					= CryptoJS.enc.Base64.stringify(iv);
				
				g.id.value = symmetricKeyObj.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				g.nickname.value = symmetricKeyObj.encrypt(f.nickname.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				g.pwdHint.value = symmetricKeyObj.encrypt(f.pwdHint.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				g.pwdAnswer.value = symmetricKeyObj.encrypt(f.pwdAnswer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });

				g.answer.value = symmetricKeyObj.encrypt(f.answer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				
				// alert(g.id.value);
				
				g.submit();
				
				return;
			}

			function reloadCaptcha() {
				var img = document.getElementById("captchaImage");
				img.src="/servlet/stickyImg?" + new Date().getTime();
			}
		//-->
		</script>
		<form method="post" name="gofrm" action="/servlet/Member">
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
		<input type="hidden" name="id" />
		<input type="hidden" name="pwd" />
		<input type="hidden" name="nickname" />
		<input type="hidden" name="pwdHint" />
		<input type="hidden" name="pwdAnswer" />
		<input type="hidden" name="answer" />
		</form>
		<form method="post" name="frm" onsubmit="return;">
			<table style="border:0">
			<tr>
				<td colspan="2" align="center" style="font-size:24px">신놀이 회원 가입</td>
			</tr>
			<tr>
				<td>아이디</td>
				<td><input type="text" name="id" size="15" maxlength="15" /></td> 
			</tr>
			<tr> 
				<td>비빌번호</td>
				<td><input type="password" name="pwd" size="15" maxlength="15" /></td>
			</tr>
			<tr>
				<td>비밀번호 확인</td>
				<td><input type="password" name="pwdconfirm" siz="15" maxlength="15" /></td> 
			</tr>
			<tr>
				<td>별명</td>
				<td><input type="text" name="nickname" size="20" maxlength="20" /></td>
			</tr>
			<tr>
				<td>비밀 번호 분실시 답변 힌트</td>
				<td><input type="text" name="pwdHint" size="30" maxlength="30" /></td>
			</tr>
			<tr> 
				<td>비밀 번호 분실시 답변</td>
				<td><textarea name="pwdAnswer" rows="5" cols="30"></textarea></td>
			</tr>
			<tr> 
				<td>Captcha 이미지</td>
				<td><table border="0"><tr><td width="80%"><img id="captchaImage" src="/servlet/stickyImg" alt="Captcha Image" width="100%" /></td><td><a href="#" onClick="reloadCaptcha()" id="refresh" style="curso:pointer"><img src="/images/gtk_refresh.png" width="100%" /></a></td></tr></table></td>	    
			</tr>
			<tr> 
				<td>Captcha 답변</td>
				<td><input type="text" name="answer" size="20" maxlength="20" /></td>
			</tr>

			<tr> 
				<td colspan="2" align="center"><input type="button" onClick="chkform()" value="확인" /></td>
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

