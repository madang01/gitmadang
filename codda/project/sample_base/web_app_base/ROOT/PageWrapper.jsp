<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.SiteTopMenuType" %><%

	SiteTopMenuType targetSiteTopMenuType = getSiteTopMenuTypeFromParameter(request, SiteTopMenuType.INTRODUCE);
	setSiteTopMenu(request, targetSiteTopMenuType);
	
	String bodyurl = request.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_BODYURL_FOR_PAGEWRAPPER);
	if (null == bodyurl) {
		bodyurl = "";
	}
	
	setSiteLeftMenu(request, bodyurl);
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
	var inx = bodyurl.indexOf("/servlet/");	
	if (0 == inx) {
		var f = document.directgofrm;
		f.action = bodyurl;
		f.submit();		
	} else {
		top.document.location.href = bodyurl;
	}
		
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
	<div id="contentbody"><%
		String arryOfOutSite[] = { "http://www.3rabbitz.com" };	
		boolean isSinnoriSite = true;

		for (int i=0; i < arryOfOutSite.length; i++) {
			int startInx = bodyurl.indexOf(arryOfOutSite[i]);
			if (startInx ==0 ) {
				isSinnoriSite = false;
				break;
			}			
		}	
		
		if (isSinnoriSite) {
%>

<!-- content here 
============================================================ -->
<jsp:include page="<%=bodyurl%>"  flush="false">
	<jsp:param name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
	<jsp:param name="bodyurl" value="<%=bodyurl%>" />
</jsp:include><%
		} else {
%><iframe src="<%=bodyurl%>" width="600" height="600"></iframe><%
		}
%>
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
