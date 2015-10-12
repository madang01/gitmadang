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

import java.nio.charset.Charset;

import kr.pe.sinnori.impl.message.SelfExn.SelfExnClientCodec;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnDecoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnEncoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnServerCodec;


/**
 * 공통 상수와 환경 변수 미 설정시 디폴트 값을 갖는 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class CommonStaticFinalVars {
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final byte ZERO_BYTE = 0;
	public static final short ZERO_SHORT = 0;
	public static final int ZERO_INTEGER = 0;
	public static final long ZERO_LONG = 0L;
	
	public static final String SINNORI_ROOT_LOGGER_NAME = "kr.pe.sinnori";
	
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME = "sinnori.projectName";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH = "sinnori.installedPath";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_PATH = "logback.configurationFile";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH = "sinnori.logPath";
	
			
	public static final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
	
		
	/************* network binary stream start *************/
	public static final short MAX_UNSIGNED_BYTE = 0xff;
	public static final int MAX_UNSIGNED_SHORT = 0xffff;
	public static final long MAX_UNSIGNED_INTEGER = 0xffffffffL;
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
	
	
	/*********** Sinnori Build System start **********/
	/** configuration start */
	public static final String DBCP_NAME_LIST_KEY_STRING = "dbcp.name_list.value";
	public static final String SUBPROJECT_NAME_LIST_KEY_STRING = "subproject.name_list.value";	
	public static final String SINNORI_LOGBACK_LOG_FILE_NAME = "logback.xml";
	public static final String SINNORI_CONFIG_FILE_NAME = "sinnori.properties";	
	public static final String DBCP_CONNECTION_POOL_DEFAULT_NAME="tw_sinnoridb";
	/** configuration end */
	
	/** ant.properties key start */
	public static final String IS_WEB_CLIENT_KEY = "is.webclient";
	public static final String SERVLET_SYSTEM_LIBIARY_PATH_KEY = "servlet.systemlib.path";
	/** ant.properties key end */
	
	/** server build.xml, appclient build.xml, webclient build.xml start */
	public static final String JAVA_COMPILE_OPTION_DEBUG_KEY = "java.complile.option.debug";
	/** server build.xml, appclient build.xml, webclient build.xml end */
	
	/** server build system start */
	public static final String SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE = "SinnoriServerRun.jar";
	public static final String SERVER_MAIN_CLASS_FULL_NAME_VALUE = "main.SinnoriServerMain";
	/** server build system end */	
	
	/** appclient build system start */
	public static final String APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE = "SinnoriAppClientRun.jar";
	public static final String APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE= "main.SinnoriAppClientMain";
	/** appclient build system end */
	
	/** webclient build system start */
	public static final String WEBCLIENT_CORE_JAR_SHORT_FILE_NAME_KEY = "webclient.core.jar";
	public static final String WEBCLIENT_CORE_JAR_SHORT_FILE_NAME_VALUE = "SinnoriWebLib.jar";
	/** webclient build system end */
	/*********** Sinnori Build System end **********/
	
	/** sinnori message information xml file's root tag */
	public static final String MESSAGE_INFO_XML_FILE_ROOT_TAG = "sinnori_message";
}
