package kr.pe.codda.common.buildsystem;

import java.util.ArrayList;
import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class AbstractAntBuildXMLContentsBuilder {
	public static void addHeader(StringBuilder contentsStringBuilder) {		
		contentsStringBuilder.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
	}
	
	public static void addNewLine(StringBuilder contentsStringBuilder) {
		contentsStringBuilder.append(CommonStaticFinalVars.NEWLINE);
	}
	
	public static void addAttribute(StringBuilder contentsStringBuilder, String attributeKey, String attributeValue) {
		contentsStringBuilder.append(" ");
		contentsStringBuilder.append(attributeKey);
		contentsStringBuilder.append("=\"");
		contentsStringBuilder.append(attributeValue);
		contentsStringBuilder.append("\"");
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
	
	public static void addEndTag(StringBuilder contentsStringBuilder, String tagName) {
		contentsStringBuilder.append("</");
		contentsStringBuilder.append(tagName);
		contentsStringBuilder.append(">");
	}
	
	public static void addROOTEndTag(StringBuilder contentsStringBuilder) {
		addEndTag(contentsStringBuilder, "project");
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
		addNewLine(contentsStringBuilder);
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
	
	public static void addLogbackJarUnionPart(StringBuilder contentsStringBuilder, final int depth, String unionID) {
		List<String> logbackJarFilePathStringList = new ArrayList<String>();
		logbackJarFilePathStringList.add("${dir.logger.build}/lib/ex/slf4j-api-1.7.25.jar");
		logbackJarFilePathStringList.add("${dir.logger.build}/lib/ex/logback-core-1.2.3.jar");
		logbackJarFilePathStringList.add("${dir.logger.build}/lib/ex/logback-classic-1.2.3.jar");
		logbackJarFilePathStringList.add("${dir.logger.build}/lib/ex/jcl-over-slf4j-1.7.25.jar");
		// addFileSetUnion(contentsStringBuilder, depth, "logback.jarlibs", null, logbackJarFilePathStringList);
		// String unionID = "logback.jarlibs";
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<union id=\"");
		contentsStringBuilder.append(unionID);
		contentsStringBuilder.append("\">");
		
		for (String filePathString : logbackJarFilePathStringList) {
			if (filePathString.lastIndexOf("jcl-over-slf4j") >= 0) {
				addNewLine(contentsStringBuilder);
				CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
				contentsStringBuilder.append("<!-- dbcp2 depends Apache commons-logging(=jcl-over-slf4j) -->");
			}
			
			addNewLine(contentsStringBuilder);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
			contentsStringBuilder.append("<fileset file=\"");
			contentsStringBuilder.append(filePathString);
			contentsStringBuilder.append("\" />");
		}		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</union>");
	}
	
	public static void addCoreCommonJarUnionPart(StringBuilder contentsStringBuilder, final int depth, String unionID) {
		List<String> coreCommonJarFilePathStringList = new ArrayList<String>();
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/main/ex/commons-io-2.6.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/main/ex/commons-collections4-4.1.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/main/ex/commons-codec-1.11.jar");
		addFileSetUnion(contentsStringBuilder, depth, unionID, null, coreCommonJarFilePathStringList);
	}
	
	public static void addCoreServerJarUnionPart(StringBuilder contentsStringBuilder, final int depth, String unionID) {
		List<String> coreCommonJarFilePathStringList = new ArrayList<String>();
		coreCommonJarFilePathStringList.add("${dir.core.server.build}/lib/main/ex/commons-dbcp2-2.0.1.jar");
		coreCommonJarFilePathStringList.add("${dir.core.server.build}/lib/main/ex/commons-pool2-2.5.0.jar");
		addFileSetUnion(contentsStringBuilder, depth, unionID, null, coreCommonJarFilePathStringList);
	}
	
	public static void addJunitTestJarUnionPart(StringBuilder contentsStringBuilder, final int depth, String unionID) {
		List<String> coreCommonJarFilePathStringList = new ArrayList<String>();
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/hamcrest-core-1.3.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/junit-4.12.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/byte-buddy-1.7.9.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/byte-buddy-agent-1.7.9.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/mockito-core-2.13.4.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/objenesis-2.6.jar");
		coreCommonJarFilePathStringList.add("${dir.core.common.build}/lib/test/commons-exec-1.3.jar");
		addFileSetUnion(contentsStringBuilder, depth, unionID, null, coreCommonJarFilePathStringList);
	}
	
	public static void addCoreAllJarUnionPart(StringBuilder contentsStringBuilder, final int depth,
			String unionID, String refidOfCommonJarLibsUnion, String refidOfServerJarLibsUnion) {
		// String unionID = "core.all.jarlibs";
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<union id=\"");
		contentsStringBuilder.append(unionID);
		contentsStringBuilder.append("\">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<union refid=\"");
		contentsStringBuilder.append(refidOfCommonJarLibsUnion);
		contentsStringBuilder.append("\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<union refid=\"");
		contentsStringBuilder.append(refidOfServerJarLibsUnion);
		contentsStringBuilder.append("\" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</union>");
	}
	
	public static void addMakeUnixCoreTargetPart(StringBuilder contentsStringBuilder, int depth) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "make.unix.core.all");
		addAttribute(contentsStringBuilder, "if", "is.unix.yes");
		addAttribute(contentsStringBuilder, "depends", "init.var");
		contentsStringBuilder.append(">");
			
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<exec");
		addAttribute(contentsStringBuilder, "dir", "${dir.core.all.build}");
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
		addAttribute(contentsStringBuilder, "name", "make.dos.core.all");
		addAttribute(contentsStringBuilder, "if", "is.windows.yes");
		addAttribute(contentsStringBuilder, "depends", "make.unix.core.all");
		contentsStringBuilder.append(">");
			
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<exec");
		addAttribute(contentsStringBuilder, "dir", "${dir.core.all.build}");
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
		addAttribute(contentsStringBuilder, "name", "make.core.all");
		addAttribute(contentsStringBuilder, "depends", "make.dos.core.all");
		contentsStringBuilder.append(" />");
	}
	
	public static void addCopyCoreTargetPart(StringBuilder contentsStringBuilder, int depth, boolean whetherOrNotToIncludeCoreLib, boolean whetherOrNotToIncludeLogbackLib) {
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("<target");
		addAttribute(contentsStringBuilder, "name", "copy.core.all");
		addAttribute(contentsStringBuilder, "depends", "make.core.all");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		if (whetherOrNotToIncludeCoreLib) {
			addAttribute(contentsStringBuilder, "todir", "${dir.corelib}/in");
		} else {
			addAttribute(contentsStringBuilder, "todir", "${dir.corelib}/ex");
		}
		
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "true");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<fileset");
		addAttribute(contentsStringBuilder, "file", "${dir.core.all.build}/dist/${core.all.jar.name}");
		contentsStringBuilder.append(" />");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("<copy");
		addAttribute(contentsStringBuilder, "todir", "${dir.corelib}/ex");
		addAttribute(contentsStringBuilder, "verbose", "true");
		addAttribute(contentsStringBuilder, "overwrite", "false");
		contentsStringBuilder.append(">");
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
		contentsStringBuilder.append("<union");
		addAttribute(contentsStringBuilder, "refid", "core.all.jarlibs");
		contentsStringBuilder.append(" />");
		
		if (whetherOrNotToIncludeLogbackLib) {
			addNewLine(contentsStringBuilder);
			CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 3);
			contentsStringBuilder.append("<union");
			addAttribute(contentsStringBuilder, "refid", "logback.jarlibs");
			contentsStringBuilder.append(" />");
		}
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 2);
		contentsStringBuilder.append("</copy>");
		
		
		addNewLine(contentsStringBuilder);
		CommonStaticUtil.addPrefixWithTabCharacters(contentsStringBuilder, depth, 1);
		contentsStringBuilder.append("</target>");		
	}
}
