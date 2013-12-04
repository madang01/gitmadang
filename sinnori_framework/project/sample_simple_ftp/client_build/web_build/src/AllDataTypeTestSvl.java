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


import java.nio.ByteBuffer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.ItemGroupDataOfArray;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;

/**
 * 모든 데이터 타입을 갖는 AllDataType 메시지 교환 테스트
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class AllDataTypeTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String goPage = "/testcode/AllDataType01.jsp";

		java.util.Random random = new java.util.Random();
		
		String projectName = System.getenv("SINNORI_PROJECT_NAME");
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		
		InputMessage allDataTypeInObj = null;
		try {
			allDataTypeInObj = clientProject.createInputMessage("AllDataType");
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
		allDataTypeInObj.setAttribute("bytesVar2", ByteBuffer.allocate(9000).array());

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

		
		String errorMessage = "";
		
		LetterFromServer letterFromServer = clientProject.sendInputMessage(allDataTypeInObj);

		if (null == letterFromServer) {
			errorMessage = String.format("input message[%s] letterList is null", allDataTypeInObj.getMessageID()); 
			log.warn(errorMessage);
		} else {
			OutputMessage allDataTypeOutObj = letterFromServer.getOutputMessage("AllDataType");

			// FXIME!
			// log.info("OutputMessage[%s], bytesVar2 length=[%d]", allDataTypeOutObj.toString(), ((byte[]) allDataTypeOutObj.getAttribute("bytesVar2")).length);

			String allDataTypeInObjStr = allDataTypeInObj.toString();
			String allDataTypeOutObjStr = allDataTypeOutObj.toString();

			// log.info("allDataTypeInObj Str=[%s]", allDataTypeInObjStr);
			//log.info(String.format("allDataTypeOutObj Str=[%s]", allDataTypeOutObjStr));

			//log.info(String.format("TestNetAllDataType Network 1.입/출력 비교결과=[%s]", allDataTypeInObjStr.equals(allDataTypeOutObjStr)));
			
			req.setAttribute("allDataTypeOutObj", allDataTypeOutObj);
			req.setAttribute("isSame", allDataTypeInObjStr.equals(allDataTypeOutObjStr));
		}
		
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("allDataTypeInObj", allDataTypeInObj);
		
		
		printJspPage(req, res, goPage);
		
	}
	
}
