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
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.ItemGroupDataOfArray;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * 메시지를 꾸리는 로직.<br/>
 * 리플렉션을 이용한 자바빈즈 방법으로 구현한 메시지와<br/>
 * 해쉬를 기본으로 자체 구현한 메시지 이렇게 2개 방법중 <br/>
 * 메시지를 꾸리는 속도 차이를 알아 보기 위해 만든 로직이다.<br/>
 * 실험 결과 해쉬를 기본으로 자체 구현한 메시지가 <br/>
 * 리플렉션을 이용한 자바빈즈 방법으로 구현한 메시지 2배 정도 빨랐으며,<br/>
 * 특히 리플렉션을 이용한 자바빈즈 방법으로 구현한 메시지에서 <br/>
 * toString 메소드를 리플렉션으로 공통 메소드로 만들 경우 큰 속도 차이를 보였다.<br/>
 * toString 메소드를 이클립스를 통해서 쉽게 만들 수 있지만 <br/>
 * 이 방법은 개발자에게 짐을 지우기 때문에 신놀이 프레임 워크 목적에 부합하지 않는다.
 * 
 * @author Jonghoon Won
 *
 */
public class SpeedTestV001CExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException,
			DynamicClassCallException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, InterruptedException, MessageItemException {
		
		InputMessage inObj = null;
		try {
			inObj = messageManger.createInputMessage("AllDataType");
		} catch (MessageInfoNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		

		inObj.setAttribute("byteVar1", Byte.MAX_VALUE);
		inObj.setAttribute("byteVar2", Byte.MIN_VALUE);
		inObj.setAttribute("byteVar3", (byte) 0x60);

		inObj.setAttribute("unsignedByteVar1", (short) 0);
		inObj.setAttribute("unsignedByteVar2", (short) 0xff);
		inObj.setAttribute("unsignedByteVar3", (short) 0x65);

		inObj.setAttribute("shortVar1", Short.MAX_VALUE);
		inObj.setAttribute("shortVar2", Short.MIN_VALUE);
		inObj.setAttribute("shortVar3", (short) 30);

		inObj.setAttribute("unsignedShortVar1", (int) 0);
		inObj.setAttribute("unsignedShortVar2", (int) 0xffff);
		inObj.setAttribute("unsignedShortVar3", (int) (Short.MAX_VALUE + 1000));

		inObj.setAttribute("intVar1", Integer.MAX_VALUE);
		inObj.setAttribute("intVar2", Integer.MIN_VALUE);
		inObj.setAttribute("intVar3", 3);

		inObj.setAttribute("unsignedIntVar1", (long) 0);
		inObj.setAttribute("unsignedIntVar2", (long) 0x7fffffff);
		inObj.setAttribute("unsignedIntVar3", Integer.MAX_VALUE + (long) 1000);

		inObj.setAttribute("longVar1", Long.MAX_VALUE);
		inObj.setAttribute("longVar2", Long.MIN_VALUE);
		inObj.setAttribute("longVar3", (long)4);

		inObj.setAttribute("strVar1", "testHH");
		inObj.setAttribute("strVar2", "1234");
		inObj.setAttribute("strVar3", "uiop");

		inObj.setAttribute("bytesVar1", new byte[] { (byte) 0x77, (byte) 0x88 });
		// inObj.setAttribute("bytesVar2", new byte[] { 1, 2, 3, 4, 5, 6, 7, 8,
		// 9, 10, 11 });
		inObj.setAttribute("bytesVar2", ByteBuffer.allocate(8000).array());

		int memberListCnt = 2;
		inObj.setAttribute("cnt", memberListCnt);
		ArrayData memberListForArrayDataList = (ArrayData) inObj.getAttribute("memberList");

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
		
		inObj.toString();
		// log.info(inObj.toString());
	}
}
