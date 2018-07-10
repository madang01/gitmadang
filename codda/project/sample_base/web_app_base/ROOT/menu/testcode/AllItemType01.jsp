<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="allDataTypeReq" class="kr.pe.codda.impl.message.AllItemType.AllItemType" scope="request" /><%
%><jsp:useBean id="allItemTypeRes" class="kr.pe.codda.impl.message.AllItemType.AllItemType" scope="request" /><%
	Boolean isSame = (Boolean) request.getAttribute("isSame");	
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
				<td colspan="2" style="text-align:center">네트워크 통신상 모든 데이타 타입 검사</td>
			</tr>
			<tr>
				<td colspan="2" style="text-align:center">
				서버와 클라이 언트 사이에 모든 데이터 타입별 최대/최소/중간값이 잘 전달되는지를 검사한다.<br/>
				서버에서는 입력 메세지를 그대로 복사하여 전달한다.  
				</td>
			</tr>
			<tr>
				<td colspan=2 style="text-align:center">AllDataType 입력메세지</td>
			</tr>
			<tr>
				<td colspan=2 style="text-align:left"><%=HtmlStringUtil.toHtml4BRString(allDataTypeReq.toString())%></td>
			</tr>
			<tr>
				<td style="text-align:center">출력 비교 결과</td><%
					if (isSame) {
				%><td style="color:blue;text-align:center">성공</td><%
					} else {
				%><td style="color:red;text-align:center">실패</td><%
					}
				%>
			</tr>

			<tr>
				<td colspan=2 style="text-align:center">AllDataType 출력메세지</td>
			</tr>
			<tr>
				<td colspan=2 style="text-align:left"><%=HtmlStringUtil.toHtml4BRString(allItemTypeRes.toString())%></td>
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

