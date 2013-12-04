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

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.io.dhb.DHBSingleItemConverter;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.ItemGroupDataOfArray;
import kr.pe.sinnori.util.AbstractClientExecutor;

public final class TestVirtualInputStreamCExtor extends AbstractClientExecutor {
	private ArrayList<InputMessage> orgInputMessageList = new ArrayList<InputMessage>();  
	private ByteBuffer baseBuffer = ByteBuffer.allocate(1024*1024);
	int messageIDFixedSize;

	@Override
	protected void doTask(MessageMangerIF messageManger,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, InterruptedException, MessageItemException {

		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF)clientProject;
		final CommonProjectInfo commonProjectInfo = clientProject.getCommonProjectInfo();
		
		ProjectConfig projectInfo = null;
		
		try {
			projectInfo = (ProjectConfig)conf.getResource(commonProjectInfo.projectName);
		} catch(RuntimeException e) {
			log.fatal(String.format("%s 프로젝트 정보가 존재하지 않습니다.", commonProjectInfo.projectName));
			System.exit(1);
		}
		
		messageIDFixedSize = projectInfo.getMessageIDFixedSize();
		
		// dhbHeader.messageHeaderSize;
		
		DHBMessageProtocol dhbMessageProtocol = 
				new DHBMessageProtocol(commonProjectInfo.dataPacketBufferSize, 
						commonProjectInfo.dataPacketBufferMaxCntPerMessage,
						dataPacketBufferQueueManager);
		
		MessageInputStreamResourcePerSocket messageInputStreamResource = 
				new MessageInputStreamResourcePerSocket(commonProjectInfo.byteOrderOfProject, dataPacketBufferQueueManager);
		
		
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil.createCharsetEncoder(commonProjectInfo.charsetOfProject);
		// CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(commonProjectInfo.charsetOfProject);
		
		
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		java.util.Random random = new java.util.Random();
		
		baseBuffer.order(commonProjectInfo.byteOrderOfProject);
		
		addAllDataTypeInObj(messageManger, dataPacketBufferQueueManager, md5, random, commonProjectInfo, charsetOfProjectEncoder);
		addEchoInObj(messageManger, dataPacketBufferQueueManager, md5, random, commonProjectInfo, charsetOfProjectEncoder);
		addEchoInObj(messageManger, dataPacketBufferQueueManager, md5, random, commonProjectInfo, charsetOfProjectEncoder);
		addAllDataTypeInObj(messageManger, dataPacketBufferQueueManager, md5, random, commonProjectInfo, charsetOfProjectEncoder);
		
		
		baseBuffer.flip();
		
		
		
		try {
			while (baseBuffer.hasRemaining()) {
				int len = random.nextInt(3000) + 30;
				len = Math.min(len, baseBuffer.remaining());
				
				ByteBuffer scOwnLastBuffer = messageInputStreamResource.getLastBuffer();
				
				len = Math.min(len, scOwnLastBuffer.remaining());
				
				log.info(String.format("1.len=[%d], baseBuffer.remaining=[%d], scOwnLastBuffer.remaining=[%d]", len, baseBuffer.remaining(), scOwnLastBuffer.remaining()));
				
				byte readBytes[] = new byte[len];
				baseBuffer.get(readBytes);
				
				scOwnLastBuffer.put(readBytes);
				
				log.info(String.format("2.len=[%d], baseBuffer.remaining=[%d], scOwnLastBuffer.remaining=[%d]", len, baseBuffer.remaining(), scOwnLastBuffer.remaining()));
				
				
				ArrayList<AbstractMessage> readInputMessageList = null;
				
				readInputMessageList = dhbMessageProtocol.S2MList(InputMessage.class, commonProjectInfo.charsetOfProject, messageInputStreamResource, messageManger);
				
				// ArrayList<InputMessage> readInputMessageList = messageInputStreamResource.getInputMessageList(messageManger, charsetOfProjectDecoder, md5);
				int readInputMessageListSize = readInputMessageList.size();
				for (int i=0; i< readInputMessageListSize; i++) {
					InputMessage inObj = (InputMessage)readInputMessageList.get(i);
					log.info(inObj.toString());
				}
			}
		} catch (HeaderFormatException e) {
			e.printStackTrace();
		}
	}
	
