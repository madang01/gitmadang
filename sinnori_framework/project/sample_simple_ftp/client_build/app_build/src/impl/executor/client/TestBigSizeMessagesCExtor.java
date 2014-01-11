/**
 * 
 */
package impl.executor.client;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.MessageProtocolIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.util.AbstractClientExecutor;

/**
 * @author Jonghoon Won
 *
 */
public class TestBigSizeMessagesCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, InterruptedException, MessageItemException {
		
		
		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF)clientProject;
		// FreeSizeOutputStream fsos = null;
		ArrayList<WrapBuffer> warpBufferList = null;
		// FreeSizeInputStream fsis = null;
		int warpBufferListSize = 0;
		// log.info(String.format("before DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
		
		MessageProtocolIF messageExchangeProtocol = clientProject.getMessageExchangeProtocol();
		
		
		String messageID = null;
		
		int startSize=4090;
		int endSize=4091;
		messageID = "BigByte";
		
		
		for (; startSize <= endSize; startSize++) {
			InputMessage bigByteInObj = messageManger.createInputMessage(messageID);
			
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigByteInObj.setAttribute("filler1", new byte[startSize]);
			bigByteInObj.setAttribute("value1", Byte.MIN_VALUE);
			bigByteInObj.setAttribute("value2", (byte) 0x60);
			
			// log.debug(bigByteInObj.toString());
			
			warpBufferList = messageExchangeProtocol.M2S(bigByteInObj, clientProjectConfig.getCharset());

			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigByteOutObj = (OutputMessage)outObjList.get(0);
			
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
			InputMessage bigUByteInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigUByteInObj.setAttribute("filler1", new byte[startSize]);
			bigUByteInObj.setAttribute("value1", (short)Byte.MAX_VALUE);
			bigUByteInObj.setAttribute("value2", (short) 0x10);
			
			warpBufferList = messageExchangeProtocol.M2S(bigUByteInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			// log.info(String.format("messageID[%s].M2BDHB FreeSizeOutputStream position=[%d], remaining=[%d]", messageID, fsos.postion(), fsos.remaining()));
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigUByteOutObj = (OutputMessage)outObjList.get(0);
			
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
			InputMessage bigShortInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigShortInObj.setAttribute("filler1", new byte[startSize]);
			bigShortInObj.setAttribute("value1", (short)Short.MAX_VALUE);
			bigShortInObj.setAttribute("value2", (short) 0x1110);
			
			warpBufferList = messageExchangeProtocol.M2S(bigShortInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigShortOutObj = (OutputMessage)outObjList.get(0);
			
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
			InputMessage bigUShortInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigUShortInObj.setAttribute("filler1", new byte[startSize]);
			bigUShortInObj.setAttribute("value1", (int)Short.MAX_VALUE);
			bigUShortInObj.setAttribute("value2", (int) 0x1211);
			
			warpBufferList = messageExchangeProtocol.M2S(bigUShortInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigUShortOutObj = (OutputMessage)outObjList.get(0);
			
			
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
			InputMessage bigIntInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigIntInObj.setAttribute("filler1", new byte[startSize]);
			bigIntInObj.setAttribute("value1", Integer.MAX_VALUE);
			bigIntInObj.setAttribute("value2", (int) 0x11221110);
			
			warpBufferList = messageExchangeProtocol.M2S(bigIntInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigIntOutObj = (OutputMessage)outObjList.get(0);
			
			
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
			InputMessage bigUIntInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigUIntInObj.setAttribute("filler1", new byte[startSize]);
			bigUIntInObj.setAttribute("value1", (long)Integer.MAX_VALUE);
			bigUIntInObj.setAttribute("value2", (long) 0x11231111);
			
			warpBufferList = messageExchangeProtocol.M2S(bigUIntInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigUIntOutObj = (OutputMessage)outObjList.get(0);
			
			
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
			InputMessage bigLongInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigLongInObj.setAttribute("filler1", new byte[startSize]);
			bigLongInObj.setAttribute("value1", Long.MAX_VALUE);
			bigLongInObj.setAttribute("value2", (long)0x1122111011221110L);
			
			warpBufferList = messageExchangeProtocol.M2S(bigLongInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigLongOutObj = (OutputMessage)outObjList.get(0);
			
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
			InputMessage bigFixedLenStringInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigFixedLenStringInObj.setAttribute("filler1", new byte[startSize]); // 데이터 패킷 맨끝 크기 4082 byte
			bigFixedLenStringInObj.setAttribute("value1", value1);
			
			warpBufferList = messageExchangeProtocol.M2S(bigFixedLenStringInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			
			// 문자셋 잘 동작 하는지 테스트를 위한 코드
			//byte charsetBytesOfValue1[] = null;
			//try {
			//	charsetBytesOfValue1 = value1.getBytes("EUC-KR");
			//	log.info(String.format("%s EUC_KR bytes=[%s]", value1, HexUtil.byteArrayAllToHex(charsetBytesOfValue1)));
			//} catch (UnsupportedEncodingException e) {
			//	e.printStackTrace();
			//	System.exit(1);
			//}
			
			// log.info(String.format("warpBufferList[0] hex=[%s]", HexUtil.byteBufferAvailableToHex(warpBufferList.get(0).getByteBuffer())));
			
			
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigFixedLenStringOutObj = (OutputMessage)outObjList.get(0);
			
			String value1OfOutObj = (String)bigFixedLenStringOutObj.getAttribute("value1");
			bigFixedLenStringOutObj.setAttribute("value1", value1OfOutObj.trim());
			
			
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
			InputMessage bigFixedLenStringInObj = messageManger.createInputMessage(messageID);
			// 데이터 패킷 버퍼 크기 4096 byte, 총 스트림 크기 = 바이트 배열 크기 4byte + filler1 size + value1 size + value2 size  
			bigFixedLenStringInObj.setAttribute("filler1", new byte[startSize]); // 데이터 패킷 맨끝 크기 4082 byte
			bigFixedLenStringInObj.setAttribute("value1", value1);
			
			warpBufferList = messageExchangeProtocol.M2S(bigFixedLenStringInObj, clientProjectConfig.getCharset());
			/**
			 * 데이터를 받은것처럼 위장하기 위해서 position 을 limit 위치로 이동
			 */
			warpBufferListSize = warpBufferList.size();
			for (int i=0; i < warpBufferListSize; i++) {
				ByteBuffer oneBuffer = warpBufferList.get(i).getByteBuffer();
				oneBuffer.position(oneBuffer.limit());
				
				// log.debug(oneBuffer.toString());
			}
			
			
			// 문자셋 잘 동작 하는지 테스트를 위한 코드
//			byte charsetBytesOfValue1[] = null;
//			try {
//				charsetBytesOfValue1 = value1.getBytes("EUC-KR");
//				log.info(String.format("%s EUC_KR bytes=[%s]", value1, HexUtil.byteArrayAllToHex(charsetBytesOfValue1)));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//			log.info(String.format("warpBufferList[0] hex=[%s]", HexUtil.byteBufferAvailableToHex(warpBufferList.get(0).getByteBuffer())));
			
			SocketInputStream messageInputStreamResourcePerSocket = new SocketInputStream(warpBufferList, dataPacketBufferQueueManager);
			
			ArrayList<AbstractMessage> outObjList = null;
			try {
				outObjList = messageExchangeProtocol.S2MList(OutputMessage.class, clientProjectConfig.getCharset(), messageInputStreamResourcePerSocket, messageManger);
			} catch (HeaderFormatException e) {
				log.fatal("HeaderFormatException", e);
				System.exit(1);
			} finally {
				messageInputStreamResourcePerSocket.destory();
			}
			
			int outObjListSize = outObjList.size();
			
			log.debug(String.format("outObjListSize=[%d]", outObjListSize));
			
			OutputMessage bigFixedLenStringOutObj = (OutputMessage)outObjList.get(0);
			
			// log.info(String.format("after DataPacketBufferQueue state=[%s]", dataPacketBufferQueueManager.getQueueState()));
			String bigFixedLenStringInObjStr = bigFixedLenStringInObj.toString();
			String bigFixedLenStringOutObjStr = bigFixedLenStringOutObj.toString();
			
			// log.info(bigFixedLenStringInObjStr);
			// log.info(bigFixedLenStringOutObjStr);
			
			log.info(String.format("messageID[%s] warpBufferListSize=[%d] Local 입/출력 비교결과=[%s]", 
					messageID, warpBufferListSize, bigFixedLenStringInObjStr.equals(bigFixedLenStringOutObjStr)));
		}
		
	}

}
