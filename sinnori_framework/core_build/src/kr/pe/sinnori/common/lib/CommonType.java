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
 * 공통 타입 정의 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public class CommonType {
	/**
	 * 메시지를 스트림으로 인코딩/디코딩 하는 방법.
	 */
	public enum MESSAGE_PROTOCOL {
		DHB, DJSON 
	};

	/**
	 * 소켓 채널 쓰레드간에 공유 모드 Single : 소켓 채널을 쓰레드간에 공유 하지 않는 모드 Multi : 소켓 채널을 쓰레드간에
	 * 공유하는 모드.
	 */
	public enum THREAD_SHARE_MODE {
		Single, Multi
	};

	/**
	 * 섹션키에서 대칭키 인코딩 방법 NONE : 인코딩 없음 BASE64 : 대칭키는 Base64 인코딩으로 되어있음. 참고) 공개키
	 * 암호화 라이브러리에서 공개키로 암호화 할때 이진 데이터를 못받고 문자열만 받을 경우 부득이 Base64 인코딩해야함.
	 */
	public enum SymmetricKeyEncoding {
		NONE, BASE64
	};

	/**
	 * 메시지 항목 구분. SINGLE_ITEM : 단일 항목, ARRAY : 배열
	 */
	public enum LOGICAL_ITEM_GUBUN {
		SINGLE_ITEM, ARRAY
	};

}
