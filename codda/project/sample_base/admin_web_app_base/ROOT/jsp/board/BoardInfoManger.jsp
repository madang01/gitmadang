<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardReplyPolicyType"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="boardInfoListRes" class="kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes" scope="request" /><%
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type='text/javascript'>
	function getByteLength(s,b,i,c){
	    for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1);
	    return b;
	}

	function showAddBoardInfoModal() {
		var g = document.addBoardInfoFrm;
		g.reset();
		$('#addBoardInfoModal').modal();
	}

	function goAddBoardInfoFrm(f) {
		if (f.boardName.value == '') {
			alert("게시판 이름을 넣어주세요");
			f.boardName.focus();
			return false;
		}
		
		if (f.boardName.value.length < 2) {
			alert("2글자 이상의 게시판 이름을 다시 넣어주세요");
			f.boardName.focus();
			return false;
		}
		
		if (getByteLength(f.boardName.value) > 30) {
			alert("30 bytes 이하의 게시판 이름을 다시 넣어주세요");
			f.boardName.focus();
			return false;
		}
		
		var g = document.addBoardInfoFrm;
		g.boardName.value = f.boardName.value;
		
		for (var i=0; i < f.boardListType.length; i++) {
			if (f.boardListType[i].checked) {
				g.boardListType.value = f.boardListType[i].value;
				break;
			}
		}
		
		for (var i=0; i < f.boardReplyPolicyType.length; i++) {
			if (f.boardReplyPolicyType[i].checked) {
				g.boardReplyPolicyType.value = f.boardReplyPolicyType[i].value;
				break;
			}
		}
		
		
		for (var i=0; i < f.boardWritePermissionType.length; i++) {
			if (f.boardWritePermissionType[i].checked) {
				g.boardWritePermissionType.value = f.boardWritePermissionType[i].value;
				break;
			}
		}
		
		for (var i=0; i < f.boardReplyPermissionType.length; i++) {
			if (f.boardReplyPermissionType[i].checked) {
				g.boardReplyPermissionType.value = f.boardReplyPermissionType[i].value;
				break;
			}
		}
		
		g.submit();
		
		$('#addBoardInfoModal').modal('toggle');
		
		return false;
	}
	
	function callBackForBoadInfoAddition(boardID) {
		var resultMessageView = document.getElementById("resultMessageView");
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 게시판 정보["+boardID+"]를 추가 하였습니다";
		
		alert(resultMessageView.innerText);
		reload();
	}

	function deleteBoardInfo(boardID) {		
		var g = document.deleteBoardInfoFrm;
		g.boardID.value = boardID;
		g.submit();
	}
	
	function callBackForBoadInfoDeletion(boardID) {
		var resultMessageView = document.getElementById("resultMessageView");
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 게시판 정보["+boardID+"]를 삭제 하였습니다";
		
		alert(resultMessageView.innerText);
		reload();
	}
	
	
	function modifyBoardInfo(boardID) {
		var boardNameObj = document.getElementById('boardName'+boardID);
		var boardReplyPolicyType = $(":input:radio[name=boardReplyPolicyType"+boardID+"]:checked").val();		
		var boardWritePermissionType = $(":input:radio[name=boardWritePermissionType"+boardID+"]:checked").val();
		var boardReplyPermissionType = $(":input:radio[name=boardReplyPermissionType"+boardID+"]:checked").val();
		
		
		if (boardNameObj.value == '') {
			boardNameObj.focus();
			alert("게시판 이름을 넣어 주세요");
			return;
		}

		var g = document.modifyBoardInfoFrm;
		g.boardID.value = boardID;
		g.boardName.value = boardNameObj.value;
		g.boardReplyPolicyType.value = boardReplyPolicyType;
		g.boardWritePermissionType.value = boardWritePermissionType;
		g.boardReplyPermissionType.value = boardReplyPermissionType;
		g.submit();
	}
	
	function callBackForBoadInfoModification(boardID) {
		var resultMessageView = document.getElementById("resultMessageView");
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 게시판 정보["+boardID+"]를 수정 하였습니다";
	}

	function reload() {
		document.location.href = "/servlet/BoardInfoManger"; 
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
	}
	window.onload = init;
