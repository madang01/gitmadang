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

package impl.executor.client;

/**
 * 가상적으로 입력 메시지들을 넣은 거대 스트림으로부터 랜덤하게 데이터를 순차적으로 읽으면서 출력 메시지를 바르게 추출하는지 검사하기 위한 로직이다.
 * 
 * @author Jonghoon Won
 *
 */
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.impl.message.AllDataType.AllDataTypeEncoder;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.impl.message.Echo.EchoEncoder;
import kr.pe.sinnori.util.AbstractClientExecutor;

public final class TestVirtualInputStreamCExtor extends AbstractClientExecutor {
	private ArrayList<AbstractMessage> inputMessageList = new ArrayList<AbstractMessage>();
	private ByteBuffer baseBuffer = ByteBuffer.allocate(1 * 1024 * 1024);
	private int mailID = 1;

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerTaskException, NotLoginException {

		ClientObjectCacheManagerIF clientObjectCacheManager = (ClientObjectCacheManagerIF) clientProject;
		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF) clientProject;

		MessageProtocolIF messageProtocol = clientProject.getMessageProtocol();

		

		java.util.Random random = new java.util.Random();

		baseBuffer.order(clientProjectConfig.getByteOrder());

		addAllDataTypeInObj(10, clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);

		log.info(String.format("1.baseBuffer=[%s]", baseBuffer.toString()));

		// log.info(String.format("1.baseBuffer=[%s]",
		// HexUtil.byteBufferToHex(baseBuffer, 0, 523)));

		addEchoInObj(clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);

		log.info(String.format("2.baseBuffer=[%s]", baseBuffer.toString()));

		addEchoInObj(clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);

		log.info(String.format("3.baseBuffer=[%s]", baseBuffer.toString()));

		addAllDataTypeInObj(10, clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);

		log.info(String.format("4.baseBuffer=[%s]", baseBuffer.toString()));

		addAllDataTypeInObj(10, clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);

		log.info(String.format("5.baseBuffer=[%s]", baseBuffer.toString()));

		addAllDataTypeInObj(10, clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);
		
		log.info(String.format("6.baseBuffer=[%s]", baseBuffer.toString()));

		addAllDataTypeInObj(30000, clientProjectConfig, random,
				messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("7.baseBuffer=[%s]", baseBuffer.toString()));
		
		addEchoInObj(clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);
		
		log.info(String.format("8.baseBuffer=[%s]", baseBuffer.toString()));
		
		addEchoInObj(clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);
		
		log.info(String.format("9.baseBuffer=[%s]", baseBuffer.toString()));
		
		addAllDataTypeInObj(30000, clientProjectConfig, random,
				messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("10.baseBuffer=[%s]", baseBuffer.toString()));
		
		addEchoInObj(clientProjectConfig, random, messageProtocol,
				dataPacketBufferQueueManager);
		
		log.info(String.format("11.baseBuffer=[%s]", baseBuffer.toString()));
		
		addAllDataTypeInObj(30000, clientProjectConfig, random,
				messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("12.baseBuffer=[%s]", baseBuffer.toString()));
		

		baseBuffer.flip();

		/*
		 * // FIXME!, THB 64 bytes 에서만 유효, DHB Header 96 bytes, 조작할 메시지 시작 옵셋
		 * 739, 조작할 위치 79, int t1 = baseBuffer.getInt((643+64+66+13));
		 * log.info(String.format("t1=[%d]", t1));
		 * 
		 * byte b1 = baseBuffer.get((643+64+66+13+4));
		 * log.info(String.format("b1=[%x]", b1));
		 * 
		 * 
		 * int t2 = baseBuffer.getInt((1134+64+66+13));
		 * log.info(String.format("t2=[%d]", t2));
		 * 
		 * // FIXME! log.info(String.format("baseBuffer=[%s]",
		 * baseBuffer.toString()));
		 */
		SocketInputStream socketInputStream = new SocketInputStream(
				dataPacketBufferQueueManager);
		

