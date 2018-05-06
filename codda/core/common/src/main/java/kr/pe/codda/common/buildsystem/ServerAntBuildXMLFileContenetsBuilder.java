package kr.pe.codda.common.buildsystem;

import java.util.ArrayList;
import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class ServerAntBuildXMLFileContenetsBuilder {

	public static String build(String mainProjectName) {
		final String builderProjectName = new StringBuilder().append(mainProjectName)
				.append("_server").toString();
		final String defaultTargetName = "compile.only.appinf";
		final String baseDirectory = ".";
		final int depth=0;		 
		
		StringBuilder contentsStringBuilder = new StringBuilder();
		addHeader(contentsStringBuilder);
		
		addNewLine(contentsStringBuilder);
		addROOTStartTag(contentsStringBuilder, builderProjectName, defaultTargetName, baseDirectory);
		
		addProprtiesPart(depth, contentsStringBuilder);
		
		addNewLine(contentsStringBuilder);		
		addWindowsCondition(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addLinuxCondition(contentsStringBuilder, depth);		
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		addLogbackJarUnionPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		addCoreCommonJarUnionPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		addCoreServerJarUnionPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		addCoreJarUnionPart(depth, contentsStringBuilder);
		
		addNewLine(contentsStringBuilder);
		addInitTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeUnixCoreTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeDosCoreTargetPart(contentsStringBuilder, depth);

		addNewLine(contentsStringBuilder);
		addMakeCoreTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCopyCoreTargetPart(contentsStringBuilder, depth);
				
		addNewLine(contentsStringBuilder);
		addCopyAppINFTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCompileMainTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCopyDistLibTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addBuildClassPathPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMainClassPathConvertPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeMainPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCompileAppINFTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addAllTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCleanAppINFTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCompileOnlyAppINFTargetPart(contentsStringBuilder, depth);
		
		// FIXME!
				
		addNewLine(contentsStringBuilder);
		addROOTEndTag(contentsStringBuilder);
		return  contentsStringBuilder.toString();
	}

	private static void addCoreJarUnionPart(final int depth, StringBuilder contentsStringBuilder) {
		List<String> coreRefIDList = new ArrayList<String>();
		coreRefIDList.add("core.common.jarlibs");
		coreRefIDList.add("core.server.jarlibs");
		addFileSetUnion(contentsStringBuilder, depth, "core.jarlibs", coreRefIDList, null);
	}

	private static void addCoreServerJarUnionPart(StringBuilder contentsStringBuilder, final int depth) {
		List<String> coreServerJarFilePathStringList = new ArrayList<String>();
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/mysql-connector-java-5.1.37-bin.jar");
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/commons-pool2-2.5.0.jar");
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/commons-dbcp2-2.0.1.jar");
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/jooq-meta-3.10.6.jar");
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/jooq-codegen-3.10.6.jar");
		coreServerJarFilePathStringList.add("${dir.core.mainlib}/ex/jooq-3.10.6.jar");
		addFileSetUnion(contentsStringBuilder, depth, "core.server.jarlibs", null, coreServerJarFilePathStringList);
	}

	private static void addCoreCommonJarUnionPart(StringBuilder contentsStringBuilder, final int depth) {
		List<String> coreCommonJarFilePathStringList = new ArrayList<String>();
		coreCommonJarFilePathStringList.add("${dir.core.mainlib}/ex/commons-io-2.6.jar");
		coreCommonJarFilePathStringList.add("${dir.core.mainlib}/ex/commons-collections4-4.1.jar");
		coreCommonJarFilePathStringList.add("${dir.core.mainlib}/ex/commons-codec-1.11.jar");
		addFileSetUnion(contentsStringBuilder, depth, "core.common.jarlibs", null, coreCommonJarFilePathStringList);
	}

	private static void addLogbackJarUnionPart(StringBuilder contentsStringBuilder, final int depth) {
		List<String> logbackJarFilePathStringList = new ArrayList<String>();
		logbackJarFilePathStringList.add("${dir.core.mainlib}/ex/slf4j-api-1.7.25.jar");
		logbackJarFilePathStringList.add("${dir.core.mainlib}/ex/jcl-over-slf4j-1.7.25.jar");
		logbackJarFilePathStringList.add("${dir.core.mainlib}/ex/logback-core-1.2.3.jar");
		logbackJarFilePathStringList.add("${dir.core.mainlib}/ex/logback-classic-1.2.3.jar");
		addFileSetUnion(contentsStringBuilder, depth, "logback.jarlibs", null, logbackJarFilePathStringList);
	}

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
	
	public static void addFileSetUnion(StringBuilder contentsStringBuilder, int depth,
			String unionID, 
			List<String> refIDList,
			List<String> filePathStringList) {
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<union id=\"");
		contentsStringBuilder.append(unionID);
		contentsStringBuilder.append("\">");
		
		if (null != refIDList) {
			for (String refID : refIDList) {
				addNewLine(contentsStringBuilder);
				CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
				contentsStringBuilder.append("<union refid=\"");
				contentsStringBuilder.append(refID);
				contentsStringBuilder.append("\" />");
			}
		}
		
		if (null != filePathStringList) {
			for (String filePathString : filePathStringList) {
				addNewLine(contentsStringBuilder);
				CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
				contentsStringBuilder.append("<fileset file=\"");
				contentsStringBuilder.append(filePathString);
				contentsStringBuilder.append("\" />");
			}
		}
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</union>");
	}
	
	public static void addInitTargetPart(StringBuilder contentsStringBuilder, int depth) {
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
	
	public static void addMakeUnixCoreTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.unixcore");
		addAttribute(contentsStringBuilder, "if", "is.unix.yes");
		addAttribute(contentsStringBuilder, "depends", "init.var");
		contentsStringBuilder.append(">");
			
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<exec");
		addAttribute(contentsStringBuilder, "dir", "${dir.core.build}");
		addAttribute(contentsStringBuilder, "executable", "ant");
		contentsStringBuilder.append(" />");		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addMakeDosCoreTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.doscore");
		addAttribute(contentsStringBuilder, "if", "is.windows.yes");
		addAttribute(contentsStringBuilder, "depends", "make.unixcore");
		contentsStringBuilder.append(">");
			
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<exec");
		addAttribute(contentsStringBuilder, "dir", "${dir.core.build}");
		addAttribute(contentsStringBuilder, "executable", "cmd");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<arg");
		addAttribute(contentsStringBuilder, "value", "/c");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<arg");
		addAttribute(contentsStringBuilder, "value", "ant.bat");
		contentsStringBuilder.append(" />");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</exec>");		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addMakeCoreTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.core");
		addAttribute(contentsStringBuilder, "depends", "make.doscore");
		contentsStringBuilder.append(" />");
	}
	
	public static void addCopyCoreTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "copy.core");
		addAttribute(contentsStringBuilder, "depends", "make.core");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/ex");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/in");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.corelib}/in");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "true");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.core.build}/dist/${core.all.jar}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.corelib}/ex");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "true");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<union");
		addAttribute(contentsStringBuilder, "refid", "core.jarlibs");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<union");
		addAttribute(contentsStringBuilder, "refid", "logback.jarlibs");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
		
	}
	
	public static void addCopyAppINFTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "copy.appinf");
		addAttribute(contentsStringBuilder, "depends", "copy.core");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/resources");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.appinf}/resources");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "false");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.core.build}/APP-INF/resources/");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCompileMainTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.main");
		addAttribute(contentsStringBuilder, "depends", "copy.appinf");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.build}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<exclude");

		String firstPrefixOfDynamicClassRelativePath = 
				new StringBuilder().append(CommonStaticFinalVars.FIRST_PREFIX_OF_DYNAMIC_CLASS_FULL_NAME.replaceAll("\\.", "/"))
				.append("**").toString();
		
		addAttribute(contentsStringBuilder, "name", firstPrefixOfDynamicClassRelativePath);
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</javac>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCopyDistLibTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "copy.distlib");
		addAttribute(contentsStringBuilder, "depends", "compile.main");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.dist}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.dist}/lib");
		contentsStringBuilder.append(" />");
		
		
		addNewLine(contentsStringBuilder);
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.dist}/lib");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "false");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/ex");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/ex");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addBuildClassPathPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<path");
		addAttribute(contentsStringBuilder, "id", "build.classpath");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "ex/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "ex/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</path>");
	}
	
	public static void addMainClassPathConvertPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<pathconvert");
		addAttribute(contentsStringBuilder, "property", "manifest.classpath");
		addAttribute(contentsStringBuilder, "pathsep", " ");		
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<path");
		addAttribute(contentsStringBuilder, "refid", "build.classpath");
		contentsStringBuilder.append(" />");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mapper>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<chainedmapper>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<flattenmapper/>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<globmapper");
		addAttribute(contentsStringBuilder, "from", "*.jar");
		addAttribute(contentsStringBuilder, "to", "lib/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</chainedmapper>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</mapper>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</pathconvert>");
	}
	
	public static void addMakeMainPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.main");
		addAttribute(contentsStringBuilder, "depends", "copy.distlib");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<jar");
		addAttribute(contentsStringBuilder, "destfile", "${dir.dist}/${server.main.jar}");
		addAttribute(contentsStringBuilder, "basedir", "${dir.build}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<restrict>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<name");
		addAttribute(contentsStringBuilder, "name", "**/*.class");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<archives>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<zips>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 6);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/in");
		addAttribute(contentsStringBuilder, "includes", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 6);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/in");
		addAttribute(contentsStringBuilder, "includes", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("</zips>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</archives>");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</restrict>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<manifest>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<attribute");
		addAttribute(contentsStringBuilder, "name", "Main-Class");
		addAttribute(contentsStringBuilder, "value", "${server.main.class}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<attribute");
		addAttribute(contentsStringBuilder, "name", "Class-Path");
		addAttribute(contentsStringBuilder, "value", "${manifest.classpath}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</manifest>");		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</jar>");		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCompileAppINFTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.appinf");
		addAttribute(contentsStringBuilder, "depends", "make.main");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.appinf}/classes");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");
		// "kr/pe/codda/impl/**"
		String firstPrefixOfDynamicClassRelativePath = 
				new StringBuilder().append(CommonStaticFinalVars.FIRST_PREFIX_OF_DYNAMIC_CLASS_FULL_NAME.replaceAll("\\.", "/"))
				.append("**").toString();
		
		addAttribute(contentsStringBuilder, "name", firstPrefixOfDynamicClassRelativePath);
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</javac>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	
	public static void addAllTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "all");
		addAttribute(contentsStringBuilder, "depends", "compile.appinf");
		contentsStringBuilder.append(" />");
	}
	
	public static void addCleanAppINFTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "clean.appinf");
		addAttribute(contentsStringBuilder, "depends", "init.var");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCompileOnlyAppINFTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.only.appinf");
		addAttribute(contentsStringBuilder, "depends", "init.var");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.appinf}/classes");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");
		// "kr/pe/codda/impl/**"
		String firstPrefixOfDynamicClassRelativePath = 
				new StringBuilder().append(CommonStaticFinalVars.FIRST_PREFIX_OF_DYNAMIC_CLASS_FULL_NAME.replaceAll("\\.", "/"))
				.append("**").toString();
		
		addAttribute(contentsStringBuilder, "name", firstPrefixOfDynamicClassRelativePath);
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/ex");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.mainlib}/in");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</javac>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	// FIXME!
	
	
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
		addPropertyTag(contentsStringBuilder, "core.all.jar", "value", CommonStaticFinalVars.CORE_ALL_JAR_FILE_NAME);
		// sinnori-core.jar
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "java.complile.option.debug", "value", "on");
	}
}
