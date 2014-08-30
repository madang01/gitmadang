package impl.executor.client;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.AllDataType.AllDataType;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestLocalAllDataTypeCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerExcecutorException, NotLoginException {
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
		
		ClientObjectCacheManagerIF clientObjectCacheManager = (ClientObjectCacheManagerIF)clientProject;
		MessageProtocolIF messageProtocol = clientProject.getMessageProtocol();
		ClassLoader classLoader =TestLocalAllDataTypeCExtor.class.getClassLoader();
		
		MessageCodecIF messageCodec = null;
		
		
		try {
			messageCodec = clientObjectCacheManager.getClientCodec(classLoader, allDataTypeInObj.getMessageID());
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		
		MessageEncoder  messageEncoder  = null;
		
		try {
			messageEncoder = messageCodec.getMessageEncoder();
		} catch(DynamicClassCallException e) {
			// log.warn(e.getMessage());
			
			// throw new DynamicClassCallException(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage());
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		ArrayList<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(allDataTypeInObj, messageEncoder, clientProjectConfig.getCharset());
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(DynamicClassCallException e) {
			throw e;
		} catch(BodyFormatException e) {
			throw e;
		} catch(Exception e) {
			log.warn("unkown error", e);
			throw new BodyFormatException("unkown error::"+e.getMessage());
		}
		
		/** 데이터를 받은것처럼 위장하기 위한 ByteBuffer 속성 조작 */
		for (WrapBuffer wrapBuffer : wrapBufferList) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.limit());
			byteBuffer.limit(byteBuffer.capacity());
		}
		
		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF)clientProject;
		SocketInputStream socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
		ArrayList<ReceivedLetter> receivedLetterList = null;
		try {
			receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			
			for (ReceivedLetter receivedLetter : receivedLetterList) {
				AbstractMessage outObj = getMessageFromMiddleReadObj(classLoader, clientProjectConfig, clientObjectCacheManager, messageProtocol, receivedLetter);
				
				if (outObj.getMessageID().equals(allDataTypeInObj.getMessageID())) {
					String allDataTypeInObjStr = allDataTypeInObj.toString();
					AllDataType allDataTypeOutObj = (AllDataType)outObj;
					String allDataTypeOutObjStr = allDataTypeOutObj.toString();
					
					//log.info(allDataTypeInObjStr);
					// log.info("출력::"+allDataTypeOutObjStr);					
					
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
					log.info(outObj.toString());
				}
			}
		} catch (HeaderFormatException e) {
			e.printStackTrace();
			// System.exit(1);
		} finally {
			socketInputStream.destory();
		}		
	}

	private AbstractMessage getMessageFromMiddleReadObj(ClassLoader classLoader, 
			ClientProjectConfig clientProjectConfig,
			ClientObjectCacheManagerIF clientObjectCacheManager,
			MessageProtocolIF messageProtocol, 
			ReceivedLetter receivedLetter) throws DynamicClassCallException, BodyFormatException {
		String messageID = receivedLetter.getMessageID();
		int mailboxID = receivedLetter.getMailboxID();
		int mailID = receivedLetter.getMailID();
		Object middleReadObj = receivedLetter.getMiddleReadObj();
		
		MessageCodecIF messageCodec = null;
		
		try {
			messageCodec = clientObjectCacheManager.getClientCodec(classLoader, messageID);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		
		
		MessageDecoder  messageDecoder  = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = String.format("클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.", messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage, mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.", messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage, mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractMessage messageObj = null;
		try {
			messageObj = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
			messageObj.messageHeaderInfo.mailboxID = mailboxID;
			messageObj.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (OutOfMemoryError e) {
			String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		}
		
		return messageObj;
	}

}
