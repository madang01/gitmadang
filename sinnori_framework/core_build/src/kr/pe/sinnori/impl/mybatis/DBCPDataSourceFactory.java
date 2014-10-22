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

import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DBCPManager;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * <pre>
 * System ClassLoader 에서 싱글턴으로 생성된 DBCP 관리자의 DB 연결 폴을 갖는 mybatis DB 연결 폴 클래스.
 * 서버 동적 클래스 로더에서 호출되는 전용 클래스이다.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class DBCPDataSourceFactory extends UnpooledDataSourceFactory implements CommonRootIF {
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	public DBCPDataSourceFactory() {
		try {
			this.dataSource = dbcpManager.getBasicDataSource();
		} catch (DBNotReadyException e) {
			log.error("DBNotReadyException", e);
			System.exit(1);
		}
	}

}
