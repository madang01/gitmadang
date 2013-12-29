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
package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드(=핸들러)
 * 
 * @author Jonghoon Won
 * 
 */
public class InputMessageWriter extends Thread implements CommonRootIF {
	/** 입력 메시지 쓰기 쓰레드 번호 */
	private int index;
	private ClientProjectConfigIF clientProjectConfig = null;
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;
	
	private MessageExchangeProtocolIF messageProtocol = null;
	private MessageMangerIF messageManger = null;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	

	/**
	 * 생성자
	 * @param index 순번
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로프로콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 
	 */
	public InputMessageWriter(int index,
			ClientProjectConfigIF clientProjectConfig,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.index = index;
		this.clientProjectConfig = clientProjectConfig;
		this.inputMessageQueue = inputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
	}

	@Override
	public void run() {
		log.info(String.format("InputMessageWriter[%d] start", index));

		// ByteBuffer inputMessageWriteBuffer = inputMessageWrapBuffer.getByteBuffer();
		try {
			while (!Thread.currentThread().isInterrupted()) {
				LetterToServer letterToServer = null;
				try {
					letterToServer = inputMessageQueue.take();
				} catch (InterruptedException e) {
					log.warn(String.format("%s index[%d] stop", clientProjectConfig.getProjectName(), index), e);
					break;
				}
	
				// log.info("1. In InputMessageWriter, letter=[%s]",
				// letterToServer.toString());
	
				AbstractAsynConnection noneBlockConnection = letterToServer
						.getServerConnection();
				InputMessage inObj = letterToServer.getInputMessage();
	
				// SocketChannel toSC = noneBlockConnection.getSocketChannel();
				
				ByteOrder clientByteOrder = clientProjectConfig.getByteOrder();
				Charset clientCharset = clientProjectConfig.getCharset();
	
				
				
				// inputMessageWriteBuffer.clear();
				// inputMessageWriteBuffer.order(connByteOrder);
				
				ArrayList<WrapBuffer> inObjWrapBufferList = null;
				
				try {
					inObjWrapBufferList = messageProtocol.M2S(inObj, clientByteOrder, clientCharset);
					noneBlockConnection.write(inObjWrapBufferList);
				} catch (NoMoreDataPacketBufferException e) {
					log.warn(String.format("%s InputMessageWriter[%d] NoMoreDataPacketBufferException::%s, inObj=[%s]", noneBlockConnection.getSimpleConnectionInfo(), index, e.getMessage(), inObj.toString()), e);
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage("SelfExn");
					} catch (MessageInfoNotFoundException e1) {
						log.fatal(	"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					errorOutObj.messageHeaderInfo.mailboxID = inObj.messageHeaderInfo.mailboxID;
					errorOutObj.messageHeaderInfo.mailID = inObj.messageHeaderInfo.mailID;
					errorOutObj.setAttribute("whereError", "C");
					errorOutObj.setAttribute("errorGubun", "N");
					errorOutObj.setAttribute("errorMessageID",
							inObj.getMessageID());
					errorOutObj.setAttribute("errorMessage",
							e.getMessage());
	
					// LetterFromServer letterFromServer = new
					// LetterFromServer(errorOutObj);
	
					noneBlockConnection
							.putToOutputMessageQueue(errorOutObj);
				} catch (BodyFormatException e) {
					log.warn(String.format("%s InputMessageWriter[%d] BodyFormatException::%s, inObj=[%s]", noneBlockConnection.getSimpleConnectionInfo(), index, e.getMessage(), inObj.toString()), e);
					// log.warn("BodyFormatException", e);
					
					/**
					 * <pre>
					 * 에러를 던지지 않고 직접 SelfExn 메시지를 만드는 이유가 1가지 있다.
					 * (1) 비동기 소켓 채널의 메시지 교환은 입력 메시지를 발생한 지점과 떨어져 있어
					 *     SelfExn 메시지로 보내서 SelfExn 를 받아 보는 순간에 예외로 던지기 위함이다.
					 *     
					 * 참고) 이곳은 죽은 코드이지만 혹여 발생해도 정상 처리 흐름을 따르도록 하였다.
					 * 원본 메시지를 이곳까지 보내기전에 원본 메시지를 랩하는 과정에서 
					 * 먼저 BodyFormatException 가 발생되어 뒷단인 이곳까지 
					 * BodyFormatException을 발생시키는 원본 메시지가 전달되지 않기때문에 이곳 로직은 죽은 코드이다.
					 * </pre>
					 */
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage(
								"SelfExn");
					} catch (MessageInfoNotFoundException e1) {						
						log.fatal(	"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					errorOutObj.messageHeaderInfo.mailboxID = inObj.messageHeaderInfo.mailboxID;
					errorOutObj.messageHeaderInfo.mailID = inObj.messageHeaderInfo.mailID;
					errorOutObj.setAttribute("whereError", "C");
					errorOutObj.setAttribute("errorGubun", "B");
					errorOutObj.setAttribute("errorMessageID",
							inObj.getMessageID());
					errorOutObj.setAttribute("errorMessage",
							e.getMessage());
	
					// LetterFromServer letterFromServer = new
					// LetterFromServer(errorOutObj);
	
					noneBlockConnection
							.putToOutputMessageQueue(errorOutObj);
				
				} catch (NotYetConnectedException e) {
					log.warn(String.format("%s InputMessageWriter[%d] NotYetConnectedException::%s, inObj=[%s]", noneBlockConnection.getSimpleConnectionInfo(), index, e.getMessage(), inObj.toString()), e);
					
					noneBlockConnection.serverClose();
					
				} catch(ClosedByInterruptException e) {
					log.warn(String.format("%s InputMessageWriter[%d] ClosedByInterruptException::%s, inObj=[%s]", noneBlockConnection.getSimpleConnectionInfo(), index, e.getMessage(), inObj.toString()), e);
					
					noneBlockConnection.serverClose();
				} catch (IOException e) {
					log.warn(String.format("%s InputMessageWriter[%d] IOException::%s, inObj=[%s]", noneBlockConnection.getSimpleConnectionInfo(), index, e.getMessage(), inObj.toString()), e);
					
					noneBlockConnection.serverClose();
				} finally {
					if (null != inObjWrapBufferList) {
						int bodyWrapBufferListSiz = inObjWrapBufferList.size();
						for (int i=0; i < bodyWrapBufferListSiz; i++) {
							WrapBuffer wrapBuffer = inObjWrapBufferList.get(0);
							inObjWrapBufferList.remove(0);
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}
					}
				}			
			}
			
			log.warn(String.format("%s InputMessageWriter[%d] loop exit", clientProjectConfig.getProjectName(), index));
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageWriter[%d] unknown error::%s", clientProjectConfig.getProjectName(), index, e.getMessage()), e);
		}

		log.warn(String.format("%s InputMessageWriter[%d] thread end", clientProjectConfig.getProjectName(), index));
	}

	public void finalize() {
		log.warn(String.format("%s InputMessageWriter[%d] 소멸::[%s]", clientProjectConfig.getProjectName(), index, toString()));
	}
}
