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
 * 기능 제공을 하지 않을때 던지는 예외.
 * 사례 목록 :
 * (1) 동기+비공유 연결에서 비동기 메시지를 보내고자 할때, 참고) 동기 소켓을 갖고서 비동기식으로 메시지를 주고 받을 수 없다.
 * (2) 비동기+공유 연결인데 비 공유 연결을 얻고자 할때 혹은 비동기+공유 연결인데 비 공유 연결을 반환하고자 할때,  
 * </pre>
 * @author "Won Jonghoon"
 *
 */
@SuppressWarnings("serial")
public class NotSupportedException extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public NotSupportedException(String errorMessage) {
		super(errorMessage);
	}
}
