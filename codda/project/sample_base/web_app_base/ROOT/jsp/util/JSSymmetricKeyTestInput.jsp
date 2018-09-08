<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%>
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
<script type="text/javascript" src="/js/cryptoJS/rollups/tripledes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function chkform() {		
		var f = document.frm;
		var g = document.gofrm;
		
		var privateKey;
		var iv;		
		var encryptedBytes;
			 
		switch(f.algorithm.selectedIndex) {
			case 0:
				privateKey = CryptoJS.lib.WordArray.random(16);
				iv = CryptoJS.lib.WordArray.random(16);				
				encryptedBytes = CryptoJS.AES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 1:
				privateKey = CryptoJS.lib.WordArray.random(8);
				iv = CryptoJS.lib.WordArray.random(8);
				encryptedBytes = CryptoJS.DES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 2:
				privateKey = CryptoJS.lib.WordArray.random(24);
				iv = CryptoJS.lib.WordArray.random(8);
				encryptedBytes = CryptoJS.TripleDES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });			  
				break;  
			default:
			 alert("unkown digest message algothm");
			 return false;
		}
		
		g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
		g.iv.value = CryptoJS.enc.Hex.stringify(iv);

		g.encryptedHexText.value = encryptedBytes.ciphertext;
		g.algorithm.value = f.algorithm.options[f.algorithm.selectedIndex].value;
		g.plainText.value = f.plainText.value;
		g.submit();
		
		return false;
	}
	

	function init() {	
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
	
	<div class="container-fluid">
		<h2>CryptoJS 대칭키 테스트 - 입력</h2>
 
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JSSymmetricKeyTestSvl.java + JSSymmetricKeyTestInput.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>
<form method="post" name="gofrm" action="/servlet/JSSymmetricKeyTest">
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
	<input type="hidden" name="algorithm" />
	<input type="hidden" name="privateKey" />
	<input type="hidden" name="iv" />
	<input type="hidden" name="plainText" />
	<input type="hidden" name="encryptedHexText" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<div class="form-group">
		<label for="algorithm">대칭키 알고리즘:</label>
		<select class="form-control" name="algorithm" id="algorithm">
			<option value="AES">AES</option>
			<option value="DES">DES</option>
			<option value="DESede">DESede(=Triple DES)</option>
		</select>
	</div>
	<div class="form-group">
		<label for="plainText">평문:</label>
		<textarea name="plainText" class="form-control" rows="5" id="plainText"></textarea>
	</div>
	<button type="submit" class="btn btn-default">확인</button>	
</form>
	</div>
</body>
</html>