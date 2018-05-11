package kr.pe.codda.common.buildsystem;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class ServerAntBuildXMLFileContenetsBuilder extends AbstractAntBuildXMLContentsBuilder {
	public static String PREFIX_OF_DYNAMIC_CLASS_RELATIVE_PATH = 
			new StringBuilder().append(CommonStaticFinalVars.BASE_DYNAMIC_CLASS_FULL_NAME.replaceAll("\\.", "/"))
			.append("/**").toString();

	public static String build(String mainProjectName) {
		final String builderProjectName = new StringBuilder().append(mainProjectName)
				.append("_server").toString();
		final String defaultTargetName = "compile.only.appinf";
		final String baseDirectory = ".";
		final int depth=0;
		boolean whetherOrNotToIncludeCoreLib = true; // 코어 라이브러리 포함 여부, true : 포함, false : 미포함
		
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
		addLogbackJarUnionPart(contentsStringBuilder, depth, "logback.jarlibs");
		
		addNewLine(contentsStringBuilder);		
		addCoreCommonJarUnionPart(contentsStringBuilder, depth, "core.common.jarlibs");
		
		addNewLine(contentsStringBuilder);
		addCoreServerJarUnionPart(contentsStringBuilder, depth, "core.server.jarlibs");
		
		addNewLine(contentsStringBuilder);
		addCoreAllJarUnionPart(contentsStringBuilder, depth, "core.all.jarlibs", "core.common.jarlibs", "core.server.jarlibs");
		
		addNewLine(contentsStringBuilder);
		addJunitTestJarUnionPart(contentsStringBuilder, depth, "core.junitlib.jarlibs");
		
		addNewLine(contentsStringBuilder);
		addCleanMainTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addInitTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeUnixCoreTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeDosCoreTargetPart(contentsStringBuilder, depth);

		addNewLine(contentsStringBuilder);
		addMakeCoreTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCopyCoreTargetPart(contentsStringBuilder, depth, whetherOrNotToIncludeCoreLib);
		
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
		addOnlyCompileAppINFTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCleanTestTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCompileTestTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addJunitTestTargetPart(contentsStringBuilder, depth);

		addNewLine(contentsStringBuilder);
		addROOTEndTag(contentsStringBuilder);
		return  contentsStringBuilder.toString();
	}
	
	public static void addJunitTestTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "test");
		addAttribute(contentsStringBuilder, "depends", "compile.test");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.report}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<junit");
		addAttribute(contentsStringBuilder, "printsummary", "yes");
		addAttribute(contentsStringBuilder, "haltonerror", "yes");
		addAttribute(contentsStringBuilder, "haltonfailure", "yes");
		addAttribute(contentsStringBuilder, "fork", "yes");
		addAttribute(contentsStringBuilder, "showoutput", "true");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<formatter");
		addAttribute(contentsStringBuilder, "type", "xml");
		contentsStringBuilder.append(" />");

		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.dist}/${server.main.jar.name}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.test.lib}");
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
		contentsStringBuilder.append("<union");
		addAttribute(contentsStringBuilder, "refid", "logback.jarlibs");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<pathelement");
		addAttribute(contentsStringBuilder, "location", "${dir.test.build}");
		contentsStringBuilder.append(" />");

		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</classpath>");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<batchtest");
		addAttribute(contentsStringBuilder, "todir", "${dir.report}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.test.src}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "kr/pe/codda/**/*Test.java");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</batchtest>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</junit>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCompileTestTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.test");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.test.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.test.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.test.build}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.dist}/${server.main.jar.name}");
		contentsStringBuilder.append(" />");
		
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
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
		contentsStringBuilder.append("<union");
		addAttribute(contentsStringBuilder, "refid", "core.junitlib.jarlibs");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.test.lib}");
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
	
	public static void addCleanTestTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "clean.test");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.report}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.test.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}


	public static void addCleanMainTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "clean");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.main.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.dist}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
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
		contentsStringBuilder.append("<mkdir dir=\"${dir.main.lib}/ex\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.main.lib}/in\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.main.build}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.dist}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.appinf}/classes\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.corelib}/ex\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.corelib}/in\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}	
	
	
	
	public static void addCompileMainTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.main");
		addAttribute(contentsStringBuilder, "depends", "copy.core.all");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.main.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.main.build}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<exclude");	
		
		addAttribute(contentsStringBuilder, "name", PREFIX_OF_DYNAMIC_CLASS_RELATIVE_PATH);
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/in");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}");
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
		addAttribute(contentsStringBuilder, "destfile", "${dir.dist}/${server.main.jar.name}");
		addAttribute(contentsStringBuilder, "basedir", "${dir.main.build}");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/in");
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
		addAttribute(contentsStringBuilder, "value", "${server.main.class.name}");
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
		
		addAppINFJavaCompilePart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}

	private static void addAppINFJavaCompilePart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.main.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.appinf}/classes");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");

		
		
		addAttribute(contentsStringBuilder, "name", PREFIX_OF_DYNAMIC_CLASS_RELATIVE_PATH);
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
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
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/in");
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
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addOnlyCompileAppINFTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.only.appinf");
		addAttribute(contentsStringBuilder, "depends", "init.var");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.appinf}/classes");
		contentsStringBuilder.append(" />");
		
		addAppINFJavaCompilePart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	private static void addProprtiesPart(final int depth, StringBuilder contentsStringBuilder) {
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.main.src", "location", "src/main/java");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.main.build", "location", "build/main");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.main.lib", "location", "lib/main");
		
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
		addPropertyTag(contentsStringBuilder, "dir.test.src", "location", "src/test/java");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.test.build", "location", "build/test");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.test.lib", "location", "lib/test");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.logger.build", "location", "../../../core/logger");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.all.build", "location", "../../../core/all");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.common.build", "location", "../../../core/common");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.server.build", "location", "../../../core/server");		
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "server.main.class.name", "value", CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "server.main.jar.name", "value", CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "core.all.jar.name", "value", CommonStaticFinalVars.CORE_ALL_JAR_FILE_NAME);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "java.complile.option.debug", "value", "on");
	}
}
