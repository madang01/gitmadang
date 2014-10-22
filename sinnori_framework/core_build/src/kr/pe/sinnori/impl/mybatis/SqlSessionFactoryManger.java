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
package kr.pe.sinnori.impl.mybatis;

import java.io.InputStream;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * <pre>
 * 싱글턴 패턴으로 생성된 프로젝트 이름별 SqlSessionFactory 관리자 클래스.
 * 서버 동적 클래스 로더에서 호출되는 전용 클래스로 
 * 싱글턴 패턴으로 딱 1번 mybatis 설정파일을 읽어와서 SqlSessionFactory 를 생성한다.
 * 이때 DB 연결 폴은 System ClassLoader 에서 싱글턴으로 생성된 DBCP 관리자의 DB 연결 폴이다.
 * 
 * 참고) 비지니스 로직의 잦은 수정으로 서버 동적 클래스 로더는 수시로 새롭게 생성된다.
 * 그런데 서버 동적 클래스 로더 마다 1:1로 mybatis 설정파일로 부터 SqlSessionFactory 가 생성되는데 
 * mybatis 기본 DB 연결 폴은 매번 SqlSessionFactory 이 생성될때 마다 새롭게 생성된다.
 * 따라서 기본 DB 연결 폴이 아닌 사용자 정의 mybatis DB 연결 폴 클래스(=DBCPDataSourceFactory) 를 이용하여
 * DB 연결 폴을 서버 동적 클래스 로더가 아닌 System ClassLoader 에서 싱글턴으로 생성하여 이 문제를 해결한다.
 * </pre>
 * 
 * @author Jonghoon Won
 *
 */
public class SqlSessionFactoryManger implements CommonRootIF {
	private final ClassLoader classLoader = this.getClass().getClassLoader();
	
	// private Hashtable<String, SqlSessionFactory> sqlSessionFactoryHash = new Hashtable<String, SqlSessionFactory>();
	private SqlSessionFactory factory = null;

	
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 */
	private SqlSessionFactoryManger() {
		
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SqlSessionFactoryMangerHolder {
		static final SqlSessionFactoryManger singleton = new SqlSessionFactoryManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SqlSessionFactoryManger getInstance() {
		return SqlSessionFactoryMangerHolder.singleton;
	}
	
	/**
	 * <pre>
	 * SqlSessionFactory 객체를 반환한다. 
	 * 
	 * 참고) 서버 프로젝트에서 서버 동적 클래스 로더가 관리되므로 
	 * 서버 프로젝트에서 관리하는 서버 동적 클래스 로더는 
	 * 딱 1번만 mybatis 설정 파일을 읽어 생성되는 SqlSessionFactory 객체를 갖는다.
	 * </pre>
	 * 
	 * @param serverProjectConfig 서버 설정
	 * @return SqlSessionFactory 객체
	 * @throws DBNotReadyException SqlSessionFactory 객체 생성 실패시 던지는 예외
	 */
	public SqlSessionFactory getSqlSessionFactory(ServerProjectConfig serverProjectConfig) throws DBNotReadyException {
		// String projectName = serverProjectConfig.getProjectName();
		// SqlSessionFactory factory = sqlSessionFactoryHash.get(projectName);
		synchronized (classLoader) {
			if (null == factory) {
				InputStream is = null;
				try {
					is = classLoader.getResourceAsStream(serverProjectConfig.getMybatisConfigFileName());			
				} catch(Exception e) {
					String errorMessage = String.format("fail to get an InputStream Object from mybatis config file[%s]", serverProjectConfig.getMybatisConfigFileName());
					log.warn(errorMessage, e);
					throw new DBNotReadyException(errorMessage);
				}
				
				try {
					factory = new SqlSessionFactoryBuilder().build(is);
				} catch(Exception e) {
					String errorMessage = String.format("fail to build a SqlSessionFactory object from mybatis config file[%s]", serverProjectConfig.getMybatisConfigFileName());
					log.warn(errorMessage, e);
					throw new DBNotReadyException(errorMessage);
				}
							
				// sqlSessionFactoryHash.put(projectName, factory);
			}		
			
			
			return factory;
		}
		
	}

}