	private void addEchoInObj(MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			java.security.MessageDigest md5,
			java.util.Random random,
			CommonProjectInfo commonProjectInfo, 
			CharsetEncoder charsetOfProjectEncoder) throws MessageInfoNotFoundException, BodyFormatException, NoMoreDataPacketBufferException, MessageItemException {
		DHBSingleItemConverter dhbSingleItemConverter = new DHBSingleItemConverter();
		
		InputMessage echoInObj = messageManger.createInputMessage("Echo");

		echoInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		echoInObj.messageHeaderInfo.mailID = Integer.MIN_VALUE;

		echoInObj.setAttribute("mRandomInt", random.nextInt());
		echoInObj.setAttribute("mStartTime", new java.util.Date().getTime());
		
		orgInputMessageList.add(echoInObj);
		
		FreeSizeOutputStream fsos = new FreeSizeOutputStream(commonProjectInfo.byteOrderOfProject, commonProjectInfo.charsetOfProject, charsetOfProjectEncoder, dataPacketBufferQueueManager);
		
		echoInObj.M2S(fsos, dhbSingleItemConverter);
		
		long bodySize = fsos.postion();
		
		ArrayList<WrapBuffer> bufferList = fsos.getDataPacketBufferList();
		
		int bufferListSize = bufferList.size();
		for (int i=0; i < bufferListSize; i++) {
			WrapBuffer bodyWrapBuffer = bufferList.get(i);
			ByteBuffer dupBodyBuffer = bodyWrapBuffer.getByteBuffer().duplicate();
			md5.update(dupBodyBuffer);
		}
		
		DHBMessageHeader messageHeader = new DHBMessageHeader(messageIDFixedSize);
		messageHeader.messageID = echoInObj.getMessageID();
		messageHeader.mailboxID = 1;
		messageHeader.mailID = orgInputMessageList.size();
		messageHeader.bodySize = bodySize;
		messageHeader.bodyMD5 = md5.digest();
		
		messageHeader.writeMessageHeader(baseBuffer, commonProjectInfo.charsetOfProject, charsetOfProjectEncoder, md5);
		
		for (int i=0; i < bufferListSize; i++) {
			WrapBuffer bodyWrapBuffer = bufferList.remove(0);
			ByteBuffer bodyBuffer = bodyWrapBuffer.getByteBuffer();
			baseBuffer.put(bodyBuffer);
			dataPacketBufferQueueManager.putDataPacketBuffer(bodyWrapBuffer);
		}
	}
	
