<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java"
	session="true" autoFlush="true" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%	
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.BoardType"%><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%	
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%	
%><jsp:useBean id="parmBoardNo" class="java.lang.String" scope="request" /><%	
%><jsp:useBean id="boardDetailRes" class="kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes"
	scope="request" /><%
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
	function goURL(bodyurl) {
		top.document.location.href = bodyurl;		
	}

	function getSessionkeyBase64() {
		var sessionkeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>');
		if (typeof(sessionKey) == 'undefined') {
			var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
			var rsa = new RSAKey();
			rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
			var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
			sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', CryptoJS.enc.Base64.stringify(privateKey));
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>', sessionkeyBase64);
		}
	
		return sessionkeyBase64;
	}
	
	function goModify() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goModofyForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goReply() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goReplyForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goVote() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goVoteForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}

	
	function goList() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goListForm;		
		g.submit();
	}

	function goDownload(attachId, attachSeq) {
		var g = document.goDownloadForm;
		g.attachId.value = attachId;
		g.attachSeq.value = attachSeq;
		g.submit();
	}

	function callbackVote(parmVoteResponse) {
		if (parmVoteResponse.isError) {
			alert("성공! "+ parmVoteResponse.message);

			var d = document.getElementById('voteTxt');
			var voteStr = d.innerText;
			d.innerText = ""+(parseInt(voteStr) + 1);
		} else {
			alert("실패! "+parmVoteResponse.message);
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
<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript"
	src="/js/cryptoJS/components/cipher-core-min.js"></script>
<h1><%= BoardType.valueOf(boardDetailRes.getBoardId()).getName() %> 게시판 - 상세 보기</h1>
<br />
<script type="text/javascript">
	
</script>
<form name=goModofyForm method="post" action="/servlet/BoardModify">
	<input type="hidden" name="topmenu" value="<%= getCurrentTopMenuIndex(request) %>" /> 
	<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE%>" value="view" /> 
	<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
	<input type="hidden" name="boardNo" value="<%=parmBoardNo%>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=goReplyForm method="post" action="/servlet/BoardReply">
	<input type="hidden" name="topmenu" value="<%= getCurrentTopMenuIndex(request) %>" /> 
	<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE%>" value="view" />
	<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
	<input type="hidden" name="parentBoardNo" value="<%=parmBoardNo%>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" /> 
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=goVoteForm target=voteResultFrame method="post" action="/servlet/BoardVote">
	<input type="hidden" name="topmenu" value="<%= getCurrentTopMenuIndex(request) %>" />
	<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
	<input type="hidden" name="boardNo" value="<%=parmBoardNo%>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=goListForm method="post" action="/servlet/BoardList">
	<input type="hidden" name="topmenu" value="<%= getCurrentTopMenuIndex(request) %>" /> <input
		type="hidden" name="boardId" value="<%=parmBoardId%>" />
</form>

<form name=goDownloadForm target="downloadResultFrame" method="post" action="/servlet/BoardDownload">
	<input type="hidden" name="topmenu" value="<%= getCurrentTopMenuIndex(request) %>" />
	<input type="hidden" name="attachId" /> <input type="hidden" name="attachSeq" />
</form>

<form name=frm onSubmit="return false">
	<div>
		<%
			if (isLogin(request)) {
				String userId = getLoginUserIDFromHttpSession(request);
		%><input type=button onClick="goReply()" value="댓글" />&nbsp;<%
			if (userId.equals(boardDetailRes.getWriterId())) {
		%><input type="button" onClick="goModify()" value="편집" />&nbsp;<%
			} else {
		%><input type=button onClick="goVote()" value="추천" />&nbsp;<%
			}
			}
		%><input type="button" onClick="goList()" value="목록으로" />
	</div>
	<br />
	<div style="height: 100%">
		<table border="1">
			<tbody>
				<tr>
					<td style="width: 90px">글번호</td>
					<td style="width: 70px"><%=boardDetailRes.getBoardNo()%></td>
					<td style="width: 90px">작성자</td>
					<td colspan=3 style="width: 350px"><%=HtmlStringUtil.toHtml4BRString( boardDetailRes.getNickname())%></td>					
				</tr>
				<tr>
					<td style="width: 70px">조회수</td>
					<td><%=boardDetailRes.getViewCount()%></td>
					<td style="width: 70px">추천수</td>
					<td style="width: 70px" id="voteTxt"><%=boardDetailRes.getVotes()%></td>
					<td style="width: 90px">최근 수정일</td>
					<td><%=boardDetailRes.getModifiedDate()%></td>
				</tr>

				<%
					java.util.List<kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes.AttachFile> attachFileList = boardDetailRes
																				.getAttachFileList();
																		if (null != attachFileList) {
				%>
				<tr>
					<td style="width: 90px">첨부 파일</td>
					<td colspan="5" style="text-align: left;">
						<div>
							<%
								for (kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes.AttachFile attachFile : attachFileList) {
																																							if (isLogin(request)) {
							%><a href="#" onClick="goDownload(<%=boardDetailRes.getAttachId()%>, <%=attachFile.getAttachSeq()%>)"><%=HtmlStringUtil.toHtml4BRString(attachFile.getAttachFileName())%></a><br /><%
								} else {
							%><%=HtmlStringUtil.toHtml4BRString(attachFile.getAttachFileName())%>&nbsp;<%
								}
																																						}
							%>
						</div>
					</td>
				</tr>
				<%
					}
				%>
				<tr>
					<td>제목</td>
					<td colspan="5" style="text-align: left;"><%=HtmlStringUtil.toHtml4BRString(boardDetailRes.getSubject())%></td>
				</tr>
				<tr>
					<td>내용</td>
					<td colspan="5" style="text-align: left;"><%=HtmlStringUtil.toHtml4BRString(boardDetailRes.getContent())%></td>
				</tr>
			</tbody>
		</table>
	</div>
	<br />
	<div>
		<%
			if (isLogin(request)) {
					String userId = getLoginUserIDFromHttpSession(request);
		%><input type=button onClick="goReply()" value="댓글" />&nbsp;<%
			if (userId.equals(boardDetailRes.getWriterId())) {
		%><input type="button" onClick="goModify()" value="편집" />&nbsp;<%
			} else {
		%><input type=button onClick="goVote()" value="추천" />&nbsp;<%
			}
				}
		%><input type="button" onClick="goList()" value="목록으로" />
	</div>
</form>
<iframe name="voteResultFrame" width="0" height="0"></iframe>
<iframe name="downloadResultFrame" width="0" height="0"></iframe>
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
