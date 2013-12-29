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

import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ProjectConfig;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.io.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.io.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.MessageInfo;
import kr.pe.sinnori.common.message.MessageInfoSAXParser;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 프로젝트 부모 추상화 클래스. 서버/클라이언트 프로젝트 공통 분모를 모은 추상화 클래스이다.
 * @author Jonghoon Won
 *
 */
public abstract class AbstractProject implements CommonRootIF, DataPacketBufferQueueManagerIF, MessageMangerIF {
	/** 모니터 객체 */
	private final Object dataPacketBufferQueueMonitor = new Object();
	
	/** 데이터 패킷 버퍼 큐 */
	protected LinkedBlockingQueue<WrapBuffer> dataPacketBufferQueue  = null;
	
	/** 키가 메시지 식별자, 값이 메시지 정보인 메시지 정보 해쉬 */
	private Hashtable<String, MessageInfo> messageInfoHash = new Hashtable<String, MessageInfo>();
	/**
	 * <pre>
	 * 키가 메시지 식별자, 값이 메시지 정보 파일 정보인 메시지 정보 해쉬
	 * 참고) 메시지 정보 파일 정보는 메시지 정보 파일과 메시지 정보를 만들 시점의 메시지 정보 파일의 수정 시간으로 구성되어 있다.
	 *      메시지 정보를 만들 시점의 메시지 정보 파일의 수정 시간은 최근 수정 여부를 따질때 사용한다.
	 *      만약 최근 수정되었다면 메시지 정보를 재 로딩한다.
	 * </pre>
	 */
	private HashMap<String, ReadFileInfo> messageInfoFileInfoHash = new HashMap<String, ReadFileInfo>();
	
	/** 메시지 정보 파일이 위치한 경로 */
	private File messageInfoPath = null; 
	
	/** 프로젝트 공통 정보 */	
	protected ProjectConfig projectConfig = null;
	// 공통 환경 변수들중 네트워크에서 메시지 교환에 필요한 변수들 묶음 
	// protected CommonProjectInfo commonProjectInfo = null;
	
