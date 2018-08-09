<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.common.BoardType"%><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
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
	
			sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', CryptoJS.enc.Base64.stringify(privateKey));
			sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY %>', sessionkeyBase64);
		}
	
		return sessionkeyBase64;
	}
	function save() {
		var f = document.frm;
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return;
		}

		if ('' == f.content.value) {
			alert("내용을 넣어 주세요.");
			f.content.focus();
			return;
		}

		var g = document.gofrm;		
		g.subject.value = f.subject.value;
		g.content.value = f.content.value;
		g.attachId.value = document.attachForm.attachId.value;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goList() {
		var g = document.listForm;
		g.submit();
	}

	function addNewAttachFile() {		
		var uploadFileMaxCnt = <%=WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT%>;
		var prefixOfOldChildDiv = 'oldChildDiv';
		var prefixOfNewChildDiv = 'newChildDiv';
		var maxIndex = -1;		
		var uploadFileCnt = 0;

		var oldFileListDiv = document.getElementById('oldFileListDiv');
		for (var i=0;i < oldFileListDiv.childNodes.length; i++) {
			var childNode = oldFileListDiv.childNodes[i];
			
			if (childNode.id.indexOf(prefixOfOldChildDiv) == 0) {
				uploadFileCnt++;				
			}
		}

		var newFileListDiv = document.getElementById('newFileListDiv');
		for (var i=0;i < newFileListDiv.childNodes.length; i++) {
			var childNode = newFileListDiv.childNodes[i];
			
			if (childNode.id.indexOf(prefixOfNewChildDiv) == 0) {
				uploadFileCnt++;
				var numStr = childNode.id.substring(prefixOfNewChildDiv.length);

				var num = parseInt(numStr);

				if (maxIndex < num) {
					maxIndex = num;
				}	
			}						
		}
	
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		

		var inx = maxIndex+1;

		var newChilddiv = document.createElement('div');

		var divIdName = prefixOfNewChildDiv+inx;		

		newChilddiv.setAttribute('id',divIdName);
		
		newChilddiv.innerHTML = "<input type=\"file\" name=\"newAttachFile\" id=\"newAttachFile"+inx+"\" style=\"width:600px\" />&nbsp;<a href=\'#\' id=newAttachFileDeleteLink"+inx+" style=\"visibility:hidden\" onclick=\'removeNewAttachFile(\""+divIdName+"\")\'>삭제</a>";


		newFileListDiv.appendChild(newChilddiv);

		fixVisibleOfNewAttachFileDeleteLinks();
	}

	function removeAllNewAttachFiles() {
		var d = document.getElementById('newFileListDiv');
		for(var i=0; i < d.childNodes.length; i++) {
			d.removeChild(d.childNodes[i]);
		}
	}

	function removeAllNewAttachFiles() {
		var d = document.getElementById('newFileListDiv');
		for(var i=0; i < d.childNodes.length; i++) {
			d.removeChild(d.childNodes[i]);
		}
	}

	function removeNewAttachFile(divIdName) {
		var d = document.getElementById('newFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		d.removeChild(olddiv);
	}

	function fixVisibleOfNewAttachFileDeleteLinks() {
		var f = document.attachForm;

		if (f.newAttachFile.length != undefined) {
			for (var i=0;i < f.newAttachFile.length; i++) {
				var newAttachFile = f.newAttachFile[i];
				var inx = newAttachFile.id.substring("newAttachFile".length);				

				var newAttachFileDeleteLink = document.getElementById('newAttachFileDeleteLink'+inx);

				if (i+1 < f.newAttachFile.length ) newAttachFileDeleteLink.style.visibility = "visible";
				else newAttachFileDeleteLink.style.visibility = "hidden";				
			}
		}
	}

	var boardUploadFileOutDTO;

	function restoreOldFiles() {
		var f = document.attachForm;		

		f.attachId.value = boardUploadFileOutDTO.attachId;		

		var prefixOfChildDiv = 'oldChildDiv';
		var oldFileListDiv = document.getElementById('oldFileListDiv');

		for (var i=0; i < boardUploadFileOutDTO.oldAttachFileList.length; i++) {
			var divIdName = prefixOfChildDiv+i;
			
			removeOldAttachFile(divIdName);

			var oldChilddiv = document.createElement('div');			

			oldChilddiv.setAttribute('id',divIdName);		

			oldChilddiv.innerHTML = "<input type=\"hidden\" name=\"oldAttachSeq\" value=\""+boardUploadFileOutDTO.oldAttachFileList[i].attachSeq+"\" /><input type=\"text\" name=\"oldAttachFileName\" disabled=\"disabled\" style=\"width:550px\" value=\""+boardUploadFileOutDTO.oldAttachFileList[i].attachFileName+"\" />&nbsp;<a href=\'#\' onclick=\'removeOldAttachFile(\""+divIdName+"\")\'>삭제</a>";

			oldFileListDiv.appendChild(oldChilddiv);
			
		}
	}

	function callbackUpload(parmBoardUploadFileOutDTO) {
		removeAllOldAttachFiles();
		removeAllNewAttachFiles();
		addNewAttachFile();

		if (parmBoardUploadFileOutDTO.isError) {
			alert(parmBoardUploadFileOutDTO.errorMessage);
		} else {
			boardUploadFileOutDTO = parmBoardUploadFileOutDTO;

			var d = document.getElementById('oldFileMenuDiv');
			d.style.visibility = "visible";

			restoreOldFiles();

			alert("서버 첨부 파일 반영 처리가 완료되었습니다.");
		}		
	}

	function removeAllOldAttachFiles() {
		var d = document.getElementById('oldFileListDiv');		
		for(var i=0; i < d.childNodes.length; i++) {
			d.removeChild(d.childNodes[i]);
		}	
	}

	function removeOldAttachFile(divIdName) {
		var d = document.getElementById('oldFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		if (olddiv != undefined) {
			d.removeChild(olddiv);
		}		
	}

	function init() {
		removeAllOldAttachFiles();
		removeAllNewAttachFiles();
		addNewAttachFile();
	}

	window.onload=init;
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
		if (! isAdminLoginedIn(request)) {
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

<h1><%= BoardType.valueOf(Short.parseShort(parmBoardId)).getName() %> 게시판 - 글 작성하기</h1>
<br/>

<form name=gofrm method="post" action="/servlet/BoardWrite">
<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE%>" value="proc" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="subject" />
<input type="hidden" name="content" />
<input type="hidden" name="attachId" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>

<form name=listForm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
</form>
<form name=frm onSubmit="return false">
	<div>
		<ul>
		<li>
			<dl>
				<dt>제목</dt>
				<dd><input type="text" name="subject" size="50" /></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>내용</dt>
				<dd><textarea name="content" style="width: 500px; height: 220px;"></textarea></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>기능</dt>
				<dd><input type="button" onClick="save(); return;" value="저장" /> <input type="button" onClick="goList()" value="취소" /></dd>
			</dl>
		</li>
		</ul>	
	</div>
</form>
<form name="attachForm" target="uploadResultFrame" action="/servlet/BoardUpload" method="post" enctype="multipart/form-data">
	<input type="hidden" name="attachId" value="0" />
	
	<div id="oldFileMenuDiv" style="visibility:hidden"><p><a href="#" onclick="restoreOldFiles();">첨부한 파일 복원</a></p></div>
	<div id="oldFileListDiv"></div><br/>
	
	<p><a href="#" onclick="addNewAttachFile();">첨부 파일 추가</a></p>
	<!-- 주의점 myDiv 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
	<div id="newFileListDiv"></div><br/>

	<input type="submit" value="서버에 첨부 파일 변경 내역 반영" />	
</form><iframe name="uploadResultFrame" width="0" height="0"></iframe>

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
