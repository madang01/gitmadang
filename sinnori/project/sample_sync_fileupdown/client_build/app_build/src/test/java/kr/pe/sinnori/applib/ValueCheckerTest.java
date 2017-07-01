package kr.pe.sinnori.applib;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.OS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.util.SearchUtil;

public class ValueCheckerTest {
	final int EXIT_SUCCESS = 0;
	private Logger log = LoggerFactory.getLogger(ValueCheckerTest.class);

	@Test
	public void testCheckValidPwd_parameterisnull_pwdBytes() {

		try {
			ValueChecker.checkValidPwd(null);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals("the paramter pwdBytes is null")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_parameterisbad_pwdBytes_maxOver() {
		try {
			ValueChecker.checkValidPwd("aaaaaaaaaaaaaaaa".toCharArray());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals(
					"the password must be at least 8 characters and up to 15 characters in alphabet, numeric and punctuation")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_parameterisbad_pwdBytes_minLess() {
		try {
			ValueChecker.checkValidPwd("aaaaaaa".toCharArray());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals(
					"the password must be at least 8 characters and up to 15 characters in alphabet, numeric and punctuation")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_parameterisbad_pwdBytes_noAlpha() {
		try {
			ValueChecker.checkValidPwd("111111111".toCharArray());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals("The password must contain at least one alphabetic character")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_parameterisbad_pwdBytes_noDigit() {
		try {
			ValueChecker.checkValidPwd("aaaaaaaa".toCharArray());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals("The password must contain at least one numeric character")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_parameterisbad_pwdBytes_noPunct() {
		try {
			ValueChecker.checkValidPwd("aa1aaaaaa".toCharArray());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);

			String errorMessage = e.getMessage();
			if (!errorMessage.equals("The password must contain at least one punctuation character")) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCheckValidPwd_패스워드메모리덤프를통한검출보안테스트() {
		char[] passwordChars = { 'a', 'a', '1', '#', 'a', 'a', 'a', 'a', 'A' };
		byte[] passwordBytes = new byte[passwordChars.length];

		try {
			ValueChecker.checkValidPwd(passwordChars);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (int i=0; i < passwordChars.length; i++) {
			passwordBytes[i] = (byte)(passwordChars[i]&0xff);
		}		
		
		Arrays.fill(passwordChars, (char)' ');
		Arrays.fill(passwordBytes, (byte)0x00);

		// Random random = new Random(298944);
		
		File memoryDumpFileAfterPasswordDeletion = new File(String.format("memoryDumpFileBeforePasswordDeletion%07d.hprof", 1L));
		if (memoryDumpFileAfterPasswordDeletion.exists()) {
			boolean isSuccess = memoryDumpFileAfterPasswordDeletion.delete();
			if (!isSuccess) {				
				fail(String.format("the file of memoryDumpFileAfterPasswordDeletion[%s] can't be deleted", memoryDumpFileAfterPasswordDeletion.getAbsolutePath()));
			}
		}
		
		String name = ManagementFactory.getRuntimeMXBean().getName();		
		String pidNumber = name.substring(0, name.indexOf("@"));	 // PID 번호 : 윈도/유닉스 공통		
		log.info("pidNumber=[{}]", pidNumber);
		
		org.apache.commons.exec.CommandLine jmapCommandLineAfterPasswordDeletion = null;
		if (OS.isFamilyUnix()) {
			jmapCommandLineAfterPasswordDeletion = new CommandLine("jmap");
		} else if (OS.isFamilyWindows()) {
			jmapCommandLineAfterPasswordDeletion = new CommandLine("jmap.exe");
		} else {
			fail("unknown OS");
		}
		
		jmapCommandLineAfterPasswordDeletion .addArgument(new StringBuilder("-dump:format=b,file=")
				.append(memoryDumpFileAfterPasswordDeletion.getAbsolutePath()).toString());
		jmapCommandLineAfterPasswordDeletion.addArgument(pidNumber);

		Executor jmapExecutor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);

		jmapExecutor.setExitValue(EXIT_SUCCESS);
		jmapExecutor.setWatchdog(watchdog);
		
		try {
			try {
				jmapExecutor.execute(jmapCommandLineAfterPasswordDeletion, resultHandler);
			} catch (ExecuteException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}

			try {
				resultHandler.waitFor();
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}

			int exitCode = resultHandler.getExitValue();

			if (EXIT_SUCCESS != exitCode) {
				fail("ant exist code[" + exitCode + "] is not a expected exit code 1");
			}
			
			char[] dupPasswordChars = { 'a', 'a', '1', '#', 'a', 'a', 'a', 'a', 'A' };
			String password = String.valueOf(dupPasswordChars);
			
			try {
				long returnedIndex =  SearchUtil.findKeywordInFile(memoryDumpFileAfterPasswordDeletion, 
						password.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET), SearchUtil.MIN_BUFFER_SIZE, 10L);
				if (returnedIndex >=0) {
					fail(new StringBuilder("the heap memory dump file[").append(memoryDumpFileAfterPasswordDeletion.getAbsolutePath())
							.append("] includes the string of password[").append(password).append("], inx=").append(returnedIndex).toString());
				}
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		} finally {
			if (null != memoryDumpFileAfterPasswordDeletion && memoryDumpFileAfterPasswordDeletion.exists()) {
				boolean isSuccess = memoryDumpFileAfterPasswordDeletion.delete();
				if (!isSuccess) {
					fail(String.format("fail to delete the heap dump file[%s]", memoryDumpFileAfterPasswordDeletion.getAbsolutePath()));
				}
			}
		}
	}

	/*
	 * Console console = System.console();
	 * 
	 * console.printf("Please enter your username: "); String username =
	 * console.readLine(); console.printf(username + "\n");
	 * 
	 * console.printf("Please enter your password: "); char[] passwordChars =
	 * console.readPassword(); String passwordString = new
	 * String(passwordChars);
	 * 
	 * console.printf(passwordString + "\n");
	 */
}
