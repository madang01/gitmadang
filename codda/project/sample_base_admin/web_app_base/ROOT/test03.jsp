<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="com.google.gson.JsonObject"%><%
%><%@ page import="com.google.gson.JsonParser"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();

	request.setCharacterEncoding("UTF-8");
	String comment = request.getParameter("comment");	
	
	System.out.println("2.comment::"+comment);	
	
	/*
	JsonParser jsonParser = new JsonParser();	
	
	// comment = org.apache.commons.text.StringEscapeUtils.unescapeEcmaScript(comment);
	JsonObject jsonObject = (JsonObject) jsonParser.parse(comment);	
	System.out.println("3.jsonObject::"+jsonObject.toString());
	
	comment = jsonObject.get("comment").getAsString();
	
	System.out.println("4.comment::"+comment);	
	*/
	
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

<script type="text/javascript">
<!--
	
//-->
</script>
</head>
<body>
<%= adminSiteMenuManger.getSiteNavbarString(isAdminLogin(request))%>	
	<div class="container-fluid">
		<h3>파라미터 보존 테스트::STEP3::loginSuccess.jsp 역활</h3>
		<form name="frm" action="test03.jsp" onsubmit="return false;">
			<div class="form-group">
				<label for="comment">Comment:</label>
				<textarea class="form-control" rows="5" id="comment" name="comment"><%= HtmlStringUtil.toHtml4String(comment) %></textarea>
			</div>
		</form>
	</div>
</body>
</html>
