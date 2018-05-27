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

package kr.pe.codda.common.etc;

import java.nio.charset.Charset;

import kr.pe.codda.impl.message.SelfExnRes.SelfExnResClientCodec;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResDecoder;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResEncoder;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResServerCodec;


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
	
	public static final String ROOT_PROJECT_NAME = "codda";
	
	/** kr.pe.codda */
	public static String BASE_PACKAGE_NAME = "kr.pe.codda";
	
	public static String BASE_DYNAMIC_CLASS_FULL_NAME = new StringBuilder(BASE_PACKAGE_NAME)
			.append(".impl").toString();
	
	public static String BASE_MESSAGE_CLASS_FULL_NAME = new StringBuilder(BASE_DYNAMIC_CLASS_FULL_NAME)
			.append(".message").toString();
	
	public static String BASE_TASK_CLASS_FULL_NAME = new StringBuilder(BASE_DYNAMIC_CLASS_FULL_NAME)
			.append(".task").toString();
	
	public static String BASE_SERVER_TASK_CLASS_FULL_NAME = new StringBuilder(BASE_TASK_CLASS_FULL_NAME)
			.append(".server").toString();
	
	public static String BASE_CLIENT_TASK_CLASS_FULL_NAME = new StringBuilder(BASE_TASK_CLASS_FULL_NAME)
			.append(".client").toString();
	
		
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME = "codda.projectName";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH = "codda.installedPath";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE = "logback.configurationFile";
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH = "codda.logPath";
	
			
	public static final String CONFIG_FILE_CHARSET = "UTF-8";
	
	
	public static final int MD5_BYTESIZE = 16;
	
		
	/************* network binary stream start *************/
	public static final short UNSIGNED_BYTE_MAX = 0xff;
	public static final int UNSIGNED_SHORT_MAX = 0xffff;
	public static final long UNSIGNED_INTEGER_MAX = 0xffffffffL;
	/************* network binary stream end *************/
	
		
	public static final String LF_CHAR = "\n";
	public static final String CR_CHAR = "\r";
	
	public static final int ASYN_MAILBOX_ID = 0;
	
	public static final String PRIVATE_KEY_FILE_NAME = "codda.privatekey";
	public static final String PUBLIC_KEY_FILE_NAME = "codda.publickey";
	public static final long MAX_KEY_FILE_SIZE = 1024 * 4L;
	
	public static final String EMPTY_STRING = "";	
		
	public static final SelfExnResEncoder SELFEXN_ENCODER = new SelfExnResEncoder();
	public static final SelfExnResDecoder SELFEXN_DECODER= new SelfExnResDecoder();
	
	public static final SelfExnResClientCodec SELFEXN_CLIENT_CODEC = new SelfExnResClientCodec();
	public static final SelfExnResServerCodec SELFEXN_SERVER_CODEC= new SelfExnResServerCodec();

	public static final Charset DEFUALT_CHARSET = Charset.forName("UTF-8");
	public static final Charset SOURCE_FILE_CHARSET = DEFUALT_CHARSET;	
	public static final Charset CIPHER_CHARSET = DEFUALT_CHARSET;
	public static final String PASSWORD_ALGORITHM_NAME = "SHA-512";
	
	
	/** 속도를 위해서 jar 파일 내의 클래스 파일들은 메모리에 적재시키기때문에 너무 큰 파일들은 시스템에 무리를 주기때문에 크기 제한을 건다. */
	public static final int MAX_FILE_SIZE_IN_JAR_FILE = 1024 * 1024;
	
	
	public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	
	/*********** Build System start **********/
	/** configuration start */
	public static final String DBCP_NAME_LIST_KEY_STRING = "dbcp.name_list.value";
	public static final String SUBPROJECT_NAME_LIST_KEY_STRING = "subproject.name_list.value";	
	public static final String LOGBACK_LOG_FILE_NAME = "logback.xml";
	public static final String CONFIG_FILE_NAME = "codda.properties";
	/** configuration end */
	
	/** ant.properties key start */
	public static final String SERVLET_SYSTEM_LIBRARY_PATH_KEY = "servlet.systemlib.path";
	/** ant.properties key end */
	
	public static final String CORE_ALL_JAR_FILE_NAME = "codda-core-all.jar";
	
	/** server build.xml, appclient build.xml, webclient build.xml start */
	public static final String JAVA_COMPILE_OPTION_DEBUG_KEY = "java.complile.option.debug";
	/** server build.xml, appclient build.xml, webclient build.xml end */
	
	/** server build system start */
	public static final String SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE = "CoddaServerRun.jar";
	public static final String SERVER_MAIN_CLASS_FULL_NAME_VALUE = "main.ServerMain";
	/** server build system end */	
	
	/** appclient build system start */
	public static final String APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE = "CoddaAppClientRun.jar";
	public static final String APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE= "main.AppClientMain";
	/** appclient build system end */
	
	/** webclient build system start */
	public static final String WEBCLIENT_CORE_JAR_SHORT_FILE_NAME_KEY = "webclient.core.jar";
	public static final String WEBCLIENT_CORE_JAR_SHORT_FILE_NAME_VALUE = "CoddaWebLib.jar";
	public static final String WEBCLIENT_ANT_PROPRTEIS_FILE_NAME_VALUE = "webAnt.properties";
	
	/** webclient build system end */
	
	/*********** Build System end **********/
	
	/** message information xml file's root tag */
	public static final String MESSAGE_INFO_XML_FILE_ROOT_TAG = "message";
	
	public static final String MYBATIS_CONFIG_XML_FILE_ROOT_TAG = "configuration";
	
}
