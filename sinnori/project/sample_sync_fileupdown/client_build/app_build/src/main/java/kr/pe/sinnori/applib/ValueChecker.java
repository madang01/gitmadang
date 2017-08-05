package kr.pe.sinnori.applib;

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValueChecker {
	/**
	 * 아이디에 대한 입력값 검사를 수행한다.
	 * @param id 아이디
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidUserId(String userId) throws IllegalArgumentException {
		if (null == userId) {
			throw new IllegalArgumentException("파라미터 아이디 값이 null 입니다.");
		}
		String regexId = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		boolean isValid = userId.matches(regexId);
		if (!isValid) {
			throw new IllegalArgumentException(String.format("아이디[%s]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 4글자 최대 15자를 요구합니다.", userId));
		}
	}
	
	/**
	 * 작성자 아이디에 대한 입력값 검사를 수행한다.
	 * @param writerId 작성자 아이디
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidWriterId(String writerId) throws IllegalArgumentException {
		if (null == writerId) {
			throw new IllegalArgumentException("파라미터 작성자 아이디 값이 null 입니다.");
		}
		String regexId = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		boolean isValid = writerId.matches(regexId);
		if (!isValid) {
			throw new IllegalArgumentException(String.format("작성자 아이디[%s]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 4글자 최대 15자를 요구합니다.", writerId));
		}
	}
	
	/**
	 * 비밀번호에 대한 입력값 검사를 수행한다.
	 * @param passwordChars 비밀번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwd(char[] passwordChars) throws IllegalArgumentException {
		if (null == passwordChars) {
			throw new IllegalArgumentException("the paramter passwordChars is null");
		}
		
		CharBuffer pwdCharBuffer = CharBuffer.wrap(passwordChars);
		
		/**
		 * \p{Graph} = [\p{Alpha}|\p{Digit}{1,}|\p{Punct}]
		 */
		String regexPwd = "^\\p{Graph}{8,15}$";
		// CharSequence
		boolean isValid = Pattern.compile(regexPwd).matcher(pwdCharBuffer).matches();
		if (!isValid) {
			throw new IllegalArgumentException("the password must be at least 8 characters and up to 15 characters in alphabet, numeric and punctuation");
		}        
		
		String regexPwdAlpha = ".*\\p{Alpha}{1,}.*";
		isValid = Pattern.compile(regexPwdAlpha).matcher(pwdCharBuffer).matches();
		if (!isValid) {
			throw new IllegalArgumentException("The password must contain at least one alphabetic character");
		}
		
		String regexPwdDigit = ".*\\p{Digit}{1,}.*";
		isValid = Pattern.compile(regexPwdDigit).matcher(pwdCharBuffer).matches();
		if (!isValid) {
			throw new IllegalArgumentException("The password must contain at least one numeric character");
		}		
				
		/**
		 * \p{Punct} : !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
		 */
		String regexPwdPunct = ".*\\p{Punct}{1,}.*";
		isValid = Pattern.compile(regexPwdPunct).matcher(pwdCharBuffer).matches();
		if (!isValid) {
			throw new IllegalArgumentException("The password must contain at least one punctuation character");
		}
		
		
	}
	
	/**
	 * 별명에 대한 입력값 검사를 수행한다.
	 * @param nickname 별명
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidNickname(String nickname) throws IllegalArgumentException {
		if (null == nickname) {
			throw new IllegalArgumentException("파라미터 별명 값이 null 입니다.");
		}
		
		String regexNickname = "^[a-zA-Z0-9가-힣]{4,20}$";
		boolean isValid = nickname.matches(regexNickname);
		if (!isValid) {
			throw new IllegalArgumentException("별명은 한글, 영문, 숫자 조합으로 최소 4자 최대 20자를 요구합니다.");
		}
		
	}
		
	/**
	 * 비밀번호 분실시 힌트에 대한 입력값 검사를 수행한다.
	 * @param pwdHint 비밀번호 분실시 힌트
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdHint(String pwdHint) throws IllegalArgumentException {
		if (null == pwdHint) {
			throw new IllegalArgumentException("파라미터 비밀번호 분실시 힌트 값이 null 입니다.");
		}
		
		if (pwdHint.length() < 2) {
			throw new IllegalArgumentException("비밀번호 분실시 힌트 값은 최소 2글자를 요구합니다.");
		}
		
	}
		
	/**
	 * 비빌번호 분실시 답변에 대한 입력값 검사를 수행한다.
	 * @param pwdAnswer 비빌번호 분실시 답변
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdAnswer(String pwdAnswer) throws IllegalArgumentException {
		if (null == pwdAnswer) {
			throw new IllegalArgumentException("파라미터 비밀번호 분실시 답변 값이 null 입니다.");
		}
		
		if (pwdAnswer.length() < 2) {
			throw new IllegalArgumentException("비밀번호 분실시 답변 값은 최소 2글자를 요구합니다.");
		}
	}

	/**
	 * 게시판 식별자 검사
	 * @param boardId 게시판 식별자
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidBoardId(long boardId) throws IllegalArgumentException {		
		if (boardId <= 0) {
			String errorMessage = new StringBuilder("게시판 식별자 번호 값[")
			.append(boardId).append("]은 0 보다 커야합니다.").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
	}	
	
	/**
	 * 게시판 번호 검사
	 * @param boardNo 게시판 번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidBoardNo(long boardNo) throws IllegalArgumentException {		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[")
			.append(boardNo).append("]은 0 보다 커야합니다.").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
	}
	
	
	/**
	 * 부모 게시판 번호 검사
	 * @param parentBoardNo 부모 게시판 번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidParentBoardNo(long parentBoardNo) throws IllegalArgumentException {		
		if (parentBoardNo <= 0) {
			String errorMessage = new StringBuilder("부모 게시판 번호 값[")
			.append(parentBoardNo).append("]은 0 보다 커야합니다.").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
	}
	
	/**
	 * 게시판 제목에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 제목
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidSubject(String subject) throws IllegalArgumentException {
		if (null == subject) {
			throw new IllegalArgumentException("파라미터 게시판 제목 값이 null 입니다.");
		}
		
		if (subject.length() < 2) {
			throw new IllegalArgumentException("게시판 제목 값은 최소 2글자를 요구합니다.");
		}

		Pattern r = Pattern.compile("\r\n|\n|\r|\u0085|\u2028|\u2029");
		
		Matcher match = r.matcher(subject);
		
		if (match.find()) {
			throw new IllegalArgumentException("게시판 제목에는 개행 문자를 넣을 수 없습니다.");
		}
	}
	
	/**
	 * 게시판 내용에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 내용
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidContent(String content) throws IllegalArgumentException {
		if (null == content) {
			throw new IllegalArgumentException("파라미터 게시판 내용 값이 null 입니다.");
		}
		
		if (content.length() < 2) {
			throw new IllegalArgumentException("게시판 내용 값은 최소 2글자를 요구합니다.");
		}
	}
	
	/**
	 * IP 주소에 대한 입력값 검사를 수행한다.
	 * @param ip IP 주소
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidIP(String ip) throws IllegalArgumentException {
		if (null == ip) {
			throw new IllegalArgumentException("파라미터 게시판 내용 값이 null 입니다.");
		}
		
		String regexIP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		boolean isValid = ip.matches(regexIP);
		if (!isValid) {
			String errorMessage = new StringBuilder("IP 값[")
			.append(ip).append("]이 IP 주소 포맷 xxx.xxx.xxx.xx 가 아닙니다.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
