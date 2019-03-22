<%@page import="kr.pe.codda.weblib.common.BoardListType"%>
<%@page import="kr.pe.codda.impl.message.BoardChangeHistoryRes.BoardChangeHistoryRes"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardChangeHistoryRes" class="kr.pe.codda.impl.message.BoardChangeHistoryRes.BoardChangeHistoryRes" scope="request" /><%
	BoardListType boardListType = BoardListType.valueOf(boardChangeHistoryRes.getBoardListType());

	boolean isSubject = BoardListType.TREE.equals(boardListType) || boardChangeHistoryRes.getParentNo() == 0;
	
	
	long boardNoForDetailPage = 0L;
	if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
		boardNoForDetailPage = boardChangeHistoryRes.getGroupNo();
	} else {
		boardNoForDetailPage = boardChangeHistoryRes.getBoardNo();
	}
	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

<script type='text/javascript'>

	function goDetailPage() {
		var g = document.detailFrm;		
		g.submit();
	}

	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
		rsa.setPublic("<%=getModulusHexString(request)%>", "10001");
	}
	
	window.onload = init;
</script>
</head>
<body>
	<div class=header>
		<div class="container">
<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<form name=detailFrm method="get" action="/servlet/BoardDetail">
		<input type="hidden" name="boardID" value="<%=boardChangeHistoryRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" value="<%= boardNoForDetailPage %>" />
	</form>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">게시글 수정 이력 조회</div>
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-group">
								<button type="button" class="btn btn-primary btn-sm" onClick="goDetailPage();">상세 화면으로 돌아가기</button>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12">&nbsp;</div>
					</div><%
	String firstWriteID = null;
	for (BoardChangeHistoryRes.BoardChangeHistory boardChangeHistory : boardChangeHistoryRes.getBoardChangeHistoryList()) {
		if (0 == boardChangeHistory.getHistorySeq()) {
			firstWriteID = boardChangeHistory.getWriterID();
%>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavender;">게시판 번호(#순번)</div>
						<div class="col-sm-1"><%= boardChangeHistoryRes.getBoardNo() %>(#<%= boardChangeHistory.getHistorySeq() %>)</div>
						<div class="col-sm-2" style="background-color:lavender;">최초 작성일</div>
						<div class="col-sm-2"><%= boardChangeHistory.getRegisteredDate() %></div>
						<div class="col-sm-2" style="background-color:lavender;">최초 작성자</div>
						<div class="col-sm-2"><%= boardChangeHistory.getWriterNickname() %></div>						
					</div><%			
		} else {
%>
					<div class="row">
						<div class="col-sm-2" style="background-color:lavender;">게시판 번호(#순번)</div>
						<div class="col-sm-1"><%= boardChangeHistoryRes.getBoardNo() %>(#<%= boardChangeHistory.getHistorySeq() %>)</div>						
						<div class="col-sm-1" style="background-color:lavender;">수정일</div>
						<div class="col-sm-2"><%= boardChangeHistory.getRegisteredDate() %></div><%
			if (! boardChangeHistory.getWriterID().equals(firstWriteID)) {		
%>
						<div class="col-sm-1" style="background-color:lavender;">수정자</div>
						<div class="col-sm-2"><%= boardChangeHistory.getWriterNickname() %></div><%
			}
%>						
					</div><%
		}			
					
					
		if (isSubject) {
%>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">제목</div>
						<div class="col-sm-11"><article><%= StringEscapeActorUtil.replace(boardChangeHistory.getSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></article></div>						
					</div><%
		}
%>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">내용</div>
						<div class="col-sm-11"><article style="white-space:pre-wrap;"><%= StringEscapeActorUtil.replace(boardChangeHistory.getContents(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></article></div>						
					</div><%
	}
%>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
