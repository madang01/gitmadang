<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
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
		<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
		<script type="text/javascript" src="/js/cryptoJS/rollups/tripledes.js"></script>
		<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
		<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
		<script type="text/javascript">
		<!--
			function chkform() {
				var f = document.frm;
				var g = document.gofrm;		
				var encryptedBytes;
				 
				switch(f.algorithm.selectedIndex) {
					case 0:
						var privateKey = CryptoJS.lib.WordArray.random(16);		
						g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
						var iv = CryptoJS.lib.WordArray.random(16);
						g.iv.value = CryptoJS.enc.Hex.stringify(iv);
						encryptedBytes = CryptoJS.AES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
						break;
					case 1:
						var privateKey = CryptoJS.lib.WordArray.random(8);		
						g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
						var iv = CryptoJS.lib.WordArray.random(8);
						g.iv.value = CryptoJS.enc.Hex.stringify(iv);
						encryptedBytes = CryptoJS.DES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
						break;
					case 2:
						var privateKey = CryptoJS.lib.WordArray.random(24);		
						g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
						var iv = CryptoJS.lib.WordArray.random(8);
						g.iv.value = CryptoJS.enc.Hex.stringify(iv);
						encryptedBytes = CryptoJS.TripleDES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });			  
						break;  
					default:
					 alert("unkown digest message algothm");
					 return false;
				}
				
				g.encryptedBytes.value = encryptedBytes.ciphertext;
				g.algorithm.value = f.algorithm.options[f.algorithm.selectedIndex].value;
				g.plainText.value = f.plainText.value;
				g.submit();
				
				return false;
			}
		//-->
		</script>

		<form method="post" name="gofrm" target="_top">
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
		<input type="hidden" name="algorithm" />
		<input type="hidden" name="privateKey" />
		<input type="hidden" name="iv" />
		<input type="hidden" name="plainText" />
		<input type="hidden" name="encryptedBytes" />
		</form>
		<form method="post" name="frm" onsubmit="return chkform();">
			<table>
			<tr><td colspan="2"><h3>CryptoJS 대칭키 테스트</h3></td></tr>
			<tr>
				<td>대칭키 알고리즘</td>
				<td>
					<select name="algorithm">
						<option value="AES">AES</option>
						<option value="DES">DES</option>
						<option value="DESede">DESede(=Triple DES)</option>
					</select>
				</td> 
			</tr>
			<tr>
				<td>평문</td>
				<td><textarea name="plainText" size="15" maxlength="15" ></textarea></td> 
			</tr>
			<tr> 
				<td colspan="2"><input type="submit" value="확인" /></td>
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


