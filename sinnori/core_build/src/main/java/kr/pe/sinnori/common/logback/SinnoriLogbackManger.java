package kr.pe.sinnori.common.logback;

import java.io.File;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
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
	
	public void setup(String sinnoriInstalledPathString, String mainProjectName, LogType logType) throws IllegalStateException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException("the parameter sinnoriInstalledPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		if (null == logType) {
			throw new IllegalArgumentException("the parameter logType is null");
		}
		
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getProjectLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getProjectLogPathString(sinnoriInstalledPathString, mainProjectName, logType);
		
		
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
			File sinnoriLogPath = new File(sinnoriLogPathString);
			
			if (! sinnoriLogPath.exists()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! sinnoriLogPath.isDirectory()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] is not a directory").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! sinnoriLogPath.canWrite()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] is marked read-only").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);
	}
	
	public void setup(String sinnoriInstalledPathString) throws IllegalStateException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException("the parameter sinnoriInstalledPathString is null");
		}
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getProjectLogbackConfigFilePathString(sinnoriInstalledPathString, "sample_base");
		String sinnoriLogPathString = BuildSystemPathSupporter.getSinnoriLogPathString(sinnoriInstalledPathString);
		
		
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
			File sinnoriLogPath = new File(sinnoriLogPathString);
			
			if (! sinnoriLogPath.exists()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] doesn't exist").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			
			if (! sinnoriLogPath.isDirectory()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] is not a directory").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}


			if (! sinnoriLogPath.canWrite()) {
				String errorMessage = new StringBuilder("the log path[")
						.append(logbackConfigFilePathString)
						.append("] is marked read-only").toString();
				System.out.println(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		}
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);
	}
}
