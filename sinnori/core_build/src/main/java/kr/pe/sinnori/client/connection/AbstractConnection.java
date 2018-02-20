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

package kr.pe.sinnori.client.connection;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;

/**
 * 클라이언트 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 참고) 연결 클래스는 블락킹 모드에 따라서 그리고 공유 여부에 따라서 종류가 나뉘어 진다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractConnection {
	protected Logger log = LoggerFactory.getLogger(AbstractConnection.class);
	
	/** 모니터 전용 오브젝트 */
	protected final Object monitor = new Object();
	
	protected String projectName = null;
	protected String host = null;
	protected int port;
	protected long socketTimeOut;
	protected boolean whetherToAutoConnect;
	
	protected SocketChannel serverSC = null;
	
	protected  MessageProtocolIF messageProtocol = null;
	protected ClientObjectCacheManagerIF clientObjectCacheManager = null;
	
	
	protected java.util.Date finalReadTime = new java.util.Date();
	protected SelectableChannel serverSelectableChannel = null;
	
	public AbstractConnection(String projectName,
			String host,
			int port,
			long socketTimeOut,
			boolean whetherToAutoConnect,
			MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.socketTimeOut = socketTimeOut;
		this.whetherToAutoConnect = whetherToAutoConnect;
		this.messageProtocol = messageProtocol;		
		this.clientObjectCacheManager = clientObjectCacheManager;
		
		openSocketChannel();
		
		doConnect();
	}
	
	abstract protected void openSocketChannel() throws IOException;
	abstract protected void doConnect() throws IOException;
	abstract public void releaseSocketResources();
	
	public void closeSocket() throws IOException {
		serverSC.close();
	}

	public boolean isConnected() {
		return serverSC.isConnected();
	}
	
	
	public boolean isBlocking() {
		return serverSelectableChannel.isBlocking();
	}
		

	/**
	 * @return 프로젝트 이름
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * @return 소켓 타임 아웃
	 */
	public final long getSocketTimeOut() {
		return socketTimeOut;
	}
		

	/**
	 * 마지막으로 읽은 시간을 반환한다.
	 * 
	 * @return 마지막으로 읽은 시간
	 */
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	/**
	 * 마지막으로 읽은 시간을 새롭게 설정한다.
	 */

	public void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	
	/*public int getEchoMesgCount() {
		return echoMesgCount;
	}*/
	
	
	
	
	
	/*public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}
		
		if (newServerHost.equals(this.hostOfProject) && newServerPort == this.portOfProject) {
			*//** if the new address is same to the old address then nothing *//*
			return;
		}
		
		if (serverSC != null && serverSC.isConnected()) {
			String errorMessage = String.format("this client cann't change new server address[host:%s,port:%s] becase this client is connected", 
					newServerHost, newServerPort);
			throw new NotSupportedException(errorMessage);
		}
		this.hostOfProject = newServerHost;
		this.portOfProject = newServerPort;
	}*/
	
	
	// @Override
	/*public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append(projectName);
		strBuffer.append("index=[");
		strBuffer.append(index);
		strBuffer.append("], finalReadTime=[");
		strBuffer.append(finalReadTime);
		strBuffer.append("]");

		return strBuffer.toString();
	}
	*/
	/*public String getSimpleConnectionInfo() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(projectName);
		strBuffer.append("], connection=[");
		strBuffer.append(index);
		strBuffer.append("]");
		return strBuffer.toString();
	}*/
	
	protected AbstractMessage buildOutputMessage(ClassLoader classLoader, WrapReadableMiddleObject wrapReadableMiddleObject)
			throws DynamicClassCallException, BodyFormatException, NoMoreDataPacketBufferException, ServerTaskException,
			AccessDeniedException, IOException {
		String messageID = wrapReadableMiddleObject.getMessageID();
		int mailboxID = wrapReadableMiddleObject.getMailboxID();
		int mailID = wrapReadableMiddleObject.getMailID();
		Object middleReadObj = wrapReadableMiddleObject.getReadableMiddleObject();

		MessageCodecIF messageCodec = clientObjectCacheManager.getClientMessageCodec(classLoader, messageID);

		AbstractMessageDecoder messageDecoder = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get the client message codec of the output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("]").toString();
			
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknwon error::fail to get the client message codec of the output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("]::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		AbstractMessage messageObj = null;
		try {
			messageObj = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), middleReadObj);
			messageObj.messageHeaderInfo.mailboxID = mailboxID;
			messageObj.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("] from readable middle object").toString();
			
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);	
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("unknwon error::fail to get a output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation())
					.append("] from readable middle object::").append(e.getMessage()).toString();
			
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}

		if (messageObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) messageObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}

		return messageObj;
	}

	protected List<WrapBuffer> buildReadableWrapBufferList(ClassLoader classLoader, AbstractMessage inputMessage) 
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException {
		MessageCodecIF messageCodec = null;

		try {
			messageCodec = clientObjectCacheManager.getClientMessageCodec(classLoader, inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a client input message codec::").append(e.getMessage()).toString();
			
			log.warn(errorMessage);

			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a client input message codec::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		}

		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get a input message encoder::").append(e.getMessage()).toString();
			log.warn(errorMessage);
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		List<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder("fail to build a input message stream[")
					.append(inputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage);
			
			throw e;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to build a input message stream[")
					.append(inputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage);
			
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to build a input message stream[")
					.append(inputMessage.getMessageID())
					.append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}

		return wrapBufferList;
	}
}
