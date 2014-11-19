/**
 * 
 */
package impl.executor.client;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.BigByte.BigByte;
import kr.pe.sinnori.impl.message.BigByte.BigByteDecoder;
import kr.pe.sinnori.impl.message.BigByte.BigByteEncoder;
import kr.pe.sinnori.impl.message.BigFixedLenString.BigFixedLenString;
import kr.pe.sinnori.impl.message.BigFixedLenString.BigFixedLenStringDecoder;
import kr.pe.sinnori.impl.message.BigFixedLenString.BigFixedLenStringEncoder;
import kr.pe.sinnori.impl.message.BigInt.BigInt;
import kr.pe.sinnori.impl.message.BigInt.BigIntDecoder;
import kr.pe.sinnori.impl.message.BigInt.BigIntEncoder;
import kr.pe.sinnori.impl.message.BigLong.BigLong;
import kr.pe.sinnori.impl.message.BigLong.BigLongDecoder;
import kr.pe.sinnori.impl.message.BigLong.BigLongEncoder;
import kr.pe.sinnori.impl.message.BigPascalString.BigPascalString;
import kr.pe.sinnori.impl.message.BigPascalString.BigPascalStringDecoder;
import kr.pe.sinnori.impl.message.BigPascalString.BigPascalStringEncoder;
import kr.pe.sinnori.impl.message.BigShort.BigShort;
import kr.pe.sinnori.impl.message.BigShort.BigShortDecoder;
import kr.pe.sinnori.impl.message.BigShort.BigShortEncoder;
import kr.pe.sinnori.impl.message.BigUByte.BigUByte;
import kr.pe.sinnori.impl.message.BigUByte.BigUByteDecoder;
import kr.pe.sinnori.impl.message.BigUByte.BigUByteEncoder;
import kr.pe.sinnori.impl.message.BigUInt.BigUInt;
import kr.pe.sinnori.impl.message.BigUInt.BigUIntDecoder;
import kr.pe.sinnori.impl.message.BigUInt.BigUIntEncoder;
import kr.pe.sinnori.impl.message.BigUShort.BigUShort;
import kr.pe.sinnori.impl.message.BigUShort.BigUShortDecoder;
import kr.pe.sinnori.impl.message.BigUShort.BigUShortEncoder;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * @author Won Jonghoon
 *
 */
