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
 * @author Won Jonghoon
 * 
 */
public class CommonType {
	/**
	 * 메시지를 스트림으로 인코딩/디코딩 하는 방법.
	 */
	public enum MESSAGE_PROTOCOL {
		DHB, DJSON, THB 
	};
	

	/**
	 * 연결 종류, NoShareAsyn(비공유+비동기), ShareAsyn(공유+비동기), NoShareSync(비공유+동기)
	 * @author Won Jonghoon
	 *
	 */
	public enum CONNECTION_TYPE {
		NoShareAsyn, ShareAsyn, NoShareSync
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
		SINGLE_ITEM, ARRAY_ITEM
	};

	/**
	 * 동작하는 곳 구분. SERVER : 서버, CLIENT : 클라이언트
	 *
	 */
	public enum SERVER_CLIENT_GUBUN {
		SERVER, CLIENT
	};

	
	public enum WRAPBUFFER_RECALL_GUBUN {
		WRAPBUFFER_RECALL_NO, WRAPBUFFER_RECALL_YES
	};
	
	/** 
	 * <pre>
	 * 메시지 전송 방향.
	 * (1) FROM_NONE_TO_NONE : 메시지는 서버에서 클라이언트로 혹은 클라이언트에서 서버로 양쪽 모두에서 전송되지 않는다.
	 * (2) FROM_SERVER_TO_CLINET : 메시지는 서버에서 클라이언트로만 전송된다.
	 * (3) FROM_CLIENT_TO_SERVER : 메시지는 클라이언트에서 서버로만 전송된다.
	 * (4) FROM_ALL_TO_ALL : 메시지는 서버에서 클라이언트로도 혹은 클라이언트에서 서버로 양쪽 모두에서 전송된다.
	 * </pre> 
	 */
	public enum MESSAGE_TRANSFER_DIRECTION {
		FROM_NONE_TO_NONE,
		FROM_SERVER_TO_CLINET, 
		FROM_CLIENT_TO_SERVER,
		FROM_ALL_TO_ALL,
	};
}
