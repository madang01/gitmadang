package kr.pe.codda.common.logback;

import java.io.File;

import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;


public class CoddaLogbackManger {
	private CoddaLogbackManger() {
	}
	
	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class LogbackMangerHolder {
		static final CoddaLogbackManger singleton = new CoddaLogbackManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static CoddaLogbackManger getInstance() {
		return LogbackMangerHolder.singleton;
	}
	
	public void setup(String installedPathString, String mainProjectName, LogType logType) throws IllegalStateException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		if (null == logType) {
			throw new IllegalArgumentException("the parameter logType is null");
		}
		
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getProjectLogbackConfigFilePathString(installedPathString, mainProjectName);
		String projectLogPathString = BuildSystemPathSupporter.getProjectLogPathString(installedPathString, mainProjectName, logType);
		
		
		{
			File logbackConfigFile = new File(logbackConfigFilePathString);		
			
			
			if (! logbackConfigFile.exists()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! logbackConfigFile.isFile()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] is not a normal file").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! logbackConfigFile.canRead()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] does not have read permissions").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		
		{
			File projectLogPath = new File(projectLogPathString);
			
			if (! projectLogPath.exists()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(projectLogPathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! projectLogPath.isDirectory()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(projectLogPathString)
						.append("] is not a directory").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! projectLogPath.canWrite()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(projectLogPathString)
						.append("] is marked read-only").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH,
				projectLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);
	}
	
	public void setup(String installedPathString) throws IllegalStateException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getProjectLogbackConfigFilePathString(installedPathString, "sample_base");
		String rootLogPathString = BuildSystemPathSupporter.getLogPathString(installedPathString);
		
		
		{
			File logbackConfigFile = new File(logbackConfigFilePathString);		
			
			
			if (! logbackConfigFile.exists()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! logbackConfigFile.isFile()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] is not a normal file").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! logbackConfigFile.canRead()) {
				String errorMessage = new StringBuilder("the logback config file[")
						.append(logbackConfigFilePathString)
						.append("] does not have read permissions").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		
		{
			File logPath = new File(rootLogPathString);
			
			if (! logPath.exists()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! logPath.isDirectory()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] is not a directory").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! logPath.canWrite()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(rootLogPathString)
						.append("] is marked read-only").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH,
				rootLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);
	}
}
