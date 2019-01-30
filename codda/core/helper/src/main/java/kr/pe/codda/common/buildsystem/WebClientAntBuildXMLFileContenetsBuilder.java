package kr.pe.codda.common.buildsystem;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public class WebClientAntBuildXMLFileContenetsBuilder extends AbstractAntBuildXMLContentsBuilder {
	
	public static String build(String mainProjectName) {
		final String builderProjectName = new StringBuilder().append(mainProjectName)
				.append("_webclient").toString();
		final String defaultTargetName = "compile.webclass.only";
		final String baseDirectory = ".";
		final int depth=0;
		boolean whetherOrNotToIncludeCoreLib = false; // 코어 라이브러리 포함 여부, true : 포함, false : 미포함
		boolean whetherOrNotToIncludeLogbackLib = false; // 로그백 라이브러리 포함 여부, true : 포함, false : 미포함
		
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
		addCopyCoreTargetPart(contentsStringBuilder, depth, whetherOrNotToIncludeCoreLib, whetherOrNotToIncludeLogbackLib);
		
		addNewLine(contentsStringBuilder);
		addCompileWebLibTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addMakeWebLibPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCopyDistWebLibTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCompileWebClassTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addAllTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addCleanWebClassTargetPart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		addOnlyCompileWebClassTargetPart(contentsStringBuilder, depth);
		
		// FIXME!
		
		addNewLine(contentsStringBuilder);
		addROOTEndTag(contentsStringBuilder);
		
		return contentsStringBuilder.toString();
	}
	
	public static void addOnlyCompileWebClassTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.webclass.only");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.class}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);		
		addWebClassJavaCompilePart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCleanWebClassTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "clean.webclass");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.class}");
		contentsStringBuilder.append(" />");
		
		/*addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<make");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.class}");
		contentsStringBuilder.append(" />");*/
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addAllTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "all");
		addAttribute(contentsStringBuilder, "depends", "compile.webclass");
		contentsStringBuilder.append(" />");
	}
	
	public static void addCompileWebClassTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.webclass");
		addAttribute(contentsStringBuilder, "depends", "dist.weblib");
		contentsStringBuilder.append(">");
		
		addWebClassJavaCompilePart(contentsStringBuilder, depth);
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}

	private static void addWebClassJavaCompilePart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<javac");
		addAttribute(contentsStringBuilder, "debug", "${java.complile.option.debug}");
		addAttribute(contentsStringBuilder, "debuglevel", "lines,vars,source");
		addAttribute(contentsStringBuilder, "encoding", "UTF-8");
		addAttribute(contentsStringBuilder, "includeantruntime", "false");
		addAttribute(contentsStringBuilder, "srcdir", "${dir.main.src}");
		addAttribute(contentsStringBuilder, "destdir", "${dir.webinf.class}");
		addAttribute(contentsStringBuilder, "excludes", "kr/pe/codda/weblib/**");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.lib}");
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
		addAttribute(contentsStringBuilder, "dir", "${servlet.systemlib.path}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*-api.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
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
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("</classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</javac>");
	}
	
	public static void addCopyDistWebLibTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "dist.weblib");
		addAttribute(contentsStringBuilder, "depends", "make.weblib");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.webinf.lib}");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "true");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.dist}/${weblib.jar.name}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.webinf.lib}");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "false");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.main.lib}/ex/*.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addMakeWebLibPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.weblib");
		addAttribute(contentsStringBuilder, "depends", "compile.weblib");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<jar");
		addAttribute(contentsStringBuilder, "jarfile", "${dir.dist}/${weblib.jar.name}");
		addAttribute(contentsStringBuilder, "basedir", "${dir.weblib.build}");
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
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</jar>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");
	}
	
	public static void addCompileWebLibTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "compile.weblib");
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
		addAttribute(contentsStringBuilder, "destdir", "${dir.weblib.build}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "kr/pe/codda/weblib/**");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<classpath>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "dir", "${servlet.systemlib.path}");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 5);
		contentsStringBuilder.append("<include");
		addAttribute(contentsStringBuilder, "name", "**/*-api.jar");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 4);
		contentsStringBuilder.append("</fileset>");
		
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
		contentsStringBuilder.append("<echo message=\"servlet.systemlib.path=${servlet.systemlib.path}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/ex");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir");
		addAttribute(contentsStringBuilder, "dir", "${dir.main.lib}/in");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.weblib.build}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.corelib}/ex\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.corelib}/in\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.dist}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.webinf.lib}\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<mkdir dir=\"${dir.webinf.class}\" />");

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
		addAttribute(contentsStringBuilder, "dir", "${dir.weblib.build}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.dist}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.corelib}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.lib}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<delete");
		addAttribute(contentsStringBuilder, "dir", "${dir.webinf.class}");
		contentsStringBuilder.append(" />");
		
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
		addPropertyTag(contentsStringBuilder, "dir.weblib.build", "location", "build/weblib");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.main.lib", "location", "lib/main");
		
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
		addPropertyTag(contentsStringBuilder, "dir.logger.build", "location", "../../../../core/logger");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.all.build", "location", "../../../../core/all");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.common.build", "location", "../../../../core/common");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.core.server.build", "location", "../../../../core/server");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "core.all.jar.name", "value", CommonStaticFinalVars.CORE_ALL_JAR_FILE_NAME);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "weblib.jar.name", "value", CommonStaticFinalVars.WEBCLIENT_CORE_JAR_SHORT_FILE_NAME_VALUE);
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.webinf.class", "location", "../../web_app_base/ROOT/WEB-INF/classes");
		
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "dir.webinf.lib", "location", "../../web_app_base/ROOT/WEB-INF/lib");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<property");
		addAttribute(contentsStringBuilder, "file", "webAnt.properties");
		contentsStringBuilder.append(" />");
				
		addNewLine(contentsStringBuilder);		
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		addPropertyTag(contentsStringBuilder, "java.complile.option.debug", "value", "on");
	}
}
