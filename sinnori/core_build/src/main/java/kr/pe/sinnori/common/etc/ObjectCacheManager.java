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
package kr.pe.sinnori.common.etc;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.configvo.CommonPartConfigurationVO;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 * 로더와 클래스 이름을 키로하는 객체에 대한 캐쉬 관리자
 * 최대 갯수만큼 객체를 캐쉬한다. 
 * 만약 지정된 최대 갯수를 넘어 서면 가장 오래 동안 사용 안한 객체를 캐쉬에서 삭제한다.
 * 최대 갯수는 초기 환경변수에서 읽어온 값으로 설정되며
 * 최대 갯수를 변경할 수 있는 메소드를 제공한다.
 * 참고) 이글 쓰는 2014.08.10 기준으로 신놀이에서는 메시지 입출력과 관련된 메시지 서버/클라이언트 코덱 객체만 다룬다.
 * </pre>
 *  
 * @author "Won Jonghoon"
 * 
 */
public final class ObjectCacheManager {
	protected Logger log = LoggerFactory.getLogger(ObjectCacheManager.class);
	private final Object monitor = new Object();
	@SuppressWarnings("rawtypes")
	private MultiKeyMap objectCache = null;
	
	// private int maxSize = 10;
	// private long maxUpdateSeqInterval=5000;
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ObjectCacheManager() {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		CommonPartConfigurationVO commonPart = sinnoriRunningProjectConfiguration.getCommonPart();
		int cachedObjectMaxSize = commonPart.getCachedObjectMaxSize();
		
		objectCache = MultiKeyMap.multiKeyMap(new LRUMap(cachedObjectMaxSize));
	}	
	
	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class SystemClassManagerHolder {
		static final ObjectCacheManager singleton = new ObjectCacheManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ObjectCacheManager getInstance() {
		return SystemClassManagerHolder.singleton;
	}
	

	@SuppressWarnings("unchecked")
	public Object getCachedObject(ClassLoader classLoader,
			String classFullName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		if (null == classLoader) {
			String errorMessage = "parameter classLoader is null";
			log.debug(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == classFullName) {
			String errorMessage = "parameter classFullName is null";
			log.debug(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		int classLoaderHashCode = classLoader.hashCode();
		
		Object cachedObj = null;
		synchronized (monitor) {
			cachedObj = objectCache.get(classLoaderHashCode, classFullName);
			if (null == cachedObj) {
				/** classLoader 미 등재 */
				Class<?> cachedObjClass = classLoader.loadClass(classFullName);
				cachedObj = cachedObjClass.newInstance();	
				objectCache.put(classLoaderHashCode, classFullName, cachedObj);
			}
		}		
		
		return cachedObj;		
	}	
}
