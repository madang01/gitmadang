<%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="com.google.gson.JsonObject"%><%
%><%@ page import="org.apache.commons.text.StringEscapeUtils" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
	
	request.setCharacterEncoding("UTF-8");
	String comment = request.getParameter("comment");
	
	System.out.println("1.comment::"+comment);
	
	/*
	JsonObject parmeterJsonObject = new JsonObject();
	parmeterJsonObject.addProperty("comment",  comment);
	
	
	System.out.println("1.parmeterJsonObject::"+parmeterJsonObject.toString());
	*/
	
	
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

<script type="text/javascript">
<!--
	
//-->
</script>
</head>
<body>
<%=adminSiteMenuManger.getMenuNavbarString(isAdminLoginedIn(request))%>	
	<div class="container-fluid">
		<h3>파라미터 보존 테스트::STEP2::login.jsp 역활</h3>
		<form name="frm" action="test03.jsp" method="post">
			<div class="form-group">
				<label for="comment">Comment:</label>
				<textarea class="form-control" rows="5" id="comment" name="comment"><%= StringEscapeUtils.escapeHtml4(comment) %></textarea>
			</div>
			<div class="form-group">
				<label for="comment">Comment:</label>
				<textarea class="form-control" rows="5" id="comment2" name="comment2" style="display:none;"><%= HtmlStringUtil.toHtml4String(comment) %></textarea>
			</div>
			<button type="submit" class="btn btn-primary">Submit</button>
		</form>
	</div>
</body>
</html>
