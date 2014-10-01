package kr.pe.sinnori.servlet;
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
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;

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
		
		AllDataType allDataTypeInObj = new AllDataType();

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
		allDataTypeInObj.setUnsignedShortVar2((int)0xffff);
		allDataTypeInObj.setUnsignedShortVar3((int) 32);
		allDataTypeInObj.setIntVar1(Integer.MAX_VALUE);
		allDataTypeInObj.setIntVar2(Integer.MIN_VALUE);
		allDataTypeInObj.setIntVar3(random.nextInt());
		allDataTypeInObj.setUnsignedIntVar1((long) 0);
		allDataTypeInObj.setUnsignedIntVar2((long) 0x7fffffff);
		allDataTypeInObj.setUnsignedIntVar3(Integer.MAX_VALUE  + 1000L);
		allDataTypeInObj.setLongVar1(Long.MAX_VALUE);
		allDataTypeInObj.setLongVar2(Long.MIN_VALUE);
		allDataTypeInObj.setLongVar3(random.nextLong());
		allDataTypeInObj.setStrVar1("testHH");
		allDataTypeInObj.setStrVar2("1234");
		allDataTypeInObj.setStrVar3("uiop");
		allDataTypeInObj.setBytesVar1(new byte[] { (byte) 0x77, (byte) 0x88, -128, -127, 126, 127, -1});
		allDataTypeInObj.setBytesVar2(ByteBuffer.allocate(30000).array());
		allDataTypeInObj.setSqldate(new java.sql.Date(new java.util.Date().getTime()));
		allDataTypeInObj.setSqltimestamp(new java.sql.Timestamp(new java.util.Date().getTime()));
		
		allDataTypeInObj.setCnt(2);
		
		AllDataType.Member[] memberList = new AllDataType.Member[allDataTypeInObj.getCnt()];
		for (int i=0; i < memberList.length; i++) {
			memberList[i] = allDataTypeInObj.new Member();
		}
		allDataTypeInObj.setMemberList(memberList);
		{
			memberList[0].setMemberID("test01ID");
			memberList[0].setMemberName("test01Name");
			memberList[0].setCnt(1);
			
			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[0].getCnt()];
			for (int i=0; i < itemList.length; i++) {
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
			
			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[1].getCnt()];
			for (int i=0; i < itemList.length; i++) {
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
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(allDataTypeInObj);

		String errorMessage = "";
		
		boolean isSame = false;
		if (messageFromServer instanceof AllDataType) {
			AllDataType allDataTypeOutObj = (AllDataType)messageFromServer;
			
			String allDataTypeInObjStr = allDataTypeInObj.toString();
			String allDataTypeOutObjStr = allDataTypeOutObj.toString();
			
			isSame = allDataTypeInObjStr.equals(allDataTypeOutObjStr);
			if (isSame) {
				if (! java.util.Arrays.equals(allDataTypeInObj.getBytesVar1(), allDataTypeOutObj.getBytesVar1()) 
						|| ! java.util.Arrays.equals(allDataTypeInObj.getBytesVar2(), allDataTypeOutObj.getBytesVar2())) { 
					//log.warn("2.입력과 출력 대조 결과 틀림");
					
					isSame = false;
				} /*else {
					//log.info("입력과 출력 같음");
				}	*/			
				
			} /*else {
				//log.warn("1.입력과 출력 대조 결과 틀림");
			}*/
			
			req.setAttribute("allDataTypeOutObj", allDataTypeOutObj);			
		} else {
			errorMessage = messageFromServer.toString();
			log.warn(errorMessage);
		}		
		
		req.setAttribute("isSame", isSame);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("allDataTypeInObj", allDataTypeInObj);
		
		
		printJspPage(req, res, goPage);
		
	}
	
}
