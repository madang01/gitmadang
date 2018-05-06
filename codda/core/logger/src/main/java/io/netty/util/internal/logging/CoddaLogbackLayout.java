package io.netty.util.internal.logging;

import java.text.SimpleDateFormat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.helpers.Transform;

public class CoddaLogbackLayout extends LayoutBase<ILoggingEvent> {
	@Override
	public String doLayout(ILoggingEvent iLoggingEvent) {
		String fileName = null;
		int lineNumber = 0;
		
		StackTraceElement[]  stackTraceElement = iLoggingEvent.getCallerData();	
		fileName = stackTraceElement[0].getFileName();
		
		if (fileName.equals("Slf4JLogger.java")) {
			fileName = stackTraceElement[1].getFileName();
			lineNumber = stackTraceElement[1].getLineNumber();
		} else {
			lineNumber = stackTraceElement[0].getLineNumber();
		}
		
		StringBuilder  logbackLayoutStringBuffer = new StringBuilder();
	    logbackLayoutStringBuffer.append(String.format("%-5s", iLoggingEvent.getLevel()));
	    logbackLayoutStringBuffer.append(" ");
	    
	    SimpleDateFormat  timestampSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    logbackLayoutStringBuffer.append(timestampSimpleDateFormat.format(new java.util.Date(iLoggingEvent.getTimeStamp())));
	    logbackLayoutStringBuffer.append(" [");
	    logbackLayoutStringBuffer.append(iLoggingEvent.getThreadName());
	    logbackLayoutStringBuffer.append("] ");
	    logbackLayoutStringBuffer.append(iLoggingEvent.getFormattedMessage());
	    logbackLayoutStringBuffer.append(" (");
	    logbackLayoutStringBuffer.append(fileName);
	    logbackLayoutStringBuffer.append(":");
	    logbackLayoutStringBuffer.append(lineNumber);
	    logbackLayoutStringBuffer.append(")");
	    logbackLayoutStringBuffer.append(CoreConstants.LINE_SEPARATOR);
	    
	    final IThrowableProxy throwableProxy = iLoggingEvent.getThrowableProxy();
	    
	    if (null != throwableProxy) {
	    	logbackLayoutStringBuffer.append(throwableProxy.getClassName());
	    	logbackLayoutStringBuffer.append(" ");
	    	logbackLayoutStringBuffer.append(throwableProxy.getMessage());
	    	logbackLayoutStringBuffer.append(CoreConstants.LINE_SEPARATOR);
	    	
	    	int commonFrames = throwableProxy.getCommonFrames();
	    	StackTraceElementProxy[] stepArray = throwableProxy.getStackTraceElementProxyArray();
	    	for (int i=0; i < stepArray.length - commonFrames; i++) {
	    		StackTraceElementProxy step = stepArray[i];
	    		
	    		logbackLayoutStringBuffer.append("\t");
	    		logbackLayoutStringBuffer.append(Transform.escapeTags(step.toString()));
	    		logbackLayoutStringBuffer.append(CoreConstants.LINE_SEPARATOR);
	    	}
	    	if (commonFrames > 0) {
	    		logbackLayoutStringBuffer.append("\t");
	    		logbackLayoutStringBuffer.append("\t... " + commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
	    	}
	    }	    
	    
	    return logbackLayoutStringBuffer.toString();

	}
}
