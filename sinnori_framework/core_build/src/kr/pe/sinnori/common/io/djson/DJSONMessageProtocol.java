package kr.pe.sinnori.common.io.djson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DJSONMessageProtocol implements CommonRootIF, MessageExchangeProtocolIF {
	private DJSONSingleItemConverter djsonSingleItemConverter = null;
	
	/** 데이터 패킷 크기 */
	private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	private int dataPacketBufferMaxCntPerMessage;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	
	private static final int messageHeaderSize = 4;
	
	public DJSONMessageProtocol( 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		this.dataPacketBufferSize = dataPacketBufferQueueManager.getDataPacketBufferSize();
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.djsonSingleItemConverter = new DJSONSingleItemConverter();
	}
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj,
			ByteOrder clientByteOrder, Charset clientCharset)
			throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder clientCharsetEncoder = CharsetUtil.createCharsetEncoder(clientCharset);
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(clientByteOrder, clientCharset, 
						clientCharsetEncoder, 0, dataPacketBufferQueueManager);
		String jsonStr = messageObj.toJSONString();
		
		// FIXME!
		// log.info(jsonStr);
		
		byte[] jsonStrBytes = jsonStr.getBytes(DJSONHeader.JSON_STRING_CHARSET);
		bodyOutputStream.putInt(jsonStrBytes.length);
		bodyOutputStream.putBytes(jsonStrBytes);
		
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream.getDataPacketBufferList();
		
		return messageWrapBufferList;
	}

	@Override
	public ArrayList<AbstractMessage> S2MList(
			Class<? extends AbstractMessage> targetClass,
			Charset clientCharset,
			MessageInputStreamResourcePerSocket messageInputStreamResource,
			MessageMangerIF messageManger) throws HeaderFormatException,
			NoMoreDataPacketBufferException {
		
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(clientCharset);
		ArrayList<WrapBuffer> messageReadWrapBufferList = messageInputStreamResource.getMessageReadWrapBufferList();
		DJSONHeader messageHeader = (DJSONHeader)messageInputStreamResource.getEtcInfo();
		
		ArrayList<AbstractMessage> messageList = new ArrayList<AbstractMessage>();
		
		boolean isMoreMessage = false;
		
		try {
			do {
				int messageReadWrapBufferListSize = messageReadWrapBufferList.size();
				if (messageReadWrapBufferListSize == 0) {
					log.fatal(String.format("messageReadWrapBufferListSize is zero"));
					System.exit(1);
				}
	
				int lastIndex = messageReadWrapBufferListSize - 1;
				ByteBuffer lastInputStreamBuffer = messageReadWrapBufferList.get(lastIndex).getByteBuffer();
				ByteOrder byteOrderOfLastBuffer = lastInputStreamBuffer.order();
				int finalReadPosition = lastInputStreamBuffer.position();
				long inputStramSizeBeforeMessageWork = lastIndex	* dataPacketBufferSize + finalReadPosition;
				
				isMoreMessage = false;
				
				// log.debug(String.format("1. messageReadWrapBufferListSize=[%d], lastInputStreamBuffer=[%s], inputStramSizeBeforeMessageWork=[%d]", messageReadWrapBufferListSize, lastInputStreamBuffer.toString(), inputStramSizeBeforeMessageWork));
				
				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 헤더 읽기전 위치 마크및 헤더 읽을 위치 0으로 이동 */
					ByteBuffer dupMessageHeaderBuffer = messageReadWrapBufferList.get(0).getByteBuffer().duplicate();
					dupMessageHeaderBuffer.order(byteOrderOfLastBuffer);
					dupMessageHeaderBuffer.position(messageHeaderSize);
					dupMessageHeaderBuffer.flip();
					
					// log.debug(String.format("3.1 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
					
					/** 헤더 읽기 */
					DJSONHeader  workMessageHeader = new DJSONHeader();
					workMessageHeader.lenOfJSONStr = dupMessageHeaderBuffer.getInt();
					
					// log.debug(String.format("3.2 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
					//log.debug(String.format("4. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
					// log.debug(workMessageHeader.toString());
					
					messageHeader = workMessageHeader;
				} 
				
				
				if (null != messageHeader) {
					//log.debug(String.format("5. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
					
					long messagePacketSize = (long)messageHeader.lenOfJSONStr + messageHeaderSize;
					
					if (inputStramSizeBeforeMessageWork >= messagePacketSize) {
						/** 메시지 추출*/
						
						int endPositionOfMessage  = (int)(messagePacketSize - lastIndex * dataPacketBufferSize);
						
						/**
						 * 마지막 출력 메시지 래퍼 버퍼내 메시지의 끝 위치는 
						 * 마지막으로 데이터를 읽어 들인 위치 안쪽에 위치하므로 
						 * long 타입을 integer 타입으로 변환해도 문제가 안된다.
						 */
						lastInputStreamBuffer.position(endPositionOfMessage);
						
						//log.debug(String.format("8. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						/** flip후 헤더 제외한 body 부분에 커서 위치 */
						WrapBuffer firstOutputMessageWrapBuffer = messageReadWrapBufferList.get(0);
						ByteBuffer firstOutputMessageBuffer = firstOutputMessageWrapBuffer.getByteBuffer();
						firstOutputMessageBuffer.flip();
						firstOutputMessageBuffer.position(messageHeaderSize);
						
						for (int i=1; i < messageReadWrapBufferListSize; i++) {
							ByteBuffer workBuffer = messageReadWrapBufferList.get(i).getByteBuffer();
							workBuffer.flip();
						}
						
						/** 바디 스트림으로 부터 메시지 내용 추출 */
						FreeSizeInputStream bodyInputStream = 
								new FreeSizeInputStream(messageReadWrapBufferList, charsetOfProjectDecoder, dataPacketBufferQueueManager);
						byte[] jsonBytes = bodyInputStream.getBytes(messageHeader.lenOfJSONStr);
						String jsonStr = new String(jsonBytes, DJSONHeader.JSON_STRING_CHARSET);
						
						// FIXME!
						// log.info(jsonStr);
						
						JSONParser jsonParser = new JSONParser();
						
						JSONObject jsonObj = null;
						
						try {
							jsonObj = (JSONObject)jsonParser.parse(jsonStr);
						} catch(ParseException pe){
							log.warn("ParseException", pe);
							throw new HeaderFormatException(String.format("JSON ParseException::%s::%s", pe.toString(), jsonStr));
						}
						
						String messageID = (String)jsonObj.get("messageID");
						if (null == messageID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check messageID::%s", jsonStr));
						}
						
						Long jsonMailboxID = (Long)jsonObj.get("mailboxID");
						if (null == jsonMailboxID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailboxID::%s", jsonStr));
						}
						
						Long jsonMailID = (Long)jsonObj.get("mailID");
						if (null == jsonMailID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						if (jsonMailboxID < Integer.MIN_VALUE || jsonMailboxID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						if (jsonMailID < Integer.MIN_VALUE || jsonMailID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						int mailboxID = jsonMailboxID.intValue();
						int mailID = jsonMailID.intValue();
						
						if (targetClass.equals(InputMessage.class)) {
							InputMessage workInObj = null;
							try {
								workInObj = messageManger.createInputMessage(messageID);
								workInObj.messageHeaderInfo.mailboxID = mailboxID;
								workInObj.messageHeaderInfo.mailID = mailID;
								
								workInObj.JSON2M(jsonObj, djsonSingleItemConverter);
								
								if (bodyInputStream.remaining() > 0) {
									// FIXME! 잔존 데이터 있음. 
									String errorMessage = String
											.format("메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
													workInObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format("MessageInfoNotFoundException::header=[%s]",
										messageHeader.toString()), e);
	
								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
								/** 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를 통해 메시지 헤더 정보를 저장한다. */
								errorInObj.messageHeaderInfo.mailboxID = mailboxID;
								errorInObj.messageHeaderInfo.mailID = mailID;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "M");
								errorInObj.setAttribute("errorMessageID", messageID);
								errorInObj.setAttribute("errorMessage", e.getMessage());
	
								workInObj = errorInObj;
							} catch (BodyFormatException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
	
								errorInObj.messageHeaderInfo = workInObj.messageHeaderInfo;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "B");
								errorInObj.setAttribute("errorMessageID", messageID);
								errorInObj.setAttribute("errorMessage", e.getMessage());
	
								workInObj = errorInObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
	
							/** 목록에 메시지 추가 */
							messageList.add(workInObj);
						} else {
							OutputMessage workOutObj = null;
							try {
								workOutObj = messageManger.createOutputMessage(messageID);
								workOutObj.messageHeaderInfo.mailboxID = mailboxID;
								workOutObj.messageHeaderInfo.mailID = mailID;
								
								workOutObj.JSON2M(jsonObj, djsonSingleItemConverter);
								
								if (bodyInputStream.remaining() > 0) {
									// FIXME! 잔존 데이터 있음. 
									String errorMessage = String
											.format("메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
													workOutObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
								/** 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를 통해 메시지 헤더 정보를 저장한다. */
								errorOutObj.messageHeaderInfo.mailboxID = mailboxID;
								errorOutObj.messageHeaderInfo.mailID = mailID;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "M");
								errorOutObj.setAttribute("errorMessageID", messageID);
								errorOutObj.setAttribute("errorMessage", e.getMessage());
	
								workOutObj = errorOutObj;
							} catch (BodyFormatException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
	
								errorOutObj.messageHeaderInfo = workOutObj.messageHeaderInfo;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "B");
								errorOutObj.setAttribute("errorMessageID", messageID);
								errorOutObj.setAttribute("errorMessage", e.getMessage());
	
								workOutObj = errorOutObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
							
							
							/** 목록에 메시지 추가 */
							messageList.add(workOutObj);	
						}
						
						
						
						
						
						//log.debug(String.format("11. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						/** 추출된 메시지 영역 삭제 - 추출된 메시지 내용이 담겨 있는 마지막을 제외한 출력 메시지 랩 버퍼를 삭제한다.  */
						messageInputStreamResource.freeWrapBufferWithoutLastBuffer(endPositionOfMessage, finalReadPosition);
						
						/** 메시지 추출 종료후 다음 메시지 추출을 위한 변수 초기화 */
						messageHeader = null;
						
						// lastIndex = 0;
						// inputStramSizeBeforeMessageWork = lastInputStreamBuffer.remaining();
	
						/**
						 * <pre>
						 * 다음 메시지 존재 여부를 판단하여 결과적으로 다음 메시지를 추출하도록 한다.   
						 * 참고) 소켓 채널 관점에서 읽기 이벤트 전용 selector 는 다음 읽은 데이터가 없을때까지 대기 모드로 빠진다.
						 * 때문에 출력 메시지 랩 버퍼에 존재하는 모든 메시지를 추츨해야만 무한 대기 없이 메시지 처리를 할 수 있다.
						 * 쉽게 예를 들면 "가" 메시지 와 "나" 메시지가 마지막 출력 메시지 랩 버퍼에 동시에 들어온 상태라면 
						 * "가" 메시지만 추출할 경우 "나" 메시지는 계속 대기 상태로 있게 된다. 
						 * 클라이언트에서 "다" 메시지를 보내어 읽기 이벤트가 발생되어야 깨어나서 "나" 메시지가 처리가 된다.
						 * </pre>
						 */
						
						inputStramSizeBeforeMessageWork = lastInputStreamBuffer.position();
						
						//log.debug(String.format("12. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						if (inputStramSizeBeforeMessageWork >= messageHeaderSize) {
							/** 헤더 읽기전 위치 마크및 헤더 읽을 위치 0으로 이동 */
							ByteBuffer dupMessageHeaderBuffer = lastInputStreamBuffer.duplicate();
							dupMessageHeaderBuffer.order(byteOrderOfLastBuffer);
							dupMessageHeaderBuffer.position(messageHeaderSize);
							dupMessageHeaderBuffer.flip();
							
							//log.debug(String.format("14 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
							
							/** 헤더 읽기 */
							DJSONHeader  workMessageHeader = new DJSONHeader();
							workMessageHeader.lenOfJSONStr = dupMessageHeaderBuffer.getInt();
							
							//log.debug(String.format("15. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
							
							
							messageHeader = workMessageHeader;
							
							
							if (inputStramSizeBeforeMessageWork >= ((long)workMessageHeader.lenOfJSONStr + messageHeaderSize)) {
								/** 버퍼 안에 다음 메시지 존재 함. */
								isMoreMessage = true;
							}
						}
					} else if (!lastInputStreamBuffer.hasRemaining()) {
						/** 다음 버퍼 */
						lastInputStreamBuffer = addWrapBuffer(messageReadWrapBufferList, messageHeader, byteOrderOfLastBuffer);
					}
				}
			} while (isMoreMessage);
		} catch(MessageItemException e) {
			log.fatal(e.getMessage(), e);
			System.exit(1);
		} finally {
			messageInputStreamResource.setEtcInfo(messageHeader);
		}
		
		
		return messageList;
	}

	/**
	 * @return 읽기 전용 버퍼 목록에 추가된 읽기 전용 버퍼
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 확보 할 수 없을대 던지는 예외
	 */
	private ByteBuffer addWrapBuffer(ArrayList<WrapBuffer> messageReadWrapBufferList, DJSONHeader messageHeader, ByteOrder byteOrderOfLastBuffer) throws NoMoreDataPacketBufferException {
		/** 메시지 1개당 최대 데이터 패킷 버퍼 갯수에 도달했을 경우 에러 처리함 */
		if (dataPacketBufferMaxCntPerMessage == messageReadWrapBufferList
				.size()) {
			String errorMessage = String
					.format("메시지당 최대 데이터 패킷 갯수[%d]를 넘는 메시지[%s]입니다. ",
							dataPacketBufferMaxCntPerMessage,
							messageHeader
									.toString());
			// log.warn(errorMessage);
			throw new NoMoreDataPacketBufferException(
					errorMessage);
		}
		
		WrapBuffer wrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer(byteOrderOfLastBuffer);
		messageReadWrapBufferList.add(wrapBuffer);
		ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
		
		return byteBuffer;
	}
	
}
