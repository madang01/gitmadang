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

package kr.pe.sinnori.server.executor;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class LetterCarrier {
	private Logger log = LoggerFactory.getLogger(LetterCarrier.class);
	
	private SocketChannel fromSC = null;
	private AbstractMessage inputMessage;
	private SocketResource clientResource  = null;
	private OutputMessageWriterIF outputMessageWriter = null;
	private MessageProtocolIF messageProtocol = null;
	private ClassLoader classLoaderOfSererTask = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;	
	
	private ArrayList<ToLetter> asynToLetterList = new ArrayList<ToLetter>();
	
	public LetterCarrier( SocketChannel fromSC, 
			AbstractMessage inputMessage,
			MessageProtocolIF messageProtocol,
			ClassLoader classLoaderOfSererTask,
			ServerObjectCacheManagerIF serverObjectCacheManager,
			OutputMessageWriterIF outputMessageWriter) {
		this.fromSC = fromSC;
		this.inputMessage = inputMessage;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
		this.outputMessageWriter = outputMessageWriter;
	}
	
	
	public void addSyncMessage(AbstractMessage outputMessage) {
		outputMessage.messageHeaderInfo = inputMessage.messageHeaderInfo;
		
		if (asynToLetterList.size() > 0) {
			log.warn("동기 메시지는 1개만 가질 수 있습니다.  추가 취소된 메시지={}", outputMessage.toString());
			return;
		}
		
		ToLetter toLetter = buildToLetter(fromSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			asynToLetterList.add(toLetter);
		}
	}
	
	public void addAsynMessage(AbstractMessage outputMessage) {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		ToLetter toLetter = buildToLetter(fromSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			asynToLetterList.add(toLetter);
		}
	}
	
	public void addAsynMessage(AbstractMessage outputMessage, SocketChannel toSC) {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		ToLetter toLetter = buildToLetter(toSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			asynToLetterList.add(toLetter);
		}
	}
	
	public void putAsynOutputMessageToOutputMessageWriter(AbstractMessage outputMessage, SocketChannel toSC) {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		ToLetter toLetter = buildToLetter(toSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e) {
				try {
					outputMessageWriter.putIntoQueue(toLetter);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), inputMessage.toString(), toLetter.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	public void putAsynOutputMessageToOutputMessageWriter(AbstractMessage outputMessage) {
		outputMessage.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		outputMessage.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		ToLetter toLetter = buildToLetter(fromSC, outputMessage, messageProtocol, outputMessageWriter);
		if (null != toLetter) {
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e) {
				try {
					outputMessageWriter.putIntoQueue(toLetter);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), inputMessage.toString(), toLetter.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	public void putAsynToLetterListToOutputMessageWriter() {
		for (ToLetter toLetter : asynToLetterList) {
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e) {
				try {
					outputMessageWriter.putIntoQueue(toLetter);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), inputMessage.toString(), toLetter.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	private void sendSelfExnToClient(SocketChannel sc, int mailboxID, int mailID, String messageID,
			String errorGuubun, 
			String errorMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) {
		
		
		SelfExn selfExnOutObj = new SelfExn();
		selfExnOutObj.messageHeaderInfo.mailboxID = mailboxID;
		selfExnOutObj.messageHeaderInfo.mailID = mailID;

		selfExnOutObj.setErrorPlace("S");
		selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));

		selfExnOutObj.setErrorMessageID(messageID);
		selfExnOutObj.setErrorMessage(errorMessage);

		List<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
		} catch (Exception e1) {
			log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, sc={}, SelfExn={}", sc.hashCode(),
					selfExnOutObj.toString());
			System.exit(1);
		}

		ToLetter toLetter = new ToLetter(sc, 
				selfExnOutObj.getMessageID(),
				selfExnOutObj.messageHeaderInfo.mailboxID,
				selfExnOutObj.messageHeaderInfo.mailID, wrapBufferList);
		try {
			outputMessageWriter.putIntoQueue(toLetter);
		} catch (InterruptedException e) {
			try {
				outputMessageWriter.putIntoQueue(toLetter);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, toSC hashCode=[{}], 전달 못한 송신 메시지=[{}]",
						sc.hashCode(), selfExnOutObj.toString());
				Thread.interrupted();
			}
		}
	}
	
	
	public ToLetter buildToLetter(SocketChannel toSC,
			AbstractMessage outputMessage, 
			MessageProtocolIF messageProtocol,
			OutputMessageWriterIF outputMessageWriter) {
		String messageIDToClient = outputMessage.getMessageID();

		List<WrapBuffer> wrapBufferList = null;		
		
		MessageCodecIF messageCodec = null;
		try {
			messageCodec = serverObjectCacheManager.getServerCodec(classLoaderOfSererTask, messageIDToClient);
		} catch (DynamicClassCallException e) {
			
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			
			
			return null;
		} catch (Exception e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get the mesage codec::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		}

		AbstractMessageEncoder messageEncoder = null;
		try {
			messageEncoder = messageCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		} catch (Exception e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class),
					"fail to get the mesage encoder::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
			return null;
		}


		/*log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
				classLoaderOfSererTask.hashCode(), inputMessageID, messageIDToClient);*/

		try {
			wrapBufferList = messageProtocol.M2S(outputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(NoMoreDataPacketBufferException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;		
		} catch (BodyFormatException e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;
		} catch (Exception e) {
			sendSelfExnToClient(toSC, 
					outputMessage.messageHeaderInfo.mailboxID,
					outputMessage.messageHeaderInfo.mailID,
					outputMessage.getMessageID(),
					SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class),
					"fail to output message stream::"+e.getMessage(),
					messageProtocol,
					outputMessageWriter);
				return null;
		}
		
		ToLetter toLetter = new ToLetter(toSC, 
				outputMessage.getMessageID(),
				outputMessage.messageHeaderInfo.mailboxID,
				outputMessage.messageHeaderInfo.mailID, wrapBufferList);

		return toLetter;
	}

	
	/*public ArrayList<LetterToClient> getMessageToClientList() {
		return letterToClientList;
	}*/
	
	/**
	 * 전체 목록 삭제. 주의점) 로그 없다. 만약 로그 필요시 {@link #writeLog(String) } 호출할것
	 */
	public void clearMessageToClientList() {
		asynToLetterList.clear();
	}

	/**
	 * @return 입력 메시지를 보낸 클라이언트의 자원
	 */
	public SocketResource getClientResource() {
		return clientResource;
	}
	
	public void writeLogAll(String title) {
		int i=0;
		for (ToLetter toLetter : asynToLetterList) {
			log.info("%::전체삭제-잔존 메시지[{}]=[{}]", i++, toLetter.toString());
		}
	}
}
