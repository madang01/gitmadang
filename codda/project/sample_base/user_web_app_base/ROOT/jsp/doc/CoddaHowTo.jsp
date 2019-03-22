<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	request.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/jsp/doc/CoddaHowTo.jsp");
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
<script type='text/javascript'>
	function init() {		
	}

	window.onload = init;
</script>
<body>
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>Codda HowTo</h4></div>
				<div class="panel-body">
					<article style="white-space:pre-line;">(1) 설치전 확인 사항
	(2) git download
	(3) codda-helper.jar 이용 설치 디렉토리에 맞춘 환경 설정
	(4) ant 실행
	(5) eclipse 환경 구축</article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>