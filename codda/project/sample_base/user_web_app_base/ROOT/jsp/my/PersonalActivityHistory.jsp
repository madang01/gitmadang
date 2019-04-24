<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberActivityType"%><%
%><%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%	
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page import="kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="personalActivityHistoryRes" class="kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes" scope="request" /><%AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);%><!DOCTYPE html>
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
	var rsa = new RSAKey();
	
	function goPersonalInformation(targetUserID, pageNo) {
		document.location.href = "/servlet/PersonalInformation?targetUserID="+targetUserID+"&pageNo="+pageNo;
	}

	function goTreeDetailPage(boardID, boardNo) {
		var detailPageURL = "/servlet/BoardDetail?boardID="+boardID+"&boardNo="+boardNo;
		
		window.open(detailPageURL, "", "width=800,height=600");
	}
	
	function goGroupDetailPage(boardID, groupNo, boardNo) {
		var detailPageURL = "/servlet/BoardDetail?boardID="+boardID+"&boardNo="+groupNo+"&interestedBoadNo="+boardNo;
		document.location.href = detailPageURL;
	}

	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
		
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
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
		
		// String sourceSubject = ;
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("						");
		out.write("<tr>");
		out.write("<td>");
		
		if (MemberActivityType.WRITE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 <strong>본문글</strong>(#");
			out.write(String.valueOf(personalActivity.getBoardNo()));			
			out.write(")을 ");
			out.write(WebCommonStaticUtil.FULL_DATE_FORMAT.format(personalActivity.getRegisteredDate()));
			out.write("에 작성하셨습니다");
			out.write("</p>");
		} else if (MemberActivityType.REPLY.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 <strong>댓글</strong>(#");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")을 ");
			out.write(WebCommonStaticUtil.FULL_DATE_FORMAT.format(personalActivity.getRegisteredDate()));
			out.write("에 작성하셨습니다");
			out.write("</p>");
		} else if (MemberActivityType.DELETE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 게시글(#");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")을 ");
			out.write(WebCommonStaticUtil.FULL_DATE_FORMAT.format(personalActivity.getRegisteredDate()));
			out.write("에 <strong>삭제</strong> 하였습니다");
			out.write("</p>");
		} else if (MemberActivityType.VOTE.equals(memberActivityType)) {
			out.write("<p>");		
			out.write(personalActivity.getBoardName());
			out.write(" 게시판에서 게시글(#");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")을 ");
			out.write(WebCommonStaticUtil.FULL_DATE_FORMAT.format(personalActivity.getRegisteredDate()));
			out.write("에 <strong>추천</strong> 하셨습니다");
			out.write("</p>");
		}		
		
		if (BoardListType.TREE.equals(boardListType)) {
			out.write("<p>");
			out.write("<a href\"#\" onClick=goTreeDetailPage(");
			out.write(String.valueOf(personalActivity.getBoardID()));
			out.write(",");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")");
			out.write(">");
			out.write(StringEscapeActorUtil.replace(personalActivity.getSourceSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("</a>");
			out.write("</p>");
		} else {
			out.write("<p>");
			out.write("<a href\"#\" onClick=goGroupDetailPage(");
			out.write(String.valueOf(personalActivity.getBoardID()));
			out.write(",");
			out.write(String.valueOf(personalActivity.getGroupNo()));
			out.write(",");
			out.write(String.valueOf(personalActivity.getBoardNo()));
			out.write(")");
			out.write(">");
			out.write(StringEscapeActorUtil.replace(personalActivity.getSourceSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("</a>");
			out.write("</p>");
		}
		
		out.write("</td>");
		out.write("</tr>");
		
	}
					
	if (personalActivityHistoryRes.getTotal() > 1) {
		final int pageNo = personalActivityHistoryRes.getPageNo();
		final int pageSize = personalActivityHistoryRes.getPageSize();
		
		
		long startPageNo = 1 + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE*(long)((pageNo - 1) / WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE);
		long endPageNo = Math.min(startPageNo + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE, 
		(personalActivityHistoryRes.getTotal() + pageSize - 1) / pageSize);
		
		out.write("<tr><td><ul class=\"pagination pagination-sm\">");
		
		if (startPageNo > 1) {			
			out.write("<li class=\"previous\"><a href=\"#\" onClick=\"goPersonalInformation('");
			out.write(personalActivityHistoryRes.getTargetUserID());
			out.write("',");
			out.write(String.valueOf(startPageNo-1));
			out.write(")\">이전</a></li>");
		} else {
			out.write("<li class=\"disabled previous\"><a href=\"#\">이전</a></li>");
		}
		
		for (int i=0; i < WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE; i++) {
			long workingPageNo = startPageNo + i;
			if (workingPageNo > endPageNo) break;

			if (workingPageNo == pageNo) {
				out.write("<li class=\"active\"><a href=\"#\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			} else {
				out.write("<li><a href=\"#\" onClick=\"goPersonalInformation('");
				out.write(personalActivityHistoryRes.getTargetUserID());
				out.write("',");
				out.write(String.valueOf(workingPageNo));
				out.write(")\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			}
		}
		
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {		
			out.write("<li class=\"next\"><a href=\"#\" onClick=\"goPersonalInformation('");
			out.write(personalActivityHistoryRes.getTargetUserID());
			out.write("',");
			out.write(String.valueOf(startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE));
			out.write(")\">다음</a></li>");
		} else {
			out.write("<li class=\"disabled next\"><a href=\"#\">다음</a></li>");
		}
		
		out.write("</ul></td></tr>");
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