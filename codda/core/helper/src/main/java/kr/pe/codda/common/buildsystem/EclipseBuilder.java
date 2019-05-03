package kr.pe.codda.common.buildsystem;

import java.io.File;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class EclipseBuilder {
	private InternalLogger log = InternalLoggerFactory.getInstance(EclipseBuilder.class);

	private String installedPathString = null;

	public EclipseBuilder(String installedPathString) throws BuildSystemException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		checkValidPath("the installed path", installedPathString);

		this.installedPathString = installedPathString;
	}

	private void checkValidPath(String title, String targetPathString) throws BuildSystemException {
		File targetPath = new File(targetPathString);
		if (!targetPath.exists()) {
			String errorMessage = new StringBuilder(title).append("[").append(targetPathString)
					.append("] does not exist").toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!targetPath.isDirectory()) {
			String errorMessage = new StringBuilder(title).append("[").append(targetPathString)
					.append("] isn't a directory").toString();
			throw new BuildSystemException(errorMessage);
		}
	}
	
	private void createCoddaAllEclipeProjectXMLFile(String eclipseProjectName, EclipsePath[] eclipsePathList)
			throws BuildSystemException {
		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);

		String coddaCoreAllEclipeProjectXMLFilePathString = new StringBuilder().append(commonResourcesPathString)
				.append(File.separator).append("eclipse").append(File.separator).append("workbench")
				.append(File.separator).append(eclipseProjectName).append(File.separator).append(".project").toString();

		// log.info("coddaCoreAllEclipeProjectXMLFilePathString=[{}]", coddaCoreAllEclipeProjectXMLFilePathString);

		File coddaCoreAllEclipeProjectXMLFile = new File(coddaCoreAllEclipeProjectXMLFilePathString);

		StringBuilder coddaCoreAllEclipeProjectXMLFileContentsStringBuilder = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CommonStaticFinalVars.NEWLINE)
				.append("<projectDescription>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<name>")
				.append(eclipseProjectName)
				.append("</name>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<comment></comment>").append(CommonStaticFinalVars.NEWLINE).append("	").append("<projects>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("</projects>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("<buildSpec>")
				.append(CommonStaticFinalVars.NEWLINE).append("		").append("<buildCommand>")
				.append(CommonStaticFinalVars.NEWLINE).append("			")
				.append("<name>org.eclipse.jdt.core.javabuilder</name>").append(CommonStaticFinalVars.NEWLINE)
				.append("			").append("<arguments>").append(CommonStaticFinalVars.NEWLINE).append("			")
				.append("</arguments>").append(CommonStaticFinalVars.NEWLINE).append("		").append("</buildCommand>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("</buildSpec>")
				.append(CommonStaticFinalVars.NEWLINE).append("	").append("<natures>")
				.append(CommonStaticFinalVars.NEWLINE).append("		")
				.append("<nature>org.eclipse.jdt.core.javanature</nature>").append(CommonStaticFinalVars.NEWLINE)
				.append("	").append("</natures>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<linkedResources>");

		String installedPathStringForEclipseRelativePath = installedPathString.replaceAll("\\\\", "/");

		for (EclipsePath eclipsePath : eclipsePathList) {
			coddaCoreAllEclipeProjectXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE)
					.append("		").append("<link>").append(CommonStaticFinalVars.NEWLINE).append("			")
					.append("<name>").append(eclipsePath.getPathName()).append("</name>")
					.append(CommonStaticFinalVars.NEWLINE).append("			").append("<type>2</type>")
					.append(CommonStaticFinalVars.NEWLINE).append("			").append("<location>")
					.append(installedPathStringForEclipseRelativePath).append("/").append(eclipsePath.getRelativePath())
					.append("</location>").append(CommonStaticFinalVars.NEWLINE).append("		").append("</link>");
		}

		coddaCoreAllEclipeProjectXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("</linkedResources>").append(CommonStaticFinalVars.NEWLINE).append("</projectDescription>");

		String coddaCoreAllEclipeProjectXMLFileContents = coddaCoreAllEclipeProjectXMLFileContentsStringBuilder
				.toString();

		// log.info("coddaCoreAllEclipeProjectXMLFileContents=[{}]", coddaCoreAllEclipeProjectXMLFileContents);

		
		try {
			CommonStaticUtil.saveFile(coddaCoreAllEclipeProjectXMLFile, coddaCoreAllEclipeProjectXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to create the eclise .project file[")
					.append(coddaCoreAllEclipeProjectXMLFilePathString)
					.append("] becase io error occured").toString();
			
			log.warn(errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
	}

	private void createCoddaAllEclipeClasspathXMLFile(String eclipseProjectName, EclipsePath[] eclipsePathList,
			String[] eclipseLibiaryRelativePathStringList) throws BuildSystemException {
		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);

		String coddaCoreAllEclipeClasspathXMLFilePathString = new StringBuilder().append(commonResourcesPathString)
				.append(File.separator).append("eclipse").append(File.separator).append("workbench")
				.append(File.separator).append(eclipseProjectName).append(File.separator).append(".classpath")
				.toString();

		// log.info("coddaCoreAllEclipeClasspathXMLFilePathString=[{}]", coddaCoreAllEclipeClasspathXMLFilePathString);

		File coddaCoreAllEclipeClasspathXMLFile = new File(coddaCoreAllEclipeClasspathXMLFilePathString);

		StringBuilder coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(CommonStaticFinalVars.NEWLINE)
				.append("<classpath>").append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>")
				.append(CommonStaticFinalVars.NEWLINE).append("	")
				.append("<classpathentry kind=\"output\" path=\"bin\"/>");

		for (EclipsePath eclipsePath : eclipsePathList) {
			coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
					.append("<classpathentry kind=\"src\" path=\"").append(eclipsePath.getPathName()).append("\"/>");
		}

		String installedPathStringForEclipseRelativePath = installedPathString.replaceAll("\\\\", "/");

		for (String eclipseLibiaryRelativePathString : eclipseLibiaryRelativePathStringList) {
			coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE).append("	")
					.append("<classpathentry kind=\"lib\" path=\"").append(installedPathStringForEclipseRelativePath)
					.append("/").append(eclipseLibiaryRelativePathString).append("\"/>");
		}

		coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder.append(CommonStaticFinalVars.NEWLINE)
				.append("</classpath>");

		String coddaCoreAllEclipeClasspathXMLFileContentsString = coddaCoreAllEclipeClasspathXMLFileContentsStringBuilder
				.toString();

		// log.info("coddaCoreAllEclipeClasspathXMLFileContentsString=[{}]", coddaCoreAllEclipeClasspathXMLFileContentsString);
		
		
		try {
			CommonStaticUtil.saveFile(coddaCoreAllEclipeClasspathXMLFile, coddaCoreAllEclipeClasspathXMLFileContentsString,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to create the eclise .classpath file[")
					.append(coddaCoreAllEclipeClasspathXMLFilePathString)
					.append("] becase io error occured").toString();
			
			log.warn(errorMessage, e);
			throw new BuildSystemException(errorMessage);
		}
	}
	

	public void createCoddaCoreAllEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_core_all";

		EclipsePath[] eclipsePathList = { new EclipsePath("core_common_main_src", "core/common/src/main/java"),
				new EclipsePath("core_client_main_src", "core/client/src/main/java"),
				new EclipsePath("core_server_main_src", "core/server/src/main/java"),
				new EclipsePath("core_all_test_src", "core/all/src/test/java") };

		String[] eclipseLibiaryRelativePathStringList = { "core/server/lib/main/ex/commons-dbcp2-2.0.1.jar",
				"core/server/lib/main/ex/commons-pool2-2.5.0.jar", "core/common/lib/test/commons-exec-1.3.jar",
				"core/common/lib/test/byte-buddy-1.7.9.jar", "core/common/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/common/lib/test/mockito-core-2.13.4.jar", "core/common/lib/test/objenesis-2.6.jar",
				"core/logger/lib/ex/jcl-over-slf4j-1.7.25.jar", "core/logger/lib/ex/logback-classic-1.2.3.jar",
				"core/logger/lib/ex/logback-core-1.2.3.jar", "core/logger/lib/ex/slf4j-api-1.7.25.jar",
				"core/logger/dist/codda-logger.jar", "core/common/dist/codda-core-junitlib.jar" };

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void createCoddaCoreLoggerEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_logger";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "core/logger/src/main/java")};

		String[] eclipseLibiaryRelativePathStringList = { "core/logger/lib/ex/commons-logging-1.2.jar",
				"core/logger/lib/ex/log4j-1.2.17.jar", 
				"core/logger/lib/ex/log4j-api-2.11.0.jar",
				"core/logger/lib/ex/log4j-core-2.11.0.jar", 
				"core/logger/lib/ex/logback-classic-1.2.3.jar",
				"core/logger/lib/ex/logback-core-1.2.3.jar", 
				"core/logger/lib/ex/slf4j-api-1.7.25.jar"
		};

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}

	public void createCoddaCoreHelperEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_helper";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "core/helper/src/main/java"),
				new EclipsePath("test_src", "core/helper/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = { 
				"core/helper/lib/main/in/jgoodies-common.jar",
				"core/helper/lib/main/in/jgoodies-forms.jar",
				"core/common/lib/test/byte-buddy-1.7.9.jar",
				"core/common/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/common/lib/test/commons-exec-1.3.jar",
				"core/common/lib/test/mockito-core-2.13.4.jar",
				"core/common/lib/test/objenesis-2.6.jar",
				"core/common/dist/codda-core-common.jar",
				"core/logger/lib/ex/jcl-over-slf4j-1.7.25.jar",
				"core/logger/lib/ex/logback-classic-1.2.3.jar",
				"core/logger/lib/ex/logback-core-1.2.3.jar",
				"core/logger/lib/ex/slf4j-api-1.7.25.jar",
				"core/helper/lib/main/in/commons-cli-1.4.jar",
				"core/helper/lib/test/codda-core-junitlib.jar",
				"core/helper/lib/main/in/commons-io-2.6.jar"
		};

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void createCoddaSampleBaseServerEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_sample_base_server";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/server_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/server_build/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = { 
				"project/sample_base/server_build/corelib/ex/commons-dbcp2-2.0.1.jar",
				"project/sample_base/server_build/corelib/ex/commons-pool2-2.5.0.jar",
				"project/sample_base/server_build/corelib/ex/jcl-over-slf4j-1.7.25.jar",
				"project/sample_base/server_build/corelib/ex/logback-classic-1.2.3.jar",
				"project/sample_base/server_build/corelib/ex/logback-core-1.2.3.jar",
				"project/sample_base/server_build/corelib/ex/slf4j-api-1.7.25.jar",
				"project/sample_base/server_build/lib/main/ex/jooq-3.10.6.jar",
				"project/sample_base/server_build/lib/main/ex/jooq-codegen-3.10.6.jar",
				"project/sample_base/server_build/lib/main/ex/jooq-meta-3.10.6.jar",
				"core/common/lib/test/byte-buddy-1.7.9.jar", 
				"core/common/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/common/lib/test/commons-exec-1.3.jar",
				"core/common/lib/test/mockito-core-2.13.4.jar", 
				"core/common/lib/test/objenesis-2.6.jar",
				"util/jooq/mysql-connector-java-5.1.46.jar",
				"project/sample_base/server_build/corelib/in/codda-core-all.jar",
				"core/common/dist/codda-core-junitlib.jar",
				"project/sample_base/server_build/corelib/ex/gson-2.8.5.jar",
				"project/sample_base/server_build/lib/main/ex/oracle-mail-1.4.7.jar",
		};

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void createCoddaSampleBaseAppClientEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_sample_base_appclient";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/client_build/app_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/client_build/app_build/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = { 
				"project/sample_base/client_build/app_build/corelib/ex/gson-2.8.5.jar",
				"project/sample_base/client_build/app_build/corelib/ex/jcl-over-slf4j-1.7.25.jar",
				"project/sample_base/client_build/app_build/corelib/ex/logback-classic-1.2.3.jar",
				"project/sample_base/client_build/app_build/corelib/ex/logback-core-1.2.3.jar",
				"project/sample_base/client_build/app_build/corelib/ex/slf4j-api-1.7.25.jar",
				"project/sample_base/client_build/app_build/corelib/in/codda-core-all.jar",
		};

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
	
	public void createCoddaSampleBaseWebClientEclipeWorkbenchFiles() throws BuildSystemException {
		String eclipseProjectName = "codda_sample_base_webclient";

		EclipsePath[] eclipsePathList = { new EclipsePath("main_src", "project/sample_base/client_build/web_build/src/main/java"),
				new EclipsePath("test_src", "project/sample_base/client_build/web_build/src/test/java")};

		String[] eclipseLibiaryRelativePathStringList = {
				"project/sample_base/client_build/web_build/corelib/ex/gson-2.8.5.jar",
				"core/common/dist/codda-core-junitlib.jar",
				"core/common/lib/test/byte-buddy-1.7.9.jar",
				"core/common/lib/test/byte-buddy-agent-1.7.9.jar",
				"core/common/lib/test/commons-exec-1.3.jar", 
				"core/common/lib/test/mockito-core-2.13.4.jar",
				"core/common/lib/test/objenesis-2.6.jar",				
				"project/sample_base/client_build/web_build/corelib/ex/codda-core-all.jar",				
				"project/sample_base/client_build/web_build/lib/main/ex/commons-fileupload-1.3.2.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/commons-lang3-3.7.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/commons-text-1.3.jar",				
				"project/sample_base/client_build/web_build/lib/main/ex/commons-io-2.6.jar",
				"project/sample_base/client_build/web_build/lib/main/ex/simplecaptcha-1.2.1.jar",
				"project/sample_base/client_build/web_build/lib/test/httpcore-4.4.10.jar",
				"project/sample_base/client_build/web_build/lib/test/httpmime-4.5.6.jar",				
		};

		createCoddaAllEclipeProjectXMLFile(eclipseProjectName, eclipsePathList);

		createCoddaAllEclipeClasspathXMLFile(eclipseProjectName, eclipsePathList, eclipseLibiaryRelativePathStringList);
	}
}