	protected MessageExchangeProtocolIF messageExchangeProtocol = null;
	
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름 
	 */
	public AbstractProject(String projectName) {
		try {
			projectConfig = (ProjectConfig)conf.getResource(projectName);
		} catch(RuntimeException e) {
			log.fatal(String.format("%s 프로젝트 정보가 존재하지 않습니다.", projectName));
			System.exit(1);
		}
		
		switch (projectConfig.getMessageProtocol()) {
			case DHB : {
				messageExchangeProtocol = new DHBMessageProtocol(
						projectConfig.getMessageIDFixedSize(), 
						projectConfig.getMessageHeaderSize(), this);
				
				break;
			}
			case DJSON : {
				messageExchangeProtocol = new DJSONMessageProtocol(
						this);
				break;
			}
			default : {
				log.fatal(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.", projectName, projectConfig.getMessageProtocol().toString()));
				System.exit(1);
			}
		}
 		

		messageInfoPath = projectConfig.getMessageInfoPath();
		FilenameFilter xmlFilter = new XMLFileFilter();
		messageInfoPath.list(xmlFilter);
		File[] fileList = messageInfoPath.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			// f.canRead()

			if (!f.isFile()) {
				log.warn(String.format("project[%s] warning :: not file , file name=[%s]", projectName, f.getName()));
				continue;
			}

			if (!f.canRead()) {
				log.warn(String.format("project[%s] warning :: can't read, file name=[%s]", projectName,
						f.getName()));
				continue;
			}
			/*
			 * String fileName = f.getName(); int lastPathInx =
			 * fileName.lastIndexOf(".xml"); if (lastPathInx <= 0) {
			 * log.warning( "3.warning :: file is not xml file, file name=[%s]",
			 * f.getName()); continue; }
			 */

			readMessageXMLFile(f);
		}
	}
	
	
	/**
	 * 메시지 스키마를 정의한 XML 파일을 SAX 파싱하여 읽어 메시지 스키마 목록에 추가한다.
	 * 
	 * @param xmlFile
	 *            메시지 스키마를 정의한 XML 파일
	 */
	private MessageInfo readMessageXMLFile(File xmlFile) {
		String projectName = projectConfig.getProjectName();
		Charset charset = projectConfig.getCharset();
		// log.info("call readMessageXMLFile file=[%s]",
		// xmlFile.getAbsolutePath());

		// SinnoriMessageSchema
		MessageInfoSAXParser messageSAXUtil = new MessageInfoSAXParser(xmlFile, charset);
		MessageInfo messageInfo = messageSAXUtil.parse();

		if (null != messageInfo) {

			String messageID = messageInfo.getMessageID();

			if (null != messageInfoHash.get(messageID)) {
				log.warn(String.format(
						"project[%s] warning :: 기 등록된 메시지 식별자[%s]입니다. 신놀이 메시지 정보 파일[%s]의 이름이 메시지 식별자인지 다시 한번 확인하시기 바랍니다.",
						projectConfig.getProjectName(), messageID, xmlFile.getName()));
				return null;
			}

			String fileName = xmlFile.getName();
			String messageIDFile = new StringBuffer(messageID).append(".xml")
					.toString();

			// log.info("2222 fileName=[%s], messageIDFile=[%s]", fileName,
			// messageIDFile);

			if (!fileName.equals(messageIDFile)) {
				log.warn(String.format(
						"project[%s] warning :: 메시지 정보 파일[%s]의 이름은 메시지 식별자[%s] 이어야 합니다.",
						projectName, xmlFile.getName(), messageID));
				return null;
			}

			// messageInfoHash.get(messageId)
			// lastModifiedHash.put(messageID, xmlFile.lastModified());
			// xmlFileHash.put(messageID, xmlFile);
			ReadFileInfo messageInfoFileInfo = new ReadFileInfo(xmlFile, xmlFile.lastModified());
			messageInfoFileInfoHash.put(messageID, messageInfoFileInfo);
			messageInfoHash.put(messageID, messageInfo);

			log.info(String.format("project[%s] 메시지[%s] 정보를 담고 있는 XML로 작성된 파일 로딩 성공", projectName, messageID));
			// System.out.printf("메시지 식별자[%s] 등록\n", messageID);
			// System.out.printf("=================================================\n%s\n=================================================\n",
			// messageInfo.toString());

		} else {
			log.info(String.format("project[%s] 메시지 정보 파일[%s] 로딩 실패", projectName, xmlFile.getAbsolutePath()));
		}
		return messageInfo;
	}

