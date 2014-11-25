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
package kr.pe.sinnori.client;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.protocol.MessageCodecIF;

/**
 * 클라이언트 운영에 필요한 동적 클래스 객체들에 대한 캐쉬 관리자 인터페이스
 * @author "Won Jonghoon"
 *
 */
public interface ClientObjectCacheManagerIF {
	/**
	 * 지정된 클래스 로더에서 메시지 식별자와 1:1 대응하는 클라이언트 코덱 객체를 얻어 반환한다.
	 * @param classLoader 클래스 로더
	 * @param messageID 메시지 식별자
	 * @return 지정된 클래스 로더에서 메시지 식별자와 1:1 대응하는 클라이언트 코덱 객체
	 * @throws DynamicClassCallException 동적 클래스를 다룰때 에러 발생시 던지는 예외
	 */
	public MessageCodecIF getClientCodec(ClassLoader classLoader, String messageID) throws DynamicClassCallException;
}
