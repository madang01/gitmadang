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
package kr.pe.sinnori.client.connection.asyn.share;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.ClientOutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.AsynServerAdderIF;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MailboxTimeoutException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

/**
 * 클라이언트 공유 방식의 비동기 연결 클래스.<br/>
 * 참고) 공유 방식은 목록으로 관리되며 순차적으로 공유 방식의 비동기 연결 클래스를 배정한다. <br/>
 * 이렇게 배정 받은 공유 방식의 비동기 연결 클래스는 메시지 송수신 순간에 <br/>
 * 메일함 큐에서 메일함을 할당받아 메일함을 통해서 메시지 교환을 수행한다.<br/>
 * 자세한 내용은 기술 문서를 참고 하세요.<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @see AsynPrivateMailbox
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnection extends AbstractAsynConnection {
	/** 개인 메일함 큐 */
	private LinkedBlockingQueue<AsynPrivateMailbox> PrivateMailboxWaitingQueue = null;
	/** 메일 식별자를 키로 활성화된 메일함을 값으로 가지는 해쉬 */
	private Map<Integer, AsynPrivateMailbox> privateMailboxMap = new Hashtable<Integer, AsynPrivateMailbox>();

	private long connectionTimeout;
	// private boolean isFailToGetMailbox = false;

	/**
	 * 생성자
	 * 
	 * @param index
	 *            연결 클래스 번호
	 * @param socketTimeOut
	 *            소켓 타임 아웃 시간
	 * @param whetherToAutoConnect
	 *            자동 접속 여부
	 * @param finishConnectMaxCall
	 *            비동기 방식에서 연결 확립 시도 최대 호출 횟수
	 * @param finishConnectWaittingTime
	 *            비동기 연결 확립 시도 간격
	 * @param mailBoxCnt
	 *            메일 박스 갯수
	 * @param projectPart
	 *            프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param asynOutputMessageQueue
	 *            서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @param inputMessageQueue
	 *            입력 메시지 큐
	 * @param outputMessageQueueQueueManager
	 *            출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @param outputMessageReaderPool
	 *            서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param messageManger
	 *            메시지 관리자
	 * @param dataPacketBufferQueueManager
	 *            데이터 패킷 버퍼 큐 관리자
	 * @throws InterruptedException
	 *             쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 부족시 실패시 던지는 예외
	 * @throws NoMoreOutputMessageQueueException
	 *             출력 메시지 큐 부족시 실패시 던지는 예외
	 */
	public ShareAsynConnection(String projectName, int index, String hostOfProject, int portOfProject,
			Charset charsetOfProject, long connectionTimeout, long socketTimeOut, boolean whetherToAutoConnect, int finishConnectMaxCall,
			long finishConnectWaittingTime, int mailBoxCnt, 
			LinkedBlockingQueue<WrapReadableMiddleObject> asynOutputMessageQueue,
			LinkedBlockingQueue<ToLetter> inputMessageQueue, MessageProtocolIF messageProtocol,
			AsynServerAdderIF outputMessageReaderPool,
			ClientOutputMessageQueueQueueMangerIF outputMessageQueueQueueManager,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException, NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException {
		super(projectName, index, hostOfProject, portOfProject, charsetOfProject, socketTimeOut, whetherToAutoConnect,
				finishConnectMaxCall, finishConnectWaittingTime, asynOutputMessageQueue, inputMessageQueue,
				messageProtocol, outputMessageReaderPool, dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager, clientObjectCacheManager);

		log.info(String.format("create MultiNoneBlockConnection, projectName=[%s%02d], mailBoxCnt=[%d]", projectName,
				index, mailBoxCnt));

		// this.messageManger = messageManger;
		this.PrivateMailboxWaitingQueue = new LinkedBlockingQueue<AsynPrivateMailbox>(mailBoxCnt);
		this.connectionTimeout = connectionTimeout;

		boolean isInterrupted = false;

		/**
		 * <pre>
		 * 연결은 메일 박스 갯수만큼의 메일함을 소유한다. 
		 * 메일함 1개당 1개의 입력 메시지큐와 1개의 출력 메시지큐를 갖는다.
		 * 입력 메시지 큐는 모든 연결 클래스간에 공유하며 , 출력 메시지 큐는 메일함마다 각각 존재한다.
		 * </pre>
		 */
		/*for (int i = 0; i < mailBoxCnt; i++) {

			AsynPrivateMailbox mailBox = new AsynPrivateMailbox(this, i + 1, inputMessageQueue, outputMessageQueueQueueManager);
			
			PrivateMailboxWaitingQueue.add(mailBox);
		}*/

		if (isInterrupted)
			Thread.currentThread().interrupt();

		/*try {
			reopenSocketChannel();
		} catch (IOException e) {
			String errorMessage = String.format("project[%s] ShareAsynConnection[%d], fail to config a socket channel",
					projectName, index);
			log.error(errorMessage, e);
			System.exit(1);
		}*/

		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				connectServerIfNoConnection();
			} catch (ServerNotReadyException e) {
				log.warn(
						String.format("project[%s] ShareAsynConnection[%d] fail to connect server", projectName, index),
						e);
				// System.exit(1);
			}
		}

		log.info(String.format("project[%s] ShareAsynConnection[%d] 생성자 end", projectName, index));
	}

	/**
	 * 공유 + 비동기 연결 전용 소켓 채널 열기
	 * 
	 * @throws IOException
	 *             소켓 채널을 개방할때 혹은 공유 + 비동기 에 맞도록 설정할때 에러 발생시 던지는 예외
	 */
	private void openAsynSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		// serverSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF,
		// clientProjectConfig.getDataPacketBufferSize()*2);

		StringBuilder infoBuilder = null;
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] asyn+share connection[");
		infoBuilder.append(index);
		infoBuilder.append("]");
		log.info(infoBuilder.append(" (re)open new serverSC=").append(serverSC.hashCode()).toString());
	}

	@Override
	public void connectServerIfNoConnection() throws ServerNotReadyException {
		/**
		 * <pre>
		 * 서버와 연결하는 시간보다 연결 이후 시간이 훨씬 길기때문에,
		 * 연결시 비용이 더 들어가도 연결후 비용을 더 줄일 수 만 있다면 좋을 것이다.
		 * 아래와 같이 (재)연결 판단 로직 문을 2번 사용하여 이를 달성한다.
		 * 그러면 소켓 채널이 서버와 연결된 후에는 동기화 비용 없어지고
		 * 연결 할때만 if 문 중복 비용만 더 추가될 것이다.
		 * </pre>
		 */

		try {
			synchronized (monitor) {
				if (null == serverSC || !serverSC.isOpen()) {
					openAsynSocketChannel();
				}
				
				// (재)연결 판단 로직, 2번이상 SocketChannel.open() 호출하는것을 막는 역활을 한다.
				if (serverSC.isConnected()) {
					// log.info(new StringBuilder(info).append("
					// connected").toString());
					return;
				}

				// log.info(new StringBuilder(info).append(" before
				// connect").toString());

				finalReadTime = new java.util.Date();

				InetSocketAddress remoteAddr = new InetSocketAddress(hostOfProject, portOfProject);
				
				serverSC.connect(remoteAddr);
				// log.info(new StringBuilder(info).append(" after
				// connect").toString());
				tryToFinishConnectUntilConnectionIsComplete(serverSC, intervalFinishingConnect, maxCountFinishingConnect);
				// log.info(new StringBuilder(info).append(" after
				// finishConnect").toString());
				afterConnectionWork();
				// log.info(new StringBuilder(info).append(" after
				// connect").toString());
			}
		} catch (ConnectException e) {
			throw new ServerNotReadyException(String.format("ConnectException::%s conn index[%02d], host[%s], port[%d]",
					projectName, index, hostOfProject, portOfProject));
		} catch (UnknownHostException e) {
			throw new ServerNotReadyException(
					String.format("UnknownHostException::%s conn index[%02d], host[%s], port[%d]", projectName, index,
							hostOfProject, portOfProject));
		} catch (ClosedChannelException e) {
			throw new ServerNotReadyException(
					String.format("ClosedChannelException::%s conn index[%02d], host[%s], port[%d]", projectName, index,
							hostOfProject, portOfProject));
		} catch (IOException e) {
			serverClose();

			throw new ServerNotReadyException(
					String.format("IOException::index[%d], projectName[%s], host[%s], port[%d]", index, projectName,
							hostOfProject, portOfProject));
		} catch (ServerNotReadyException e) {
			throw e;
		} catch (Exception e) {
			serverClose();

			log.warn("unknown exception", e);
			throw new ServerNotReadyException(String.format("unknown::%s conn index[%02d], host[%s], port[%d]",
					projectName, index, hostOfProject, portOfProject));
		}

		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}

	@Override
	public void putToOutputMessageQueue(WrapReadableMiddleObject receivedLetter) {
		if (receivedLetter.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			boolean result = false;
			try {
				result = asynOutputMessageQueue.offer(receivedLetter, socketTimeOut, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				/**
				 * 인터럽트 발생시 메소드 끝가지 로직 수행후 인터럽트 상태를 복귀 시켜 최종 인터럽트 처리를 마무리 하도록 유도
				 */
				try {
					result = asynOutputMessageQueue.offer(receivedLetter, socketTimeOut, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e1) {
					log.error("인터럽트 받아 후속 처리중 발생", e1);
					System.exit(1);
				}
				Thread.currentThread().interrupt();
			}

			if (!result) {
				String errorMsg = String.format(
						"서버 공용 출력 메시지 큐 응답 시간[%d]이 초과되었습니다. serverConnection=[%s], receivedLetter=[%s]", socketTimeOut,
						this.getSimpleConnectionInfo(), receivedLetter.toString());

				log.warn(errorMsg);
			}
		} else {
			AsynPrivateMailbox mailbox = privateMailboxMap.get(receivedLetter.getMailboxID());
			if (null == mailbox) {

				log.warn(String.format("no match mailid, projectName=[%s%02d], serverSC=[%d], receivedLetter=[%s]",
						projectName, index, serverSC.hashCode(), receivedLetter.toString()));
				return;
			}
			//mailbox.putToSyncOutputMessageQueue(receivedLetter);
		}
	}

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws ServerNotReadyException, SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException, InterruptedException, MailboxTimeoutException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// log.info("projectName[%s] inputMessage=[%s]", projectName,
		// inObj.toString());

		WrapReadableMiddleObject receivedLetter = null;

		connectServerIfNoConnection();

		AsynPrivateMailbox mailbox = null;

		// boolean isInterrupted = false;

		mailbox = PrivateMailboxWaitingQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
		if (null == mailbox) {
			throw new MailboxTimeoutException("share asynchronized mailbox timeout");
		}

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		ToLetter letterToServer = null;

		/**
		 * <pre>
		 * 공유+비동기 연결 객체를 직접 받을 수 없지만 동시 사용이 가능 하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * 비공유+비동기 연결 객체는 원칙적으로 공유할 수 없지만 직접 받을 수 있기때문에 동시 사용 가능이 가능하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * </pre>
		 */
		/*synchronized (mailbox) {
			try {
				mailbox.setActive();

				inObj.messageHeaderInfo.mailboxID = mailbox.getMailboxID();
				inObj.messageHeaderInfo.mailID = mailbox.getMailID();
				
				// getWrapBufferList(classLoader, serverSC);
				letterToServer = new ToLetter(serverSC, 
						inObj.getMessageID(), 
						inObj.messageHeaderInfo.mailboxID,
						inObj.messageHeaderInfo.mailID,
						getWrapBufferList(classLoader, inObj));

				privateMailboxMap.put(inObj.messageHeaderInfo.mailboxID, mailbox);

				try {
					mailbox.putSyncInputMessage(letterToServer);
				} catch (InterruptedException e) {
					log.warn("fail to put the input message[{}] in the input message queue of mailbox becase this current thread was interrupted", inObj.toString());
					throw e;
				}

				// LetterFromServer letterFromServer = null;

				try {
					receivedLetter = mailbox.getSyncOutputMessage();

					// letterFromServer = new LetterFromServer(workOutObj);
				} catch (InterruptedException e) {
					log.warn("fail to get the output message from the output message queue of mailbox becase this current thread was interrupted, the input message[{}]", inObj.toString());
					throw e;
				}

			} finally {
				if (null != mailbox) {
					privateMailboxMap.remove(mailbox.getMailboxID());
					mailbox.setDisable();
					boolean isSuccess =  PrivateMailboxWaitingQueue.offer(mailbox);
					if (!isSuccess) {
						log.error("fail to put mailbox[{}] in the mailbox queue becase of bug, you need to check and fix bug", mailbox.hashCode());
						System.exit(1);
					}
				}
			}
		}*/

		AbstractMessage outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));

		

		return outObj;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inObj) throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, NotSupportedException, InterruptedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// log.info("projectName[%s] inputMessage=[%s]", projectName,
		// inObj.toString());

		connectServerIfNoConnection();

		AsynPrivateMailbox mailbox = null;
		mailbox = PrivateMailboxWaitingQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		

		/**
		 * <pre>
		 * 공유+비동기 연결 객체를 직접 받을 수 없지만 동시 사용이 가능 하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * 비공유+비동기 연결 객체는 원칙적으로 공유할 수 없지만 직접 받을 수 있기때문에 동시 사용 가능이 가능하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * </pre>
		 */
		/*synchronized (mailbox) {
			try {
				mailbox.setActive();

				inObj.messageHeaderInfo.mailboxID = mailbox.getMailboxID();
				inObj.messageHeaderInfo.mailID = mailbox.getMailID();
				ToLetter toLetter = new ToLetter(serverSC, 
						inObj.getMessageID(), 
						inObj.messageHeaderInfo.mailboxID,
						inObj.messageHeaderInfo.mailID,
						getWrapBufferList(classLoader, inObj));

				privateMailboxMap.put(mailbox.getMailboxID(), mailbox);

				try {
					mailbox.putAsynInputMessage(toLetter);
				} catch (InterruptedException e) {
					log.warn("fail to put the input message[{}] in the input message queue of mailbox becase this current thread was interrupted", inObj.toString());
					throw e;
				}

			} finally {
				if (null != mailbox) {
					privateMailboxMap.remove(mailbox.getMailboxID());
					mailbox.setDisable();
					boolean isSuccess =  PrivateMailboxWaitingQueue.offer(mailbox);
					if (!isSuccess) {
						log.error("fail to put mailbox[{}] in the mailbox queue becase of bug, you need to check and fix bug", mailbox.hashCode());
						System.exit(1);
					}
				}
				
			}
		}*/

		endTime = new java.util.Date().getTime();
		log.info(String.format("시간차=[%d]", (endTime - startTime)));
	}

	/**
	 * @return 사용중인 메일함 갯수
	 */
	public int getUsedMailboxCnt() {
		return PrivateMailboxWaitingQueue.remainingCapacity();
	}

	@Override
	protected void afterConnectionWork() throws InterruptedException {
		outputMessageReaderPool.addNewServer(this);
	}

	@Override
	public void finalize() {
		socketOutputStream.close();
		log.warn(String.format("MultiNoneBlockConnection 소멸::[%s]", toString()));
	}

}