	/**
	 * 메시지 정보를 반환한다.
	 * 
	 * @param messageID
	 *            messageID 받고자 하는 메시지 정보의 메시지 식별자
	 * @return 지정된 메시지 식별자에 대응하는 메시지 정보, 대응하는 메시지 정보가 없다면 null 을 반환한다.
	 * @throws IllegalArgumentException
	 *             파라미터 입력이 null 이거나 메시지 식별자 이름 규칙을 위반할때 발생합니다.
	 */
	private MessageInfo getMessageInfo(String messageID)
			throws IllegalArgumentException {
		if (null == messageID) {
			String errorMessage = "파라미터 메시지 식별자가 null 입니다.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		String projectName = projectConfig.getProjectName();

		if (!DHBMessageHeader.IsValidMessageID(messageID)) {
			IllegalArgumentException e = new IllegalArgumentException(
					String.format("파라미터 메시지 식별자[%s]는 메시지 식별자 이름 규칙을 위반 하였습니다.",
							messageID));
			/** Throwable 객체 생성은 리소스를 많이 잡아 먹지만 잘못된 메시지 식별자를 넣은 소스를 추적하여 제거 하기 위한 부득이한 조취임 */
			log.warn(String.format("project[%s] IllegalArgumentException in getMessageInfo", projectName), e);
			throw e;
		}

		MessageInfo messageInfo = null;

		//synchronized (messageInfoHash) {
			messageInfo = messageInfoHash.get(messageID);

			if (null == messageInfo) {
				// log.info("11111.messageInfoXMLPath.getAbsolutePath=[%s]",
				// messageInfoXMLPath.getAbsolutePath());

				StringBuffer xmlFileName = new StringBuffer(
						messageInfoPath.getAbsolutePath());
				xmlFileName.append(File.separator);
				xmlFileName.append(messageID);
				xmlFileName.append(".xml");

				// FIXME!
				// log.info("미 등록된 메시지 식별자에 대응하는 메시지 정보 파일 이름=[%s]",
				// xmlFileName.toString());

				File xmlFile = new File(xmlFileName.toString());
				if (!xmlFile.exists()) {
					log.warn(String.format("project[%s] warning :: not exist file , file name=[%s]",
							projectName, xmlFile.getAbsolutePath()));
					return null;
				}

				if (!xmlFile.isFile()) {
					log.warn(String.format("project[%s] warning :: not file , file name=[%s]",
							projectName, xmlFile.getAbsolutePath()));
					return null;
				}

				if (!xmlFile.canRead()) {
					log.warn(String.format("project[%s] warning :: can't read, file name=[%s]",
							projectName, xmlFile.getAbsolutePath()));
					return null;
				}

				/** 신규 메시지 정보 파일 추가 */
				messageInfo = readMessageXMLFile(xmlFile);
			} else {
				/** 최근 수정되었다면 재 로딩한다. */

				ReadFileInfo messageInfoFileInfo = messageInfoFileInfoHash.get(messageID);
				File workedFile = (File)messageInfoFileInfo.resultObject;
				long lastModified = workedFile.lastModified();
				if (lastModified > messageInfoFileInfo.lastModified) {
					messageInfoHash.remove(messageID);
					messageInfo = readMessageXMLFile(workedFile);
				}
			}
		//}

		return messageInfo;
	}
	

	@Override
	public InputMessage createInputMessage(String messageID) throws MessageInfoNotFoundException, IllegalArgumentException {
		MessageInfo messageInfo = getMessageInfo(messageID);
		if (null == messageInfo) {
			String errorMessage = String.format(
					"메시지 식별자[%s]에 대응하는 메시지 정보가 존재하지 않거나 잘못된 메시지 정보 파일입니다.", messageID);
			throw new MessageInfoNotFoundException(errorMessage);
		}
		return new InputMessage(messageID, messageInfo);
	}
	
	@Override
	public OutputMessage createOutputMessage(String messageID) throws MessageInfoNotFoundException, IllegalArgumentException {
		MessageInfo messageInfo = getMessageInfo(messageID);
		if (null == messageInfo) {
			String errorMessage = String.format(
					"메시지 식별자[%s]에 대응하는 메시지 정보가 존재하지 않거나 잘못된 메시지 정보 파일입니다.", messageID);
			throw new MessageInfoNotFoundException(errorMessage);
		}
		return new OutputMessage(messageID, messageInfo);
	}
	
	
	@Override
	public WrapBuffer pollDataPacketBuffer(ByteOrder newByteOrder) throws NoMoreDataPacketBufferException {
		WrapBuffer buffer = dataPacketBufferQueue.poll();
		if (null == buffer) {
			String errorMessage = String.format("클라이언트 프로젝트[%s]에서 데이터 패킷 버퍼 큐가 부족합니다.", projectConfig.getProjectName());
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		
		buffer.queueOut();
		buffer.getByteBuffer().order(newByteOrder);
		
		return buffer;
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer buffer) {
		if (null == buffer)
			return;

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (dataPacketBufferQueueMonitor) {
			if (buffer.isInQueue()) {
				log.warn(String.format("project[%s] 데이터 패킷 버퍼 2번 연속 반환 시도", projectConfig.getProjectName()));
				return;
			}
			buffer.queueIn();
		}

		dataPacketBufferQueue.add(buffer);
	}
	
	@Override
	public int getDataPacketBufferMaxCntPerMessage() {
		return projectConfig.getDataPacketBufferMaxCntPerMessage();
	}
	
	@Override
	public int getDataPacketBufferSize() {
		return projectConfig.getDataPacketBufferSize();
	}
	
	@Override
	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("DataPacketBufferQueue size=[");
		strBuilder.append(dataPacketBufferQueue.size());
		strBuilder.append("]");
		
		return strBuilder.toString();
	}
	
	
}
