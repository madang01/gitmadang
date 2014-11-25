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
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestNetAllDataTypeCExtor extends AbstractClientExecutor {

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
		
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(allDataTypeInObj);
		//log.info("1111111111111");
		// log.info(messageFromServer.toString());
		if (messageFromServer instanceof AllDataType) {
			AllDataType allDataTypeOutObj = (AllDataType)messageFromServer;
			
			String allDataTypeInObjStr = allDataTypeInObj.toString();
			String allDataTypeOutObjStr = allDataTypeOutObj.toString();
			
			boolean result = allDataTypeInObjStr.equals(allDataTypeOutObjStr);
			if (! result) {
				log.warn("1.입력과 출력 대조 결과 틀림");
				
			} else {
				if (! java.util.Arrays.equals(allDataTypeInObj.getBytesVar1(), allDataTypeOutObj.getBytesVar1()) 
						|| ! java.util.Arrays.equals(allDataTypeInObj.getBytesVar2(), allDataTypeOutObj.getBytesVar2())) { 
					log.warn("2.입력과 출력 대조 결과 틀림");
				} else {
					//log.info("입력과 출력 같음");
				}
			}
		} else {
			log.warn("messageFromServer={}", messageFromServer.toString());
		}
	}
}
