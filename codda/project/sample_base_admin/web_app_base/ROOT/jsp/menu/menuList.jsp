<%@page import="kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes"%><%
%><%@page import="java.util.ArrayList"%><%
%><%@page import="java.util.List"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
%><%@page import="kr.pe.codda.impl.message.MenuListRes.MenuListRes" %><%
%><%@page import="kr.pe.codda.impl.message.MessageResultRes.MessageResultRes"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%	
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
	MenuListRes menuListRes = (MenuListRes)request.getAttribute("menuListRes"); 
	
	if (null == menuListRes) {
		menuListRes = new MenuListRes();
		
		List<MenuListRes.Menu> menuList = new ArrayList<MenuListRes.Menu>();
		{
			MenuListRes.Menu menu = new MenuListRes.Menu();
			menu.setMenuNo(1);
			menu.setParentNo(0);
			menu.setOrderSeq((short)0);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴01");
			menu.setLinkURL("/test01");
			
			menuList.add(menu);
		}
		
		{
			MenuListRes.Menu menu = new MenuListRes.Menu();
			menu.setMenuNo(2);
			menu.setParentNo(0);
			menu.setOrderSeq((short)1);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴02");
			menu.setLinkURL("/test02");
			
			menuList.add(menu);
		}
		
		{
			MenuListRes.Menu menu = new MenuListRes.Menu();
			menu.setMenuNo(3);
			menu.setParentNo(0);
			menu.setOrderSeq((short)2);
			menu.setDepth((short)0);
			menu.setMenuName("테스트메뉴03");
			menu.setLinkURL("/test03");
			
			menuList.add(menu);
		}
		
		menuListRes.setCnt(menuList.size());
		menuListRes.setMenuList(menuList);
	}
	
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
<script type='text/javascript'>
	var menuListResJson = <%= new Gson().toJson(menuListRes) %>;
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
	
	function makeFuncColButtonList(rowIndex) {
		var buttonList = [];
		
		<% /** 상단 이동이 가능하다면 상단 이동 버튼 추가 */ %>
		if (0 != menuListResJson.menuList[rowIndex].orderSeq) {			
			buttonList.push(makeGlyphIconButton("btn btn-primary btn-sm", "moveMenuUp("+menuListResJson.menuList[rowIndex].menuNo+","+rowIndex+");", "glyphicon-arrow-up", "Up"));
		}
		
		try {
			checkWhetherNextSameDepthMenuExist(rowIndex, menuListResJson.menuList[rowIndex].depth);
			<% /** 하단 이동이 가능하다면 상단 이동 버튼 추가 */ %>
			buttonList.push(makeGlyphIconButton("btn btn-primary btn-sm", "moveMenuDown("+menuListResJson.menuList[rowIndex].menuNo+","+rowIndex+");", "glyphicon-arrow-down", "Down"));
		} catch(err) {		
			<% // checkWhetherNextSameDepthMenuExist 함수는 하단으로 이동할 메뉴를 못찼았다면 "not found exception" 를 던지므로 이 경우는 무시하고 그외 경우 에러 추적을 위해서 로그 출력함 %> 	
			if (err != "not found exception") {
				console.log(err);
			}
		}
		
		buttonList.push(makeTextButton("btn btn-primary btn-sm", "modifyMenu("+menuListResJson.menuList[rowIndex].menuNo+"," + rowIndex + ");", "Modify"));
		buttonList.push(makeTextButton("btn btn-primary btn-sm", "addChildMenu("+menuListResJson.menuList[rowIndex].menuNo+"," + rowIndex + ");", "Add"));
		
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
			
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJson.menuList[rowIndex].menuNo));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJson.menuList[rowIndex].parentNo));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJson.menuList[rowIndex].depth));
		rowDiv.appendChild(makeTextTypeColDivOfList("col-sm-1", menuListResJson.menuList[rowIndex].orderSeq));
				
		rowDiv.appendChild(makeInputTextTypeColDivOfList("col-sm-2", "menuName" + rowIndex, 80, menuListResJson.menuList[rowIndex].menuName));			
		rowDiv.appendChild(makeInputTextTypeColDivOfList("col-sm-4", "linkURL" + rowIndex, 80, menuListResJson.menuList[rowIndex].linkURL));	
		
		rowDiv.appendChild(makeFuncColDivOfList("col-sm-2 btn-group", makeFuncColButtonList(rowIndex)));
		
		formGroupDiv.appendChild(rowDiv);
		
		return formGroupDiv;
	}
	
	function checkWhetherNextSameDepthMenuExist(sourceIndex, wantedDepth) {
		if (sourceIndex < 0) {
			throw "the parameter sourceIndex is less than zero";
		}
		
		if (sourceIndex >= menuListResJson.cnt) {
			throw "the parameter sourceIndex is greater than or equal to menuList.size["+menuListResJson.cnt+"]";
		}
		
		for (var i=sourceIndex+1; i < menuListResJson.cnt; i++) {
			if (menuListResJson.menuList[i].depth == wantedDepth) {
				return i;
			}
			
		}
		throw "not found exception";
	}
	
	function buildListView() {
		var listView = document.getElementById("listView");
		
		while (listView.firstChild) {
			listView.removeChild(listView.firstChild);
		}		
		
		for (var i=0; i < menuListResJson.cnt; i++) {
			var formGroupDiv = makeRowDivOfList(i);
			
			listView.appendChild(formGroupDiv);
		}		
	}
	
 
	function modifyMenu(menuNo, rowIndex) {
		var g = document.modifyMenuFrm;
		g.menuNo.value = menuNo;
		g.menuName.value = document.getElementById("menuName"+rowIndex).value;
		g.linkURL.value = document.getElementById("linkURL"+rowIndex).value;
		
		__rowIndex = rowIndex;
		
		alert("수정할 메뉴번호="+g.menuNo.value);<%
	
		MessageResultRes modifyMenuMessageResultRes = new MessageResultRes();
		
		modifyMenuMessageResultRes.setIsSuccess(true);
		modifyMenuMessageResultRes.setResultMessage("지정한  메뉴 수정이 성공하였습니다");
%>
		var modifyMenuMessageResultRes = <%= new Gson().toJson(modifyMenuMessageResultRes) %>;

		modifyMenuCallBack(modifyMenuMessageResultRes);
	}
	
	function modifyMenuOkCallBack() {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;			
		var g = document.modifyMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 지정한  메뉴["+g.menuNo.value+"] 수정이 성공하였습니다";			
		
		menuListResJson.menuList[rowIndex].menuName = g.menuName.value;
		menuListResJson.menuList[rowIndex].linkURL = g.linkURL.value;		
		
		var newRowDivOfList = makeRowDivOfList(rowIndex);
		
		var listView = document.getElementById("listView");
		
		listView.childNodes[rowIndex].display  = 'none';
		
		listView.childNodes[rowIndex] = newRowDivOfList;
				
		listView.childNodes[rowIndex].display  = 'show';
	}
	
	function moveMenuUp(menuNo, rowIndex) {
		var g = document.moveMenuUpFrm;
		g.menuNo.value = menuNo;
		__rowIndex = rowIndex;
		
		moveMenuUpCallBack();
	}
	
	function moveMenuUpOkCallBack() {
		var g = document.moveMenuUpFrm;
		var resultMessageView = document.getElementById("resultMessageView");
		
		var rowIndex = __rowIndex;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> " + "지정한 메뉴["+g.menuNo.value+"]의 상단 이동이 성공하였습니다";
		
		var fromMenu = menuListResJson.menuList[rowIndex];
		
		var fromMenuList = [];
		fromMenuList.push(fromMenu);		
		for (var i=rowIndex+1; i < menuListResJson.cnt; i++) {				
			if (fromMenu.depth >= menuListResJson.menuList[i].depth) {
				break;
			}
			
			fromMenuList.push(menuListResJson.menuList[i]);
		}		
		
		var toMenu;
		var rowIndexOfToMenu;
		for (var i=rowIndex - 1; i >= 0; i--) {
			if (fromMenu.depth == menuListResJson.menuList[i].depth) {
				rowIndexOfToMenu = i;
				toMenu = menuListResJson.menuList[i];
				break;
			}
		}
		
		var toMenuList = [];
		toMenuList.push(toMenu);
		for (var i=rowIndexOfToMenu + 1; i < menuListResJson.cnt; i++) {
			if (fromMenu.depth >= menuListResJson.menuList[i].depth) {
				break;
			}
			toMenuList.push(menuListResJson.menuList[i]);
		}
		
		var oldOrderSeq = fromMenu.orderSeq; 
		fromMenu.orderSeq = toMenu.orderSeq;
		toMenu.orderSeq = oldOrderSeq;		
		
		for (var i=0; i < fromMenuList.length; i++) {
			menuListResJson.menuList[rowIndexOfToMenu + i] = fromMenuList[i];
		}
		
		for (var i=0; i < toMenuList.length; i++) {
			menuListResJson.menuList[rowIndexOfToMenu+fromMenuList.length + i] = toMenuList[i];
		}
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function moveMenuDown(menuNo, rowIndex) {
		var g = document.moveMenuDownFrm;
		g.menuNo.value = menuNo;
		__rowIndex = rowIndex;
						
		moveMenuDownCallBack();
	}
	
	function moveMenuDownOkCallBack() {
		var g = document.moveMenuDownFrm;
		var resultMessageView = document.getElementById("resultMessageView");
		
		var rowIndex = __rowIndex;			
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> " + "지정한 메뉴["+g.menuNo.value+"] 하단 이동이 성공하였습니다";
		
		var fromMenu = menuListResJson.menuList[rowIndex];
		
		var fromMenuList = [];
		fromMenuList.push(fromMenu);		
		for (var i=rowIndex+1; i < menuListResJson.cnt; i++) {				
			if (fromMenu.depth >= menuListResJson.menuList[i].depth) {
				break;
			}
			
			fromMenuList.push(menuListResJson.menuList[i]);
		}		
		
		var toMenu = menuListResJson.menuList[rowIndex + fromMenuList.length];
		var toMenuList = [];
		toMenuList.push(toMenu);
		for (var i=rowIndex + fromMenuList.length + 1; i < menuListResJson.cnt; i++) {
			if (fromMenu.depth >= menuListResJson.menuList[i].depth) {
				break;
			}
			toMenuList.push(menuListResJson.menuList[i]);
		}
		
		var oldOrderSeq = fromMenu.orderSeq; 
		fromMenu.orderSeq = toMenu.orderSeq;
		toMenu.orderSeq = oldOrderSeq;
		
		
		for (var i=0; i < toMenuList.length; i++) {
			menuListResJson.menuList[rowIndex+i] = toMenuList[i];
		}
		
		for (var i=0; i < fromMenuList.length; i++) {
			menuListResJson.menuList[rowIndex+toMenuList.length+i] = fromMenuList[i];
		}		
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function addChildMenu(parentNo, rowIndex) {
		var g = document.addChildMenuFrm;
		g.parentNo.value = parentNo;
		__rowIndex = rowIndex;
		$("#childMenuModal").modal();<%
		
		ChildMenuAddRes childMenuAddRes = new ChildMenuAddRes();
		childMenuAddRes.setMenuNo(4);
		childMenuAddRes.setOrderSeq((short)1);
%>
		var childMenuAddRes = <%= new Gson().toJson(childMenuAddRes) %>;
		
		addChildMenuOkCallBack(childMenuAddRes);
		
	}
	
	function addChildMenuOkCallBack(childMenuAddRes) {
		var resultMessageView = document.getElementById("resultMessageView");
		var rowIndex = __rowIndex;
		var parentMenu = menuListResJson.menuList[rowIndex];
		var g = document.addChildMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 부모 메뉴["+parentMenu.menuNo+"]의 자식 메뉴["+childMenuAddRes.menuNo+"] 생성이 성공하였습니다";
				
		var childMenu = {};
		childMenu.menuNo = childMenuAddRes.menuNo;
		childMenu.parentNo = parentMenu.menuNo;
		childMenu.depth = parentMenu.depth + 1;
		childMenu.orderSeq = childMenuAddRes.orderSeq;
		childMenu.menuName = g.menuName.value;
		childMenu.linkURL = g.linkURL.value;
		
		menuListResJson.insert(rowIndex+1, childMenu);
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function addRootMenuOkCallBack(rootMenuAddRes) {
		var resultMessageView = document.getElementById("resultMessageView");
		var parentMenu = menuListResJson.menuList[rowIndex];
		var g = document.addRootMenuFrm;
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> 루트 메뉴["+rootMenuAddRes.menuNo+"] 생성이 성공하였습니다";
				
		var rootMenu = {};
		rootMenu.menuNo = rootMenuAddRes.menuNo;
		rootMenu.parentNo = 0;
		rootMenu.depth = 0;
		rootMenu.orderSeq = rootMenuAddRes.orderSeq;
		rootMenu.menuName = g.menuName.value;
		rootMenu.linkURL = g.linkURL.value;
		
		menuListResJson.push(rootMenu);
		
		var listView = document.getElementById("listView");		
		
		listView.display  = 'none';
				
		buildListView();
				
		listView.display  = 'show';
	}
	
	function errorMessageCallBack(errorMessage) {
		var resultMessageView = document.getElementById("resultMessageView");
		
		resultMessageView.setAttribute("class", "alert alert-warning fade in");
		resultMessageView.innerHTML = "<strong>Warning!</strong> " + errorMessage;
	}
	
	function reload() {
		documnet.location.href = "/servlet/MenuList"; 
	}
	
	function init() {
		buildListView();
	}
	
	window.onload = init;
</script>
</head>
<body>
<%= adminSiteMenuManger.getSiteNavbarString(isAdminLogin(request)) %>
<form name="moveMenuUpFrm" action="/servlet/MenuUpMove" target="hiddenFrame">
	<input type="hidden" name="menuNo">
</form>
<form name="moveMenuDownFrm" action="/servlet/MenuDownMove" target="hiddenFrame">
	<input type="hidden" name="menuNo">
</form>
<form name="modifyMenuFrm" action="/servlet/MenuModify" target="hiddenFrame">
	<input type="hidden" name="menuNo">
	<input type="hidden" name="menuName">
	<input type="hidden" name="linkURL">
</form>
	<div class="container-fluid">
		<h3>메뉴 관리</h3>		
		<div class="btn-group">
			<button type="button" class="btn btn-primary btn-sm" data-toggle="modal" data-target="#rootMenuModal">Add</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="reload();">Reload</button>
		</div>
					 
		<div id="resultMessageView"></div>
		<div class="row">
			<div class="col-sm-1">메뉴번호</div>
			<div class="col-sm-1">부모번호</div>
			<div class="col-sm-1">깊이</div>
			<div class="col-sm-1">순서</div>
			<div class="col-sm-2">메뉴이름</div>			
			<div class="col-sm-4">URL</div>
			<div class="col-sm-2">기능</div>
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
						<form name="addChildMenuFrm" class="form-inline" onSubmit="return false" action="/servlet/ChildMenuAdd" target="hiddenFrame">
							<div class="form-group">
							    <label class="sr-only" for="parentNoForChildMenu">부모 메뉴번호</label>
							    <input type="hidden" id="parentNoForChildMenu" name="parentNo">
							 </div>
							 <div class="form-group">
							    <label for="menuNameForChildMenu">자식 메뉴명</label>
							    <input type="text" id="menuNameForChildMenu" name="menuName">
							 </div>
							 <div class="form-group">
							    <label for="linkURLForChildMenu">URL</label>
							    <input type="text" id="linkURLForChildMenu" name="linkURL">
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
						<form name="addRootMenuFrm" class="form-inline" onSubmit="return false" action="/servlet/RootMenuAdd" target="hiddenFrame">							
							 <div class="form-group">
							    <label for="menuNameForRootMenu">메뉴명</label>
							    <input type="text" id="menuNameForRootMenu" name="menuName">
							 </div>
							 <div class="form-group">
							    <label for="linkURLForRootMenu">URL</label>
							    <input type="text" id="linkURLForRootMenu" name="linkURL">
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
		<iframe id="hiddenFrame" height="0" width="0" name="hiddenFrame"></iframe>
	</div>
</body>
</html>
