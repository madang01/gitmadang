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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.util.HexUtil;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * <pre>
 * DB 연결 폴 관리자인 아파치 commons-dbcp 를 신놀이 설정 파일에서 
 * 지정한 DB 관련 환경 변수값에 맞쳐 설정하여 이용하는 클래스.
 * </pre>
 * 
 * @author Jonghoon Won
 * 
 */
public final class SinnoriDBManager implements CommonRootIF {
	private DataSource dataSource = null;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SinnoriDBManagerHolder {
		static final SinnoriDBManager singleton = new SinnoriDBManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriDBManager getInstance() {
		return SinnoriDBManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private SinnoriDBManager() {
		/*
		 * jdbc.driver_class.name.value jdbc.db_user_name.value
		 * jdbc.db_user_password_hex.value jdbc.connection_uri.value
		 */
		String driverClassName = (String) conf
				.getResource("jdbc.driver_class_name.value");
		String dbUserName = (String) conf
				.getResource("jdbc.db_user_name.value");
		String dbUserPasswordHex = (String) conf
				.getResource("jdbc.db_user_password_hex.value");
		String dbConnectURI = (String) conf
				.getResource("jdbc.connection_uri.value");

		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			log.warn("JDBC Driver not exist", e);
			return;
			// throw new RuntimeException("JDBC Driver not exist");
		}

		byte[] dbUserPasswordEncryptedBytes = HexUtil
				.hexToByteArray(dbUserPasswordHex);
		byte[] dbUserPasswordBytes = null;
		try {
			dbUserPasswordBytes = ServerSessionKeyManager.getInstance()
					.decryptUsingPrivateKey(dbUserPasswordEncryptedBytes);
		} catch (SymmetricException e) {
			e.printStackTrace();
			return;
		}

		String dbUserPassword = new String(dbUserPasswordBytes);
		
		// FIXME!, 보안을 위해 제거 필요하지만 냅두자.
		log.debug(String.format("dbUserPassword=", dbUserPassword));

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUsername(dbUserName);
		ds.setPassword(dbUserPassword);
		ds.setUrl(dbConnectURI);

		dataSource = ds;

	}

	/**
	 * JDBC 연결 객체 반환한다.
	 * 
	 * @return JDBC 연결 객체
	 * @throws SQLException
	 * @throws DBNotReadyException DB 사용 준비가 안되었을 경우 던지는 예외
	 */
	public Connection getConnection() throws SQLException, DBNotReadyException {
		if (null == dataSource)
			throw new DBNotReadyException("JDBC Driver not ready");

		return dataSource.getConnection();
	}
}
