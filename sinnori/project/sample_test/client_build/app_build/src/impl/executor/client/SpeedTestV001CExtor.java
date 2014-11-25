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
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
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
 * @author Won Jonghoon
 *
 */
public class SpeedTestV001CExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerTaskException, NotLoginException {
		
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
		allDataTypeInObj.setIntVar3((int) 33);
		allDataTypeInObj.setUnsignedIntVar1((long) 0);
		allDataTypeInObj.setUnsignedIntVar2((long) 0x7fffffff);
		allDataTypeInObj.setUnsignedIntVar3(Integer.MAX_VALUE  + 1000L);
		allDataTypeInObj.setLongVar1(Long.MAX_VALUE);
		allDataTypeInObj.setLongVar2(Long.MIN_VALUE);
		allDataTypeInObj.setLongVar3(34L);
		allDataTypeInObj.setStrVar1("testHH");
		allDataTypeInObj.setStrVar2("1234");
		allDataTypeInObj.setStrVar3("uiop");
		allDataTypeInObj.setBytesVar1(new byte[] { (byte) 0x77, (byte) 0x88, -128, -127, 126, 127, -1});
		allDataTypeInObj.setBytesVar2(ByteBuffer.allocate(30000).array());
		allDataTypeInObj.setSqldate(new java.sql.Date(new java.util.Date().getTime()));
		allDataTypeInObj.setSqltimestamp(new java.sql.Timestamp(new java.util.Date().getTime()));
		allDataTypeInObj.setCnt(2);
		
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
		
		allDataTypeInObj.toString();
		// log.info(inObj.toString());
	}
}
