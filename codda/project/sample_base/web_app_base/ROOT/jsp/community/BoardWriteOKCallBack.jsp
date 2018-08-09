<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.common.BoardType"%><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultRes" class="kr.pe.codda.impl.message.MessageResultRes.MessageResultRes" scope="request" /><%
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
		if (! isAdminLoginedIn(request)) {
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

		<h1><%= BoardType.valueOf(Short.parseShort(parmBoardId)).getName() %> 게시판 - 글 저장 결과</h1>
		<br/>
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
			function goList() {
				var g = document.listfrm;
				g.submit();
			}
		</script>
		<form name=listfrm method="post" action="/servlet/BoardList">
		<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
		<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
		</form>
		<form name=frm onsubmit="return false">
			<div>
				<ul>
				<li>
					<dl>
						<dt>성공 여부</dt>
						<dd><%=messageResultRes.getIsSuccess()%></dd>
					</dl>
				</li>
				<li>
					<dl>
						<dt>처리 결과 내용</dt>
						<dd><%=HtmlStringUtil.toHtml4BRString(messageResultRes.getResultMessage())%></dd>
					</dl>
				</li>
				<li>
					<dl>
						<dt>기능</dt>
						<dd><input type="button" onClick="goList()" value="목록으로..." /></dd>
					</dl>
				</li>
				</ul>	
			</div>
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
