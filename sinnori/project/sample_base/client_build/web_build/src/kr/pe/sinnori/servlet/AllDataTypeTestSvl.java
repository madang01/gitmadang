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
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

/**
 * 모든 데이터 타입을 갖는 AllDataType 메시지 교환 테스트
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class AllDataTypeTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String goPage = "/menu/testcode/AllDataType01.jsp";

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
		
		//int memberListSize = allDataTypeInObj.getCnt();
		java.util.List<AllDataType.Member> memberList = new java.util.ArrayList<AllDataType.Member>();
		
		{	
			/** memberList[0] */
			AllDataType.Member member = new AllDataType.Member();			
			member.setMemberID("test01ID");
			member.setMemberName("test01Name");
			member.setCnt(1);
			
			// int itemListSize = member.getCnt();
			java.util.List<AllDataType.Member.Item> itemList = new java.util.ArrayList<AllDataType.Member.Item>();
			member.setItemList(itemList);
			{
				/** memberList[0].itemList[0] */
				AllDataType.Member.Item item = new AllDataType.Member.Item();			
				item.setItemID("1");
				item.setItemName("최강의검");
				item.setItemCnt(1);
				itemList.add(item);
			}
			
			memberList.add(member);
		}
		{
			/** memberList[1] */
			AllDataType.Member member = new AllDataType.Member();			
			member.setMemberID("test01ID");
			member.setMemberName("test01Name");
			member.setCnt(2);
			
			// int itemListSize = member.getCnt();
			java.util.List<AllDataType.Member.Item> itemList = new java.util.ArrayList<AllDataType.Member.Item>();
			member.setItemList(itemList);
			{
				/** memberList[1].itemList[0] */
				AllDataType.Member.Item item = new AllDataType.Member.Item();							
				item.setItemID("2");
				item.setItemName("살살검");
				item.setItemCnt(5);
				
				itemList.add(item);
			}
			{
				/** memberList[1].itemList[1] */
				AllDataType.Member.Item item = new AllDataType.Member.Item();			
				item.setItemID("3");
				item.setItemName("안좋은검");
				item.setItemCnt(100);
				
				itemList.add(item);
			}
			memberList.add(member);
		}
		
		allDataTypeInObj.setMemberList(memberList);
			
		ClientProject clientProject = ClientProjectManager.getInstance().getMainClientProject();
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
			errorMessage = "모든 데이터 타입 응답 메시지를 얻는데 실패하였습니다.";
			
			if (!(messageFromServer instanceof SelfExn)) {
				log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", allDataTypeInObj.toString(), messageFromServer.toString());
			} else {
				log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", allDataTypeInObj.toString(), messageFromServer.toString());
			}
		}		
		
		req.setAttribute("isSame", isSame);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("allDataTypeInObj", allDataTypeInObj);
		
		
		printWebLayoutControlJspPage(req, res, goPage);
		
	}
	
}
