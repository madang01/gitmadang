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
		if (! Character.isAlphabetic(firstChar)) {
			String errorMessage = new StringBuilder(title)
			.append(" 아이디[")
					.append(userID)
					.append("]의 첫글자가 영문이 아닙니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=1; i < userIDChars.length; i++) {
			char c = userIDChars[i];
			if (! Character.isDigit(c) && ! Character.isAlphabetic(c)) {
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
		boolean isAlphabet = false;
		boolean isPunct = false;
			
		for (int i=0; i < passwordBytes.length; i++) {
			byte value = passwordBytes[i];
			if (value <= 0) {
				String errorMessage = new StringBuilder()
						.append(title).append(" 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			} 
			
			char c = (char)value;
			
			if (Character.isAlphabetic(c)) {
				isAlphabet = true;
			} else if (Character.isDigit(c)) {
				isDigit = true;
			} else if (CommonStaticUtil.isPunct(c)) {
				isPunct = true;
			} else {
				String errorMessage = new StringBuilder()
						.append(title)
						.append(" 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			// passwdCharBuffer.append((char)value);
		}
		
		if (! isAlphabet) {
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
	
	/*
	 * public static void checkValidMemberWithdrawPwd(byte[] passwordBytes) throws
	 * IllegalArgumentException { checkValidPwd("로그인", passwordBytes); }
	 */
	
	/**
	 * 별명에 대한 입력값 검사를 수행한다.
	 * @param nickname 별명
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidNickname(String nickname) throws IllegalArgumentException {
		if (null == nickname) {
			throw new IllegalArgumentException("the parameter nickname is null");
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
			if (! CommonStaticUtil.isFullHangul(c)  && ! Character.isAlphabetic(c) && ! Character.isDigit(c)) {
				String errorMessage = new StringBuilder("별명은 공백 없이 한글(가-힣), 영문, 숫자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		/*String regexNickname = new StringBuilder("^[a-zA-Z0-9가-힣]{")
				.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS)
				.append(",")
				.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS)
				.append("}$").toString();
		
		boolean isValid = nickname.matches(regexNickname);
		if (!isValid) {			
			String errorMessage = new StringBuilder("별명은 공백 없이 한글, 영문, 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_NICKNAME_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
		
	}
		
	/**
	 * 비밀번호 분실시 힌트에 대한 입력값 검사를 수행한다.
	 * @param pwdHint 비밀번호 분실시 힌트
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdHint(String pwdHint) throws IllegalArgumentException {
		if (null == pwdHint) {
			throw new IllegalArgumentException("the parameter pwdHint is null");
		}
		
		char[] pwdHintChars = pwdHint.toCharArray();
		
		if (pwdHintChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호 분실시 힌트[")
					.append(pwdHint)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (pwdHintChars.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호 분실시 힌트[")
					.append(pwdHint)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : pwdHintChars) {
			if (CommonStaticUtil.isLineSeparator(c)) {				
				String errorMessage = new StringBuilder("비밀번호 분실시 힌트는 한줄로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		if (pwdHintChars.length > 0 && (' ' == pwdHintChars[0] || ' ' == pwdHintChars[pwdHintChars.length -1 ])) {
			String errorMessage = new StringBuilder("비밀번호 분실시 힌트는 앞뒤로 공백없이 한줄로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : pwdHintChars) {
			if (! CommonStaticUtil.isFullHangul(c) && ' ' != c && ! Character.isAlphabetic(c) && ! Character.isDigit(c)) {
				String errorMessage = new StringBuilder("비밀번호 분실시 힌트는 공백 포함 한글(가-힣), 영문, 숫자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
		
	/**
	 * 비빌번호 분실시 답변에 대한 입력값 검사를 수행한다.
	 * @param pwdAnswer 비빌번호 분실시 답변
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdAnswer(String pwdAnswer) throws IllegalArgumentException {
		if (null == pwdAnswer) {
			throw new IllegalArgumentException("파라미터 비밀번호 분실시 답변 값이 null 입니다");
		}
		
		char[] pwdAnswerChars = pwdAnswer.toCharArray();
		
		if (pwdAnswerChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호 분실시 답변[")
					.append(pwdAnswer)
					.append("]의 글자수는 최소 글자수[")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("] 보다 작습니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (pwdAnswerChars.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호 분실시 답변[")
					.append(pwdAnswer)
					.append("]의 글자수는 최대 글자수[")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("] 보다 큽니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (pwdAnswerChars.length > 0 && (' ' == pwdAnswerChars[0] || ' ' == pwdAnswerChars[pwdAnswerChars.length -1 ])) {
			String errorMessage = new StringBuilder("비밀번호 분실시 답변은 앞뒤로 공백없이 한줄로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : pwdAnswerChars) {
			if (! CommonStaticUtil.isFullHangul(c) && ' ' != c && ! Character.isAlphabetic(c) && ! Character.isDigit(c)) {
				String errorMessage = new StringBuilder("비밀번호 분실시 답변은 공백 포함 한글(가-힣), 영문, 숫자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
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
		if (pageSize <= 0) {
			String errorMessage = new StringBuilder("페이지 크기[")
			.append(pageSize).append("]가 0보다 작거나 같습니다").toString();
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
		if (null == subject) {
			throw new IllegalArgumentException("the parameter subject is null");
		}
		
		char[] subjectChars = subject.toCharArray();
		
		if (subjectChars.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_SUBJECT_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시판 제목은 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_SUBJECT_CHARRACTERS)
					.append("글자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (subjectChars.length > 0 && (Character.isWhitespace(subjectChars[0]) || Character.isWhitespace(subjectChars[subjectChars.length-1]))) {
			String errorMessage = "게시판 제목은 앞 혹은 뒤에 공백 문자가 올 수 없습니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : subjectChars) {
			if (CommonStaticUtil.isLineSeparator(c)) {
				throw new IllegalArgumentException("게시판 제목에 개행 문자가 포함되었습니다");
			}
		}	
	}
	
	
	
	
	
	/**
	 * 게시판 내용에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 내용
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidContents(String content) throws IllegalArgumentException {
		if (null == content) {
			throw new IllegalArgumentException("the parameter content is null");
		}
		
		if (content.length() < ServerCommonStaticFinalVars.MIN_NUMBER_OF_CONTENTS_CHARRACTERS) {
			String errorMessage = new StringBuilder("게시판 내용은 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_CONTENTS_CHARRACTERS)
					.append("글자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	/**
	 * IP4 주소에 대한 입력값 검사를 수행한다.
	 * @param ip IP4 주소
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidIP(String ip) throws IllegalArgumentException {
		if (null == ip) {
			throw new IllegalArgumentException("the parameter ip is null");
		}
		
		String regexIPV4 = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		boolean isValid = ip.matches(regexIPV4);
		if (!isValid) {
			
			String regexIPV6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
			isValid = ip.matches(regexIPV6);		
			
			if (! isValid) {
				String errorMessage = new StringBuilder("IP 값[")
						.append(ip).append("]이 IP 주소 포맷이 아닙니다").toString();
						throw new IllegalArgumentException(errorMessage);
			}
		}		
	}
	
	public static void checkValidFileName(String fileName) throws IllegalArgumentException {
		if (null == fileName) {
			throw new IllegalArgumentException("the parameter fileName is null");
		}
		
		char[] fileNameChars = fileName.toCharArray();
				
		for (int i=0; i < fileNameChars.length; i++) {
			char workingChar = fileNameChars[i];
			
			if (CommonStaticUtil.isLineSeparator(workingChar)) {
				String errorMessage = new StringBuilder("첨부 파일명[")
						.append(fileName)
						.append("]에 개행 문자가 존재합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			} else if (Character.isWhitespace(workingChar)) {
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
			} else {
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
			}
		}	
	}
	
	public static void checkValidBoardName(String boardName) throws IllegalArgumentException {
		if (null == boardName) {
			throw new IllegalArgumentException("the parameter boardName is null");
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
		
		if (boardNameChars.length > 0 && (' ' == boardNameChars[0] || ' ' == boardNameChars[boardNameChars.length-1])) {
			String errorMessage = "게시판 이름은 앞 혹은 뒤에 공백 문자가 올 수 없습니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (char c : boardNameChars) {
			if (! CommonStaticUtil.isFullHangul(c) && ' ' != c && ! Character.isAlphabetic(c) && ! Character.isDigit(c)) {
				String errorMessage = new StringBuilder("게시판 이름은 공백 포함 한글(가-힣), 영문, 숫자 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_BOARDNAME_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_BOARDNAME_CHARRACTERS)
						.append("자를 요구합니다").toString();
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
		if (null != boardPasswordHashBase64) {
			try {
				CommonStaticUtil.Base64Decoder.decode(boardPasswordHashBase64);
			} catch(IllegalArgumentException e) {
				String errorMessage = "베이스64(base64)로 인코딩한 게시글 비밀번호 해쉬가 잘못되었습니다";
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
}
