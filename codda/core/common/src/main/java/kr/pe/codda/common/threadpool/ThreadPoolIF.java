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
package kr.pe.codda.common.threadpool;

import kr.pe.codda.common.exception.NotSupportedException;

/**
 * 쓰레드 폴 인터페이스
 * 
 * @author Won Jonghoon
 */
public interface ThreadPoolIF {
	/**
	 * 폴 크기 반환
	 * 
	 * @return 폴 크기
	 */
	public int getPoolSize();

	/**
	 * 폴에 쓰레드 추가
	 */
	public void addTask() throws IllegalStateException, NotSupportedException;

	/**
	 * 폴에 등록된 모든 쓰레드 시작
	 */
	public void startAll();

	/**
	 * 폴에 등록된 모드 쓰레드 중지
	 */
	public void stopAll();
}
