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

package kr.pe.sinnori.common.exception;

/**
 * <pre>
 * 메시지 항목명의 값을 얻거나 항목의 값을 지정할때 메시지의 항목명과 관련된 에러가 발행시 던지는 예외
 * 구체적으로 다음과 같은 경우에 던지는 예외이다.
 * (1) 파라미터 항목명에 널 포인트 넣었을 경우,
 * (2) 파라미터 항목명이 빈 문자열일 경우,
 * (3) 해당 메지지에 항목명이 존재하지 않을 경우 
 * </pre>
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class MessageItemException extends Exception {
	
	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public MessageItemException(String errorMessage) {
		super(errorMessage);
	}

}
