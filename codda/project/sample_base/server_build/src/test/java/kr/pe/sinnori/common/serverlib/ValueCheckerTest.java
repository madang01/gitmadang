package kr.pe.sinnori.common.serverlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.Charset;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;

public class ValueCheckerTest extends AbstractJunitTest {
	
	@Test
	public void testCheckValidPwdHint_앞에공백() {
		String pwdHint = " 힌트 그것이 알고싶다";
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실 힌트는 앞뒤로 공백없이 한글, 영문, 숫자 조합으로 최소 ")
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
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실 힌트는 앞뒤로 공백없이 한글, 영문, 숫자 조합으로 최소 ")
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
		
		try {
			ValueChecker.checkValidPwdHint(pawdHitStringBuilder.toString());
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호 분실 힌트는 한글, 영문, 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			
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
		
		password = "j1fuev#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.warn("error", e);
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
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
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPwd_badPasswordChar() {
		String password = null;;
		byte[] passwordBytes = null;
		
		password = "j1fue=\riv#";
		passwordBytes = password.getBytes(Charset.forName("utf-8"));
		try {
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
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
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "비밀번호는 영문을 최소 1문자 포함해야 합니다";
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
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "비밀번호는 숫자를 최소 1문자 포함해야 합니다";
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
			ValueChecker.checkValidPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "비밀번호는 문장부호를 최소 1문자 포함해야 합니다";
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
			ValueChecker.checkValidPwd(passwordBytes);
		} catch(IllegalArgumentException e) {
			log.warn("error", e);
			fail(e.getMessage());
		}
	}
}
