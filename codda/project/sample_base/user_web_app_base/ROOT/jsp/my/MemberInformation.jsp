<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberStateType"%><%
%><%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberRoleType"%><%	
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="memberInformationRes" class="kr.pe.codda.impl.message.MemberInformationRes.MemberInformationRes" scope="request" /><%AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);%><!DOCTYPE html>
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
			<div class="panel-heading"><h4>회원 정보 조회</h4></div>
			<div class="panel-body">
				<table class="table">
					<tbody><% 
	if (null != memberInformationRes.getTargetUserID() && ! memberInformationRes.getTargetUserID().isEmpty()) {
		MemberRoleType memberRoleType = MemberRoleType.valueOf(memberInformationRes.getRole());
		MemberStateType memberStateType = MemberStateType.valueOf(memberInformationRes.getState());
		
%>
						<tr><td>아이디</td><td><%= memberInformationRes.getTargetUserID() %></td></tr>
						<tr><td>별명</td><td><%= memberInformationRes.getNickname() %></td></tr>
						<tr><td>상태</td><td><%= memberStateType.getName() %></td></tr>
						<tr><td>역활</td><td><%= memberRoleType.getName() %></td></tr>
						<tr><td>최초 가입일</td><td><%= WebCommonStaticUtil.FULL_DATE_FORMAT.format(memberInformationRes.getRegisteredDate()) %></td></tr><%
		if (accessedUserformation.isAdmin() || accessedUserformation.getUserID().equals(memberInformationRes.getTargetUserID())) {
%>
						<tr><td>이메일</td><td><%= memberInformationRes.getEmail() %></td></tr>
						<tr><td>마지막 별명 수정일</td><td><%= WebCommonStaticUtil.FULL_DATE_FORMAT.format(memberInformationRes.getLastNicknameModifiedDate()) %></td></tr>
						<tr><td>마지막 이메일 수정일</td><td><%= WebCommonStaticUtil.FULL_DATE_FORMAT.format(memberInformationRes.getLastEmailModifiedDate()) %></td></tr>
						<tr><td>마지막 비밀번호 수정일</td><td><%= WebCommonStaticUtil.FULL_DATE_FORMAT.format(memberInformationRes.getLastPasswordModifiedDate()) %></td></tr><%
		}
	} else {
		out.write("<tr><td><p>회원 정보 조회는 지정 받은 사용자에 대해서 조회를 수행합니다. 단 지정된 대상이 없다면 안내 문구만 소개하지만 로그인 했을 경우 로그인 사용자가 지정된 사용자로 대체됩니다.</p></td><td></tr>");
	}
%>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>