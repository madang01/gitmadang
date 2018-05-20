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

package kr.pe.codda.server;

import java.nio.channels.SocketChannel;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;
import kr.pe.codda.server.threadpool.outputmessage.OutputMessageWriterIF;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SocketResource {
	@SuppressWarnings("unused")
	private InternalLogger log = InternalLoggerFactory.getInstance(SocketResource.class);

	private SocketChannel ownerSC = null;
	private ServerExecutorIF executor = null;
	private OutputMessageWriterIF outputMessageWriter = null;
	private SocketOutputStream socketOutputStream = null;
	private PersonalLoginManagerIF personalLoginManager = null;
	
	
	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;
	
	private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;
		
	public SocketResource(SocketChannel ownerSC,
			ServerExecutorIF executorOfOwnerSC,
			OutputMessageWriterIF outputMessageWriterOfOwnerSC,
			SocketOutputStream socketOutputStreamOfOwnerSC,
			PersonalLoginManagerIF personalLoginManagerOfOwnerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}


		if (null == executorOfOwnerSC) {
			throw new IllegalArgumentException("the parameter executorOfOwnerSC is null");
		}
		
		if (null == outputMessageWriterOfOwnerSC) {
			throw new IllegalArgumentException("the parameter outputMessageWriterOfOwnerSC is null");
		}
		
		if (null == socketOutputStreamOfOwnerSC) {
			throw new IllegalArgumentException("the parameter socketOutputStreamOfOwnerSC is null");
		}
		
		if (null == personalLoginManagerOfOwnerSC) {
			throw new IllegalArgumentException("the parameter personalLoginManagerOfOwnerSC is null");
		}		
		
		this.ownerSC = ownerSC;
		this.executor = executorOfOwnerSC;
		this.outputMessageWriter = outputMessageWriterOfOwnerSC;
		this.socketOutputStream = socketOutputStreamOfOwnerSC;
		this.personalLoginManager = personalLoginManagerOfOwnerSC;
		
		finalReadTime = new java.util.Date();
	}
	
	public SocketChannel getOwnerSC() {
		return ownerSC;
	}
	
	
	public ServerExecutorIF getExecutor() {
		return executor;
	}

	public OutputMessageWriterIF getOutputMessageWriter() {
		return outputMessageWriter;
	}
	
	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	public SocketOutputStream getSocketOutputStream() {
		
		return socketOutputStream;
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

	/**
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		synchronized (monitorOfServerMailID) {
			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return serverMailID;
		}
	}
	
	
	
	
	public PersonalLoginManagerIF getPersonalLoginManager() {
		return personalLoginManager;
	}
	
	public void close() {
		socketOutputStream.close();
		personalLoginManager.releaseLoginUserResource();

		/**
		 * 참고 : '메시지 입력 담당 쓰레드'(=InputMessageReader) 는 소켓이 닫히면 자동적으로 selector 에서 인지하여 제거되므로 따로 작업할 필요 없음
		 */
		executor.removeSocket(ownerSC);
		outputMessageWriter.removeSocket(ownerSC);
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientResource [");		
		builder.append("ownerSC=");
		builder.append(ownerSC.hashCode());
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);		
		builder.append(", serverMailID=");
		builder.append(serverMailID);
		builder.append("]");
		return builder.toString();
	}
}
