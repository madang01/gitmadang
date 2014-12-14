package kr.pe.sinnori.common.configuration;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Properties;

import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.protocol.dhb.header.DHBMessageHeader;

import org.slf4j.Logger;

public class CommonProjectConfig {
	protected String projectName;
	private Properties configFileProperties = null;
	protected Logger log = null;
	
	
	/************* common 변수 시작 ******************/
	/** 메시지 정보 파일들이 위치한 경로 */
	private File messageInfoPath = null;
	
	private String serverHost;
	private int serverPort;
	private ByteOrder byteOrder;
	private Charset charset;
	
	protected int dataPacketBufferMaxCntPerMessage;	
	protected int dataPacketBufferSize;
	private int messageIDFixedSize;
	/********* 가공 데이터 시작 *********/
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	/** 메시지 바디 크기, 단위 byte */
	/********* 가공 데이터 종료 *********/
		
	private CommonType.MESSAGE_PROTOCOL messageProtocol;
	
	/***** 서버 동적 클래스 변수 시작 *****/
	private String classLoaderClassPackagePrefixName = null;
	/***** 서버 동적 클래스 변수 종료 *****/
		
	
	/************* common 변수 종료 ******************/
	
	public CommonProjectConfig(String projectName, Properties configFileProperties, Logger log) {
		this.projectName = projectName;
		this.configFileProperties = configFileProperties;
		this.log = log;
		configCommon(configFileProperties);
	}
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름을 반환한다.
	 * 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름 구조는 <프로젝트명>.common.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 공통 환경 변수의 부분 키
	 * @return 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름
	 */
	private String getCommonKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".common.");
		strBuff.append(subkey);
		strBuff.append(".value");
		
		return strBuff.toString();
	}
	
	/**
	 * 프로젝트의 공통 환경 변수를 읽어와서 저장한다.
	 * @param configFileProperties
	 */
	private void configCommon(Properties configFileProperties) {
		String propKey = null;
		String propValue = null;
		
		/******** 메시지 정보 파일이 위치한 경로 시작 **********/
		propKey = getCommonKeyName("message_info.xmlpath");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			log.error("warning:: 메시지 정보 파일 경로[{}][{}]를 지정해 주세요", propKey, propValue);
			System.exit(1);
		} else {
			messageInfoPath = new File(propValue);
		}
		
		if (!messageInfoPath.exists()) {
			log.error("메시지 정보 파일 경로[{}][{}]가 존재하지 않습니다.", propKey, propValue);
			System.exit(1);
		}
		if (!messageInfoPath.isDirectory() || !messageInfoPath.canRead()) {
			log.error("메시지 정보 파일 경로[{}][{}][{}]가 잘못 되었습니다.", 
					propKey, propValue, messageInfoPath.getAbsolutePath());
			System.exit(1);
		}		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, messageInfoPath.getAbsolutePath());
		/******** 메시지 정보 파일이 위치한 경로 종료 **********/
		
		propKey = getCommonKeyName("host");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverHost = "localhost";
		} else {
			serverHost = propValue;
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverHost);
		
		propKey = getCommonKeyName("port");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverPort = 9090;
		} else {
			try {
				serverPort = Integer.parseInt(propValue);
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverPort);
		
		propKey = getCommonKeyName("byteorder");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			if (propValue.equals("LITTLE_ENDIAN")) {
				byteOrder = ByteOrder.LITTLE_ENDIAN;
			} else if (propValue.equals("BIG_ENDIAN")) {
				byteOrder = ByteOrder.BIG_ENDIAN;
			} else {
				byteOrder = ByteOrder.LITTLE_ENDIAN;
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, byteOrder.toString());
		
		propKey = getCommonKeyName("charset");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			charset = Charset.forName("UTF-8");
		} else {
			try {
				charset = Charset.forName(propValue);
			} catch(IllegalCharsetNameException e) {
				charset = Charset.forName("UTF-8");
			} catch(UnsupportedCharsetException e) {
				charset = Charset.forName("UTF-8");
			}
		}		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, charset.name());
		
		// body_buffer_max_cnt_per_message
		propKey = getCommonKeyName("data_packet_buffer_max_cnt_per_message");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			dataPacketBufferMaxCntPerMessage = 10;
		} else {
			try {
				dataPacketBufferMaxCntPerMessage = Integer.parseInt(propValue);
				if (dataPacketBufferMaxCntPerMessage < 2) dataPacketBufferMaxCntPerMessage = 10;
			} catch(NumberFormatException nfe) {
				dataPacketBufferMaxCntPerMessage = 10;
			}
		}
		
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, dataPacketBufferMaxCntPerMessage);
		
		
		propKey = getCommonKeyName("data_packet_buffer_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			dataPacketBufferSize = 4096;
		} else {
			try {
				dataPacketBufferSize = Integer.parseInt(propValue);
				/** 1024byte 의 배수 아니면 종료 */
				if ((dataPacketBufferSize % 1024) != 0) {
					log.error("{}::prop value[{}], 데이터 패킷 크기는 1024byte 의 배수이어야 합니다.", propKey, propValue);
					System.exit(1);
				}
				if (dataPacketBufferSize < 1024) dataPacketBufferSize = 1024;
			} catch(NumberFormatException nfe) {
				dataPacketBufferSize = 4096;
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, dataPacketBufferSize);
		
		
		propKey = getCommonKeyName("message_id_fixed_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			messageIDFixedSize = 24;
		} else {
			try {
				messageIDFixedSize = Integer.parseInt(propValue);
				if (messageIDFixedSize < 2) messageIDFixedSize = 2;
			} catch(NumberFormatException nfe) {
				messageIDFixedSize = 24;
			}
		}		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, messageIDFixedSize);
		
		/** 가공 데이터 시작 */
		messageHeaderSize = DHBMessageHeader.getMessageHeaderSize(messageIDFixedSize);
		/** 가공 데이터 종료 */	
		
		propKey = getCommonKeyName("message_protocol");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			messageProtocol = CommonType.MESSAGE_PROTOCOL.DHB;
		} else {
			if (propValue.equals("DHB")) {
				messageProtocol = CommonType.MESSAGE_PROTOCOL.DHB;
			} else if (propValue.equals("DJSON")) {
				messageProtocol = CommonType.MESSAGE_PROTOCOL.DJSON;
			} else if (propValue.equals("THB")) {
				messageProtocol = CommonType.MESSAGE_PROTOCOL.THB;
			} else {
				log.error("지원하지 않는 이진형식[{}] 입니다.", propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, messageProtocol.toString());
		
		propKey = getCommonKeyName("classloader.class_package_prefix_name");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			classLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.message.";
		} else {
			classLoaderClassPackagePrefixName = propValue;
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, classLoaderClassPackagePrefixName);
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public File getMessageInfoPath() {
		return messageInfoPath;
	}

	public String getServerHost() {
		return serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public void setServerHost(String newServerHost) {
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info("old serverHost[{}] to new serverHost[{}]", 
				serverHost, newServerHost, t);
		
		
		this.serverHost = newServerHost;
		
		String prop_key = getCommonKeyName("host");
		configFileProperties.setProperty(prop_key, newServerHost);
	}

	public void setServerPort(int newServerPort) {
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info("old serverPost[{}] to new serverPost[{}]", 
				serverPort, newServerPort, t);
		
		this.serverPort = newServerPort;
		String prop_key = getCommonKeyName("port");		
		configFileProperties.setProperty(prop_key, String.valueOf(newServerPort));
	}
	
	public void changeServerAddress(String newServerHost, int newServerPort) {
		/*
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info("old serverHost[{}] to new serverHost[{}], old serverPost[{}] to new serverPost[{}]", 
				serverHost, newServerHost, serverPort, newServerPort), t);
				*/
		
		this.serverHost = newServerHost;
		String prop_key = getCommonKeyName("host");
		configFileProperties.setProperty(prop_key, newServerHost);
		
		this.serverPort = newServerPort;
		prop_key = getCommonKeyName("port");		
		configFileProperties.setProperty(prop_key, String.valueOf(newServerPort));
	}
	
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}
	
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}
	
	public int getMessageIDFixedSize() {
		return messageIDFixedSize;
	}
	
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
	
	public CommonType.MESSAGE_PROTOCOL getMessageProtocol() {
		return messageProtocol;
	}	
	
	public String getClassLoaderClassPackagePrefixName() {
		return classLoaderClassPackagePrefixName;
	}

	public String toCommonString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonProjectConfig [projectName=");
		builder.append(projectName);
		builder.append(", messageInfoPath=");
		builder.append(messageInfoPath.getAbsolutePath());
		builder.append(", serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", charset=");
		builder.append(charset);
		builder.append(", dataPacketBufferMaxCntPerMessage=");
		builder.append(dataPacketBufferMaxCntPerMessage);
		builder.append(", dataPacketBufferSize=");
		builder.append(dataPacketBufferSize);
		builder.append(", messageIDFixedSize=");
		builder.append(messageIDFixedSize);
		builder.append(", messageHeaderSize=");
		builder.append(messageHeaderSize);
		builder.append(", messageProtocol=");
		builder.append(messageProtocol);
		builder.append(", classLoaderClassPackagePrefixName=");
		builder.append(classLoaderClassPackagePrefixName);
		builder.append("]");
		return builder.toString();
	}
}