</script>
</head>
<body>
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>게시판 정보 관리자 화면</h4></div>
				<div class="panel-body">
					<form name="addBoardInfoFrm" method="post" action="/servlet/BoardInfoAdd" target="hiddenFrame">
						<input type="hidden" name="boardName">
						<input type="hidden" name="boardListType">
						<input type="hidden" name="boardReplyPolicyType">
						<input type="hidden" name="boardWritePermissionType">
						<input type="hidden" name="boardReplyPermissionType">
					</form>
					<form name="deleteBoardInfoFrm" method="post" action="/servlet/BoardInfoDelete" target="hiddenFrame">
						<input type="hidden" name="boardID">
					</form>
					<form name="modifyBoardInfoFrm" method="post" action="/servlet/BoardInfoModify" target="hiddenFrame">
						<input type="hidden" name="boardID">
						<input type="hidden" name="boardName">
						<input type="hidden" name="boardReplyPolicyType">
						<input type="hidden" name="boardWritePermissionType">
						<input type="hidden" name="boardReplyPermissionType">
					</form>
					<div class="btn-group">			
						<button type="button" class="btn btn-primary btn-sm" onClick="showAddBoardInfoModal()">Add Root</button>
						<button type="button" class="btn btn-primary btn-sm" onClick="reload();">Reload</button>				
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>				
					</div>					 
					<div id="resultMessageView"></div>
					<br>
					<form name="frm">
						<div class="form-group">
							<table class="table table-striped">
								<tbody><%
	boolean forBackgroupColor = false;
	for (BoardInfoListRes.BoardInfo board : boardInfoListRes.getBoardInfoList()) {
		short boardID = board.getBoardID();
		String boardName = board.getBoardName(); 
		byte boardListTypeValue = board.getBoardListType();
		byte boardReplyPolicyTypeValue = board.getBoardReplyPolicyType();
		byte boardWritePermissionTypeValue = board.getBoardWritePermissionType();
		byte boardReplyPermissionTypeValue = board.getBoardReplyPermissionType();
		
		BoardListType boardListType = null;
		try {
			boardListType = BoardListType.valueOf(boardListTypeValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("게시판 정보[")
					.append(board.toString())
					.append("]에  알 수 없는 게시판 목록 유형 값[")
					.append(boardListTypeValue)
					.append("이 있습니다").toString();
			
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<tr>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<td class=\"alert alert-warning\">");
			out.write(errorMessage);
			out.write("</td>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("</tr>");
			continue;
		}
		
		BoardReplyPolicyType boardReplyPolicyType = null;
		try {
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardReplyPolicyTypeValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("게시판 정보[")
					.append(board.toString())
					.append("]에  알 수 없는 게시판 댓글 정책 유형 값[")
					.append(boardReplyPolicyTypeValue)
					.append("이 있습니다").toString();
			
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<tr>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<td class=\"alert alert-warning\">");
			out.write(errorMessage);
			out.write("</td>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("</tr>");
			continue;
		}
		
		PermissionType boardWritePermissionType = null;
		try {
			boardWritePermissionType = PermissionType.valueOf(boardWritePermissionTypeValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("게시판 정보[")
					.append(board.toString())
					.append("]에  알 수 없는 본문 쓰기 권한 유형 값[")
					.append(boardWritePermissionTypeValue)
					.append("이 있습니다").toString();
			
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<tr>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<td class=\"alert alert-warning\">");
			out.write(errorMessage);
			out.write("</td>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("</tr>");
			continue;
		}
		
		PermissionType boardReplyPermissionType = null;
		try {
			boardReplyPermissionType = PermissionType.valueOf(boardReplyPermissionTypeValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("게시판 정보[")
					.append(board.toString())
					.append("]에  알 수 없는 댓글 쓰기 권한 유형 값[")
					.append(boardReplyPermissionTypeValue)
					.append("이 있습니다").toString();
			
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<tr>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("				<td class=\"alert alert-warning\">");
			out.write(errorMessage);
			out.write("</td>");
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("</tr>");
			continue;
		}
%>
							<tr>
								<td><strong>게시판 식별자</strong></td>
								<td><%= boardID %></td>
							
								<td><strong>게시판 이름</strong></td>
								<td>
									<input type="text" class="form-control" id="boardName<%=boardID %>" maxlength="30" value="<%= boardName %>">
								</td>
								<td><strong>본문 쓰기 권한</strong></td>
								<td><%
		for (PermissionType permissionType  : PermissionType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardWritePermissionType");
			out.write(String.valueOf(boardID));
			out.write("\"");
			if (permissionType.equals(boardWritePermissionType)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(permissionType.getValue()));
			out.write("\">");
			out.write(permissionType.getName());
			out.write("</label>");
		}
%>
								</td>	
								<td><strong>게시판 목록 유형</strong></td>			
								<td><%= boardListType.getName() %></td>		
							</tr>
							<tr>				
								<td><strong>댓글 정책</strong></td>
								<td colspan="3"><%
		for (BoardReplyPolicyType boardReplyPolicyTypeOfSet  : BoardReplyPolicyType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardReplyPolicyType");
			out.write(String.valueOf(boardID));
			out.write("\"");
			if (boardReplyPolicyTypeOfSet.equals(boardReplyPolicyType)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(boardReplyPolicyTypeOfSet.getValue()));
			out.write("\">");
			out.write(boardReplyPolicyTypeOfSet.getName());
			out.write("</label>");
		}
%>
								</td>				
							
								<td><strong>댓글 쓰기 권한</strong></td>
								<td><%
		for (PermissionType permissionType  : PermissionType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardReplyPermissionType");
			out.write(String.valueOf(boardID));
			out.write("\"");
			if (permissionType.equals(boardReplyPermissionType)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(permissionType.getValue()));
			out.write("\">");
			out.write(permissionType.getName());
			out.write("</label>");				
		}
%>
								</td>
			
								<td><strong>기능</strong></td>
								<td>				
									<button type="button" class="btn btn-primary btn-sm" onClick="deleteBoardInfo(<%= boardID %>)">delete</button>
									<button type="button" class="btn btn-primary btn-sm" onClick="modifyBoardInfo(<%= boardID %>)">modify</button>				
								</td>	
							</tr><%
	}
%>								</tbody>
							</table>
						</div>
					</form>
					
					<!-- Root Menu Modal -->
					<div class="modal fade" id="addBoardInfoModal" role="dialog">
						<div class="modal-dialog">			
							<!-- Modal content-->
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal">&times;</button>
									<h4 class="modal-title">게시판 정보 추가 화면</h4>
								</div>
								<div class="modal-body">
									<form name="addBoardInfoFrmOfModal" method="post" class="form-inline" onSubmit="return goAddBoardInfoFrm(this);" action="/">							
										 <div class="form-group">
										 	<div class="row">
										    	<label for="menuNameForRootMenu">게시판 이름</label>
										    	<input type="text" name="boardName" />
										 	</div>
										 	<div class="row">
										 		<label for="linkURLForRootMenu">게시판 목록 유형</label><%
		for (BoardListType boardListType  : BoardListType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("								    ");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardListType\"");			
			if (boardListType.equals(BoardListType.ONLY_GROUP_ROOT)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(boardListType.getValue()));
			out.write("\">");
			out.write(boardListType.getName());
			out.write("</label>");
		}
%>
										 	</div>
										 	<div class="row">
										 		<label for="linkURLForRootMenu">댓글 정책 유형</label><%
		for (BoardReplyPolicyType boardReplyPolicyType  : BoardReplyPolicyType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("								    ");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardReplyPolicyType\"");			
			if (boardReplyPolicyType.equals(BoardReplyPolicyType.ALL)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(boardReplyPolicyType.getValue()));
			out.write("\">");
			out.write(boardReplyPolicyType.getName());
			out.write("</label>");
		}
%>
										 	</div>
										 	<div class="row">
										 		<label for="linkURLForRootMenu">본문 쓰기 권한 유형</label><%
		for (PermissionType permissionType  : PermissionType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("								    ");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardWritePermissionType\"");			
			if (permissionType.equals(PermissionType.MEMBER)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(permissionType.getValue()));
			out.write("\">");
			out.write(permissionType.getName());
			out.write("</label>");
		}
%>
										 	</div>
										 	<div class="row">
										 		<label for="linkURLForRootMenu">댓글 쓰기 권한 유형</label><%
		for (PermissionType permissionType  : PermissionType.values()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("								    ");
			out.write("<label class=\"radio-inline\"><input type=\"radio\" name=\"boardReplyPermissionType\"");			
			if (permissionType.equals(PermissionType.MEMBER)) {
				out.write(" checked");
			}
			out.write(" value=\"");
			out.write(String.valueOf(permissionType.getValue()));
			out.write("\">");
			out.write(permissionType.getName());
			out.write("</label>");
		}
%>
										 	</div>							 
											<button type="submit" class="btn btn-default">추가</button>
										</div>
									</form>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
								</div>
							</div>			
						</div>
					</div>					
					<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
				</div>
			</div>
		</div>
	</div>
<body>
</html>