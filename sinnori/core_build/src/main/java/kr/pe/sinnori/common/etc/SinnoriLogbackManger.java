package kr.pe.sinnori.common.etc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.type.LogType;


public class SinnoriLogbackManger {
	private SinnoriLogbackManger() {
	}
	
	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class SinnoriLogbackMangerHolder {
		static final SinnoriLogbackManger singleton = new SinnoriLogbackManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriLogbackManger getInstance() {
		return SinnoriLogbackMangerHolder.singleton;
	}
	
	public void setup(String sinnoriInstalledPathString, String mainProjectName, LogType logType) {
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString, mainProjectName, logType);
		
				
		/**
		 * 로그백 설정 파일과 로그 경로가 올바르지 않는  경우 내장한 환경 설정 내용으로 로그백을 설정한다.
		 */
		boolean isValid = false;
		if (null != sinnoriLogPathString && null != logbackConfigFilePathString) {	
			File logbackConfigFile = new File(logbackConfigFilePathString);
			File sinnoriLogPath = new File(sinnoriLogPathString);
			if (logbackConfigFile.exists() &&  logbackConfigFile.isFile() && logbackConfigFile.canRead()) {				
				if (sinnoriLogPath.exists() &&  sinnoriLogPath.isDirectory() && logbackConfigFile.canWrite()) {
					isValid = true;
				}
			}
		}
		
		//System.out.println("isValid="+isValid);
		//System.out.println("logbackConfigFilePathString="+logbackConfigFilePathString);
		
		if (isValid) {
			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
					sinnoriLogPathString);
			System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
					logbackConfigFilePathString);
		} else {
			String contentsOfConfigFile = null;
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("<configuration>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<appender name=\"logfile\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<file>log/logger.log</file>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<encoder>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t\t<pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t</encoder>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t\t<fileNamePattern>log/logFile.%d{yyyy-MM-dd}.log</fileNamePattern>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t\t<maxHistory>15</maxHistory>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t</rollingPolicy>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t</appender>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<appender name=\"console\" class=\"ch.qos.logback.core.ConsoleAppender\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<encoder>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t\t<pattern>%d %-5level [%thread] %msg \\(%F:%L\\)%n</pattern>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t</encoder>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t</appender>\t");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<root level=\"INFO\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<appender-ref ref=\"console\"/>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t</root>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<logger name=\"kr.pe.sinnori\" level=\"INFO\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<appender-ref ref=\"logfile\"/>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t</logger>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<!-- \"mapper\" tag's attribute namespace in the mybatis mapper xml file -->");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t<logger name=\"kr.pr.sinnori.mybatis\" level=\"DEBUG\">");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t\t<appender-ref ref=\"logfile\"/>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("\t</logger>");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("</configuration>");
			stringBuilder.append(System.getProperty("line.separator"));
			contentsOfConfigFile = stringBuilder.toString();
			
			
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			try {
				configurator.doConfigure(new ByteArrayInputStream(contentsOfConfigFile.getBytes(Charset.forName("UTF8"))));
			} catch (JoranException e) {
				Logger log = LoggerFactory
						.getLogger(SinnoriLogbackManger.class);
				log.warn("fail to config logback having sinnori logger format without logback config file", e);
				System.exit(1);
			}
			
			
			StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		}
	}
}
