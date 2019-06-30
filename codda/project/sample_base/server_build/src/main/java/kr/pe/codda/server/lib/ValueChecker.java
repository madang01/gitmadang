/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.codda.server.lib;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 항목 검사기, 예를 들면 아이디, 비밀번호 같은 공통 항목에 대한 입력값 검사기
 * @author Won Jonghoon
 *
 */
public class ValueChecker {
	/**
	 * 낱글자 한글 포함한 한글 여부를 반환한다
	 * @param c
	 * @return 낱글자 한글 포함한 한글 여부
	 */
	public static boolean isHangul(final char c) {
		boolean isHangul = false;
		if (c >= 'ㄱ' && c <= 'ㅎ') {
			isHangul = true;
		} else if (c >= 'ㅏ' && c <= 'ㅣ') {
			isHangul = true;
		} else if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	/**
	 * 초성 중성 종성이 다 갖추어진'가' 에서 '힣' 까지의 한글 여부를 반환한다
	 * @param c
	 * @return 초성 중성 종성이 다 갖추어진'가' 에서 '힣' 까지의 한글 여부
	 */
	public static boolean isFullHangul(final char c) {
		boolean isHangul = false;
		if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}
	
	/**
	 * Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
	 * @param c
	 * @return
	 */
	public static boolean isPunct(final char c) {
		boolean isPunct = false;

		if (c >= '!' && c <= '/') {
			isPunct = true;
		} else if (c >= ':' && c <= '@') {
			isPunct = true;
		} else if (c >= '[' && c <= '`') {
			isPunct = true;
		} else if (c >= '{' && c <= '~') {
			isPunct = true;
		}
		return isPunct;
	}
	
	/**
	 * SPACE(0x20) or TAB('\t') 문자 여부를 반환
	 *  
	 * @param c
	 * @return SPACE(0x20) or TAB('\t') 문자 여부
	 */
	public static boolean isSpaceOrTab(char c) {
		boolean isWhiteSpace = false;

		if ((' ' == c) || ('\t' == c)) {
			isWhiteSpace = true;
		}
		
		return isWhiteSpace;
	}

	/**
	 * 개행 문자('\r' or '\n') 여부를 반환 
	 * @param c
	 * @return 개행 문자('\r' or '\n') 여부
	 */
	public static boolean isLineSeparator(char c) {
		boolean isLineSeparator = false;
		//  || ('\u0085' == c) || ('\u2028' == c) || ('\u2029' == c)
		if (('\r' == c) || ('\n' == c)) {
			isLineSeparator = true;
		}

		return isLineSeparator;
	}

	public static boolean isEnglish(final char c) {
		boolean isAlphabet = ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		
		return isAlphabet;
	}
	
	public static boolean isEnglishAndDigit(String sourceString) {
		for (char c : sourceString.toCharArray()) {
			if (! isEnglish(c) && ! Character.isDigit(c)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isEnglishAndDigitWithRegular(String sourceString) {
		String regex = "[a-zA-Z0-9]+";

		boolean isValid = sourceString.matches(regex);
		return isValid;
	}
	
	private static void checkValidUserID(String title, String userID) throws IllegalArgumentException {
		if (null == title) {
			throw new IllegalArgumentException("the parameter title is null");
		}
		
		if (null == userID) {
			String errorMessage = new StringBuilder(title)
					.append(" 아이디를 넣어주세요").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}		
		
		char[] userIDChars = userID.toCharArray();
		
				
		if (userIDChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS) {
			String errorMessage = new StringBuilder(title)
					.append(" 아이디[")
					.append(userID)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (userIDChars.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS) {
			String errorMessage = new StringBuilder(title)
					.append(" 아이디[")
					.append(userID)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		char firstChar = userIDChars[0];
		if (! isEnglish(firstChar)) {
			String errorMessage = new StringBuilder(title)
			.append(" 아이디[")
					.append(userID)
					.append("]의 첫글자가 영문이 아닙니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=1; i < userIDChars.length; i++) {
			char c = userIDChars[i];
			if (! Character.isDigit(c) && ! isEnglish(c)) {
				String errorMessage = new StringBuilder(title)
						.append(" 아이디[")
						.append(userID)
						.append("]의 첫글자 이후 문자는 영문과 숫자 조합이어야 합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
	/**
	 * 사용자 아이디에 대한 입력값 검사를 수행한다.
	 * @param userID 아이디
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidUserID(String userID) throws IllegalArgumentException {
		
		checkValidUserID("사용자", userID);
		
		
		/*String regexId = new StringBuilder("^\\p{Alpha}\\p{Alnum}{")
				.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
				.append(",")
				.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
				.append("}$").toString();
		boolean isValid = userId.matches(regexId);
		if (!isValid) {
			String errorMessage = new StringBuilder("사용자 아이디[")
					.append(userId)
					.append("]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("글자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
	}
	
	/**
	 * 작성자 아이디에 대한 입력값 검사를 수행한다.
	 * @param writerID 작성자 아이디
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidWriterID(String writerID) throws IllegalArgumentException {
		checkValidUserID("작성자", writerID);
		
		
		/*String regexId = new StringBuilder("^\\p{Alpha}\\p{Alnum}{")
				.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
				.append(",")
				.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
				.append("}$").toString();

		boolean isValid = writerId.matches(regexId);
		if (!isValid) {
			String errorMessage = new StringBuilder("작성자 아이디[")
					.append(writerId)
					.append("]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("글자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_USER_ID_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
	}
	
	
	
	public static void checkValidRequestedUserID(String requestedUserID) throws IllegalArgumentException {
		checkValidUserID("요청한 사용자", requestedUserID);
	}
	
	public static void checkValidBlockUserID(String userID) throws IllegalArgumentException {
		checkValidUserID("차단할 사용자", userID);
	}
	
	public static void checkValidUnBlockUserID(String userID) throws IllegalArgumentException {
		checkValidUserID("차단 해제할 사용자", userID);
	}
	
	public static void checkValidActivtyTargetUserID(String userID) throws IllegalArgumentException {		
		checkValidUserID("개인 활동 내역 대상 사용자", userID);
	}
	
	public static void checkValidPersonalInformationTargetUserID(String userID) throws IllegalArgumentException {		
		checkValidUserID("개인 정보 조회 대상 사용자", userID);
	}
		
	
	private static void checkValidPwd(String title, byte[] passwordBytes) throws IllegalArgumentException {
		if (null == title) {
			throw new IllegalArgumentException("the parameter title is null");
		}
		
		if (null == passwordBytes) {
			String errorMessage = new StringBuilder(title)
					.append(" 비밀 번호를 입력해 주세요").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (passwordBytes.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS) {
			String errorMessage = new StringBuilder()
					.append(title)
					.append(" 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (passwordBytes.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS) {
			String errorMessage = new StringBuilder()
					.append(title).append(" 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		// CharBuffer passwdCharBuffer = CharBuffer.allocate(passwordBytes.length);
		
		boolean isDigit = false;
		boolean isEnglish = false;
		boolean isPunct = false;
			
		for (int i=0; i < passwordBytes.length; i++) {
			byte value = passwordBytes[i];
			if (value <= 0) {				
				String errorMessage = new StringBuilder()
						.append(title).append(" 비밀번호가 잘못되었습니다").toString();
				throw new IllegalArgumentException(errorMessage);
			} 
			
			char c = (char)value;
			
			if (isEnglish(c)) {
				isEnglish = true;
			} else if (Character.isDigit(c)) {
				isDigit = true;
			} else if (isPunct(c)) {
				isPunct = true;
			} else {
				String errorMessage = new StringBuilder()
						.append(title)
						.append(" 비밀번호에 영문, 숫자 혹은 특수문자가 아닌 문자가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			// passwdCharBuffer.append((char)value);
		}
		
		if (! isEnglish) {
			String errorMessage = new StringBuilder()
					.append(title)
					.append(" 비밀번호는 영문을 최소 한글자 포함해야 합니다").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! isDigit) {
			String errorMessage = new StringBuilder()
					.append(title)
					.append(" 비밀번호는 숫자를 최소 한글자 포함해야 합니다").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (! isPunct) {
			String errorMessage = new StringBuilder()
					.append(title)
					.append(" 비밀번호는 특수문자를 최소 한글자 포함해야 합니다").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public static void checkValidLoginPwd(byte[] passwordBytes) throws IllegalArgumentException {
		checkValidPwd("로그인", passwordBytes);
	}
	
	public static void checkValidMemberReigsterPwd(byte[] passwordBytes) throws IllegalArgumentException {
		checkValidPwd("회원", passwordBytes);
	}
	
	public static void checkValidOldPwd(byte[] passwordBytes) throws IllegalArgumentException {
		checkValidPwd("변경 전", passwordBytes);
	}
	
	public static void checkValidNewPwd(byte[] passwordBytes) throws IllegalArgumentException {
		checkValidPwd("변경 후", passwordBytes);
	}
	
	public static void checkValidPasswordChangePwd(byte[] passwordBytes) throws IllegalArgumentException {
		checkValidPwd("새로운", passwordBytes);
	}
	
	
	/**
	 * 별명에 대한 입력값 검사를 수행한다.
	 * @param nickname 별명
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidNickname(String nickname) throws IllegalArgumentException {
		if (null == nickname || nickname.isEmpty()) {
			throw new IllegalArgumentException("별명을 입력해 주세요");
		}
		
		char[] nicknameChars = nickname.toCharArray();
		
		if (nicknameChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS) {
			String errorMessage = new StringBuilder("별명[")
					.append(nickname)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (nicknameChars.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS) {
			String errorMessage = new StringBuilder("별명[")
					.append(nickname)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : nicknameChars) {
			
			
			if (isLineSeparator(c)) {
				throw new IllegalArgumentException("별명에 개행 문자가 포함되었습니다");
			} else if (isSpaceOrTab(c)) {
				throw new IllegalArgumentException("별명에 공백이나 탭 문자가 포함되었습니다");
			} else if (Character.isISOControl(c)) {
				throw new IllegalArgumentException("별명에 제어 문자가 포함되었습니다");
			} else if (isPunct(c)) {
				/** xss 공격을 막기 위한 조취 */
				throw new IllegalArgumentException("별명에 특수 문자가 포함되었습니다");
			} else if (! Character.isAlphabetic(c) && ! Character.isDigit(c)) {
				
				System.out.println();
				System.out.printf("%d", Character.getType(c));
				
				throw new IllegalArgumentException("별명에 알 수 없는 문자가 포함되었습니다");
			}
		}
	}
	
	/**
	 * 게시판 식별자 검사, 0 <= 게시판 식별자 <= unsigned byte max(=255)
	 * @param boardID 게시판 식별자
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidBoardID(short boardID) throws IllegalArgumentException {		
		if (boardID < 0) {
			String errorMessage = new StringBuilder("게시판 식별자[")
			.append(boardID).append("]가 0보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder("게시판 식별자[")
					.append(boardID).append("]가 unsigned byte 최대값 255 보다 큽니다").toString();
					throw new IllegalArgumentException(errorMessage);
		}
	}	
	
	/**
	 * 게시판 번호 검사, 1 <= 게시판 번호 <= unsigned integer max(=4294967295)  
	 * @param boardNo 게시판 번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidBoardNo(long boardNo) throws IllegalArgumentException {		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호[")
			.append(boardNo).append("]가 0 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		if (boardNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder("게시판 번호[")
			.append(boardNo).append("]가 unsigned integer 최대값 4294967295 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	/**
	 * 페이지 번호와 페이지 크기를 검사한다. 페이지 번호는 페이지 크기에 따라 최대 값이 정해지기때문에 두개 항목을 동시에 검사한다. 
	 * @param pageNo 페이지 번호, 1 <= 페이지 번호 <= ((Integer.MAX / 페이지 크기) + 1) 
	 * @param pageSize 페이지 크기(=페이지당 최대 게시글 수), 1<= 페이지 크기 <= unsigned short max(=65535) 
	 * @throws IllegalArgumentException
	 */
	public static void checkValidPageNoAndPageSize(int pageNo, int pageSize) throws IllegalArgumentException {		
		if (pageSize < 2) {
			String errorMessage = new StringBuilder("페이지 크기[")
			.append(pageSize).append("]가 2보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		if (pageSize > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = new StringBuilder("페이지 크기[")
					.append(pageSize).append("]가 unsigned short 최대값 65535  보다 큽니다").toString();
					throw new IllegalArgumentException(errorMessage);
		}
		
		if (pageNo < 1) {
			String errorMessage = new StringBuilder("페이지 번호[")
			.append(pageNo).append("]가 1보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		int maxPageNo = (Integer.MAX_VALUE  / pageSize) + 1;
		
		if (pageNo > maxPageNo) {
			String errorMessage = new StringBuilder("페이지 번호[")
					.append(pageNo).append("]가 최대값[")
					.append(maxPageNo)
					.append("] 보다 큽니다").toString();
					throw new IllegalArgumentException(errorMessage);
		}
	}	
	
	/**
	 * 부모 게시판 번호 검사, 0 <= 부모 게시판 번호 <= unsigned integer max(=4294967295)
	 * @param parentBoardNo 부모 게시판 번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidParentBoardNo(long parentBoardNo) throws IllegalArgumentException {		
		if (parentBoardNo < 0) {
			String errorMessage = new StringBuilder("부모 게시판 번호[")
			.append(parentBoardNo).append("]가 0 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (parentBoardNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder("부모 게시판 번호[")
			.append(parentBoardNo).append("]가 unsigned integer 최대값 4294967295 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	/**
	 * 게시판 제목에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 제목
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidSubject(String subject) throws IllegalArgumentException {
		if (null == subject || subject.isEmpty()) {
			throw new IllegalArgumentException("게시글 제목을 입력해 주세요");
		}
		
		char[] subjectChars = subject.toCharArray();
		
		if (subjectChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_SUBJECT_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시글 제목은 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_SUBJECT_CHARRACTERS)
					.append("글자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (subjectChars.length > 0 && (Character.isWhitespace(subjectChars[0]) || Character.isWhitespace(subjectChars[subjectChars.length-1]))) {
			String errorMessage = "게시글 제목은 앞 혹은 뒤로 공백 문자가 올 수 없습니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < subjectChars.length; i++) {
			char c = subjectChars[i];
			
			if (isLineSeparator(c)) {
				throw new IllegalArgumentException("게시글 제목에 개행 문자가 포함되었습니다");
			}
			
			/** SPACE(0x20) 문자와 TAB('\t', 0x09) 문자는 게시판 제목 앞 혹은 뒤에는 허용되지 않지만 중간에는 허용 */			
			if (!isSpaceOrTab(c)  && !isPunct(c) && !Character.isLetterOrDigit(c)) {
				/*
				byte t1 = Character.COMBINING_SPACING_MARK;
				byte t2 = Character.CONNECTOR_PUNCTUATION;
				byte t3  = Character.CONTROL;
				, CURRENCY_SYMBOL, DASH_PUNCTUATION, DECIMAL_DIGIT_NUMBER, ENCLOSING_MARK, END_PUNCTUATION, FINAL_QUOTE_PUNCTUATION, FORMAT, INITIAL_QUOTE_PUNCTUATION, LETTER_NUMBER, LINE_SEPARATOR, LOWERCASE_LETTER, MATH_SYMBOL, MODIFIER_LETTER, MODIFIER_SYMBOL, NON_SPACING_MARK, OTHER_LETTER, OTHER_NUMBER, OTHER_PUNCTUATION, OTHER_SYMBOL, PARAGRAPH_SEPARATOR, PRIVATE_USE, SPACE_SEPARATOR, START_PUNCTUATION, SURROGATE, TITLECASE_LETTER, UNASSIGNED, UPPERCASE_LETTER
				*/
				
				int codePoint = Character.codePointAt(subjectChars, i);
				
				String errorMessage = new StringBuilder()
						.append("게시글 제목에 허용되지 않는 문자[inx=")
						.append(i)
						.append(", type=")
						.append(Character.getType(c))
						.append(", code point=")
						.append(codePoint)
						.append(", name=")
						.append(Character.getName(codePoint))
						.append("]가 존재합니다, 게시글 제목은 문자, 숫자, 특수문자 그리고 공백문자만 허용됩니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}	
	}
	
	/**
	 * 게시판 내용에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 내용
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidContents(String contents) throws IllegalArgumentException {
		if (null == contents || contents.isEmpty()) {
			throw new IllegalArgumentException("게시글 내용을 입력해 주세요");
		}
		
		if (contents.length() < ServerCommonStaticFinalVars.MIN_NUMBER_OF_CONTENTS_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시판 내용은 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_CONTENTS_CHARRACTERS)
					.append("글자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		char[] contentsChars = contents.toCharArray();
		
		for (int i=0; i < contentsChars.length; i++) {
			char c = contentsChars[i];
						
			if (!isLineSeparator(c) && !isSpaceOrTab(c)  && !isPunct(c) && !Character.isLetterOrDigit(c)) {
				int codePoint = Character.codePointAt(contentsChars, i);
				
				String errorMessage = new StringBuilder()
						.append("게시글 내용에 허용되지 않는 문자[inx=")
						.append(i)
						.append(", type=")
						.append(Character.getType(c))
						.append(", code point=")
						.append(codePoint)
						.append(", name=")
						.append(Character.getName(codePoint))
						.append("]가 존재합니다, 게시글 내용은 문자, 숫자, 특수문자, 공백문자 그리고 개행문자만 허용됩니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}	
	}
	
	/**
	 * IP4 주소에 대한 입력값 검사를 수행한다.
	 * @param ip IP4 주소
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidIP(String ip) throws IllegalArgumentException {
		if (null == ip || ip.isEmpty()) {
			throw new IllegalArgumentException("아이피 주소를 입력해 주세요");
		}
		
		String regexIPV4 = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		boolean isValid = ip.matches(regexIPV4);
		if (!isValid) {
			
			String regexIPV6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
			isValid = ip.matches(regexIPV6);		
			
			if (! isValid) {
				String errorMessage = new StringBuilder("아이피 주소[")
						.append(ip).append("] 형태가 잘못되었습니다").toString();
						throw new IllegalArgumentException(errorMessage);
			}
		}		
	}
	
	public static void checkValidFileName(String fileName) throws IllegalArgumentException {
		if (null == fileName || fileName.isEmpty()) {
			throw new IllegalArgumentException("첨부 파일 이름을 넣어주세요");
		}
		
		char[] fileNameChars = fileName.toCharArray();
				
		for (int i=0; i < fileNameChars.length; i++) {
			char workingChar = fileNameChars[i];
			
			if (isLineSeparator(workingChar)) {
				String errorMessage = new StringBuilder("첨부 파일명[")
						.append(fileName)
						.append("]에 개행 문자가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			} else if (isSpaceOrTab(workingChar)) {
				String errorMessage = new StringBuilder("첨부 파일명[")
						.append(fileName)
						.append("]에 공백 문자가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);			
			} else if (workingChar == '.') {
				if (i > 0 && fileNameChars[i-1] == '.') {
					String errorMessage = new StringBuilder("첨부 파일명[")
					.append(fileName)
					.append("]에 금지된 문자열[..]이 존재합니다").toString();
					throw new IllegalArgumentException(errorMessage);
				}
			} else if (isPunct(workingChar) || Character.isLetterOrDigit(workingChar)) {
				for (char forbiddenChar : ServerCommonStaticFinalVars.FILENAME_FORBIDDEN_CHARS) {
					if (workingChar == forbiddenChar) {
						String errorMessage = new StringBuilder("첨부 파일명[")
								.append(fileName)
								.append("]에 금지된 문자[")
								.append(forbiddenChar)
								.append("]가 존재합니다").toString();
						throw new IllegalArgumentException(errorMessage);
					} 
				}
			} else {
				int codePoint = Character.codePointAt(fileNameChars, i);
				
				String errorMessage = new StringBuilder()
						.append("첨부 파일명에 허용되지 않은 문자[inx=")
						.append(i)
						.append(", type=")
						.append(Character.getType(workingChar))
						.append(", code point=")
						.append(codePoint)
						.append(", name=")
						.append(Character.getName(codePoint))
						.append("]가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);
				
			}
		}	
	}
	
	public static void checkValidBoardName(String boardName) throws IllegalArgumentException {
		if (null == boardName || boardName.isEmpty()) {
			throw new IllegalArgumentException("게시판 이름을 넣어주세요");
		}
		
		char[] boardNameChars = boardName.toCharArray();
		
		if (boardNameChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_BOARDNAME_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시판 이름[")
					.append(boardName)
					.append("]의 글자수가 최소 글자 수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_BOARDNAME_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
		
		if (boardNameChars.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_BOARDNAME_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시판 이름[")
					.append(boardName)
					.append("]의 글자 수[")
					.append(boardNameChars.length)
					.append("]가 최대 글자 수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_BOARDNAME_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (boardNameChars.length > 0 && (Character.isWhitespace(boardNameChars[0]) || Character.isWhitespace(boardNameChars[boardNameChars.length-1]))) {
			String errorMessage = "게시판 이름은 앞 혹은 뒤로 공백 문자가 올 수 없습니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		for (int i=0; i < boardNameChars.length; i++) {
			char c = boardNameChars[i];
			
			if (!isSpaceOrTab(c) && !Character.isLetterOrDigit(c)) {	
				
				int codePoint = Character.codePointAt(boardNameChars, i);
				
				String errorMessage = new StringBuilder()
						.append("게시판 이름에 허용되지 않은 문자[inx=")
						.append(i)
						.append(", type=")
						.append(Character.getType(c))
						.append(", code point=")
						.append(codePoint)
						.append(", name=")
						.append(Character.getName(codePoint))
						.append("]가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
	
	/**
	 * WARNING!) 파라미터 null 체크 없음
	 *  
	 * @param boardPasswordHashBase64
	 * @throws IllegalArgumentException
	 */
	public static void checkValidBoardPasswordHashBase64(String boardPasswordHashBase64) throws IllegalArgumentException {
		if (null != boardPasswordHashBase64 && ! boardPasswordHashBase64.isEmpty()) {
			try {
				CommonStaticUtil.Base64Decoder.decode(boardPasswordHashBase64);
			} catch(IllegalArgumentException e) {
				String errorMessage = "베이스64(base64)로 인코딩한 게시글 비밀번호 해쉬가 잘못되었습니다";
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
	
	public static void checkValidEmail(String email) throws IllegalArgumentException {
		if (null == email || email.isEmpty()) {
			throw new IllegalArgumentException("이메일 주소를 입력해 주세요");
		}
		
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		
		if (! email.matches(regex)) {
			String errorMessage = "이메일 주소 형태가 잘못되었습니다";
			throw new IllegalArgumentException(errorMessage);
		} 
	}
	
	public static void checkValidSecretAuthenticationValue(String secretAuthenticationValue) throws IllegalArgumentException {
		if (null == secretAuthenticationValue || secretAuthenticationValue.isEmpty()) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀 값을 입력해 주세요";
			throw new IllegalArgumentException(errorMessage);
		}
		
		try {
			CommonStaticUtil.Base64Decoder.decode(secretAuthenticationValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀 값이 베이스64가 아닙니다";
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public static void checkValidAttachedFilCount(int newAttachedFileCount) throws IllegalArgumentException {
		if (newAttachedFileCount > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("신규 첨부 파일 등록 갯수[")
					.append(newAttachedFileCount).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (newAttachedFileCount > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder().append("신규 첨부 파일 등록 갯수[")
					.append(newAttachedFileCount).append("]가 총 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("]를 초과하였습니다")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	public static void checkValidAttachedFilCount(int oldAttachedFileCount, int newAttachedFileCount) throws IllegalArgumentException {
		if (oldAttachedFileCount > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("기존 첨부 파일 등록 갯수[")
					.append(newAttachedFileCount).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (newAttachedFileCount > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("신규 첨부 파일 등록 갯수[")
					.append(newAttachedFileCount).append("]가 unsgiend byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX).append("]을 초과하였습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if ((oldAttachedFileCount + newAttachedFileCount) > ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
			String errorMessage = new StringBuilder().append("기존 첨부 파일 등록 갯수[")
					.append(oldAttachedFileCount)
					.append("]와 신규 첨부 파일 등록 갯수[")
					.append(newAttachedFileCount).append("]의 합이 총 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("]를 초과하였습니다")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
