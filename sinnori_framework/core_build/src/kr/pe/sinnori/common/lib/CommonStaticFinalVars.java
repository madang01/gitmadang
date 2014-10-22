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

import java.nio.charset.Charset;

import kr.pe.sinnori.impl.message.SelfExn.SelfExnClientCodec;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnDecoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnEncoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnServerCodec;


/**
 * 공통 상수와 환경 변수 미 설정시 디폴트 값을 갖는 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class CommonStaticFinalVars {
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final byte ZERO_BYTE = 0;
	public static final short ZERO_SHORT = 0;
	public static final int ZERO_INTEGER = 0;
	public static final long ZERO_LONG = 0L;
	
	public static final String SINNORI_ROOT_LOGGER_NAME = "kr.pe.sinnori";
	
	public static final String SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME = "sinnori.projectName";
	public static final String SINNORI_LOG_PATH_JAVA_SYSTEM_VAR_NAME = "sinnori.logPath";
	public static final String SINNORI_CONFIG_FILE_JAVA_SYSTEM_VAR_NAME = "sinnori.configurationFile";
	public static final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
		
	/************* network binary stream start *************/
	public static final short MAX_UNSIGNED_BYTE = 0xff;
	public static final int MAX_UNSIGNED_SHORT = 0xffff;
	public static final long MAX_UNSIGNED_INT = 0xffffffffL;
	/************* network binary stream end *************/
	
		
	public static final String LF_CHAR = "\n";
	public static final String CR_CHAR = "\r";
	
	public static final int ASYN_MAILBOX_ID = 0;
	
	public static final String PRIVATE_KEY_FILE_NAME = "sinnori.privatekey";
	public static final String PUBLIC_KEY_FILE_NAME = "sinnori.publickey";
	public static final long MAX_KEY_FILE_SIZE = 1024 * 4L;
	
	public static final String EMPTY_STRING = "";	
		
	public static final SelfExnEncoder SELFEXN_ENCODER = new SelfExnEncoder();
	public static final SelfExnDecoder SELFEXN_DECODER= new SelfExnDecoder();
	
	public static final SelfExnClientCodec SELFEXN_CLIENT_CODEC = new SelfExnClientCodec();
	public static final SelfExnServerCodec SELFEXN_SERVER_CODEC= new SelfExnServerCodec();

	public static final Charset SINNORI_SOURCE_FILE_CHARSET = Charset.forName("UTF-8");
	
	public static final Charset SINNORI_PASSWORD_CHARSET = Charset.forName("UTF-8");
	public static final String SINNORI_PASSWORD_ALGORITHM_NAME = "SHA-512";
	
	
	/** 속도를 위해서 jar 파일 내의 클래스 파일들은 메모리에 적재시키기때문에 너무 큰 파일들은 시스템에 무리를 주기때문에 크기 제한을 건다. */
	public static final int MAX_FILE_SIZE_IN_JAR_FILE = 1024 * 1024;
}
