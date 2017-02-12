package kr.pe.sinnori.common.message.builder;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;


public class IOFileSetContentsBuilderManager {
	private MessageFileContensBuilder messageFileContensBuilder = null;
	private DecoderFileContensBuilder decoderFileContensBuilder = null;
	private EncoderFileContensBuilder encoderFileContensBuilder = null;
	private ClientCodecFileContensBuilder clientCodecFileContensBuilder = null;
	private ServerCodecFileContensBuilder serverCodecFileContensBuilder = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MessageProcessFileContentsManagerHolder {
		static final IOFileSetContentsBuilderManager singleton = new IOFileSetContentsBuilderManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static IOFileSetContentsBuilderManager getInstance() {
		return MessageProcessFileContentsManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private IOFileSetContentsBuilderManager() {
		messageFileContensBuilder = new MessageFileContensBuilder();
		decoderFileContensBuilder = new DecoderFileContensBuilder();
		encoderFileContensBuilder = new EncoderFileContensBuilder();
		clientCodecFileContensBuilder = new ClientCodecFileContensBuilder();
		serverCodecFileContensBuilder = new ServerCodecFileContensBuilder();
	}

	public String getMessageSourceFileContents(String messageID, String author, kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		return messageFileContensBuilder.getFileContents(messageID, author, messageInfo);
	}
	
	public String getDecoderSourceFileContents(String messageID,
			String author,
			kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {		
		return decoderFileContensBuilder.getFileContents(messageID, author, messageInfo);
	}

	public String getEncoderSourceFileContents(String messageID,
			String author,
			kr.pe.sinnori.common.message.builder.info.MessageInfo messageInfo) {
		return encoderFileContensBuilder.getFileContents(messageID, author, messageInfo);
	}
	
	public String getClientCodecSourceFileContents(
			CommonType.MESSAGE_TRANSFER_DIRECTION connectionDirectionMode,
			String messageID, String author) {
		return clientCodecFileContensBuilder.getFileContents(connectionDirectionMode, messageID, author);
	}

	public String getServerCodecSourceFileContents(CommonType.MESSAGE_TRANSFER_DIRECTION connectionDirectionMode, String messageID, String author) {
		return serverCodecFileContensBuilder.getFileContents(connectionDirectionMode, messageID, author);
	}
}