public class TestBigSizeMessagesCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException,
			ServerTaskException, NotLoginException {
		
		// FreeSizeOutputStream fsos = null;
		ArrayList<WrapBuffer> wrapBufferList = null;
		// FreeSizeInputStream fsis = null;
		int warpBufferListSize = 0;
		// log.info(String.format("before DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
		
		
		MessageProtocolIF messageProtocol = clientProject.getMessageProtocol();		
		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF)clientProject;
		SocketInputStream socketInputStream = null;
		ArrayList<ReceivedLetter> receivedLetterList = null;
		ReceivedLetter receivedLetter = null;
		Object middleReadObj = null;
		AbstractMessage messageObj = null;
		int outObjListSize;
		
		String messageID = null;
		
		int startSize=4090;
		int endSize=4091;
		messageID = "BigByte";
		
		for (; startSize <= endSize; startSize++) {
			BigByte bigByteInObj = new BigByte();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigByteInObj.setAttribute("filler1", new byte[startSize]);
			bigByteInObj.setAttribute("value1", Byte.MIN_VALUE);
			bigByteInObj.setAttribute("value2", (byte) 0x60);*/
			bigByteInObj.setFiller1(new byte[startSize]);
			bigByteInObj.setValue1(Byte.MIN_VALUE);
			bigByteInObj.setValue2((byte) 0x60);
			
			// log.debug(bigByteInObj.toString());
			
			try {
				wrapBufferList = messageProtocol.M2S(bigByteInObj, new BigByteEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigByteDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigByte bigByteOutObj = (BigByte)messageObj;
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigByteInObjStr = bigByteInObj.toString();
			String bigByteOutObjStr = bigByteOutObj.toString();
			
			// log.info(bigByteInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigByteInObjStr.equals(bigByteOutObjStr)));
		}
		
		startSize=4090;
		endSize=4091;
		messageID = "BigUByte";
		for (; startSize <= endSize; startSize++) {
			//InputMessage bigUByteInObj = messageManger.createInputMessage(messageID);
			BigUByte bigUByteInObj = new BigUByte();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigUByteInObj.setAttribute("filler1", new byte[startSize]);
			bigUByteInObj.setAttribute("value1", (short)Byte.MAX_VALUE);
			bigUByteInObj.setAttribute("value2", (short) 0x10);*/
			bigUByteInObj.setFiller1(new byte[startSize]);
			bigUByteInObj.setValue1((short)Byte.MAX_VALUE);
			bigUByteInObj.setValue2((short) 0x10);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigUByteInObj, new BigUByteEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigUByteDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigUByte bigUByteOutObj = (BigUByte)messageObj;
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigUByteInObjStr = bigUByteInObj.toString();
			String bigUByteOutObjStr = bigUByteOutObj.toString();
			
			// log.info(bigUByteInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigUByteInObjStr.equals(bigUByteOutObjStr)));
		}
		
		
		startSize=4088;
		endSize=4090;
		messageID = "BigShort";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigShortInObj = messageManger.createInputMessage(messageID);
			BigShort bigShortInObj = new BigShort();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigShortInObj.setAttribute("filler1", new byte[startSize]);
			bigShortInObj.setAttribute("value1", (short)Short.MAX_VALUE);
			bigShortInObj.setAttribute("value2", (short) 0x1110);*/
			
			bigShortInObj.setFiller1(new byte[startSize]);
			bigShortInObj.setValue1((short)Byte.MAX_VALUE);
			bigShortInObj.setValue2((short) 0x1110);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigShortInObj, new BigShortEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigShortDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigShort bigShortOutObj = (BigShort)messageObj;
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigShortInObjStr = bigShortInObj.toString();
			String bigShortOutObjStr = bigShortOutObj.toString();
			
			// log.info(bigShortInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigShortInObjStr.equals(bigShortOutObjStr)));
		}
		
		
		startSize=4088;
		endSize=4090;
		messageID = "BigUShort";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigUShortInObj = messageManger.createInputMessage(messageID);
			BigUShort bigUShortInObj = new BigUShort();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigUShortInObj.setAttribute("filler1", new byte[startSize]);
			bigUShortInObj.setAttribute("value1", (int)Short.MAX_VALUE);
			bigUShortInObj.setAttribute("value2", (int) 0x1211);*/
			
			
			bigUShortInObj.setFiller1(new byte[startSize]);
			bigUShortInObj.setValue1((int)Byte.MAX_VALUE);
			bigUShortInObj.setValue2((int) 0x1110);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigUShortInObj, new BigUShortEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigUShortDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigUShort bigUShortOutObj = (BigUShort)messageObj;
			
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigUShortInObjStr = bigUShortInObj.toString();
			String bigUShortOutObjStr = bigUShortOutObj.toString();
			
			// log.info(bigUShortInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigUShortInObjStr.equals(bigUShortOutObjStr)));
		}
		
		
		startSize=4084;
		endSize=4088;
		messageID = "BigInt";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigIntInObj = messageManger.createInputMessage(messageID);
			BigInt bigIntInObj = new BigInt();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigIntInObj.setAttribute("filler1", new byte[startSize]);
			bigIntInObj.setAttribute("value1", Integer.MAX_VALUE);
			bigIntInObj.setAttribute("value2", (int) 0x11221110);*/
			
			
			bigIntInObj.setFiller1(new byte[startSize]);
			bigIntInObj.setValue1((int)Byte.MAX_VALUE);
			bigIntInObj.setValue2((int) 0x11221110);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigIntInObj, new BigIntEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigIntDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigInt bigIntOutObj = (BigInt)messageObj;			
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigIntInObjStr = bigIntInObj.toString();
			String bigIntOutObjStr = bigIntOutObj.toString();
			
			// log.info(bigIntInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigIntInObjStr.equals(bigIntOutObjStr)));
		}
		
		startSize=4084;
		endSize=4088;
		messageID = "BigUInt";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigUIntInObj = messageManger.createInputMessage(messageID);
			BigUInt bigUIntInObj = new BigUInt();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigUIntInObj.setAttribute("filler1", new byte[startSize]);
			bigUIntInObj.setAttribute("value1", (long)Integer.MAX_VALUE);
			bigUIntInObj.setAttribute("value2", (long) 0x11231111);*/
			
			bigUIntInObj.setFiller1(new byte[startSize]);
			bigUIntInObj.setValue1((long)Byte.MAX_VALUE);
			bigUIntInObj.setValue2((long) 0x11231111);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigUIntInObj, new BigUIntEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigUIntDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigUInt bigUIntOutObj = (BigUInt)messageObj;
			
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigUIntInObjStr = bigUIntInObj.toString();
			String bigUIntOutObjStr = bigUIntOutObj.toString();
			
			// log.info(bigUIntInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigUIntInObjStr.equals(bigUIntOutObjStr)));
		}
		
		startSize=4076;
		endSize=4084;
		messageID = "BigLong";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigLongInObj = messageManger.createInputMessage(messageID);
			BigLong bigLongInObj = new BigLong();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigLongInObj.setAttribute("filler1", new byte[startSize]);
			bigLongInObj.setAttribute("value1", Long.MAX_VALUE);
			bigLongInObj.setAttribute("value2", (long)0x1122111011221110L);*/
			
			bigLongInObj.setFiller1(new byte[startSize]);
			bigLongInObj.setValue1((long)Byte.MAX_VALUE);
			bigLongInObj.setValue2((long) 0x11231111);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigLongInObj, new BigLongEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigLongDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigLong bigLongOutObj = (BigLong)messageObj;
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigLongInObjStr = bigLongInObj.toString();
			String bigLongOutObjStr = bigLongOutObj.toString();
			
			// log.info(bigLongInObjStr);
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigLongInObjStr.equals(bigLongOutObjStr)));
		}
		
		
		String value1 = "한글a사랑";
		startSize=4082;
		endSize=4092;
		
		
		messageID = "BigFixedLenString";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigFixedLenStringInObj = messageManger.createInputMessage(messageID);
			BigFixedLenString bigFixedLenStringInObj = new BigFixedLenString();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigFixedLenStringInObj.setAttribute("filler1", new byte[startSize]); // 데이터 패킷 맨끝 크기 4082 byte
			bigFixedLenStringInObj.setAttribute("value1", value1);*/
			
			bigFixedLenStringInObj.setFiller1(new byte[startSize]);
			bigFixedLenStringInObj.setValue1(value1);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigFixedLenStringInObj, new BigFixedLenStringEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigFixedLenStringDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigFixedLenString bigFixedLenStringOutObj = (BigFixedLenString)messageObj;
			
			// String value1OfOutObj = (String)bigFixedLenStringOutObj.getAttribute("value1");
			// bigFixedLenStringOutObj.setAttribute("value1", value1OfOutObj.trim());
			String value1OfOutObj = bigFixedLenStringOutObj.getValue1();
			if (null != value1OfOutObj) {
				bigFixedLenStringOutObj.setValue1(value1OfOutObj.trim());
			}
			
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigFixedLenStringInObjStr = bigFixedLenStringInObj.toString();
			String bigFixedLenStringOutObjStr = bigFixedLenStringOutObj.toString();
			
			// log.info(bigFixedLenStringInObjStr);
			// log.info(bigFixedLenStringOutObjStr);
			
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigFixedLenStringInObjStr.equals(bigFixedLenStringOutObjStr)));
		}

		value1 = "한글a사랑그";
		startSize=4080;
		endSize=4091;
		messageID = "BigPascalString";
		for (; startSize <= endSize; startSize++) {
			// InputMessage bigFixedLenStringInObj = messageManger.createInputMessage(messageID);
			BigPascalString bigPascalStringInObj = new BigPascalString();
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			/*bigFixedLenStringInObj.setAttribute("filler1", new byte[startSize]); // 데이터 패킷 맨끝 크기 4082 byte
			bigFixedLenStringInObj.setAttribute("value1", value1);*/
			
			bigPascalStringInObj.setFiller1(new byte[startSize]);
			bigPascalStringInObj.setValue1(value1);
			
			try {
				wrapBufferList = messageProtocol.M2S(bigPascalStringInObj, new BigPascalStringEncoder(), clientProjectConfig.getCharset());
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
			
			socketInputStream = new SocketInputStream(wrapBufferList, dataPacketBufferQueueManager);
			try {
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), socketInputStream);
			} catch (HeaderFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} finally {
				socketInputStream.destory();
			}
			
			outObjListSize = receivedLetterList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			receivedLetter = receivedLetterList.get(0);
			
			middleReadObj = receivedLetter.getMiddleReadObj();
			try {
				messageObj = new BigPascalStringDecoder().decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				messageObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			} catch (BodyFormatException e) {
				String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, receivedLetter.getMailboxID(), receivedLetter.getMailID(), e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}
			
			BigPascalString bigPascalStringOutObj = (BigPascalString)messageObj;
			
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigPascalStringInObjStr = bigPascalStringInObj.toString();
			String bigPascalStringOutObjStr = bigPascalStringOutObj.toString();
			
			// log.info(bigFixedLenStringInObjStr);
			// log.info(bigFixedLenStringOutObjStr);
			
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigPascalStringInObjStr.equals(bigPascalStringOutObjStr)));
		}
		
	}

}
