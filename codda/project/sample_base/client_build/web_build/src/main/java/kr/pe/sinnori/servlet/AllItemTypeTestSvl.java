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

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.AllItemType.AllItemType;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;

/**
 * 모든 데이터 타입을 갖는 AllItemType 메시지 교환 테스트
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class AllItemTypeTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.TEST_EXAMPLE);
	
		java.util.Random random = new java.util.Random();
		
		AllItemType allDataTypeReq = new AllItemType();

		allDataTypeReq.setByteVar1(Byte.MAX_VALUE);
		allDataTypeReq.setByteVar2(Byte.MIN_VALUE);
		allDataTypeReq.setByteVar3((byte) 0x60);
		allDataTypeReq.setUnsignedByteVar1((short) 0);
		allDataTypeReq.setUnsignedByteVar2((short) 0xff);
		allDataTypeReq.setUnsignedByteVar3((short) 0x65);
		allDataTypeReq.setShortVar1(Short.MAX_VALUE);
		allDataTypeReq.setShortVar2(Short.MIN_VALUE);
		allDataTypeReq.setShortVar3((short) 31);
		allDataTypeReq.setUnsignedShortVar1(0);
		allDataTypeReq.setUnsignedShortVar2((int)0xffff);
		allDataTypeReq.setUnsignedShortVar3((int) 32);
		allDataTypeReq.setIntVar1(Integer.MAX_VALUE);
		allDataTypeReq.setIntVar2(Integer.MIN_VALUE);
		allDataTypeReq.setIntVar3(random.nextInt());
		allDataTypeReq.setUnsignedIntVar1((long) 0);
		allDataTypeReq.setUnsignedIntVar2((long) 0x7fffffff);
		allDataTypeReq.setUnsignedIntVar3(Integer.MAX_VALUE  + 1000L);
		allDataTypeReq.setLongVar1(Long.MAX_VALUE);
		allDataTypeReq.setLongVar2(Long.MIN_VALUE);
		allDataTypeReq.setLongVar3(random.nextLong());
		allDataTypeReq.setStrVar1("testHH");
		allDataTypeReq.setStrVar2("1234");
		allDataTypeReq.setStrVar3("uiop");
		allDataTypeReq.setBytesVar1(new byte[] { (byte) 0x77, (byte) 0x88, -128, -127, 126, 127, -1});
		allDataTypeReq.setBytesVar2(ByteBuffer.allocate(30000).array());
		allDataTypeReq.setSqldate(new java.sql.Date(new java.util.Date().getTime()));
		allDataTypeReq.setSqltimestamp(new java.sql.Timestamp(new java.util.Date().getTime()));
		
		allDataTypeReq.setCnt(2);
		
		//int memberListSize = allDataTypeInObj.getCnt();
		java.util.List<AllItemType.Member> memberList = new java.util.ArrayList<AllItemType.Member>();
		
		{	
			/** memberList[0] */
			AllItemType.Member member = new AllItemType.Member();			
			member.setMemberID("test01ID");
			member.setMemberName("test01Name");
			member.setCnt(1);
			
			// int itemListSize = member.getCnt();
			java.util.List<AllItemType.Member.Item> itemList = new java.util.ArrayList<AllItemType.Member.Item>();
			member.setItemList(itemList);
			{
				/** memberList[0].itemList[0] */
				AllItemType.Member.Item item = new AllItemType.Member.Item();			
				item.setItemID("1");
				item.setItemName("최강의검");
				item.setItemCnt(1);
				
				java.util.List<AllItemType.Member.Item.SubItem> subItemList 
					= new java.util.ArrayList<AllItemType.Member.Item.SubItem>();
				
				item.setSubItemList(subItemList);
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(1);
					subItem.setSubItemID("sub01");
					subItem.setSubItemName("칼집");
					
					subItemList.add(subItem);
				} 
				
				itemList.add(item);
			}
			
			memberList.add(member);
		}
		{
			/** memberList[1] */
			AllItemType.Member member = new AllItemType.Member();			
			member.setMemberID("test01ID");
			member.setMemberName("test01Name");
			member.setCnt(2);
			
			// int itemListSize = member.getCnt();
			java.util.List<AllItemType.Member.Item> itemList = new java.util.ArrayList<AllItemType.Member.Item>();
			member.setItemList(itemList);
			{
				/** memberList[1].itemList[0] */
				AllItemType.Member.Item item = new AllItemType.Member.Item();							
				item.setItemID("2");
				item.setItemName("살살검");
				item.setItemCnt(5);
				
				java.util.List<AllItemType.Member.Item.SubItem> subItemList 
					= new java.util.ArrayList<AllItemType.Member.Item.SubItem>();			
				item.setSubItemList(subItemList);
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(1);
					subItem.setSubItemID("sub02");
					subItem.setSubItemName("신발");
					
					subItemList.add(subItem);
				} 
				
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(2);
					subItem.setSubItemID("sub03");
					subItem.setSubItemName("모자");
					
					subItemList.add(subItem);
				} 
				
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(1);
					subItem.setSubItemID("sub04");
					subItem.setSubItemName("장갑");
					
					subItemList.add(subItem);
				} 
				
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(1);
					subItem.setSubItemID("sub05");
					subItem.setSubItemName("배낭");
					
					subItemList.add(subItem);
				} 
				
				{
					AllItemType.Member.Item.SubItem subItem = new AllItemType.Member.Item.SubItem();
					subItem.setItemCnt(1);
					subItem.setSubItemID("sub06");
					subItem.setSubItemName("배낭");
					
					subItemList.add(subItem);
				} 
				
				itemList.add(item);
			}
			{
				/** memberList[1].itemList[1] */
				AllItemType.Member.Item item = new AllItemType.Member.Item();			
				item.setItemID("3");
				item.setItemName("안좋은검");
				item.setItemCnt(0);
				
				java.util.List<AllItemType.Member.Item.SubItem> subItemList 
				= new java.util.ArrayList<AllItemType.Member.Item.SubItem>();			
				item.setSubItemList(subItemList);				
				
				itemList.add(item);
			}
			memberList.add(member);
		}
		
		allDataTypeReq.setMemberList(memberList);
			
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(allDataTypeReq);		
		
		boolean isSame = false;
		if (outputMessage instanceof AllItemType) {
			AllItemType allItemTypeRes = (AllItemType)outputMessage;
			
			String allDataTypeInObjStr = allDataTypeReq.toString();
			
			
			java.util.List<AllItemType.Member> receviedMemberList = allItemTypeRes.getMemberList();
			
			for (AllItemType.Member member : receviedMemberList) {
				member.setMemberID(member.getMemberID().trim());
				member.setMemberName(member.getMemberName().trim());
				
				java.util.List<AllItemType.Member.Item> receviedItemList = member.getItemList();
				for (AllItemType.Member.Item item : receviedItemList) {
					item.setItemID(item.getItemID().trim());
					item.setItemName(item.getItemName().trim());
				}
			}
			
			String allDataTypeOutObjStr = allItemTypeRes.toString();			
			isSame = allDataTypeInObjStr.equals(allDataTypeOutObjStr);
			
			doFirstPage(req, res, allDataTypeReq, isSame, allItemTypeRes);
		} else {
			String errorMessage = "모든 데이터 타입 응답 메시지를 얻는데 실패하였습니다.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(allDataTypeReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		
	}	
	
	private void doFirstPage(HttpServletRequest req, HttpServletResponse res,
			AllItemType allDataTypeReq,
			boolean isSame,
			AllItemType allItemTypeRes) {
		req.setAttribute("allDataTypeReq", allDataTypeReq);
		req.setAttribute("isSame", isSame);		
		req.setAttribute("allItemTypeRes", allItemTypeRes);
		printJspPage(req, res, "/menu/testcode/AllItemType01.jsp");
	}
}
