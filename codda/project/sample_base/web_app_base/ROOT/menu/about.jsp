<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType" %><%
	setSiteTopMenu(request, SiteTopMenuType.INTRODUCE);
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
	<h1>신놀이 프레임워크</h1>
<ol>
<li>
	<dl>
		<dt>신놀이 프레임워크란?</dt>
		<dd> 자바로 개발된 메시지 주도 개발 프레임워크입니다.<br/>
		아래 2가지 요소(신놀이 서버, 클라이언트 서버 접속 라이브러리)로 구성되어 있습니다.
			<ol>
				<li>
					<dl>
						<dt>신놀이 서버</dt>
						<dd>한빛미디어 김성박/송지훈님의 "자바 I/O & NIO 네트워크 프로그래밍" 에서 소개한 AdvancedChatServer 기반으로 만들었습니다.</dd>
					</dl>
				</li>
				<li>
					<dl><dt>서버 접속용 클라이언트 라이브러리</dt>
						<dd>신놀이 서버에 연결하여 메세지를 주고 받을 수 있는 클라이언트 라이브러리</dd>
					</dl>	
				</li>
			</ol>	
		</dd>
	</dl>	
</li>
<li>
	<dl>
		<dt>개발한것을 아파치 라이센스 2.0 으로 공개하는 특별한 이유?</dt>
		<dd>개인적으로 자유 소프트웨어 진영에 공헌을 하고 싶고<br/>
		상용으로도 유용하게 사용했으면 하기에 아파치 라이센스 2.0 을 선택하였습니다.
		</dd>
	</dl>
</li>
<li>
	<dl>
		<dt>개발을 하게된 동기및 목표</dt>
		<dd>메시지 주도 개발 프레임워크를 이용하여 원하는 클라이언트/서버 기반으로하는 서비스<br/>
		(ex 파일 송수신 서비스, 윷놀이 게임 서비스, 웹서비스) 구축을 위해서 만들었습니다.
		</dd>
	</dl>
</li>
<li>
	<dl>
		<dt>메시지 주도 개발 프레임워크의 장점과 단점</dt>
		<dd>
			<ol>
				<li>메시지 주도 개발 프레임워크는 응답 메시지를 서버의 비지니스 로직 완료를 기다릴 필요 없이 가상으로 만들어서 클라이언트 개발을 진행할 수 있는 장점을 갖습니다.
				이러한 점은 전체 개발 시간을 단축시키며 서버/클라이언트 각자 독립적으로 반복적 테스트를 가능하게 하여 결국 품질 개선을 이끌게 합니다.
				다만 시스템이 커지면서 메시지와 비지니스 로직 양이 많아 관리가 어려워 지는데, 설상가상으로 의존 관계가 점점 더 꼬여 이해가 어려워져 더더욱 관리가 어렵게 되는 단점이 있습니다.
				</li>
				
				<li>메시지 주도 개발 프레임워크 서버의 비지니스 로직은 콤포넌트 특성을 갖습니다. 조립하여 새로운 서비스를 제공할 수 있습니다.
		단점으로는 공통 모듈처럼 의존 관계가 생겨 유지 보수를 어렵게 하며 1번 호출이 아닌 여러번 호출로 하나의 서비스를 완성하기때문에 네트워크 비용이 낭비 됩니다.
		그렇다고 네트워크 비용 낭비를 방지하기 위해 1번 호출하기위해서 비지니스 로직들을 통합하여 제공할 경우 나뉘어져 있는것과 통합 모듈을 동시에 잘 관리해야 하기때문에 유지 보수 난이도가 상승합니다.
				</li>
			</ol>
		</dd>
	</dl>
</li>

</ol>
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

