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
package kr.pe.sinnori.common.lib;

/**
 * 항목 검사기, 예를 들면 아이디, 비밀번호 같은 공통 항목에 대한 입력값 검사기
 * @author Jonghoon Won
 *
 */
public class ValueChecker {
	/**
	 * 아이디에 대한 입력값 검사를 수행한다. 만약 아이디값으로 적당하지 않으면 RuntimeException 예외를 던진다.
	 * @param id 아이디
	 * @throws RuntimeException 아이디 값으로 적당하지 않으면 던진는 예외
	 */
	public static void checkValidID(String id) throws RuntimeException {
		if (null == id) {
			throw new RuntimeException("파라미터 아이디 값이 null 입니다.");
		}
		String regexID = "^\\p{Alpha}\\p{Alnum}{3,14}$";
		boolean isValid = id.matches(regexID);
		if (!isValid) {
			throw new RuntimeException(String.format("아이디[%s]는 첫글자는 영문자 두번째 글자부터는 영문과 숫자 조합으로 최소 4글자 최대 15자로 구성됩니다.", id));
		}
	}
	
	/**
	 * 비밀번호에 대한 입력값 검사를 수행한다. 만약 비밀번호값으로 적당하지 않으면 RuntimeException 예외를 던진다.
	 * @param password 비밀번호
	 * @throws RuntimeException 비밀번호 값으로 적당하지 않으면 던진는 예외
	 */
	public static void checkValidPWD(String password) throws RuntimeException {
		if (null == password) {
			throw new RuntimeException("파라미터 비밀번호 값이 null 입니다.");
		}
		
		/**
		 * \p{Graph} = [\p{Alpha}|\p{Digit}{1,}|\p{Punct}]
		 */
		String regexPwd = "^\\p{Graph}{8,15}$";
		boolean isValid = password.matches(regexPwd);
		if (!isValid) {
			throw new RuntimeException("비밀번호는 영문, 숫자 그리고 문장부호 조합으로 최소 8자 최대 15자로 구성됩니다.");
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
	
	public static void checkValidPwdQuestion(String pwdQuestion) throws RuntimeException {
		if (null == pwdQuestion) {
			throw new RuntimeException("파라미터 비밀번호 분실시 답변을 유도 할 수 있는 질문 값이 null 입니다.");
		}
	}
	
	public static void checkValidPwdAnswer(String pwdAnswer) throws RuntimeException {
		if (null == pwdAnswer) {
			throw new RuntimeException("파라미터 비밀번호 분실시 답변 값이 null 입니다.");
		}
	}
	
}
