package impl.executor.client;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestLocalAllDataTypeCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerExcecutorException, NotLoginException {
		AllDataType allDataType = new AllDataType();

		allDataType.setByteVar1(Byte.MAX_VALUE);
		allDataType.setByteVar2(Byte.MIN_VALUE);
		allDataType.setByteVar3((byte) 0x60);
		allDataType.setUnsignedByteVar1((short) 0);
		allDataType.setUnsignedByteVar2((short) 0xff);
		allDataType.setUnsignedByteVar3((short) 0x65);
		allDataType.setShortVar1(Short.MAX_VALUE);
		allDataType.setShortVar2(Short.MIN_VALUE);
		allDataType.setShortVar3((short) 31);
		allDataType.setUnsignedShortVar1(0);
		allDataType.setUnsignedShortVar2((int)0xffff);
		allDataType.setUnsignedShortVar3((int) 32);
		allDataType.setIntVar1(Integer.MAX_VALUE);
		allDataType.setIntVar2(Integer.MIN_VALUE);
		allDataType.setIntVar3((int) 33);
		allDataType.setUnsignedIntVar1((long) 0);
		allDataType.setUnsignedIntVar2((long) 0x7fffffff);
		allDataType.setUnsignedIntVar3(Integer.MAX_VALUE  + 1000L);
		allDataType.setLongVar1(Long.MAX_VALUE);
		allDataType.setLongVar2(Long.MIN_VALUE);
		allDataType.setLongVar3(34L);
		allDataType.setStrVar1("testHH");
		allDataType.setStrVar2("1234");
		allDataType.setStrVar3("uiop");
		allDataType.setBytesVar1(new byte[] { (byte) 0x77, (byte) 0x88, -128, -127, 126, 127, -1});
		allDataType.setBytesVar2(ByteBuffer.allocate(30000).array());
		allDataType.setCnt(2);
		
		AllDataType.Member[] memberList = new AllDataType.Member[allDataType.getCnt()];
		for (int i=0; i < memberList.length; i++) {
			memberList[i] = allDataType.new Member();
		}
		allDataType.setMemberList(memberList);
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
		}
		{
			memberList[1].setMemberID("test01ID");
			memberList[1].setMemberName("test01Name");
			memberList[1].setCnt(2);
			
			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[1].getCnt()];
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
		}
		
		
		log.info(allDataType.toString());
	}


}
