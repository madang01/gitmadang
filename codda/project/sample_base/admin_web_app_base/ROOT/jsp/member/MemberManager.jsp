<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberStateType"%><%
%><%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.MemberSearchRes.MemberSearchRes.Member"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="memberSearchRes" class="kr.pe.codda.impl.message.MemberSearchRes.MemberSearchRes" scope="request" /><%

	SimpleDateFormat daySimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat timeSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	final MemberStateType memberStateTypeForSearch = MemberStateType.valueOf(memberSearchRes.getMemberState());
	
	Calendar fromCalendar = (Calendar)request.getAttribute("fromCalendar");	
	Calendar toCalendar = (Calendar)request.getAttribute("toCalendar");
	
	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<link href="/tui/date-picker/tui-date-picker.css" rel="stylesheet">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/tui/code-snippet/tui-code-snippet.js"></script>
<script type="text/javascript" src="/tui/date-picker/tui-date-picker.js"></script>

<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script src="/js/common.js"></script>

<script type='text/javascript'>
	function buildPrivateKey() {
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);	
		return privateKey;
	}
	
	function putNewPrivateKeyToSessionStorage() {
		var newPrivateKey = buildPrivateKey();
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', newPrivateKeyBase64);
		
		return newPrivateKeyBase64;
	}
	
	function getPrivateKeyFromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {			
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		var privateKey = null;
		try {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		} catch(err) {
			console.log(err);
			throw err;
		}
		
		return privateKey;
	}
	
	function getSessionkeyBase64FromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		
		var rsa = new RSAKey();	
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		return CryptoJS.enc.Base64.stringify(sessionKey);
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		return iv;
	}


	function goMemberManger() {
		var f = document.memberMangerFrm;
		var g = document.goMemberManagerFrm;
		
		var regexSearchID = /^[A-Za-z0-9]{4,15}$/;
		
		if (f.searchID.value != '') {
			if (!regexID.test(f.searchID.value)) {
				var errmsg = "검색할 아이디는 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요";
				alert(errmsg);
				return;
			}
		}
		
		
		var iv = buildIV();
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.memberState.value = f.memberState[f.memberState.selectedIndex].value;
		g.searchID.value = f.searchID.value;
		g.fromDate.value = f.fromDate.value;
		g.toDate.value = f.toDate.value;
		g.pageNo.value = 1;
		
		g.submit();		
	}
	
	function goPage(pageNo) {
		var g = document.goMemberManagerFrm;
		var iv = buildIV();
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.memberState.value = <%= memberStateTypeForSearch.getValue() %>;
		g.searchID.value = document.getElementById("searchIDForSearch").innerText;
		g.fromDate.value = document.getElementById("fromDateForSearch").innerText;
		g.toDate.value = document.getElementById("toDateForSearch").innerText;
		g.pageNo.value = pageNo;
		
		g.submit();	
	}
	
	function goMemberBlock(targetUserID) {
		
		if (! confirm(targetUserID + " 님을 차단 하시겠습니까?")) {
			return;
		}
	
		var g = document.goMemberBlockFrm;
		var iv = buildIV();
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.targetUserID.value = targetUserID;
		
		g.submit();
	}
	
	function callBackForMemberBlockProcess() {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> "+document.goMemberBlockFrm.targetUserID.value+" 님 차단 처리가 완료 되었습니다";
		
		alert(resultMessageDiv.innerText);
		
		goPage(1);
	}
	
	function goMemberUnBlock(targetUserID) {
		if (! confirm(targetUserID + " 님을 차단 해제 하시겠습니까?")) {
			return;
		}
		
	
		var g = document.goMemberUnBlockFrm;
		var iv = buildIV();
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.targetUserID.value = targetUserID;
		
		g.submit();
	}

	function callBackForMemberUnBlockProcess() {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> "+document.goMemberUnBlockFrm.targetUserID.value+" 님 차단 해제 처리가 완료 되었습니다";
		
		alert(resultMessageDiv.innerText);
		
		goPage(1);
	}
	
	function clickHiddenFrameButton(thisObj) {		
		var hiddenFrame = document.getElementById("hiddenFrame");
		
		if (hiddenFrame.style.display == 'none') {
			thisObj.innerText = "Hide Hidden Frame";
			hiddenFrame.style.display = "block";			
		} else {
			thisObj.innerText = "Show Hidden Frame";
			hiddenFrame.style.display = "none";
		}
	}
	
	function init() {
		var f = document.memberMangerFrm;
		f.searchID.value = document.getElementById("searchIDForSearch").innerText;		
		
	
		 var fromDatePicker = new tui.DatePicker('#datepicker-fromDate-container', {
	            date: new Date(<%= fromCalendar.get(Calendar.YEAR) %>,
	    				<%= fromCalendar.get(Calendar.MONTH) %>,
	    				<%= fromCalendar.get(Calendar.DAY_OF_MONTH) %>), 
	            input: {
	                element: '#datepicker-fromDate',
	                format: 'yyyyMMdd'
	            }
	        });
		 
		 var toDatePicker = new tui.DatePicker('#datepicker-toDate-container', {
	            date: new Date(<%= toCalendar.get(Calendar.YEAR) %>,
	    				<%= toCalendar.get(Calendar.MONTH) %>,
	    				<%= toCalendar.get(Calendar.DAY_OF_MONTH) %>),
	            input: {
	                element: '#datepicker-toDate',
	                format: 'yyyyMMdd'
	            }
	        });
		 
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
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>회원 관리자</h4></div>				
				<div class="panel-body">
					<form name="goMemberManagerFrm" method="post" action="/servlet/MemberManager">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="memberState">
						<input type="hidden" name="searchID">
						<input type="hidden" name="fromDate">
						<input type="hidden" name="toDate">
						<input type="hidden" name="pageNo">
					</form>
					<form name="goMemberBlockFrm" target="hiddenFrame" method="post" action="/servlet/MemberBlockProcess">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="targetUserID">
					</form>
					<form name="goMemberUnBlockFrm" target="hiddenFrame" method="post" action="/servlet/MemberUnBlockProcess">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="targetUserID">
					</form>
					<div id="searchIDForSearch" style="display:none;"><%= toEscapeHtml4(memberSearchRes.getSearchID()) %></div>
					<div id="fromDateForSearch" style="display:none;"><%= toEscapeHtml4(memberSearchRes.getFromDateString()) %></div>
					<div id="toDateForSearch" style="display:none;"><%= toEscapeHtml4(memberSearchRes.getToDateString()) %></div>
					
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
					</div>					
					<div id="resultMessage"></div><br>								
								
					<form class="form-horizontal" name="memberMangerFrm" onsubmit="goMemberManger(); return false;">
						<div class="form-group">
							<label class="col-sm-1 control-label" for="memberState">회원 상태:</label>
							<div class="col-sm-11"><select class="form-control" id="memberState" name="memberState"><%
	for (MemberStateType memberStateType : MemberStateType.values()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("							");
		out.write("<option value=\"");
		out.write(String.valueOf(memberStateType.getValue()));
		out.write("\"");
		if (memberStateType.equals(memberStateTypeForSearch)) {
			out.write(" selected");
		}
		out.write(">");		
		out.write(toEscapeHtml4(memberStateType.getName()));
		out.write("</option>");
	}
%>
							</select></div>						
						</div>
						<div class="form-group">
							<label class="col-sm-1 control-label" for="searchID">아이디:</label>
							<div class="col-sm-11"><input type="text" class="form-control" maxlength=15 id="searchID" placeholder="검색할 아이디를 입력해 주세요" name="searchID"></div>
						</div>
						<div class="form-group">
							<label class="col-sm-1 control-label" for="datepicker-fromDate">시작일자:</label>
							<div class="col-sm-3"><div class="tui-datepicker-input tui-datetime-input tui-has-focus">
								<input type="text" readonly name="fromDate" id="datepicker-fromDate" aria-label="Date-Time">
								<span class="tui-ico-date"></span>
							</div>
							<div id="datepicker-fromDate-container" style="margin-top: -1px;"></div></div>
							
							<label class="col-sm-1 control-label" for="datepicker-toDate">종료일자:</label>
							<div class="col-sm-3"><div class="tui-datepicker-input tui-datetime-input tui-has-focus">
								<input type="text" readonly name="toDate" id="datepicker-toDate" aria-label="Date-Time">
								<span class="tui-ico-date"></span>
							</div>
							<div id="datepicker-toDate-container" style="margin-top: -1px;"></div></div>						
						</div>					
						<div class="form-group">
							<div class="col-sm-12">
							<button type="submit" class="btn btn-default">검색</button>
							</div>
						</div>										
					</form>
					<table class="table">
						<thead>
							<tr>
								<th colspan="4"><h4>검색 조건</h4></th>
							</tr>
							<tr>
								<th>회원상태</th>
								<th>아이디</th>
								<th>시작일</th>
								<th>종료일</th>
							</tr>						
						</thead>
						<tbody>
							<tr>
								<td><%= toEscapeHtml4(memberStateTypeForSearch.getName()) %></td>
								<td><%= toEscapeHtml4(memberSearchRes.getSearchID()) %></td>
								<td><%= daySimpleDateFormat.format(fromCalendar.getTime()) %></td>
								<td><%= daySimpleDateFormat.format(toCalendar.getTime()) %></td>
							</tr>
						</tbody>
					</table>
					<table class="table">
						<thead>
							<tr>
								<th>아이디</th>
								<th>별명</th>
								<th>비밀번호 틀린 횟수</th>
								<th>비밀번호 마지막 수정 일자</th>
								<th>회원 상태 마지막 수정 일자</th>
								<th>가입일</th>
							</tr>
						</thead>
						<tbody><%
					
	if (memberSearchRes.getCnt() == 0) {
%>
							<tr>
								<td colspan="6" align="center">검색 결과가 없습니다</td>
							</tr><%
	} else {	
	
		for (Member member : memberSearchRes.getMemberList()) {
%>
							<tr>
								<td><%
			if (MemberStateType.OK.equals(memberStateTypeForSearch)) {
%>
									<div class="dropdown">
										<button class="btn btn-default dropdown-toggle" type="button" id="menuOf<%= member.getUserID() %>" data-toggle="dropdown"><%= member.getUserID() %>
										<span class="caret"></span></button>
										<ul class="dropdown-menu" role="menu" aria-labelledby="menuOf<%= member.getUserID() %>">
											<li role="presentation"><a role="menuitem" tabindex="-1" href="#" onClick="goMemberBlock('<%= member.getUserID() %>')">차단</a></li>
										</ul>
	          						</div><%				
			} else if (MemberStateType.BLOCK.equals(memberStateTypeForSearch)) {
%>
									<div class="dropdown">
										<button class="btn btn-default dropdown-toggle" type="button" id="menuOf<%= member.getUserID() %>" data-toggle="dropdown"><%= member.getUserID() %>
										<span class="caret"></span></button>
										<ul class="dropdown-menu" role="menu" aria-labelledby="menuOf<%= member.getUserID() %>">
											<li role="presentation"><a role="menuitem" tabindex="-1" href="#" onClick="goMemberUnBlock('<%= member.getUserID() %>')">차단해제</a></li>
										</ul>
	          						</div><%
			} else {
%>
								<%= member.getUserID() %><%
			}
%>
								
	          					</td>
								<td><%= toEscapeHtml4(member.getNickname()) %></td>
								<td><%= member.getPasswordFailCount() %></td>
								<td><%= timeSimpleDateFormat.format(member.getLastPasswordModifiedDate()) %></td>
								<td><%= timeSimpleDateFormat.format(member.getLastStateModifiedDate()) %></td>
								<td><%= timeSimpleDateFormat.format(member.getRegisteredDate()) %></td>
							</tr><%
		}
	}
%>
						</tbody>
					</table>
					<ul class="pager"><%
				
	if (memberSearchRes.getPageNo() > 1) {
%>
						<li><a href="#" onClick="goPage(<%= memberSearchRes.getPageNo() - 1 %>)">이전</a></li><% 
	}

	if (memberSearchRes.getIsNextPage()) {
%>
						<li><a href="#" onClick="goPage(<%= memberSearchRes.getPageNo() + 1 %>)">다음</a></li><%
		
	}
%>
					</ul>
					<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>			
				</div>		
			</div>
		</div>
	</div>
</body>
</html>