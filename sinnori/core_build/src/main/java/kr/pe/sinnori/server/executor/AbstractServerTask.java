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
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
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
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * <pre>
 * 로그인을 요구하지 않는 서버 비지니스 로직 부모 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractServerTask {
	protected Logger log = LoggerFactory.getLogger(AbstractServerTask.class);
	protected final ClassLoader classLoader = this.getClass().getClassLoader();
	private java.util.Hashtable<String, AbstractMessageEncoder> encoderHash = new java.util.Hashtable<String, AbstractMessageEncoder>();
	private java.util.Hashtable<String, AbstractMessageDecoder> decoderHash = new java.util.Hashtable<String, AbstractMessageDecoder>(
			1);

	/**
	 * Executor 에서 호출되는 메소드로 비지니스 로직 수행을 포함한 비지니스 로직 전후 작업을 수행한다.
	 * 
	 * @param index
	 *            순번
	 * @param ouputMessageQueue
	 *            출력 메시지 큐
	 * @param messageProtocol
	 *            서버 프로젝트의 메시지 프로토콜
	 * @param fromSC
	 *            입력 메시지를 보낸 클라이언트
	 * @param clientResource
	 *            클라이언트 자원
	 * @param wrapReadableMiddleObject
	 *            수신 편지
	 * @param loginManager
	 *            로그인 관리자
	 * @param serverObjectCacheManager
	 *            서버 객체 캐쉬 관리자
	 */
	public void execute(int index, String projectName, Charset charsetOfProject,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue, MessageProtocolIF messageProtocol,
			SocketChannel fromSC, ClientResource clientResource, WrapReadableMiddleObject wrapReadableMiddleObject,
			LoginManagerIF loginManager, ServerObjectCacheManagerIF serverObjectCacheManager) {
		// FIXME!
		// log.info("inputMessage=[%s]", inputMessage.toString());
		// long firstErraseTime = new java.util.Date().getTime();

		// CharsetEncoder charsetEncoderOfProject =
		// CharsetUtil.createCharsetEncoder(charsetOfProject);

		String messageIDFromClient = wrapReadableMiddleObject.getMessageID();

		// Charset projectCharset = serverProjectConfig.getCharset();

		AbstractMessageDecoder messageDecoder = decoderHash.get(messageIDFromClient);
		AbstractMessage messageFromClient = null;

		if (null == messageDecoder) {
			MessageCodecIF messageCodec = null;

			try {
				messageCodec = serverObjectCacheManager.getServerCodec(classLoader, messageIDFromClient);
			} catch (DynamicClassCallException e) {
				log.warn(e.getMessage());

				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();

				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));

				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage(e.getMessage());

				List<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
							selfExnOutObj.toString());
					System.exit(1);
				}

				putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
						ouputMessageQueue);
				return;
			} catch (Exception e) {
				log.warn(e.getMessage(), e);

				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
				// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는
				// 에러 발생::"+e.getMessage()));
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage("메시지 서버 코덱을 얻을때 알수 없는 에러 발생::" + e.getMessage());

				List<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

					putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
							ouputMessageQueue);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
							selfExnOutObj.toString());
				}
				return;
			}

			try {
				messageDecoder = messageCodec.getMessageDecoder();
			} catch (DynamicClassCallException e) {
				log.warn(e.getMessage());

				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
				// selfExnOutObj.setError("S", messageID, new
				// DynamicClassCallException(e.getMessage()));
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage(e.getMessage());

				List<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
					putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
							ouputMessageQueue);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
							selfExnOutObj.toString());
				}
				return;
			} catch (Exception e) {
				log.warn(e.getMessage());

				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage("메시지 디코더를 얻을때 알수 없는 에러 발생::" + e.getMessage());

				List<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
					putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
							ouputMessageQueue);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
							selfExnOutObj.toString());
				}
				return;
			}

			decoderHash.put(messageIDFromClient, messageDecoder);

			log.info("classLoader[{}], serverTask[{}], create new messageDecoder", classLoader.hashCode(),
					messageIDFromClient);
		}

		try {
			messageFromClient = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), charsetOfProject,
					wrapReadableMiddleObject.getReadableMiddleObject());
			messageFromClient.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			messageFromClient.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
		} catch (BodyFormatException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class));
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(e.getMessage());

			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

				putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
						ouputMessageQueue);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
						selfExnOutObj.toString());
			}
			return;
		} catch (OutOfMemoryError e) {
			log.warn(e.getMessage());

			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class));
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(new StringBuilder("OutOfMemoryError::").append(e.getMessage()).toString());

			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

				putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
						ouputMessageQueue);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
						selfExnOutObj.toString());
			}
			return;
		} catch (Exception e) {
			log.warn(e.getMessage());

			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class));
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(
					new StringBuilder("메시지를 디코딩하여 추출할때 알수 없는 에러 발생::").append(e.getMessage()).toString());

			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

				putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList,
						ouputMessageQueue);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
						selfExnOutObj.toString());
			}
			return;
		}

		// messageProtocol, projectCharset
		LetterSender letterSender = new LetterSender(this, clientResource, messageFromClient, charsetOfProject,
				ouputMessageQueue, messageProtocol, serverObjectCacheManager);

		try {
			doTask(projectName, loginManager, letterSender, messageFromClient);
		} catch (java.lang.Error e) {
			// FIXME!
			log.warn("1.unknown error", e);

			String errorMessgae = e.getMessage();
			if (null == errorMessgae) {
				errorMessgae = "1.서비 비지니스 로직 실행시 에러 발생";
			} else {
				errorMessgae = new StringBuilder("1.서비 비지니스 로직 실행시 에러 발생::").append(errorMessgae).toString();
			}

			log.warn(String.format("1.%s Executor[%d], fromSC[%d], messageFromClient[%s], %s", projectName, index,
					fromSC.hashCode(), messageFromClient.toString(), errorMessgae), e);

			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageFromClient.messageHeaderInfo;
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class));
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(errorMessgae);

			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

				putToOutputMessageQueue(fromSC, messageFromClient, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch (Exception e1) {
				log.error("1.시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
						selfExnOutObj.toString());
			}

			/**
			 * FIXME! 서버 타스크 수행중 받은 편지들 로그 남기기, 삭제할 필요는 없어 삭제는 하지 않음.
			 */
			letterSender.writeLogAll("1.서버 타스크 수행중 에러");
			return;
		} catch (Exception e) {
			// FIXME!
			log.warn("2.unknown error", e);

			String errorMessgae = e.getMessage();
			if (null == errorMessgae) {
				errorMessgae = "2.서비 비지니스 로직 실행시 에러 발생";
			} else {
				errorMessgae = new StringBuilder("2.서비 비지니스 로직 실행시 에러 발생::").append(errorMessgae).toString();
			}

			log.warn(String.format("2.%s Executor[%d], fromSC[%d], messageFromClient[%s], %s", projectName, index,
					fromSC.hashCode(), messageFromClient.toString(), errorMessgae), e);

			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageFromClient.messageHeaderInfo;
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class));
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(errorMessgae);

			List<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);

				putToOutputMessageQueue(fromSC, messageFromClient, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch (Exception e1) {
				log.error("2.시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(),
						selfExnOutObj.toString());
			}

			/**
			 * FIXME! 서버 타스크 수행중 받은 편지들 로그 남기기, 삭제할 필요는 없어 삭제는 하지 않음.
			 */
			letterSender.writeLogAll("2.서버 타스크 수행중 에러");
			return;
		}

		letterSender.directSendLetterToClientList();

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}

	public List<WrapBuffer> getMessageStream(String messageIDFromClient, SocketChannel toSC,
			AbstractMessage messageToClient, Charset charsetOfProject, MessageProtocolIF messageProtocol,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		String messageIDToClient = messageToClient.getMessageID();

		List<WrapBuffer> wrapBufferList = null;

		AbstractMessageEncoder messageEncoder = encoderHash.get(messageIDToClient);

		// CharsetEncoder charsetEncoderOfProject =
		// CharsetUtil.createCharsetEncoder(charsetOfProject);

		if (null == messageEncoder) {
			MessageCodecIF messageCodec = null;
			try {
				messageCodec = serverObjectCacheManager.getServerCodec(classLoader, messageIDToClient);
			} catch (DynamicClassCallException e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());

				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
							selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			} catch (Exception e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());

				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
							selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			}

			try {
				messageEncoder = messageCodec.getMessageEncoder();
			} catch (DynamicClassCallException e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
							selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			} catch (Exception e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorPlace("S");
				selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());

				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
				} catch (Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
							selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			}

			encoderHash.put(messageIDToClient, messageEncoder);

			log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}",
					classLoader.hashCode(), messageIDFromClient, messageIDToClient);
		}

		try {
			wrapBufferList = messageProtocol.M2S(messageToClient, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(NoMoreDataPacketBufferException.class));
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage(e.getMessage());
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
						selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;		
		} catch (BodyFormatException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class));
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage(e.getMessage());

			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
						selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		} catch (Exception e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorPlace("S");
			selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(BodyFormatException.class));
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage("unknown error::" + e.getMessage());

			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
			} catch (Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(),
						selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		}

		return wrapBufferList;
	}

	private void putToOutputMessageQueue(SocketChannel clientSC, AbstractMessage messageFromClient,
			AbstractMessage wrapBufferMessage, List<WrapBuffer> wrapBufferList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {

		// wrapBufferMessage.messageHeaderInfo = messageFromClient.messageHeaderInfo;

		LetterToClient letterToClient = new LetterToClient(clientSC, wrapBufferMessage, wrapBufferList);
		try {
			ouputMessageQueue.put(letterToClient);
		} catch (InterruptedException e) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]",
						clientSC.hashCode(), messageFromClient.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}

	private void putToOutputMessageQueue(SocketChannel clientSC, WrapReadableMiddleObject wrapReadableMiddleObject,
			AbstractMessage wrapBufferMessage, List<WrapBuffer> wrapBufferList,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {

		/*
		 * wrapBufferMessage.messageHeaderInfo.mailboxID =
		 * receivedLetter.getMailboxID(); wrapBufferMessage.messageHeaderInfo.mailID =
		 * receivedLetter.getMailID();
		 */

		LetterToClient letterToClient = new LetterToClient(clientSC, wrapBufferMessage, wrapBufferList);
		try {
			ouputMessageQueue.put(letterToClient);
		} catch (InterruptedException e) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], 입력 메시지[{}] 추출 실패, 전달 못한 송신 메시지=[{}]",
						clientSC.hashCode(), wrapReadableMiddleObject.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}

	/**
	 * 출력메시지 직접 전송하는 개발자가 직접 작성해야할 비지니스 로직
	 * 
	 * @param projectName
	 *            프로젝트 이름
	 * @param loginManager
	 *            로그인 관리자
	 * @param letterSender
	 *            클라이언트로 보내는 편지 배달부
	 * @param requestMessage
	 *            요청 메시지
	 * @throws Exception
	 *             에러 발생시 던지는 예외
	 */
	abstract public void doTask(String projectName, LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage requestMessage) throws Exception;
}
