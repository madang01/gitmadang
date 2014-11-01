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
package kr.pe.sinnori.common.serverlib;

/**
 * 항목 검사기, 예를 들면 아이디, 비밀번호 같은 공통 항목에 대한 입력값 검사기
 * @author Jonghoon Won
 *
 */
public class ValueChecker {
	/**
	 * 아이디에 대한 입력값 검사를 수행한다.
	 * @param id 아이디
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidUserId(String userId) throws RuntimeException {
		if (null == userId) {
			throw new RuntimeException("파라미터 아이디 값이 null 입니다.");
		}
		String regexId = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		boolean isValid = userId.matches(regexId);
		if (!isValid) {
			throw new RuntimeException(String.format("아이디[%s]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 4글자 최대 15자를 요구합니다.", userId));
		}
	}
	
	/**
	 * 작성자 아이디에 대한 입력값 검사를 수행한다.
	 * @param writerId 작성자 아이디
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidWriterId(String writerId) throws RuntimeException {
		if (null == writerId) {
			throw new RuntimeException("파라미터 작성자 아이디 값이 null 입니다.");
		}
		String regexId = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		boolean isValid = writerId.matches(regexId);
		if (!isValid) {
			throw new RuntimeException(String.format("작성자 아이디[%s]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 4글자 최대 15자를 요구합니다.", writerId));
		}
	}
	
	/**
	 * 비밀번호에 대한 입력값 검사를 수행한다.
	 * @param password 비밀번호
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwd(String password) throws RuntimeException {
		if (null == password) {
			throw new RuntimeException("파라미터 비밀번호 값이 null 입니다.");
		}
		
		/**
		 * \p{Graph} = [\p{Alpha}|\p{Digit}{1,}|\p{Punct}]
		 */
		String regexPwd = "^\\p{Graph}{8,15}$";
		boolean isValid = password.matches(regexPwd);
		if (!isValid) {
			throw new RuntimeException("비밀번호는 영문, 숫자 그리고 문장부호 조합으로 최소 8자 최대 15자를 요구합니다.");
		}
		
		String regexPwdAlpha = ".*\\p{Alpha}{1,}.*";
		isValid = password.matches(regexPwdAlpha);
		if (!isValid) {
			throw new RuntimeException("비밀번호는 영문을 최소 1문자 포함해야 합니다.");
		}
		
		String regexPwdDigit = ".*\\p{Digit}{1,}.*";
		isValid = password.matches(regexPwdDigit);
		if (!isValid) {
			throw new RuntimeException("비밀번호는 숫자를 최소 1문자 포함해야 합니다.");
		}		
				
		/**
		 * \p{Punct} : !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
		 */
		String regexPwdPunct = ".*\\p{Punct}{1,}.*";
		isValid = password.matches(regexPwdPunct);
		if (!isValid) {
			throw new RuntimeException("비밀번호는 문장부호를 최소 1문자 포함해야 합니다.");
		}		
	}
	
	/**
	 * 별명에 대한 입력값 검사를 수행한다.
	 * @param nickname 별명
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidNickname(String nickname) throws RuntimeException {
		if (null == nickname) {
			throw new RuntimeException("파라미터 별명 값이 null 입니다.");
		}
		
		String regexNickname = "^[a-zA-Z0-9가-힣]{4,20}$";
		boolean isValid = nickname.matches(regexNickname);
		if (!isValid) {
			throw new RuntimeException("별명은 한글, 영문, 숫자 조합으로 최소 4자 최대 20자를 요구합니다.");
		}
		
	}
		
	/**
	 * 비밀번호 분실시 힌트에 대한 입력값 검사를 수행한다.
	 * @param pwdHint 비밀번호 분실시 힌트
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdHint(String pwdHint) throws RuntimeException {
		if (null == pwdHint) {
			throw new RuntimeException("파라미터 비밀번호 분실시 힌트 값이 null 입니다.");
		}
		
		if (pwdHint.length() < 2) {
			throw new RuntimeException("비밀번호 분실시 힌트 값은 최소 2글자를 요구합니다.");
		}
		
	}
		
	/**
	 * 비빌번호 분실시 답변에 대한 입력값 검사를 수행한다.
	 * @param pwdAnswer 비빌번호 분실시 답변
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPwdAnswer(String pwdAnswer) throws RuntimeException {
		if (null == pwdAnswer) {
			throw new RuntimeException("파라미터 비밀번호 분실시 답변 값이 null 입니다.");
		}
		
		if (pwdAnswer.length() < 2) {
			throw new RuntimeException("비밀번호 분실시 답변 값은 최소 2글자를 요구합니다.");
		}
	}
	
	/** 게시판 식별자 검사 */
	public static void checkValidBoardId(long boardId) throws RuntimeException {		
		if (boardId <= 0) {
			String errorMessage = new StringBuilder("게시판 식별자 번호 값[")
			.append(boardId).append("]은 0 보다 커야합니다.").toString();
			throw new RuntimeException(errorMessage);
		}	
	}
	
	/** 게시판 번호 검사 */
	public static void checkValidBoardNo(long boardNo) throws RuntimeException {		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[")
			.append(boardNo).append("]은 0 보다 커야합니다.").toString();
			throw new RuntimeException(errorMessage);
		}	
	}
	
	/** 부모 게시판 번호 검사 */
	public static void checkValidParentBoardNo(long parentBoardNo) throws RuntimeException {		
		if (parentBoardNo <= 0) {
			String errorMessage = new StringBuilder("부모 게시판 번호 값[")
			.append(parentBoardNo).append("]은 0 보다 커야합니다.").toString();
			throw new RuntimeException(errorMessage);
		}	
	}
	
	/**
	 * 게시판 제목에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 제목
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidSubject(String subject) throws RuntimeException {
		if (null == subject) {
			throw new RuntimeException("파라미터 게시판 제목 값이 null 입니다.");
		}
		
		if (subject.length() < 2) {
			throw new RuntimeException("게시판 제목 값은 최소 2글자를 요구합니다.");
		}
	}
	
	/**
	 * 게시판 내용에 대한 입력값 검사를 수행한다.
	 * @param subject 게시판 내용
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidContent(String content) throws RuntimeException {
		if (null == content) {
			throw new RuntimeException("파라미터 게시판 내용 값이 null 입니다.");
		}
		
		if (content.length() < 2) {
			throw new RuntimeException("게시판 내용 값은 최소 2글자를 요구합니다.");
		}
	}
	
	/**
	 * IP 주소에 대한 입력값 검사를 수행한다.
	 * @param ip IP 주소
	 * @throws RuntimeException 값이 적당하지 않으면 던진는 예외
	 */
	public static void checkValidIP(String ip) throws RuntimeException {
		if (null == ip) {
			throw new RuntimeException("파라미터 게시판 내용 값이 null 입니다.");
		}
		
		String regexIP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		boolean isValid = ip.matches(regexIP);
		if (!isValid) {
			String errorMessage = new StringBuilder("IP 값[")
			.append(ip).append("]이 IP 주소 포맷 xxx.xxx.xxx.xx 가 아닙니다.").toString();
			throw new RuntimeException(errorMessage);
		}
	}
	
	
	
}
