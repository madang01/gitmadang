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

import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 메시지 관리자 인터 페이스.
 * @author Jonghoon Won
 *
 */
public interface MessageMangerIF {
	/**
	 * 프로젝트에서 정의한 메시지 식별자에 대응하는 입력 메시지를 반환한다.
	 * @param messageID 메시지 식별자
	 * @return 프로젝트에서 정의한 메시지 식별자에 대응하는 입력 메시지
	 * @throws MessageInfoNotFoundException  메시지 정보 파일이 존재하지 않을때 던지는 예외
	 * @throws IllegalArgumentException 파라미터 메시지 식별자값이 null 혹은 유효하지 않을때 던지는 예외
	 */
	public InputMessage createInputMessage(String messageID) throws IllegalArgumentException, MessageInfoNotFoundException;
	
	/**
	 * 프로젝트에서 정의한 메시지 식별자에 대응하는 출력 메시지를 반환한다.
	 * @param messageID 메시지 식별자
	 * @return 프로젝트에서 정의한 메시지 식별자에 대응하는 출력 메시지
	 * @throws MessageInfoNotFoundException  메시지 정보 파일이 존재하지 않을때 던지는 예외
	 * @throws IllegalArgumentException 파라미터 메시지 식별자값이 null 혹은 유효하지 않을때 던지는 예외
	 */
	public OutputMessage createOutputMessage(String messageID) throws IllegalArgumentException, MessageInfoNotFoundException;
}
