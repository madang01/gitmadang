package kr.pe.codda.common.buildsystem;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class ServerAntBuildXMLFileContenetsBuilder {
	
	public static void addHeader(StringBuilder contentsStringBuilder) {		
		contentsStringBuilder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
	}
	
	public static void addNewLine(StringBuilder contentsStringBuilder) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
	}
		
	
	public static void addROOTStartTag(StringBuilder contentsStringBuilder, 
			String projectName, String defaultTargetName, String baseDirectory) {
		contentsStringBuilder.append("<project name=\"");
		contentsStringBuilder.append(projectName);
		contentsStringBuilder.append("\" default=\"");
		contentsStringBuilder.append(defaultTargetName);
		contentsStringBuilder.append("\" basedir=\"");
		contentsStringBuilder.append(baseDirectory);
		contentsStringBuilder.append("\">");
	}
	
	public static void addROOTEndTag(StringBuilder contentsStringBuilder) {
		addEndTag(contentsStringBuilder, "project");
	}
	
	public static void addEndTag(StringBuilder contentsStringBuilder, String tagName) {
		contentsStringBuilder.append("</");
		contentsStringBuilder.append(tagName);
		contentsStringBuilder.append(">");
	}
	
	public static void addAttribute(StringBuilder contentsStringBuilder, String attributeKey, String attributeValue) {
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(attributeKey);
		contentsStringBuilder.append("=\"");
		contentsStringBuilder.append(attributeValue);
		contentsStringBuilder.append("\"");
	}
	
	public static void addPropertyTag(StringBuilder contentsStringBuilder,
			String propertyName, String singleAttributeKey, String singleAttributeValue) {
		contentsStringBuilder.append("<property name=\"");
		contentsStringBuilder.append(propertyName);
		contentsStringBuilder.append("\"");
		addAttribute(contentsStringBuilder, singleAttributeKey, singleAttributeValue);
		contentsStringBuilder.append(" />");
	}	
	
	public static void addWindowsCondition(StringBuilder contentsStringBuilder, final int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<condition property=\"is.windows.yes\">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<os family=\"windows\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</condition>");
	}
	
	public static void addLinuxCondition(StringBuilder contentsStringBuilder, final int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<condition property=\"is.unix.yes\">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<os family=\"unix\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</condition>");
	}
	
	public static void addInitTarget(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "init.var");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<!-- Create the time stamp -->");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<tstamp />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<echo message=\"java.complile.option.debug=${java.complile.option.debug}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<echo message=\"is.windows.yes=${is.windows.yes}, is.unix.yes=${is.unix.yes}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.mainlib}/ex\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.mainlib}/in\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static String build(String mainProjectName) {
		final String builderProjectName = new StringBuilder().append(mainProjectName)
				.append("_server").toString();
		final String defaultTargetName = "compile.appinf";
		final String baseDirectory = ".";
		final int depth=0;		 
		
		StringBuilder contentsStringBuilder = new StringBuilder();
		addHeader(contentsStringBuilder);
		
		addNewLine(contentsStringBuilder);
		addROOTStartTag(contentsStringBuilder, builderProjectName, defaultTargetName, baseDirectory);
		
		addProprtiesPart(depth, contentsStringBuilder);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addWindowsCondition(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addLinuxCondition(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addInitTarget(contentsStringBuilder, depth);
		
		
		addNewLine(contentsStringBuilder);
		addROOTEndTag(contentsStringBuilder);
		return  contentsStringBuilder.toString();
	}

	private static void addProprtiesPart(final int depth, StringBuilder contentsStringBuilder) {
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.src", "location", "src/main/java");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.build", "location", "build");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.appinf", "location", "APP-INF");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.dist", "location", "dist");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.corelib", "location", "corelib");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.mainlib", "location", "lib/main");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.build", "location", "../../../core_build");		
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.mainlib", "location", "${dir.core.build}/lib/main");		
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "server.main.class", "value", CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "server.main.jar", "value", CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "java.complile.option.debug", "value", "on");
	}
}
