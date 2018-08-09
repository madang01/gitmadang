<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.common.BoardType"%><%
%><%@ page import="kr.pe.codda.impl.message.BoardListRes.BoardListRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardListRes" class="kr.pe.codda.impl.message.BoardListRes.BoardListRes" scope="request" /><%
	// System.out.println(boardListRes.toString());
	// final int pageListSize = 2;
	// final int pageSize = 10;
	
	
	String boardListResJsonString = new Gson().toJson(boardListRes);
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
<script type='text/javascript'>
	var boardListResJsonString = <%= boardListResJsonString %>;

	function getSessionkeyBase64() {
		var privateKey;
		var privateKeyBase64 = sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>');
		
		if (typeof(privateKeyBase64) == 'undefined') {
			privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);
			privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
			
			sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', privateKeyBase64);
		} else {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		}
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));		
	
		return sessionkeyBase64;
	}

	function goWritePage() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}
	
		var g = document.goWriteForm;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}
	
	
	function goDetailPage(boardNo) {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}
	
		var g = document.goDetailForm;
		g.boardNo.value = boardNo;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}
	
	
	function goListPage(pageNo) {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}
	
		var g = document.gofrm;
		g.pageNo.value = pageNo;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function init() {
	}
	
	window.onload = init;
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
<form name=goWriteForm method="post" action="/servlet/BoardWrite">
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="input" />
<input type="hidden" name="boardID" value="<%= boardListRes.getBoardID() %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=goDetailForm method="post" action="/servlet/BoardDetail">
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="input" />
<input type="hidden" name="boardID" value="<%= boardListRes.getBoardID() %>" />
<input type="hidden" name="boardNo" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=gofrm method="post" action="/servlet/BoardList">
<input type="hidden" name="boardID" value="<%= boardListRes.getBoardID() %>" />
<input type="hidden" name="pageNo" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>
	<div class="container-fluid">
		<h3><%= BoardType.valueOf(boardListRes.getBoardID()).getName() %>게시판</h3>		
		<div class="btn-group">
			<button type="button" class="btn btn-primary btn-sm" onClick="goWritePage();">글 작성하기</button>
		</div>			 
		<div id="resultMessageView"></div>
		<div class="row">
			<div class="col-sm-1">번호</div>
			<div class="col-sm-5">제목</div>
			<div class="col-sm-2">작성자</div>
			<div class="col-sm-1">조회수</div>
			<div class="col-sm-1">추천수</div>
			<div class="col-sm-1">최초 작성일</div>
			<div class="col-sm-1">마지막 수정일</div>
		</div><%
	List<BoardListRes.Board> boardList = boardListRes.getBoardList();
	if (null != boardList) {
		for (BoardListRes.Board board : boardList) {
			int depth = board.getDepth();
%>
		<div class="row">
			<div class="col-sm-1"><%= board.getBoardNo() %></div>
			<div class="col-sm-5"><%
			if (depth > 0) {
				for (int i=0; i < depth; i++) {
					out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				out.print("ㄴ");
			}			
%><a href="#" onClick="goDetailPage('<%= board.getBoardNo() %>')"><%= StringReplacementActorUtil.replace(board.getSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></a></div>
			<div class="col-sm-2"><%= StringReplacementActorUtil.replace(board.getNickname(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></div>
			<div class="col-sm-1"><%= board.getViewCount() %></div>
			<div class="col-sm-1"><%= board.getVotes() %></div>
			<div class="col-sm-1"><%= board.getRegisteredDate() %></div>
			<div class="col-sm-1"><%= board.getFinalModifiedDate() %></div>
		</div><%
		}
	}

	if (boardListRes.getTotal() > 1) {
		long pageNo = boardListRes.getPageOffset() / boardListRes.getPageLength() + 1;
		
		long startPageNo = 1 + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE*(long)((pageNo - 1) / WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE);
		long endPageNo = Math.min(startPageNo + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE, 
				(boardListRes.getTotal() + boardListRes.getPageLength() - 1) / boardListRes.getPageLength());

		if (startPageNo > 1) {
%>
		<ul class="pager"><li><a href="#" onClick="goListPage('<%= startPageNo-1 %>')">Previous</a></li></ul><%
		}
%>
		<ul class="pagination"><%
		for (int i=0; i < WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE; i++) {
			long workingPageNo = startPageNo + i;
			if (workingPageNo > endPageNo) break;

			if (workingPageNo == pageNo) {
%>
			<li><%= workingPageNo %></li><%
			} else {
%><li><a href="#" onClick="goListPage('<%= workingPageNo %>')"><%= workingPageNo %></a></li><%
			}
							
		}
%>
		</ul><%
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {
%>
		<ul class="pager"><li><a href="#" onClick="goListPage('<%=startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE%>')">Next</a></li></ul><%
		}
	}
%>	
		<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;visibility:hidden"></iframe>
	</div>
</body>
</html>


