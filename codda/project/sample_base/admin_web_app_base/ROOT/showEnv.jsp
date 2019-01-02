<%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.common.etc.CommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
%><%
	String projectName = System.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
	String installedPath = System.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
	
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Codda JAVA System Properties</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

</head>
<body>
<%=adminSiteMenuManger.getWebsiteMenuString(isAdminLoginedIn(request))%>
	
	<div class="container-fluid">
		<h3>Codda JAVA System Properties</h3>
		<table class="table table-striped">
			<thead>
				<tr>
					<th>Key</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><%= CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME %></td>
					<td><%= projectName %></td>
				</tr>
				<tr>
					<td><%= CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH %></td>
					<td><%= installedPath %></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>