	private void addAllDataTypeInObj(MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			java.security.MessageDigest md5,
			java.util.Random random,
			CommonProjectInfo commonProjectInfo, 
			CharsetEncoder charsetOfProjectEncoder) throws MessageInfoNotFoundException, BodyFormatException, NoMoreDataPacketBufferException, MessageItemException {
		DHBSingleItemConverter dhbSingleItemConverter = new DHBSingleItemConverter();
		
		InputMessage allDataTypeInObj = messageManger.createInputMessage("AllDataType");
		allDataTypeInObj.setAttribute("byteVar1", Byte.MAX_VALUE);
		allDataTypeInObj.setAttribute("byteVar2", Byte.MIN_VALUE);
		allDataTypeInObj.setAttribute("byteVar3", (byte) 0x60);

		allDataTypeInObj.setAttribute("unsignedByteVar1", (short) 0);
		allDataTypeInObj.setAttribute("unsignedByteVar2", (short) 0xff);
		allDataTypeInObj.setAttribute("unsignedByteVar3", (short) 0x65);

		allDataTypeInObj.setAttribute("shortVar1", Short.MAX_VALUE);
		allDataTypeInObj.setAttribute("shortVar2", Short.MIN_VALUE);
		allDataTypeInObj.setAttribute("shortVar3", (short) 30);

		allDataTypeInObj.setAttribute("unsignedShortVar1", (int) 0);
		allDataTypeInObj.setAttribute("unsignedShortVar2", (int) 0xffff);
		allDataTypeInObj.setAttribute("unsignedShortVar3",
				(int) (Short.MAX_VALUE + 1000));

		allDataTypeInObj.setAttribute("intVar1", Integer.MAX_VALUE);
		allDataTypeInObj.setAttribute("intVar2", Integer.MIN_VALUE);
		allDataTypeInObj.setAttribute("intVar3", (int) 31);

		allDataTypeInObj.setAttribute("unsignedIntVar1", (long) 0);
		allDataTypeInObj.setAttribute("unsignedIntVar2", (long) 0x7fffffff);
		allDataTypeInObj.setAttribute("unsignedIntVar3", Integer.MAX_VALUE
				+ (long) 1000);

		allDataTypeInObj.setAttribute("longVar1", Long.MAX_VALUE);
		allDataTypeInObj.setAttribute("longVar2", Long.MIN_VALUE);
		allDataTypeInObj.setAttribute("longVar3", (long) 32);

		allDataTypeInObj.setAttribute("strVar1", "testHH");
		allDataTypeInObj.setAttribute("strVar2", "1234");
		allDataTypeInObj.setAttribute("strVar3", "uiop");

		allDataTypeInObj.setAttribute("bytesVar1", new byte[] { (byte) 0x77,
				(byte) 0x88 });
		// allDataTypeInObj.setAttribute("bytesVar2", new byte[] { 1, 2, 3, 4, 5, 6, 7,
		// 8,
		// 9, 10, 11 });
		allDataTypeInObj.setAttribute("bytesVar2", ByteBuffer.allocate(8000).array());

		int memberListCnt = 2;
		allDataTypeInObj.setAttribute("cnt", memberListCnt);

		ArrayData memberListForArrayDataList = (ArrayData) allDataTypeInObj
				.getAttribute("memberList");

		ItemGroupDataIF memberList[] = new ItemGroupDataIF[memberListCnt];

		ArrayData itemListForArrayDataList[] = new ArrayData[memberListCnt];
		ItemGroupDataIF itemList[][] = new ItemGroupDataIF[memberListCnt][];
		// int itemListCnt[]=new int[memberListCnt];

		memberList[0] = memberListForArrayDataList.get(0);
		memberList[0].setAttribute("memberID", "test01ID");
		memberList[0].setAttribute("memberName", "test01");
		memberList[0].setAttribute("cnt", 2);

		// System.out.println(memberList[0].toString());

		itemListForArrayDataList[0] = (ArrayData) memberList[0]
				.getAttribute("itemList");

		itemList[0] = new ItemGroupDataOfArray[2];

		itemList[0][0] = itemListForArrayDataList[0].get(0);
		itemList[0][0].setAttribute("itemID", "1");
		itemList[0][0].setAttribute("itemName", "최강의검");

		// System.out.println(memberList[0].toString());

		itemList[0][0].setAttribute("itemCnt", 1);

		// System.out.println(memberList[0].toString());

		itemList[0][1] = itemListForArrayDataList[0].get(1);
		itemList[0][1].setAttribute("itemID", "2");
		itemList[0][1].setAttribute("itemName", "살살검");
		itemList[0][1].setAttribute("itemCnt", 2);

		// System.out.println(memberList[0].toString());

		memberList[1] = memberListForArrayDataList.get(1);

		// System.out.println(memberList[1].toString());

		memberList[1].setAttribute("memberID", "test02ID");
		memberList[1].setAttribute("memberName", "test02");
		memberList[1].setAttribute("cnt", 1);

		// System.out.println(memberList[1].toString());

		itemListForArrayDataList[1] = (ArrayData) memberList[1]
				.getAttribute("itemList");

		// System.out.println(itemListForArrayDataList[1].toString());

		itemList[1] = new ItemGroupDataOfArray[1];
		itemList[1][0] = itemListForArrayDataList[1].get(0);
		itemList[1][0].setAttribute("itemID", "3");
		itemList[1][0].setAttribute("itemName", "안좋은검");
		itemList[1][0].setAttribute("itemCnt", 65000);
		
		orgInputMessageList.add(allDataTypeInObj);
		
		FreeSizeOutputStream fsos = new FreeSizeOutputStream(commonProjectInfo.byteOrderOfProject, commonProjectInfo.charsetOfProject, charsetOfProjectEncoder, dataPacketBufferQueueManager);
		
		allDataTypeInObj.M2S(fsos, dhbSingleItemConverter);
		
		long bodySize = fsos.postion();
		
		ArrayList<WrapBuffer> bufferList = fsos.getDataPacketBufferList();
		
		int bufferListSize = bufferList.size();
		for (int i=0; i < bufferListSize; i++) {
			WrapBuffer bodyWrapBuffer = bufferList.get(i);
			ByteBuffer dupBodyBuffer = bodyWrapBuffer.getByteBuffer().duplicate();
			md5.update(dupBodyBuffer);
		}
		
		DHBMessageHeader messageHeader = new DHBMessageHeader(messageIDFixedSize);
		messageHeader.messageID = allDataTypeInObj.getMessageID();
		messageHeader.mailboxID = 1;
		messageHeader.mailID = orgInputMessageList.size();
		messageHeader.bodySize = bodySize;
		messageHeader.bodyMD5 = md5.digest();
		
		messageHeader.writeMessageHeader(baseBuffer, commonProjectInfo.charsetOfProject, charsetOfProjectEncoder, md5);
		
		for (int i=0; i < bufferListSize; i++) {
			WrapBuffer bodyWrapBuffer = bufferList.remove(0);
			ByteBuffer bodyBuffer = bodyWrapBuffer.getByteBuffer();
			baseBuffer.put(bodyBuffer);
			dataPacketBufferQueueManager.putDataPacketBuffer(bodyWrapBuffer);
		}
	}
}
