package ch.qos.logback.classic.joran;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 로그 설정 파일 내장 테스트
 * @author Won Jonghoon
 *
 */
public class JoranConfiguratorTest {
	@Test
	public void testDoConfigure() {
		
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
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			configurator.doConfigure(new ByteArrayInputStream(contentsOfConfigFile.getBytes(Charset.forName("UTF8"))));
		} catch(JoranException e) {
			e.printStackTrace();
			
			org.junit.Assert.fail(e.getMessage());
		}
		
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori");
		log.info("test");
		log.info("test11111");
		
		log = LoggerFactory.getLogger("kr2.pe.sinnori");
		log.info("this log writes to console but it doesn't write to logger.log file because the logger name[kr2.pe.sinnori] doesn't start with kr.pe.sinnori");
	}
}
