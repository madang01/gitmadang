<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType" %><%
	setSiteTopMenu(request, SiteTopMenuType.GETTING_STARTED);
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
		<h1>신놀이 개발 프레임워크 시작하기</h1>
		<ol>
			<li>
				<dl>
					<dt>다운로드</dt>
					<dd>
					신놀이를 이용하기 위해 가장 먼저 해야할 일입니다. 다운 로드 방법부터 설명하겠습니다.<br/>
					앞으로 모든 설명은 2가지 운영체제 우분투와 윈도7을 기준으로 설명할 것입니다.<br/>&nbsp;
					</dd>
				</dl>      
				<ol>
					<li>
						<dl>
							<dt>공통</dt>
							<dd>
								먼저 다운 로드할 디렉토리를 만듭니다.<br/>
								아래는 제 컴퓨터 기준의 순차적인 절차입니다. 자신의 컴퓨터에 맞도록 수정하셔야 합니다.<br/>
								<ol>
									<li>
										<dt>우분투</dt>
										<dd>
										터미널에서 (1) cd ~<br/>
										(2) mkdir gitsinnori<br/>
										</dd>
									</li>
									<li>
										<dt>윈도7</dt>
										<dd>
										명령 프롬프트(=cmd) (1) d:<br/>
										(2) mkdir gitsinnori<br/>&nbsp;
										</dd>
									</li>
								</ol>
							</dd>
						</dl>
					</li>
					<li>
						<dl>
							<dt>git 이용 다운로드</dt>
							<dd> git 설치후 신놀이 프레임워크를 다운 로드 받습니다.<br/>
								<ol>
									<li>
										<dt>우분투</dt>
										<dd>
										터미널에서 (1) sudo apt-get install git<br/> 
										(2) git clone https://github.com/SinnoriTeam/gitsinnori.git gitsinnori<br/>
										(3) cd gitsinnori<br/>
										(4) git checkout v1.0<br/>
										</dd>
									</li>
									<li>
										<dt>윈도7</dt>
										<dd>
										(1) msysgit 를 설치합니다.<br/> 
										msysgit 홈피 주소는 http://code.google.com/p/msysgit/ 입니다.<br/>
										홈피 들어가셔서 다운로드후 설치를 진행하시기 바랍니다.<br/>
										git 설치시 참고한 주소 : http://forum.falinux.com/zbxe/index.php?document_srl=588283&mid=lecture_tip<br/>
										주의점) 권장하는 데로 "advenced context menu" 를 꼭 선택해서 설치를 해 주시기 바랍니다. 앞으로 이 기준으로 설명할것이기때문입니다.<br/>
										(2) Windows 탐색기를 이용하여 gitsinnori 디렉토리에서 마우스 오른쪽 버튼을 눌러 나오는 팝업 메뉴에서 <br/>
										"Git Bash Here" 를 선택합니다. 그러면 윈도우 환경에서 우분투랑 비슷한 터미털이 뜨지요.<br/> 
										그 다음은 위에 언급한 우분투 (2) 항에서 (4)항까지 동일합니다.<br/>&nbsp;
										</dd>
									</li>
								</ol>
							</dd>
						</dl>
					</li>
					<li>
						<dl>
							<dt>파일 링크 통한 다운로드</dt>
							<dd>
							이것은 따로 설명 안드려도 되지요.<br/>  
							압축 파일은 "gitsinnori-1.0" 디렉토리로 압축되어 있습니다.<br/>  
							따라서 압축을 푼시면 "gitsinnori-1.0" 디렉토리 밑에 
							3개 파일(.gitignore, LICENSE, README.md)과 1개 폴더(sinnori_framework)을 보실 수 있습니다.
							"gitsinnori-1.0" 디렉토리 밑에 파일들과 폴더 전체를 앞으로 작업할 gitsinnori 로 복사를 하시면 됩니다.<br/>&nbsp;
							</dd>
						</dl>
					</li>
				</ol>
			</li>
			<li>
				<dl>
					<dt>ant build 환경 만들기</dt>
					<dd>
					신놀이 개발 프레임워크는 ant build 를 기준으로 배포를 하기때문에 자바와 Ant 설치되어 있어야 합니다.<br/>
					자바및 ant 설치는 언급하지 않겠습니다.<br/>
					신놀이를 다운 로드 받은 이후에는 "신놀이 ant build 유틸" 프로그램을 수행시킵니다.<br/>
					"신놀이 ant build 유틸" 프로그램은 지정된 설치 경로 기준으로<br/> 
					프로젝트 개발과 구동에 필요한 신놀이 설정파일와 ant.properties 그리고 쉘의 내용중<br/> 
					경로 관련 된 부분을 자동적으로 수정시켜 주는 유틸리티 프로그램입니다.<br/>
					단, 대부분 경로 부분은 자동이지만 수동으로 경로를 지정해야 하는 경우가 있습니다.<br/>
					대표적인 예로 톰켓 설치 여부를 설치로 선택하면 <br/>
					톰캣 라이브러리 경로를 선택해 주어야 톰캣과 연동이 되는 ant 환경이 구축됩니다.<br/>
					하지만 톰켓과 연동은 이곳에서는 다루지 않겠습니다.<br/> 
					따라서 톰켓 설치 여부를 "미 설치" 로 선택 해주세요.
					</dd>
				</dl>
				<ol>
					<li>
						<dt>우분투</dt>
						<dd>
						터미널에서 
						(1) cd sinnori_framework<br/>
						(2) ./SinnoriAntBuildUtil.sh<br/>
						</dd>
					</li>
					<li>
						<dt>윈도7</dt>
						<dd>
						명령 프롬프트(=cmd) (1) cd sinnori_framework<br/>
						(2) SinnoriAntBuildUtil.bat<br/>&nbsp;
						</dd>
					</li>
				</ol>
			</li>
			<li>
				<dl>
					<dt>예제로 제공된 파일 송수신 서버/클라이언트 실행 하기</dt>
					<dd>
					신놀이 프레임워크를 이용하여 파일 송수신 서버/클라이언트를 구현한것을 예제로 제공하고 있습니다.<br/>
					로컬에서 도는 서버와 클라이언트로 동작하는것을 확인해 보세요.
					</dd>
				</dl>
				<ol>
					<li>
						<dt>우분투</dt>
						<dd>
							<ol>
								<li>첫번째 터미널에서
									<dt>파일 송수신 서버 실행</dt>
									<dd>
									(1) cd ~/gitsinnori/sinnori_framework/project/sample_simple_ftp/server_build/<br/>
									(2) ant all<br/>
									(3) ./sinnori_server.sh<br/>
									</dd>
								</li>
								<li>두번째 터미널에서
									<dt>파일 송수신 클라이언트 실행</dt>
									<dd>
									(1) cd ~/gitsinnori/sinnori_framework/project/sample_simple_ftp/client_build/app_build/<br/>
									(2) ant all<br/>
									(3) ./sinnori_appclient.sh<br/>
									</dd>
								</li>
							</ol>

						</dd>
					</li>
					<li>
						<dt>윈도7</dt>
						<dd>
							<ol>
								<li>첫번째 명령 프롬프트에서
									<dt>파일 송수신 서버 실행</dt>
									<dd>
									(1) d:
									(2) cd d:\gitsinnori\sinnori_framework\project\sample_simple_ftp\server_build<br/>
									(3) ant all<br/>
									(4) sinnori_server.bat<br/>
									</dd>
								</li>
								<li>두번째 명령 프롬프트에서
									<dt>파일 송수신 클라이언트 실행</dt>
									<dd>
									(1) d:
									(2) cd d:\/gitsinnori/sinnori_framework/project/sample_simple_ftp/client_build/app_build<br/>
									(3) ant all<br/>
									(4) sinnori_appclient.bat<br/>
									</dd>
								</li>
							</ol>
						</dd>
					</li>
				</ol>
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

       
