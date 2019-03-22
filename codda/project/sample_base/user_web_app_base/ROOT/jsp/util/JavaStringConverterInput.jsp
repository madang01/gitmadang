<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	
%><!DOCTYPE html>
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
	function chkform(f) {
		if ('' == f.sourceString.value) {
			alsert("변환을 원하는 문자열을 넣어주세요");
			return false;
		}
		
		var g = document.gofrm;
		g.sourceString.value = f.sourceString.value;
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
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>자바 문자열 변환 도구 - 입력</h4></div>
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-12" style="background-color:lavender;"><h4>설명</h4></div>
					</div>
					<div class="row">
						<div class="col-sm-12"> 이 페이지는 JDF 를 기반으로 개발되었으며 servlet+jsp 조합인 MVC Model 2 를 따릅니다.<br><br>
					일반 사용자용 사이트에서 JDF 기본 서블릿은 AbstractServlet 를 상속 받습니다.<br>
					일반 사용자용 사이트용 jsp 페이지는 AbstractUserJSP 를 상속 받고 어드민 사이트용 jsp 페이지는 AbstractAdminJSP 를 상속 받습니다.<br><br>	
					이 페이지는 일반 사용자 사이트의 JDF 기본 페이지로써 JavaStringConverterSvl.java + JavaStringConverterInput.jsp 로 구성되어있습니다.</div>
					</div>
					<br>
					
					<form method="post" name="gofrm" action="/servlet/JavaStringConverterProcess">
						<input type="hidden" name="sourceString" />
					</form>
					<form name="frm" onsubmit="return chkform(this);">
						<div class="form-group">
							<label for="sourceString">평문:</label>
							<textarea name="sourceString" class="form-control" rows="5" id="sourceString"></textarea>
						</div>
						<button type="submit" class="btn btn-default">확인</button>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>