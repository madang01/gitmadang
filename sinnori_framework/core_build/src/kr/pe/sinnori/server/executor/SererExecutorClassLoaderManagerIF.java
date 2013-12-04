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

package kr.pe.sinnori.server.executor;

import kr.pe.sinnori.common.exception.DynamicClassCallException;


/**
 * @author Jonghoon Won
 *
 */
public interface SererExecutorClassLoaderManagerIF {
	/**
	 * 메시지 식별자에 대응하는 비지니스 로직 클래스 인스턴스를 반환한다.
	 * @param messageID 메시지 식별자
	 * @return 메시지 식별자에 대응하는 비지니스 로직 클래스 인스턴스
	 * @throws IllegalArgumentException 파라미터 메시지 식별자 값이 잘못 되었을 경우 던지는 예외.
	 * @throws DynamicClassCallException 동적 클래스 로딩시 생기는 에러 발생시 던지는 예외
	 */
	public AbstractServerExecutor getServerExecutorObject(String messageID) throws IllegalArgumentException, DynamicClassCallException;
}
