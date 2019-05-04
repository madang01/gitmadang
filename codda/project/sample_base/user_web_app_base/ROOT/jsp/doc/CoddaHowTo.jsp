<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%>
<%@page import="java.io.File"%>
<%@page import="kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter"%>
<%@page import="kr.pe.codda.common.config.CoddaConfiguration"%>
<%@page import="kr.pe.codda.common.config.CoddaConfigurationManager"%>
<%@page import="kr.pe.codda.common.util.CommonStaticUtil"%>
<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	request.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/jsp/doc/CoddaHowTo.jsp");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type='text/javascript'>
	function init() {		
	}

	window.onload = init;
</script>
<body>
	<div class=header>
		<div class="container">
		<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>Codda HowTo</h4></div>
				<div class="panel-body">
					<article style="white-space:pre-line;"><%
					
	CoddaConfiguration  coddaConfiguration  = CoddaConfigurationManager.getInstance().getRunningProjectConfiguration();
	String installedPath = coddaConfiguration.getInstalledPathString();
	String mainProjectName = coddaConfiguration.getMainProjectName();
	String wwwWebRootPathString = WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath, mainProjectName);
	String coddaHowtoTextFilePathString = new StringBuilder()
			.append(wwwWebRootPathString)
			.append(File.separator)
			.append("jsp")
			.append(File.separator)
			.append("doc")
			.append(File.separator)
			.append("CoddaHowto.txt")
			.toString();
	
	File coddaHowtoTextFile = new File(coddaHowtoTextFilePathString);
	byte[] coddaHowtoTextFileContents = CommonStaticUtil.readFileToByteArray(coddaHowtoTextFile, 1024*1024*10L);
	String coddaHowtoText = new String(coddaHowtoTextFileContents, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
	
%><%= toEscapeHtml4(coddaHowtoText) %>
					</article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>