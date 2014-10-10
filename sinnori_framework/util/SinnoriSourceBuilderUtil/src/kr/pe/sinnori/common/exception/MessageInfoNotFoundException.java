package kr.pe.sinnori.common.exception;
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



/**
 * 입/출력 메시지를 생성할때 XML로 작성된 메시지 정보 파일이 존재하지 않을때 던지는 예외
 * 
 * @author Jonghoon Won
 * 
 */

@SuppressWarnings("serial")
public class MessageInfoNotFoundException extends Exception {

	/**
	 * 생성자
	 * 
	 * @param errorMessage 에러 내용
	 */
	public MessageInfoNotFoundException(String errorMessage) {
		super(errorMessage);
	}

}
