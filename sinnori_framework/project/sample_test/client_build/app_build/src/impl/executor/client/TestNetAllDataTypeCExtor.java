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
					log.info("입력과 출력 같음");
				}
			}
		} else {
			log.warn("messageFromServer={}", messageFromServer.toString());
		}
	}
}
