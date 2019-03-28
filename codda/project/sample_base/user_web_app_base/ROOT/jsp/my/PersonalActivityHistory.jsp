<%@page import="kr.pe.codda.weblib.common.MemberActivityType"%>
<%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%	
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page import="kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="personalActivityHistoryRes" class="kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes" scope="request" /><%
	AccessedUserInformation accessedUserformation = getAccessedUserInformation(request);
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
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

<script src="/js/common.js"></script>
<script type='text/javascript'>
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
		
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		expandTextarea('contentsInWritePart');
	}
	
	window.onload = init;
</script>
</head>
<body>
<div class="header">
	<div class="container">
<%= getMenuNavbarString(request) %>
	</div>
</div>
<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4><%= personalActivityHistoryRes.getTargetUserNickname() %> 님의 개인 활동 이력</h4></div>
			<div class="panel-body">
				<table class="table">
					<tbody><%
	for (PersonalActivityHistoryRes.PersonalActivity personalActivity : personalActivityHistoryRes.getPersonalActivityList()) {
		
		byte memberActivityTypeValue = personalActivity.getMemberActivityType();
		byte boardListTypeValue = personalActivity.getBoardListType();
		
		MemberActivityType memberActivityType = null;
		try {
			memberActivityType = MemberActivityType.valueOf(memberActivityTypeValue);
		} catch(IllegalArgumentException e) {
			// dead code
			log.warn("알수 없는 회원[{}]의 활동 유형[{}]", personalActivityHistoryRes.getTargetUserID(), memberActivityTypeValue);
			continue;
		}
		
		BoardListType boardListType = null;
		
		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);
		} catch(IllegalArgumentException e) {
			// dead code
			log.warn("알수 없는 회원[{}] 게시글[boardID={}, boardNo={}]의 게시판 목록 유형[{}]", 
					personalActivityHistoryRes.getTargetUserID(), 
					personalActivity.getBoardID(), personalActivity.getBoardNo(),
					boardListTypeValue);
			continue;
		}
		
		String sourceSubject = StringEscapeActorUtil.replace(personalActivity.getSourceSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4);
		
		out.write("<tr>");
		out.write("<td>");
		
		if (MemberActivityType.WRITE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 게시글(");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")을 작성하셨습니다");
			out.write("</p>");
		} else if (MemberActivityType.REPLY.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 댓글(");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")을 작성하셨습니다");
			out.write("</p>");
		} else if (MemberActivityType.DELETE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 게시글(");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")를 삭제 하였습니다");
			out.write("</p>");
		} else if (MemberActivityType.VOTE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 게시글(");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")를 추천 하셨습니다");
			out.write("</p>");
		}		
		
		
		if (BoardListType.TREE.equals(boardListType)) {
			out.write("<p>");
			out.write(sourceSubject);
			out.write("</p>");
		} else {
			out.write("<p>");
			out.write(sourceSubject);
			out.write("</p>");
		}
		
		out.write("</td>");
		out.write("</tr>");
		
	}
%>
					</tbody>
				</table>		
			</div>
		</div>
		</div>
	</div>
</body>
</html>