package main;

import kr.pe.sinnori.gui.config.lib.WindowManger;


public class SinnoriProjectMangerMain {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        // fileAppender.setFile(file);
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();        */

	    
		WindowManger.getInstance().startMainWindow();
	}

}
