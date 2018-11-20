<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="arraySiteMenuRes" class="kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes" scope="request" /><%
	// ArraySiteMenuRes arraySiteMenuRes = (ArraySiteMenuRes)request.getAttribute("arraySiteMenuRes"); 
	
	/* if (null == arraySiteMenuRes) {
		arraySiteMenuRes = new ArraySiteMenuRes();
		
		List<ArraySiteMenuRes.Menu> menuList = new ArrayList<ArraySiteMenuRes.Menu>();
		{
			ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
			menu.setMenuNo(1);
			menu.setParentNo(0);
			menu.setOrderSeq((short)0);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴01");
			menu.setLinkURL("/test01");
			
			menuList.add(menu);
		}
		
		{
			ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
			menu.setMenuNo(2);
			menu.setParentNo(0);
			menu.setOrderSeq((short)1);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴02");
			menu.setLinkURL("/test02");
			
			menuList.add(menu);
		}
		
		{
			ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
			menu.setMenuNo(3);
			menu.setParentNo(0);
			menu.setOrderSeq((short)2);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴03");
			menu.setLinkURL("/test03");
			
			menuList.add(menu);
		}
		
		arraySiteMenuRes.setCnt(menuList.size());
		arraySiteMenuRes.setMenuList(menuList);
	}
	 */
	String menuListResJsonString = new Gson().toJson(arraySiteMenuRes);
	
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
	var menuListResJsonObj = <%= menuListResJsonString %>;
	var __rowIndex;
	
	Array.prototype.insert = function(index) {
	    index = Math.min(index, this.length);
	    arguments.length > 1
	        && this.splice.apply(this, [index, 0].concat([].pop.call(arguments)))
	        && this.insert.apply(this, arguments);
	    return this;
	};

	function makeTextTypeColDivOfList(classAttributeValue, colText) {
		var colDiv = document.createElement("div");
		colDiv.className = classAttributeValue;
		colDiv.innerText = colText;
		
		return colDiv;
	}

	function makeInputTextTypeColDivOfList(classAttributeValue, inputTextID, inputTextMaxLength, inputTextValue) {
		var colDiv = document.createElement("div");
		colDiv.className = classAttributeValue;
		
		var inputText = document.createElement("INPUT");
		inputText.setAttribute("type", "text");
		inputText.setAttribute("class", "form-control");
		inputText.setAttribute("id", inputTextID);
		inputText.setAttribute("maxlength", inputTextMaxLength);
		inputText.setAttribute("value", inputTextValue);
		
		colDiv.appendChild(inputText);
		
		return colDiv;
	}

	function makeFuncColDivOfList(classAttributeValue, funcColButtonList) {
		var colDiv = document.createElement("div");
		colDiv.className = classAttributeValue;
		
		for (var i=0; i <  funcColButtonList.length; i++) {
			colDiv.appendChild(funcColButtonList[i]);	
		}
		
		return colDiv;
	}
	
	function makeGlyphIconButton(classAttributeValue, onClickValue, glyphIconName, buttonText) {
		var glyphIconButton = document.createElement("button");
		glyphIconButton.setAttribute("class", classAttributeValue);			
		glyphIconButton.setAttribute("onClick", onClickValue);				
		var arrowDownSpan = document.createElement("span");
		arrowDownSpan.setAttribute("class", "glyphicon "+glyphIconName);			
		glyphIconButton.appendChild(arrowDownSpan);				
		glyphIconButton.innerHTML += buttonText;
		return glyphIconButton;
	}
	
	function makeTextButton(classAttributeValue, onClickValue, buttonText) {
		var textButton = document.createElement("button");
		textButton.setAttribute("class", classAttributeValue);			
		textButton.setAttribute("onClick", onClickValue);
		textButton.innerText = buttonText;
		return textButton;
	}
	
	function getPrevRowIndexHavingSameDepth(targetRowIndex) {
		var targetDepth = menuListResJsonObj.menuList[targetRowIndex].depth;
		
		for (var i=targetRowIndex-1; i >= 0; i--) {
			if (menuListResJsonObj.menuList[i].depth < targetDepth) {
				return -1;
			}
			if (menuListResJsonObj.menuList[i].depth == targetDepth) {
				return i;
			}
		}
		
		return -1;
	}

	function getNextRowIndexHavingSameDepth(targetRowIndex) {
		var targetDepth = menuListResJsonObj.menuList[targetRowIndex].depth;
		
		for (var i=targetRowIndex+1; i < menuListResJsonObj.cnt; i++) {
			if (menuListResJsonObj.menuList[i].depth < targetDepth) {
				return -1;
			}
			if (menuListResJsonObj.menuList[i].depth == targetDepth) {
				return i;
			}
		}
		
		return -1;
	}
	
	function makeFuncColButtonList(rowIndex) {
		var buttonList = [];
		
		<% /** 상단 이동이 가능하다면 상단 이동 버튼 추가 */ %>
		if (-1 != getPrevRowIndexHavingSameDepth(rowIndex)) {			
			buttonList.push(makeGlyphIconButton("btn btn-primary btn-sm", "moveMenuUp("+menuListResJsonObj.menuList[rowIndex].menuNo+","+rowIndex+");", "glyphicon-arrow-up", "Up"));
		}
		
		<% /** 하단 이동이 가능하다면 상단 이동 버튼 추가 */ %>
		if (-1 != getNextRowIndexHavingSameDepth(rowIndex)) {
			buttonList.push(makeGlyphIconButton("btn btn-primary btn-sm", "moveMenuDown("+menuListResJsonObj.menuList[rowIndex].menuNo+","+rowIndex+");", "glyphicon-arrow-down", "Down"));
		}
		
		buttonList.push(makeTextButton("btn btn-primary btn-sm", "modifyMenu("+menuListResJsonObj.menuList[rowIndex].menuNo+"," + rowIndex + ");", "Modify"));
		
		if ((rowIndex + 1) == menuListResJsonObj.cnt ||  menuListResJsonObj.menuList[rowIndex+1].depth <= menuListResJsonObj.menuList[rowIndex].depth) {
			buttonList.push(makeTextButton("btn btn-primary btn-sm", "deleteMenu("+menuListResJsonObj.menuList[rowIndex].menuNo+"," + rowIndex + ");", "Delete"));
		}
		
		
		buttonList.push(makeTextButton("btn btn-primary btn-sm", "addChildMenu("+menuListResJsonObj.menuList[rowIndex].menuNo+"," + rowIndex + ");", "Add Child"));
		
		return buttonList;
	}
