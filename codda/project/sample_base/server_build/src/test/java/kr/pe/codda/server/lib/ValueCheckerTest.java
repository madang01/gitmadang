package kr.pe.codda.server.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ValueCheckerTest extends AbstractJunitTest {
	
	@Test
	public void test_isLetter() {
		log.info("초성 isLetter={}", Character.isLetter('ㄱ'));
		log.info("중성 isLetter={}", Character.isLetter('ㅏ'));
		log.info("한글 isLetter={}", Character.isLetter('가'));
	}
	
	@Test
	public void testCheckValidUserID_isNull() {
		try {
			ValueChecker.checkValidUserID(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "사용자 아이디를 넣어주세요";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidUserID_lessThanMin() {
		String userID = "tst";
		try {
			ValueChecker.checkValidUserID(userID);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("사용자")
					.append(" 아이디[")
					.append(userID)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidUserID_greaterThanMax() {
		String userID = "1234567890123456";
		try {
			ValueChecker.checkValidUserID(userID);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("사용자")
					.append(" 아이디[")
					.append(userID)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidUserID_firstCharacterIsNotAlphabet() {
		String userID = "1tes";
		try {
			ValueChecker.checkValidUserID(userID);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("사용자")
			.append(" 아이디[")
					.append(userID)
					.append("]의 첫글자가 영문이 아닙니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidUserID_middleCharacterIsNotAlphabetOrNumber() {
		String userID = "at?s";
		try {
			ValueChecker.checkValidUserID(userID);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("사용자")
					.append(" 아이디[")
					.append(userID)
					.append("]의 첫글자 이후 문자는 영문과 숫자 조합이어야 합니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidUserID_ok() {
		String[] userIDs = {"ates", "a1te", "a1te4a1te", "a1te4a1te4a1te4"};
		
		for (String userID : userIDs) {
			try {
				ValueChecker.checkValidUserID(userID);
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testCheckValidPwd_isNull() {
		byte[] passwordBytes = null;
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.warn("error", e);
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("회원")
					.append(" 비밀 번호를 입력해 주세요").toString();
			assertEquals(expectedErrorMessage, errorMessage);
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
			String expectedErrorMessage =  new StringBuilder("회원 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
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
			String expectedErrorMessage =  new StringBuilder("회원 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
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
		passwordBytes[passwordBytes.length/2] = -2;
		try {
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// log.info("추적용", e);
			
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder()
					.append("회원").append(" 비밀번호가 잘못되었습니다").toString();
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
			String expectedErrorMessage =  new StringBuilder()
					.append("회원")
					.append(" 비밀번호에 영문, 숫자 혹은 특수문자가 아닌 문자가 존재합니다").toString();
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
			String expectedErrorMessage =  "회원 비밀번호는 영문을 최소 한글자 포함해야 합니다";
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
			String expectedErrorMessage =  "회원 비밀번호는 숫자를 최소 한글자 포함해야 합니다";
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
			String expectedErrorMessage =  "회원 비밀번호는 특수문자를 최소 한글자 포함해야 합니다";
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
	public void testCheckValidNickname_isNull() {
		try {
			ValueChecker.checkValidNickname(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  "별명을 입력해 주세요";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_lessThanMin() {
		String nickname = "t";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage =  new StringBuilder("별명[")
					.append(nickname)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_greaterThanMax() {
		String nickname = "12345678901234567890a";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("별명[")
					.append(nickname)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_includingLineSeparactor() {
		String nickname = "오구오\r구";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "별명에 개행 문자가 포함되었습니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	
	@Test
	public void testCheckValidNickname_includingSpace() {
		String nickname = "오구오 구";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "별명에 공백이나 탭 문자가 포함되었습니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_includingTab() {
		String nickname = "오구오\t구";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "별명에 공백이나 탭 문자가 포함되었습니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_includingISOControl() {
		String nickname = "오구오\u007f구";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "별명에 제어 문자가 포함되었습니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidNickname_includingPunctuation() {
		String nickname = "오구오<구";
		try {
			ValueChecker.checkValidNickname(nickname);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "별명에 특수 문자가 포함되었습니다";
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPageNoAndPageSize_theParameterPageSizeIsLessThanTwo() {
		int pageNo=0, pageSize=1;
		try {
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("페이지 크기[")
					.append(pageSize).append("]가 2보다 작습니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPageNoAndPageSize_theParameterPageSizeIsGreaterThanMax() {
		int pageNo=0, pageSize=(CommonStaticFinalVars.UNSIGNED_SHORT_MAX+1);
		try {
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("페이지 크기[")
					.append(pageSize).append("]가 unsigned short 최대값 65535  보다 큽니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPageNoAndPageSize_theParameterPageNoIsLessThanOne() {
		int pageNo=0, pageSize=20;
		try {
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("페이지 번호[")
					.append(pageNo).append("]가 1보다 작습니다").toString();
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPageNoAndPageSize_theParameterPageNoIsGreaterThanMax() {
		int pageSize=20;
		long maxPageNo = (Integer.MAX_VALUE  / pageSize) + 1;
		int pageNo = (int)maxPageNo + 1;
		try {
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("페이지 번호[")
					.append(pageNo).append("]가 최대값[")
					.append(maxPageNo)
					.append("] 보다 큽니다").toString();
			
			log.info(errorMessage);
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidPageNoAndPageSize_ok() {
		int pageSize=2;
		int maxPageNo = (Integer.MAX_VALUE  / pageSize) + 1;
		int pageNo = maxPageNo;
		try {
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			fail(errorMessage);
		}
	}
	
	@Test
	public void testCeckValidSubject_isNull() {
		String subject = null;
		try {
			ValueChecker.checkValidSubject(subject);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "게시글 제목을 입력해 주세요";	
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	
	@Test
	public void testCeckValidSubject_lessThanMin() {
		String subject = "한";
		try {
			ValueChecker.checkValidSubject(subject);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("게시글 제목은 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_SUBJECT_CHARRACTERS)
					.append("글자를 요구합니다").toString();	
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidSubject_앞뒤로공백문자() {
		String subject = "한글\r ";
		try {
			ValueChecker.checkValidSubject(subject);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "게시글 제목은 앞 혹은 뒤로 공백 문자가 올 수 없습니다";
			
			assertEquals(expectedErrorMessage, errorMessage);
		}
	}
	
	@Test
	public void testCheckValidSubject_비허용문자() {
		String subject = "한글!사랑$12나라사랑 그림 \u007f하나를 그리다";
		try {
			ValueChecker.checkValidSubject(subject);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.info(errorMessage);
			
			if (errorMessage.indexOf("게시글 제목에 허용되지 않는 문자") != 0) {
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void test() {
		SecureRandom rand3 = null;
		try {
		    rand3 = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		    fail("errmsg="+e.getMessage());
		}

		long auth_value = rand3.nextLong();
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		buffer.putLong(auth_value);
		buffer.flip();
		String tt = CommonStaticUtil.Base64Encoder.encodeToString(buffer.array());
		
		log.info("[{}]'s len={}", tt, tt.length());
	}
	
	@Test
	public void testTrim() {
		String value = "\r\n한글  \r\n ";
		String trimValue = value.trim();
		
		log.info("trimValue=[{}][len={}]", trimValue, trimValue.length());
	}	
	
	@Test
	public void testCheckValidNickname_한글() {
		String nickname = "한글사랑1aAHkzZ";
		try {
			ValueChecker.checkValidNickname(nickname);
		} catch (IllegalArgumentException e) {
		    fail("errmsg="+e.getMessage());
		}
		
	}
}
