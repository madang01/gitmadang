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

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.ItemGroupDataOfArray;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * @author Jonghoon Won
 *
 */
public class TestNetAllDataType2CExtor extends AbstractClientExecutor {
	
	private static AbstractConnection conn = null;

	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException,
			DynamicClassCallException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, 
			NoMatchOutputMessage, InterruptedException, 
			MessageItemException, ServerExcecutorUnknownException, NotLoginException {
		java.util.Random random = new java.util.Random();
		
		if (null == conn) {
			try {
				conn = clientProject.getConnection();
			} catch (NotSupportedException e) {
				log.fatal(e.toString(), e);
				System.exit(1);
			}
		}
		
		InputMessage allDataTypeInObj = null;
		try {
			allDataTypeInObj = messageManger.createInputMessage("AllDataType");
		} catch (MessageInfoNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		

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
		allDataTypeInObj.setAttribute("unsignedShortVar3", (int) (Short.MAX_VALUE + 1000));

		allDataTypeInObj.setAttribute("intVar1", Integer.MAX_VALUE);
		allDataTypeInObj.setAttribute("intVar2", Integer.MIN_VALUE);
		allDataTypeInObj.setAttribute("intVar3", random.nextInt());

		allDataTypeInObj.setAttribute("unsignedIntVar1", (long) 0);
		allDataTypeInObj.setAttribute("unsignedIntVar2", (long) 0x7fffffff);
		allDataTypeInObj.setAttribute("unsignedIntVar3", Integer.MAX_VALUE + (long) 1000);

		allDataTypeInObj.setAttribute("longVar1", Long.MAX_VALUE);
		allDataTypeInObj.setAttribute("longVar2", Long.MIN_VALUE);
		allDataTypeInObj.setAttribute("longVar3", random.nextLong());

		allDataTypeInObj.setAttribute("strVar1", "testHH");
		allDataTypeInObj.setAttribute("strVar2", "1234");
		allDataTypeInObj.setAttribute("strVar3", "uiop");

		allDataTypeInObj.setAttribute("bytesVar1", new byte[] { (byte) 0x77, (byte) 0x88 });
		// allDataTypeInObj.setAttribute("bytesVar2", new byte[] { 1, 2, 3, 4, 5, 6, 7, 8,
		// 9, 10, 11 });
		allDataTypeInObj.setAttribute("bytesVar2", ByteBuffer.allocate(30000).array());

		int memberListCnt = 2;
		allDataTypeInObj.setAttribute("cnt", memberListCnt);
		ArrayData memberListForArrayDataList = (ArrayData) allDataTypeInObj.getAttribute("memberList");

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

		allDataTypeInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		allDataTypeInObj.messageHeaderInfo.mailID = Integer.MIN_VALUE;
		
		LetterFromServer letterFromServer = conn.sendSyncInputMessage(allDataTypeInObj);

		if (null == letterFromServer) {
			log.warn(String.format("input message[%s] letterFromServer is null", allDataTypeInObj.getMessageID()));
			return;
		}
		
		OutputMessage allDataTypeOutObj = letterFromServer.getOutputMessage("AllDataType");

		// FXIME!
		// log.info("OutputMessage[%s], bytesVar2 length=[%d]", allDataTypeOutObj.toString(), ((byte[]) allDataTypeOutObj.getAttribute("bytesVar2")).length);

		String allDataTypeInObjStr = allDataTypeInObj.toString();
		String allDataTypeOutObjStr = allDataTypeOutObj.toString();

		// log.info("allDataTypeInObj Str=[%s]", allDataTypeInObjStr);
		// log.info(String.format("allDataTypeOutObj str=[%s]", allDataTypeInObjStr));
		
		/*
		String allDataTypeInObjJSONStr = allDataTypeOutObj.toJSONString();
		log.info(String.format("allDataTypeOutObj JSON Str=[%s]", allDataTypeInObjJSONStr));
		
		JSONParser parser = new JSONParser();
		
		  
		JSONObject jsonMain = null;
		
		try {
			jsonMain = (JSONObject)parser.parse(allDataTypeInObjJSONStr);
			
			JSONArray jsonMemberList =  (JSONArray) jsonMain.get("memberList");
			int jsonMemberListSize = jsonMemberList.size();
			for (int i=0; i < jsonMemberListSize; i++) {
				JSONObject jsonMember = (JSONObject)jsonMemberList.get(i);
				
				log.info(jsonMember.toJSONString());
				
			}
			
		} catch(ParseException pe){
			log.warn("ParseException", pe);
		    // System.out.println("position: " + pe.getPosition());
		    // System.out.println(pe);
		}
		*/

		log.info(String.format("TestNetAllDataType Network 입/출력 비교결과=[%s]", allDataTypeInObjStr.equals(allDataTypeOutObjStr)));
		
	}

}