<%	
/**
제   목 : makeRowDivOfList function 의 HTML 디자인 샘플
작성자 : Won Jonghoon
작성일 : 2018.07.10

	<div class="form-group">
		<div class="row">
			<div class="col-sm-1">#menuNo</div>
			<div class="col-sm-1">#parentNo</div>
			<div class="col-sm-1">#depth</div>
			<div class="col-sm-1">#orderSeq</div>
			<div class="col-sm-2">
				<input type="text" class="form-control" id="menuName#rowIndex" maxlength="80" value="#menuName">
			</div>
			<div class="col-sm-4">
				<input type="text" class="form-control" id="linkURL#rowIndex" maxlength="80" value="#linkURL">
			</div>
			<div class="col-sm-2 btn-group">
				if (상단 이동이 가능하다면?) {
					<button class="btn btn-primary btn-sm" onClick="moveMenuUp(#menuNo, #rowIndex);">
						<span class="glyphicon glyphicon-arrow-up"><span> Up
					</button>
				}
				if (하단 이동이 가능하다면?) {
					<button class="btn btn-primary btn-sm" onClick="moveMenuDown(#menuNo, #rowIndex);">
						<span class="glyphicon glyphicon-arrow-down"><span> Up
					</button>
				}
				
				<button class="btn btn-primary btn-sm" onClick="modifyMenu(#menuNo, #rowIndex);">Modify</button>
				<button class="btn btn-primary btn-sm" onClick="addChildMenu(#menuNo, #rowIndex);">Add</button>
			</div>
		</div>
	</div>
*/
%>
	function makeRowDivOfList(rowIndex) {
		var formGroupDiv = document.createElement("div");
		formGroupDiv.className = "form-group";
		
		var rowDiv = document.createElement("div");
		rowDiv.className = "row";		
			
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJsonObj.menuList[rowIndex].menuNo));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJsonObj.menuList[rowIndex].parentNo));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJsonObj.menuList[rowIndex].depth));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJsonObj.menuList[rowIndex].orderSeq));
				
		rowDiv.appendChild(makeInputTextTypeColDivOfList("col-sm-2", "menuName" + rowIndex, 80, menuListResJsonObj.menuList[rowIndex].menuName));			
		rowDiv.appendChild(makeInputTextTypeColDivOfList("col-sm-3", "linkURL" + rowIndex, 80, menuListResJsonObj.menuList[rowIndex].linkURL));	
		
		rowDiv.appendChild(makeFuncColDivOfList("col-sm-3 btn-group", makeFuncColButtonList(rowIndex)));
		
		formGroupDiv.appendChild(rowDiv);
		
		return formGroupDiv;
	}
	
	
	
	function buildListView() {
		var listView = document.getElementById("listView");
		
		while (listView.hasChildNodes()) {
			listView.removeChild(listView.firstChild);
		}		
		
		for (var i=0; i < menuListResJsonObj.cnt; i++) {
			var formGroupDiv = makeRowDivOfList(i);
			
			listView.appendChild(formGroupDiv);
		}		
	}
	
 
	function modifyMenu(menuNo, rowIndex) {
		__rowIndex = rowIndex;
		
		var g = document.modifyMenuFrm;
		g.menuNo.value = menuNo;
		g.menuName.value = document.getElementById("menuName"+rowIndex).value;
		g.linkURL.value = document.getElementById("linkURL"+rowIndex).value;
		g.submit();
	}
	
	function modifyMenuOkCallBack() {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;			
		var g = document.modifyMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 지정한  메뉴[메뉴번호:" + g.menuNo.value + ", 변경전 메뉴명:" + menuListResJsonObj.menuList[rowIndex].menuName + "] 수정이 성공하였습니다";			
		
		menuListResJsonObj.menuList[rowIndex].menuName = g.menuName.value;
		menuListResJsonObj.menuList[rowIndex].linkURL = g.linkURL.value;		
		
		var newRowDivOfList = makeRowDivOfList(rowIndex);
		
		var listView = document.getElementById("listView");
		
		listView.childNodes[rowIndex].display  = 'none';
		
		listView.childNodes[rowIndex] = newRowDivOfList;
				
		listView.childNodes[rowIndex].display  = 'show';
	}
	
	function deleteMenu(menuNo, rowIndex) {
		__rowIndex = rowIndex;
		
		var r = confirm("지정한 메뉴[메뉴번호:" + menuNo + ", 메뉴명:" + menuListResJsonObj.menuList[rowIndex].menuName + "]를 삭제 하시겠습니까?");
		if (r == true) {
			var g = document.deleteMenuFrm;
			g.menuNo.value = menuNo;
			g.submit();
		} else {
			txt = "You pressed Cancel!";
			resultMessageView.setAttribute("class", "alert alert-info");
			resultMessageView.innerHTML = "지정한  메뉴[메뉴번호:"+ menuNo + ", 메뉴명:" + menuListResJsonObj.menuList[rowIndex].menuName+"] 삭제를 취소했습니다";
		}
	}
	
	function deleteMenuOkCallBack() {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;			
		var g = document.deleteMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 지정한  메뉴[메뉴번호:" + g.menuNo.value + ", 메뉴명:" + menuListResJsonObj.menuList[rowIndex].menuName + "]를 삭제 했습니다";			
		
		
		for (var i=rowIndex+1; i < menuListResJsonObj.cnt; i++) {
			menuListResJsonObj.menuList[i].orderSeq--;
		}	
		
		menuListResJsonObj.menuList.splice(rowIndex, 1);
		menuListResJsonObj.cnt--;
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function moveMenuUp(menuNo, rowIndex) {
		__rowIndex = rowIndex;
		
		var g = document.moveMenuUpFrm;
		g.menuNo.value = menuNo;
		g.submit();
	}
	
	function moveMenuUpOkCallBack() {
		var g = document.moveMenuUpFrm;
		var resultMessageView = document.getElementById("resultMessageView");
		
		var sourceRowIndex = __rowIndex;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> " + "지정한 메뉴[메뉴번호:" + g.menuNo.value + ", 메뉴명:" + menuListResJsonObj.menuList[sourceRowIndex].menuName + "]의 상단 이동이 성공하였습니다";
		
		var sourceMenu = menuListResJsonObj.menuList[sourceRowIndex];
		
		var targetRowIndex = getPrevRowIndexHavingSameDepth(sourceRowIndex);
		if (-1 == targetRowIndex) {
			alert("메뉴 상단 이동시 교환에 필요한 한칸 높은 메뉴를 찾지 못했습니다");
			return;
		}
		
		var targetMenu = menuListResJsonObj.menuList[targetRowIndex];
		
		var sourceMenuGroupList = [];
		sourceMenuGroupList.push(sourceMenu);		
		for (var i=sourceRowIndex+1; i < menuListResJsonObj.cnt; i++) {				
			if (sourceMenu.depth >= menuListResJsonObj.menuList[i].depth) {
				break;
			}
			
			sourceMenuGroupList.push(menuListResJsonObj.menuList[i]);
		}		
		
		var targetMenuGroupList = [];		
		for (var i=targetRowIndex; i < sourceRowIndex; i++) {
			targetMenuGroupList.push(menuListResJsonObj.menuList[i]);
		}
		
		var oldOrderSeq = sourceMenu.orderSeq; 
		sourceMenu.orderSeq = targetMenu.orderSeq;
		targetMenu.orderSeq = oldOrderSeq;		
		
		for (var i=0; i < sourceMenuGroupList.length; i++) {
			menuListResJsonObj.menuList[targetRowIndex + i] = sourceMenuGroupList[i];
		}
		
		for (var i=0; i < targetMenuGroupList.length; i++) {
			menuListResJsonObj.menuList[targetRowIndex+sourceMenuGroupList.length + i] = targetMenuGroupList[i];
		}
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function moveMenuDown(menuNo, rowIndex) {
		__rowIndex = rowIndex;
		
		var g = document.moveMenuDownFrm;
		g.menuNo.value = menuNo;				
		g.submit();
	}
	
	function moveMenuDownOkCallBack() {
		var g = document.moveMenuDownFrm;
		var resultMessageView = document.getElementById("resultMessageView");
		
		var rowIndex = __rowIndex;			
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> " + "지정한 메뉴[메뉴번호:" + g.menuNo.value +", 메뉴명:" + menuListResJsonObj.menuList[rowIndex].menuName + "] 하단 이동이 성공하였습니다";
		
		var sourceMenu = menuListResJsonObj.menuList[rowIndex];
		
		var fromMenuList = [];
		fromMenuList.push(sourceMenu);		
		for (var i=rowIndex+1; i < menuListResJsonObj.cnt; i++) {				
			if (sourceMenu.depth >= menuListResJsonObj.menuList[i].depth) {
				break;
			}
			
			fromMenuList.push(menuListResJsonObj.menuList[i]);
		}		
		
		var toMenu = menuListResJsonObj.menuList[rowIndex + fromMenuList.length];
		var toMenuList = [];
		toMenuList.push(toMenu);
		for (var i=rowIndex + fromMenuList.length + 1; i < menuListResJsonObj.cnt; i++) {
			if (sourceMenu.depth >= menuListResJsonObj.menuList[i].depth) {
				break;
			}
			toMenuList.push(menuListResJsonObj.menuList[i]);
		}
		
		var oldOrderSeq = sourceMenu.orderSeq; 
		sourceMenu.orderSeq = toMenu.orderSeq;
		toMenu.orderSeq = oldOrderSeq;
		
		
		for (var i=0; i < toMenuList.length; i++) {
			menuListResJsonObj.menuList[rowIndex+i] = toMenuList[i];
		}
		
		for (var i=0; i < fromMenuList.length; i++) {
			menuListResJsonObj.menuList[rowIndex+toMenuList.length+i] = fromMenuList[i];
		}		
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function addChildMenu(parentNo, rowIndex) {
		__rowIndex = rowIndex;
		
		var g = document.addChildMenuFrm;
		g.reset();
		g.parentNo.value = parentNo;
		$("#childMenuModal").modal();
	}
	
	
	
	function addChildMenuOkCallBack(childMenuAddRes) {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;
		var parentMenu = menuListResJsonObj.menuList[rowIndex];
		var g = document.addChildMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 부모 메뉴["+parentMenu.menuNo+"]에 자식 메뉴["+childMenuAddRes.menuNo+"]를 추가했습니다";
				
		var childMenu = {};
		childMenu.menuNo = childMenuAddRes.menuNo;
		childMenu.parentNo = parentMenu.menuNo;
		childMenu.depth = parentMenu.depth + 1;
		childMenu.orderSeq = childMenuAddRes.orderSeq;
		childMenu.menuName = g.menuName.value;
		childMenu.linkURL = g.linkURL.value;
		
		var nextSameDepthRowIndex = getNextRowIndexHavingSameDepth(rowIndex);
		
		if (-1 == nextSameDepthRowIndex) {
			menuListResJsonObj.menuList.push(childMenu);
		} else {
			menuListResJsonObj.menuList.insert(nextSameDepthRowIndex, childMenu);
		}	
		
		menuListResJsonObj.cnt++;
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function addRootMenu() {
		var g = document.addRootMenuFrm;
		g.reset();
		$('#rootMenuModal').modal();
	}
	
	function addRootMenuOkCallBack(rootMenuAddRes) {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;
		var parentMenu = menuListResJsonObj.menuList[rowIndex];
		var g = document.addRootMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 루트 메뉴[메뉴번호:"+rootMenuAddRes.menuNo+", 메뉴명:"+g.menuName.value+"]를 추가했습니다";
				
		var rootMenu = {};
		rootMenu.menuNo = rootMenuAddRes.menuNo;
		rootMenu.parentNo = 0;
		rootMenu.depth = 0;
		rootMenu.orderSeq = rootMenuAddRes.orderSeq;
		rootMenu.menuName = g.menuName.value;
		rootMenu.linkURL = g.linkURL.value;

		menuListResJsonObj.menuList.push(rootMenu);
		menuListResJsonObj.cnt++;
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function adminLoginErrorCallBack(errorMessage) {
		var resultMessageView = document.getElementById("resultMessageView");
		
		// resultMessageView.setAttribute("class", "alert alert-warning fade in");
		resultMessageView.setAttribute("class", "alert alert-warning");
		resultMessageView.innerHTML = "<strong>Warning!</strong> " + errorMessage;
	}
	
	function reload() {
		document.location.href = "/servlet/MenuManagement"; 
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
		buildListView();
	}
	
	window.onload = init;
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
<form name="moveMenuUpFrm" method="post" action="/servlet/MenuMoveUp" target="hiddenFrame">
	<input type="hidden" name="menuNo">
</form>
<form name="moveMenuDownFrm" method="post" action="/servlet/MenuMoveDown" target="hiddenFrame">
	<input type="hidden" name="menuNo">
</form>
<form name="modifyMenuFrm" method="post" action="/servlet/MenuModification" target="hiddenFrame">
	<input type="hidden" name="menuNo">
	<input type="hidden" name="menuName">
	<input type="hidden" name="linkURL">
</form>
<form name="deleteMenuFrm" method="post" action="/servlet/MenuDeletion" target="hiddenFrame">
	<input type="hidden" name="menuNo">
</form>
	<div class="container-fluid">
		<h3>메뉴 관리</h3>		
		<div class="btn-group">
			<button type="button" class="btn btn-primary btn-sm" onClick="addRootMenu()">Add Root</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="reload();">Reload</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
		</div>
					 
		<div id="resultMessageView"></div>
		<div class="row">
			<div class="col-sm-1">메뉴번호</div>
			<div class="col-sm-1">부모번호</div>
			<div class="col-sm-1">깊이</div>
			<div class="col-sm-1">순서</div>
			<div class="col-sm-2">메뉴이름</div>			
			<div class="col-sm-3">URL</div>
			<div class="col-sm-3">기능</div>
		</div>
		<form name="frm" onSubmit="return false">
			<div id="listView">
			</div>	
		</form>
		<!-- Child Menu Modal -->
		<div class="modal fade" id="childMenuModal" role="dialog">
			<div class="modal-dialog">			
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">자식 메뉴 추가 화면</h4>
					</div>
					<div class="modal-body">
						<form name="addChildMenuFrm" method="post" class="form-inline" onSubmit="$('#childMenuModal').modal('toggle'); return true;" action="/servlet/ChildMenuAddition" target="hiddenFrame">
							<div class="form-group">
							    <label class="sr-only" for="parentNoForChildMenu">부모 메뉴번호</label>
							    <input type="hidden" id="parentNoForChildMenu" name="parentNo" />
							 </div>
							 <div class="form-group">
							    <label for="menuNameForChildMenu">자식 메뉴명</label>
							    <input type="text" id="menuNameForChildMenu" name="menuName" />
							 </div>
							 <div class="form-group">
							    <label for="linkURLForChildMenu">URL</label>
							    <input type="text" id="linkURLForChildMenu" name="linkURL" />
							 </div>
							<button type="submit" class="btn btn-default">추가</button>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>			
			</div>
		</div>
		
		<!-- Root Menu Modal -->
		<div class="modal fade" id="rootMenuModal" role="dialog">
			<div class="modal-dialog">			
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">루트 메뉴 추가 화면</h4>
					</div>
					<div class="modal-body">
						<form name="addRootMenuFrm" method="post" class="form-inline" onSubmit="$('#rootMenuModal').modal('toggle'); return true;" action="/servlet/RootMenuAddition" target="hiddenFrame">							
							 <div class="form-group">
							    <label for="menuNameForRootMenu">메뉴명</label>
							    <input type="text" id="menuNameForRootMenu" name="menuName" />
							 </div>
							 <div class="form-group">
							    <label for="linkURLForRootMenu">URL</label>
							    <input type="text" id="linkURLForRootMenu" name="linkURL" />
							 </div>
							<button type="submit" class="btn btn-default">추가</button>
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
</body>
</html>