		int inxOfInputMessage = 0;
		try {
			while (baseBuffer.hasRemaining()) {
				int len = random.nextInt(3000) + 30;
				len = Math.min(len, baseBuffer.remaining());
				ByteBuffer sourceBuffer = ByteBuffer.allocate(len);
				baseBuffer.get(sourceBuffer.array());

				ByteBuffer scOwnLastBuffer = socketInputStream
						.getLastDataPacketBuffer();
				// FIXME!
				if (null == scOwnLastBuffer)
					log.info("scOwnLastBuffer is null");

				while (sourceBuffer.hasRemaining()) {
					if (!scOwnLastBuffer.hasRemaining()) {
						scOwnLastBuffer = socketInputStream
								.nextDataPacketBuffer();
					}

					scOwnLastBuffer.put(sourceBuffer.get());
				}

				// log.info("1.len=[{}], baseBuffer.position=[{}], socketInputStream.position=[{}], baseBuffer.remaining=[{}]", len, baseBuffer.position(), socketInputStream.position(), baseBuffer.remaining());

				// log.info(String.format("2.len=[%d], baseBuffer.position=[%d], socketInputStream.position=[%d]",
				// len, baseBuffer.position(), socketInputStream.position()));
				
				// FIXME!
				// log.info("socketInputStream.position={}", socketInputStream.position());

				ArrayList<ReceivedLetter> receivedLetterList = null;
				
				receivedLetterList = messageProtocol.S2MList(
						clientProjectConfig.getCharset(), socketInputStream);

				for (ReceivedLetter receivedLetter : receivedLetterList) {
					// log.info(receivedLetter.toString());

					AbstractMessage outObj = getMessageFromMiddleReadObj(
							TestVirtualInputStreamCExtor.class.getClassLoader(),
							clientProjectConfig, clientObjectCacheManager,
							messageProtocol, receivedLetter);
					String outObjStr= outObj.toString();
					
					AbstractMessage inObj = inputMessageList.get(inxOfInputMessage);
					String inObjStr = inObj.toString();
					
					
					if (inObjStr.equals(outObjStr)) {
						if (inObj instanceof AllDataType) {
							AllDataType allDataTypeInObj = (AllDataType)inObj;
							AllDataType allDataTypeOutObj = (AllDataType)outObj;
							
							if (! java.util.Arrays.equals(allDataTypeInObj.getBytesVar1(), allDataTypeOutObj.getBytesVar1()) 
									|| ! java.util.Arrays.equals(allDataTypeInObj.getBytesVar2(), allDataTypeOutObj.getBytesVar2())) { 
								log.warn("2.입력 메시지[{}/{}]와 출력 메시지 비교 결과 다름", inxOfInputMessage+1, inputMessageList.size());
							} else {
								log.info("입력 메시지[{}/{}]와 출력 메시지 같음", inxOfInputMessage+1, inputMessageList.size());
							}
						} else {
							log.info("입력 메시지[{}/{}]와 출력 메시지 같음", inxOfInputMessage+1, inputMessageList.size());
						}
					} else {
						log.warn("1.입력 메시지[{}/{}]와 출력 메시지 비교 결과 다름", inxOfInputMessage+1, inputMessageList.size());
						System.exit(1);
					}
					
					inxOfInputMessage++;
				}
				
			}
		} catch (HeaderFormatException e) {
			e.printStackTrace();
			// System.exit(1);
		} finally {
			socketInputStream.destory();
		}

	}

	private AbstractMessage getMessageFromMiddleReadObj(
			ClassLoader classLoader, ClientProjectConfig clientProjectConfig,
			ClientObjectCacheManagerIF clientObjectCacheManager,
			MessageProtocolIF messageProtocol, ReceivedLetter receivedLetter)
			throws DynamicClassCallException, BodyFormatException {
		String messageID = receivedLetter.getMessageID();
		int mailboxID = receivedLetter.getMailboxID();
		int mailID = receivedLetter.getMailID();
		Object middleReadObj = receivedLetter.getMiddleReadObj();
		
		// FIXME!
		//log.info("11111AAA"+middleReadObj.toString());

		MessageCodecIF messageCodec = null;

		try {
			messageCodec = clientObjectCacheManager.getClientCodec(classLoader,
					messageID);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());

			throw e;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);

			throw new DynamicClassCallException("unkown error::"
					+ e.getMessage());
		}

		MessageDecoder messageDecoder = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = String.format(
					"클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.", messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage,
					mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String.format(
					"알 수 없는 원인으로 클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.",
					messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage,
					mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		}

		AbstractMessage messageObj = null;
		try {
			messageObj = messageDecoder.decode(
					messageProtocol.getSingleItemDecoder(),
					clientProjectConfig.getCharset(), middleReadObj);
			messageObj.messageHeaderInfo.mailboxID = mailboxID;
			messageObj.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = String
					.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
							messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		} catch (OutOfMemoryError e) {
			String errorMessage = String
					.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
							messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String
					.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
							messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		}

		return messageObj;
	}

	private void addEchoInObj(ClientProjectConfig clientProjectConfig,
			java.util.Random random, MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager)
			throws NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException {

		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());

		echoInObj.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		echoInObj.messageHeaderInfo.mailID = mailID++;

		inputMessageList.add(echoInObj);

		ArrayList<WrapBuffer> wrapBufferList = messageProtocol.M2S(echoInObj,
				new EchoEncoder(), clientProjectConfig.getCharset());

		int warpBufferListSize = wrapBufferList.size();
		for (int i = 0; i < warpBufferListSize; i++) {
			WrapBuffer wrapBuffer = wrapBufferList.get(i);
			ByteBuffer workByteBuffer = wrapBuffer.getByteBuffer();
			baseBuffer.put(workByteBuffer);
			// log.debug(oneBuffer.toString());
			dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
		}
	}

	private void addAllDataTypeInObj(int lenOfBytesVar2,
			ClientProjectConfig clientProjectConfig, java.util.Random random,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager)
			throws NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException {

		AllDataType allDataTypeInObj = new AllDataType();
		allDataTypeInObj.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		allDataTypeInObj.messageHeaderInfo.mailID = mailID++;

		allDataTypeInObj.setByteVar1(Byte.MAX_VALUE);
		allDataTypeInObj.setByteVar2(Byte.MIN_VALUE);
		allDataTypeInObj.setByteVar3((byte) 0x60);
		allDataTypeInObj.setUnsignedByteVar1((short) 0);
		allDataTypeInObj.setUnsignedByteVar2((short) 0xff);
		allDataTypeInObj.setUnsignedByteVar3((short) 0x65);
		allDataTypeInObj.setShortVar1(Short.MAX_VALUE);
		allDataTypeInObj.setShortVar2(Short.MIN_VALUE);
		allDataTypeInObj.setShortVar3((short) 31);
		allDataTypeInObj.setUnsignedShortVar1(0);
		allDataTypeInObj.setUnsignedShortVar2((int) 0xffff);
		allDataTypeInObj.setUnsignedShortVar3((int) 32);
		allDataTypeInObj.setIntVar1(Integer.MAX_VALUE);
		allDataTypeInObj.setIntVar2(Integer.MIN_VALUE);
		allDataTypeInObj.setIntVar3(random.nextInt());
		allDataTypeInObj.setUnsignedIntVar1((long) 0);
		allDataTypeInObj.setUnsignedIntVar2((long) 0x7fffffff);
		allDataTypeInObj.setUnsignedIntVar3(Integer.MAX_VALUE + 1000L);
		allDataTypeInObj.setLongVar1(Long.MAX_VALUE);
		allDataTypeInObj.setLongVar2(Long.MIN_VALUE);
		allDataTypeInObj.setLongVar3(random.nextLong());
		allDataTypeInObj.setStrVar1("testHH");
		allDataTypeInObj.setStrVar2("1234");
		allDataTypeInObj.setStrVar3("uiop");
		allDataTypeInObj.setBytesVar1(new byte[] { (byte) 0x77, (byte) 0x88,
				-128, -127, 126, 127, -1 });
		allDataTypeInObj.setBytesVar2(ByteBuffer.allocate(lenOfBytesVar2).array());
		allDataTypeInObj.setSqldate(new java.sql.Date(new java.util.Date().getTime()));
		allDataTypeInObj.setSqltimestamp(new java.sql.Timestamp(new java.util.Date().getTime()));
		allDataTypeInObj.setCnt(2);

		AllDataType.Member[] memberList = new AllDataType.Member[allDataTypeInObj
				.getCnt()];
		for (int i = 0; i < memberList.length; i++) {
			memberList[i] = allDataTypeInObj.new Member();
		}
		allDataTypeInObj.setMemberList(memberList);
		{
			memberList[0].setMemberID("test01ID");
			memberList[0].setMemberName("test01Name");
			memberList[0].setCnt(1);

			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[0]
					.getCnt()];
			for (int i = 0; i < itemList.length; i++) {
				itemList[i] = memberList[0].new Item();
			}
			{
				itemList[0].setItemID("1");
				itemList[0].setItemName("최강의검");
				itemList[0].setItemCnt(1);
			}
			memberList[0].setItemList(itemList);
		}
		{
			memberList[1].setMemberID("test01ID");
			memberList[1].setMemberName("test01Name");
			memberList[1].setCnt(2);

			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[1]
					.getCnt()];
			for (int i = 0; i < itemList.length; i++) {
				itemList[i] = memberList[0].new Item();
			}
			{
				itemList[0].setItemID("2");
				itemList[0].setItemName("살살검");
				itemList[0].setItemCnt(5);
			}
			{
				itemList[1].setItemID("3");
				itemList[1].setItemName("안좋은검");
				itemList[1].setItemCnt(100);
			}
			memberList[1].setItemList(itemList);
		}

		inputMessageList.add(allDataTypeInObj);

		ArrayList<WrapBuffer> wrapBufferList = messageProtocol.M2S(
				allDataTypeInObj, new AllDataTypeEncoder(),
				clientProjectConfig.getCharset());

		int warpBufferListSize = wrapBufferList.size();
		for (int i = 0; i < warpBufferListSize; i++) {
			WrapBuffer wrapBuffer = wrapBufferList.get(i);
			ByteBuffer workByteBuffer = wrapBuffer.getByteBuffer();
			baseBuffer.put(workByteBuffer);
			// log.debug(oneBuffer.toString());
			dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
		}

		// FIXME! THB 프로토콜에서만 유효
		/*
		 * if (5 == mailID) { baseBuffer.putInt((643+64+66+13), -1); } else if
		 * (6 == mailID) { baseBuffer.putInt((1134+64+66+13), 30000); }
		 */
	}
}
