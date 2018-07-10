<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="isSame" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="echoRes" class="kr.pe.codda.impl.message.Echo.Echo" scope="request" /><%
	// kr.pe.codda.impl.message.Echo.Echo echoRes = (kr.pe.codda.impl.message.Echo.Echo)request.getAttribute("echoRes");
		
	String erraseTime = (String)request.getAttribute("erraseTime");

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
		if (! isAdminLogin(request)) {
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
	
		<table border="1">
			<tr>
				<td colspan=2 style="text-align:center">Echo 검사</td>
			</tr>
			<tr>
				<td colspan=2 style="text-align:left">
				클라이언트에서 생성한 랜덤수를 갖는 에코 입력 메세지를
				그대로 출력 메세지로 받게 되는지를 검사한다.<br/>
				서버에서는 에코 입력 메세지를 그대로 출력 메세지로 복사한다.<br/>
				이 테스트를 얻게 되는 효과는 쓰레드 세이프 검증이다.<br/>
				만약 서버에서 입력메세지 처리시 쓰레드 세이프하지 않다면
				클라이언트로 보내는 값은 원래 보낸 데이터와 다르게 된다.
				</td>
			</tr>
			<tr>
				<td colspan=2 style="text-align:center">Echo 입력메세지</td>
			</tr>
			<tr>
				<td style="text-align:center">항목</td>
				<td style="text-align:center">값</td>
			</tr>
			<tr>
				<td style="text-align:left">랜덤 32bit 부호화 정수</td>
				<td style="text-align:right"><%= echoRes.getRandomInt() %></td>
			</tr>
			<tr>
				<td style="text-align:left">the number of milliseconds since January 1, 1970, 00:00:00<br/>GMT represented</td>
				<td style="text-align:right"><%= echoRes.getStartTime() %></td>
			</tr>
			<tr>
				<td style="text-align:left">경과시간(milliseconds)</td>
				<td style="text-align:right"><%=erraseTime%> ms</td>
			</tr>
			<tr>
				<td style="text-align:left">출력 비교 결과</td>
				<td style="text-align:right"><%= isSame %></td>
			</tr>
		</table>
		
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



