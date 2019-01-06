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

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import io.netty.util.internal.logging.InternalLogger;

import java.sql.Connection;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;

import org.jooq.DSLContext;
import org.jooq.Record2;

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
			throw new IllegalArgumentException("the parameter userID is null");
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
	
	/**
	 * 비밀번호에 대한 입력값 검사를 수행한다.
	 * @param password 비밀번호
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwd(byte[] passwordBytes) throws IllegalArgumentException {
		if (null == passwordBytes) {
			throw new IllegalArgumentException("the parameter passwordBytes is null");
		}
		
		if (passwordBytes.length < ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (passwordBytes.length > ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS) {
			String errorMessage = new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
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
				String errorMessage = new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
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
				String errorMessage = new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			// passwdCharBuffer.append((char)value);
		}
		
		if (! isAlphabet) {
			throw new IllegalArgumentException("비밀번호는 영문을 최소 1문자 포함해야 합니다");
		}
		
		if (! isDigit) {
			throw new IllegalArgumentException("비밀번호는 숫자를 최소 1문자 포함해야 합니다");
		}
		
		
		if (! isPunct) {
			throw new IllegalArgumentException("비밀번호는 문장부호를 최소 1문자 포함해야 합니다");
		}
		
		/*Pattern p = null;
		Matcher m = null;		
		
		String regexPwd = "^\\p{Graph}{"+ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS+","
		 + ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS+"}$";
		
		p = Pattern.compile(regexPwd);
		passwdCharBuffer.clear();
		m = p.matcher(passwdCharBuffer);		
		boolean isValid = m.matches();
		if (!isValid) {
			String errorMessage = new StringBuilder("비밀번호는 영문, 숫자 그리고 문장 부호 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		String regexPwdAlpha = ".*\\p{Alpha}{1,}.*";
		p = Pattern.compile(regexPwdAlpha);
		passwdCharBuffer.clear();
		m = p.matcher(passwdCharBuffer);		
		isValid = m.matches();
		if (!isValid) {
			throw new IllegalArgumentException("비밀번호는 영문을 최소 1문자 포함해야 합니다");
		}
		
		
		String regexPwdDigit = ".*\\p{Digit}{1,}.*";
		p = Pattern.compile(regexPwdDigit);
		passwdCharBuffer.clear();
		m = p.matcher(passwdCharBuffer);		
		isValid = m.matches();
		if (!isValid) {
			throw new IllegalArgumentException("비밀번호는 숫자를 최소 1문자 포함해야 합니다");
		}		
				
		*//**
		 * \p{Punct} : !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
		 *//*
		String regexPwdPunct = ".*\\p{Punct}{1,}.*";
		p = Pattern.compile(regexPwdPunct);
		passwdCharBuffer.clear();
		m = p.matcher(passwdCharBuffer);		
		isValid = m.matches();
		if (!isValid) {
			throw new IllegalArgumentException("비밀번호는 문장부호를 최소 1문자 포함해야 합니다");
		}		*/
	}
	
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
		
		if (pwdHintChars.length > 0 && (pwdHintChars[0] == ' ' || pwdHintChars[pwdHintChars.length -1 ] == ' ')) {
			String errorMessage = new StringBuilder("비밀번호 분실시 힌트는 앞뒤로 공백없이 한줄로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		// System.out.println("pwdHint=["+pwdHint+"]");
		
		/*String regexMinMax = new StringBuilder("^[a-zA-Z0-9가-힣\\s]{")
				.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
				.append(",")
				.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
				.append("}$").toString();
		
		// System.out.println("regexPwdHint=["+regexPwdHint+"]");
		
		boolean isValid = pwdHint.matches(regexMinMax);
		if (!isValid) {			
			String errorMessage = new StringBuilder("비밀번호 분실 힌트는 한글, 영문, 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String regexTrim = new StringBuilder("^[a-zA-Z0-9가-힣]{1}[a-zA-Z0-9가-힣\\s]*[a-zA-Z0-9가-힣]{1}$").toString();
		isValid = pwdHint.matches(regexTrim);
		if (!isValid) {			
			String errorMessage = new StringBuilder("비밀번호 분실 힌트는 앞뒤로 공백없이 한글, 영문, 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
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
		
		/*String regexMinMax = new StringBuilder("^[a-zA-Z0-9가-힣\\s]{")
				.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
				.append(",")
				.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
				.append("}$").toString();
				
		boolean isValid = pwdAnswer.matches(regexMinMax);
		if (!isValid) {			
			String errorMessage = new StringBuilder("비밀번호 분실 답변은 한글, 영문, 숫자, 공백문자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String regexTrim = new StringBuilder("^[a-zA-Z0-9가-힣]{1}[a-zA-Z0-9가-힣\\s]*[a-zA-Z0-9가-힣]{1}$").toString();
		isValid = pwdAnswer.matches(regexTrim);
		if (!isValid) {			
			String errorMessage = new StringBuilder("비밀번호 분실 답변은 앞뒤로 공백없이 한글, 영문, 숫자 조합으로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
		
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
		
		for (char c : pwdAnswerChars) {
			if (CommonStaticUtil.isLineSeparator(c)) {
				String errorMessage = new StringBuilder("비밀번호 분실시 답변은 한줄으며 최소 ")
						.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
						.append("자 최대 ")
						.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
						.append("자를 요구합니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		if (pwdAnswerChars.length > 0 && (pwdAnswerChars[0] == ' ' || pwdAnswerChars[pwdAnswerChars.length -1 ] == ' ')) {
			String errorMessage = new StringBuilder("비밀번호 분실시 답변의 앞뒤로 공백없이 한줄로 최소 ")
					.append(ServerCommonStaticFinalVars.MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("자 최대 ")
					.append(ServerCommonStaticFinalVars.MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS)
					.append("자를 요구합니다").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * 게시판 식별자 검사
	 * @param boardID 게시판 식별자
	 * @throws IllegalArgumentException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidBoardID(long boardID) throws IllegalArgumentException {		
		if (boardID < 0) {
			String errorMessage = new StringBuilder("게시판 식별자 번호 값[")
			.append(boardID).append("]은 0 보다 크거나 같아야 합니다").toString();
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
			.append(boardNo).append("]은 0 보다 커야합니다").toString();
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
			.append(parentBoardNo).append("]은 0 보다 커야합니다").toString();
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
		
		

		/*Pattern r = Pattern.compile("\r\n|\n|\r|\u0085|\u2028|\u2029");
		
		Matcher match = r.matcher(subject);
		
		if (match.find()) {
			throw new IllegalArgumentException("게시판 제목에는 개행 문자를 넣을 수 없습니다");
		}*/
		
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
	public static void checkValidContent(String content) throws IllegalArgumentException {
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
		
		/*if (fileNameChars.length > 0 && (Character.isWhitespace(fileNameChars[0]) || Character.isWhitespace(fileNameChars[fileNameChars.length-1]))) {
			String errorMessage = new StringBuilder().append("this string is a string with leading and trailing whitespace").toString();
			throw new IllegalArgumentException(errorMessage);
		}	*/
		
		// char prevChar = ' ';
		for (int i=0; i < fileNameChars.length; i++) {
			char workingChar = fileNameChars[i];
			
			if (Character.isWhitespace(workingChar)) {
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
	
	
	
	/*public static void checkNoTrimString(String str) throws IllegalArgumentException {
		if (null == str) {
			throw new IllegalArgumentException("the parameter str is null");
		}
				
		boolean isValid = false;
		
		String regexPrefixSpace = "^\\s.*$"; 
		isValid = str.matches(regexPrefixSpace);
		if (!isValid) {	
			String errorMessage = "공백으로 시작하는 문자열입니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		String regexSuffixSpace = "^.*\\s$"; 
		isValid = str.matches(regexSuffixSpace);
		if (!isValid) {	
			String errorMessage = "공백으로 끝나는 문자열입니다";
			throw new IllegalArgumentException(errorMessage);
		}
		
		char[] strChars = str.toCharArray();
		
		if (strChars.length > 0 && (Character.isWhitespace(strChars[0]) || Character.isWhitespace(strChars[strChars.length-1]))) {
			String errorMessage = new StringBuilder().append("the parameter str[")
					.append(str)
					.append("] is a string with leading and trailing whitespace").toString();
			throw new IllegalArgumentException(errorMessage);
		}		
	}*/
	
	/**
	 * <pre>
	 * 요청한 사용자가 정상인지 검사를 수행하여 비정상시 예외를 던지고 '회원 역활'을 반환한다
	 *  
	 * WARNING! 파라미터 null 검사를 수행하지 않습니다 
	 * 또한 아이디 'guest' 는 예약된 가상 아이디로써 회원 테이블에 존재 하지 않지만 
	 * 손님을 뜻하는 아이디이다.  
	 * </pre>
	 * 
	 * @param conn
	 * @param create
	 * @param log
	 * @param requestedUserID 사용자 아이디
	 * @throws ServerServiceException 비정상인 경우 던지는 예외, (1) 회원 테이블 미 존재 (2) 알 수 없는 회원 상태 값 (3) 회원 상태가 차단이나 탈퇴 즉 비정상일때
	 * @return  회원 역활
	 */
	public static String checkValidRequestedUserState(Connection conn, DSLContext create, InternalLogger log, String requestedUserID) throws ServerServiceException {
		if (requestedUserID.equals("guest")) {
			return MemberRoleType.USER.getValue();
		}
		
		Record2<String, String> memberRecord = create.select(SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE)
				.from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(requestedUserID))
				.fetchOne();
		
		if (null == memberRecord) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
			
			String errorMessage = new StringBuilder("요청한 사용자[")
					.append(requestedUserID)
					.append("가 회원 테이블에 존재하지 않습니다").toString();				
			throw new ServerServiceException(errorMessage);
		}
		
		String memeberStateOfRequestedUserID = memberRecord.getValue(SB_MEMBER_TB.STATE);
		
		MemberStateType memberStateTypeOfRequestedUserID = null;
		try {
			memberStateTypeOfRequestedUserID = MemberStateType.valueOf(memeberStateOfRequestedUserID, false);
		} catch(IllegalArgumentException e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}
			
			String errorMessage = new StringBuilder("알 수 없는 회원[")
				.append(requestedUserID)
				.append("] 상태[")
				.append(memeberStateOfRequestedUserID)
				.append("] 값입니다").toString();
			
			throw new ServerServiceException(errorMessage);
		}
		
		if (! MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}
			
			String errorMessage = new StringBuilder("요청한 사용자[")
					.append(requestedUserID)
					.append("] 상태[")
					.append(memberStateTypeOfRequestedUserID.getName())
					.append("]가 정상이 아닙니다").toString();				
			throw new ServerServiceException(errorMessage);
		}
		
		return memberRecord.getValue(SB_MEMBER_TB.ROLE);
	}
	
}
