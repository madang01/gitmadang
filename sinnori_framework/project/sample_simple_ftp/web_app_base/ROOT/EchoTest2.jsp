<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="kr.pe.sinnori.client.ClientProject" %>
<%@page import="kr.pe.sinnori.client.ClientProjectManager" %>
<%@page import="kr.pe.sinnori.common.util.HexUtil" %>
<%@page import="kr.pe.sinnori.common.util.ConfigManager" %>
<%@page import="kr.pe.sinnori.common.util.LogManager" %>
<%@page import="kr.pe.sinnori.common.message.InputMessage" %>
<%@page import="java.util.Random" %>
<%
	byte[] buffer = {0x10, 0x20};
	String hexStr = HexUtil.byteArrayAllToHex(buffer);
	
	java.util.Random random = new java.util.Random();
	
	String projectName = "sample_simple_chat";
	ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
	
	InputMessage echoInObj = null;
	echoInObj = clientProject.createInputMessage("Echo");
	
	ConfigManager conf = ConfigManager.getInstance();
	LogManager log = LogManager.getLogger();
	
	log.info("hello");
	
	echoInObj.setAttribute("mRandomInt", random.nextInt());
	echoInObj.setAttribute("mStartTime", new java.util.Date().getTime());
%>
Ehco test page::<%=hexStr%><br/>
echoInputMessage::<%=echoInObj.toString()%><br/>
