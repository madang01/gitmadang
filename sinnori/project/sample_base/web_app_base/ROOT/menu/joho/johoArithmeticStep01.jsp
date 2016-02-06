<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width,initial-scale=1.0,user-scalable=no" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
    <!-- 공통요소 -->
	<link rel="stylesheet" type="text/css" href="/axisj/axicon/axicon.min.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXJ.css" />
    <script type="text/javascript" src="/axisj/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXJ.js"></script>

    <!-- 추가하는 UI 요소 -->
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXInput.css" />
	 <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXButton.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXSelect.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXGrid.css" />
    <script type="text/javascript" src="/axisj/lib/AXInput.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXSelect.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXGrid.js"></script>
	<script type="text/javascript" src="/axisj/lib/AXMobileMenu.js"></script>
	

	<script type="text/javascript">
		function goForm(f) {
			var parmCreatingTableSql = f.parmCreatingTableSql.value;
			if (parmCreatingTableSql == "") {
				alert("body 에 DB 테이블 생성 쿼리문을 넣어주세요.");
				f.parmCreatingTableSql.focus();
				return false;
			}


			return true;
		}

	var pageID = "AXMobileMenu";
	var myMobileMenu = new AXMobileMenu();
	var fnObj = {
		pageStart: function(){
			//AXUtil.readyMobileConsole();

			myMobileMenu.setConfig({
				reserveKeys:{
					primaryKey:"menuID",
					labelKey:"label",
					urlKey:"url",
					targetKey:"target",
					addClassKey:"ac",
					subMenuKey:"cn"
				},
				menu:[
					{menuID:"1", label:"menu 1", ac:"Dashboard", url:"http://www.axisj.com"},
					{menuID:"4", label:"menu 4", ac:"Cashiering", url:"http://www.axisj.com"},
					{menuID:"5", label:"menu 5", ac:"Housekeeping", url:"http://www.axisj.com"},
					{menuID:"6", label:"menu 6", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"2", label:"menu 2", ac:"Reservation", cn:[
						{menuID:"2-1", label:"menu 2-1", url:"http://www.axisj.com"},
						{menuID:"2-2", label:"menu 2-2", cn:[
							{menuID:"2-2-1", label:"menu 2-2-1", url:"http://www.axisj.com"},
							{menuID:"2-2-2", label:"menu 2-2-2", url:"http://www.axisj.com"},
							{menuID:"2-2-3", label:"menu 2-2-3", url:"http://www.axisj.com"}
						]},
						{menuID:"2-3", label:"menu 2-3", url:"http://www.axisj.com"},
						{menuID:"2-4", label:"menu 2-4", url:"http://www.axisj.com"},
						{menuID:"2-5", label:"menu 2-5", url:"http://www.axisj.com"},
						{menuID:"2-6", label:"menu 2-6", url:"http://www.axisj.com"},
						{menuID:"2-7", label:"menu 2-7", url:"http://www.axisj.com"},
						{menuID:"2-8", label:"menu 2-8", url:"http://www.axisj.com"},
						{menuID:"2-9", label:"menu 2-9", url:"http://www.axisj.com"},
						{menuID:"2-10", label:"menu 2-10", url:"http://www.axisj.com"},
						{menuID:"2-11", label:"menu 2-11", url:"http://www.axisj.com"},
						{menuID:"2-12", label:"menu 2-12", url:"http://www.axisj.com"},
						{menuID:"2-13", label:"menu 2-13", url:"http://www.axisj.com"}
					]},
					{menuID:"7", label:"menu 7", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"8", label:"menu 8", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"9", label:"menu 9", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"10", label:"menu 10", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"11", label:"menu 11", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"12", label:"menu 12", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"13", label:"menu 13", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"14", label:"menu 14", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"15", label:"menu 15", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"16", label:"menu 16", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"17", label:"menu 17", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"18", label:"menu 18", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"19", label:"menu 19", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"20", label:"menu 20", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"21", label:"menu 21", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"22", label:"menu 22", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"23", label:"menu 23", ac:"Management", url:"http://www.axisj.com"},
					{menuID:"3", label:"menu 3", ac:"Guest", cn:[
						{menuID:"3-1", label:"menu 3-1", url:"http://www.axisj.com"},
						{menuID:"3-2", label:"menu 3-2", cn:[
							{menuID:"3-2-1", label:"menu 3-2-1", url:"http://www.axisj.com"},
							{menuID:"3-2-2", label:"menu 3-2-2", url:"http://www.axisj.com"},
							{menuID:"3-2-3", label:"menu 3-2-3", url:"http://www.axisj.com"}
						]},
						{menuID:"3-3", label:"menu 3-3", url:"http://www.axisj.com"},
						{menuID:"3-4", label:"menu 3-4", url:"http://www.axisj.com"},
						{menuID:"3-5", label:"menu 3-5", url:"http://www.axisj.com"}
					]},
					{menuID:"24", label:"menu 24", ac:"Configuration", url:"http://www.axisj.com"}
				],
				onclick: function(){ // 메뉴 클릭 이벤트
					myMobileMenu.close();
					//location.href = this.url;
				}
			});

			myMobileMenu.setHighLight("2-2");
/*
			myMobileMenu.setConfig({
				reserveKeys:{
					primaryKey:"parent_srl",
					labelKey:"label",
					urlKey:"link",
					targetKey:"target",
					addClassKey:"ac",
					subMenuKey:"cn"
				},
				onclick: function(){ // 메뉴 클릭 이벤트
					myMobileMenu.close();
					//location.href = this.url;
				}
			});
*/			
var menuStr = '[{"label":"test", "link":"/index.php?mid=page_XhGM56", "target":"_self", "url":"page_XhGM56", "selected":1, "expand":"N", "isShow":true, "parent_srl":"0", "k":"66", "cn":null}, {"label":"We are...", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"104", "cn":[{"label":"Jowrney & Stacey", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"105", "cn":[{"label":"aaa", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"105", "k":"140", "cn":[{"label":"ddd", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"140", "k":"143", "cn":[{"label":"fff", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"143", "k":"145", "cn":null}], "addClass":"hasSubMenu"}, {"label":"eee", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"140", "k":"144", "cn":null}], "addClass":"hasSubMenu"}, {"label":"bbb", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"105", "k":"141", "cn":null}, {"label":"ccc", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"105", "k":"142", "cn":null}], "addClass":"hasSubMenu"}, {"label":"Bike", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"106", "cn":null}, {"label":"Gear & Stuff", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"107", "cn":null}, {"label":"Media outlet", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"108", "cn":null}, {"label":"Sponsor", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"109", "cn":null}, {"label":"World adventure proposal", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"110", "cn":null}, {"label":"iBooks", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"111", "cn":null}, {"label":"Rewards", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"112", "cn":null}, {"label":"Rancho", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"104", "k":"113", "cn":null}], "addClass":"hasSubMenu"}, {"label":"Route", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"114", "cn":[{"label":"Where we go", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"114", "k":"115", "cn":null}, {"label":"Trace of flybasket", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"114", "k":"116", "cn":null}, {"label":"Cost", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"114", "k":"117", "cn":null}], "addClass":"hasSubMenu"}, {"label":"Travels", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"118", "cn":[{"label":"World Adventure", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"119", "cn":null}, {"label":"2013 Dokdo, Aroound the Ulleun island", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"120", "cn":null}, {"label":"2012 Cross country, along the river", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"121", "cn":null}, {"label":"2011 Around the Jeju island", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"122", "cn":null}, {"label":"2010 Jumujin, Go to the East sea", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"123", "cn":null}, {"label":"2009 We rode the japan honeymoon", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"124", "cn":null}, {"label":"2008 Haenam, the end of the Korea", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"118", "k":"125", "cn":null}], "addClass":"hasSubMenu"}, {"label":"Blog", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"126", "cn":null}, {"label":"Project", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"127", "cn":[{"label":"Experience farm in the world", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"128", "cn":null}, {"label":"Click the shutter for the world", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"129", "cn":null}, {"label":"10 thousands hours playing the violins", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"130", "cn":null}, {"label":"Go to 30,000 km by bike", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"131", "cn":null}, {"label":"On around the earth", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"132", "cn":null}, {"label":"Create UI set by countries", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"127", "k":"133", "cn":null}], "addClass":"hasSubMenu"}, {"label":"Friends", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"134", "cn":null}, {"label":"Guestbook", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"135", "cn":null}, {"label":"Donation", "link":"#", "target":"_self", "url":"#", "selected":0, "expand":"N", "isShow":true, "parent_srl":"0", "k":"136", "cn":null}] ';
			
			//myMobileMenu.setTree( menuStr.object() );
/*
			var menus = [
					{menuID:"1", label:"menu 1", ac:"Dashboard", url:"http://www.axisj.com"},
					{menuID:"4", label:"menu 4", ac:"Cashiering", url:"http://www.axisj.com"},
					{menuID:"5", label:"menu 5", ac:"Housekeeping", url:"http://www.axisj.com"}
			];			
			myMobileMenu.setTree( menus );
*/
			//myMobileMenu.setHighLightMenu("0");

		}
	};
	$(document.body).ready(function(){
		fnObj.pageStart.delay(0.1);
	});
	</script>
	<!-- js block -->
</head>
<body>
<div class="AXGridTarget">
    <div class="title"><h1>조호-사칙연산(Arithmetic)</h1></div>
    <button class="AXButton" onclick="myMobileMenu.open();">Open the mobile menu</button>
</div>


</body>
</html>
