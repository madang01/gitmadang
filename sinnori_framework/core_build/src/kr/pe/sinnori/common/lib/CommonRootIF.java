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

import kr.pe.sinnori.common.configuration.SinnoriConfig;
import kr.pe.sinnori.common.util.LogManager;

import org.apache.log4j.Logger;

/**
 * 로그와 신놀이 환경 변수 접근 변수를 담은 공통 인터페이스<br/>
 * * 로그를 남기는 변수명과 신놀이 환경 변수 값에 대한 변수명을 강제하기위한 수단이다.
 * 
 * @author Jonghoon Won
 */
public interface CommonRootIF {
	/**
	 * 신놀이 환경 변수 접근 변수
	 */
	public static final SinnoriConfig conf = SinnoriConfig.getInstance();
	/**
	 * 로그 변수
	 */
	public static final Logger log = LogManager.getInstance().getLogger();
}
