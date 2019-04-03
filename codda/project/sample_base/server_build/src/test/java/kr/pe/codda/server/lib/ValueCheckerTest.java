package kr.pe.codda.server.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class ValueCheckerTest extends AbstractJunitTest {
	
	@Test
	public void testCheckValidPwdHint_앞에공백() {
		String pwdHint = " 힌트 그것이 알고싶다";
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실시 힌트는 앞뒤로 공백없이 한줄로 최소 ")
			.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
			.append("자 최대 ")
			.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
			.append("자를 요구합니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwdHint_뒤에공백() {
		String pwdHint = "힌트 그것이 알고싶다 ";
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실시 힌트는 앞뒤로 공백없이 한줄로 최소 ")
			.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
			.append("자 최대 ")
			.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
			.append("자를 요구합니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwdHint_최대글자수초과() {
		StringBuilder pawdHitStringBuilder = new StringBuilder();
		
		for (int i=0; i <= ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS; i++) {
			pawdHitStringBuilder.append("a");
		}
		
		String pwdHint = pawdHitStringBuilder.toString();
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실시 힌트[")
					.append(pwdHint)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwdHint_최소글자수미달() {
		String pwdHint = "a";
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실시 힌트[")
					.append(pwdHint)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwdHint_ok() {
		String pwdHint = "힌트 그것이 알고싶다";
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCheckValidPwd_lessThanMin() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "j1fev#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.warn("error", e);
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("회원가입 비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_greaterThanMax() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "j1fueiv#1j1fueivj1fueivj1fueiv";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("회원가입 비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_비밀번호가저장된바이트배열에서음수값을포함한경우() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "한글iv#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.info("추적용", e);
			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("회원가입 비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_비밀번호가저장된바이트배열에서특수문자가포함된경우() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "ab37!@\r#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.info("추적용", e);
			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("회원가입 비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_noAlpha() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "11111111";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "회원가입 비밀번호는 영문을 최소 1문자 포함해야 합니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_noDigit() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "aaaaaaaa";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "회원가입 비밀번호는 숫자를 최소 1문자 포함해야 합니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_noPunct() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "aaaaaaaa1";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "회원가입 비밀번호는 문장부호를 최소 1문자 포함해야 합니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_ok() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "j1fueiv#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
		} catch(IllegalArgumentException e) {
			log.warn("error", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTrim() {
		String value = "\r\n한글  \r\n ";
		String trimValue = value.trim();
		
		log.info("trimValue=[{}][len={}]", trimValue, trimValue.length());
	}

	
	/*public void test() {
		String str = "string";

		String test1 = "";
		String test2 = "";
		String test3 = "";

		long t1_start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
		   test1 += str;
		}
		long t1_end = System.currentTimeMillis();

		long t2_start = System.currentTimeMillis();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < 100000; i++) {
		   stringBuffer.append(str);
		}
		long t2_end = System.currentTimeMillis();

		long t3_start = System.currentTimeMillis();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
		   stringBuilder.append(str);
		}
		long t3_end = System.currentTimeMillis();


		System.out.println("test case 1 = " + (t1_end - t1_start));
		System.out.println("test case 2 = " + (t2_end - t2_start));
		System.out.println("test case 3 = " + (t3_end - t3_start));
	}*/
	
	
}
