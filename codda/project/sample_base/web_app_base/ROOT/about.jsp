<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><!DOCTYPE html>
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
<script type='text/javascript'>
	function init() {
	
	}
	
	window.onload = init;
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
	<div class="container-fluid">
		<div class="panel panel-default">
			<div class="panel-heading">코다(Codda)</div>
			<div class="panel-body">코다는 RPC 서버를 기반으로 하는 개발 프레임워크입니다. <br>
			입력 메시지를 작성하여 1:1 대응하는 원격 비지니스 로직을 호출하여 출력 메시지를 얻어 응용 어플 작성해 나가도록 도와주는 개발 프레임워크입니다.<br>
			코다는 크게 2가지로 구성되어 있습니다.<br>
			첫번째 코다 서버로 자바 NIO selector 를 기반으로 하는 싱글 쓰레드 서버입니다.<br>
			마지막 두번째는  코다 서버 접속 API 로 동기식만 지원하는 폴과 동기/비동기 모두 다 지원하는 폴 2가지로 구성되어 있습니다.<br>
			보다 자세한 사항은 메뉴 "기술문서" 에 있는 내용들을 참고해 주시기 바랍니다.
			</div>
		</div>
	</div>
</body>
</html>
