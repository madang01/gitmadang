<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%>
<!DOCTYPE html>
<html lang="ko">
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
<script type="text/javascript" src="/js/cryptoJS/rollups/md5.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha1.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha512.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function chkform() {		
		var f = document.frm;
		var g = document.gofrm;
		
		switch(f.algorithm.selectedIndex) {
			case 0:
			  g.javascriptMD.value = CryptoJS.MD5(f.plainText.value);
			  break;
			case 1:
			  g.javascriptMD.value = CryptoJS.SHA1(f.plainText.value);
			  break;
			case 2:
			  g.javascriptMD.value = CryptoJS.SHA256(f.plainText.value);			  
			  break; 
			case 3:
			  g.javascriptMD.value = CryptoJS.SHA512(f.plainText.value);			  
			  break;  
			default:
			 alert("unkown digest message algothm");
			 return false;
		}
		
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
		<h2>CryptoJS 해시 알고리즘(=메세지 다이제스트) 테스트 - 입력</h2>
 
<div class="row">
	<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
</div>
<div class="row">
	<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JSMessageDigestTestSvl.java + JSMessageDigestTestInput.jsp 로 구성되어있습니다.</div>
</div>

<div class="row">
	<div class="col-sm-12">&nbsp;</div>
</div>
<form method="post" name="gofrm" action="/servlet/JSMessageDigestTest">
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
	<input type="hidden" name="algorithm" />
	<input type="hidden" name="javascriptMD" />
	<input type="hidden" name="plainText" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<div class="form-group">
		<label for="algorithm">해시 알고리즘(=메세지 다이제스트) 알고리즘:</label>
		<select class="form-control" name="algorithm" id="algorithm">
			<option value="MD5">MD5</option>
			<option value="SHA1">SHA1</option>
			<option value="SHA-256">SHA256</option>
			<option value="SHA-512">SHA512</option>
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