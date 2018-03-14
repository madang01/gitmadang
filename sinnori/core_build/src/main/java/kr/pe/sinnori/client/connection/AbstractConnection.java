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
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

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
	protected ClientMessageUtilityIF clientMessageUtility = null;
	
	
	protected SocketChannel serverSC = null;
	protected java.util.Date finalReadTime = new java.util.Date();
	protected SelectableChannel serverSelectableChannel = null;
	
	public AbstractConnection(ConnectionFixedParameter connectionFixedParameter) throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		if (null == connectionFixedParameter) {
			throw new IllegalArgumentException("the parameter connectionFixedParameter is null");
		}
		this.projectName = connectionFixedParameter.getProjectName();
		this.host = connectionFixedParameter.getHost();
		this.port = connectionFixedParameter.getPort();
		this.socketTimeOut = connectionFixedParameter.getSocketTimeOut();
		this.clientMessageUtility = connectionFixedParameter.getClientMessageUtility();	
		
		openSocketChannel();
		
		doConnect();
	}
	
	private void openSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSelectableChannel = serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		serverSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);		
		//setReuseAddress
		
		//log.info("{} connection[{}] created", projectName, serverSC.hashCode());
	}
	
	
	private void doConnect() throws IOException {
		Selector connectionEventOnlySelector = Selector.open();

		try {
			serverSC.register(connectionEventOnlySelector, SelectionKey.OP_CONNECT);

			InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
			if (! serverSC.connect(remoteAddr)) {
				@SuppressWarnings("unused")
				int numberOfKeys = connectionEventOnlySelector.select(socketTimeOut);

				// log.info("numberOfKeys={}", numberOfKeys);

				Iterator<SelectionKey> selectionKeyIterator = connectionEventOnlySelector.selectedKeys().iterator();
				if (!selectionKeyIterator.hasNext()) {

					String errorMessage = String.format("1.the socket[sc hascode=%d] timeout", serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}

				SelectionKey selectionKey = selectionKeyIterator.next();
				selectionKey.cancel();

				if (!serverSC.finishConnect()) {
					String errorMessage = String.format("the socket[sc hascode=%d] has an error pending",
							serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}
			}
		} finally {
			connectionEventOnlySelector.close();
		}

		log.info("{} connection[{}] {}:{} connected", 
				projectName, serverSC.hashCode(),
				host, port);
	}
	/**
	 * {@link #close} 에서 사용되는 내부 메소드로 동기 성격의 연결에서는 실질적인 소켓 자원을 반환하지만  비동기 성격의 연결의 경우 아무것도 안한다.
	 */
	abstract protected void doReleaseSocketResources();		
	

	abstract public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws NoMoreDataPacketBufferException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, BodyFormatException, IOException;
	
	abstract public void sendAsynInputMessage(AbstractMessage inObj)
			throws NotSupportedException, NoMoreDataPacketBufferException, DynamicClassCallException,
			InterruptedException, BodyFormatException, HeaderFormatException;
	
	public SocketChannel getSocketChannel() {
		return serverSC;
	}
	
	/**
	 * <pre>
	 * 소켓을 닫고 할당한 자원을 반환한다. 
	 * 단 자원 반환은 비동기 성격의 연결은 따로 자원 반환을 처리하므로 따로 하는 일 없으며 
	 * 오직 동기 성격의 연결에서만 실질적으로 수행한다.
	 * </pre>       
	 * @throws IOException
	 */
	public void close() throws IOException {		
		serverSC.close();
		
		doReleaseSocketResources();
	}

	/** isOpen & isConnected */
	public boolean isConnected() {
		return serverSC.isOpen() && serverSC.isConnected();
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractConnection [projectName=");
		builder.append(projectName);
		builder.append(", host=");
		builder.append(host);
		builder.append(", port=");
		builder.append(port);
		builder.append(", socketTimeOut=");
		builder.append(socketTimeOut);
		builder.append(", serverSC=");
		builder.append(serverSC.hashCode());
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);
		builder.append("]");
		return builder.toString();
	}
	
	public String getSimpleConnectionInfo() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(projectName);
		strBuffer.append("], serverSC[");
		strBuffer.append(serverSC.hashCode());
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
